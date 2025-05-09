package rpg.project.lib.internal.network;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import rpg.project.lib.api.data.ObjectType;
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
		.playToClient(VanillaProgressionSync.TYPE, VanillaProgressionSync.STREAM_CODEC, VanillaProgressionSync::handle)
		.playToClient(CP_SyncData.TYPE, CP_SyncData.STREAM_CODEC, CP_SyncData::handle)
		.playToClient(CP_ClearData.TYPE, StreamCodec.unit(new CP_ClearData()), CP_ClearData::handle);
		//CLIENT BOUND PACKETS
		
		//SERVER BOUND PACKETS
		
		MsLoggy.INFO.log(LOG_CODE.NETWORK, "Messages Registered");
	}
	
	public static void registerDataSyncPackets() {
		Core.get(LogicalSide.SERVER).getLoader().RELOADER.subscribeAsSyncable(CP_ClearData::new);
		Core.get(LogicalSide.SERVER).getLoader().all().forEach(listener -> listener.subscribeAsSyncable((o) -> new CP_SyncData(ObjectType.ITEM, o)));
	}

	public static void sendToClient(CustomPacketPayload packet, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, packet);
	}
	public static void sendToServer(CustomPacketPayload packet) {
		PacketDistributor.sendToServer(packet);
	}

}
