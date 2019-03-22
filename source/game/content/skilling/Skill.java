package game.content.skilling;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 10:09 AM
 */
public enum Skill {
	ATTACK(0),
	DEFENSE(1),
	STRENGTH(2),
	HITPOINTS(3),
	RANGED(4),
	PRAYER(5),
	MAGIC(6),
	COOKING(7),
	WOODCUTTING(8),
	FLETCHING(9),
	FISHING(10),
	FIREMAKING(11),
	CRAFTING(12),
	SMITHING(13),
	MINING(14),
	HERBLORE(15),
	AGILITY(16),
	THIEVING(17),
	SLAYER(18),
	FARMING(19),
	RUNECRAFTING(20),
	HUNTER(21);

	private final int id;

	Skill(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
