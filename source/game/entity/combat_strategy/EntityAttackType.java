package game.entity.combat_strategy;

/**
 * Created by Jason MacKeigan on 2018-01-05 at 5:15 PM
 * <p>
 * Represents a certain style of attack made by non-playable characters.
 */
public enum EntityAttackType {
	MELEE(0),

	RANGED(1),

	MAGIC(2),

	DRAGON_FIRE(3),

	WYVERN(4);

	private final int id;

	EntityAttackType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
