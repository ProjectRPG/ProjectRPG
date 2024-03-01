package rpg.project.lib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

@Mod(Reference.MODID)
public class ProjectRPG {    
    public ProjectRPG(IEventBus bus) {
    	//Configs
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

    	EventRegistry.EVENTS.register(bus);
    	
    	//MOD BUS event listeners
    	bus.addListener(CommonSetup::init);
    	bus.addListener(CommonSetup::gatherData);
    }
}
