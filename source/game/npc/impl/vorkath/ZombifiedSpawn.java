package game.npc.impl.vorkath;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsnpc.CombatNpc;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.player.Player;
import game.player.PlayerHandler;
import game.type.GameTypeIdentity;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-02-07 at 11:36 PM
 */
//@CustomNpcComponent(id = 8062, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 8062))
public class ZombifiedSpawn extends Npc {

	private final ZombifiedSpawnCombatStrategy combatStrategy = new ZombifiedSpawnCombatStrategy();

	public ZombifiedSpawn(int npcId, int type) {
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
		return new ZombifiedSpawn(index, npcType);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		setClippingIgnored(true);
		setNeverRandomWalks(true);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();

		int killerId = getKillerId();

		if (killerId < 0 || killerId >= PlayerHandler.players.length - 1) {
			return;
		}
		Player player = PlayerHandler.players[killerId];

		if (player == null) {
			return;
		}
		Server.npcHandler.followPlayer(npcIndex, player.getPlayerId());

		if (!isDead() && player.distanceToPoint(getX(), getY()) <= 1 && getMoveX() == 0 && getMoveY() == 0) {
			CombatNpc.applyHitSplatOnNpc(player, this, getCurrentHitPoints(), ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 0);
			setMovementState(MovementState.DISABLED);
			setClippingIgnored(false);
		}
	}

	/**
	 * A listener function that is referenced when the non-playable character first dies, at the start of the
	 * animation.
	 */
	@Override
	public void onDeath() {
		super.onDeath();

		int killerId = getKilledBy();

		Player player = PlayerHandler.players[killerId];

		if (player == null) {
			return;
		}
		if (player.distanceToPoint(getX(), getY()) <= 1) {
			Combat.createHitsplatOnPlayerNormal(player, ThreadLocalRandom.current().nextInt(1, 61),
			                                    ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
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
