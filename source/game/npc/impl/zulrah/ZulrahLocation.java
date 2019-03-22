package game.npc.impl.zulrah;

import game.position.Position;

public enum ZulrahLocation {
	NORTH(new Position(2266, 3072)),
	SOUTH(new Position(2266, 3063)),
	WEST(new Position(2258, 3072)),
	EAST(new Position(2277, 3072));

	private final Position location;

	ZulrahLocation(Position location) {
		this.location = location;
	}

	public Position getLocation() {
		return location;
	}

}
