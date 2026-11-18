package com.timedtiles;

import java.awt.Polygon;
import java.awt.geom.PathIterator;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The clock's sums, the menu pictures and the choices of the Clock and Tile label settings.
 */
public class ClockFaceTest
{
	private static Polygon square()
	{
		return new Polygon(new int[]{10, 30, 30, 10}, new int[]{10, 10, 30, 30}, 4);
	}

	/** The length of a path made of straight lines. */
	private static double length(java.awt.geom.Path2D path)
	{
		final double[] at = new double[6];
		double total = 0;
		double x = 0;
		double y = 0;
		for (PathIterator it = path.getPathIterator(null); !it.isDone(); it.next())
		{
			final int type = it.currentSegment(at);
			if (type == PathIterator.SEG_LINETO)
			{
				total += Math.hypot(at[0] - x, at[1] - y);
			}
			x = at[0];
			y = at[1];
		}
		return total;
	}

	@Test
	public void timeLeftIsTheShareOfTheLifeHeldBetweenZeroAndOne()
	{
		assertEquals(1f, ClockFace.timeLeft(10_000, 10_000), 1e-6);
		assertEquals(0.25f, ClockFace.timeLeft(2_500, 10_000), 1e-6);
		assertEquals(0f, ClockFace.timeLeft(-5, 10_000), 1e-6);
		assertEquals(1f, ClockFace.timeLeft(20_000, 10_000), 1e-6);
		assertEquals("no life is no time left", 0f, ClockFace.timeLeft(5_000, 0), 1e-6);
	}

	@Test
	public void theCentreIsTheAverageOfTheCorners()
	{
		final double[] c = ClockFace.centre(square());
		assertEquals(20, c[0], 1e-9);
		assertEquals(20, c[1], 1e-9);
	}

	@Test
	public void theTopRightCornerIsFurthestUpAndRight()
	{
		assertEquals(1, ClockFace.topRight(square()));
		final Polygon tilted = new Polygon(new int[]{0, 50, 60, 5}, new int[]{20, 0, 40, 50}, 4);
		assertEquals(1, ClockFace.topRight(tilted));
	}

	@Test
	public void theOutlinePartIsThatShareOfTheOutline()
	{
		assertEquals(0, length(ClockFace.outlinePart(square(), 0f)), 1e-9);
		assertEquals(20, length(ClockFace.outlinePart(square(), 0.25f)), 1e-6);
		assertEquals(50, length(ClockFace.outlinePart(square(), 0.625f)), 1e-6);
		assertEquals(80, length(ClockFace.outlinePart(square(), 1f)), 1e-6);
		assertEquals("more than all is all", 80, length(ClockFace.outlinePart(square(), 3f)), 1e-6);
	}

	@Test
	public void theOutlineStartsAtTheTopLeftWhicheverPointComesFirst()
	{
		final Polygon shifted = new Polygon(new int[]{30, 30, 10, 10}, new int[]{10, 30, 30, 10}, 4);
		final double[] first = new double[6];
		ClockFace.outlinePart(shifted, 0.1f).getPathIterator(null).currentSegment(first);
		assertEquals(10, first[0], 1e-9);
		assertEquals(10, first[1], 1e-9);
	}

	@Test
	public void everyChoiceShowsItsPictureAndItsNameInMenuOrder()
	{
		final String[] label = {"Number", "Number + pie", "Number + ring", "Number + border", "Number + sweep",
			"Number + corner", "Timer", "Timer + ring", "Timer + border", "Timer + sweep", "Timer + corner", "Nothing",
			"Pie", "Ring", "Border", "Sweep"};
		assertEquals(label.length, TileLabel.values().length);
		for (int i = 0; i < label.length; i++)
		{
			final String menu = TileLabel.values()[i].toString();
			assertTrue(menu, menu.startsWith("<html><img src='") && menu.endsWith("&nbsp;" + label[i] + "</html>"));
		}
	}

	@Test
	public void eachChoiceWritesAndClocksWhatItsNameSays()
	{
		for (TileLabel t : TileLabel.values())
		{
			final String name = t.name();
			final TileLabel.Words words = name.startsWith("NUMBER") ? TileLabel.Words.NUMBER
				: name.startsWith("TIMER") ? TileLabel.Words.TIMER : TileLabel.Words.NONE;
			assertEquals(name, words, t.words());
			final String clockPart = name.contains("_") ? name.substring(name.indexOf('_') + 1)
				: words == TileLabel.Words.NONE && t != TileLabel.NOTHING ? name : "OFF";
			assertEquals(name, ClockStyle.valueOf(clockPart), t.clock());
		}
	}

	@Test
	public void the101ChoicesKeepTheirStoredNames()
	{
		assertEquals(TileLabel.NUMBER, TileLabel.valueOf("NUMBER"));
		assertEquals(TileLabel.TIMER, TileLabel.valueOf("TIMER"));
		assertEquals(TileLabel.NOTHING, TileLabel.valueOf("NOTHING"));
	}

	@Test
	public void everyChoiceHasItsPictureAtTheMenuSize() throws Exception
	{
		for (TileLabel t : TileLabel.values())
		{
			final String name = "menu/tile-" + t.name().toLowerCase(java.util.Locale.ROOT).replace('_', '-') + ".png";
			final java.net.URL url = TileLabel.class.getResource(name);
			assertTrue("missing " + name, url != null);
			final java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(url);
			assertEquals(name, MenuText.WIDTH, image.getWidth());
			assertEquals(name, MenuText.HEIGHT, image.getHeight());
			assertTrue(t.toString(), t.toString().contains("width='" + MenuText.WIDTH + "' height='" + MenuText.HEIGHT + "'"));
		}
	}

	@Test
	public void aMissingPictureFallsBackToThePlainName()
	{
		assertEquals("Ring", MenuText.with("no-such-picture", "Ring"));
	}
}
