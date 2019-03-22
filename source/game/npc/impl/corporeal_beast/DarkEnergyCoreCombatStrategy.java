package game.npc.impl.corporeal_beast;

import core.ServerConstants;
import game.position.Position;
import game.entity.Entity;
import game.entity.combat_strategy.EntityCombatStrategyFactory;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MacKeigan on 2018-02-20 at 10:44 AM
 */
public class DarkEnergyCoreCombatStrategy extends NpcCombatStrategy {

	private boolean poisonedAlready;

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
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
		return ServerConstants.MELEE_ICON;
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

			if (defenderAsPlayer.distanceToPoint(attackerAsNpc.getX(), attackerAsNpc.getY()) >= 2) {
				if (attackerAsNpc.poisonEvent) {
					attackerAsNpc.curePoison();
					poisonedAlready = true;
				}
				attackerAsNpc.facePlayer(defenderAsPlayer.getPlayerId());

				final Position destination = new Position(defenderAsPlayer.getX(), defenderAsPlayer.getY(), defenderAsPlayer.getHeight());

				defenderAsPlayer.getPA().createPlayersProjectile(attackerAsNpc, destination, 50, 125, 319, 0, 0, 0, 0, 30);

				attackerAsNpc.setVisible(false);

				attackerAsNpc.getEventHandler().addEvent(attackerAsNpc, new CycleEvent<Entity>() {
					@Override
					public void execute(CycleEventContainer<Entity> container) {
						container.stop();

						attackerAsNpc.move(destination);
						attackerAsNpc.setVisible(true);
						attackerAsNpc.facePlayer(defenderAsPlayer.getPlayerId());
					}

					@Override
					public void stop() {

					}
				}, 3);
			} else {
				DamageQueue.add(new Damage(defenderAsPlayer, attackerAsNpc, ServerConstants.MELEE_ICON, 1, 8, -1, null, (d, p) -> {
					if (d > 0) {
						Npc corporeal = attackerAsNpc.getLocalNpcs().stream().filter(n -> n != null && n.npcType == 319).findAny().orElse(null);

						if (corporeal != null) {
							corporeal.heal(d);
						}
					}
				}));
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
		if (EntityCombatStrategyFactory.isNpcVersusPlayer(this)) {
			Npc attackerAsNpc = (Npc) attacker;

			if (attackerAsNpc.poisonEvent && !poisonedAlready) {
				attackerAsNpc.attackTimer += 6;
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
