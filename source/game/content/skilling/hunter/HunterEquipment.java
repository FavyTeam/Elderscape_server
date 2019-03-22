package game.content.skilling.hunter;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 9:42 AM
 */
public enum HunterEquipment {
	NOOSE_WAND(1, 10150),

	BIRD_SNARE(1, 10006),

	BUTTERFLY_NET(1, 10010),

	BUTTERFLY_JAR(15, 10012),

	BOX_TRAP(27, 10008),

	RABBIT_SNARE(27, 10031),

	TEASING_STICK(31, 10029),

	MAGIC_BOX(71, 10026);

	private final int levelRequired;

	private final int itemId;

	HunterEquipment(int levelRequired, int itemId) {
		this.levelRequired = levelRequired;
		this.itemId = itemId;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public int getItemId() {
		return itemId;
	}
}
