package com.timedtiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/**
 * Draws the markers: a tile outline in the marker's colour ("Outline thickness") with a fill of "Tile fill" percent,
 * fading out over "Fade time" at the end of its life, with the clock "Tile shows" picks showing the share of its time
 * left, and the words it picks over it - its number, its seconds left or nothing - and the placer's name for a party
 * marker when "Show names" is on. The clock and the words take the colour "Timer & number" picks (the marker's
 * by default); the outline and fill always keep the marker's. Runs on the client thread.
 */
public class MarkerOverlay extends Overlay
{
	private static final int STACK_STEP_PX = 16;
	private static final float LARGE_FONT_SIZE = 24f;
	private static final long ECHO_MS = 1500;
	private static final int MIN_WIDTH = 1;
	private static final int MAX_WIDTH = 6;
	private static final Stroke ECHO_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
		new float[]{4f, 4f}, 0f);

	private final Client client;
	private final TimedTilesConfig config;
	private final MarkerBook book;

	/** The outline stroke, rebuilt only when the thickness setting changes. */
	private Stroke border = new BasicStroke(2);
	private int borderWidth = 2;

	/** Developer-mode rings: {x, y, plane, until}. */
	private final List<long[]> echoes = new ArrayList<>();

	@Inject
	private MarkerOverlay(Client client, TimedTilesConfig config, MarkerBook book)
	{
		this.client = client;
		this.config = config;
		this.book = book;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	/** A dashed ring on a tile for 1.5 s: the developer-mode sign that a message came back from the party server. */
	void addEcho(int x, int y, int plane, long nowMs)
	{
		echoes.add(new long[]{x, y, plane, nowMs + ECHO_MS});
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		final long now = System.currentTimeMillis();
		book.expire(now);
		final int width = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, config.outlineWidth()));
		if (width != borderWidth)
		{
			borderWidth = width;
			border = new BasicStroke(width);
		}
		final int plane = client.getPlane();
		final List<Marker> visible = book.visible(plane, now);
		if (!visible.isEmpty())
		{
			final int[] stack = MarkerBook.stackIndex(visible);
			final Font before = g.getFont();
			g.setFont(config.largeNumbers()
				? FontManager.getRunescapeBoldFont().deriveFont(LARGE_FONT_SIZE)
				: FontManager.getRunescapeBoldFont());
			for (int i = 0; i < visible.size(); i++)
			{
				drawMarker(g, visible.get(i), stack[i], now);
			}
			g.setFont(before);
		}
		drawEchoes(g, plane, now);
		return null;
	}

	private void drawMarker(Graphics2D g, Marker m, int stacked, long now)
	{
		final float a = MarkerBook.alpha(m, now, config.fadeSeconds() * 1000L);
		if (a <= 0f)
		{
			return;
		}
		final LocalPoint lp = LocalPoint.fromWorld(client, new WorldPoint(m.getX(), m.getY(), m.getPlane()));
		if (lp == null)
		{
			return;
		}
		final Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}
		final TileLabel shows = config.tileLabel();
		final int detail = NumberColour.of(config.numberColour(), config.customColour(), m.getRgb());
		MarkerPainter.tile(g, poly, m.getRgb(), a, config.tileFill(), border, borderWidth,
			shows == null ? ClockStyle.OFF : shows.clock(), ClockFace.timeLeft(m.expiresAtMs() - now, m.getLifeMs()),
			detail);
		final String label = LabelText.of(shows, m.getNumber(), m.getWho(), config.showNames(),
			m.expiresAtMs() - now, config.timerDecimals());
		if (label == null)
		{
			return;
		}
		final Point at = Perspective.getCanvasTextLocation(client, g, lp, label, 0);
		if (at != null)
		{
			// Drawn by the painter and not by OverlayUtil.renderTextLocation, whose shadow would not fade with the marker.
			MarkerPainter.label(g, label, at.getX(), at.getY() - stacked * STACK_STEP_PX, detail, a);
		}
	}

	private void drawEchoes(Graphics2D g, int plane, long now)
	{
		for (Iterator<long[]> it = echoes.iterator(); it.hasNext(); )
		{
			final long[] echo = it.next();
			if (now >= echo[3])
			{
				it.remove();
				continue;
			}
			if (echo[2] != plane)
			{
				continue;
			}
			final LocalPoint lp = LocalPoint.fromWorld(client, new WorldPoint((int) echo[0], (int) echo[1], plane));
			if (lp == null)
			{
				continue;
			}
			final Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			if (poly != null)
			{
				OverlayUtil.renderPolygon(g, poly, Color.WHITE, ECHO_STROKE);
			}
		}
	}

}
