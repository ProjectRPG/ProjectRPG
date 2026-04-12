package rpg.project.lib.api.data;

/**This interface is layered over vanilla objects to allow the Context
 * Builder access to the ProjectRPG type values.  These are applied via
 * mixin to the vanilla objects at the root of each applicable registry.
 * <br>
 * It is highly unlikely that an object registered by a mod bypasses this
 * functionality.
 */
public interface DataObject{
    ObjectType prpg$getObjectType();
}
