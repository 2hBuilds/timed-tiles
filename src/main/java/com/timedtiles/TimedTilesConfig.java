package com.timedtiles;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

/**
 * The plugin's settings. Flat on purpose: every item is declared here and this interface extends {@link Config}
 * alone, because RuneLite stores defaults only for the methods declared on the interface it is given. The group and
 * the keys are frozen at first release; renaming one silently discards every player's setting. The names shown in the
 * panel may change freely. The sections only group the items in the panel and change no key.
 */
@ConfigGroup(TimedTilesConfig.GROUP)
public interface TimedTilesConfig extends Config
{
	String GROUP = "timed-tiles";

	@ConfigSection(
		name = "Placing markers",
		description = "The keys, and how long markers last.",
		position = 0
	)
	String PLACING = "placing";

	@ConfigSection(
		name = "Look",
		description = "How markers are drawn.",
		position = 10
	)
	String LOOK = "look";

	@ConfigSection(
		name = "Party",
		description = "Sharing markers with your RuneLite party.",
		position = 20
	)
	String PARTY = "party";

	@ConfigItem(
		keyName = "pingKey",
		name = "Ping key",
		description = "Hold it and left-click the ground to place a marker. Avoid your Party ping key.",
		position = 1,
		section = PLACING
	)
	default Keybind pingKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "clearKey",
		name = "Clear key",
		description = "Removes all your markers.",
		position = 2,
		section = PLACING
	)
	default Keybind clearKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "rightClickClear",
		name = "Right-click clears",
		description = "Ping key + right-click removes all your markers.",
		position = 3,
		section = PLACING
	)
	default boolean rightClickClear()
	{
		return true;
	}

	@ConfigItem(
		keyName = "lifeSeconds",
		name = "Markers last",
		description = "How long each marker stays.",
		position = 4,
		section = PLACING
	)
	@Range(min = 1, max = 120)
	@Units(Units.SECONDS)
	default int lifeSeconds()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "fadeSeconds",
		name = "Fade time",
		description = "How long the fade-out takes. 0 = no fade.",
		position = 5,
		section = PLACING
	)
	@Range(min = 0, max = 5)
	@Units(Units.SECONDS)
	default int fadeSeconds()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "myColour",
		name = "Marker colour",
		description = "Your markers' colour. Your party sees it too.",
		position = 11,
		section = LOOK
	)
	default Color myColour()
	{
		return new Color(255, 200, 0);
	}

	@ConfigItem(
		keyName = "tileFill",
		name = "Tile fill",
		description = "How filled the tile is. 0 = outline only.",
		position = 12,
		section = LOOK
	)
	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	default int tileFill()
	{
		return 6;
	}

	@ConfigItem(
		keyName = "outlineWidth",
		name = "Outline thickness",
		description = "How thick the tile outline is.",
		position = 13,
		section = LOOK
	)
	@Range(min = 1, max = 6)
	@Units(Units.PIXELS)
	default int outlineWidth()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "tileLabel",
		name = "Tile shows",
		description = "What each marker shows. Every choice has a picture.",
		position = 14,
		section = LOOK
	)
	default TileLabel tileLabel()
	{
		return TileLabel.NUMBER;
	}

	@ConfigItem(
		keyName = "numberColour",
		name = "Timer & number",
		description = "Colour of the timer, number and clock.",
		position = 15,
		section = LOOK
	)
	default NumberColour numberColour()
	{
		return NumberColour.MARKER;
	}

	@ConfigItem(
		keyName = "customColour",
		name = "Custom colour",
		description = "Used when Timer & number is Custom.",
		position = 16,
		section = LOOK
	)
	default Color customColour()
	{
		return new Color(180, 120, 255);
	}

	@ConfigItem(
		keyName = "timerDecimals",
		name = "Timer decimals",
		description = "Digits after the point on the timer.",
		position = 17,
		section = LOOK
	)
	@Range(min = 0, max = 2)
	default int timerDecimals()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "largeNumbers",
		name = "Large numbers",
		description = "Draws the number and timer bigger.",
		position = 18,
		section = LOOK
	)
	default boolean largeNumbers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "share",
		name = "Share with party",
		description = "Your party sees your markers. Off in the Wilderness and PvP worlds.",
		position = 21,
		section = PARTY
	)
	default boolean share()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showParty",
		name = "Show party markers",
		description = "Shows your party's markers.",
		position = 22,
		section = PARTY
	)
	default boolean showParty()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showNames",
		name = "Show names",
		description = "Shows who placed each party marker.",
		position = 23,
		section = PARTY
	)
	default boolean showNames()
	{
		return false;
	}
}
