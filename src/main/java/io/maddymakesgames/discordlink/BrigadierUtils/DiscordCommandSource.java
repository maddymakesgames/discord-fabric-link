package io.maddymakesgames.discordlink.BrigadierUtils;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public interface DiscordCommandSource {
	boolean isDiscord();
	void setDiscord(boolean isDiscord);
	User getUser();
	void setUser(User user);
	Message getMessage();
	void setMessage(Message msg);
}
