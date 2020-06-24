package io.maddymakesgames.discordlink.BrigadierUtils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.DiscordBot.Util.CommandHelper;
import io.maddymakesgames.discordlink.DiscordLink;
import net.minecraft.server.command.ServerCommandSource;

public class DiscordCommandDispatcher extends CommandDispatcher<ServerCommandSource> {
	public final String prefix;

	public DiscordCommandDispatcher(String prefix) {
		this.prefix = prefix;
	}

	public int execute(String input, Message msg) {
		User user = msg.getAuthor().get();
		ServerCommandSource source = CommandHelper.createSource(DiscordLink.instance.bot.registeredPlayers.get(user.getId()));

		((DiscordCommandSource) source).setDiscord(true);
		((DiscordCommandSource) source).setMessage(msg);
		((DiscordCommandSource) source).setUser(user);

		StringReader reader = new StringReader(input);
		StringReader reader1 = new StringReader(input);

		if(reader.canRead()) {
			for(char c : prefix.toCharArray()) {
				if(reader.peek() == c) {
					reader.skip();
					reader1.skip();
				}
				else
					return -2;
			}
		}

		System.out.println(reader1.getRemaining());

		try {
			return super.execute(reader, source);
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
	}
}
