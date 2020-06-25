// Currently this mixin just doesn't apply and I don't know why
// Going to make this work after 1.16 is out
// Guessing this has to do with me mixing into a non-minecraft/fabric class

package io.maddymakesgames.discordlink.mixin;

import discord4j.core.ServiceMediator;
import discord4j.core.object.data.stored.UserBean;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.LinkableUser;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = User.class, remap = false)
public abstract class DiscordUserMixin implements LinkableUser {

	@Shadow public abstract Snowflake getId();

	@Inject(method="<init>", at = @At("RETURN"))
	public void constructor(ServiceMediator serviceMediator, UserBean data, CallbackInfo ci) {
		DiscordLink.instance.bot.getLink(getId());
		System.out.println("Creating a new user");
	}
	
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
		System.out.println("Registering link with " + linkedID.getDisplayName().asString());
		DiscordLink.instance.bot.registerLink(getId(), linkedPlayer);
	}

	@Unique
	@Override
	public ServerPlayerEntity getLink() {
		if(linkedPlayer == null) link(DiscordLink.instance.bot.getLink(getId()));
		return linkedPlayer;
	}
}
