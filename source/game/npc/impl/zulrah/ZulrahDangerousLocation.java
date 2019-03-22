package game.npc.impl.zulrah;

import game.position.Position;

public enum ZulrahDangerousLocation {
	SOUTH_WEST(new Position(2262, 3069), new Position(2265, 3068)),
	SOUTH_EAST(new Position(2268, 3068), new Position(2271, 3069)),
	EAST(new Position(2272, 3071), new Position(2272, 3074)),
	WEST(new Position(2262, 3071), new Position(2262, 3074));

	private Position[] points;

	ZulrahDangerousLocation(Position... points) {
		this.points = points;
	}

	public Position[] getPositions() {
		return points;
	}

}
