package game.npc.impl.scorpia;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-01-08 at 10:32 AM
 */
//@CustomNpcComponent(id = 6617, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 6617))
public class ScorpiaGuardian extends Npc {

	/**
	 * The strategy for this
	 */
	private final EntityCombatStrategy combatStrategy = new ScorpiaGuardianCombatStrategy();

	/**
	 * The boss associated with these guardians.
	 */
	private Scorpia scorpia;

	public ScorpiaGuardian(int npcId, int type) {
		super(npcId, type);
		setNeverRandomWalks(true);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new ScorpiaGuardian(index, npcType);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {

	}

	/**
	 * Sets the boss for this guardian.
	 *
	 * @param scorpia the parent scorpia.
	 */
	public void setScorpia(Scorpia scorpia) {
		if (this.scorpia != null || scorpia == null) {
			throw new IllegalArgumentException("Scorpia already assigned or value provided is null.");
		}
		this.scorpia = scorpia;
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return combatStrategy;
	}
}
