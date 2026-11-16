package com.timedtiles;

/** When the player's markers are sent to the party. */
public final class ShareRule
{
	private ShareRule()
	{
	}

	/** True only when sharing is on, the player is in a party, not in the Wilderness and not on a PvP world. */
	public static boolean canShare(boolean shareOn, boolean inParty, boolean inWilderness, boolean pvpWorld)
	{
		return shareOn && inParty && !inWilderness && !pvpWorld;
	}
}
