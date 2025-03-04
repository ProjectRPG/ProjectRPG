package rpg.project.lib;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.builtins.Abilities;
import rpg.project.lib.builtins.vanilla.VanillaCodecs;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.network.Networking;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

@Mod(Reference.MODID)
public class ProjectRPG {    
    public ProjectRPG(IEventBus bus, ModContainer container) {
    	//Configs
    	container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    	container.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    	container.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

		EventRegistry.EVENTS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
    	EventRegistry.EVENTS.register(bus);
		EventCondition.CONDITIONS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		EventCondition.CONDITIONS.register(bus);
		CommonSetup.CODECS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.CODECS.register(bus);
		VanillaCodecs.init(bus);
		CommonSetup.FEATURES.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.FEATURES.register(bus);
		CommonSetup.ABILITIES.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.ABILITIES.register(bus);
		Abilities.init();
		CommonSetup.GATES_EVENTS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.GATES_EVENTS.register(bus);
		CommonSetup.GATES_PROGRESS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.GATES_PROGRESS.register(bus);
		CommonSetup.GATES_FEATURES.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.GATES_FEATURES.register(bus);
		CommonSetup.GATES_ABILITIES.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.GATES_ABILITIES.register(bus);
		CommonSetup.PROGRESSION_ADDONS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		CommonSetup.PROGRESSION_ADDONS.register(bus);
    	
    	//MOD BUS event listeners
    	bus.addListener(CommonSetup::init);
    	bus.addListener(CommonSetup::gatherData);
		bus.addListener(Networking::registerMessages);
    }
}
