package com.timedtiles;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** The frozen tests T1 to T14: the marker book, with plain tiles and times. */
public class MarkerBookTest
{
	private static final long LIFE = 15_000;
	private static final int GOLD = 0xFFC800;

	private static Change place(MarkerBook book, int x, long now)
	{
		return book.placeMine(x, 3200, 0, GOLD, now, LIFE);
	}

	private static List<Integer> numbers(List<Marker> markers)
	{
		final List<Integer> numbers = new ArrayList<>();
		for (Marker m : markers)
		{
			numbers.add(m.getNumber());
		}
		return numbers;
	}

	private static MarkerBook threePlaced()
	{
		final MarkerBook book = new MarkerBook();
		place(book, 3200, 0);
		place(book, 3201, 0);
		place(book, 3202, 0);
		return book;
	}

	@Test
	public void t1_threeClicksAreNumberedOneTwoThree()
	{
		final MarkerBook book = new MarkerBook();
		for (int i = 0; i < 3; i++)
		{
			final Change change = place(book, 3200 + i, 0);
			assertEquals(Change.Type.ADD, change.getType());
			assertEquals(i + 1, change.getMarker().getNumber());
			assertEquals(0, change.getMarker().getOwner());
			assertNull(change.getMarker().getWho());
		}
		assertEquals(3, book.visible(0, 0).size());
	}

	@Test
	public void t2_clickingAMarkedTileRemovesItAndTheOthersKeepTheirNumbers()
	{
		final MarkerBook book = threePlaced();
		final Change change = place(book, 3201, 0);
		assertEquals(Change.Type.REMOVE, change.getType());
		assertEquals(2, change.getMarker().getNumber());
		assertEquals(java.util.Arrays.asList(1, 3), numbers(book.visible(0, 0)));
	}

	@Test
	public void t3_theNextMarkerAfterARemovalCountsOn()
	{
		final MarkerBook book = threePlaced();
		place(book, 3201, 0);
		assertEquals(4, place(book, 3203, 0).getMarker().getNumber());
	}

	@Test
	public void t4_aMarkerLivesUntilItsLifeIsUp()
	{
		final MarkerBook book = new MarkerBook();
		place(book, 3200, 0);
		assertEquals(1, book.visible(0, 14_999).size());
		assertTrue(book.visible(0, 15_000).isEmpty());
		book.expire(15_000);
		assertTrue(book.visible(0, 0).isEmpty());
	}

	@Test
	public void t5_afterAllExpireTheNextMarkerIsOne()
	{
		final MarkerBook book = threePlaced();
		book.expire(15_000);
		assertEquals(1, book.placeMine(3200, 3200, 0, GOLD, 15_000, LIFE).getMarker().getNumber());
	}

	@Test
	public void t6_clearMineRemovesAllAndRestartsTheCount()
	{
		final MarkerBook book = threePlaced();
		assertTrue(book.clearMine());
		assertTrue(book.visible(0, 0).isEmpty());
		assertEquals(1, place(book, 3200, 0).getMarker().getNumber());
		book.clearMine();
		assertFalse(book.clearMine());
	}

	@Test
	public void t7_aTwentyFirstMarkerRemovesTheOldest()
	{
		final MarkerBook book = new MarkerBook();
		for (int i = 0; i < 21; i++)
		{
			place(book, 3200 + i, 0);
		}
		final List<Marker> visible = book.visible(0, 0);
		assertEquals(20, visible.size());
		assertEquals(2, visible.get(0).getNumber());
		assertEquals(21, visible.get(19).getNumber());
	}

	@Test
	public void t8_theFadeIsTheLastTwoSecondsOrAllOfAShorterLife()
	{
		final Marker long15 = new Marker(0, null, 1, 1, 0, 1, GOLD, 0, 15_000);
		assertEquals(1.0f, MarkerBook.alpha(long15, 13_000), 0.0001f);
		assertEquals(0.5f, MarkerBook.alpha(long15, 14_000), 0.0001f);
		assertEquals(0.0f, MarkerBook.alpha(long15, 15_000), 0.0001f);
		assertEquals(1.0f, MarkerBook.alpha(long15, 0), 0.0001f);
		assertEquals(0.0f, MarkerBook.alpha(long15, 16_000), 0.0001f);
		final Marker short1 = new Marker(0, null, 1, 1, 0, 1, GOLD, 0, 1_000);
		assertEquals(0.5f, MarkerBook.alpha(short1, 500), 0.0001f);
	}

	@Test
	public void t9_aPartyAddIsKeptWithItsOwnerNumberColourAndName()
	{
		final MarkerBook book = new MarkerBook();
		book.applyParty(42, "Klagnar", MarkerBook.TYPE_ADD, 3205, 3200, 0, 3, 0x00FF00, 10, 0);
		final List<Marker> visible = book.visible(0, 0);
		assertEquals(1, visible.size());
		final Marker m = visible.get(0);
		assertEquals(42, m.getOwner());
		assertEquals(3, m.getNumber());
		assertEquals(0x00FF00, m.getRgb());
		assertEquals("Klagnar", m.getWho());
		assertEquals(10_000, m.expiresAtMs());
		book.expire(10_000);
		assertTrue(book.visible(0, 0).isEmpty());
	}

