package com.timedtiles;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps each party member's markers in a colour you can tell apart: the colour they chose, unless it is too close to
 * yours or to another member's, in which case a stand-in that is clear of them all (the one they already had, if it
 * still is, otherwise the first free palette colour). It is decided again at every marker, so a member who changes
 * their colour is shown in it as soon as it no longer clashes. Plain ints, no game client; client thread only.
 */
public final class PartyColours
{
	/** Colours closer than this (straight-line distance in RGB) look the same on screen. */
	private static final int TOO_CLOSE = 30;

	private static final int[] PALETTE = {
		0x00C8FF, // cyan
		0xFF40C0, // pink
		0x40FF40, // green
		0xA064FF, // violet
		0xFF6030, // red-orange
		0xFFFFFF, // white
	};

	private final Map<Long, Integer> assigned = new HashMap<>();

	/** The colour to draw this member's markers in. */
	public int colourFor(long member, int sentRgb, int myRgb)
	{
		final int sent = sentRgb & 0xFFFFFF;
		int chosen = sent;
		if (clashes(sent, member, myRgb))
		{
			final Integer known = assigned.get(member);
			if (known != null && !clashes(known, member, myRgb))
			{
				chosen = known;
			}
			else
			{
				for (int candidate : PALETTE)
				{
					if (!clashes(candidate, member, myRgb))
					{
						chosen = candidate;
						break;
					}
				}
			}
		}
		assigned.put(member, chosen);
		return chosen;
	}

	public void forget(long member)
	{
		assigned.remove(member);
	}

	public void clear()
	{
		assigned.clear();
	}

	private boolean clashes(int rgb, long member, int myRgb)
	{
		if (close(rgb, myRgb))
		{
			return true;
		}
		for (Map.Entry<Long, Integer> other : assigned.entrySet())
		{
			if (other.getKey() != member && close(rgb, other.getValue()))
			{
				return true;
			}
		}
		return false;
	}

	static boolean close(int a, int b)
	{
		final int dr = ((a >> 16) & 0xFF) - ((b >> 16) & 0xFF);
		final int dg = ((a >> 8) & 0xFF) - ((b >> 8) & 0xFF);
		final int db = (a & 0xFF) - (b & 0xFF);
		return dr * dr + dg * dg + db * db < TOO_CLOSE * TOO_CLOSE;
	}
}
