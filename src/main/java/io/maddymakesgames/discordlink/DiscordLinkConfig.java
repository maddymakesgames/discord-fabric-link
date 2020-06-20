package io.maddymakesgames.discordlink;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "discord-link")
public class DiscordLinkConfig implements ConfigData {

	@Comment("Token for discord api")
	public String token = "msf-safetoken-145780";

	@Comment("Channels to send the messages in (needs to be from servers the bot is in)")
	public String[] channelIds = new String[0];

	@Comment("The discord id owner of the server")
	public String ownerID = "your-discord-user-id";
}
