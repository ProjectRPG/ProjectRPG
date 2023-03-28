package rpg.project.lib.internal.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;

import java.util.ArrayList;
import java.util.List;

public class Config {
	public static ForgeConfigSpec CLIENT_CONFIG;
	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec SERVER_CONFIG;
	
	static {
		ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
		
		setupClient(CLIENT_BUILDER);
		setupCommon(COMMON_BUILDER);
		setupServer(SERVER_BUILDER);
		
		CLIENT_CONFIG = CLIENT_BUILDER.build();
		COMMON_CONFIG = COMMON_BUILDER.build();
		SERVER_CONFIG = SERVER_BUILDER.build();
	}
	
	//====================CLIENT SETTINGS===============================
	
	private static void setupClient(ForgeConfigSpec.Builder builder) {
		
	}

	//====================COMMON SETTINGS===============================
	
	private static void setupCommon(ForgeConfigSpec.Builder builder) {
		builder.push("Common");
		
		buildMsLoggy(builder);
		
		builder.pop(); //Common Blocks
	}
	
	public static ConfigValue<List<? extends String>> INFO_LOGGING;
	public static ConfigValue<List<? extends String>> DEBUG_LOGGING;
	public static ConfigValue<List<? extends String>> WARN_LOGGING;
	public static ConfigValue<List<? extends String>> ERROR_LOGGING;
	public static ConfigValue<List<? extends String>> FATAL_LOGGING;
	
	private static void buildMsLoggy(ForgeConfigSpec.Builder builder) {
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
			.defineList("Debug Logging", new ArrayList<>(List.of(LOG_CODE.AUTO_VALUES.code)), s -> s instanceof String);
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
	
	private static void setupServer(ForgeConfigSpec.Builder builder) {
		
	}
}
