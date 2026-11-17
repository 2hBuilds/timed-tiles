package com.timedtiles;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** What the tile shows for each "Tile label" choice, and how the timer is written. */
public class LabelTextTest
{
	@Test
	public void numberShowsTheNumber()
	{
		assertEquals("3", LabelText.of(TileLabel.NUMBER, 3, null, false, 9_000, 0));
	}

	@Test
	public void timerShowsTheSecondsLeft()
	{
		assertEquals("9", LabelText.of(TileLabel.TIMER, 3, null, false, 8_270, 0));
	}

	@Test
	public void nothingShowsNothingEvenWithNames()
	{
		assertNull(LabelText.of(TileLabel.NOTHING, 3, null, false, 9_000, 0));
		assertNull(LabelText.of(TileLabel.NOTHING, 3, "Ann", true, 9_000, 2));
	}

	@Test
	public void aPartyNameFollowsTheNumberOrTheTimerWhenNamesAreOn()
	{
		assertEquals("3 Ann", LabelText.of(TileLabel.NUMBER, 3, "Ann", true, 9_000, 0));
		assertEquals("9 Ann", LabelText.of(TileLabel.TIMER, 3, "Ann", true, 8_270, 0));
		assertEquals("3", LabelText.of(TileLabel.NUMBER, 3, "Ann", false, 9_000, 0));
		assertEquals("3", LabelText.of(TileLabel.NUMBER, 3, null, true, 9_000, 0));
	}

	@Test
	public void aSettingThatCouldNotBeReadShowsTheNumber()
	{
		assertEquals("4", LabelText.of(null, 4, null, false, 1_000, 0));
	}

	@Test
	public void wholeSecondsRoundUpSoTheTimerNeverReadsZeroWhileTheMarkerIsThere()
	{
		assertEquals("15", LabelText.seconds(15_000, 0));
		assertEquals("15", LabelText.seconds(14_001, 0));
		assertEquals("14", LabelText.seconds(14_000, 0));
		assertEquals("4", LabelText.seconds(3_840, 0));
		assertEquals("1", LabelText.seconds(1, 0));
		assertEquals("0", LabelText.seconds(0, 0));
	}

	@Test
	public void tenthsAndHundredthsRoundUpAtTheShownDigit()
	{
		assertEquals("3.9", LabelText.seconds(3_840, 1));
		assertEquals("3.8", LabelText.seconds(3_800, 1));
		assertEquals("15.0", LabelText.seconds(15_000, 1));
		assertEquals("0.1", LabelText.seconds(1, 1));
		assertEquals("3.84", LabelText.seconds(3_840, 2));
		assertEquals("3.84", LabelText.seconds(3_835, 2));
		assertEquals("3.04", LabelText.seconds(3_040, 2));
		assertEquals("3.00", LabelText.seconds(3_000, 2));
		assertEquals("0.01", LabelText.seconds(1, 2));
		assertEquals("0.00", LabelText.seconds(0, 2));
		assertEquals("120.00", LabelText.seconds(120_000, 2));
	}

	@Test
	public void aNegativeTimeIsZeroAndOddDecimalsAreHeldToZeroToTwo()
	{
		assertEquals("0", LabelText.seconds(-500, 0));
		assertEquals("0.00", LabelText.seconds(-500, 2));
		assertEquals("4", LabelText.seconds(3_840, -1));
		assertEquals("3.84", LabelText.seconds(3_840, 5));
	}
}
