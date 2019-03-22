package game.content.combat.damage.queue;

import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.entity.Entity;
import game.player.Player;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 10:11 AM
 */
public abstract class EntityDamageQueue<T extends Entity, S extends Entity> {

	/**
	 * A FIFO queue used to maintain the collection of incoming damage to the target entity.
	 */
	private final Queue<EntityDamage<T, S>> futureDamage = new ArrayDeque<>();

	/**
	 * Adds a new damage object to the end of the queue.
	 *
	 * @param damage
	 *            the damage to be added.
	 */
	public void add(EntityDamage<T, S> damage) {
		if (damage == null) {
			throw new IllegalArgumentException("You cannot add null damage to the queue.");
		}
		EntityDamage<T, S> damageOriginal = damage;
		Queue<EntityDamageEffect<T, S>> effects = new LinkedList<>(new ArrayList<>(damage.getEffects()));

		while (!effects.isEmpty()) {
			EntityDamageEffect<T, S> effect = effects.poll();

			if (effect != null && !damage.getEffectCalculated()) {
				EntityDamage<T, S> calculated = effect.onCalculation(damage);
				if (calculated != null) {
					damage = calculated;
					damage.setEffectCalculated(true);
				}
			}
		}
		if (damageOriginal.getSendXpDrop()) {
			Combat.addCombatExperience((Player) damageOriginal.getSender(), damageOriginal.getDamageType().getPreEocHitsplatIcon(), damageOriginal.getDamage());
		}

		futureDamage.add(damage);
	}

	/**
	 * Processes the existing future damage that should be applied to the target.
	 */
	public void process(boolean decrease) {
		if (futureDamage.isEmpty()) {
			return;
		}
		EntityDamage<T, S> damage;

		Queue<EntityDamage<T, S>> updatedQueue = new LinkedList<>();
		while ((damage = futureDamage.poll()) != null) {
			if (damage.getSkip()) {
				damage.setSkip(false);
				updatedQueue.add(damage);
				continue;
			}
			if (damage.getDelay() > 1 && decrease) {
				damage.decreaseDelay();
			}

			if (damage.getDelay() == (decrease ? 1 : 2)) {
				applyDamage(damage);
				continue;
			}
			updatedQueue.add(damage);
		}
		futureDamage.addAll(updatedQueue);
	}

	private void applyDamage(EntityDamage<T, S> damage) {
		Entity target = damage.getTarget();

		Entity sender = damage.getSender();

		if (target == null || sender == null) {
			return;
		}
		apply(damage);
	}

	public abstract void apply(EntityDamage<T, S> damage);

}
