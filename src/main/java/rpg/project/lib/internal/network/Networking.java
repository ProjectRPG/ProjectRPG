package rpg.project.lib.internal.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.Reference;

public class Networking {
	private static SimpleChannel INSTANCE;

	public static void registerMessages() { 
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MODID, "net"),
				() -> "1.1", 
				s -> true, 
				s -> true);
		
		//int ID = 0;
		//CLIENT BOUND PACKETS
		
		//SERVER BOUND PACKETS
		
		MsLoggy.INFO.log(LOG_CODE.NETWORK, "Messages Registered");
	}
	
	public static void registerDataSyncPackets() {
//		DataLoader.RELOADER.subscribeAsSyncable(INSTANCE, () -> new CP_ClearData());
//		Core.get(LogicalSide.SERVER).getLoader().ITEM_LOADER.subscribeAsSyncable(INSTANCE, (o) -> new CP_SyncData(ObjectType.ITEM, o));
//		Core.get(LogicalSide.SERVER).getLoader().BLOCK_LOADER.subscribeAsSyncable(INSTANCE, (o) -> new CP_SyncData(ObjectType.BLOCK, o));
//		Core.get(LogicalSide.SERVER).getLoader().ENTITY_LOADER.subscribeAsSyncable(INSTANCE, (o) -> new CP_SyncData(ObjectType.ENTITY, o));
//		Core.get(LogicalSide.SERVER).getLoader().BIOME_LOADER.subscribeAsSyncable(INSTANCE, (o) -> new CP_SyncData(ObjectType.BIOME, o));
//		Core.get(LogicalSide.SERVER).getLoader().DIMENSION_LOADER.subscribeAsSyncable(INSTANCE, (o) -> new CP_SyncData(ObjectType.DIMENSION, o));
//		Core.get(LogicalSide.SERVER).getLoader().ENCHANTMENT_LOADER.subscribeAsSyncable(INSTANCE, o -> new CP_SyncData(ObjectType.ENCHANTMENT, o));
//		Core.get(LogicalSide.SERVER).getLoader().EFFECT_LOADER.subscribeAsSyncable(INSTANCE, o -> new CP_SyncData(ObjectType.EFFECT, o));
//		Core.get(LogicalSide.SERVER).getLoader().PLAYER_LOADER.subscribeAsSyncable(INSTANCE, o -> new CP_SyncData(ObjectType.PLAYER, o));
	}

	public static void sendToClient(Object packet, ServerPlayer player) {
		INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
	public static void sendToServer(Object packet) {
		INSTANCE.sendToServer(packet);
	}

}
