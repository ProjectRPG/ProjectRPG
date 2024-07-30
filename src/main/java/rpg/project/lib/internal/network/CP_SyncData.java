package rpg.project.lib.internal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.util.Reference;

import java.util.HashMap;
import java.util.Map;

public record CP_SyncData(ObjectType objectType, Map<ResourceLocation, MainSystemConfig> data) implements CustomPacketPayload {
    public static final Type<CP_SyncData> TYPE = new Type<>(Reference.resource("client_data_sync"));
    @Override public Type<CP_SyncData> type() {return TYPE;}

    public static final StreamCodec<FriendlyByteBuf, CP_SyncData> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(ObjectType.class)), CP_SyncData::objectType,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, MainSystemConfig.STREAM_CODEC), CP_SyncData::data,
            CP_SyncData::new
    );

    public static void handle(CP_SyncData packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Core.get(LogicalSide.CLIENT).getLoader().applyData(packet.objectType(), packet.data()));
    }
}
