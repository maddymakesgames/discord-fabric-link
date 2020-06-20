package io.maddymakesgames.discordlink.DiscordBot.Commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.CommandNode;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.DiscordCommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class CommandHelper {

	public static String nullname = "THIS IS NOT A REAL PLAYER PLEASE DO NOT NAME YOURSELF THIS";

	public static int handleResponse(CommandReturn cmdReturn, ServerPlayerEntity player) {
		player.sendMessage(new LiteralText(cmdReturn.message)/*, false*/);
		return cmdReturn.returnVal;
	}

	public static void handleResponse(CommandReturn cmdReturn, Message msg) {
		if(cmdReturn.dm) msg.getAuthor().get().getPrivateChannel().subscribe(channel -> channel.createMessage(cmdReturn.message).subscribe().dispose()).dispose();
		else msg.getChannel().subscribe(channel -> channel.createMessage(cmdReturn.message).subscribe().dispose()).dispose();
	}

	public static CommandContext<ServerCommandSource> createContext(UUID playerID, User user, String messageContent, CommandNode<ServerCommandSource> command) {
		MinecraftServer server = DiscordLink.instance.server;
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerID);
		if(player == null)
			player = server.getPlayerManager().createPlayer(new GameProfile(UUID.randomUUID(), nullname));

		ServerCommandSource source = new ServerCommandSource(null, Vec3d.ZERO, Vec2f.ZERO, server.getWorlds().iterator().next(), 1, "stuff", new LiteralText(""), server, player);
		((DiscordCommandSource)source).setDiscord(true);
		((DiscordCommandSource)source).setUser(user);
		return new CommandContextBuilder<>(null, source, command, 0).build(messageContent);
	}
}
