package game.npc.impl.scorpia;

import core.Server;
import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-01-05 at 11:03 AM
 */
//@CustomNpcComponent(id = 6615, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 6615))
public class Scorpia extends Npc {

	/**
	 * The strategy for this boss.
	 */
	private final ScorpiaCombatStrategy strategy = new ScorpiaCombatStrategy(this);

	private long lastHeal = System.nanoTime();

	public Scorpia(int npcId, int type) {
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
		return new Scorpia(index, npcType);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		if (strategy.getSpawnState() == ScorpiaSpawnState.UNSPAWNED) {
			return;
		}
		long nanoTime = System.nanoTime();

		long elapsedSecondsSinceHeal = TimeUnit.NANOSECONDS.toSeconds(nanoTime - lastHeal);

		List<Npc> guardians = strategy.getGuardians();

		if (!isDead()) {
			guardians.stream().filter(npc -> npc.getFollowingType() == null).forEach(npc -> Server.npcHandler.followNpc(npc.npcIndex, npcIndex));
		}
		if (elapsedSecondsSinceHeal >= 5) {
			if (isDead()) {
				guardians.forEach(Npc::killIfAlive);
				return;
			}
			if (strategy.getGuardians().stream().allMatch(npc -> npc.distanceTo(getX(), getY()) > 6)) {
				if (elapsedSecondsSinceHeal >= 8) {
					guardians.forEach(Npc::killIfAlive);
				}
				return;
			}
			lastHeal = nanoTime;
			heal(35);
		}
	}

	/**
	 * A listener function that is referenced when the non-playable character first dies, at the start of the
	 * animation.
	 */
	@Override
	public void onDeath() {
		super.onDeath();

		strategy.getGuardians().stream().filter(Objects::nonNull).forEach(Npc::killIfAlive);
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
