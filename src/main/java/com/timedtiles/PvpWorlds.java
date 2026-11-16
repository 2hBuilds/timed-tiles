package com.timedtiles;

import java.util.Collection;
import net.runelite.api.WorldType;

/** Which worlds count as PvP for sharing. */
public final class PvpWorlds
{
	private PvpWorlds()
	{
	}

	public static boolean isPvp(Collection<WorldType> types)
	{
		return WorldType.isPvpWorld(types)
			|| types.contains(WorldType.DEADMAN)
			|| types.contains(WorldType.PVP_ARENA)
			|| types.contains(WorldType.LAST_MAN_STANDING)
			|| types.contains(WorldType.BOUNTY)
			|| types.contains(WorldType.HIGH_RISK);
	}
}
