package com.timedtiles;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** "Mouse back button" and "Mouse forward button": each button does its own setting, every other button nothing. */
public class ButtonUseTest
{
	@Test
	public void eachButtonDoesItsOwnSetting()
	{
		for (ButtonUse back : ButtonUse.values())
		{
			for (ButtonUse forward : ButtonUse.values())
			{
				assertEquals(back, ButtonUse.forPress(back, forward, ButtonUse.BACK));
				assertEquals(forward, ButtonUse.forPress(back, forward, ButtonUse.FORWARD));
			}
		}
	}

	@Test
	public void theOrdinaryButtonsAreNeverTaken()
	{
		for (ButtonUse use : ButtonUse.values())
		{
			for (int button : Arrays.asList(MouseEvent.NOBUTTON, MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3))
			{
				assertEquals(ButtonUse.NOTHING, ButtonUse.forPress(use, use, button));
			}
		}
	}

	@Test
	public void anUnreadableSettingDoesNothing()
	{
		assertEquals(ButtonUse.NOTHING, ButtonUse.forPress(null, null, ButtonUse.BACK));
		assertEquals(ButtonUse.NOTHING, ButtonUse.forPress(null, null, ButtonUse.FORWARD));
		assertFalse(ButtonUse.anyInUse(null, null));
	}

	@Test
	public void theButtonsAreInUseWhenEitherIsNotNothing()
	{
		assertFalse(ButtonUse.anyInUse(ButtonUse.NOTHING, ButtonUse.NOTHING));
		assertTrue(ButtonUse.anyInUse(ButtonUse.MARKS, ButtonUse.NOTHING));
		assertTrue(ButtonUse.anyInUse(ButtonUse.NOTHING, ButtonUse.CLEARS));
		assertTrue(ButtonUse.anyInUse(null, ButtonUse.MARKS));
	}

	@Test
	public void theChoicesReadInTheMenuAndKeepTheirStoredNames()
	{
		assertEquals(Arrays.asList("NOTHING", "MARKS", "CLEARS"), Arrays.asList(
			ButtonUse.NOTHING.name(), ButtonUse.MARKS.name(), ButtonUse.CLEARS.name()));
		assertEquals(Arrays.asList("Nothing", "Marks", "Clears"), Arrays.asList(
			ButtonUse.NOTHING.toString(), ButtonUse.MARKS.toString(), ButtonUse.CLEARS.toString()));
		assertEquals(4, ButtonUse.BACK);
		assertEquals(5, ButtonUse.FORWARD);
	}
}
