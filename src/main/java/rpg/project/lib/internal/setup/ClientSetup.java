package rpg.project.lib.internal.setup;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.api.events.conditions.EventConditionAnd;
import rpg.project.lib.api.events.conditions.EventConditionAny;
import rpg.project.lib.api.events.conditions.EventConditionEntityMatches;
import rpg.project.lib.api.events.conditions.EventConditionNBT;
import rpg.project.lib.api.events.conditions.EventConditionNot;
import rpg.project.lib.builtins.vanilla.VanillaProgressionConfigType;
import rpg.project.lib.builtins.vanilla.client.VanillaProgressionPanel;
import rpg.project.lib.internal.client.glossary.AndConditionPanel;
import rpg.project.lib.internal.client.glossary.AnyConditionPanel;
import rpg.project.lib.internal.client.glossary.EntityMatchConditionPanel;
import rpg.project.lib.internal.client.glossary.NBTConditionPanel;
import rpg.project.lib.internal.client.glossary.NotConditionPanel;
import rpg.project.lib.internal.client.overlays.OverlaySlidePanels;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.registry.ClientPanelRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID, value= Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Reference.resource("progress_overlay"), new OverlaySlidePanels(false, 0.1, 0.65, 0.4, Config.PROG_MENU_OPEN));
        event.registerAboveAll(Reference.resource("abilities_overlay"), new OverlaySlidePanels(true, 0.1, 0.65, 0.4, Config.ABILITY_MENU_OPEN));
    }

    public static final KeyMapping.Category KEY_BIND_CATEGORY = new KeyMapping.Category(Reference.resource("key_binds"));
    public static final KeyMapping LEFT_MENU = new KeyMapping(LangProvider.KEYBIND_SHOW_PROGRESSION.key(), GLFW.GLFW_KEY_LEFT_ALT, KEY_BIND_CATEGORY);
    public static final KeyMapping RIGHT_MENU = new KeyMapping(LangProvider.KEYBIND_SHOW_ABILITIES.key(), GLFW.GLFW_KEY_RIGHT_ALT, KEY_BIND_CATEGORY);
    public static final KeyMapping GLOSSARY = new KeyMapping(LangProvider.GLOSSARY_OPEN.key(), GLFW.GLFW_KEY_P, KEY_BIND_CATEGORY);
    @SubscribeEvent
    public static void init(RegisterKeyMappingsEvent event) {
        event.register(LEFT_MENU);
        event.register(RIGHT_MENU);
        event.register(GLOSSARY);
    }

    @SubscribeEvent
    public static void registerPanels(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientPanelRegistry.registerProgressionPanel(VanillaProgressionConfigType.ID, VanillaProgressionPanel::new);

            ClientPanelRegistry.registerConditionPanel(EventCondition.ALL_OF.getId(), c -> new AndConditionPanel((EventConditionAnd) c));
            ClientPanelRegistry.registerConditionPanel(EventCondition.ANY_OF.getId(), c -> new AnyConditionPanel((EventConditionAny) c));
            ClientPanelRegistry.registerConditionPanel(EventCondition.NOT.getId(), c -> new NotConditionPanel((EventConditionNot) c));
            ClientPanelRegistry.registerConditionPanel(EventCondition.ENTITY_MATCHES.getId(), c -> new EntityMatchConditionPanel((EventConditionEntityMatches) c));
            ClientPanelRegistry.registerConditionPanel(EventCondition.NBT.getId(), c -> new NBTConditionPanel((EventConditionNBT) c));
        });
    }
}
