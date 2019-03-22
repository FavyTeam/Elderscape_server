package game.npc.impl.giant_mole;

import com.google.common.collect.ImmutableList;
import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import java.util.Arrays;
import java.util.List;
import utility.Misc;

/**
 * Created by Jason MacKeigan on 2018-01-18 at 5:02 PM
 */
public class GiantMoleCombatStrategy extends NpcCombatStrategy {

	/**
	 * The list of locations that a giant mole can burrow to.
	 */
	private static final List<Position> BURROW_POSITIONS = ImmutableList.copyOf(Arrays.asList(
			new Position(1760, 5187, 0),
			new Position(1737, 5211, 0),
			new Position(1760, 5216, 0),
			new Position(1744, 5170, 0)));

	public static final AttributeKey<Integer> ATTACKS_SINCE_BURROWED = new TransientAttributeKey<>(0);

	/**
	 * Determines whether or not the giant mole is burrowing in a hole or not.
	 */
	private GiantMoleBurrowState burrowState = GiantMoleBurrowState.UNBURROWED;

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		if (burrowState == GiantMoleBurrowState.BURROWING) {
			return false;
		}
		return true;
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
		if (burrowState == GiantMoleBurrowState.BURROWING) {
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
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return -1;
		}
		Npc attackerAsNpc = (Npc) attacker;

		int attacksSinceBurrow = attackerAsNpc.getAttributes().getOrDefault(ATTACKS_SINCE_BURROWED);

		if (attackerAsNpc.getCurrentHitPoints() <= attackerAsNpc.maximumHitPoints / 2) {
			if (attacksSinceBurrow >= 3 && Misc.hasPercentageChance(50)) {
				return ServerConstants.MAGIC_ICON;
			}
		}
		return ServerConstants.MELEE_ICON;
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
		if (defender.getType() != EntityType.NPC || attacker.getType() != EntityType.PLAYER) {
			return damage;
		}
		if (burrowState == GiantMoleBurrowState.BURROWING) {
			return 0;
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
		if (!EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			return;
		}
		Npc attackerAsNpc = (Npc) attacker;

		Player defenderAsPlayer = (Player) defender;

		if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON && burrowState == GiantMoleBurrowState.UNBURROWED) {
			burrowState = GiantMoleBurrowState.BURROWING;
			attacker.getAttributes().put(ATTACKS_SINCE_BURROWED, 0);
			attackerAsNpc.getEventHandler().addEvent(attackerAsNpc, new CycleEvent<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					if (container.getExecutions() == 2) {
						Position next = Misc.random(BURROW_POSITIONS);

						attackerAsNpc.move(next);
					} else if (container.getExecutions() >= 3) {
						container.stop();
						burrowState = GiantMoleBurrowState.UNBURROWED;
						attackerAsNpc.requestAnimation(3315);
					}
				}

				@Override
				public void stop() {

				}
			}, 1);
		} else {
		    attacker.getAttributes().increase(ATTACKS_SINCE_BURROWED);
			DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 7, -1));
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
		if (attacker.getType() != EntityType.NPC) {
			return -1;
		}
		Npc attackerAsNpc = (Npc) attacker;

		if (attackerAsNpc.attackType == ServerConstants.MAGIC_ICON) {
			return 3314;
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
		if (burrowState == GiantMoleBurrowState.BURROWING) {
			return false;
		}
		return true;
	}
}
