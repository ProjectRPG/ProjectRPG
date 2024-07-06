package rpg.project.lib.internal.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

@Mixin(Player.class)
public class PlayerMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {
        return ObjectType.PLAYER;
    }
}
