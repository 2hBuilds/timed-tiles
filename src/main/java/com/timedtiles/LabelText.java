package com.timedtiles;

/**
 * The text drawn on a marker's tile for each {@link TileLabel}. Plain numbers in, a string out, so it needs no game client.
 */
public final class LabelText
{
	/** The most decimals the timer can show. */
	public static final int MAX_DECIMALS = 2;

	private LabelText()
	{
	}

	/**
	 * The text for one marker, or null when nothing is drawn.
	 *
	 * @param mode null is treated as {@link TileLabel#NUMBER}, as a setting RuneLite could not read
	 * @param who the placer's name for a party marker, null for the player's own
	 * @param remainingMs time left on the marker
	 * @param decimals digits after the point for the timer, 0 to {@link #MAX_DECIMALS}
	 */
	public static String of(TileLabel mode, int number, String who, boolean showNames, long remainingMs, int decimals)
	{
		final String main;
		switch (mode == null ? TileLabel.NUMBER : mode)
		{
			case NUMBER:
				main = Integer.toString(number);
				break;
			case TIMER:
				main = seconds(remainingMs, decimals);
				break;
			default:
				return null;
		}
		return who != null && showNames ? main + " " + who : main;
	}

	/**
	 * Seconds left, rounded up at the shown digit so the timer never reads zero while the marker is still there:
	 * 3.84 s is "4", "3.9" or "3.84". Not below zero.
	 */
	public static String seconds(long remainingMs, int decimals)
	{
		final int d = Math.max(0, Math.min(MAX_DECIMALS, decimals));
		final long unitMs = d == 2 ? 10 : d == 1 ? 100 : 1000;
		final long units = Math.max(0, (Math.max(0, remainingMs) + unitMs - 1) / unitMs);
		if (d == 0)
		{
			return Long.toString(units);
		}
		final long scale = d == 2 ? 100 : 10;
		final long fraction = units % scale;
		return (units / scale) + "." + (d == 2 && fraction < 10 ? "0" : "") + fraction;
	}
}
