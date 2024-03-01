package rpg.project.lib.builtins.vanilla;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import rpg.project.lib.api.party.PartySystem;

public class VanillaPartySystem implements PartySystem{
	private final HashMultimap<String, String> partyInvites = HashMultimap.create();

	@Override
	public boolean createParty(String partyName) {
		board().addPlayerTeam(partyName);		
		return true;
	}

	@Override
	public boolean closeParty(String partyName) {
		board().removePlayerTeam(board().getPlayerTeam(partyName));
		return false;
	}

	@Override
	public boolean invitePlayer(Player executor, String invitee) {
		PlayerTeam team = board().getPlayersTeam(executor.getUUID().toString());
		if (team != null) {
			partyInvites.put(team.getName(), invitee);
			return true;
		}			
		return false;
	}

	@Override
	public boolean revokeInvite(Player executor, String invitee) {
		PlayerTeam team = board().getPlayersTeam(executor.getUUID().toString());
		if (team != null) 
			return partyInvites.remove(team.getName(), invitee);
		return false;
	}

	@Override
	public boolean acceptInvite(Player invitee, String partyName) {
		if (partyInvites.get(partyName).contains(invitee.getUUID().toString())) {
			board().addPlayerToTeam(invitee.getUUID().toString(), board().getPlayerTeam(partyName));
			partyInvites.keySet().forEach(party -> partyInvites.get(party).remove(invitee.getUUID().toString()));
			return true;
		}
		return false;
	}

	@Override
	public boolean declineInvite(Player invitee, String partyName) {
		return partyInvites.get(partyName).remove(invitee.getUUID().toString());
	}

	@Override
	public boolean joinParty(Player executor, String partyName) {
		return board().addPlayerToTeam(executor.getStringUUID(), board().getPlayerTeam(partyName));
	}

	@Override
	public boolean leaveParty(Player executor, String partyName) {
		boolean success =  board().removePlayerFromTeam(executor.getStringUUID());
		if (getPartyMembers(partyName).isEmpty())
			closeParty(partyName);
		return success;
	}
	
	@Override
	public String getPlayerParty(UUID player) {
		return board().getPlayersTeam(player.toString()).getName();
	}

	@Override
	public List<UUID> getPartyMembers(String partyName) {
		return board().getPlayerTeam(partyName).getPlayers().stream().map(UUID::fromString).toList();
	}

	@Override
	public Component getPartyDisplayName(String partyName) {
		return board().getPlayerTeam(partyName).getDisplayName();
	}

	@Override
	public List<String> getAllParties() {
		return board().getPlayerTeams().stream().map(PlayerTeam::getName).toList();
	}

	@Override
	public List<Component> getAllPartyNames() {
		return board().getPlayerTeams().stream().map(PlayerTeam::getDisplayName).toList();
	}

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> getCommands() {
		return null;
	}

	private MinecraftServer server() {return ServerLifecycleHooks.getCurrentServer();}
	private ServerScoreboard board() {return server().getScoreboard();}
}
