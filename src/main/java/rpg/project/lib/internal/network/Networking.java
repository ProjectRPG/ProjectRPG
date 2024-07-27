package rpg.project.lib.internal.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import rpg.project.lib.builtins.vanilla.network.VanillaProgressionSync;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.Reference;

public class Networking {

	@SubscribeEvent
	public static void registerMessages(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(Reference.MODID);

		registrar
		//CLIENT BOUND PACKETS
		.playToClient(VanillaProgressionSync.TYPE, VanillaProgressionSync.STREAM_CODEC, VanillaProgressionSync::handle);
		//CLIENT BOUND PACKETS
		
		//SERVER BOUND PACKETS
		
		MsLoggy.INFO.log(LOG_CODE.NETWORK, "Messages Registered");
	}
	
	public static void registerDataSyncPackets() {
//		DataLoader.RELOADER.subscribeAsSyncable(() -> new CP_ClearData());
//		Core.get(LogicalSide.SERVER).getLoader().ITEM_LOADER.subscribeAsSyncable((o) -> new CP_SyncData(ObjectType.ITEM, o));
//		Core.get(LogicalSide.SERVER).getLoader().BLOCK_LOADER.subscribeAsSyncable((o) -> new CP_SyncData(ObjectType.BLOCK, o));
//		Core.get(LogicalSide.SERVER).getLoader().ENTITY_LOADER.subscribeAsSyncable((o) -> new CP_SyncData(ObjectType.ENTITY, o));
//		Core.get(LogicalSide.SERVER).getLoader().BIOME_LOADER.subscribeAsSyncable((o) -> new CP_SyncData(ObjectType.BIOME, o));
//		Core.get(LogicalSide.SERVER).getLoader().DIMENSION_LOADER.subscribeAsSyncable((o) -> new CP_SyncData(ObjectType.DIMENSION, o));
//		Core.get(LogicalSide.SERVER).getLoader().ENCHANTMENT_LOADER.subscribeAsSyncable(o -> new CP_SyncData(ObjectType.ENCHANTMENT, o));
//		Core.get(LogicalSide.SERVER).getLoader().EFFECT_LOADER.subscribeAsSyncable(o -> new CP_SyncData(ObjectType.EFFECT, o));
//		Core.get(LogicalSide.SERVER).getLoader().PLAYER_LOADER.subscribeAsSyncable(o -> new CP_SyncData(ObjectType.PLAYER, o));
	}

	public static void sendToClient(CustomPacketPayload packet, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, packet);
	}
	public static void sendToServer(CustomPacketPayload packet) {
		PacketDistributor.sendToServer(packet);
	}

}
