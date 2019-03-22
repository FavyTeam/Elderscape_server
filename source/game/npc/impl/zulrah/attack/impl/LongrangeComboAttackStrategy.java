package game.npc.impl.zulrah.attack.impl;

import game.entity.Entity;

import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-17 at 2:27 PM
 */
public class LongrangeComboAttackStrategy extends LongrangeAttackStrategy {

	private final List<LongrangeAttackStrategy> strategies;

	private LongrangeAttackStrategy strategy;

	public LongrangeComboAttackStrategy(List<LongrangeAttackStrategy> strategies) {
		if (strategies.isEmpty()) {
			throw new IllegalArgumentException("List of strategies cannot be empty.");
		}
		this.strategies = strategies;
		this.strategy = strategies.get(0);
	}

	@Override
	public void afterCustomAttack(Entity attacker, Entity defender) {
		int indexOf = strategies.indexOf(strategy);

		strategy = strategies.get(indexOf + 1 >= strategies.size() ? 0 : indexOf + 1);
	}

	@Override
	int getMaximumNumberOfAttacks() {
		return 10;
	}

	@Override
	int getProjectile() {
		return strategy.getProjectile();
	}

	@Override
	int getAttackType() {
		return strategy.getAttackType();
	}
}
