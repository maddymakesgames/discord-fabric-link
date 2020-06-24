package io.maddymakesgames.discordlink;

import io.maddymakesgames.discordlink.DiscordBot.Commands.AnnounceCommand;
import io.maddymakesgames.discordlink.DiscordBot.Commands.LinkCommand;
import io.maddymakesgames.discordlink.DiscordBot.Commands.PingCommand;
import io.maddymakesgames.discordlink.DiscordBot.Commands.PlaytimeCommand;
import io.maddymakesgames.discordlink.DiscordBot.DiscordLinkBot;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;

public class DiscordLink implements ModInitializer {

	public static DiscordLink instance;
	public MinecraftServer server;
	public DiscordLinkBot bot;
	public DiscordLinkConfig config;

	@Override
	public void onInitialize() {
		instance = this;

		LogManager.getLogger("discord-link").info("Loading discord-link...");
		AutoConfig.register(DiscordLinkConfig.class, JanksonConfigSerializer::new);

		config = AutoConfig.getConfigHolder(DiscordLinkConfig.class).getConfig();
		bot = new DiscordLinkBot(config.token, config.channelIds, "?");

		CommandRegistrationCallback.EVENT.register((dispatcher, bool) -> {
			new PingCommand().register(dispatcher);
			new LinkCommand().register(dispatcher);
			new PlaytimeCommand().register(dispatcher);
			new AnnounceCommand().register(dispatcher);
		});

		bot.register(new PingCommand());
		bot.register(new LinkCommand());
		bot.register(new PlaytimeCommand());
		bot.register(new AnnounceCommand());

	}
}