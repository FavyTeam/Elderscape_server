package game.npc.impl.abyssal_sire;

/**
 * Created by Jason MacKeigan on 2018-02-21 at 3:07 PM
 */
public enum AbyssalSirePhase {
	SLEEPING(0),

	AWAKE(1),

	DISORIENTED(2),

	WALKING_SOUTH(3),

	MELEE_COMBAT(4),

	WALKING_TO_CENTER(5),

	MAGIC_COMBAT(6),

	WALKING_HOME(7);

	private final int order;

	AbyssalSirePhase(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
}
