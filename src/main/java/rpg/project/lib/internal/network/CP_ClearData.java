package rpg.project.lib.internal.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.Reference;

public record CP_ClearData() implements CustomPacketPayload {
    public static void handle(CP_ClearData packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Core.get(LogicalSide.CLIENT).getLoader().resetData());
    }

    public static final Type<CP_ClearData> TYPE = new Type<>(Reference.resource("s2c_clear_data"));
    @Override public Type<? extends CustomPacketPayload> type() {return TYPE;}
}
