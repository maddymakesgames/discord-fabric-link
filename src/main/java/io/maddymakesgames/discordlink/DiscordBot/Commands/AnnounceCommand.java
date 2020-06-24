package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.brigadier.CommandDispatcher;
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
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandHelper.handleResponse;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn.failure;
import static io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn.success;

public class AnnounceCommand implements DiscordCommand {

	CommandNode<ServerCommandSource> root = LiteralArgumentBuilder.<ServerCommandSource>literal("announce")
			.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("message", greedyString())
					.executes(ctx -> handleResponse(execute(ctx), ctx.getSource())))
			.build();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(root);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Permission getPermission() {
		return null;
	}

	@Override
	public CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerCommandSource source = ctx.getSource();
		if(!(((DiscordCommandSource)source).isDiscord() ? Permission.Owner.isAllowed(((DiscordCommandSource)source).getUser()) : Permission.Owner.isAllowed(source.getPlayer())))
			return failure("");

		DiscordLink.instance.server.getPlayerManager().sendToAll(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText(ctx.getArgument("message", String.class))));
		return success("");
	}
}
