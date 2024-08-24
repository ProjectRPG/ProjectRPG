package rpg.project.lib.internal.client.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import rpg.project.lib.internal.client.glossary.GlossaryScreen;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.setup.ClientSetup;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KeyPressHandler {
    @SubscribeEvent
    public static void keyPressEvent(InputEvent.Key event) {
        if (ClientSetup.LEFT_MENU.isDown())
            Config.PROG_MENU_OPEN.set(!Config.PROG_MENU_OPEN.get());
        if (ClientSetup.RIGHT_MENU.isDown())
            Config.ABILITY_MENU_OPEN.set(!Config.ABILITY_MENU_OPEN.get());
        if (ClientSetup.GLOSSARY.isDown() && Minecraft.getInstance().screen == null)
            Minecraft.getInstance().setScreen(new GlossaryScreen());
    }
}
