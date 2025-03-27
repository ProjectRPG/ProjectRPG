package rpg.project.lib.api.client.components;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**Utilities for the client which safely do not access server-only classes*/
public class ClientUtils {

    /**Utility method for obtaining the translated name of any event from its ID.
     * translations of your own events should conform to the format of
     * <code>projectrpg.event.namespace.eventname</code>
     *
     * @param eventID the id of the event
     * @return a translatable component of the event name
     */
    public static MutableComponent getEventName(ResourceLocation eventID) {
        return Component.translatable("projectrpg.event."+eventID.toString().replace(":","."));
    }

    /**Utility method for obtaining the translated name of any ability from its ID.
     * translations of your own abilities should conform to the format of
     * <code>ability.namespace.abilityid</code>
     *
     * @param abilityID the id of the ability
     * @return a translatable component of the ability name
     */
    public static MutableComponent getAbilityName(ResourceLocation abilityID) {
        return Component.translatable("ability."+abilityID.toString().replace(":","."));
    }
}
