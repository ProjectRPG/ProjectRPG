package rpg.project.lib;


import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

@Mod(Reference.MODID)
public class ProjectRPG {    
    public ProjectRPG() {
    	//Configs
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG); 
    	
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	EventRegistry.EVENTS.register(modBus);
    	
    	//MOD BUS event listeners
    	modBus.addListener(CommonSetup::gatherData);
    }
}
