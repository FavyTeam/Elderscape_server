package game.npc.impl.abyssal_sire;

import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;

import java.util.concurrent.ThreadLocalRandom;
import core.GameType;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-03-10 at 10:45 AM
 */
//@CustomNpcComponent(id = 5914, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 5914))

public class AbyssalSireRespiratorySystem extends Npc implements EntityCombatStrategy {

	public AbyssalSireRespiratorySystem(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new AbyssalSireRespiratorySystem(index, npcType);
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return this;
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

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return false;
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
	@Override
	public int calculateCustomDamageTaken(Entity attacker, Entity defender, int damage, int attackType) {
		if (!getAttributes().getOrDefault(AbyssalSire.AWAKE, false)) {
			if (attacker.getType() == EntityType.PLAYER && defender.getType() == EntityType.NPC) {
				Npc defenderAsNpc = (Npc) defender;

				defenderAsNpc.heal(4);
			}
			return ThreadLocalRandom.current().nextInt(0, 4);
		}
		return damage;
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
