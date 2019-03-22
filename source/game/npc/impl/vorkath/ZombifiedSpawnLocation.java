package game.npc.impl.vorkath;

import game.position.Position;

/**
 * Created by Jason MacKeigan on 2018-02-08 at 9:58 AM
 */
public enum ZombifiedSpawnLocation {
	SOUTH_WEST(new Position(2267, 4060, 0)),
	SOUTH_EAST(new Position(2277, 4060, 0)),
	NORTH_EAST(new Position(2277, 4070, 0)),
	NORTH_WEST(new Position(2267, 4070, 0));

	private final Position position;

	ZombifiedSpawnLocation(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}
}
