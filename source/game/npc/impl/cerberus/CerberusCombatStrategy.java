package game.npc.impl.cerberus;

import com.google.common.collect.ImmutableList;
import core.GameType;
import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEventContainer;
import utility.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-02-09 at 11:34 AM
 */
public class CerberusCombatStrategy extends NpcCombatStrategy {

	/**
	 * The time in nanoseconds when the last unique special was executed.
	 */
	private long timeOfLastUnique;

	/**
	 * The list of active lava objects, if any.
	 */
	private final List<Position> lavaPositions = new ArrayList<>();

	/**
	 * The current attack of cerberus.
	 */
	private CerberusAttack attack = CerberusAttack.UNIQUE_MAGIC;

	/**
	 * The current lava event, or null if there is none.
	 */
	private CerberusLavaCycleEvent lavaCycleEvent;

	/**
	 * The current event for spawning summoned souls, or null if the event is not active.
	 */
	private CerberusSummonedSoulCycleEvent soulCycleEvent;

	/**
	 * The unique attacks that cerberus uses in consecutive order.
	 */
	private static final List<CerberusAttack> UNIQUE_ATTACKS = ImmutableList.of(CerberusAttack.UNIQUE_MAGIC,
	                                                                            CerberusAttack.UNIQUE_MELEE, CerberusAttack.UNIQUE_RANGE);

	/**
	 * The basic attacks used by cerberus in no specific order.
	 */
	private static final List<CerberusAttack> BASIC_ATTACKS = ImmutableList.of(CerberusAttack.MAGIC,
	                                                                           CerberusAttack.MELEE, CerberusAttack.RANGE);

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		if (defender.getType() != EntityType.PLAYER) {
			return false;
		}
		Player defenderAsPlayer = (Player) defender;

		if (defenderAsPlayer.baseSkillLevel[ServerConstants.SLAYER] < 91 && GameType.isOsrsEco()) {
			return false;
		}
		return true;
	}

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		if (attacker.getType() != EntityType.PLAYER) {
			return false;
		}
		Player attackerAsPlayer = (Player) attacker;

		if (attackerAsPlayer.baseSkillLevel[ServerConstants.SLAYER] < 91 && GameType.isOsrsEco()) {
			attackerAsPlayer.getPA().sendMessage("You need a slayer level of 91 to attack this.");
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (!UNIQUE_ATTACKS.contains(attack) && TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - timeOfLastUnique) >= 40) {
				attack = CerberusAttack.UNIQUE_MAGIC;

				return ServerConstants.MAGIC_ICON;
			}

			if (attack == CerberusAttack.UNIQUE_MAGIC) {
				attack = CerberusAttack.UNIQUE_RANGE;

				return ServerConstants.RANGED_ICON;
			}

			if (attack == CerberusAttack.UNIQUE_RANGE) {
				if (attackerAsNpc.distanceTo(defenderAsPlayer.getX(), defenderAsPlayer.getY()) <= 5) {
					attack = CerberusAttack.UNIQUE_MELEE;

					return ServerConstants.MELEE_ICON;
				}
				timeOfLastUnique = System.nanoTime();
			} else if (attack == CerberusAttack.UNIQUE_MELEE) {
				timeOfLastUnique = System.nanoTime();
			}

			if (lavaCycleEvent == null && attackerAsNpc.getCurrentHitPoints() <= 200) {
				if (ThreadLocalRandom.current().nextInt(0, 3) == 0) {
					attack = CerberusAttack.LAVA;

					return ServerConstants.MAGIC_ICON;
				}
			}

			if (soulCycleEvent == null && attackerAsNpc.getCurrentHitPoints() <= 400) {
				if (ThreadLocalRandom.current().nextInt(0, 7) == 0) {
					attack = CerberusAttack.SUMMONED_SOUL;

					return ServerConstants.MAGIC_ICON;
				}
			}
			attack = Misc.random(BASIC_ATTACKS);

			return attack == CerberusAttack.MELEE ? ServerConstants.MELEE_ICON : attack == CerberusAttack.RANGE
					                                                                     ? ServerConstants.RANGED_ICON : ServerConstants.MAGIC_ICON;
		}
		return -1;
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Player defenderAsPlayer = (Player) defender;

			if (attack == CerberusAttack.UNIQUE_MELEE || attack == CerberusAttack.MELEE) {
				if (defenderAsPlayer.prayerActive[ServerConstants.MELEE_ICON]) {
					return 0;
				}
			} else if (attack == CerberusAttack.UNIQUE_MAGIC || attack == CerberusAttack.MAGIC) {
				if (defenderAsPlayer.prayerActive[ServerConstants.MAGIC_ICON]) {
					return 0;
				}
			} else if (attack == CerberusAttack.UNIQUE_RANGE || attack == CerberusAttack.RANGE) {
				if (defenderAsPlayer.prayerActive[ServerConstants.RANGED_ICON]) {
					return 0;
				}
			}
		}
		return -1;
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			if (attack == CerberusAttack.MAGIC || attack == CerberusAttack.UNIQUE_MAGIC) {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer,
				                                                 50, 125, 1242, 43, 31, -defenderAsPlayer.getPlayerId() - 1, 65, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 5, 23, -1));
			} else if (attack == CerberusAttack.RANGE || attack == CerberusAttack.UNIQUE_RANGE) {
				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer,
				                                                 50, 125, 1245, 43, 31, -defenderAsPlayer.getPlayerId() - 1, 65, 0);
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 5, 23, -1));
			} else if (attack == CerberusAttack.MELEE || attack == CerberusAttack.UNIQUE_MELEE) {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 23, -1));
			} else if (attack == CerberusAttack.LAVA) {
				Position playerPosition = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

				lavaPositions.clear();
				lavaPositions.add(playerPosition);
				lavaPositions.add(playerPosition.randomTranslate(-3, 3, -3, 3));
				lavaPositions.add(playerPosition.randomTranslate(-3, 3, -3, 3));

				attackerAsNpc.getEventHandler().addEvent(attackerAsNpc, lavaCycleEvent = new CerberusLavaCycleEvent(attackerAsNpc, lavaPositions), 1);

				lavaPositions.forEach(position -> defenderAsPlayer.getPA().createPlayersStillGfx(1246, position.getX(), position.getY(), position.getZ(), 10));
				attackerAsNpc.forceChat("Grrrrrrrrrrrrrr");
			} else if (attack == CerberusAttack.SUMMONED_SOUL) {
				if (soulCycleEvent != null) {
					CycleEventContainer<Entity> container = attackerAsNpc.getEventHandler().getContainer(soulCycleEvent);

					if (container != null && container.isRunning() && soulCycleEvent.getState() != CerberusSummonedSoulCycleEvent.State.COMPLETED) {
						return;
					}
				}
				attackerAsNpc.forceChat("Aaarrrooooooo");

				CycleEventContainer<Entity> container = attackerAsNpc.getEventHandler().addEvent(attackerAsNpc,
				                                                                                 soulCycleEvent = new CerberusSummonedSoulCycleEvent(attackerAsNpc,
				                                                                                                                                     defenderAsPlayer), 1);

				container.addStopListener(() -> soulCycleEvent = null);
			}
		}
	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		switch (attack) {
			case MELEE:
			case UNIQUE_MELEE:
				return 4491;

			case RANGE:
			case UNIQUE_RANGE:
			case MAGIC:
			case UNIQUE_MAGIC:
				return 4489;

			case SUMMONED_SOUL:
				return 4494;

			case LAVA:
				return 4490;
		}
		return -1;
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
