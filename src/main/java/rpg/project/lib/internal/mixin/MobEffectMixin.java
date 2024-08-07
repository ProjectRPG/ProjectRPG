package rpg.project.lib.internal.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin({MobEffect.class, MobEffectInstance.class})
public class MobEffectMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {
        return ObjectType.EFFECT;
    }
}
