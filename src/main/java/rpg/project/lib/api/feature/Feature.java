package rpg.project.lib.api.feature;

import net.minecraft.resources.ResourceLocation;

/**Features are special callbacks that occur after event gating but before abilities
 * and progression have been executed.  Features are intended to implement features that
 * rely on aspects of the RPG ecosystem, but aren't necessarily abilities.
 *
 * Examples include:
 * - adding stats to items when crafted based on a user's progression
 * - increasing/decreasing damage to the player based on biome and equipment worn scaling with progression
 * - applying a debuff to the player when an event is gated less than 1
 * - sending messages to players about the statistics of their play relative to their progression
 * - scaling mobs that spawn near a player according to their progression
 *
 * @param featureID
 * @param execution
 */
public record Feature(ResourceLocation featureID, FeatureFunction execution) {
}



    