package com.timedtiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.inject.Singleton;

/**
 * Every marker on screen: the player's own and the party's. Used only on the client thread, so it has no locks.
 */
@Singleton
public final class MarkerBook
{
	/** Party message types, as {@link TimedTilesMessage} carries them. */
	public static final int TYPE_ADD = 0;
	public static final int TYPE_REMOVE = 1;
	public static final int TYPE_CLEAR = 2;

	/** The most markers one owner has at once; one more removes the oldest. */
	public static final int MAX_PER_OWNER = 20;

	/** The longest life a party member can give a marker of theirs, in seconds. */
	private static final int MAX_SECONDS = 120;

	/** The player's markers, oldest first. */
	private final List<Marker> mine = new ArrayList<>();

	/** Party markers, oldest first. */
	private final List<Marker> party = new ArrayList<>();

	private int counter;

	/** The sample marker shown after a look setting changes, or null. Never shared, never numbered with the player's. */
	private Marker preview;

	/** Shows a sample marker numbered 1 on a tile, in place of any earlier one, so the player sees a new look at once. */
	public void showPreview(int x, int y, int plane, int rgb, long nowMs, long lifeMs)
	{
		preview = new Marker(0, null, x, y, plane, 1, rgb & 0xFFFFFF, nowMs, lifeMs);
	}

	/**
	 * The player clicked a tile: remove their marker there, or add one with the next number.
	 */
	public Change placeMine(int x, int y, int plane, int rgb, long nowMs, long lifeMs)
	{
		expire(nowMs);
		for (Iterator<Marker> it = mine.iterator(); it.hasNext(); )
		{
			final Marker m = it.next();
			if (m.sameTile(x, y, plane))
			{
				it.remove();
				return new Change(Change.Type.REMOVE, m);
			}
		}
		if (mine.isEmpty())
		{
			counter = 0;
		}
		counter++;
		final Marker marker = new Marker(0, null, x, y, plane, counter, rgb & 0xFFFFFF, nowMs, lifeMs);
		mine.add(marker);
		if (mine.size() > MAX_PER_OWNER)
		{
			mine.remove(0);
		}
		return new Change(Change.Type.ADD, marker);
	}

	/** Removes all the player's markers; true if there were any. */
	public boolean clearMine()
	{
		final boolean any = !mine.isEmpty();
		mine.clear();
		preview = null;
		return any;
	}

	/**
	 * A party member's message. Never touches the player's own markers.
	 */
	public void applyParty(long member, String who, int type, int x, int y, int plane, int number, int rgb,
		int seconds, long nowMs)
	{
		expire(nowMs);
		if (who != null)
		{
			nameMember(member, who);
		}
		if (type == TYPE_CLEAR)
		{
			dropMember(member);
			return;
		}
		party.removeIf(m -> m.getOwner() == member && m.sameTile(x, y, plane));
		if (type != TYPE_ADD)
		{
			return;
		}
		final long lifeMs = Math.max(1, Math.min(MAX_SECONDS, seconds)) * 1000L;
		party.add(new Marker(member, who, x, y, plane, number, rgb & 0xFFFFFF, nowMs, lifeMs));
		int count = 0;
		for (Marker m : party)
		{
			if (m.getOwner() == member)
			{
				count++;
			}
		}
		for (Iterator<Marker> it = party.iterator(); it.hasNext() && count > MAX_PER_OWNER; )
		{
			if (it.next().getOwner() == member)
			{
				it.remove();
				count--;
			}
		}
	}

	/** Gives a member's markers that have no name yet (the sender was not in the party list at the time) theirs. */
	private void nameMember(long member, String who)
	{
		for (ListIterator<Marker> it = party.listIterator(); it.hasNext(); )
		{
			final Marker m = it.next();
			if (m.getOwner() == member && m.getWho() == null)
			{
				it.set(new Marker(member, who, m.getX(), m.getY(), m.getPlane(), m.getNumber(), m.getRgb(),
					m.getCreatedMs(), m.getLifeMs()));
			}
		}
	}

	/** Removes one party member's markers. */
	public void dropMember(long member)
	{
		party.removeIf(m -> m.getOwner() == member);
	}

	/** Removes every party marker, keeps the player's. */
	public void dropParty()
	{
		party.clear();
	}

	/** Removes everything. */
	public void dropAll()
	{
		mine.clear();
		party.clear();
		preview = null;
		counter = 0;
	}

	/** Removes every marker whose time has come. */
	public void expire(long nowMs)
	{
		mine.removeIf(m -> nowMs >= m.expiresAtMs());
		party.removeIf(m -> nowMs >= m.expiresAtMs());
		if (preview != null && nowMs >= preview.expiresAtMs())
		{
			preview = null;
		}
	}

	/**
	 * The unexpired markers on a plane: the player's by number, then the party's by creation time, then the sample
	 * marker.
	 */
	public List<Marker> visible(int plane, long nowMs)
	{
		final List<Marker> own = new ArrayList<>();
		for (Marker m : mine)
		{
			if (m.getPlane() == plane && nowMs < m.expiresAtMs())
			{
				own.add(m);
			}
		}
		own.sort(Comparator.comparingInt(Marker::getNumber));
		final List<Marker> others = new ArrayList<>();
		for (Marker m : party)
		{
			if (m.getPlane() == plane && nowMs < m.expiresAtMs())
			{
				others.add(m);
			}
		}
		others.sort(Comparator.comparingLong(Marker::getCreatedMs));
		own.addAll(others);
		if (preview != null && preview.getPlane() == plane && nowMs < preview.expiresAtMs())
		{
			own.add(preview);
		}
		return own;
	}

	/**
	 * 1 until the fade starts, then straight down to 0 at expiry; the fade is the last {@code fadeMs} (all of a shorter
	 * life). A fade of 0 stays solid until the marker is gone.
	 */
	public static float alpha(Marker m, long nowMs, long fadeMs)
	{
		if (fadeMs <= 0)
		{
			return nowMs < m.expiresAtMs() ? 1f : 0f;
		}
		final long fade = Math.min(fadeMs, m.getLifeMs());
		if (fade <= 0)
		{
			return 0f;
		}
		final float a = (m.expiresAtMs() - nowMs) / (float) fade;
		return Math.max(0f, Math.min(1f, a));
	}

	/** For each marker, how many earlier ones in the list share its tile. */
	public static int[] stackIndex(List<Marker> visible)
	{
		final int[] stack = new int[visible.size()];
		for (int i = 0; i < stack.length; i++)
		{
			final Marker m = visible.get(i);
			for (int j = 0; j < i; j++)
			{
				if (visible.get(j).sameTile(m.getX(), m.getY(), m.getPlane()))
				{
					stack[i]++;
				}
			}
		}
		return stack;
	}
}
