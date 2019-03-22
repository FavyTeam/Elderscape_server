package game.npc.impl.barrelchest;

import core.ServerConstants;
import game.content.combat.Combat;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.impl.TimedCameraShakeReset;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 1:03 PM
 */
public class BarrelchestCombatStrategy extends NpcCombatStrategy {

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender
	 */
	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON) {
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 50, -1));
			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 100, 856, 150, 20, 0, 45, 30);
		} else if (attackerAsNpc.attackType == ServerConstants.MELEE_ICON) {
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 31, -1));
		}
	}

	/**
	 * The custom damage that should be dealt, or -1 if the parent damage should be taken into consideration.
	 *
	 * @param attacker the attacker dealing the damage.
	 * @param defender the defender taking the damage.
	 * @param entityAttackType
	 * @return the custom calculation of damage, or -1 if the parent damage should be used instead.
	 */
	@Override
	public int calculateCustomDamage(Entity attacker, Entity defender, int entityAttackType) {
		if (entityAttackType != ServerConstants.MAGIC_ICON) {
			return -1;
		}
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Player playerDefender = (Player) defender;

			if (playerDefender.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
				return 0;
			}
			playerDefender.getPA().sendShakeCamera(3, 3, 2, 2);
			playerDefender.getEventHandler().addEvent(playerDefender, new TimedCameraShakeReset(), 5);
			return ThreadLocalRandom.current().nextInt(20, 51);
		}
		return -1;
	}

	/**
	 * Referenced when damage is applied to the defender from the attacker.
	 *
	 * @param attacker the entity damaging the defender.
	 * @param defender the entity being damaged by the attacker.
	 * @param damage
	 */
	@Override
	public void onDamageDealt(Entity attacker, Entity defender, int damage, int attackType) {
		if (damage <= 0) {
			return;
		}
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}

		Player defenderAsPlayer = (Player) defender;

		int reduction = Math.max(0, (int) ((double) damage / (attackType == ServerConstants.MAGIC_ICON ? 2D : 4D)));

		if (reduction == 0) {
			return;
		}

		Combat.applyPrayerReduction(defenderAsPlayer, reduction);


	}

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @param attacker
	 * @param defender
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		if (ThreadLocalRandom.current().nextInt(0, 100) <= 20) {
			return ServerConstants.MAGIC_ICON;
		}
		return ServerConstants.MELEE_ICON;
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

	/**
	 * Determines if a boss should perform a block operation.
	 *
	 * @return true by default.
	 */
	@Override
	public boolean performsBlockAnimation() {
		return false;
	}
}
