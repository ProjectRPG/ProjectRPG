package rpg.project.lib.internal.mixin;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin(Biome.class)
public class BiomeMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {
        return ObjectType.BIOME;
    }
}
