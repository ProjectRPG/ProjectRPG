package rpg.project.lib.internal.util;

import net.minecraft.resources.ResourceLocation;

public class Reference {
	public static final String MODID = "projectrpg";
	
	public static ResourceLocation resource(String path) {
		return new ResourceLocation(MODID, path);
	}
}
