package game.npc.impl.lizard_shaman;

import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Jason MacKeigan on 2018-01-17 at 11:07 AM
 */
public class LizardShamanCombatStrategy extends NpcCombatStrategy {

	/**
	 * Determines whether or not the entity is currently executing a special attack.
	 */
	private LizardShamanSpecialAttack specialAttack;

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		if (specialAttack == LizardShamanSpecialAttack.JUMP) {
			return false;
		}
		return true;
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
		int result = ThreadLocalRandom.current().nextInt(0, 100);

		if (result <= 25) {
			specialAttack = Misc.random(LizardShamanSpecialAttack.values());

			if (specialAttack == LizardShamanSpecialAttack.JUMP) {
				if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
					Npc attackerAsNpc = (Npc) attacker;

					Player defenderAsPlayer = (Player) defender;

					if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) > 3) {
						specialAttack = Misc.random(Arrays.asList(LizardShamanSpecialAttack.GREEN_ACID, LizardShamanSpecialAttack.SPAWN));
					}
				}
			}

			return ServerConstants.MAGIC_ICON;
		}
		return ServerConstants.RANGED_ICON;
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
		if (specialAttack == LizardShamanSpecialAttack.JUMP || specialAttack == LizardShamanSpecialAttack.GREEN_ACID) {
			return ThreadLocalRandom.current().nextInt(20, 26);
		}
		return -1;
	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		if (specialAttack != null) {
			switch (specialAttack) {
				case JUMP:
					return 7152;
				case SPAWN:
					return 7157;
				case GREEN_ACID:
					return 7193;
			}
		}
		return 7193;
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
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (specialAttack == LizardShamanSpecialAttack.SPAWN) {
			IntStream.range(0, 3).forEach(i -> {
				Npc npc = NpcHandler.spawnNpc(6768, attackerAsNpc.getX(), attackerAsNpc.getY(), attackerAsNpc.getHeight());

				if (npc instanceof LizardShamanSpawn) {
					LizardShamanSpawn spawn = (LizardShamanSpawn) npc;

					spawn.setTarget(defenderAsPlayer);
				}
			});
		} else if (specialAttack == LizardShamanSpecialAttack.GREEN_ACID) {
			Position damageLocation = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, damageLocation, 50, 70, 1293, 130, 0, 0, 45, 30);

			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 4, 25, -1, p -> p.samePosition(damageLocation)));

			defenderAsPlayer.getPA().createPlayersStillGfx(1294, damageLocation.getX(), damageLocation.getY(), damageLocation.getZ(), 95);
		} else if (specialAttack == LizardShamanSpecialAttack.JUMP) {
			Position damageLocation = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

			attackerAsNpc.getEventHandler().addEvent(defenderAsPlayer, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
					attackerAsNpc.requestAnimation(6946);

					if (defenderAsPlayer.distanceToPoint(damageLocation.getX(), damageLocation.getY()) <= 2) {
						int jumpDamage = Misc.random(20, 25);
						DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 1, 25, jumpDamage));
					}
				}

				@Override
				public void stop() {

				}
			}, 6);
		} else if (specialAttack == null) {
			if (attackerAsNpc.attackType == ServerConstants.RANGED_ICON) {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 80, 1291, 130, 0, -defenderAsPlayer.getPlayerId() - 1, 45, 30);

				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 3, 31, -1));
			}
		}
	}

	/**
	 * Referenced only if {@link #isCustomAttack()} returns true, in which case we can change some
	 * important combat related information to the attacker and defender, like attack speed.
	 *
	 * @param attacker the entity making the attack.
	 * @param defender
	 */
	@Override
	public void afterCustomAttack(Entity attacker, Entity defender) {
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		if (specialAttack == LizardShamanSpecialAttack.JUMP) {
			attackerAsNpc.attackTimer = 10;
		}
		specialAttack = null;
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
		if (specialAttack != null) {
			return false;
		}
		return true;
	}
}
