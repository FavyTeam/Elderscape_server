package game.npc.impl.demonic_gorilla;

/**
 * Created by Jason MacKeigan on 2018-01-16 at 5:53 PM
 * <p>
 * Represents each of the individual phases or transformations that a demonic gorilla will go
 * through. Each phase has a specific
 */
public enum DemonicGorillaTransformation {
	MELEE(7144),
	RANGE(7145),
	MAGIC(7146);

	private final int npcId;

	DemonicGorillaTransformation(int npcId) {
		this.npcId = npcId;
	}

	public int getNpcId() {
		return npcId;
	}
}
