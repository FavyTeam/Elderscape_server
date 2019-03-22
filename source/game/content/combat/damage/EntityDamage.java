package game.content.combat.damage;

import game.entity.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 10:38 AM
 */
public class EntityDamage<T extends Entity, S extends Entity> {

	private final T target;//edit

	private final S sender;

	private final Predicate<T> requirementOrNull;

	private final BiConsumer<Integer, T> onSuccessfulHit;

	private final EntityDamageType damageType;

	private final List<EntityDamageEffect<T, S>> effects = new ArrayList<>();

	private final int damage;

	private int delay;

	private int maximumDamage;

	private boolean sendXpDrop;

	private boolean isMagicSplash;

	private boolean skip;

	private boolean effectCalculated;

	public EntityDamage(T target, S sender, int damage, int delay, EntityDamageType damageType, int maximumDamage, Predicate<T> requirementOrNull, BiConsumer<Integer, T>
	onSuccessfulHit, boolean sendXpDrop, boolean isMagicSplash) {
		this.target = target;
		this.sender = sender;
		this.damage = damage;
		this.delay = delay;
		this.requirementOrNull = requirementOrNull;
		this.onSuccessfulHit = onSuccessfulHit;
		this.damageType = damageType;
		this.maximumDamage = maximumDamage;
		this.sendXpDrop = sendXpDrop;
		this.isMagicSplash = isMagicSplash;
	}

	public EntityDamage(T target, S sender, int damage, int delay, EntityDamageType damageType, int maximumDamage, boolean sendXpDrop, boolean isMagicSplash) {
		this(target, sender, damage, delay, damageType, maximumDamage, null, null, sendXpDrop, isMagicSplash);
	}

	public EntityDamage<T, S> setDamage(int damage) {
		EntityDamage<T, S> copy = new EntityDamage<>(target, sender, damage, delay, damageType, maximumDamage, requirementOrNull, onSuccessfulHit, sendXpDrop, isMagicSplash);

		copy.effects.addAll(effects);

		return copy;
	}

	public EntityDamage addEffect(EntityDamageEffect<T, S> effect) {
		effects.add(effect);

		return this;
	}

	public List<EntityDamageEffect<T, S>> getEffects() {
		return effects;
	}
	
	public void setEffectCalculated(boolean state) {
		effectCalculated = state;
	}

	public boolean getEffectCalculated() {
		return effectCalculated;
	}

	public void clearEffects() {
		effects.clear();
	}

	public void decreaseDelay() {
		delay--;
	}

	public T getTarget() {
		return target;
	}

	public S getSender() {
		return sender;
	}

	public int getDamage() {
		return damage;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int amount) {
		delay = amount;
	}

	public Predicate<T> getRequirementOrNull() {
		return requirementOrNull;
	}

	public BiConsumer<Integer, T> getOnSuccessfulHit() {
		return onSuccessfulHit;
	}

	public EntityDamageType getDamageType() {
		return damageType;
	}

	public int getMaximumDamage() {
		return maximumDamage;
	}

	public boolean getSendXpDrop() {
		return sendXpDrop;
	}
	
	public void setSendXpDrop(boolean state) {
		sendXpDrop = state;
	}

	public boolean isMagicSplash()
	{
		return isMagicSplash;
	}

	public boolean getSkip() {
		return skip;
	}

	public void setSkip(boolean state) {
		skip = state;
	}
}
