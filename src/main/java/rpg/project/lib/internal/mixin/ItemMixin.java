package rpg.project.lib.internal.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin({Item.class, ItemStack.class})
public class ItemMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {return ObjectType.ITEM;}
}
