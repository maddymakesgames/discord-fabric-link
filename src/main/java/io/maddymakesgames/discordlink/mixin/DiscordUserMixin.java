// Currently this mixin just doesn't apply and I don't know why
// Going to make this work after 1.16 is out
// Guessing this has to do with me mixing into a non-minecraft/fabric class

package io.maddymakesgames.discordlink.mixin;

import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = User.class, remap = false)
public class DiscordUserMixin implements LinkableUser {
	
	@Unique
	private ServerPlayerEntity linkedPlayer;

	@Unique
	@Override
	public boolean isLinked() {
		return linkedPlayer != null;
	}

	@Unique
	@Override
	public void link(ServerPlayerEntity linkedID) {
		this.linkedPlayer = linkedID;
	}

	@Unique
	@Override
	public ServerPlayerEntity getLink() {
		return linkedPlayer;
	}
}
