package com.timedtiles;

/**
 * How a marker shows the share of its time it has left, under whatever is written on it: no clock, a pie in the
 * middle, a ring, the tile's own outline running down, a sweep across the whole tile, or a small pie in the corner.
 * Chosen through {@link TileLabel}; not a setting of its own.
 */
public enum ClockStyle
{
	OFF,
	PIE,
	RING,
	BORDER,
	SWEEP,
	CORNER
}
