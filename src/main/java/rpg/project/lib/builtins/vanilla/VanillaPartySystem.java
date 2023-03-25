package rpg.project.lib.builtins.vanilla;

import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.party.PartySystem;

public class VanillaPartySystem implements PartySystem{

	@Override
	public boolean createParty(String partyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean closeParty(String partyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean invitePlayer(Player executor, String invitee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean revokeInvite(Player executor, String invitee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean acceptInvite(Player invitee, String partyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean declineInvite(Player invitee, String partyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean joinParty(Player executor, String partyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean leaveParty(Player executor, String partyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<UUID> getPartyMembers(String partyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component getPartyDisplayName(String partyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllParties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Component> getAllPartyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandNode<?> getCommands() {
		// TODO Auto-generated method stub
		return null;
	}

}
