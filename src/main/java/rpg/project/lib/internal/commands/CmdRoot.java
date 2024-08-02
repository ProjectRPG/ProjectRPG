package rpg.project.lib.internal.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.writers.DatapackGenerator;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class CmdRoot {	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rpg")
				.then(getGenDataNode())
				.then(getPartyNode())
				.then(getProgressNode()));
	}
	
	private static LiteralArgumentBuilder<CommandSourceStack> getProgressNode() {
		var progressCommand = Core.get(LogicalSide.SERVER).getProgression().getCommands();
		return progressCommand != null ? progressCommand :
			Commands.literal("progress")
				.executes(ctx -> {ctx.getSource().sendSuccess(() -> Component.literal("Unregistered Command"), false); return 0;});
	}
	
	private static final String NAME = "name";
	private static final String PLAYER = "player";
	private static LiteralArgumentBuilder<CommandSourceStack> getPartyNode() {
		var partyCommand = Core.get(LogicalSide.SERVER).getParty().getCommands();
		return partyCommand != null ? partyCommand :
			Commands.literal("party")
			.then(Commands.literal("create")
				.then(Commands.argument(NAME, StringArgumentType.word())
					.executes(ctx -> {
						Core core = Core.get(LogicalSide.SERVER); 
						String partyName = StringArgumentType.getString(ctx, NAME);
						if (core.getParty().createParty(partyName)) {
							Component name = core.getParty().getPartyDisplayName(partyName);
							core.getParty().joinParty(ctx.getSource().getPlayerOrException(), partyName);
							ctx.getSource().sendSuccess(() -> LangProvider.PARTY_CREATE_SUCCESS.asComponent(name), true);
							return 0;
						}
						ctx.getSource().sendFailure(LangProvider.PARTY_CREATE_FAILURE.asComponent());
						return 1;
					})))
			.then(Commands.literal("leave")
				.executes(ctx -> {
					Core core = Core.get(LogicalSide.SERVER);
					Player player = ctx.getSource().getPlayerOrException();
					String party = core.getParty().getPlayerParty(player.getUUID());
					if (core.getParty().leaveParty(player, party)) {
						Component name = core.getParty().getPartyDisplayName(party);
						ctx.getSource().sendSuccess(() -> LangProvider.PARTY_LEAVE_SUCCESS.asComponent(name), false);
						return 0;
					}
					ctx.getSource().sendFailure(LangProvider.PARTY_LEAVE_FAILURE.asComponent());
					return 1;
				}))
			.then(Commands.literal("invite")
				.then(Commands.argument(PLAYER, EntityArgument.players())
					.executes(ctx -> {
						Core core = Core.get(LogicalSide.SERVER);
						if (core.getParty().getPlayerParty(ctx.getSource().getPlayerOrException().getUUID()) == null) {
							ctx.getSource().sendFailure(LangProvider.PARTY_INVITE_FAILURE.asComponent());
							return 1;
						}
						List<ServerPlayer> invitedPlayers = new ArrayList<>();
						ServerPlayer executor = ctx.getSource().getPlayerOrException();
						for (ServerPlayer player : EntityArgument.getPlayers(ctx, PLAYER)) {
							if (core.getParty().invitePlayer(executor, player.getStringUUID()))
								invitedPlayers.add(player);
						}
						ctx.getSource().sendSuccess(() -> LangProvider.PARTY_INVITE_SUCCESS.asComponent(invitedPlayers), false);
						return 0;
					})))
			.then(Commands.literal("uninvite")
				.then(Commands.argument(PLAYER, EntityArgument.players())
					.executes(ctx -> {
						Core core = Core.get(LogicalSide.SERVER);
						if (core.getParty().getPlayerParty(ctx.getSource().getPlayerOrException().getUUID()) == null) {
							ctx.getSource().sendFailure(LangProvider.PARTY_UNINVITE_FAILURE.asComponent());
							return 1;
						}
						List<ServerPlayer> uninvitedPlayers = new ArrayList<>();
						ServerPlayer executor = ctx.getSource().getPlayerOrException();
						for (ServerPlayer player : EntityArgument.getPlayers(ctx, PLAYER)) {
							if (core.getParty().revokeInvite(executor, player.getStringUUID()))
								uninvitedPlayers.add(player);
						}
						ctx.getSource().sendSuccess(() -> LangProvider.PARTY_UNINVITE_SUCCESS.asComponent(uninvitedPlayers), false);
						return 0;
					})))
			.then(Commands.literal("listMembers")
				.then(Commands.argument(NAME, StringArgumentType.word())
					.executes(ctx -> {
						for (UUID playerID : Core.get(LogicalSide.SERVER).getParty().getPartyMembers(StringArgumentType.getString(ctx, NAME))) {
							String name = ctx.getSource().getServer().getProfileCache().get(playerID).get().getName();
							ctx.getSource().sendSuccess(() -> LangProvider.PARTY_LIST_SUCCESS.asComponent(name), false);
						}
						return 0;
					})))
			.then(Commands.literal("listParties")
				.executes(ctx -> {
					Core core = Core.get(LogicalSide.SERVER);
					Map<String, Integer> parties = core.getParty().getAllParties().stream()
							.collect(Collectors.toMap(name -> name, name -> core.getParty().getPartyMembers(name).size()));
					for (String party : core.getParty().getAllParties()) {
						ctx.getSource().sendSuccess(() -> core.getParty().getPartyDisplayName(party).copy()
								.append("("+parties.getOrDefault(party, 0)+")"), false);
					}
					return 0;
				}))
			.then(Commands.literal("join")
				.requires(ctx -> ctx.hasPermission(2))
				.then(Commands.argument(PLAYER, EntityArgument.players())
					.then(Commands.argument(NAME, StringArgumentType.word())
						.executes(ctx -> {
							Core core = Core.get(LogicalSide.SERVER);
							String partyName = StringArgumentType.getString(ctx, NAME);
							Component prettyPartyName = core.getParty().getPartyDisplayName(partyName);
							EntityArgument.getPlayers(ctx, PLAYER).forEach(player -> {								
								core.getParty().joinParty(player, partyName);
								ctx.getSource().sendSuccess(() -> LangProvider.PARTY_JOIN_SUCCESS.asComponent(player.getDisplayName(), prettyPartyName), false);
							});
							return 0;
						}))));
				
	}

	private static LiteralArgumentBuilder<CommandSourceStack> getGenDataNode() {
		return Commands.literal("genData").executes(ctx -> DatapackGenerator.generatePack(ctx.getSource().getServer()));
	}
}
