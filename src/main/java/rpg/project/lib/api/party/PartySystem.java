package rpg.project.lib.api.party;

import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface PartySystem {
	boolean createParty(String partyName);
	boolean closeParty(String partyName);
	
	boolean invitePlayer(Player executor, String invitee);
	boolean revokeInvite(Player executor, String invitee);
	boolean acceptInvite(Player invitee, String partyName);
	boolean declineInvite(Player invitee, String partyName);
	boolean joinParty(Player executor, String partyName);
	boolean leaveParty(Player executor, String partyName);
	
	/**Checks if the provided player is a member of the specified party
	 * 
	 * @param subject the unique identifier of the player
	 * @param partyName the party's internal name
	 * @return if the player is in the party
	 */
	default boolean isPlayerAPartyMember(UUID subject, String partyName) {
		for (UUID player : getPartyMembers(partyName)) {
			if (player.equals(subject)) return true;
		}
		return false;
	}
	
	/**<p>Gets the internal party name of the provided player, if the
	 * player is in a party, else returns null.</p>
	 * <p><u>NOTE: the default implementation of this method is highly
	 * unoptimized.  Overriding is strongly advised.</u></p>  
	 * 
	 * @param player the unique identifier of the player
	 * @return the party internal name or null if not in a party
	 */
	default String getPlayerParty(UUID player) {
		for (String party : getAllParties()) {
			if (isPlayerAPartyMember(player, party)) return party;
		}
		return null;
	}
	
	/**Returns the {@link UUID} for each player in the party.
	 * 
	 * @param partyName the party's internal name
	 * @return all party members
	 */
	List<UUID> getPartyMembers(String partyName);
	
	Component getPartyDisplayName(String partyName);
	/**@return the internal ID of all parties currently created*/
	List<String> getAllParties();
	/**@return the display component for all parties currently created*/
	List<Component> getAllPartyNames();
	
	/**This method is called once during server startup to set the party
	 * commands for the entire ecosystem.  If your implementation works
	 * with the default command set, you can return null to have this 
	 * ignored in favor of keeping the defaults.
	 * 
	 * @return a replacement command tree for the party literal
	 */
	CommandNode<?> getCommands();
}
