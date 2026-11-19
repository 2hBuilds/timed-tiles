package com.timedtiles;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserPart;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 2h Timed Tiles: hold the Ping key and left-click the ground to put a numbered marker on a tile; it fades out after
 * a set time and, in a RuneLite party, shows for party members who have this plugin. Every marker comes from a click.
 */
@PluginDescriptor(
	name = "2h Timed Tiles",
	internalName = "timed-tiles",
	description = "Numbered tile markers that disappear after a set time, shared with your party. (v" + Version.CURRENT + ")",
	tags = {"tile", "markers", "ping", "party", "timer"}
)
public class TimedTilesPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(TimedTilesPlugin.class);

	static final String NO_KEY_LINE = "2h Timed Tiles: choose a ping key in its settings.";

	/** The settings that change how a marker looks: changing one shows a sample marker under the player. */
	private static final Set<String> PREVIEW_KEYS = new HashSet<>(Arrays.asList("fadeSeconds", "myColour",
		"tileFill", "outlineWidth", "tileLabel", "numberColour", "customColour", "timerDecimals", "largeNumbers"));

	/** How long the sample marker stays solid before its fade. */
	private static final long PREVIEW_SOLID_MS = 5_000;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private PartyService partyService;

	@Inject
	private WSClient wsClient;

	@Inject
	private TimedTilesConfig config;

	@Inject
	private MarkerOverlay overlay;

	@Inject
	private MarkerBook book;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	private volatile boolean pingKeyHeld;

	private boolean warnedNoKey;

	/** Each party member's drawing colour. Client thread only. */
	private final PartyColours partyColours = new PartyColours();

	private final HotkeyListener pingListener = new HotkeyListener(() -> config.pingKey())
	{
		@Override
		public void hotkeyPressed()
		{
			pingKeyHeld = true;
		}

		@Override
		public void hotkeyReleased()
		{
			pingKeyHeld = false;
		}
	};

	private final HotkeyListener clearListener = new HotkeyListener(() -> config.clearKey())
	{
		@Override
		public void hotkeyPressed()
		{
			clientThread.invoke(TimedTilesPlugin.this::clearMine);
		}
	};

	/** Ping key + right-click clears, when "Right-click clears" is on: the menu is kept from opening. */
	private final MouseAdapter rightClickListener = new MouseAdapter()
	{
		private volatile boolean swallowRelease;
		private volatile boolean swallowClick;

		@Override
		public MouseEvent mousePressed(MouseEvent e)
		{
			swallowClick = false;
			if (pingKeyHeld && config.rightClickClear() && SwingUtilities.isRightMouseButton(e))
			{
				swallowRelease = true;
				e.consume();
				clientThread.invoke(TimedTilesPlugin.this::clearMine);
			}
			return e;
		}

		@Override
		public MouseEvent mouseReleased(MouseEvent e)
		{
			if (swallowRelease)
			{
				swallowRelease = false;
				swallowClick = true;
				e.consume();
			}
			return e;
		}

		@Override
		public MouseEvent mouseClicked(MouseEvent e)
		{
			if (swallowClick)
			{
				swallowClick = false;
				e.consume();
			}
			return e;
		}
	};

	@Provides
	TimedTilesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TimedTilesConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		keyManager.registerKeyListener(pingListener);
		keyManager.registerKeyListener(clearListener);
		mouseManager.registerMouseListener(rightClickListener);
		wsClient.registerMessage(TimedTilesMessage.class);
		NumberColour.showCustom(config.customColour());
		log.info("2h Timed Tiles {} started", Version.CURRENT);
	}

	@Override
	protected void shutDown()
	{
		wsClient.unregisterMessage(TimedTilesMessage.class);
		mouseManager.unregisterMouseListener(rightClickListener);
		keyManager.unregisterKeyListener(clearListener);
		keyManager.unregisterKeyListener(pingListener);
		overlayManager.remove(overlay);
		pingKeyHeld = false;
		clientThread.invoke(this::dropEverything);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e)
	{
		boolean hasWalkHere = false;
		for (MenuEntry entry : client.getMenuEntries())
		{
			if ("walk here".equalsIgnoreCase(entry.getOption()))
			{
				hasWalkHere = true;
				break;
			}
		}
		final Tile tile = client.getSelectedSceneTile();
		if (!ClickRule.shouldPlace(pingKeyHeld, client.isMenuOpen(), hasWalkHere, tile != null))
		{
			return;
		}
		e.consume();
		final WorldPoint point = tile.getWorldLocation();
		final int seconds = config.lifeSeconds();
		final Change change = book.placeMine(point.getX(), point.getY(), point.getPlane(),
			config.myColour().getRGB() & 0xFFFFFF, System.currentTimeMillis(), seconds * 1000L);
		if (canShare())
		{
			final Marker m = change.getMarker();
			final int type = change.getType() == Change.Type.ADD ? MarkerBook.TYPE_ADD : MarkerBook.TYPE_REMOVE;
			// Shared as template coordinates, so that party members in the same instance see the same tile.
			final WorldPoint shared = WorldPoint.fromLocalInstance(client, tile.getLocalLocation(), point.getPlane());
			partyService.send(new TimedTilesMessage(type, shared.getX(), shared.getY(), shared.getPlane(),
				m.getNumber(), m.getRgb(), seconds));
		}
	}

	private void dropParty()
	{
		book.dropParty();
		partyColours.clear();
	}

	private void dropEverything()
	{
		book.dropAll();
		partyColours.clear();
	}

	/** Removes all the player's markers, for the party too. Client thread. */
	private void clearMine()
	{
		if (book.clearMine() && canShare())
		{
			partyService.send(new TimedTilesMessage(MarkerBook.TYPE_CLEAR, 0, 0, 0, 0, 0, 0));
		}
	}

	private boolean canShare()
	{
		return ShareRule.canShare(config.share(), partyService.isInParty(),
			client.getVarbitValue(VarbitID.INSIDE_WILDERNESS) == 1, PvpWorlds.isPvp(client.getWorldType()));
	}

	@Subscribe
	public void onTimedTilesMessage(TimedTilesMessage m)
	{
		final long sender = m.getMemberId();
		final int type = m.getType();
		final int x = m.getX();
		final int y = m.getY();
		final int plane = m.getPlane();
		final int number = m.getNumber();
		final int rgb = m.getRgb();
		final int seconds = m.getSeconds();
		clientThread.invoke(() ->
		{
			final PartyMember local = partyService.getLocalMember();
			final boolean mine = local != null && local.getMemberId() == sender;
			if (mine && !(developerMode && type == MarkerBook.TYPE_ADD))
			{
				return;
			}
			if (!mine && !config.showParty())
			{
				return;
			}
			final long now = System.currentTimeMillis();
			if (mine)
			{
				for (WorldPoint p : onMyScene(x, y, plane))
				{
					overlay.addEcho(p.getX(), p.getY(), p.getPlane(), now);
				}
				return;
			}
			final PartyMember member = partyService.getMemberById(sender);
			final String who = member == null ? null : member.getDisplayName();
			final int shownRgb = type == MarkerBook.TYPE_ADD
				? partyColours.colourFor(sender, rgb, config.myColour().getRGB() & 0xFFFFFF)
				: rgb;
			if (type == MarkerBook.TYPE_CLEAR)
			{
				book.applyParty(sender, who, type, 0, 0, 0, number, rgb, seconds, now);
				return;
			}
			for (WorldPoint p : onMyScene(x, y, plane))
			{
				book.applyParty(sender, who, type, p.getX(), p.getY(), p.getPlane(), number, shownRgb, seconds, now);
			}
		});
	}

	/** A shared tile as this client's scene holds it: itself outside instances, the instance's copies inside one. */
	private Collection<WorldPoint> onMyScene(int x, int y, int plane)
	{
		final WorldPoint point = new WorldPoint(x, y, plane);
		if (!client.getTopLevelWorldView().isInstance())
		{
			return Collections.singleton(point);
		}
		return WorldPoint.toLocalInstance(client, point);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		final GameState state = e.getGameState();
		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING)
		{
			dropEverything();
		}
		else if (state == GameState.LOGGED_IN && !warnedNoKey && Keybind.NOT_SET.equals(config.pingKey()))
		{
			client.addChatMessage(ChatMessageType.CONSOLE, "", NO_KEY_LINE, null);
			warnedNoKey = true;
		}
	}

	@Subscribe
	public void onPartyChanged(PartyChanged e)
	{
		clientThread.invoke(this::dropParty);
	}

	@Subscribe
	public void onUserPart(UserPart e)
	{
		final long member = e.getMemberId();
		clientThread.invoke(() ->
		{
			book.dropMember(member);
			partyColours.forget(member);
		});
	}

	@Subscribe
	public void onFocusChanged(FocusChanged e)
	{
		if (!e.isFocused())
		{
			pingKeyHeld = false;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		if (!TimedTilesConfig.GROUP.equals(e.getGroup()))
		{
			return;
		}
		if ("showParty".equals(e.getKey()) && !config.showParty())
		{
			clientThread.invoke(this::dropParty);
		}
		if ("customColour".equals(e.getKey()))
		{
			NumberColour.showCustom(config.customColour());
		}
		if (PREVIEW_KEYS.contains(e.getKey()))
		{
			clientThread.invoke(this::showPreview);
		}
	}

	/**
	 * A sample marker under the player for a few seconds, then its fade, in the new look. On this screen only: it is
	 * never sent to the party. Client thread.
	 */
	private void showPreview()
	{
		final Player me = client.getLocalPlayer();
		if (client.getGameState() != GameState.LOGGED_IN || me == null)
		{
			return;
		}
		final WorldPoint at = me.getWorldLocation();
		book.showPreview(at.getX(), at.getY(), at.getPlane(), config.myColour().getRGB() & 0xFFFFFF,
			System.currentTimeMillis(), PREVIEW_SOLID_MS + Math.max(0, config.fadeSeconds()) * 1000L);
	}
}
