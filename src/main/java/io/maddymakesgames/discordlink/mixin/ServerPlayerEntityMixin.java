package io.maddymakesgames.discordlink.mixin;

import discord4j.core.object.util.Snowflake;
import io.maddymakesgames.discordlink.Util.LinkablePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements LinkablePlayer {
	@Unique
	private Snowflake linkedAccount;

	@Unique
	@Override
	public boolean isLinked() {
		return linkedAccount != null;
	}

	@Unique
	@Override
	public void link(Snowflake linkedID) {
		linkedAccount = linkedID;
	}

	@Unique
	@Override
	public Snowflake getLink() {
		return linkedAccount;
	}

	@Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		if(linkedAccount != null) tag.putString("dl-snow", linkedAccount.asString());
	}

	@Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
	public void readCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		if(tag.contains("dl-snow")) linkedAccount = Snowflake.of(tag.getString("dl-snow"));
	}
}
