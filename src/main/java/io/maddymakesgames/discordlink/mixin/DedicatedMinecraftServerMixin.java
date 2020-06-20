package io.maddymakesgames.discordlink.mixin;

import io.maddymakesgames.discordlink.DiscordBot.DiscordLinkBot;
import io.maddymakesgames.discordlink.DiscordLink;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public abstract class DedicatedMinecraftServerMixin {

	@Inject(method = "setupServer", at = @At("HEAD"))
	public void setupServerHead(CallbackInfoReturnable<Boolean> cbi) {
		if(DiscordLinkBot.initialized) DiscordLink.instance.bot.sendMessage("Server is starting up...");
	}

	@Shadow public abstract DedicatedPlayerManager getPlayerManager();

	@Inject(method = "setupServer", at = @At("TAIL"))
	public void setupServerTail(CallbackInfoReturnable<Boolean> cbi) {
		DiscordLink.instance.server = getPlayerManager().getServer();

		LogManager.getLogger("discord-link").info(getPlayerManager().getPlayerList());


		if(DiscordLinkBot.initialized) DiscordLink.instance.bot.sendMessage("Server is running!");
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	public void shutdownHead(CallbackInfo cbi) {
		if(DiscordLinkBot.initialized) DiscordLink.instance.bot.sendMessage("Server is closing...");
	}

	@Inject(method = "shutdown", at = @At("TAIL"))
	public void shutdownTail(CallbackInfo cbi) {
		if(DiscordLinkBot.initialized) {
			DiscordLink.instance.bot.sendMessage("Server has closed.");
			DiscordLink.instance.bot.close();
		}
	}
}
