package io.maddymakesgames.discordlink.mixin;

import discord4j.core.object.entity.User;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = User.class, remap = false)
public class DiscordUserMixin implements LinkableUser {

	@Inject(method = "getUsername", at = @At("RETURN"), cancellable = true)
	private void getUsername(CallbackInfoReturnable<String> cir) {
		System.out.println("User Mixin Works");
		cir.setReturnValue("Test Mixin");
	}
	
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
