package rpg.project.lib.internal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.fml.LogicalSide;
import rpg.project.lib.internal.Core;

public class CmdRoot {	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rpg")
				.then(getPartyNode())
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> getPartyNode() {
		var partyCommand = Core.get(LogicalSide.SERVER).getParty().getCommands();
		return partyCommand != null ? partyCommand :
			Commands.literal("party"); //TODO flesh out the default command structure
				
	}
}
