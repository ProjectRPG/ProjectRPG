package rpg.project.lib.builtins.vanilla;

import java.util.UUID;
import java.util.function.Consumer;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.data.CodecTypes;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.builtins.vanilla.VanillaProgressionConfigType.VanillaProgressionConfig;
import rpg.project.lib.builtins.vanilla.VanillaProgressionDataType.VanillaProgressionData;

public class VanillaProgressionSystem implements ProgressionSystem<VanillaProgressionData>{
	private static final String container = "exp";
	
	public VanillaProgressionSystem() {
		MinecraftForge.EVENT_BUS.addListener(VanillaProgressionSystem::updateScoreFromOfflineProgress);
	}
	
	public static void updateScoreFromOfflineProgress(PlayerLoggedInEvent event) {
		VanillaProgressionData cache = OfflineProgress.get().cachedProgress.getOrDefault(event.getEntity().getUUID(), new VanillaProgressionData(0));
		event.getEntity().increaseScore(cache.exp());
	}

	@Override
	public VanillaProgressionData getProgress(UUID playerID, String container) {
		if (ServerLifecycleHooks.getCurrentServer() == null)
			return new VanillaProgressionData(0);
		ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID);
		if (player == null)
			return new VanillaProgressionData(0);
		return new VanillaProgressionData(player.getScore());
	}

	@Override
	public void setProgress(UUID playerID, String container, VanillaProgressionData value) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID) == null)
			//TODO make this a hard override for when they log in otherwise this will just add to the amount
			OfflineProgress.get().cachedProgress.merge(playerID, value, (og, ng) -> new VanillaProgressionData(og.exp() + ng.exp()));
		else
			ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID).setExperiencePoints(value.exp());		
	}
	
	private void addXp(UUID playerID, VanillaProgressionData value) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID) == null)
			OfflineProgress.get().cachedProgress.merge(playerID, value, (og, ng) -> new VanillaProgressionData(og.exp() + ng.exp()));
		else
			ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID).giveExperiencePoints(value.exp());
	}
	
	@Override
	public List<Pair<String, Consumer<Float>>> getProgressionToBeAwarded(Hub core, ResourceLocation eventID, EventContext context) {
		ResourceLocation objectID = context.subjectObject().getSecond();
		ObjectType type = context.subjectObject().getFirst();
		return core.getProgressionData(VanillaProgressionConfigType.IMPL, type, objectID)
				.map(config -> {
					int xpToAward = ((VanillaProgressionConfig)config).eventToXp().getOrDefault(eventID, 0);
					List<Pair<String, Consumer<Float>>> output = List.of(Pair.of(container, gate -> this.addXp(context.actor().getUUID(), new VanillaProgressionData((int)((float)xpToAward * gate)))));
					return output; 
				}
			).orElse(List.of());
	}	
	
	private static class OfflineProgress extends SavedData {
		private Map<UUID, VanillaProgressionData> cachedProgress;
		
		private static final Codec<Map<UUID, VanillaProgressionData>> CODEC = Codec.unboundedMap(CodecTypes.UUID_CODEC, VanillaProgressionData.CODEC.xmap(s -> (VanillaProgressionData)s, s -> s));
		
		private static final String MAP_KEY = "data";
		
		public static OfflineProgress get() {			
			return ServerLifecycleHooks.getCurrentServer() == null ? new OfflineProgress()
					: ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(OfflineProgress::new, OfflineProgress::new, "prpg_vanilla_progress_data");
		}
		
		private OfflineProgress() {
			cachedProgress = new HashMap<>();
		}
		private OfflineProgress(CompoundTag nbt) {
			cachedProgress = new HashMap<>(CODEC.parse(NbtOps.INSTANCE, nbt.getCompound(MAP_KEY)).resultOrPartial(err -> LogManager.getLogger().error(err)).orElse(new HashMap<>()));
		}

		@Override
		public CompoundTag save(CompoundTag pCompoundTag) {
			pCompoundTag.put(MAP_KEY, (CompoundTag)CODEC.encodeStart(NbtOps.INSTANCE, cachedProgress).resultOrPartial(err -> LogManager.getLogger().error(err)).orElse(new CompoundTag()));
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
}
