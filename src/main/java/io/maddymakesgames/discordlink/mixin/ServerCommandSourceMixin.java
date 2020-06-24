package io.maddymakesgames.discordlink.mixin;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.BrigadierUtils.DiscordCommandSource;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements DiscordCommandSource {
	@Unique
	private boolean isDiscord = false;

	@Unique
	private User user;

	@Unique
	private Message msg;

	@Override
	@Unique
	public boolean isDiscord() {
		return isDiscord;
	}

	@Override
	@Unique
	public void setDiscord(boolean isDiscord) {
		this.isDiscord = isDiscord;
	}

	@Override
	@Unique
	public User getUser() {
		return user;
	}

	@Override
	@Unique
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	@Unique
	public Message getMessage() {
		return msg;
	}

	@Override
	@Unique
	public void setMessage(Message msg) {
		this.msg = msg;
	}
}
