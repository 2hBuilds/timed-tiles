package com.timedtiles;

import java.awt.Color;

/**
 * The "Timer & number" choices: the colour of each marker's timer, number, clock and name. "Marker colour" keeps the
 * marker's own colour; the others draw every marker's in one colour, easier to read on a busy floor; "Custom" uses the
 * "Custom colour" picker. The tile's outline and fill always keep the marker's colour. Each choice shows a small
 * colour square in the dropdown ({@link MenuText#withSquare}); Custom's shows the Custom colour now.
 */
public enum NumberColour
{
	MARKER("Marker colour", -1),
	WHITE("White", 0xFFFFFF),
	BLACK("Black", 0x000000),
	YELLOW("Yellow", 0xFFFF00),
	CYAN("Cyan", 0x00FFFF),
	GREEN("Green", 0x00FF00),
	PINK("Pink", 0xFF69B4),
	CUSTOM("Custom", -1);

	/** The grey frame drawn over every colour square; Marker colour has its own picture, a gold tile outline. */
	static final String FRAME = "colour-frame";
	static final String MARKER_PICTURE = "colour-marker";

	/**
	 * The "Custom colour" now, as 0xRRGGBB, so Custom's square in the dropdown shows it. Set by the plugin at start-up
	 * and whenever the colour changes; the dropdown asks each choice for its text every time it draws.
	 */
	private static volatile int customRgb = 0xB478FF;

	/** Custom's square shows {@code colour} from now on; null is ignored. */
	static void showCustom(Color colour)
	{
		if (colour != null)
		{
			customRgb = colour.getRGB() & 0xFFFFFF;
		}
	}

	/** The fixed colour as 0xRRGGBB, or -1 for Marker colour and Custom. */
	private final int rgb;
	private final String text;
	private final String menu;

	NumberColour(String text, int rgb)
	{
		this.rgb = rgb;
		this.text = text;
		this.menu = rgb < 0
			? MenuText.withSquare(-1, MARKER_PICTURE, text)
			: MenuText.withSquare(rgb, FRAME, text);
	}

	/**
	 * The colour as 0xRRGGBB for a marker of {@code markerRgb}. A setting RuneLite could not read, or a Custom with no
	 * colour, keeps the marker's.
	 */
	public static int of(NumberColour choice, Color custom, int markerRgb)
	{
		if (choice == null || choice == MARKER)
		{
			return markerRgb;
		}
		if (choice == CUSTOM)
		{
			return custom == null ? markerRgb : custom.getRGB() & 0xFFFFFF;
		}
		return choice.rgb;
	}

	@Override
	public String toString()
	{
		return this == CUSTOM ? MenuText.withSquare(customRgb, FRAME, text) : menu;
	}
}
