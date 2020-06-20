package io.maddymakesgames.discordlink.Util;

public interface Linkable<T> {
	boolean isLinked();
	void link(T linkedID);
	T getLink();
}
