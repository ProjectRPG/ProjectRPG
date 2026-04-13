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
	//region======GLOSSARY VALUES========
	public static final Translation NONE_DROPDOWN = Translation.Builder.start("prpg.glossary.dropdown.none")
			.addLocale(Locale.EN_US, "Choose a System").build();
	public static final Translation CHOOSE_DROPDOWN = Translation.Builder.start("prpg.glossary.dropdown.choose")
			.addLocale(Locale.EN_US, "Choose a Type").build();
	public static final Translation CHOOSE_ID = Translation.Builder.start("prpg.glossary.dropdown.choose_id")
			.addLocale(Locale.EN_US, "Filter by Addon").build();
	public static final Translation ABILITY_DROPDOWN = Translation.Builder.start("prpg.glossary.dropdown.ability")
			.addLocale(Locale.EN_US, "Player Abilities").build();
	public static final Translation FEATURE_DROPDOWN = Translation.Builder.start("prpg.glossary.dropdown.features")
			.addLocale(Locale.EN_US, "Gameplay Features").build();
	public static final Translation PROGRESSION_DROPDOWN = Translation.Builder.start("prpg.glossary.dropdown.progression")
			.addLocale(Locale.EN_US, "Progression").build();
	public static final Translation OBJECT_DROPDOWN_ITEM = Translation.Builder.start("prpg.glossary.dropdown.object.item")
			.addLocale(Locale.EN_US, "Items").build();
	public static final Translation OBJECT_DROPDOWN_BLOCK = Translation.Builder.start("prpg.glossary.dropdown.object.block")
			.addLocale(Locale.EN_US, "Blocks").build();
	public static final Translation OBJECT_DROPDOWN_ENTITY = Translation.Builder.start("prpg.glossary.dropdown.object.entity")
			.addLocale(Locale.EN_US, "Animals/Mobs").build();
	public static final Translation OBJECT_DROPDOWN_DIMENSION = Translation.Builder.start("prpg.glossary.dropdown.object.dimension")
			.addLocale(Locale.EN_US, "Dimensions").build();
	public static final Translation OBJECT_DROPDOWN_BIOME = Translation.Builder.start("prpg.glossary.dropdown.object.biome")
			.addLocale(Locale.EN_US, "Biomes").build();
	public static final Translation OBJECT_DROPDOWN_ENCHANTMENT = Translation.Builder.start("prpg.glossary.dropdown.object.enchantment")
			.addLocale(Locale.EN_US, "Enchantments").build();
	public static final Translation OBJECT_DROPDOWN_EFFECT = Translation.Builder.start("prpg.glossary.dropdown.object.effect")
			.addLocale(Locale.EN_US, "Effects").build();
	public static final Translation OBJECT_DROPDOWN_EVENT = Translation.Builder.start("prpg.glossary.dropdown.object.event")
			.addLocale(Locale.EN_US, "Gameplay Events").build();
	public static final Translation OBJECT_DROPDOWN_PLAYER = Translation.Builder.start("prpg.glossary.dropdown.object.player")
			.addLocale(Locale.EN_US, "Player-Specific").build();
	//endregion===GLOSSARY VALUES========
	//region======GUI PANEL VALUES=======
	public static final Translation CONDITION_ANY_OF = Translation.Builder.start("prpg.condition.any_of.header")
			.addLocale(Locale.EN_US, "If any of the following are true:").build();
	public static final Translation CONDITION_ALL_OF = Translation.Builder.start("prpg.condition.all_of.header")
			.addLocale(Locale.EN_US, "If ALL of the following are true:").build();
	public static final Translation CONDITION_NOT = Translation.Builder.start("prpg.condition.not.header")
			.addLocale(Locale.EN_US, "When the following is false:").build();
	public static final Translation CONDITION_ENTITY_MATCH = Translation.Builder.start("prpg.condition.entity_match")
			.addLocale(Locale.EN_US, "When the %s equals %s").build();
	public static final Translation ENTITY_PARAM_PRPG_ACTOR = Translation.Builder.start("prpg.entity_param.projectrpg.actor")
			.addLocale(Locale.EN_US, "action doer").build();
	public static final Translation CONDITION_NBT = Translation.Builder.start("prpg.condition.nbt.header")
			.addLocale(Locale.EN_US, "If the specific NBT value matches any of the following cases:").build();
	//endregion===GUI PANEL VALUES=======
	//region======NBT GLOSSARY===========
	public static final Translation GLOSSARY_NBT_PATH = Translation.Builder.start("prpg.gui.glossary.nbt.path")
			.addLocale(Locale.EN_US, "the value at location(s):").build();
	public static final Translation GLOSSARY_NBT_CRITERIA = Translation.Builder.start("prpg.gui.glossary.nbt.criteria")
			.addLocale(Locale.EN_US, "Matches any of the following:").build();
	public static final Translation GLOSSARY_NBT_OP_EQ = Translation.Builder.start("prpg.gui.glossary.nbt.op.eq")
			.addLocale(Locale.EN_US, "value is exactly %s").build();
	public static final Translation GLOSSARY_NBT_OP_GT = Translation.Builder.start("prpg.gui.glossary.nbt.op.gt")
			.addLocale(Locale.EN_US, "value > %s").build();
	public static final Translation GLOSSARY_NBT_OP_LT = Translation.Builder.start("prpg.gui.glossary.nbt.op.lt")
			.addLocale(Locale.EN_US, "value < %s").build();
	public static final Translation GLOSSARY_NBT_OP_GTOE = Translation.Builder.start("prpg.gui.glossary.nbt.op.gtoe")
			.addLocale(Locale.EN_US, "value >= %s").build();
	public static final Translation GLOSSARY_NBT_OP_LTOE = Translation.Builder.start("prpg.gui.glossary.nbt.op.ltoe")
			.addLocale(Locale.EN_US, "value <= %s").build();
	public static final Translation GLOSSARY_NBT_OP_EXISTS = Translation.Builder.start("prpg.gui.glossary.nbt.op.exists")
			.addLocale(Locale.EN_US, "value is present").build();
	public static final Translation GLOSSARY_NBT_OP_CONTAINS = Translation.Builder.start("prpg.gui.glossary.nbt.op.contains")
			.addLocale(Locale.EN_US, "value partially contains %s").build();
	//endregion===NBT GLOSSARY===========
	//region======GATE SYSTEM TYPES======
	public static final Translation GATE_TYPE_EVENT = Translation.Builder.start("prpg.gating.type.event")
			.addLocale(Locale.EN_US, "Event Gates").build();
	public static final Translation GATE_TYPE_PROGRESSION = Translation.Builder.start("prpg.gating.type.progression")
			.addLocale(Locale.EN_US, "Progress Gates").build();
	public static final Translation GATE_TYPE_ABILITY = Translation.Builder.start("prpg.gating.type.ability")
			.addLocale(Locale.EN_US, "Ability Gates").build();
	public static final Translation GATE_TYPE_FEATURE = Translation.Builder.start("prpg.gating.type.feature")
			.addLocale(Locale.EN_US, "Feature Gates").build();
	//endregion GATE SYSTEM TYPES
	//region ======EVENTS===========
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
	//endregion EVENTS
	//region======PARTY SYSTEM=====
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
	//endregion PARTY SYSTEM
	//region=======ABILITIES=======
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
	public static final Translation ATTRIBUTE_STATUS1 = Translation.Builder.start("ability.projectrpg.attribute.status1")
			.addLocale(Locale.EN_US, "%s modified by %s").build();
	public static final Translation COMMAND_DESC = Translation.Builder.start("ability.projectrpg.command")
			.addLocale(Locale.EN_US, "Execute Command/Function").build();
	public static final Translation COMMAND_STATUS_1 = Translation.Builder.start("ability.projectrpg.cmd.status1")
			.addLocale(Locale.EN_US, "%s: `%s` executed").build();
	public static final Translation COMMMAND_COMMAND = Translation.Builder.start("ability.projectrpg.cmd.cmd")
			.addLocale(Locale.EN_US, "Command").build();
	public static final Translation COMMMAND_FUNCTION = Translation.Builder.start("ability.projectrpg.cmd.fnc")
			.addLocale(Locale.EN_US, "Function").build();
	public static final Translation MODIFY = Translation.Builder.start("ability.projectrpg.modify")
			.addLocale(Locale.EN_US, "Modify Event").build();
	public static final Translation MODIFY_STATUS = Translation.Builder.start("ability.projectrpg.modify.status")
			.addLocale(Locale.EN_US, "Event output changed by %s").build();
	public static final Translation BREAK_SPEED_ABILITY = Translation.Builder.start("ability.projectrpg.break_speed")
			.addLocale(Locale.EN_US, "Dig Speed Boost").build();
	public static final Translation BREAK_SPEED_ABILITY_STATUS1 = Translation.Builder.start("ability.projectrpg.break_speed.status1")
			.addLocale(Locale.EN_US, "%s increased by %s").build();
	//endregion ABILITIES
	//region========KEY BINDS=========
	public static final Translation KEYBIND_CATEGORY = Translation.Builder.start("category.projectrpg")
			.addLocale(Locale.EN_US, "Project RPG").build();
	public static final Translation KEYBIND_SHOW_PROGRESSION = Translation.Builder.start("key.projectrpg.showprogression")
			.addLocale(Locale.EN_US, "Opens or closes the left-side menu").build();
	public static final Translation KEYBIND_SHOW_ABILITIES = Translation.Builder.start("key.projectrpg.showabilities")
			.addLocale(Locale.EN_US, "Opens or closes the right-side menu").build();
	public static final Translation GLOSSARY_OPEN = Translation.Builder.start("key.projectrpg.openglossary")
			.addLocale(Locale.EN_US, "Opens the Glossary menu").build();
	//endregion KEY BINDS
	//region=======VANILLA SYSTEM TRANSLATIONS================
	public static final Translation ABILITY_SIDE_PANEL_HEADER = Translation.Builder.start("vanilla.ability.panel.header")
			.addLocale(Locale.EN_US, "Recently Activated Abilities").build();
	public static final Translation ABILITY_SIDE_PANEL_EVENT_HEADER = Translation.Builder.start("vanilla.ability.panel.eventheader")
			.addLocale(Locale.EN_US, "%s activated for %s").build();
	public static final Translation PROGRESSION_SIDE_PANEL_HEADER = Translation.Builder.start("vanilla.progression.panel.header")
			.addLocale(Locale.EN_US, "Recently Gained Experience").build();
	public static final Translation PROGRESSION_GAIN = Translation.Builder.start("vanilla.progression.panel.gain")
			.addLocale(Locale.EN_US, "%s Exp from %s").build();
	//endregion Vanilla System Translations
	
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
