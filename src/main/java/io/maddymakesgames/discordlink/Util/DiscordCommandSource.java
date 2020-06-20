package io.maddymakesgames.discordlink.Util;

import discord4j.core.object.entity.User;

public interface DiscordCommandSource {
	boolean isDiscord();
	void setDiscord(boolean isDiscord);
	User getUser();
	void setUser(User user);
}
