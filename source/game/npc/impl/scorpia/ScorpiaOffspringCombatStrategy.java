package game.npc.impl.scorpia;

import core.ServerConstants;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-01-11 at 9:44 AM
 */
public class ScorpiaOffspringCombatStrategy implements EntityCombatStrategy {
	/**
	 * The type of the attacker.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getAttackerType() {
		return EntityType.NPC;
	}

	/**
	 * The type of the defender.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getDefenderType() {
		return EntityType.PLAYER;
	}

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender
	 */
	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (attackerAsNpc.attackType == ServerConstants.RANGED_ICON) {
			int damage = NpcHandler.calculateNpcRangedData(attackerAsNpc, defenderAsPlayer, -1, 1);
			if (damage > 0) {
				Combat.applyPrayerReduction(null, defenderAsPlayer, 1, false);
			}
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 3, 2, damage, null));
		}
	}

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		return ServerConstants.RANGED_ICON;
	}

	/**
	 * Determines if we're going to handle the entire attack process our self.
	 *
	 * @return {@code true} if it's a custom attack, by default, false.
	 */
	@Override
	public boolean isCustomAttack() {
		return true;
	}
}
