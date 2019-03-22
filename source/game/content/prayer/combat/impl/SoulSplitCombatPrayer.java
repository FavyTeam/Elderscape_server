package game.content.prayer.combat.impl;

import core.ServerConstants;
import game.content.prayer.combat.CombatPrayer;
import game.content.skilling.Skilling;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Handles the soul split combat prayer
 * 
 * @author 2012
 *
 */
public class SoulSplitCombatPrayer extends CombatPrayer {

	/**
	 * Represents the soul split prayer
	 */
	public SoulSplitCombatPrayer() {
		super(0);
	}

	/**
	 * Whether soul split is active
	 */
	private boolean active;

	@Override
	public void execute(Player player, Player victim, int damage) {
		/*
		 * Invalid victim
		 */
		if (victim == null) {
			return;
		}
		/*
		 * Already active
		 */
		if (active) {
			return;
		}
		/*
		 * No damage
		 */
		if (damage < 1) {
			return;
		}
		/*
		 * Active
		 */
		active = true;
		/*
		 * Send projectile
		 */
		player.getPA().createPlayersProjectile2(player, victim, 50, 70, 2263, 20, 20,
				-victim.getPlayerId() - 1, 51, 0, player.getHeight());
		/*
		 * The effect
		 */
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			/*
			 * The delay
			 */
			int delay = getHitDelay(player, victim);
			/*
			 * The amount
			 */
			int amount = (int) (damage * 0.20);

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Go to victim
				 */
				if (container.getExecutions() == delay - 1) {
					victim.gfx0(2264);
					player.getPA().createPlayersProjectile2(victim, player, 50, 70, 2263, 20, 20,
							-player.getPlayerId() - 1, 51, 0, player.getHeight());
					/*
					 * Back to player
					 */
				} else
				if (container.getExecutions() == delay) {
					Skilling.decreaseCombatSkill(victim, ServerConstants.PRAYER, amount);
					Skilling.increaseCombatSkill(player, ServerConstants.HITPOINTS, amount, 0);
					container.stop();
				}
			}

			@Override
			public void stop() {
				active = false;
			}
		}, 1);
	}
}
