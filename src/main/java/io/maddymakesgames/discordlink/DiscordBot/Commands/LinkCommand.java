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
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandHelper.*;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn.failure;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn.success;

public class LinkCommand implements DiscordCommand {

	CommandNode<ServerCommandSource> root = CommandManager.literal("link")
														.executes(ctx -> handleResponse(mcExecute(ctx), ctx.getSource())).build();

	CommandNode<ServerCommandSource> name = CommandManager.argument("playername", StringArgumentType.greedyString())
																			.requires(source -> ((DiscordCommandSource)source).isDiscord())
																			.executes(ctx -> handleResponse(execute(ctx), ctx.getSource()))
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
	public CommandReturn execute(CommandContext<ServerCommandSource> ctx) {
		if(((LinkableUser)((DiscordCommandSource)ctx.getSource()).getUser()).isLinked())
			return failure(String.format("You're account has been linked to %s", ((LinkableUser)((DiscordCommandSource)ctx.getSource()).getUser()).getLink().getDisplayName().asString()));
		String playerName = ctx.getArgument("playername", String.class);
		if(playerName == null)
			return failure("Please input a account name");

		ServerPlayerEntity player = DiscordLink.instance.bot.registeringPlayersCache.remove(playerName);

		if(player == null)
			return failure("That account has not run /link");

		User user = ((DiscordCommandSource)ctx.getSource()).getUser();

		((LinkablePlayer)player).link(user.getId());
		((LinkableUser)user).link(player);

		return success("Successfully linked your account");
	}

	public CommandReturn mcExecute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity caller = ctx.getSource().getPlayer();
		DiscordLink.instance.bot.registeringPlayersCache.put(caller.getDisplayName().asString(), caller);
		if(((LinkablePlayer)caller).isLinked())
			return failure("You're account has already been linked");
		return success(String.format("Please send %slink %s to %s", DiscordLink.instance.bot.prefix, ctx.getSource().getPlayer().getDisplayName().asString(), DiscordLinkBot.name));
	}
}
