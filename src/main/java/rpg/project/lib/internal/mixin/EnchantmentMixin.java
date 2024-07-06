package rpg.project.lib.internal.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {
        return ObjectType.ENCHANTMENT;
    }
}
