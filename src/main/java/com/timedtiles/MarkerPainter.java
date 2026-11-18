package com.timedtiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import net.runelite.client.ui.overlay.OverlayUtil;

/**
 * Paints one marker onto a tile's screen polygon: its outline and fill, its clock, and its label. The overlay calls it
 * with the game's tile; the menu pictures beside each "Tile shows" choice were drawn with it too, so they look as the
 * markers do in game. Needs no game client.
 */
public final class MarkerPainter
{
	/** The clock's radius as a share of the tile's smaller side on screen, and the smallest it is drawn. */
	private static final double CLOCK_SIZE = 0.28;
	private static final double MIN_CLOCK_RADIUS = 3;
	/** How far the corner clock sits in from the tile's top-right corner, as a share of the way to its middle. */
	private static final double CORNER_INSET = 0.38;
	/** The faint parts of a clock (its face, the spent outline), as a share of the marker's opacity. */
	private static final float FAINT = 0.3f;
	private static final float SWEEP_ALPHA = 0.45f;

	private MarkerPainter()
	{
	}

	/**
	 * The tile and its clock.
	 *
	 * @param a           the marker's fade, 0 to 1
	 * @param fillPercent "Tile fill", 0 to 100
	 * @param border      the outline stroke, {@code borderWidth} pixels wide
	 * @param left        the share of its time the marker has left, 0 to 1
	 */
	public static void tile(Graphics2D g, Polygon poly, int rgb, float a, int fillPercent, Stroke border,
		int borderWidth, ClockStyle clock, float left)
	{
		final Color fill = withAlpha(rgb, a * 255 * fillPercent / 100f);
		final Color outline = withAlpha(rgb, a * 255 * (clock == ClockStyle.BORDER ? FAINT : 1f));
		OverlayUtil.renderPolygon(g, poly, outline, fill, border);
		if (clock != null && clock != ClockStyle.OFF)
		{
			clock(g, poly, clock, left, rgb, a, borderWidth);
		}
	}

	/** The label with a black shadow, both fading with the marker; (x, y) is the text's baseline start. */
	public static void label(Graphics2D g, String text, int x, int y, int rgb, float a)
	{
		final Color colour = withAlpha(rgb, a * 255);
		g.setColor(new Color(0, 0, 0, colour.getAlpha()));
		g.drawString(text, x + 1, y + 1);
		g.setColor(colour);
		g.drawString(text, x, y);
	}

	private static void clock(Graphics2D g, Polygon poly, ClockStyle clock, float left, int rgb, float a,
		int borderWidth)
	{
		final Object smooth = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		final Stroke stroke = g.getStroke();
		final Shape clip = g.getClip();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final double[] c = ClockFace.centre(poly);
		final Rectangle box = poly.getBounds();
		final double r = Math.max(MIN_CLOCK_RADIUS, Math.min(box.width, box.height) * CLOCK_SIZE);
		final double sweep = -360.0 * left;
		final Color solid = withAlpha(rgb, a * 255);
		final Color faint = withAlpha(rgb, a * 255 * FAINT);
		switch (clock)
		{
			case PIE:
				g.setColor(faint);
				g.fill(new Ellipse2D.Double(c[0] - r, c[1] - r, 2 * r, 2 * r));
				g.setColor(solid);
				g.fill(new Arc2D.Double(c[0] - r, c[1] - r, 2 * r, 2 * r, 90, sweep, Arc2D.PIE));
				break;
			case RING:
				g.setStroke(new BasicStroke((float) Math.max(2.0, r * 0.35), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
				g.setColor(faint);
				g.draw(new Ellipse2D.Double(c[0] - r, c[1] - r, 2 * r, 2 * r));
				g.setColor(solid);
				g.draw(new Arc2D.Double(c[0] - r, c[1] - r, 2 * r, 2 * r, 90, sweep, Arc2D.OPEN));
				break;
			case BORDER:
				g.setStroke(new BasicStroke(borderWidth + 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.setColor(solid);
				g.draw(ClockFace.outlinePart(poly, left));
				break;
			case SWEEP:
				final double big = Math.max(box.width, box.height);
				g.clip(poly);
				g.setColor(withAlpha(rgb, a * 255 * SWEEP_ALPHA));
				g.fill(new Arc2D.Double(c[0] - big, c[1] - big, 2 * big, 2 * big, 90, sweep, Arc2D.PIE));
				break;
			case CORNER:
				final int k = ClockFace.topRight(poly);
				final double x = poly.xpoints[k] + (c[0] - poly.xpoints[k]) * CORNER_INSET;
				final double y = poly.ypoints[k] + (c[1] - poly.ypoints[k]) * CORNER_INSET;
				final double cr = Math.max(MIN_CLOCK_RADIUS, r * 0.55);
				g.setColor(new Color(0, 0, 0, Math.round(a * 150)));
				g.fill(new Ellipse2D.Double(x - cr - 1, y - cr - 1, 2 * cr + 2, 2 * cr + 2));
				g.setColor(solid);
				g.fill(new Arc2D.Double(x - cr, y - cr, 2 * cr, 2 * cr, 90, sweep, Arc2D.PIE));
				break;
			default:
				break;
		}
		g.setClip(clip);
		g.setStroke(stroke);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, smooth);
	}

	private static Color withAlpha(int rgb, float alpha)
	{
		return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF,
			Math.max(0, Math.min(255, Math.round(alpha))));
	}
}
