package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.maddymakesgames.discordlink.BrigadierUtils.DiscordCommandSource;
import io.maddymakesgames.discordlink.DiscordBot.Permission;
import io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn;
import io.maddymakesgames.discordlink.DiscordBot.Util.DiscordCommand;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandHelper.handleResponse;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn.failure;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn.success;
import static io.maddymakesgames.discordlink.DiscordLink.instance;

public class PlaytimeCommand implements DiscordCommand {

	CommandNode<ServerCommandSource> root = LiteralArgumentBuilder.<ServerCommandSource>literal("playtime").executes(ctx -> handleResponse(execute(ctx), ctx.getSource())).build();
	CommandNode<ServerCommandSource> playername = RequiredArgumentBuilder.<ServerCommandSource, String>argument("playername", StringArgumentType.greedyString()).executes(ctx -> handleResponse(executeArg(ctx), ctx.getSource())).build();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(root);
		root.addChild(playername);
	}

	@Override
	public String getName() {
		return "playtime";
	}

	@Override
	public Permission getPermission() {
		return Permission.Registered;
	}

	@Override
	public CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = ((DiscordCommandSource)ctx.getSource()).isDiscord() ? ((LinkableUser)((DiscordCommandSource)ctx.getSource()).getUser()).getLink() : ctx.getSource().getPlayer();

		if(player == null)
			return failure("You must be linked to a minecraft account to use this command");


		return success(String.format("You have played for %s",formatTime(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE)))));
	}

	public CommandReturn executeArg(CommandContext<ServerCommandSource> ctx) {
		String playerName = ctx.getArgument("playername", String.class);

		ServerPlayerEntity player = DiscordLink.instance.server.getPlayerManager().getPlayer(playerName);
		if(player == null)
			player = ((LinkableUser)instance.bot.getUser(playerName)).getLink();
		if(player == null)
			return failure(String.format("No player with the name %s was found", playerName));
		return success(String.format("%s has played for %s", player.getDisplayName().asString(), formatTime(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE)))));
	}

	private String formatTime(int time) {
		time = time / 20;
		int days = time / 86400;
		int hours = time / 3600 % 24;
		int minutes = time / 60 % 60;
		int seconds = time % 60;
		String output = "";
		if(days != 0)
			output += String.format("%sD:", days);
		if(hours != 0 || days != 0)
			output += String.format("%sH:", hours);

		output += String.format("%sM:%sS", minutes, seconds);

		return output;
	}
}
