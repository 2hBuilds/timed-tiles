package com.timedtiles;

/**
 * What a marker's tile shows: its number or the seconds it has left, each alone or with a clock showing the share of
 * its time left, a clock alone, or nothing. One setting, ordered by what is written, so the menu reads in groups.
 * The settings menu lists the choices by {@link #toString()}, a picture and the name. RuneLite stores the constant's
 * name, so NUMBER, TIMER and NOTHING keep the names they had in 1.0.1 and a player's choice carries over.
 */
public enum TileLabel
{
	NUMBER("Number", Words.NUMBER, ClockStyle.OFF),
	NUMBER_PIE("Number + pie", Words.NUMBER, ClockStyle.PIE),
	NUMBER_RING("Number + ring", Words.NUMBER, ClockStyle.RING),
	NUMBER_BORDER("Number + border", Words.NUMBER, ClockStyle.BORDER),
	NUMBER_SWEEP("Number + sweep", Words.NUMBER, ClockStyle.SWEEP),
	NUMBER_CORNER("Number + corner", Words.NUMBER, ClockStyle.CORNER),
	TIMER("Timer", Words.TIMER, ClockStyle.OFF),
	TIMER_RING("Timer + ring", Words.TIMER, ClockStyle.RING),
	TIMER_BORDER("Timer + border", Words.TIMER, ClockStyle.BORDER),
	TIMER_SWEEP("Timer + sweep", Words.TIMER, ClockStyle.SWEEP),
	TIMER_CORNER("Timer + corner", Words.TIMER, ClockStyle.CORNER),
	NOTHING("Nothing", Words.NONE, ClockStyle.OFF),
	PIE("Pie", Words.NONE, ClockStyle.PIE),
	RING("Ring", Words.NONE, ClockStyle.RING),
	BORDER("Border", Words.NONE, ClockStyle.BORDER),
	SWEEP("Sweep", Words.NONE, ClockStyle.SWEEP);

	/** What is written on the tile. */
	public enum Words
	{
		NUMBER,
		TIMER,
		NONE
	}

	private final String menu;
	private final Words words;
	private final ClockStyle clock;

	TileLabel(String text, Words words, ClockStyle clock)
	{
		this.menu = MenuText.with("tile-" + name().toLowerCase(java.util.Locale.ROOT).replace('_', '-'), text);
		this.words = words;
		this.clock = clock;
	}

	public Words words()
	{
		return words;
	}

	public ClockStyle clock()
	{
		return clock;
	}

	@Override
	public String toString()
	{
		return menu;
	}
}
