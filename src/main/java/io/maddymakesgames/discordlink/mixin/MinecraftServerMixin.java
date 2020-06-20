package io.maddymakesgames.discordlink.mixin;

import io.maddymakesgames.discordlink.DiscordBot.DiscordLinkBot;
import io.maddymakesgames.discordlink.DiscordLink;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "sendMessage", at = @At("RETURN"))
	public void sendMessage(Text message, CallbackInfo cbi) {
		if(DiscordLinkBot.initialized && !message.getString().equalsIgnoreCase("") && message instanceof TranslatableText) DiscordLink.instance.bot.sendMessage(message.getString());
	}
}