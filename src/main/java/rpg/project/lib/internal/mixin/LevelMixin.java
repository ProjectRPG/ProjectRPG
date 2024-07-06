package rpg.project.lib.internal.mixin;


import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin(LevelAccessor.class)
public interface LevelMixin extends DataObject {
    @Override
    default ObjectType prpg$getObjectType() {return ObjectType.DIMENSION;}
}
