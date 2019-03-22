package game.content.minigame.impl.fight_pits;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 9:32 AM
 */
public enum FightPitsMonster {
	TZ_KIH(2189),
	TZ_KEK(2191);

	private final int id;

	FightPitsMonster(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
