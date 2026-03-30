package rpg.project.lib.internal.util;

import net.minecraft.resources.Identifier;

public class Reference {
	public static final String MODID = "projectrpg";
	
	public static Identifier resource(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
	public static Identifier mc(String path) {return Identifier.withDefaultNamespace(path);}
}
