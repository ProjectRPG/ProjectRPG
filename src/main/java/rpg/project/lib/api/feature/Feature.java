package rpg.project.lib.api.feature;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;

import java.util.function.BiPredicate;

/**<p></p>Features are special callbacks that occur after event gating but before abilities
 * and progression have been executed.  Features are intended to implement features that
 * rely on aspects of the RPG ecosystem, but aren't necessarily abilities.
 * <p>
 * Features can be configured on any {@link ObjectType} and will only present to the event
 * handler if that object is contextually present in the event.
 * <p>
 * Examples include: <ul>
 * <li>adding stats to items when crafted based on a user's progression</li>
 * <li>increasing/decreasing damage to the player based on biome and equipment worn scaling with progression</li>
 * <li>applying a debuff to the player when an event is gated less than 1</li>
 * <li>sending messages to players about the statistics of their play relative to their progression</li>
 * <li>scaling mobs that spawn near a player according to their progression</li>
 * </ul>
 * @param featureID the id used in configurations to reference this feature
 * @param execution consumes state information and executes feature behavior when invoked
 * @param isValidContext determines if the feature can execute when invoked.  This is tested when features are queried
*         by the event and determines if this should be passed back to the event handlers.  The event
 *        ID and context are provided.  Sidedness test should be conducted here.
 */
public record Feature(ResourceLocation featureID, FeatureFunction execution, BiPredicate<ResourceLocation, EventContext> isValidContext) {
}



    