package game.content.skilling.herblore;


/**
 * <p>
 * Contains all data relevant to a herb
 * </p>
 *
 * @author Joshua Barry <Ares>
 */
public enum HerbloreCleaningData {
	/**
	 * Guam
	 */
	GUAM(199, 249, 1, 2.5),
	/**
	 * Marrentill
	 */
	MARRENTILL(201, 251, 5, 3.8),
	/**
	 * Tarromin
	 */
	TARROMIN(203, 253, 11, 5),
	/**
	 * Harralander
	 */
	HARRALANDER(205, 255, 20, 6.3),
	/**
	 * Ranaar
	 */
	RANARR(207, 257, 25, 7.5),


	TOAD_FLAX(3049, 2998, 30, 8),
	/**
	 * Irit
	 */
	IRIT(209, 259, 40, 8.8),
	/**
	 * Avantoe
	 */
	AVANTOE(211, 261, 48, 10),
	/**
	 * Kuarm
	 */
	KWUARM(213, 263, 54, 11.3),

	SNAP_DRAGON(3051, 3000, 59, 12),

	/**
	 * Cadantine
	 */
	CADANTINE(215, 265, 65, 12.5),


	LANTADYME(2485, 2481, 67, 13),

	/**
	 * Dwarf Weed
	 */
	DWARF_WEED(217, 267, 70, 13.8),
	/**
	 * Torstol
	 */
	TORSTOL(219, 269, 75, 15);

	/**
	 * The id of the herb
	 */
	private int id;

	/**
	 * The reward for identifying the herb.
	 */
	private int cleanedId;

	/**
	 * The level required to identify this herb.
	 */
	private int level;

	/**
	 * The experience granted for identifying the herb.
	 */
	private double experience;

	private HerbloreCleaningData(int id, int reward, int level, double experience) {
		this.id = id;
		this.cleanedId = reward;
		this.level = level;
		this.experience = experience;
	}

	/**
	 * @return the experience
	 */
	public double getExperience() {
		return experience;
	}

	/**
	 * @return the id
	 */
	public int getGrimyId() {
		return id;
	}

	/**
	 * @return the level
	 */
	public int getRequiredLevel() {
		return level;
	}

	/**
	 * @return the reward
	 */
	public int getCleanedId() {
		return cleanedId;
	}
}
