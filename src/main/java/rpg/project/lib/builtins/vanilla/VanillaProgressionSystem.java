package rpg.project.lib.builtins.vanilla;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.api.data.CodecTypes;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.ProgressionAdvanceEvent;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.builtins.vanilla.VanillaProgressionConfigType.VanillaProgressionConfig;
import rpg.project.lib.builtins.vanilla.VanillaProgressionDataType.VanillaProgressionData;
import rpg.project.lib.builtins.vanilla.network.VanillaProgressionSync;
import rpg.project.lib.internal.network.Networking;

public class VanillaProgressionSystem implements ProgressionSystem<VanillaProgressionData>{
	private static final String container = "exp";
	
	public VanillaProgressionSystem() {
		NeoForge.EVENT_BUS.addListener(VanillaProgressionSystem::updateScoreFromOfflineProgress);
	}
	
	public static void updateScoreFromOfflineProgress(PlayerEvent.PlayerLoggedInEvent event) {
		VanillaProgressionData cache = OfflineProgress.get().cachedProgress.remove(event.getEntity().getUUID());
		if (cache != null) {
			VanillaProgressionData current = new VanillaProgressionData(event.getEntity().experienceLevel);
			event.getEntity().increaseScore(cache.exp());
			if (current.exp() < event.getEntity().experienceLevel)
				NeoForge.EVENT_BUS.post(new ProgressionAdvanceEvent(event.getEntity(), container, current,
						new VanillaProgressionData(event.getEntity().experienceLevel)));
		}
	}

	@Override
	public VanillaProgressionData getProgress(UUID playerID, String container) {
		if (ServerLifecycleHooks.getCurrentServer() == null)
			return new VanillaProgressionData(0);
		ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID);
		if (player == null)
			return new VanillaProgressionData(0);
		return new VanillaProgressionData(player.experienceLevel);
	}

	@Override
	public void setProgress(UUID playerID, String container, VanillaProgressionData value) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID) == null)
			OfflineProgress.get().cachedProgress.put(playerID, value);
		else {
			ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID);
			VanillaProgressionData current = new VanillaProgressionData(player.experienceLevel);
			player.setExperiencePoints(value.exp());
			if (current.exp() < player.experienceLevel)
				NeoForge.EVENT_BUS.post(new ProgressionAdvanceEvent(player, container, current, new VanillaProgressionData(player.experienceLevel)));
		}
	}
	
	private void addXp(UUID playerID, VanillaProgressionData value) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID) == null)
			OfflineProgress.get().cachedProgress.merge(playerID, value, (og, ng) -> new VanillaProgressionData(og.exp() + ng.exp()));
		else {
			ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID);
			VanillaProgressionData current = new VanillaProgressionData(player.experienceLevel);
			player.giveExperiencePoints(value.exp());
			if (current.exp() < player.experienceLevel)
				NeoForge.EVENT_BUS.post(new ProgressionAdvanceEvent(player, container, current, new VanillaProgressionData(player.experienceLevel)));
		}
	}
	
	@Override
	public List<Pair<String, Consumer<Float>>> getProgressionToBeAwarded(Hub core, ResourceLocation eventID, EventContext context) {
		return core.getProgressionData(VanillaProgressionConfigType.IMPL, context.getSubjectType(), context.getSubjectID())
				.map(config -> {
					AtomicInteger xpToAward = new AtomicInteger(((VanillaProgressionConfig)config).eventToXp().getOrDefault(eventID, 0));
					core.getProgressionAddons().forEach(addon -> {
						xpToAward.getAndSet(((VanillaProgressionData)addon.modifyProgression(core, context, new VanillaProgressionData(xpToAward.get()))).exp());						
					});
					List<Pair<String, Consumer<Float>>> output = List.of(Pair.of(container, gate -> {
						int xp = (int)((float)xpToAward.get() * gate);
						if (xp <= 0) return;
						this.addXp(context.getActor().getUUID(), new VanillaProgressionData(xp));
						if (context.getActor() instanceof ServerPlayer player)
							Networking.sendToClient(new VanillaProgressionSync(xp, eventID), player);
					}));
					return output;
				}
			).orElse(List.of());
	}	
	
	private static class OfflineProgress extends SavedData {
		private final Map<UUID, VanillaProgressionData> cachedProgress;
		
		private static final Codec<Map<UUID, VanillaProgressionData>> CODEC = Codec.unboundedMap(CodecTypes.UUID_CODEC, VanillaProgressionData.CODEC.xmap(s -> (VanillaProgressionData)s, s -> s).codec());
		
		private static final String MAP_KEY = "data";

		public static Factory<OfflineProgress> dataFactory() {
			return new Factory<>(OfflineProgress::new, OfflineProgress::new, null);
		}
		public static OfflineProgress get() {			
			return ServerLifecycleHooks.getCurrentServer() == null ? new OfflineProgress()
					: ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(dataFactory(), "prpg_vanilla_progress_data");
		}
		
		private OfflineProgress() {
			cachedProgress = new HashMap<>();
		}
		private OfflineProgress(CompoundTag nbt, HolderLookup.Provider provider) {
			cachedProgress = new HashMap<>(CODEC.parse(NbtOps.INSTANCE, nbt.getCompound(MAP_KEY)).resultOrPartial(err -> LogManager.getLogger().error(err)).orElse(new HashMap<>()));
		}

		@Override
		public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider provider) {
			pCompoundTag.put(MAP_KEY, CODEC.encodeStart(NbtOps.INSTANCE, cachedProgress).resultOrPartial(err -> LogManager.getLogger().error(err)).orElse(new CompoundTag()));
			return pCompoundTag;
		}		
	}

	public static final String PLAYERS = "players";
	public static final String AMOUNT = "amount";
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> getCommands() {
		return Commands.literal("xp")
				.requires(ctx -> ctx.hasPermission(2))
				.then(Commands.argument(PLAYERS, EntityArgument.players())
					.then(Commands.literal("set")
						.then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
							.executes(ctx -> {
								int score = IntegerArgumentType.getInteger(ctx, AMOUNT);
								EntityArgument.getPlayers(ctx, PLAYERS).forEach(player -> player.setExperiencePoints(score));
								return 0;
							})))
					.then(Commands.literal("add")
						.then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
							.executes(ctx -> {
								int score = IntegerArgumentType.getInteger(ctx, AMOUNT);
								EntityArgument.getPlayers(ctx, PLAYERS).forEach(player -> player.giveExperiencePoints(score));
								return 0;
							}))));
	}

	@Override
	public SubSystemConfigType dataType() {
		return VanillaProgressionDataType.IMPL;
	}

	@Override
	public SidePanelContentProvider getSidePanelProvider() {
		return VanillaProgressionPanel.INSTANCE;
	}
}
