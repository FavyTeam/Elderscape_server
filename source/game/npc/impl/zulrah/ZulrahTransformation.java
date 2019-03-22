package game.npc.impl.zulrah;

/**
 * Created by Jason MacKeigan on 2018-04-02 at 1:56 PM
 */
public enum ZulrahTransformation {
	MELEE(2043),

	MAGIC(2044),

	RANGE(2042);

	private final int npcId;

	ZulrahTransformation(int npcId) {
		this.npcId = npcId;
	}

	public int getNpcId() {
		return npcId;
	}
}
