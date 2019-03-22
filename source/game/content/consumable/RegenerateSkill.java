package game.content.consumable;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Drain the boosted level or regenerate the drained level. This also includes hitpoints draining.
 *
 * @author MGT Madness, created on 21-03-2015.
 */
public class RegenerateSkill {

	/**
	 * Start the drain event system.
	 *
	 * @param player The associated player.
	 */
	public static void drainSkillEvent(final Player player) {
		if (player.isDrainEvent() || player.getTank()) {
			return;
		}
		player.setDrainEvent(true);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				boolean drain = false;
				for (int value = 0; value <= 6; value++) {
					if (player.currentCombatSkillLevel[value] > player.baseSkillLevel[value]) {
						if (player.hasOverloadBoost) {
							return;
						}
						if (hasMinuteElapsed(player.boostedTime[value], player.prayerActive[ServerConstants.PRESERVE])) {
							player.currentCombatSkillLevel[value] -= 1;
							Skilling.updateSkillTabFrontTextMain(player, value);
							player.boostedTime[value] = System.currentTimeMillis();
						}
						drain = true;
					} else if (player.currentCombatSkillLevel[value] < player.baseSkillLevel[value] && value != ServerConstants.PRAYER) {
						if (value == ServerConstants.HITPOINTS && player.getDead()) {
							continue;
						}
						if (hasMinuteElapsed(player.boostedTime[value], false)) {
							player.currentCombatSkillLevel[value] += 1;
							Skilling.updateSkillTabFrontTextMain(player, value);
							player.boostedTime[value] = System.currentTimeMillis();
						}
						drain = true;
					}
				}
				if (!drain || player.getTank()) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.setDrainEvent(false);
			}
		}, 2);
	}

	/**
	 * Store the time of when the skill was boosted.
	 *
	 * @param player The associated player.
	 * @param skill The skill that is boosted.
	 */
	public static void storeBoostedTime(Player player, int skill) {
		player.boostedTime[skill] = System.currentTimeMillis();
		drainSkillEvent(player);
	}

	/**
	 * @param boostedTime The time of when the skill was boosted.
	 * @return True, if the given boostedTime has occured over 60 seconds ago.
	 */
	private static boolean hasMinuteElapsed(long boostedTime, boolean preservePrayer) {
		int amount = preservePrayer ? 90000 : 60000;
		if (System.currentTimeMillis() - boostedTime >= amount) {
			return true;
		}
		return false;
	}

	/**
	 * Apply the DrainSkill methods that are required upon the player logging in.
	 *
	 * @param player The associated player.
	 */
	public static void logInUpdate(Player player) {
		RegenerateSkill.addLostTime(player);
		RegenerateSkill.drainSkillEvent(player);
	}

	/**
	 * Add the lost time to the boostedTime long array, so if the player logs out for 30 minutes and he logs back in, it won't
	 * instantly drain the skills.
	 *
	 * @param player The associated player.
	 */
	public static void addLostTime(Player player) {
		long space = System.currentTimeMillis() - player.logOutTime;
		if (space < 0) {
			return;
		}
		player.setXpBonusEndTime(player.getXpBonusEndTime() + space);
		player.teleBlockEndTime += space;
		player.chargeSpellTime += space;
		player.imbuedHeartEndTime += space;
		for (int value = 0; value <= 6; value++) {
			player.boostedTime[value] += space;
		}
	}
}
