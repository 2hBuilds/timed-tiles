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
	public void theTimerAndNumberChoicesEndWithCustomAndEachHasItsSquare() throws Exception
	{
		final String[] texts = {"Marker colour", "White", "Black", "Yellow", "Cyan", "Green", "Pink", "Custom"};
		final NumberColour[] all = NumberColour.values();
		assertEquals(texts.length, all.length);
		assertEquals(NumberColour.CUSTOM, all[all.length - 1]);
		for (String picture : new String[]{NumberColour.FRAME, NumberColour.MARKER_PICTURE})
		{
			final java.net.URL url = NumberColour.class.getResource("menu/" + picture + ".png");
			assertTrue("missing " + picture, url != null);
			final java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(url);
			assertEquals(picture, MenuText.SQUARE, image.getWidth());
			assertEquals(picture, MenuText.SQUARE, image.getHeight());
		}
		for (int i = 0; i < all.length; i++)
		{
			final String menu = all[i].toString();
			assertTrue(menu, menu.endsWith("&nbsp;" + texts[i] + "</td></tr></table></html>"));
			assertTrue("every square is the same size", menu.contains("width='12' height='12'"));
		}
		assertTrue(NumberColour.MARKER.toString().contains(NumberColour.MARKER_PICTURE));
		assertTrue(NumberColour.WHITE.toString().contains("bgcolor='#FFFFFF'"));
		assertTrue(NumberColour.PINK.toString().contains("bgcolor='#FF69B4'"));
	}

	@Test
	public void customsSquareShowsTheCurrentCustomColour()
	{
		try
		{
			NumberColour.showCustom(new java.awt.Color(0x12, 0xAB, 0xEF));
			assertTrue(NumberColour.CUSTOM.toString(), NumberColour.CUSTOM.toString().contains("bgcolor='#12ABEF'"));
			assertTrue(NumberColour.CUSTOM.toString().contains(NumberColour.FRAME));
			NumberColour.showCustom(null);
			assertTrue("null is ignored", NumberColour.CUSTOM.toString().contains("#12ABEF"));
			NumberColour.showCustom(java.awt.Color.BLACK);
			assertTrue(NumberColour.CUSTOM.toString().contains("bgcolor='#000000'"));
		}
		finally
		{
			NumberColour.showCustom(new java.awt.Color(0xB478FF));
		}
	}

	@Test
	public void theTimerAndNumberColourIsTheMarkersUnlessAnotherIsPicked()
	{
		final java.awt.Color purple = new java.awt.Color(180, 120, 255);
		assertEquals(0x00FF00, NumberColour.of(NumberColour.MARKER, purple, 0x00FF00));
		assertEquals(0xFFFFFF, NumberColour.of(NumberColour.WHITE, purple, 0x00FF00));
		assertEquals(0x000000, NumberColour.of(NumberColour.BLACK, purple, 0x00FF00));
		assertEquals(0xFF69B4, NumberColour.of(NumberColour.PINK, purple, 0x00FF00));
		assertEquals(0xB478FF, NumberColour.of(NumberColour.CUSTOM, purple, 0x00FF00));
		assertEquals("unread choice", 0x00FF00, NumberColour.of(null, purple, 0x00FF00));
		assertEquals("custom with no colour", 0x00FF00, NumberColour.of(NumberColour.CUSTOM, null, 0x00FF00));
	}

	@Test
	public void aMissingPictureFallsBackToThePlainName()
	{
		assertEquals("Ring", MenuText.with("no-such-picture", "Ring"));
		assertEquals("Pink", MenuText.withSquare(0xFF69B4, "no-such-picture", "Pink"));
	}
}
