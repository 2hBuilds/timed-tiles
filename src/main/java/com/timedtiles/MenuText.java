package com.timedtiles;

import java.net.URL;

/**
 * The text of one choice in a settings dropdown, with a picture of what it looks like in front. RuneLite's dropdown is
 * a Swing label showing each choice's {@code toString()}, and a Swing label draws HTML, so the picture is an
 * {@code <img>} of the plugin's own resource. When the picture cannot be found, the plain text is used.
 *
 * <p>The pictures are 60 x 40, the size a marker is in game, drawn by {@link MarkerPainter} (docs/menu-icons/RealIcons.java.txt in the private repository).
 * The opened list's rows grow to fit them; the closed box stays 22 px tall and shows the middle of the picture.
 */
final class MenuText
{
	static final int WIDTH = 60;
	static final int HEIGHT = 40;

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
}
