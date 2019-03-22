package game.npc.impl.demonic_gorilla;

import com.google.common.primitives.Ints;
import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import utility.Misc;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Jason MacKeigan on 2018-01-16 at 5:28 PM
 */
public class DemonicGorillaCombatStrategy extends NpcCombatStrategy {

	/**
	 * The maximum number of consecutive misses occur before the style changes.
	 */
	private static final int MAXIMUM_CONSECUTIVE_MISSES = 3;

	/**
	 * The maximum number of damage taken before switching styles.
	 */
	private static final int MAXIMUM_DAMAGE_BEFORE_CHANGE = 50;

	/**
	 * The queue of last consecutive damage.
	 */
	private final Queue<Integer> lastConsecutiveDamage = new ArrayDeque<>();

	/**
	 * The last amount of damage taken in the current attack type.
	 */
	private final Queue<Integer> lastConsecutiveDamageTaken = new ArrayDeque<>();

	/**
	 * The current attack type of the npc.
	 */
	private int currentAttackType = ServerConstants.MELEE_ICON;

	/**
	 * The current transformation of the gorilla.
	 */
	private DemonicGorillaTransformation currentTransformation = DemonicGorillaTransformation.MELEE;

	/**
	 * Determines if the current attack of the gorilla is a special attack.
	 */
	private boolean rangedSpecialAttack;

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @param attacker
	 * @param defender
	 * @return the attack type, or -1 if none can be found.
	 */
	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		if (ThreadLocalRandom.current().nextInt(0, 100) <= 15) {
			rangedSpecialAttack = true;
			return ServerConstants.RANGED_ICON;
		}
		return currentAttackType;
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
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (rangedSpecialAttack) {
			Position damagePosition = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 155, 856, 150, 0, 0, 45, 30);
			int fixedDamage = defenderAsPlayer.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 3;
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 6, 31,
					fixedDamage, p -> p.getX() == damagePosition.getX() && p.getY() == damagePosition.getY() && p.getHeight() == damagePosition.getZ(),
					null, (f, s) -> s.getPA().stillGfx(305, damagePosition.getX(), damagePosition.getY(), 0, 0)));
			rangedSpecialAttack = false;
		} else if (currentAttackType == ServerConstants.MELEE_ICON) {
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 2, 31, -1));
		} else if (currentAttackType == ServerConstants.RANGED_ICON) {
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.RANGED_ICON, 4, 31, -1, null, (d, p) -> p.gfx0(1303)));
		} else if (currentAttackType == ServerConstants.MAGIC_ICON) {
			defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, defenderAsPlayer, 50, 95, 1305, 43, 0, 0, 45, 30);

			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MAGIC_ICON, 4, 31, -1, null, (d, p) -> p.gfx0(1304)));
		}
	}

	/**
	 * The custom damage to be taken from another entity, or -1 if no custom damage is to be calculated.
	 *
	 * @param attacker the entity dealing the damage.
	 * @param defender the entity taking the damage.
	 * @param damage the amount of damage calculated before being modified for this sequence of combat.
	 * @return the custom damage taken, or -1 if no custom damage is calculated.
	 */
	@Override
	public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		if (currentTransformation == DemonicGorillaTransformation.MELEE && attackType == ServerConstants.MELEE_ICON) {
			return 0;
		} else if (currentTransformation == DemonicGorillaTransformation.RANGE && attackType == ServerConstants.RANGED_ICON
		           && !rangedSpecialAttack) {
			return 0;
		} else if (currentTransformation == DemonicGorillaTransformation.MAGIC && attackType == ServerConstants.MAGIC_ICON) {
			return 0;
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
		if (attacker.getType() != EntityType.NPC || defender.getType() != EntityType.PLAYER) {
			return;
		}
		lastConsecutiveDamage.offer(damage);

		if (lastConsecutiveDamage.size() >= 10 || lastConsecutiveDamage.stream().filter(value -> value == 0).count() >= MAXIMUM_CONSECUTIVE_MISSES) {
			lastConsecutiveDamage.clear();
			currentAttackType = Misc.random(Ints.asList(0, 1, 2).stream().filter(value -> value != currentAttackType).collect(Collectors.toList()));
		}
	}

	/**
	 * Referenced when the defender is damaged by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @param damage the damage being dealt.
	 * @param entityAttackType
	 */
	@Override
	public void onDamageTaken(Entity attacker, Entity defender, int damage, int entityAttackType) {
		if (attacker.getType() != EntityType.PLAYER || defender.getType() != EntityType.NPC) {
			return;
		}
		Npc defenderAsNpc = (Npc) defender;

		lastConsecutiveDamageTaken.offer(damage);

		if (lastConsecutiveDamageTaken.stream().mapToInt(value -> value).sum() >= MAXIMUM_DAMAGE_BEFORE_CHANGE) {
			lastConsecutiveDamageTaken.clear();

			currentTransformation = entityAttackType == ServerConstants.MELEE_ICON ?
					                        DemonicGorillaTransformation.MELEE : entityAttackType == ServerConstants.RANGED_ICON ?
							                                                             DemonicGorillaTransformation.RANGE : DemonicGorillaTransformation.MAGIC;

			if (defenderAsNpc.getTransformOrId() != currentTransformation.getNpcId()) {
				defenderAsNpc.transform(currentTransformation.getNpcId());
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
		if (rangedSpecialAttack) {
			return 7228;
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
}
