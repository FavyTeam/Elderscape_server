package game.npc.impl.corporeal_beast;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-02-16 at 4:56 PM
 */
//@CustomNpcComponent(id = 319, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 319))
public class CorporealBeast extends Npc {

	private final CorporealBeastCombatStrategy strategy = new CorporealBeastCombatStrategy();

	public CorporealBeast(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new CorporealBeast(index, npcType);
	}

	/**
	 * A listener function that is referenced when the non-playable character first dies, at the start of the
	 * animation.
	 */
	@Override
	public void onDeath() {
		super.onDeath();

		Npc core = strategy.getDarkEnergyCore();

		if (core != null && !core.isDead() && !core.applyDead) {
			core.setDead(true);
		}
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		Npc core = strategy.getDarkEnergyCore();

		if (System.currentTimeMillis() - lastDamageTaken > 30_000 && core != null && !core.isDead() && !core.applyDead) {
			core.setDead(true);
		}
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return strategy;
	}
}
