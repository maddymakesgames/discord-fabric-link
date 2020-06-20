package io.maddymakesgames.discordlink.mixin;

import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.Util.Linkable;
import io.maddymakesgames.discordlink.DiscordLink;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo cbi) {
		if(((LinkablePlayer) player).isLinked()) DiscordLink.instance.bot.linkAccount(player);
		else player.sendMessage(new LiteralText("Please link your discord account via /link").formatted(Formatting.DARK_PURPLE)/*, false*/);
	}
}