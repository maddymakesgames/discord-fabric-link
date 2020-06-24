package io.maddymakesgames.discordlink.DiscordBot.Util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.BrigadierUtils.DiscordCommandSource;
import io.maddymakesgames.discordlink.DiscordBot.Util.CommandReturn;
import io.maddymakesgames.discordlink.DiscordLink;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class CommandHelper {

	public static String nullname = "THIS IS NOT A REAL PLAYER PLEASE DO NOT NAME YOURSELF THIS";

	public static int handleResponse(CommandReturn cmdReturn, ServerCommandSource source) throws CommandSyntaxException {
		if("".equals(cmdReturn.message))
			return cmdReturn.returnVal;

		DiscordCommandSource dSource = (DiscordCommandSource) source;
		if(dSource.isDiscord()) {
			Message msg = dSource.getMessage();
			if(cmdReturn.dm && msg.getAuthor().isPresent()) DiscordLink.instance.bot.sendMessage(cmdReturn.message, msg.getAuthor().get().getPrivateChannel().block().getId());
			else DiscordLink.instance.bot.sendMessage(cmdReturn.message, msg.getChannelId());
		}
		else {
			ServerPlayerEntity player = source.getPlayer();
			player.sendMessage(new LiteralText(cmdReturn.message), false);
		}

		return cmdReturn.returnVal;
	}

	public static ServerCommandSource createSource(ServerPlayerEntity player) {
		MinecraftServer server = DiscordLink.instance.server;
		if(player == null)
			player = server.getPlayerManager().createPlayer(new GameProfile(UUID.randomUUID(), nullname));

		return new ServerCommandSource(null, Vec3d.ZERO, Vec2f.ZERO, server.getWorlds().iterator().next(), 1, "stuff", new LiteralText(""), server, player);
	}
}
