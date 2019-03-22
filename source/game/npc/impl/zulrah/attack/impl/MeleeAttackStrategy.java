package game.npc.impl.zulrah.attack.impl;

import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.npc.impl.zulrah.attack.ZulrahAttackStrategy;
import game.player.Player;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-04-17 at 1:10 PM
 */
public class MeleeAttackStrategy implements ZulrahAttackStrategy {

	/**
	 * The current attack sequence, by default, none.
	 */
	private AttackSequence attackSequence = AttackSequence.FIRST;

	/**
	 * The position of the attack, by default; new Position(0, 0, 0);
	 */
	private Position positionOfAttack = new Position(0, 0, 0);

	/**
	 * The list of ordered sequences that this attack will go through until it is finished.
	 */
	private Queue<AttackSequence> sequences = new ArrayDeque<>(Arrays.asList(AttackSequence.values()));

	@Override
	public void onStart(Npc zulrah) {
		zulrah.resetFace();
		zulrah.setFacingEntityDisabled(true);
	}

	@Override
	public void onEnd(Npc zulrah) {
		zulrah.resetFace();
		zulrah.setFacingEntityDisabled(false);
	}

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return attackSequence != AttackSequence.FINISHED;
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
		return true;
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
		return 30 + ThreadLocalRandom.current().nextInt(0, 12);
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
		if (attacker.getType() == EntityType.NPC && defender.getType() == EntityType.PLAYER) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 7, 41, -1, p ->
					                                                                                                   positionOfAttack != null
					                                                                                                   && positionOfAttack.distanceTo(p.getX(), p.getY()) <= 1,
			                           null));

			attackSequence = sequences.peek() == null ? AttackSequence.FINISHED : sequences.poll();
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			Player defenderAsPlayer = (Player) defender;

			positionOfAttack = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

			attackerAsNpc.resetFace();
			attackerAsNpc.turnNpc(positionOfAttack.getX(), positionOfAttack.getY());
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
		return 5806;
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
		return 0;
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

	/**
	 * Determines whether or not an entity should perform an attack animation.
	 *
	 * @return by default this is true.
	 */
	@Override
	public boolean performsAttackAnimation() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return attackSequence == AttackSequence.FINISHED;
	}

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


	private enum AttackSequence {
		FIRST,

		SECOND,

		FINISHED
	}

}
