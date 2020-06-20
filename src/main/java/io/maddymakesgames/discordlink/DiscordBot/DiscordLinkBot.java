package io.maddymakesgames.discordlink.DiscordBot;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.DiscordBot.Commands.CommandHelper;
import io.maddymakesgames.discordlink.DiscordBot.Commands.DiscordCommand;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordLinkBot {
	private final DiscordClient client;
	private final List<TextChannel> activeChannels = new ArrayList<>();
	private final HashMap<String, DiscordCommand> commands = new HashMap<>();
	public final HashMap<String, ServerPlayerEntity> registeringPlayersCache = new HashMap<>();
	public final String prefix;

	public static boolean initialized = false;
	public static String name;

	public DiscordLinkBot(String token, String[] channels, String prefix) {
		client = DiscordClient.create(token);
		this.prefix = prefix;

		client.getEventDispatcher().on(ReadyEvent.class).subscribe(readyEvent -> {
			LogManager.getLogger("discord-link").log(Level.INFO,  String.format("Logged in as %s#%s", readyEvent.getSelf().getUsername(), readyEvent.getSelf().getDiscriminator()));
			name = String.format("%s#%s", readyEvent.getSelf().getUsername(), readyEvent.getSelf().getDiscriminator());
			for(String id : channels) {
				Channel channel = client.getChannelById(Snowflake.of(id)).block();

				if(channel.getType() == Channel.Type.GUILD_TEXT) activeChannels.add((TextChannel) channel);
			}

			initialized = true;
		});

		client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(this::onMessage);

		client.login().subscribe();
	}

	public void register(DiscordCommand cmd) {
		commands.put(cmd.getName(), cmd);
	}

	public DiscordCommand getCommand(String str) {
		return commands.get(str.split(" ")[0]);
	}

	public void sendMessage(String message) {
		LogManager.getLogger("discord-link").info(message);
		for(TextChannel channel : activeChannels) channel.createMessage(message).subscribe().dispose();
	}

	public void close() {
		client.logout();
		initialized = false;
	}

	public long ping() {
		return client.getResponseTime();
	}

	public void linkAccount(ServerPlayerEntity player) {
		if(!((LinkablePlayer)player).isLinked()) return;
		LinkableUser user = (LinkableUser) client.getUserById(((LinkablePlayer)player).getLink());
		user.link(player.getUuid());
	}

	private boolean isListening(Snowflake channelID) {
		for(TextChannel channel : activeChannels) {
			if(channel.getId().equals(channelID)) return true;
		}
		return false;
	}

	private void onMessage(MessageCreateEvent event) {
		Message msg = event.getMessage();
		DiscordCommand cmd = getCommand(msg.getContent().orElse(""));

		if (!event.getMember().isPresent() && event.getMember().get().isBot()) return;
		if (cmd != null	) {

			Member member = event.getMember().get();
			UUID playerID = ((LinkableUser)msg.getAuthor().get()).getLink();

			if (playerID == null && cmd.requireLink()) {
				event.getMessage().getChannel().subscribe(channel -> channel.createMessage(String.format("%s you need to have your minecraft player registered to use that command", member.getDisplayName())));
			}

			try {
				CommandHelper.handleResponse(cmd.execute(CommandHelper.createContext(playerID, msg.getAuthor().get(), msg.getContent().get(), cmd.getNode())), msg);
			} catch (CommandSyntaxException e) {
				LogManager.getLogger("discord-link").error(String.format("Error executing command %s, %s", cmd.getName(), e));
			}

		} else if (cmd != null)
			msg.getChannel().subscribe(channel -> channel.createMessage("You do not have permission to use that command").subscribe().dispose());

		else if(isListening(msg.getChannelId()))
			DiscordLink.instance.server.getPlayerManager().sendToAll(new LiteralText(msg.getContent().orElse("§oEmpty Message")));
	}
}
