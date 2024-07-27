package rpg.project.lib.internal.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;

import java.util.ArrayList;
import java.util.List;

public class Config {
	public static ModConfigSpec CLIENT_CONFIG;
	public static ModConfigSpec COMMON_CONFIG;
	public static ModConfigSpec SERVER_CONFIG;
	
	static {
		ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
		ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
		ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
		
		setupClient(CLIENT_BUILDER);
		setupCommon(COMMON_BUILDER);
		setupServer(SERVER_BUILDER);
		
		CLIENT_CONFIG = CLIENT_BUILDER.build();
		COMMON_CONFIG = COMMON_BUILDER.build();
		SERVER_CONFIG = SERVER_BUILDER.build();
	}
	
	//====================CLIENT SETTINGS===============================
	public static ModConfigSpec.BooleanValue PROG_MENU_OPEN;
	public static ModConfigSpec.BooleanValue ABILITY_MENU_OPEN;
	public static ModConfigSpec.DoubleValue SIDE_MENU_SPEED;
	public static ModConfigSpec.BooleanValue PROG_ON_LEFT;
	private static void setupClient(ModConfigSpec.Builder builder) {
		PROG_MENU_OPEN = builder.comment("sets the current state of the progression menu.  Changes in game will",
				" be stored here so your setting persists between loads")
				.define("progresssion_menu_open", false);
		ABILITY_MENU_OPEN = builder.comment("sets the current state of the ability menu.  Changes in game will",
						" be stored here so your setting persists between loads")
				.define("ability_menu_open", false);
		SIDE_MENU_SPEED = builder.comment("How fast should the side menus open and close")
				.defineInRange("side_menu_speed", 5d, 0d, Double.MAX_VALUE);
		PROG_ON_LEFT = builder.comment("Should progression display on the left panel? if false, it will display on the right")
				.define("progression_on_left", true);
	}

	//====================COMMON SETTINGS===============================
	
	private static void setupCommon(ModConfigSpec.Builder builder) {
		builder.push("Common");
		
		buildMsLoggy(builder);
		
		builder.pop(); //Common Blocks
	}
	
	public static ModConfigSpec.ConfigValue<List<? extends String>> INFO_LOGGING;
	public static ModConfigSpec.ConfigValue<List<? extends String>> DEBUG_LOGGING;
	public static ModConfigSpec.ConfigValue<List<? extends String>> WARN_LOGGING;
	public static ModConfigSpec.ConfigValue<List<? extends String>> ERROR_LOGGING;
	public static ModConfigSpec.ConfigValue<List<? extends String>> FATAL_LOGGING;
	
	private static void buildMsLoggy(ModConfigSpec.Builder builder) {
		builder.comment("PRPG Error Logging Configuration","",
			"===================================================",
			"To enable Logging with MsLoggy, enter a logging",
			"keyword into the logging level list that you want.",
			"the list of keywords are (lowercase only):",
			"'api', 'autovalues', 'chunk', 'data', 'event', ",
			"'feature', 'gui', 'loading', 'network', and 'xp'",
			"===================================================").push("Ms Loggy");
		
		INFO_LOGGING = builder
			.comment("Which MsLoggy info logging should be enabled?  This will flood your log with data, but provides essential details",
				" when trying to find data errors and bug fixing.  ")
			.defineList("Info Logging", new ArrayList<>(List.of(LOG_CODE.LOADING.code,LOG_CODE.NETWORK.code,LOG_CODE.API.code)), s -> s instanceof String);
		DEBUG_LOGGING = builder
			.comment("Which MsLoggy debug logging should be enabled?  This will flood your log with data, but provides essential details",
				" when trying to find bugs. DEVELOPER SETTING (mostly).  ")
			.defineList("Debug Logging", new ArrayList<>(List.of(LOG_CODE.API.code)), s -> s instanceof String);
		WARN_LOGGING = builder
			.comment("Which MsLoggy warn logging should be enabled?  This log type is helpful for catching important but non-fatal issues")
			.defineList("Warn Logging", new ArrayList<>(List.of(LOG_CODE.API.code)), s -> s instanceof String);
		ERROR_LOGGING = builder
			.comment("Which Error Logging should be enabled.  it is highly recommended this stay true.  however, you can",
				"disable it to remove pmmo errors from the log.")
			.defineList("Error Logging", new ArrayList<>(List.of(LOG_CODE.DATA.code, LOG_CODE.API.code)), s -> s instanceof String);
		FATAL_LOGGING = builder
			.comment("Which MsLoggy fatal logging should be enabled?  I can't imagine a situation where you'd want this off, but here you go.")
			.defineList("Fatal Logging", new ArrayList<>(List.of(LOG_CODE.API.code)), s -> s instanceof String);
		
		builder.pop(); //Ms. Loggy Block
	}
	
	//====================SERVER SETTINGS===============================
	
	private static void setupServer(ModConfigSpec.Builder builder) {
		
	}
}
