package com.timedtiles;

/**
 * One tile marker. Immutable. Plain ints and longs, so it needs no game client: {@code owner} is 0 for the player's
 * own marker and a party member's id otherwise, {@code who} is null for the player's own, and {@code rgb} is 24-bit.
 */
public final class Marker
{
	private final long owner;
	private final String who;
	private final int x;
	private final int y;
	private final int plane;
	private final int number;
	private final int rgb;
	private final long createdMs;
	private final long lifeMs;

	public Marker(long owner, String who, int x, int y, int plane, int number, int rgb, long createdMs, long lifeMs)
	{
		this.owner = owner;
		this.who = who;
		this.x = x;
		this.y = y;
		this.plane = plane;
		this.number = number;
		this.rgb = rgb;
		this.createdMs = createdMs;
		this.lifeMs = lifeMs;
	}

	public long getOwner()
	{
		return owner;
	}

	public String getWho()
	{
		return who;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getPlane()
	{
		return plane;
	}

	public int getNumber()
	{
		return number;
	}

	public int getRgb()
	{
		return rgb;
	}

	public long getCreatedMs()
	{
		return createdMs;
	}

	public long getLifeMs()
	{
		return lifeMs;
	}

	/** The moment this marker is gone: created plus life. */
	public long expiresAtMs()
	{
		return createdMs + lifeMs;
	}

	boolean sameTile(int x, int y, int plane)
	{
		return this.x == x && this.y == y && this.plane == plane;
	}
}
