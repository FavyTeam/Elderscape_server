package game.npc.impl.kalphite_queen;

/**
 * Created by Jason MacKeigan on 2018-01-12 at 10:08 AM
 */
public enum KalphiteQueenPhase {
	UNTRANSFORMED(963),

	TRANSFORMING(963),

	TRANSFORMED(965);

	private final int id;

	KalphiteQueenPhase(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
