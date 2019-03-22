package game.content.skilling.summoning.familiar;

import game.npc.Npc;

/**
 * Represents a summoning familiar
 *
 * @author 2012
 */
public class SummoningFamiliarNpc extends Npc {

	/**
	 * The summoning familiar details
	 */
	private SummoningFamiliar familiar;

	/**
	 * Represents a summoning familiar
	 *
	 * @param npcId the npc id
	 * @param familiar the familiar
	 */
	public SummoningFamiliarNpc(int npcId, SummoningFamiliar familiar) {
		super(npcId, familiar.getNpc());
		this.setFamiliar(familiar);
	}

	/**
	 * Sets the familiar
	 *
	 * @return the familiar
	 */
	public SummoningFamiliar getFamiliar() {
		return familiar;
	}

	/**
	 * Sets the familiar
	 *
	 * @param familiar the familiar
	 */
	public void setFamiliar(SummoningFamiliar familiar) {
		this.familiar = familiar;
	}
}
