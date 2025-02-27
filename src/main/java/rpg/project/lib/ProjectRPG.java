package rpg.project.lib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.builtins.Abilities;
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
		APIUtils.FEATURES.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		APIUtils.FEATURES.register(bus);
		APIUtils.ABILITIES.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE-1).sync(true));
		APIUtils.ABILITIES.register(bus);
		Abilities.init();
    	
    	//MOD BUS event listeners
    	bus.addListener(CommonSetup::init);
    	bus.addListener(CommonSetup::gatherData);
		bus.addListener(Networking::registerMessages);
    }
}
