package rpg.project.lib.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;

import javax.swing.text.html.parser.Entity;

@Mixin(Entity.class)
public class EntityMixin implements DataObject {
    @Override
    public ObjectType prpg$getObjectType() {return ObjectType.ENTITY;}
}
