package io.maddymakesgames.discordlink.DiscordBot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.BrigadierUtils.DiscordCommandDispatcher;
import io.maddymakesgames.discordlink.DiscordBot.Util.DiscordCommand;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordLinkBot {
	private final DiscordClient client;
	private final List<TextChannel> activeChannels = new ArrayList<>();
	private final HashMap<String, DiscordCommand> commands = new HashMap<>();
	private final DiscordCommandDispatcher dispatcher;

	public final HashMap<String, ServerPlayerEntity> registeringPlayersCache = new HashMap<>();
	public final String prefix;

	public static boolean initialized = false;
	public static boolean listening = false;
	public static String name;

	public DiscordLinkBot(String token, String[] channels, String prefix) {
		client = DiscordClient.create(token);
		this.prefix = prefix;

		dispatcher = new DiscordCommandDispatcher(prefix);

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
		cmd.register(dispatcher);
	}

	public DiscordCommand getCommand(String str) {
		return commands.get(str.split(" ")[0]);
	}

	public void sendMessage(String message) {
		LogManager.getLogger("discord-link").info(message);
		for(TextChannel channel : activeChannels) channel.createMessage(message).subscribe().dispose();
	}

	public void sendMessage(String message, Snowflake channelID) {
		Optional<TextChannel> channel = activeChannels.stream().filter(c -> c.getId().equals(channelID)).findFirst();
		if(channel.isPresent())
			channel.get().createMessage(message).subscribe().dispose();
		else
			client.getChannelById(channelID).subscribe(c -> {
				if(c.getType() != Channel.Type.GUILD_CATEGORY && c.getType() != Channel.Type.GUILD_VOICE)
					((TextChannel)c).createMessage(message).subscribe().dispose();
			});
	}

	public void close() {
		client.logout();
		initialized = false;
	}

	public long ping() {
		return client.getResponseTime();
	}

	public User getUser(String username) {
		List<User> users = this.client.getUsers().collectList().block();
		users = users.stream().filter(u -> u.getUsername().equals(username) || (u.getUsername() + "#" + u.getDiscriminator()).equals(username)  || String.format("<@%s>", u.getId()).equals(username)).collect(Collectors.toList());
		if(users.size() > 0)
			return users.get(0);
		return null;
	}

	public User getUser(Snowflake id) {
		return client.getUserById(id).block();
	}

	private boolean isListening(Snowflake channelID) {
		for(TextChannel channel : activeChannels) {
			if(channel.getId().equals(channelID)) return true;
		}
		return false;
	}

	private void onMessage(MessageCreateEvent event) {
		Message msg = event.getMessage();
		if (!event.getMember().isPresent() || event.getMember().get().isBot() || !isListening(msg.getChannelId()) || listening) return;
		int executeReturn = -1;

		try {
			executeReturn = dispatcher.execute(msg.getContent().get(), msg);
		} catch (Exception e) {}
		LogManager.getLogger("discord-link").info(String.format("%s %s", executeReturn, msg.getContent().get()));
		if(executeReturn == -2) {
			DiscordLink.instance.server.getPlayerManager().sendToAll(new GameMessageS2CPacket(new LiteralText(msg.getContent().orElse("§oEmpty Message")),
					MessageType.CHAT,
					((LinkableUser)msg.getAuthor().get()).isLinked() ? ((LinkableUser)msg.getAuthor().get()).getLink().getUuid() : UUID.randomUUID()));
		}

	}
}
