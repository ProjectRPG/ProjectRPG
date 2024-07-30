package rpg.project.lib.internal.setup.datagen;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.common.data.LanguageProvider;
import rpg.project.lib.internal.util.Reference;

public class LangProvider extends LanguageProvider {
	public enum Locale {
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
		
		public final String str;
		Locale(String locale) { str = locale; }
	}

	private final String locale;
	
	public LangProvider(PackOutput output, Locale locale) {
		super(output, Reference.MODID, locale.str);
		this.locale = locale.str;
	}
	
	//Insert Translations between these lines
	//Example Translation
	public static final Translation EXAMPLE = Translation.Builder.start("key.key.key")
			.addLocale(Locale.EN_US, "Translated Text").build();

	//======EVENTS===========
	public static final Translation LEVEL_UP = Translation.Builder.start("projectrpg.event.projectrpg.level_up")
			.addLocale(Locale.EN_US, "Leveling Up").build();
	public static final Translation ANVIL_REPAIR = Translation.Builder.start("projectrpg.event.projectrpg.anvil_repair")
			.addLocale(Locale.EN_US, "Anvil Repairing").build();
	public static final Translation BREAK_BLOCK = Translation.Builder.start("projectrpg.event.projectrpg.break_block")
			.addLocale(Locale.EN_US, "Breaking Block").build();
	public static final Translation BREAK_SPEED = Translation.Builder.start("projectrpg.event.projectrpg.break_speed")
			.addLocale(Locale.EN_US, "Digging").build();
	public static final Translation PLACE_BLOCK = Translation.Builder.start("projectrpg.event.projectrpg.place_block")
			.addLocale(Locale.EN_US, "Placing Block").build();
	public static final Translation BREATH_CHANGE = Translation.Builder.start("projectrpg.event.projectrpg.breath_change")
			.addLocale(Locale.EN_US, "Breathing").build();
	public static final Translation BREEDING = Translation.Builder.start("projectrpg.event.projectrpg.breed_animal")
			.addLocale(Locale.EN_US, "Breeding").build();
	public static final Translation TAMING = Translation.Builder.start("projectrpg.event.projectrpg.tame_animal")
			.addLocale(Locale.EN_US, "Taming").build();
	public static final Translation PLAYER_FISH = Translation.Builder.start("projectrpg.event.projectrpg.player_fish")
			.addLocale(Locale.EN_US, "Player Fishing").build();
	public static final Translation BREWING = Translation.Builder.start("projectrpg.event.projectrpg.brew_potion")
			.addLocale(Locale.EN_US, "Brewing Potion").build();
	public static final Translation EATING = Translation.Builder.start("projectrpg.event.projectrpg.consume")
			.addLocale(Locale.EN_US, "Eating/Drinking").build();
	public static final Translation CRAFTING = Translation.Builder.start("projectrpg.event.projectrpg.item_crafted")
			.addLocale(Locale.EN_US, "Crafting").build();
	public static final Translation DEATH = Translation.Builder.start("projectrpg.event.projectrpg.on_death")
			.addLocale(Locale.EN_US, "Killing").build();
	public static final Translation EFFECT = Translation.Builder.start("projectrpg.event.projectrpg.effect_added")
			.addLocale(Locale.EN_US, "Effects").build();
	public static final Translation HEAL = Translation.Builder.start("projectrpg.event.projectrpg.heal")
			.addLocale(Locale.EN_US, "Healing").build();
	public static final Translation JUMP = Translation.Builder.start("projectrpg.event.projectrpg.jump")
			.addLocale(Locale.EN_US, "Jumping").build();
	public static final Translation SPRINT_JUMP = Translation.Builder.start("projectrpg.event.projectrpg.sprint_jump")
			.addLocale(Locale.EN_US, "Sprint Jumping").build();
	public static final Translation CROUCH_JUMP = Translation.Builder.start("projectrpg.event.projectrpg.crouch_jump")
			.addLocale(Locale.EN_US, "Crouch Jumping").build();
	public static final Translation ON_ATTACK = Translation.Builder.start("projectrpg.event.projectrpg.player_attack_entity")
			.addLocale(Locale.EN_US, "Attacking").build();
	public static final Translation ON_DAMAGE = Translation.Builder.start("projectrpg.event.projectrpg.damaged_by_player")
			.addLocale(Locale.EN_US, "Attacking").build();
	public static final Translation GETTING_HURT = Translation.Builder.start("projectrpg.event.projectrpg.damage_player")
			.addLocale(Locale.EN_US, "Getting Hurt").build();
	public static final Translation MITIGATING = Translation.Builder.start("projectrpg.event.projectrpg.mitigated_damage")
			.addLocale(Locale.EN_US, "Preventing Damage").build();
	public static final Translation MITIGATING_ARMOR = Translation.Builder.start("projectrpg.event.projectrpg.mitigated_damage_armor")
			.addLocale(Locale.EN_US, "Damage Blocked by Armor").build();
	public static final Translation MITIGATING_ABS = Translation.Builder.start("projectrpg.event.projectrpg.mitigated_damage_absorption")
			.addLocale(Locale.EN_US, "Damage Blocked by Absorption").build();
	public static final Translation MITIGATING_EFFECT = Translation.Builder.start("projectrpg.event.projectrpg.mitigated_damage_effect")
			.addLocale(Locale.EN_US, "Damage Blocked by Effects").build();
	public static final Translation MITIGATING_ENCHANT = Translation.Builder.start("projectrpg.event.projectrpg.mitigated_damage_enchants")
			.addLocale(Locale.EN_US, "Damage Blocked by Enchantments").build();
	public static final Translation MITIGATING_SHIELD = Translation.Builder.start("projectrpg.event.projectrpg.mitigated_damage_block")
			.addLocale(Locale.EN_US, "Damage Blocked by Shields").build();
	public static final Translation SPRINTING = Translation.Builder.start("projectrpg.event.projectrpg.sprinting")
			.addLocale(Locale.EN_US, "Sprinting").build();
	public static final Translation SUBMERGED = Translation.Builder.start("projectrpg.event.projectrpg.submerged")
			.addLocale(Locale.EN_US, "Being Underwater").build();
	public static final Translation SWIMMING = Translation.Builder.start("projectrpg.event.projectrpg.swimming")
			.addLocale(Locale.EN_US, "Swimming").build();
	public static final Translation DIVING = Translation.Builder.start("projectrpg.event.projectrpg.diving")
			.addLocale(Locale.EN_US, "Diving").build();
	public static final Translation SURFACING = Translation.Builder.start("projectrpg.event.projectrpg.surfacing")
			.addLocale(Locale.EN_US, "Surfacing").build();
	public static final Translation SWIM_SPRINT = Translation.Builder.start("projectrpg.event.projectrpg.swim_sprinting")
			.addLocale(Locale.EN_US, "Swim Sprinting").build();
	public static final Translation RIDING = Translation.Builder.start("projectrpg.event.projectrpg.riding")
			.addLocale(Locale.EN_US, "Riding").build();
	public static final Translation USE_ITEM = Translation.Builder.start("projectrpg.event.projectrpg.use_item")
			.addLocale(Locale.EN_US, "Using an Item").build();
	
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
	public static final Translation PARTY_JOIN_SUCCESS = Translation.Builder.start("projectrpg.cmd.party.join.success")
			.addLocale(Locale.EN_US, "You have added %s to the %s party.").build();
	
