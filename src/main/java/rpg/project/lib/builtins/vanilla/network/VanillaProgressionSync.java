package rpg.project.lib.builtins.vanilla.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import rpg.project.lib.builtins.vanilla.VanillaAbilityPanel;
import rpg.project.lib.builtins.vanilla.VanillaProgressionPanel;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.Reference;

public record VanillaProgressionSync(int xp, ResourceLocation eventID) implements CustomPacketPayload {
    public static final Type<VanillaProgressionSync> TYPE = new Type(Reference.resource("s2c_sync_config"));
    @Override public Type<VanillaProgressionSync> type() {return TYPE;}

    public static final StreamCodec<FriendlyByteBuf, VanillaProgressionSync> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, VanillaProgressionSync::xp,
            ResourceLocation.STREAM_CODEC, VanillaProgressionSync::eventID,
            VanillaProgressionSync::new
    );

    public static void handle(VanillaProgressionSync packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            MutableComponent eventName = Component.translatable("projectrpg.event."+packet.eventID().getNamespace()+"."+packet.eventID().getPath());
            VanillaProgressionPanel.INSTANCE.addLine(LangProvider.PROGRESSION_GAIN.asComponent(packet.xp(), eventName));
        });
    }
}
