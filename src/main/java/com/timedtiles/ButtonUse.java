package com.timedtiles;

/**
 * What a tap of one of the mouse's side buttons does: the choices of "Mouse back button" and "Mouse forward button".
 * A tap that marks puts a marker on the tile under the mouse; one that clears removes all the player's markers. Java
 * numbers the back button 4 and the forward button 5. Both settings start at Nothing: a side button is often already
 * in use, and RuneLite lets only one plugin have the side buttons (see the plugin's listener).
 */
public enum ButtonUse
{
	NOTHING("Nothing"),
	MARKS("Marks"),
	CLEARS("Clears");

	/** The mouse's back button as Java numbers it (the first extra button, after left, middle and right). */
	public static final int BACK = 4;

	/** The mouse's forward button as Java numbers it. */
	public static final int FORWARD = 5;

	private final String text;

	ButtonUse(String text)
	{
		this.text = text;
	}

	/**
	 * What a press of mouse {@code button} does with {@code back} and {@code forward} set as they are. Any other button,
	 * and a setting RuneLite could not read, does nothing.
	 */
	public static ButtonUse forPress(ButtonUse back, ButtonUse forward, int button)
	{
		final ButtonUse use = button == BACK ? back : button == FORWARD ? forward : null;
		return use == null ? NOTHING : use;
	}

	/** True when either side button does something, so the plugin needs them. */
	public static boolean anyInUse(ButtonUse back, ButtonUse forward)
	{
		return inUse(back) || inUse(forward);
	}

	private static boolean inUse(ButtonUse use)
	{
		return use != null && use != NOTHING;
	}

	@Override
	public String toString()
	{
		return text;
	}
}
