# 2h Timed Tiles

> The full description of 2h Timed Tiles - every setting and rule. The short version is the plugin's page on the
> Plugin Hub (the repository's README).

Hold a key, left-click the ground, and a numbered marker appears on that tile. It fades out after a set time.
Inside a RuneLite party, party members who also have 2h Timed Tiles see your markers too.

Every marker comes from your own click. The plugin never places one by itself.

## Placing markers

- **Hold the Ping key and left-click the ground.** A marker goes on the tile under the mouse and your character does
  not walk. It works only when no right-click menu is open and the click would have been "Walk here", the same rule
  RuneLite's own Party ping follows. Without the key held, clicks work as normal.
- **Numbers.** Your markers are numbered 1, 2, 3 in the order you click. Click a tile that already has one of your
  markers to remove it; the others keep their numbers. Numbers keep counting up while you have any marker. Once none
  are left (removed, cleared or timed out) the next marker is 1 again.
- **Time.** Each marker lasts "Markers last" seconds (1 to 120, default 15) and fades out over its last 2 seconds
  (over its whole life if that is shorter than 2 seconds).
- **Limit.** At most 20 of your markers at once. A 21st removes your oldest.
- **Clear key (optional).** A second way to remove all your markers, for your party too.
- **Right-click clears (on by default).** Holding the Ping key and right-clicking removes all your markers, and the
  right-click menu does not open while the key is held. Turn it off in the settings to get the menu back.
- **Logout and world hop** remove all markers.

## Party

Make or join a party with RuneLite's own Party plugin. With "Share with my party" on, each marker you place, remove
or clear is sent through RuneLite's party server, and nowhere else. Party members who have 2h Timed Tiles see your
markers with your number, for the duration you set. Each party member's markers are drawn in the colour they chose,
so you can tell who placed what. If a colour looks the same as yours or as another member's (everyone on the default,
say), the plugin shows that member in a clearly different colour on your screen, and switches to their real colour as
soon as it no longer clashes. Turn on "Show names" to also write the placer's name next to the number. Members without
the plugin see nothing.

- Sharing pauses in the Wilderness and on PvP worlds (Deadman, PvP, bounty, high risk, Last Man Standing and PvP
  arena). Your markers then stay on your own screen only.
- A party member's markers go when they leave the party. All party markers go when you leave or change party.
- Inside instances, markers are shared by their place in the instance's template, so party members in the same
  instance see the same tile.

## Settings

| Setting | Default | What it does |
|---|---|---|
| Ping key | not set | Hold it and left-click the ground to place a numbered marker. Pick a key RuneLite's Party ping does not use. |
| Clear key | not set | Removes all your markers. |
| Right-click clears | on | Hold the Ping key and right-click to remove all your markers. The menu does not open while you do. |
| Markers last | 15 seconds | How long each of your markers stays (1 to 120). |
| My colour | orange-gold | The colour of your markers. Your party sees it too, unless it looks the same as theirs. |
| Tile fill | 20% | How much each marker's tile is filled in. 0 draws just the outline. |
| Large numbers | off | Draws the numbers bigger. |
| Share with my party | on | Party members with 2h Timed Tiles see your markers. Paused in the Wilderness and on PvP worlds. |
| Show party markers | on | Shows markers from your party. |
| Show names | off | Shows who placed each party marker. |

Once per RuneLite run, at login, if the Ping key is not set, the chat shows one line: "2h Timed Tiles: choose a ping
key in its settings."

## Good to know

- **Privacy.** Nothing about you is sent anywhere except the marker's tile, number, colour and duration, to your own
  party through RuneLite's party server, and only while you are in a party with sharing on.
- **Something wrong?** Tell us in the 2hBuilds Discord - the link is on the plugin's Hub page.
