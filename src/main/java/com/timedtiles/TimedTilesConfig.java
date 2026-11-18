package com.timedtiles;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

/**
 * The plugin's settings. Flat on purpose: every item is declared here and this interface extends {@link Config}
 * alone, because RuneLite stores defaults only for the methods declared on the interface it is given. The group and
 * the keys are frozen at first release; renaming one silently discards every player's setting.
 */
@ConfigGroup(TimedTilesConfig.GROUP)
public interface TimedTilesConfig extends Config
{
	String GROUP = "timed-tiles";

	@ConfigItem(
		keyName = "pingKey",
		name = "Ping key",
		description = "Hold this key and left-click the ground to place a numbered marker. Pick a key RuneLite's Party ping does not use.",
		position = 1
	)
	default Keybind pingKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "clearKey",
		name = "Clear key",
		description = "Removes all your markers.",
		position = 2
	)
	default Keybind clearKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "rightClickClear",
		name = "Right-click clears",
		description = "Hold the Ping key and right-click to remove all your markers. The menu does not open while you do.",
		position = 3
	)
	default boolean rightClickClear()
	{
		return true;
	}

	@ConfigItem(
		keyName = "lifeSeconds",
		name = "Markers last",
		description = "How long each of your markers stays.",
		position = 4
	)
	@Range(min = 1, max = 120)
	@Units(Units.SECONDS)
	default int lifeSeconds()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "myColour",
		name = "My colour",
		description = "The colour of your markers. Your party sees it too, unless it looks the same as theirs.",
		position = 5
	)
	default Color myColour()
	{
		return new Color(255, 200, 0);
	}

	@ConfigItem(
		keyName = "tileFill",
		name = "Tile fill",
		description = "How much each marker's tile is filled in. 0 draws just the outline.",
		position = 6
	)
	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	default int tileFill()
	{
		return 6;
	}

	@ConfigItem(
		keyName = "tileLabel",
		name = "Tile label",
		description = "What each marker's tile shows: its number, the seconds it has left, or nothing.",
		position = 7
	)
	default TileLabel tileLabel()
	{
		return TileLabel.NUMBER;
	}

	@ConfigItem(
		keyName = "timerDecimals",
		name = "Timer decimals",
		description = "Digits after the point when Tile label is Timer: 0 shows whole seconds, 1 tenths, 2 hundredths.",
		position = 8
	)
	@Range(min = 0, max = 2)
	default int timerDecimals()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "outlineWidth",
		name = "Outline thickness",
		description = "How thick each marker's tile outline is.",
		position = 9
	)
	@Range(min = 1, max = 6)
	@Units(Units.PIXELS)
	default int outlineWidth()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "fadeSeconds",
		name = "Fade length",
		description = "How long a marker takes to fade out at the end of its time. 0 keeps it solid until it vanishes.",
		position = 10
	)
	@Range(min = 0, max = 5)
	@Units(Units.SECONDS)
	default int fadeSeconds()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "largeNumbers",
		name = "Large numbers",
		description = "Draws the numbers and the timer bigger.",
		position = 11
	)
	default boolean largeNumbers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "share",
		name = "Share with my party",
		description = "Party members with 2h Timed Tiles see your markers. Paused in the Wilderness and on PvP worlds.",
		position = 12
	)
	default boolean share()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showParty",
		name = "Show party markers",
		description = "Shows markers from your party.",
		position = 13
	)
	default boolean showParty()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showNames",
		name = "Show names",
		description = "Shows who placed each party marker, after the number or timer. Nothing is shown when Tile label is Nothing.",
		position = 14
	)
	default boolean showNames()
	{
		return false;
	}
}
