package io.maddymakesgames.discordlink.DiscordBot.Util;

public class CommandReturn {
	public String message;
	public int returnVal;
	public boolean dm;

	public CommandReturn(String msg, int val, boolean dm) {
		message = msg;
		returnVal = val;
		this.dm = dm;
	}

	public static CommandReturn failure(String message) {
		return new CommandReturn(message, 0, false);
	}

	public static CommandReturn success(String message) {
		return new CommandReturn(message, 1, false);
	}
}
