package game.content.minigame.impl.fight_pits;

import game.position.Position;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 9:37 AM
 */
public enum FightPitsRegion {
	NORTH(new Position(2397, 5159, 0)),
	SOUTH(new Position(2399, 5142, 0)),
	EAST(new Position(2412, 5152, 0));

	private final Position position;

	FightPitsRegion(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}
}
