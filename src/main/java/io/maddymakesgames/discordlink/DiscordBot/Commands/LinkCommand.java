package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.DiscordBot.DiscordLinkBot;
import io.maddymakesgames.discordlink.DiscordBot.Permission;
import io.maddymakesgames.discordlink.DiscordBot.Util.CommandHelper;
import io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn;
import io.maddymakesgames.discordlink.DiscordBot.Util.DiscordCommand;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.BrigadierUtils.DiscordCommandSource;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class LinkCommand implements DiscordCommand {

	CommandNode<ServerCommandSource> root = CommandManager.literal("link")
														.executes(ctx -> CommandHelper.handleResponse(mcExecute(ctx), ctx.getSource())).build();

	CommandNode<ServerCommandSource> name = CommandManager.argument("playername", StringArgumentType.greedyString())
																			.requires(source -> ((DiscordCommandSource)source).isDiscord())
																			.executes(ctx -> CommandHelper.handleResponse(execute(ctx), ctx.getSource()))
																			.build();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(root);
		root.addChild(name);
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
	public CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		if(DiscordLink.instance.bot.registeredPlayers.containsKey(((DiscordCommandSource)ctx.getSource()).getUser().getId()))
			return new CommandReturn(String.format("You're account has been linked to %s", DiscordLink.instance.bot.registeredPlayers.get(((DiscordCommandSource)ctx.getSource()).getUser().getId()).getDisplayName().asString()), 0, false);
		String playerName = ctx.getArgument("playername", String.class);
		if(playerName == null)
			return new CommandReturn("Please input a account name", 0, false);

		ServerPlayerEntity player = DiscordLink.instance.bot.registeringPlayersCache.remove(playerName);

		if(player == null)
			return new CommandReturn("That account has not run /link", 0, false);

		User user = ((DiscordCommandSource)ctx.getSource()).getUser();

		((LinkablePlayer)player).link(user.getId());
		DiscordLink.instance.bot.linkAccount(player);

		return new CommandReturn("Successfully linked your account", 1, false);
	}

	public CommandReturn mcExecute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity caller = ctx.getSource().getPlayer();
		DiscordLink.instance.bot.registeringPlayersCache.put(caller.getDisplayName().asString(), caller);
		if(((LinkablePlayer)caller).isLinked())
			return new CommandReturn("You're account has already been linked", 0, false);
		return new CommandReturn(String.format("Please send %slink %s to %s", DiscordLink.instance.bot.prefix, ctx.getSource().getPlayer().getDisplayName().asString(), DiscordLinkBot.name), 1, false);
	}
}
