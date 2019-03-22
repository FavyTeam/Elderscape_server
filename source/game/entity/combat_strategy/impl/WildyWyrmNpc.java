package game.entity.combat_strategy.impl;

import java.util.concurrent.ThreadLocalRandom;
import core.GameType;
import core.ServerConstants;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.type.GameTypeIdentity;

/**
 * Handles the Wyldy Wyrm npc combat strategy
 * 
 * @author 2012
 *
 */
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type = GameType.PRE_EOC, identity = {3334, 9463})
})
public class WildyWyrmNpc extends Npc implements EntityCombatStrategy {


	/**
	 * 12791 melee
	 * 
	 * 12792 block
	 * 
	 * 12793 death
	 * 
	 * 12794 mage/ range
	 * 
	 * 12795 re-enter
	 * 
	 * 12796 disapear
	 * 
	 * 2314 gfx?
	 */

	/**
	 * Represents the wildy wyrm
	 * 
	 * @param npcId the npc id
	 * @param npcType the npc type
	 */
	public WildyWyrmNpc(int npcId, int npcType) {
		super(npcId, npcType);
	}

	@Override
	public Npc copy(int index) {
		return new WildyWyrmNpc(index, npcType);
	}

	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return this;
	}

	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return -1;
		}
		int randomResult = ThreadLocalRandom.current().nextInt(0, 100);

		if (randomResult <= 60) {
			return ServerConstants.MAGIC_ICON;
		}
		if (randomResult <= 20) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) <= 3) {
				return ServerConstants.MELEE_ICON;
			}
		}
		return ServerConstants.RANGED_ICON;
	}

	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return;
		}
		Npc npc = (Npc) attacker;

		Player player = (Player) defender;
		player.getPA().sendMessage("this");
		int attackType = npc.attackType;
		player.getPA().createPlayersProjectile(npc, player, 50, 95, 2314, 43, 0,
				-player.getPlayerId() - 1, 45, 30);

		if (attackType == ServerConstants.MAGIC_ICON) {
			DamageQueue.add(new Damage(player, npc, ServerConstants.MAGIC_ICON,
						4, 31, -1, null, (d, p) -> p.gfx0(281)));
		} else if (attackType == ServerConstants.RANGED_ICON) {
			player.getPA().createPlayersProjectile(npc, player, 50, 95, 473, 43, 0,
					-player.getPlayerId() - 1, 45, 30);
			DamageQueue.add(
					new Damage(player, npc, ServerConstants.RANGED_ICON, 4, 31, -1));
		} else if (attackType == ServerConstants.MELEE_ICON) {
			DamageQueue.add(
					new Damage(player, npc, ServerConstants.MAGIC_ICON, 2, 31, -1));
		}
	}

	@Override
	public EntityType getAttackerType() {
		return EntityType.NPC;
	}

	@Override
	public EntityType getDefenderType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isCustomAttack() {
		return true;
	}
}
