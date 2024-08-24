package rpg.project.lib.internal.setup;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import rpg.project.lib.internal.client.overlays.OverlaySlidePanels;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID, bus=EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Reference.resource("progress_overlay"), new OverlaySlidePanels(false, 0.1, 0.65, 0.4, Config.PROG_MENU_OPEN));
        event.registerAboveAll(Reference.resource("abilities_overlay"), new OverlaySlidePanels(true, 0.1, 0.65, 0.4, Config.ABILITY_MENU_OPEN));
    }

    public static final KeyMapping LEFT_MENU = new KeyMapping(LangProvider.KEYBIND_SHOW_PROGRESSION.key(), GLFW.GLFW_KEY_LEFT_ALT, LangProvider.KEYBIND_CATEGORY.key());
    public static final KeyMapping RIGHT_MENU = new KeyMapping(LangProvider.KEYBIND_SHOW_ABILITIES.key(), GLFW.GLFW_KEY_RIGHT_ALT, LangProvider.KEYBIND_CATEGORY.key());
    public static final KeyMapping GLOSSARY = new KeyMapping(LangProvider.GLOSSARY_OPEN.key(), GLFW.GLFW_KEY_P, LangProvider.KEYBIND_CATEGORY.key());
    @SubscribeEvent
    public static void init(RegisterKeyMappingsEvent event) {
        event.register(LEFT_MENU);
        event.register(RIGHT_MENU);
        event.register(GLOSSARY);
    }
}
