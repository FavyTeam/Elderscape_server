package game.entity.combat_strategy;

import game.entity.Entity;
import game.entity.EntityType;

/**
 * Created by Jason MacKeigan on 2018-01-05 at 12:58 PM
 * <p>
 * A combat strategy between two entities of unique types.
 * <p>
 * TODO Remove {@link #getAttackerType()} and {@link #getDefenderType()} once combat has entity versus entity support,
 * TODO unfortunately at the moment we need to know the attacker type and defender type to ensure no illegal usage on
 * TODO certain types.
 */
public interface EntityCombatStrategy {

	/**
	 * The type of the attacker.
	 *
	 * @return the entity type.
	 */
	EntityType getAttackerType();

	/**
	 * The type of the defender.
	 *
	 * @return the entity type.
	 */
	EntityType getDefenderType();

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	default boolean canAttack(Entity attacker, Entity defender) {
		return true;
	}

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	default boolean canBeAttacked(Entity attacker, Entity defender) {
		return true;
	}

	/**
	 * Performed on the new attack of one entity on another. This is not when damage is performed.
	 *
	 * @param attacker the entity attacking.
	 * @param defender the entity defending.
	 */
	default void onAttack(Entity attacker, Entity defender) {

	}

	/**
	 * Determines the attack type of the entity, representing what style of combat is being used.
	 *
	 * @return the attack type, or -1 if none can be found.
	 */
	default int calculateAttackType(Entity attacker, Entity defender) {
		return -1;
	}

	/**
	 * The custom damage that should be dealt, or -1 if the parent damage should be taken into consideration.
	 *
	 * @param attacker the attacker dealing the damage.
	 * @param defender the defender taking the damage.
	 * @return the custom calculation of damage, or -1 if the parent damage should be used instead.
	 */
	default int calculateCustomDamage(Entity attacker, Entity defender, int entityAttackType) {
		return -1;
	}

	/**
	 * The custom damage to be taken from another entity, or -1 if no custom damage is to be calculated.
	 *
	 * @param attacker the entity dealing the damage.
	 * @param defender the entity taking the damage.
	 * @param damage the amount of damage calculated before being modified for this sequence of combat.
	 * @param attackType the type of attacking the damage was for.
	 * @return the custom damage taken, or -1 if no custom damage is calculated.
	 */
	default int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		return -1;
	}

	/**
	 * Modifies the combat defence value for this entity.
	 *
	 * @param attacker
	 * 			  the entity attacking this defender.
	 * @param defender
	 * 			  the entity defending off the attack.
	 * @param defence
	 * 			  the defence we're modifying.
	 * @param attackType
	 * 			  the attack type of the attacker.
	 * @return the defence parameter by default, or some modification if necessary.
	 */
	default int calculateCustomCombatDefence(Entity attacker, Entity defender, int defence, int attackType) {
		return defence;
	}

	/**
	 * Referenced when damage is applied to the defender from the attacker.
	 *
	 * @param attacker the entity damaging the defender.
	 * @param defender the entity being damaged by the attacker.
	 * @param damage the amount of damage being applied to the defender.
	 * @param attackType the
	 */
	default void onDamageDealt(Entity attacker, Entity defender, int damage, int attackType) {

	}

	/**
	 * Referenced when the defender is damaged by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @param damage the damage being dealt.
	 * @param entityAttackType the type of attack that created the damage.
	 */
	default void onDamageTaken(Entity attacker, Entity defender, int damage, int entityAttackType) {

	}

	/**
	 * References when {@link #isCustomAttack()} returns true, in which case we're responsible the
	 * regular combat process.
	 *
	 * @param attacker the attacker.
	 * @param defender the defender.
	 */
	default void onCustomAttack(Entity attacker, Entity defender) {

	}

	/**
	 * Referenced only if {@link #isCustomAttack()} returns true, in which case we can change some
	 * important combat related information to the attacker and defender, like attack speed.
	 *
	 * @param attacker the entity making the attack.
	 * @param defender the entity defending themselves.
	 */
	default void afterCustomAttack(Entity attacker, Entity defender) {

	}

	/**
	 * Retrieves the custom emote for the attacking entity, or -1 if there is no custom emote.
	 *
	 * @param attacker the entity making the attack animation.
	 * @return the attack emote, or -1 by default for no custom attack emote.
	 */
	default int getCustomAttackAnimation(Entity attacker) {
		return -1;
	}

	/**
	 * The projectile speed of the attacker.
	 *
	 * @param attacker the entity attacking.
	 * @return the projectile speed in terms of ticks.
	 */
	default int getProjectileSpeed(Entity attacker) {
		return 0;
	}

	/**
	 * The start height of a graphic performed by the attacker.
	 *
	 * @return the gfx start height, or 43 by default.
	 */
	default int getGfxStartHeight(Entity attacker) {
		return 43;
	}

	/**
	 * Determines if the target is lost when the {@link #canAttack(Entity, Entity)} condition returns false.
	 *
	 * @return {@code true} if the target should be reset when they cannot be attacked, by default {@code false}.
	 */
	default boolean resetsTargetOnCannotAttack() {
		return false;
	}

	/**
	 * Determines if we're going to handle the entire attack process our self.
	 *
	 * @return {@code true} if it's a custom attack, by default, false.
	 */
	default boolean isCustomAttack() {
		return false;
	}

	/**
	 * Determines if a boss should perform a block operation.
	 *
	 * @return true by default.
	 */
	default boolean performsBlockAnimation() {
		return true;
	}

	/**
	 * Determines whether or not an entity should perform an attack animation.
	 *
	 * @return by default this is true.
	 */
	default boolean performsAttackAnimation() {
		return true;
	}

	/**
	 * Determines if this entity can hit through prayer.
	 *
	 * @return false by default.
	 */
	default boolean hitsThroughPrayer(Entity attacker, Entity defender, int damage, int type) {
		return false;
	}

	/**
	 * Determines if this entity can be attacked by this type.
	 *
	 * @param attacker
	 *            the attacking making the attack.
	 * @param defender
	 *            the defender being attacked.
	 * @param attackType
	 *            the type of attack being made.
	 * @return {@code true} by default.
	 */
	default boolean canBeAttackedByType(Entity attacker, Entity defender, EntityAttackType attackType) {
		return true;
	}

	/**
	 * Referenced when an attack timer is on cooldown.
	 *
	 * @param attacker
	 * 			  the entity making the attack.
	 * @param cyclesRemaining
	 * 			  the amount of cycles remaining until the next attack.
	 */
	default void onAttackCooldown(Entity attacker, int cyclesRemaining) {

	}

	/**
	 * Determines whether or not this entity is able to find a target.
	 *
	 * @param attacker
	 * 			  the entity we're determining can find targets or not.
	 * @return {@code true} by default.
	 */
	default boolean canFindTarget(Entity attacker) {
		return true;
	}
}
