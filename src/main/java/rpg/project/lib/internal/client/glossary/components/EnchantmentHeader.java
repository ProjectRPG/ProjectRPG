package rpg.project.lib.internal.client.glossary.components;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.util.RegistryUtil;

import java.util.Map;

public class EnchantmentHeader extends ReactiveWidget {
    private final Identifier id;
    private final String name;
    public EnchantmentHeader(Enchantment enchant) {
        super(0,0,50,20);
        Minecraft mc = Minecraft.getInstance();
        RegistryAccess access = mc.player.registryAccess();
        Holder<Enchantment> holder = access.lookupOrThrow(Registries.ENCHANTMENT).wrapAsHolder(enchant);
        ItemEnchantments.Mutable enchantDataComponent = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantDataComponent.set(holder, enchant.getMaxLevel());
        this.id = RegistryUtil.getId(access, enchant);
        this.name = enchant.description().toString();
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        stack.set(DataComponents.ENCHANTMENTS, enchantDataComponent.toImmutable());
        addChild(new ItemDisplayWidget(mc, 0,0,16,16, enchant.description(), stack, false, true)
                , PositionConstraints.grid(0,0)
                , SizeConstraints.builder().absoluteHeight(16).absoulteWidth(16).build());
        addString(enchant.description(), PositionConstraints.grid(0, 1), textConstraint);
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !(filter.matchesTextFilter(id.toString())
                || filter.matchesTextFilter(name))
                || !filter.matchesObject(ObjectType.ENCHANTMENT);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.GRID;}
}
