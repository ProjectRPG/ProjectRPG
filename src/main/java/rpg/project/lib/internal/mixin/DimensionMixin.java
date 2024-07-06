package rpg.project.lib.internal.mixin;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin(DimensionType.class)
public class DimensionMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {
        return ObjectType.DIMENSION;
    }
}
