package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.DiscordBot.DiscordLinkBot;
import io.maddymakesgames.discordlink.DiscordBot.Permission;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.DiscordCommandSource;
import io.maddymakesgames.discordlink.Util.Linkable;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;

public class LinkCommand implements DiscordCommand {

	LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("link").executes(ctx -> CommandHelper.handleResponse(mcExecute(ctx), ctx.getSource().getPlayer()));

	RequiredArgumentBuilder<ServerCommandSource, String> name = CommandManager.argument("playername", StringArgumentType.greedyString())
																			  .requires(source -> ((DiscordCommandSource)source).isDiscord())
																			  .executes(ctx -> CommandHelper.handleResponse(execute(ctx), ctx.getSource().getPlayer()));

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(root);
	}

	@Override
	public String getName() {
		return "link";
	}

	@Override
	public Permission getPermission() {
		return Permission.Unregistered;
	}

	@Override
	public boolean requireLink() {
		return false;
	}

	@Override
	public CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		String playerName = ctx.getArgument("playername", String.class);
		if(playerName == null)
			return new CommandReturn("Please input a account name", 0, false);

		ServerPlayerEntity player = DiscordLink.instance.bot.registeringPlayersCache.get(playerName);

		if(player == null)
			return new CommandReturn("That account has not run /link", 0, false);

		User user = ((DiscordCommandSource)ctx.getSource()).getUser();

		((LinkablePlayer)player).link(user.getId());
		((LinkableUser)user).link(player.getUuid());

		return new CommandReturn("Successfully linked your account", 1, false);
	}

	public CommandReturn mcExecute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity caller = ctx.getSource().getPlayer();
		DiscordLink.instance.bot.registeringPlayersCache.put(caller.getDisplayName().asString(), caller);
		LogManager.getLogger("discord-link").info(((LinkablePlayer)caller).isLinked());
		return new CommandReturn(String.format("Please send %slink %s to %s", DiscordLink.instance.bot.prefix, ctx.getSource().getPlayer().getDisplayName().asString(), DiscordLinkBot.name), 1, false);
	}

	@Override
	public CommandNode<ServerCommandSource> getNode() {
		CommandNode<ServerCommandSource> rootNode = root.build();
		rootNode.addChild(name.build());
		return rootNode;
	}
}
