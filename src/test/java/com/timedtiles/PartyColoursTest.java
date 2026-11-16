package com.timedtiles;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/** Party members' colours: kept when they differ from yours, steered clear when they clash. */
public class PartyColoursTest
{
	private static final int GOLD = 0xFFC800;
	private static final int GREEN = 0x00FF00;

	@Test
	public void aColourThatIsNotLikeYoursIsKept()
	{
		assertEquals(GREEN, new PartyColours().colourFor(42, GREEN, GOLD));
	}

	@Test
	public void aColourLikeYoursIsReplacedByOneClearOfIt()
	{
		final int got = new PartyColours().colourFor(42, GOLD, GOLD);
		assertNotEquals(GOLD, got);
		assertFalse(PartyColours.close(got, GOLD));
		final int nearGold = new PartyColours().colourFor(42, 0xFFD010, GOLD);
		assertFalse(PartyColours.close(nearGold, GOLD));
	}

	@Test
	public void twoMembersWithTheSameColourGetDifferentOnes()
	{
		final PartyColours colours = new PartyColours();
		final int a = colours.colourFor(1, GREEN, GOLD);
		final int b = colours.colourFor(2, GREEN, GOLD);
		assertEquals(GREEN, a);
		assertFalse(PartyColours.close(a, b));
		assertFalse(PartyColours.close(b, GOLD));
	}

	@Test
	public void everyoneOnTheDefaultGoldEndsUpWithDistinctColours()
	{
		final PartyColours colours = new PartyColours();
		final int[] got = new int[5];
		for (int i = 0; i < got.length; i++)
		{
			got[i] = colours.colourFor(i + 1, GOLD, GOLD);
			assertFalse(PartyColours.close(got[i], GOLD));
		}
		for (int i = 0; i < got.length; i++)
		{
			for (int j = i + 1; j < got.length; j++)
			{
				assertFalse(i + " vs " + j, PartyColours.close(got[i], got[j]));
			}
		}
	}

	@Test
	public void aColourThatIsDifferentToTheEyeIsShownAsChosen()
	{
		final PartyColours colours = new PartyColours();
		assertEquals(0xFFFF00, colours.colourFor(1, 0xFFFF00, GOLD));
		assertEquals(0xFF8000, colours.colourFor(2, 0xFF8000, GOLD));
		assertEquals(0xEF1010, colours.colourFor(3, 0xEF1010, GOLD));
		assertEquals(0x0000FF, colours.colourFor(4, 0x0000FF, GOLD));
	}

	@Test
	public void aMemberWhoStillClashesKeepsTheirStandIn()
	{
		final PartyColours colours = new PartyColours();
		final int first = colours.colourFor(42, GOLD, GOLD);
		assertEquals(first, colours.colourFor(42, GOLD, GOLD));
		assertEquals(first, colours.colourFor(42, 0xFFD010, GOLD));
	}

	@Test
	public void aMemberWhoChangesToAColourThatNoLongerClashesIsShownInIt()
	{
		final PartyColours colours = new PartyColours();
		final int standIn = colours.colourFor(42, GOLD, GOLD);
		assertNotEquals(GOLD, standIn);
		assertEquals(0xEF1010, colours.colourFor(42, 0xEF1010, GOLD));
		assertEquals(GREEN, colours.colourFor(42, GREEN, GOLD));
	}

	@Test
	public void whenYouChangeYourColourToMatchTheirsTheyAreMovedOffIt()
	{
		final PartyColours colours = new PartyColours();
		assertEquals(GREEN, colours.colourFor(42, GREEN, GOLD));
		final int moved = colours.colourFor(42, GREEN, GREEN);
		assertFalse(PartyColours.close(moved, GREEN));
	}

	@Test
	public void aMemberWhoLeavesIsForgotten()
	{
		final PartyColours colours = new PartyColours();
		colours.colourFor(42, GOLD, GOLD);
		colours.forget(42);
		assertEquals(GREEN, colours.colourFor(42, GREEN, GOLD));
		colours.clear();
		assertEquals(0x123456, colours.colourFor(42, 0x123456, GOLD));
	}

	@Test
	public void whenThePaletteRunsOutTheSentColourIsUsed()
	{
		final PartyColours colours = new PartyColours();
		for (int i = 0; i < 10; i++)
		{
			colours.colourFor(i, GOLD, GOLD);
		}
		assertEquals(GOLD, colours.colourFor(99, GOLD, 0x000000));
	}
}
