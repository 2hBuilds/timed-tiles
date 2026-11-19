# 2h Timed Tiles

> The full description of 2h Timed Tiles - every setting and rule. The short version is the plugin's page on the
> Plugin Hub (the repository's README).

Hold a key, left-click the ground, and a numbered marker appears on that tile. It fades out after a set time.
Inside a RuneLite party, party members who also have 2h Timed Tiles see your markers too. It works on your own as well:
a party only adds sharing.

Every marker comes from your own click. The plugin never places one by itself.

## Placing markers

- **Hold the Ping key and left-click the ground.** A marker goes on the tile under the mouse and your character does
  not walk. It works only when no right-click menu is open and the click would have been "Walk here", the same rule
  RuneLite's own Party ping follows. Without the key held, clicks work as normal.
- **Numbers.** Your markers are numbered 1, 2, 3 in the order you click. Hold the Ping key and click a tile that
  already has one of your markers to remove it; the others keep their numbers. Numbers keep counting up while you have
  any marker. Once none are left (removed, cleared or timed out) the next marker is 1 again.
- **Time.** Each marker lasts "Markers last" seconds (1 to 120, default 10), then it is gone. It fades out over its
  last "Fade time" seconds (see below).
- **Limit.** At most 20 of your markers at once. A 21st removes your oldest.
- **Clear key (optional).** A second way to remove all your markers, from your party's screens too.
- **Right-click clears (on by default).** Holding the Ping key and right-clicking removes all your markers, and the
  right-click menu does not open while the key is held. Turn it off in the settings to get the menu back.
- **Logout and world hop** remove all markers.

## What a marker looks like

- **Tile shows** is one menu of 16 choices, each with a small picture of it. A marker can show its **Number** (the
  default) or the seconds it has left (**Timer**), alone or with a clock of how much of its time is left; a clock
  alone; or **Nothing**, just the coloured tile. The clocks: a **pie** in the middle that empties, a **ring** that
  runs down, the tile's own **border** running down, a **sweep** that wipes round the tile, or a small pie in the
  **corner**. The choices, in menu order: Number, Number + pie, Number + ring, Number + border, Number + sweep,
  Number + corner, Timer, Timer + ring, Timer + border, Timer + sweep, Timer + corner, Nothing, Pie, Ring, Border,
  Sweep. With no number or timer, the placer's name is hidden too. A party member's markers count down and run
  their clocks on their own time, as they set it.
- **Timer decimals** (0, 1 or 2) sets the timer's digits after the point. It rounds up, so it reads 1 until the marker
  is gone and never 0 while it is still there: 3.84 seconds shows as 4, 3.9 or 3.84.
- **Outline thickness** (1 to 6 pixels) and **Tile fill** (0 to 100 percent) set the outline and how filled the tile is.
- **Fade time** (0 to 5 seconds, default 2) is how long a marker takes to fade out at the end of its time. A marker
  that lasts less than that fades over its whole life. 0 keeps it solid until it vanishes.
- **Large numbers** draws the number or the timer bigger. **Show names** adds a party member's name after theirs.
- Your own markers are drawn in **Marker colour**; a party member's in their colour, swapped as described under Party.
- **Timer & number** sets the colour of every marker's timer, number, clock and name: **Marker colour** (the default,
  each marker's own), White, Black, Yellow, Cyan, Green, Pink, or **Custom**, which uses the **Custom colour** picker.
  Each choice has a small colour square in the menu; Custom's shows the Custom colour you picked. One colour is easier to read on a busy floor. The tile's outline
  and fill always keep the marker's colour, so you can still tell whose marker it is.
- Changing any look setting (including Fade time) puts a sample marker numbered 1 under your character for 5 seconds
  and then its fade, so you see the new look at once. It is on your screen only, never shared, and does not take a
  number from your markers. RuneLite saves a colour when its picker closes, so the sample shows after you close it.
- How a marker looks is each player's own choice. Only the tile, number, colour and time are shared, so you and a party
  member can each show the same marker their own way.

## Party

Make or join a party with RuneLite's own Party plugin. With "Share with party" on, each marker you place, remove
or clear is sent through RuneLite's party server, and nowhere else. Party members who have 2h Timed Tiles see your
markers with your number, for the duration you set. Each party member's markers are drawn in the colour they chose,
so you can tell who placed what. If a colour looks the same as yours or as another member's (everyone on the default,
say), the plugin shows that member in a clearly different colour on your screen, and switches to their real colour as
soon as it no longer clashes. Turn on "Show names" to also write the placer's name next to the number or timer.
Members without the plugin see nothing.

- **Show party markers** off hides your party's markers at once. It does not stop yours from being shared; that is
  "Share with party".
- Sharing pauses in the Wilderness and on PvP worlds (Deadman, PvP, bounty, high risk, Last Man Standing and PvP
  arena). Your markers then stay on your own screen only.
- A party member's markers go when they leave the party. All party markers go when you leave or change party.
- Inside instances, markers are shared by their place in the instance's template, so party members in the same
  instance see the same tile.

## Settings

The settings sit in three sections, all open: Placing markers, Look and Party (since 1.1.0; no key changed, so 1.0.1 choices carry over even where a name changed: My colour is now Marker colour, Fade length is Fade time, Share with my party is Share with party).

| Setting | Default | What it does |
|---|---|---|
| **Placing markers** | | |
| Ping key | not set | Hold it and left-click the ground to place a marker. Avoid your Party ping key. |
| Clear key | not set | Removes all your markers. |
| Right-click clears | on | Ping key + right-click removes all your markers (the menu does not open). |
| Markers last | 10 seconds | How long each marker stays (1 to 120). |
| Fade time | 2 seconds | How long the fade-out takes (0 to 5). 0 = no fade. |
| **Look** | | |
| Marker colour | orange-gold | Your markers' colour. Your party sees it too, unless it looks the same as theirs. |
| Tile fill | 6% | How filled the tile is. 0 = outline only. |
| Outline thickness | 2 px | How thick the tile outline is (1 to 6). |
| Tile shows | Number | What each marker shows: number or timer, alone or with a clock, a clock alone, or nothing. 16 choices, each with a picture. |
| Timer & number | Marker colour | Colour of the timer, number and clock: Marker colour, White, Black, Yellow, Cyan, Green, Pink or Custom. The outline and fill keep the marker's colour. |
| Custom colour | purple | Used when Timer & number is Custom. |
| Timer decimals | 0 | Digits after the point on the timer (0 to 2). It rounds up, so it never reads zero while the marker is there. |
| Large numbers | off | Draws the number and timer bigger. |
| **Party** | | |
| Share with party | on | Your party sees your markers. Off in the Wilderness and on PvP worlds. |
| Show party markers | on | Shows your party's markers. |
| Show names | off | Shows who placed each party marker. Hidden when the tile shows no number or timer. |

Once per RuneLite run, at login, if the Ping key is not set, the chat shows one line: "2h Timed Tiles: choose a ping
key in its settings."

## Good to know

- **Privacy.** Nothing about you is sent anywhere except the marker's tile, number, colour and duration, to your own
  party through RuneLite's party server, and only while you are in a party with sharing on.
- **Something wrong?** Tell us in the 2hBuilds Discord - the link is on the plugin's Hub page.
