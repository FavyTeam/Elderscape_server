package game.content.skilling.fishing;

import java.util.Arrays;

/**
 * The fish
 */
public enum Fish {
	SHRIMP(317, 1, 10, 621, 303, -1, 1),

	ANCHOVIES(321, 15, 40, 621, 303, -1, 1),

	TROUT(335, 20, 50, 622, 309, 314, 1),

	SALMON(331, 30, 70, 622, 309, 314, 1),

	TUNA(359, 35, 80, 618, 311, -1, 2),

	LOBSTER(377, 40, 85, 619, 301, -1, 3),

	SWORDFISH(371, 50, 90, 618, 311, -1, 3),

	MONKFISH(7944, 62, 100, 620, 303, -1, 4),

	KARAMBWAN(3142, 65, 120, 1193, 3159, -1, 5),

	SHARK(383, 76, 140, 618, 311, -1, 5),

	DARK_CRAB(11934, 85, 190, 619, 301, 11940, 8),

	ANGLERFISH(13439, 90, 200, 309, 303, 13431, 8);

	private int itemId, level, experience, animation, equipment, bait, timer;

	private Fish(int itemId, int level, int experience, int animation, int equipment, int bait,
			int timer) {
		this.itemId = itemId;
		this.level = level;
		this.experience = experience;
		this.animation = animation;
		this.equipment = equipment;
		this.bait = bait;
		this.timer = timer;
	}

	public int getItemId() {
		return itemId;
	}

	public int getRequiredLevel() {
		return level;
	}

	public int getExperience() {
		return experience;
	}

	public int getAnimation() {
		return animation;
	}

	public int getEquipment() {
		return equipment;
	}

	public int getBait() {
		return bait;
	}

	public int getTimer() {
		return timer;
	}

	public static Fish forId(int id) {
		return Arrays.stream(values()).filter(c -> c.getItemId() == id).findFirst().orElse(null);
	}
}