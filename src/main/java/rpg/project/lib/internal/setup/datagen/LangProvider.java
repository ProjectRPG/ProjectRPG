package rpg.project.lib.internal.setup.datagen;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.data.LanguageProvider;
import rpg.project.lib.internal.util.Reference;

public class LangProvider extends LanguageProvider{
	private String locale;
	
	public static enum Locale {
		DE_DE("de_de"),
		EN_US("en_us"),
		ES_AR("es_ar"),
		ES_CL("es_cl"),
		ES_EC("es_ec"),
		ES_ES("es_es"),
		ES_MX("ex_mx"),
		ES_UY("es_uy"),
		ES_VE("es_ve"),
		FR_FR("fr_fr"),
		HU("hu"),
		IT_IT("it_it"),
		JA("ja"),
		KO_KR("ko_kr"),
		LT_LT("lt_lt"),
		NL_NL("nl_nl"),
		PL("pl"),
		PT_BR("pt_br"),
		RU_RU("ru_ru"),
		SV_SE("sv_se"),
		UK_UA("uk_ua"),
		ZH_CN("zh_cn"),
		ZH_TW("zh_tw");
		
		public String str;
		Locale(String locale) {str = locale;}
	}

	public LangProvider(PackOutput output, String locale) {
		super(output, Reference.MODID, locale);
	}
	
	//Insert Translations between these lines
	//Example Translation
	public static final Translation EXAMPLE = Translation.Builder.start("key.key.key")
			.addLocale(Locale.EN_US, "Translated Text").build();
	
	//======PARTY SYSTEM=====
	public static final Translation PARTY_CREATE_SUCCESS = Translation.Builder.start("projectrpg.cmd.party.create.success")
		.addLocale(Locale.EN_US, "Created Party: %s").build();
	public static final Translation PARTY_CREATE_FAILURE = Translation.Builder.start("projectrpg.cmd.party.create.failure")
			.addLocale(Locale.EN_US, "Party Creation Failed.").build();
	public static final Translation PARTY_LEAVE_SUCCESS = Translation.Builder.start("projectrpg.cmd.party.leave.success")
			.addLocale(Locale.EN_US, "You have left %s party").build();
	public static final Translation PARTY_LEAVE_FAILURE = Translation.Builder.start("projectrpg.cmd.party.leave.failure")
			.addLocale(Locale.EN_US, "You are not in a party to leave.").build();
	public static final Translation PARTY_LIST_SUCCESS = Translation.Builder.start("projectrpg.cmd.party.list.success")
			.addLocale(Locale.EN_US, "Member: %s").build();
	public static final Translation PARTY_INVITE_SUCCESS = Translation.Builder.start("projectrpg.cmd.party.invite.success")
			.addLocale(Locale.EN_US, "You have invited %s to the party.").build();
	public static final Translation PARTY_INVITE_FAILURE = Translation.Builder.start("projectrpg.cmd.party.invite.failure")
			.addLocale(Locale.EN_US, "You are not in a party to invite players to.").build();
	public static final Translation PARTY_UNINVITE_SUCCESS = Translation.Builder.start("projectrpg.cmd.party.uninvite.success")
			.addLocale(Locale.EN_US, "You have uninvited %s to the party.").build();
	public static final Translation PARTY_UNINVITE_FAILURE = Translation.Builder.start("projectrpg.cmd.party.uninvite.failure")
			.addLocale(Locale.EN_US, "You are not in a party to invite players to.").build();
	//End Translations
	
	@Override
	protected void addTranslations() {
		for (Field entry : this.getClass().getDeclaredFields()) {
			if (entry.getType() == Translation.class) {
				try {add((Translation)entry.get(LangProvider.class));}
				catch(Exception e) {e.printStackTrace();}
			}
		}
	}
	
	private void add(Translation translation) {
		if (translation.localeMap().get(locale) != null)
			add(translation.key(), translation.localeMap().get(locale));
	}
	
	public static record Translation(String key, Map<String, String> localeMap) {
		public MutableComponent asComponent() {
			return Component.translatable(key);
		}
		public MutableComponent asComponent(Object...obj) {
			return Component.translatable(key(), obj);
		}
		public static class Builder {
			private final String key;
			private Map<String, String> localeMap;
			private Builder(String key) {this.key = key; localeMap = new HashMap<>();}
			
			public static Builder start(String key) {
				return new Builder(key);
			}
			public Builder addLocale(Locale locale, String translation) {
				this.localeMap.put(locale.str, translation);
				return this;
			}
			public Translation build() {
				return new Translation(key, localeMap);
			}
		}
	}

}