	//=======ABILITIES=======
	public static final Translation PERK_EFFECT = Translation.Builder.start("ability.projectrpg.effect")
			.addLocale(Locale.EN_US, "Status Effect").build();
	public static final Translation PERK_EFFECT_DESC = Translation.Builder.start("ability.projectrpg.effect.description")
			.addLocale(Locale.EN_US, "Grants the player an effect. If the player already has the effect, it pauses the cooldown").build();
	public static final Translation PERK_EFFECT_STATUS_1 = Translation.Builder.start("ability.projectrpg.effect.status1")
			.addLocale(Locale.EN_US, "Effect: %s").build();
	public static final Translation PERK_EFFECT_STATUS_2 = Translation.Builder.start("ability.projectrpg.effect.status2")
			.addLocale(Locale.EN_US, "Lvl:%s for %ss").build();
	public static final Translation ATTRIBUTE_DESC = Translation.Builder.start("ability.projectrpg.attribute")
			.addLocale(Locale.EN_US, "Player Attribute").build();
	public static final Translation COMMAND_DESC = Translation.Builder.start("ability.projectrpg.command")
			.addLocale(Locale.EN_US, "Execute Command/Function").build();

	//========KEY BINDS=========
	public static final Translation KEYBIND_CATEGORY = Translation.Builder.start("category.projectrpg")
			.addLocale(Locale.EN_US, "Project RPG").build();
	public static final Translation KEYBIND_SHOW_PROGRESSION = Translation.Builder.start("key.projectrpg.showprogression")
			.addLocale(Locale.EN_US, "Opens or closes the left-side menu").build();
	public static final Translation KEYBIND_SHOW_ABILITIES = Translation.Builder.start("key.projectrpg.showabilities")
			.addLocale(Locale.EN_US, "Opens or closes the right-side menu").build();

	//=======VANILLA SYSTEM TRANSLATIONS================
	public static final Translation ABILITY_SIDE_PANEL_HEADER = Translation.Builder.start("vanilla.ability.panel.header")
			.addLocale(Locale.EN_US, "Recently Activated Abilities").build();
	public static final Translation PROGRESSION_SIDE_PANEL_HEADER = Translation.Builder.start("vanilla.progression.panel.header")
			.addLocale(Locale.EN_US, "Recently Gained Experience").build();
	public static final Translation PROGRESSION_GAIN = Translation.Builder.start("vanilla.progression.panel.gain")
			.addLocale(Locale.EN_US, "%s Exp from %s").build();
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
		if (translation.localeMap().get(this.locale) != null)
			add(translation.key(), translation.localeMap().get(this.locale));
	}
	
	public record Translation(String key, Map<String, String> localeMap) {
		public MutableComponent asComponent() {
			return Component.translatable(key);
		}
		public MutableComponent asComponent(Object... obj) {
			return Component.translatable(key(), obj);
		}
		
		public static class Builder {
			private final String key;
			private final Map<String, String> localeMap;
			
			private Builder(String key) {
				this.key = key;
				localeMap = new HashMap<>();
			}
			
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
