package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.maddymakesgames.discordlink.DiscordBot.Permission;
import net.minecraft.server.command.ServerCommandSource;

public interface DiscordCommand {

	void register(CommandDispatcher<ServerCommandSource> dispatcher);
	String getName();
	Permission getPermission();
	boolean requireLink();
	CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException;
	CommandNode<ServerCommandSource> getNode();
}
