package io.maddymakesgames.discordlink.DiscordBot.Util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.maddymakesgames.discordlink.DiscordBot.Permission;
import net.minecraft.server.command.ServerCommandSource;

public interface DiscordCommand {

	void register(CommandDispatcher<ServerCommandSource> dispatcher);
	String getName();
	Permission getPermission();
	CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException;
}
