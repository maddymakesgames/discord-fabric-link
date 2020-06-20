package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.maddymakesgames.discordlink.DiscordBot.DiscordLinkBot;
import io.maddymakesgames.discordlink.DiscordBot.Permission;
import io.maddymakesgames.discordlink.DiscordLink;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;

public class PingCommand implements DiscordCommand {
	LiteralArgumentBuilder<ServerCommandSource> cmd = CommandManager.literal("ping").requires((source) -> {
		boolean hasPlayer;
		try {
			source.getPlayer();
			hasPlayer = true;
		} catch (Exception e) {
			hasPlayer = false;
		}
		return DiscordLinkBot.initialized && hasPlayer;
	}).executes(ctx -> CommandHelper.handleResponse(execute(ctx), ctx.getSource().getPlayer()));;

	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(cmd);
	}

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public Permission getPermission() {
		return Permission.Everyone;
	}

	@Override
	public boolean requireLink() {
		return false;
	}

	public CommandReturn execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = ctx.getSource().getPlayer();
		long discordPing = DiscordLink.instance.bot.ping();
		Float serverTps = 20 - ctx.getSource().getMinecraftServer().getTickTime();

		String output = (player.getEntityName().equals(CommandHelper.nullname) ? "" : "You're server ping is " + player.pingMilliseconds + "\n") + String.format("The server tps is %2.2f\nThe ping to discord is %s", serverTps, discordPing);

		return new CommandReturn(output, 1, false);
	}

	@Override
	public CommandNode<ServerCommandSource> getNode() {
		return cmd.build();
	}
}
