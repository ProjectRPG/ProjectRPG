package rpg.project.lib.internal.util;

import net.minecraft.resources.ResourceLocation;

public class Reference {
	public static final String MODID = "projectrpg";
	
	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
	public static ResourceLocation mc(String path) {return ResourceLocation.withDefaultNamespace(path);}
}
