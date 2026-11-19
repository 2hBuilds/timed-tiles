package com.timedtiles;

import java.net.URL;

/**
 * The text of one choice in a settings dropdown, with a picture of what it looks like in front. RuneLite's dropdown is
 * a Swing label showing each choice's {@code toString()}, and a Swing label draws HTML, so the picture is an
 * {@code <img>} of the plugin's own resource. When the picture cannot be found, the plain text is used.
 *
 * <p>The pictures are 48 x 32, a marker at game scale, drawn by {@link MarkerPainter} (docs/menu-icons/RealIcons.java.txt in the private repository).
 * The opened list's rows grow to fit them; the closed box stays 22 px tall and shows the middle of the picture.
 */
final class MenuText
{
	static final int WIDTH = 48;
	static final int HEIGHT = 32;

	private MenuText()
	{
	}

	/**
	 * @param icon the picture's name in {@code com/timedtiles/menu/}, without ".png"
	 * @param text the choice's name, shown after the picture
	 */
	static String with(String icon, String text)
	{
		final URL url = MenuText.class.getResource("menu/" + icon + ".png");
		if (url == null)
		{
			return text;
		}
		return "<html><img src='" + url + "' width='" + WIDTH + "' height='" + HEIGHT + "' align='middle'>&nbsp;" + text
			+ "</html>";
	}

	/** The colour squares' size. */
	static final int SQUARE = 12;

	/**
	 * A choice with a {@value #SQUARE} px square in front: a table cell of {@code rgb} holding the picture {@code icon}
	 * (a grey frame with a clear middle), or the picture alone when {@code rgb} is negative. A table keeps every square
	 * the same size and level with the words in any font; a coloured run of spaces would take the font's width, which
	 * in the RuneScape font is a thin bar.
	 */
	static String withSquare(int rgb, String icon, String text)
	{
		final URL url = MenuText.class.getResource("menu/" + icon + ".png");
		if (url == null)
		{
			return text;
		}
		final String cell = rgb < 0
			? "<td valign='middle'>"
			: String.format("<td valign='middle' bgcolor='#%06X'>", rgb & 0xFFFFFF);
		return "<html><table cellspacing='0' cellpadding='0'><tr><td valign='middle'>"
			+ "<table cellspacing='0' cellpadding='0'><tr>" + cell + "<img src='" + url + "' width='" + SQUARE
			+ "' height='" + SQUARE + "'></td></tr></table></td><td valign='middle'>&nbsp;" + text
			+ "</td></tr></table></html>";
	}
}
