package rpg.project.lib.internal.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin(Entity.class)
public class EntityMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {return ObjectType.ENTITY;}
}
