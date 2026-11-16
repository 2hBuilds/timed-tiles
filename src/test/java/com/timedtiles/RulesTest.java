package com.timedtiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import net.runelite.api.WorldType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** The frozen tests T15 to T18: the click rule, the share rule, the PvP worlds and the party message. */
public class RulesTest
{
	@Test
	public void t15_clickRuleNeedsAllFour()
	{
		assertTrue(ClickRule.shouldPlace(true, false, true, true));
		assertFalse(ClickRule.shouldPlace(false, false, true, true));
		assertFalse(ClickRule.shouldPlace(true, true, true, true));
		assertFalse(ClickRule.shouldPlace(true, false, false, true));
		assertFalse(ClickRule.shouldPlace(true, false, true, false));
	}

	@Test
	public void t16_shareRuleNeedsAllFour()
	{
		assertTrue(ShareRule.canShare(true, true, false, false));
		assertFalse(ShareRule.canShare(false, true, false, false));
		assertFalse(ShareRule.canShare(true, false, false, false));
		assertFalse(ShareRule.canShare(true, true, true, false));
		assertFalse(ShareRule.canShare(true, true, false, true));
	}

	@Test
	public void t17_pvpWorlds()
	{
		assertFalse(PvpWorlds.isPvp(Collections.<WorldType>emptySet()));
		assertFalse(PvpWorlds.isPvp(EnumSet.of(WorldType.MEMBERS)));
		for (WorldType type : Arrays.asList(WorldType.PVP, WorldType.DEADMAN, WorldType.PVP_ARENA,
			WorldType.LAST_MAN_STANDING, WorldType.BOUNTY, WorldType.HIGH_RISK))
		{
			assertTrue(type.name(), PvpWorlds.isPvp(EnumSet.of(type)));
			assertTrue(type.name() + " with members", PvpWorlds.isPvp(EnumSet.of(WorldType.MEMBERS, type)));
		}
	}

	@Test
	public void t18_theMessageKeepsEveryField()
	{
		final TimedTilesMessage m = new TimedTilesMessage(1, 3200, 3201, 2, 7, 0x123456, 15);
		assertEquals(1, m.getType());
		assertEquals(3200, m.getX());
		assertEquals(3201, m.getY());
		assertEquals(2, m.getPlane());
		assertEquals(7, m.getNumber());
		assertEquals(0x123456, m.getRgb());
		assertEquals(15, m.getSeconds());
		assertEquals(MarkerBook.TYPE_ADD, 0);
		assertEquals(MarkerBook.TYPE_REMOVE, 1);
		assertEquals(MarkerBook.TYPE_CLEAR, 2);
		assertNotNull(new TimedTilesMessage());
		assertEquals(new TimedTilesMessage(1, 3200, 3201, 2, 7, 0x123456, 15), m);
		assertEquals("TimedTilesMessage", TimedTilesMessage.class.getSimpleName());
	}
}
