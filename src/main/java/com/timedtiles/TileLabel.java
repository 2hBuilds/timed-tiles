package com.timedtiles;

/**
 * What a marker's tile shows: its number, the seconds it has left, or nothing. The settings menu lists the choices by
 * {@link #toString()}; RuneLite stores the constant's name.
 */
public enum TileLabel
{
	NUMBER("Number"),
	TIMER("Timer"),
	NOTHING("Nothing");

	private final String text;

	TileLabel(String text)
	{
		this.text = text;
	}

	@Override
	public String toString()
	{
		return text;
	}
}
