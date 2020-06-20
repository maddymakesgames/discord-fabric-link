package io.maddymakesgames.discordlink.DiscordBot.Commands;

public class CommandReturn {
	public String message;
	public int returnVal;
	public boolean dm;

	public CommandReturn(String msg, int val, boolean dm) {
		message = msg;
		returnVal = val;
		this.dm = dm;
	}
}
