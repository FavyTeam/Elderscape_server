package game.npc.impl.zulrah;

import core.ServerConfiguration;
import game.entity.Entity;
import game.entity.combat_strategy.EntityAttackType;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.impl.zulrah.attack.ZulrahAttackStrategy;
import game.npc.impl.zulrah.rotation.ZulrahRotation;
import game.npc.impl.zulrah.sequence.ZulrahSequence;
import game.player.Player;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Jason MacKeigan on 2018-04-02 at 5:15 PM
 */
public class ZulrahCombatStrategy extends NpcCombatStrategy {

	private final ZulrahRotation rotation;

	private Queue<ZulrahSequence> sequences;

	private Queue<ZulrahAttackStrategy> attacksForSequence;

	private ZulrahSequence currentSequence;

	private ZulrahAttackStrategy currentAttack;

	private ZulrahTransformationState transformationState = ZulrahTransformationState.NONE;

	public ZulrahCombatStrategy(ZulrahRotation rotation) {
		this.rotation = rotation;
		this.sequences = new ArrayDeque<>(rotation.getSequences());

		if (sequences.isEmpty()) {
			throw new IllegalArgumentException("Rotation contains no sequences.");
		}
		this.currentSequence = sequences.poll();
		this.attacksForSequence = new ArrayDeque<>(currentSequence.getAttacks());
		if (attacksForSequence.isEmpty()) {
			throw new IllegalArgumentException("No attacks exist for the first sequence.");
		}
		this.currentAttack = attacksForSequence.poll();
	}

	public void transform(Npc zulrah, Player defender) {
		if (transformationState == ZulrahTransformationState.TRANSFORMING) {
			throw new IllegalStateException("Already transforming.");
		}
		zulrah.getLocalNpcs().forEach(Npc::killIfAlive);
		transformationState = ZulrahTransformationState.TRANSFORMING;
		zulrah.getEventHandler().addEvent(zulrah, new ZulrahTransformationEvent(
				currentSequence.getLocation(), currentSequence.getTransformation(), defender), 1).
						                                                                                 addStopListener(() -> {
							                                                                                 transformationState = ZulrahTransformationState.NONE;
							                                                                                 currentAttack.onStart(zulrah);
						                                                                                 });
	}

	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return false;
		}
		return transformationState != ZulrahTransformationState.TRANSFORMING && currentAttack.canAttack(attacker, defender);
	}

	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return false;
		}
		return transformationState != ZulrahTransformationState.TRANSFORMING && currentAttack.canBeAttacked(attacker, defender);
	}

	@Override
	public int calculateAttackType(Entity attacker, Entity defender) {
		return currentAttack.calculateAttackType(attacker, defender);
	}

	@Override
	public int calculateCustomDamage(Entity attacker, Entity defender, int entityAttackType) {
		return currentAttack.calculateCustomDamage(attacker, defender, entityAttackType);
	}

	@Override
	public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		return currentAttack.calculateCustomDamageTaken(attacker, defender, damage, attackType);
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
		currentAttack.afterCustomAttack(attacker, defender);
	}

	@Override
	public void onDamageDealt(Entity attacker, Entity defender, int damage, int attackType) {
		currentAttack.onDamageDealt(attacker, defender, damage, attackType);
	}

	@Override
	public void onDamageTaken(Entity attacker, Entity defender, int damage, int entityAttackType) {
		currentAttack.onDamageTaken(attacker, defender, damage, entityAttackType);
	}

	@Override
	public void onCustomAttack(Entity attacker, Entity defender) {
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		currentAttack.onCustomAttack(attacker, defender);

		if (currentAttack.isComplete()) {
			if (attacksForSequence.isEmpty()) {
				if (sequences.isEmpty()) {
					sequences = new ArrayDeque<>(rotation.getSequences());
					currentSequence = sequences.poll();
					attacksForSequence = new ArrayDeque<>(currentSequence.getAttacks());
					currentAttack.onEnd(attackerAsNpc);
					currentAttack = attacksForSequence.poll();
					transform(attackerAsNpc, defenderAsPlayer);
					return;
				}
				currentSequence = sequences.poll();
				attacksForSequence = new ArrayDeque<>(currentSequence.getAttacks());
				currentAttack.onEnd(attackerAsNpc);
				currentAttack = attacksForSequence.poll();
				transform(attackerAsNpc, defenderAsPlayer);
				return;
			}
			currentAttack = attacksForSequence.poll();
		}
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

	@Override
	public int getCustomAttackAnimation(Entity attacker) {
		return currentAttack.getCustomAttackAnimation(attacker);
	}

	@Override
	public boolean performsBlockAnimation() {
		return false;
	}

	@Override
	public boolean performsAttackAnimation() {
		return transformationState != ZulrahTransformationState.TRANSFORMING && currentAttack.performsAttackAnimation();
	}

	/**
	 * Determines if this entity can be attacked by this type.
	 *
	 * @param attacker the attacking making the attack.
	 * @param defender the defender being attacked.
	 * @param attackType the type of attack being made.
	 * @return {@code true} by default.
	 */
	@Override
	public boolean canBeAttackedByType(Entity attacker, Entity defender, EntityAttackType attackType) {
		if (attackType == EntityAttackType.MELEE) {
			return false;
		}
		return super.canBeAttackedByType(attacker, defender, attackType);
	}

	public ZulrahSequence getCurrentSequence() {
		return currentSequence;
	}
}
