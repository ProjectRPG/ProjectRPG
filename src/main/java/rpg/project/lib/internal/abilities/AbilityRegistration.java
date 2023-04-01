package rpg.project.lib.internal.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.enums.AbilitySide;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.TagBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbilityRegistration {
    public static void init() {
        AbilityUtils.registerAbility(Reference.resource("break_speed"),
            Ability.begin()
                .addDefaults(getDefaults())
                .setStart((player, compoundTag) -> {
                    float speedIn = compoundTag.contains(AbilityUtils.BREAK_SPEED_INPUT_VALUE) ? compoundTag.getFloat(AbilityUtils.BREAK_SPEED_INPUT_VALUE) : player.getMainHandItem().getDestroySpeed(Blocks.OBSIDIAN.defaultBlockState());
                    float speedBonus = getRatioForTool(player.getMainHandItem(), compoundTag);
                    if (speedBonus == 0) { return new CompoundTag(); }
                    
                    float newSpeed = speedIn * Math.max(0, 1 + compoundTag.getInt(AbilityUtils.SKILL_LEVEL) * speedBonus);
                    return TagBuilder.start().withFloat(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE, newSpeed).build();
                })
                .setStatus((player, settings) -> {
                    List<MutableComponent> lines = new ArrayList<>();
                    // int skillLevel = settings.getInt(APIUtils.SKILL_LEVEL);
                    // DIG_ACTIONS.stream()
                    //     .filter(action -> settings.getFloat(action.name()) > 0)
                    //     .forEach(action -> lines.add(LangProvider.PERK_BREAK_SPEED_STATUS_1.asComponent(action.name(), settings.getFloat(action.name()) * (float) skillLevel)));
                    lines.add(Component.literal("TEST"));
                    return lines;
                })
                .build(),
            AbilitySide.BOTH
        );
    }
    
    private static Set<ToolAction> DIG_ACTIONS = Set.of(ToolActions.PICKAXE_DIG, ToolActions.AXE_DIG, ToolActions.SHOVEL_DIG, ToolActions.HOE_DIG, ToolActions.SHEARS_DIG, ToolActions.SWORD_DIG);
    
    private static float getRatioForTool(ItemStack tool, CompoundTag nbt) {
        float ratio = 0f;
        for (ToolAction action : DIG_ACTIONS) {
            if (tool.canPerformAction(action)) {
                ratio += nbt.getFloat(action.name());
            }
        }
        return ratio;
    }
    
    public static CompoundTag getDefaults() {
        TagBuilder builder = TagBuilder.start();
        for (ToolAction action : DIG_ACTIONS) {
            builder.withFloat(action.name(), 0);
        }
        return builder.build();
    }
}
