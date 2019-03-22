package game.content.skilling;

import core.ServerConstants;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Hitpoints regeneration. This is separate to RegenerateSkill because this one has speeding up through prayer health renewal.
 *
 * @author MGT Madness, created on 04-10-2015.
 */
public class HitPointsRegeneration {

	/**
	 * Begin the Hit points regeneration.
	 *
	 * @param player The associated player.
	 */
	public static void startHitPointsRegeneration(final Player player) {
		if (player.isInZombiesMinigame()) {
			return;
		}
		if (player.hitPointsRegenerationEvent) {
			return;
		}
		if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) >= player.getBaseHitPointsLevel()) {
			return;
		}
		player.hitPointsRegenerationEvent = true;
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				int hitpoints = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				if (player.isInZombiesMinigame()) {
					container.stop();
					return;
				}
				if (player.getTank()) {
					return;
				}
				if (player.dead) {
					container.stop();
					return;
				}
				if (hitpoints == 0) {
					container.stop();
					return;
				}
				if (hitpoints < player.getBaseHitPointsLevel()) {
					player.hitPointsRegenerationCount++;
					if (player.hitPointsRegenerationCount == calculator(player)) {
						boolean addOne = hitpoints < player.getBaseHitPointsLevel() - 1;
						hitpoints += addOne ? 1 : regenAmount(player);
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
					}
					if (player.hitPointsRegenerationCount == calculator(player)) {
						player.hitPointsRegenerationCount = 0;
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.hitPointsRegenerationEvent = false;
				player.hitPointsRegenerationCount = 0;
			}
		}, 50);
	}

	public static int calculator(Player player) {
		if (player.prayerActive[ServerConstants.RAPID_HEAL] || ItemAssistant.hasItemEquipped(player, 11133)) {
			return 1;
		}
		return 2;
	}

	public static int regenAmount(Player player) {
		if (player.prayerActive[ServerConstants.RAPID_HEAL] && ItemAssistant.hasItemEquipped(player, 11133)) {
			return 2;
		}
		return 1;
	}

}
