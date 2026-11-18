package com.timedtiles;

import java.awt.Polygon;
import java.awt.geom.Path2D;

/**
 * The sums behind the clock styles: how much of a marker's time is left, where a tile's middle and its top-right
 * corner are on screen, and the part of its outline that is still to run. Plain numbers and AWT shapes, so it needs
 * no game client.
 */
public final class ClockFace
{
	private ClockFace()
	{
	}

	/** The share of its life a marker has left, 0 to 1. A life of 0 or less counts as none left. */
	public static float timeLeft(long remainingMs, long lifeMs)
	{
		if (lifeMs <= 0)
		{
			return 0f;
		}
		return (float) Math.max(0.0, Math.min(1.0, remainingMs / (double) lifeMs));
	}

	/** The average of the polygon's corners: {x, y}. */
	public static double[] centre(Polygon p)
	{
		double x = 0;
		double y = 0;
		for (int i = 0; i < p.npoints; i++)
		{
			x += p.xpoints[i];
			y += p.ypoints[i];
		}
		return new double[]{x / Math.max(1, p.npoints), y / Math.max(1, p.npoints)};
	}

	/** The index of the corner furthest up and to the right on screen (the largest x minus y). */
	public static int topRight(Polygon p)
	{
		int best = 0;
		for (int i = 1; i < p.npoints; i++)
		{
			if (p.xpoints[i] - p.ypoints[i] > p.xpoints[best] - p.ypoints[best])
			{
				best = i;
			}
		}
		return best;
	}

	/**
	 * The first {@code share} of the polygon's outline, walked from its top-left corner (the smallest x plus y) in
	 * the order of its points. Empty for a share of 0 or less, the whole closed outline for 1 or more.
	 */
	public static Path2D.Double outlinePart(Polygon p, float share)
	{
		final Path2D.Double path = new Path2D.Double();
		final int n = p.npoints;
		if (n < 2 || share <= 0f)
		{
			return path;
		}
		int start = 0;
		for (int i = 1; i < n; i++)
		{
			if (p.xpoints[i] + p.ypoints[i] < p.xpoints[start] + p.ypoints[start])
			{
				start = i;
			}
		}
		double total = 0;
		for (int i = 0; i < n; i++)
		{
			total += edge(p, (start + i) % n);
		}
		double left = Math.min(1.0, share) * total;
		path.moveTo(p.xpoints[start], p.ypoints[start]);
		for (int i = 0; i < n && left > 0; i++)
		{
			final int a = (start + i) % n;
			final int b = (a + 1) % n;
			final double len = edge(p, a);
			final double t = len <= 0 ? 1.0 : Math.min(1.0, left / len);
			path.lineTo(p.xpoints[a] + (p.xpoints[b] - p.xpoints[a]) * t, p.ypoints[a] + (p.ypoints[b] - p.ypoints[a]) * t);
			left -= len;
		}
		return path;
	}

	private static double edge(Polygon p, int a)
	{
		final int b = (a + 1) % p.npoints;
		return Math.hypot(p.xpoints[b] - p.xpoints[a], p.ypoints[b] - p.ypoints[a]);
	}
}
