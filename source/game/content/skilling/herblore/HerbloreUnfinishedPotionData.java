package game.content.skilling.herblore;

/**
 * @author Joshua Barry <Ares>
 */
public enum HerbloreUnfinishedPotionData {

	/**
	 * Guam leaf.
	 */
	GUAM(249, 91, 3),
	/**
	 * Marrentill leaf.
	 */
	MARRENTILL(251, 93, 5),
	/**
	 * Tarromin leaf.
	 */
	TARROMIN(253, 95, 12),
	/**
	 * Harralander leaf.
	 */
	HARRALANDER(255, 97, 22),
	/**
	 * Ranarr leaf.
	 */
	RANARR(257, 99, 30),
	/**
	 * Toadflax leaf.
	 */
	TOADFLAX(2998, 3002, 34),
	/**
	 * Irit leaf.
	 */
	IRIT(259, 101, 45),
	/**
	 * Avantoe leaf.
	 */
	AVANTOE(261, 103, 50),
	/**
	 * Kwuarm leaf.
	 */
	KWUARM(263, 105, 55),
	/**
	 * Snapdragon leaf.
	 */
	SNAPDRAGON(3000, 3004, 63),
	/**
	 * Cadantine leaf.
	 */
	CADANTINE(265, 107, 66),
	/**
	 * Dwarf Weed leaf.
	 */
	DWARF_WEED(267, 109, 72),
	/**
	 * Lantadyme potion (unf)
	 */
	LANTADYME(2481, 2483, 76),
	/**
	 * Torstol leaf.
	 */
	TORSTOL(269, 111, 78);

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The reward.
	 */
	private int reward;

	/**
	 * The level.
	 */
	private int level;

	private HerbloreUnfinishedPotionData(int id, int reward, int level) {
		this.id = id;
		this.level = level;
		this.reward = reward;
	}

	/**
	 * Gets the id.
	 *
	 * @return The id.
	 */
	public int getCleanedId() {
		return id;
	}

	/**
	 * Gets the required level.
	 *
	 * @return The required level.
	 */
	public int getRequiredLevel() {
		return level;
	}

	/**
	 * Gets the reward.
	 *
	 * @return The reward.
	 */
	public int getUnfinishedId() {
		return reward;
	}

}
