package com.timedtiles;

/** When a left click places a marker instead of walking. */
public final class ClickRule
{
	private ClickRule()
	{
	}

	/** True only when the key is held, no menu is open, "Walk here" is in the menu and a tile was found. */
	public static boolean shouldPlace(boolean keyHeld, boolean menuOpen, boolean hasWalkHere, boolean tileFound)
	{
		return keyHeld && !menuOpen && hasWalkHere && tileFound;
	}
}
