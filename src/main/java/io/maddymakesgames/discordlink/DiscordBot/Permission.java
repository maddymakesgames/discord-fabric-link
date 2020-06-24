package io.maddymakesgames.discordlink.DiscordBot;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.Util.Linkable;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public enum Permission {
	Everyone,
	Unregistered,
	Registered,
	Owner;

	public boolean isAllowed(User user) {
		switch(this) {
			case Everyone:
				return true;
			case Owner:
				return user.getId().equals(Snowflake.of("206102420177027072"));
			case Unregistered:
				return !((LinkableUser)user).isLinked();
			case Registered:
				return ((LinkableUser)user).isLinked();
			default:
				return false;
		}
	}

	public boolean isAllowed(ServerPlayerEntity player) {
		switch (this) {
			case Everyone:
				return true;
			case Owner:
				return ((LinkablePlayer)player).isLinked() && ((LinkablePlayer)player).getLink().asString().equals(DiscordLink.instance.config.ownerID);
			case Unregistered:
				return !((LinkablePlayer) player).isLinked();
			case Registered:
				return ((LinkablePlayer) player).isLinked();
			default:
				return false;
		}
	}
}