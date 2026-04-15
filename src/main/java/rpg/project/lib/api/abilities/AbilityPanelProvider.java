package rpg.project.lib.api.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.client.ReactiveWidget;

/**Abilities only exist in the context of an AbilitySystem.  It is the duty of
 * that system to implement a glossary panel and to invoke these using the registry
 * at {@link AbilityUtils#getAbilityPanel(Identifier)}
 */
@FunctionalInterface
public interface AbilityPanelProvider {
    /** Creates a widget to be displayed in the glossary for the given player and config.
     *
     * @param player the local player reference
     * @param config the configuration provided from data
     * @return a panel to be used by the AbilitySystem to display ability information
     */
    ReactiveWidget make(Player player, CompoundTag config);
}
