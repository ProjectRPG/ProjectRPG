package rpg.project.lib.builtins.vanilla;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;

import com.mojang.serialization.Codec;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import rpg.project.lib.api.CodecTypes;
import rpg.project.lib.api.progression.ProgressionSystem;

public class VanillaProgressionSystem implements ProgressionSystem<Integer>{
	
	public VanillaProgressionSystem() {
		MinecraftForge.EVENT_BUS.addListener(VanillaProgressionSystem::updateScoreFromOfflineProgress);
	}
	
	public static void updateScoreFromOfflineProgress(PlayerLoggedInEvent event) {
		int cache = OfflineProgress.get().cachedProgress.getOrDefault(event.getEntity().getUUID(), 0);
		event.getEntity().increaseScore(cache);
	}

	@Override
	public Integer getProgress(UUID playerID, String container) {
		if (ServerLifecycleHooks.getCurrentServer() == null)
			return 0;
		ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID);
		if (player == null)
			return 0;
		return player.getScore();
	}

	@Override
	public void setProgress(UUID playerID, String container, Integer value) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID) == null)
			OfflineProgress.get().cachedProgress.put(playerID, value);
		else
			ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID).increaseScore(value);
		
	}
	
	private static class OfflineProgress extends SavedData {
		private Map<UUID, Integer> cachedProgress;
		
		private static final Codec<Map<UUID, Integer>> CODEC = Codec.unboundedMap(CodecTypes.UUID_CODEC, Codec.INT);
		
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
}
