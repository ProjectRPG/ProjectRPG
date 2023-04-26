package rpg.project.lib.api.party;

import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**<p>A party is a group of players who share features in
 * some way.  The benefits they receive are determined
 * by the feature itself and are not the responsibility
 * of this system.</p>  
 * <p>An implementation of the party system may have more
 * complex inner workings than what is exposed by these 
 * methods. However, the other PRPG systems will only expect
 * to have access to these methods.</p>
 */
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
	 * <p><i>NOTE: the default implementation of this method is highly
	 * unoptimized.  Overriding is strongly advised.</i></p>  
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
	
	/**<p>This method is called once during server startup to set the party
	 * commands for the entire ecosystem.  If your implementation works
	 * with the default command set, you can return null to have this 
	 * ignored in favor of keeping the defaults.</p>
	 * <p><i>NOTE: your implementation replaces "<code>/rpg party</code>" with
	 * "<code>/rpg yourLiteralKeyword</code>"</i></p>
	 * 
	 * @return a replacement command tree for the party literal
	 */
	LiteralArgumentBuilder<CommandSourceStack> getCommands();
}
