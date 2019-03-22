package game.npc.impl.corporeal_beast;

import core.Server;
import core.GameType;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;

import java.util.Comparator;

/**
 * Created by Jason MacKeigan on 2018-02-20 at 10:44 AM
 */
//@CustomNpcComponent(id = 320, type = GameType.OSRS)
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type=GameType.OSRS, identity = 320)
})
public class DarkEnergyCore extends Npc {

	private final EntityCombatStrategy combatStrategy = new DarkEnergyCoreCombatStrategy();

	public DarkEnergyCore(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		super.setNeverRandomWalks(true);
		super.setMovementState(MovementState.DISABLED);
		super.setWalkingHomeDisabled(true);
		super.setAttackableWhileSamePosition(true);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new DarkEnergyCore(index, npcType);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		if (getKillerId() == 0) {
			Player closest = Server.npcHandler.findPlayerToAttack(this).stream().min(Comparator.comparing(a -> a.distanceToPoint(getX(), getY()))).orElse(null);

			if (closest == null) {
				return;
			}
			setKillerId(closest.getPlayerId());
		}
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