	@Test
	public void t10_aPartyRemoveAndClearNeverTouchThePlayersOwn()
	{
		final MarkerBook book = new MarkerBook();
		place(book, 3200, 0);
		book.applyParty(42, "Klagnar", MarkerBook.TYPE_ADD, 3205, 3200, 0, 1, 0x00FF00, 10, 0);
		book.applyParty(42, "Klagnar", MarkerBook.TYPE_ADD, 3206, 3200, 0, 2, 0x00FF00, 10, 0);
		book.applyParty(42, "Klagnar", MarkerBook.TYPE_REMOVE, 3205, 3200, 0, 1, 0x00FF00, 10, 0);
		assertEquals(2, book.visible(0, 0).size());
		book.applyParty(42, "Klagnar", MarkerBook.TYPE_CLEAR, 0, 0, 0, 0, 0, 0, 0);
		final List<Marker> visible = book.visible(0, 0);
		assertEquals(1, visible.size());
		assertEquals(0, visible.get(0).getOwner());
	}

	@Test
	public void t11_dropPartyAndDropMember()
	{
		final MarkerBook book = new MarkerBook();
		place(book, 3200, 0);
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3205, 3200, 0, 1, 0, 10, 0);
		book.applyParty(43, "B", MarkerBook.TYPE_ADD, 3206, 3200, 0, 1, 0, 10, 0);
		book.dropMember(42);
		assertEquals(2, book.visible(0, 0).size());
		assertEquals(43, book.visible(0, 0).get(1).getOwner());
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3205, 3200, 0, 1, 0, 10, 0);
		book.dropParty();
		final List<Marker> visible = book.visible(0, 0);
		assertEquals(1, visible.size());
		assertEquals(0, visible.get(0).getOwner());
	}

	@Test
	public void t12_visibleLeavesOutOtherPlanesAndOrdersOwnByNumberThenPartyByTime()
	{
		final MarkerBook book = new MarkerBook();
		book.applyParty(43, "B", MarkerBook.TYPE_ADD, 3300, 3200, 0, 9, 0, 10, 5);
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3301, 3200, 0, 8, 0, 10, 1);
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3302, 3200, 1, 7, 0, 10, 2);
		book.placeMine(3200, 3200, 0, GOLD, 3, LIFE);
		book.placeMine(3201, 3200, 1, GOLD, 4, LIFE);
		book.placeMine(3202, 3200, 0, GOLD, 6, LIFE);
		final List<Marker> visible = book.visible(0, 6);
		assertEquals(4, visible.size());
		assertEquals(1, visible.get(0).getNumber());
		assertEquals(0, visible.get(0).getOwner());
		assertEquals(3, visible.get(1).getNumber());
		assertEquals(0, visible.get(1).getOwner());
		assertEquals(42, visible.get(2).getOwner());
		assertEquals(43, visible.get(3).getOwner());
		assertEquals(1, book.visible(1, 6).get(0).getPlane());
	}

	@Test
	public void t13_stackIndexCountsEarlierMarkersOnTheSameTile()
	{
		final MarkerBook book = new MarkerBook();
		book.placeMine(3200, 3200, 0, GOLD, 0, LIFE);
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3200, 3200, 0, 3, 0, 10, 1);
		book.placeMine(3210, 3200, 0, GOLD, 2, LIFE);
		final List<Marker> visible = book.visible(0, 2);
		assertEquals(3, visible.size());
		final int[] stack = MarkerBook.stackIndex(visible);
		// order: own 1 (3200), own 2 (3210), party 3 (3200)
		assertEquals(0, stack[0]);
		assertEquals(0, stack[1]);
		assertEquals(1, stack[2]);
	}

	@Test
	public void t13_stackIndexForTheSpecsThreeMarkers()
	{
		final List<Marker> list = new ArrayList<>();
		list.add(new Marker(0, null, 5, 5, 0, 1, 0, 0, 1000));
		list.add(new Marker(42, "A", 5, 5, 0, 3, 0, 0, 1000));
		list.add(new Marker(0, null, 6, 5, 0, 2, 0, 0, 1000));
		final int[] stack = MarkerBook.stackIndex(list);
		assertEquals(0, stack[0]);
		assertEquals(1, stack[1]);
		assertEquals(0, stack[2]);
	}

	@Test
	public void t14_aSecondAddOnTheSameTileReplacesTheFirst()
	{
		final MarkerBook book = new MarkerBook();
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3205, 3200, 0, 1, 0, 10, 0);
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3205, 3200, 0, 2, 0, 10, 1);
		final List<Marker> visible = book.visible(0, 1);
		assertEquals(1, visible.size());
		assertEquals(2, visible.get(0).getNumber());
	}

	@Test
	public void aMemberKeepsAtMostTwentyMarkers()
	{
		final MarkerBook book = new MarkerBook();
		for (int i = 0; i < 21; i++)
		{
			book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3200 + i, 3200, 0, i + 1, 0, 10, i);
		}
		final List<Marker> visible = book.visible(0, 21);
		assertEquals(20, visible.size());
		assertEquals(2, visible.get(0).getNumber());
	}

	@Test
	public void aPartyLifeIsKeptInsideOneToOneTwentySeconds()
	{
		final MarkerBook book = new MarkerBook();
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 1, 1, 0, 1, 0, 100_000, 0);
		book.applyParty(43, "B", MarkerBook.TYPE_ADD, 2, 1, 0, 1, 0, -5, 0);
		for (Marker m : book.visible(0, 0))
		{
			assertTrue(m.getLifeMs() >= 1_000 && m.getLifeMs() <= 120_000);
		}
	}

	@Test
	public void dropAllClearsEverythingAndTheCount()
	{
		final MarkerBook book = threePlaced();
		book.applyParty(42, "A", MarkerBook.TYPE_ADD, 3205, 3200, 0, 1, 0, 10, 0);
		book.dropAll();
		assertTrue(book.visible(0, 0).isEmpty());
		assertEquals(1, place(book, 3200, 0).getMarker().getNumber());
	}
}
