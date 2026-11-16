package com.timedtiles;

import java.util.Objects;
import net.runelite.client.party.messages.PartyMemberMessage;

/**
 * The one message this plugin sends through RuneLite's party server. The party server names a message by its class's
 * simple name, so this name must stay unique ({@code TilePing} is the core's). {@code type}: 0 add, 1 remove, 2 clear.
 */
public class TimedTilesMessage extends PartyMemberMessage
{
	private int type;
	private int x;
	private int y;
	private int plane;
	private int number;
	private int rgb;
	private int seconds;

	/** For Gson. */
	public TimedTilesMessage()
	{
	}

	public TimedTilesMessage(int type, int x, int y, int plane, int number, int rgb, int seconds)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.plane = plane;
		this.number = number;
		this.rgb = rgb;
		this.seconds = seconds;
	}

	public int getType()
	{
		return type;
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

	public int getSeconds()
	{
		return seconds;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof TimedTilesMessage))
		{
			return false;
		}
		final TimedTilesMessage m = (TimedTilesMessage) o;
		return getMemberId() == m.getMemberId() && type == m.type && x == m.x && y == m.y && plane == m.plane
			&& number == m.number && rgb == m.rgb && seconds == m.seconds;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getMemberId(), type, x, y, plane, number, rgb, seconds);
	}
}
