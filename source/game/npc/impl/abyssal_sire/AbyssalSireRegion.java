package game.npc.impl.abyssal_sire;

import game.position.Position;


public enum AbyssalSireRegion {
	NORTH_EAST(new Position(3105, 4839)),
	NORTH_WEST(new Position(2980, 4839)),
	SOUTH_EAST(new Position(3110, 4775)),
	SOUTH_WEST(new Position(2970, 4775));

	private final Position center;

	private final Position sire;

	private final Position tentacleNorthWest;

	private final Position tentacleNorthEast;

	private final Position tentacleWest;

	private final Position tentacleEast;

	private final Position tentacleSouthWest;

	private final Position tentacleSouthEast;

	private final Position respiratorySystemNorthWest;

	private final Position respiratorySystemNorthEast;

	private final Position respiratorySystemSouthWest;

	private final Position respiratorySystemSouthEast;

	private final Position phaseTwoCenter;

	AbyssalSireRegion(Position center) {
		this.center = center;

		sire = center.translate(-3, 16);
		tentacleNorthWest = center.translate(-13, 5);
		tentacleNorthEast = center.translate(4, 5);
		tentacleWest = center.translate(-10, -4);
		tentacleEast = center.translate(2, -4);
		tentacleSouthWest = center.translate(-12, -13);
		tentacleSouthEast = center.translate(5, -13);

		respiratorySystemNorthWest = center.translate(-16, 5);
		respiratorySystemNorthEast = center.translate(12, 4);
		respiratorySystemSouthWest = center.translate(-13, -5);
		respiratorySystemSouthEast = center.translate(15, -6);

		phaseTwoCenter = center.translate(0, 6);
	}

	public Position getCenter() {
		return center;
	}

	public Position getSire() {
		return sire;
	}

	public Position getTentacleNorthWest() {
		return tentacleNorthWest;
	}

	public Position getTentacleNorthEast() {
		return tentacleNorthEast;
	}

	public Position getTentacleWest() {
		return tentacleWest;
	}

	public Position getTentacleEast() {
		return tentacleEast;
	}

	public Position getTentacleSouthWest() {
		return tentacleSouthWest;
	}

	public Position getTentacleSouthEast() {
		return tentacleSouthEast;
	}

	public Position getRespiratorySystemNorthWest() {
		return respiratorySystemNorthWest;
	}

	public Position getRespiratorySystemNorthEast() {
		return respiratorySystemNorthEast;
	}

	public Position getRespiratorySystemSouthWest() {
		return respiratorySystemSouthWest;
	}

	public Position getRespiratorySystemSouthEast() {
		return respiratorySystemSouthEast;
	}

	public Position getPhaseTwoCenter() {
		return phaseTwoCenter;
	}
}
