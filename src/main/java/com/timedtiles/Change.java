package com.timedtiles;

/** What {@link MarkerBook#placeMine} did: a marker was added or removed. */
public final class Change
{
	/** The two things a click can do. */
	public enum Type
	{
		ADD,
		REMOVE
	}

	private final Type type;
	private final Marker marker;

	public Change(Type type, Marker marker)
	{
		this.type = type;
		this.marker = marker;
	}

	public Type getType()
	{
		return type;
	}

	public Marker getMarker()
	{
		return marker;
	}
}
