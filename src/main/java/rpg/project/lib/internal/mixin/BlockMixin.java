package rpg.project.lib.internal.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin({Block.class, BlockState.class, BlockEntity.class})
public class BlockMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {return ObjectType.BLOCK;}
}
