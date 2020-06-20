package io.maddymakesgames.discordlink.mixin;

import discord4j.core.object.entity.Member;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(value = Member.class, remap = false)
public class DiscordMemberMixin implements LinkableUser {
	@Unique
	private UUID linkedID;

	@Unique
	@Override
	public boolean isLinked() {
		return linkedID != null;
	}

	@Unique
	@Override
	public void link(UUID linkedID) {
		this.linkedID = linkedID;
	}

	@Unique
	@Override
	public UUID getLink() {
		return linkedID;
	}
}
