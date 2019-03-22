package game.content.skilling.agility;

import core.ServerConstants;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Handles the Gnome agility course
 *
 * @author MGT Madness, revised at 02-02-2015.
 */

public class GnomeCourse {

	/**
	 * Apply the log balance action.
	 *
	 * @param player The associated player.
	 */
	public static void logBalanceAction(Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		AgilityAssistant.agilityAnimation(player, 762, false, 0, -7);
		logBalanceReward(player);
	}

	/**
	 * Reward the player for completing the log balance once the player reaches the other end.
	 *
	 * @param player The associated player.
	 */
	private static void logBalanceReward(final Player player) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getY() == 3429) {
					player.setDoingAgility(false);
					container.stop();
				}
			}

			@Override
			public void stop() {
				AgilityAssistant.resetAgilityWalk(player);
				player.turnPlayerTo(player.getX(), player.getY() - 1);
				Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
				player.logBalance = true;
				player.setDoingAgility(false);
			}
		}, 1);
	}

	public static boolean isGnomeCourseObject(final Player player, final int objectType) {
		if (player.getTransformed() > 0) {
			return true;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return false;
		}

		switch (objectType) {

			// Log balance.
			case 23145:
				logBalanceAction(player);
				return true;

			// Obstacle net
			case 23134:
				if (player.getY() != 3426) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility2 < 3000) {
					return true;
				}
				player.agility2 = System.currentTimeMillis();

				player.startAnimation(3063);
				player.cannotIssueMovement = true;

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						player.startAnimation(65535);
						player.cannotIssueMovement = false;
						player.getPA().movePlayer(player.getX(), 3424, 1);
						Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
						player.obstacleNetUp = true;
					}
				}, 4);
				return true;

			// Tree branch
			case 23559:
				if (player.getY() != 3423 && player.getY() != 3422) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility3 < 3000) {
					return true;
				}
				player.agility3 = System.currentTimeMillis();
				player.startAnimation(828);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						player.startAnimation(65535);
						player.getPA().movePlayer(2473, 3420, 2);
						Skilling.addSkillExperience(player, 5, ServerConstants.AGILITY, false);
						player.treeBranchUp = true;
					}
				}, 1);
				return true;

			// Balancing rope
			case 23557:
				if (player.getY() != 3420) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility4 < 3000) {
					return true;
				}
				player.agility4 = System.currentTimeMillis();
				AgilityAssistant.agilityAnimation(player, 762, false, 6, 0);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (player.getX() == 2483 && player.getY() == 3420) {
							container.stop();
						}
					}

					@Override
					public void stop() {
						AgilityAssistant.resetAgilityWalk(player);
						player.turnPlayerTo(player.getX() + 1, player.getY());
						Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
						player.balanceRope = true;
					}
				}, 1);
				return true;

			// Tree branch
			case 23560:
			case 23561:
				if (System.currentTimeMillis() - player.agility5 < 3000) {
					return true;
				}
				player.agility5 = System.currentTimeMillis();
				player.startAnimation(828);

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						player.startAnimation(65535);
						player.getPA().movePlayer(2486, 3419, 0);
						Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
						player.treeBranchDown = true;
					}
				}, 1);
				return true;

			// Obstacle net
			case 23135:
				if (player.getY() != 3425) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility6 < 3000) {
					return true;
				}
				player.agility6 = System.currentTimeMillis();
				player.setDoingAgility(true);
				player.agilityEndX = player.getX();
				player.agilityEndY = 3427;
				player.startAnimation(3063);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						player.turnPlayerTo(player.getX(), 3426);
						player.getPA().movePlayer(player.getX(), 3427, 0);
						container.stop();
					}

					@Override
					public void stop() {
						Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
						player.obstacleNetOver = true;
						player.setDoingAgility(false);
					}
				}, 4);
				return true;

			// Obstacle pipe
			case 23139:
			case 23138:
				obstaclePipeAction(player);
				return true;
		}

		return false;
	}

	/**
	 * Apply the obstacle pipe action.
	 *
	 * @param player The associated player.
	 */
	private static void obstaclePipeAction(final Player player) {
		AgilityAssistant.agilityAnimation(player, AgilityAssistant.OBSTACLE_PIPE_ANIMATION, false, 0, 7);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getY() == 3437) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				AgilityAssistant.resetAgilityWalk(player);
				player.turnPlayerTo(player.getX(), player.getY() + 1);
				if (player.logBalance && player.obstacleNetUp && player.treeBranchUp && player.balanceRope && player.treeBranchDown && player.obstacleNetOver) {
					int chance = 0;
					int amount = 1;
					amount = amount * (Misc.hasPercentageChance(chance) ? 2 : 1);
					Skilling.addSkillExperience(player, 47, ServerConstants.AGILITY, false);
					Skilling.petChance(player, 47, 499, 640, ServerConstants.AGILITY, null);
					//player.agilityPoints += amount;
					ItemAssistant.addItemToInventoryOrDrop(player, 11849, amount);
					String s = amount == 1 ? "" : "s";
					player.playerAssistant.sendFilterableMessage("<col=0008f7>The gnomes award you with " + amount + " mark" + s + " of grace for your efforts.");
					player.skillingStatistics[SkillingStatistics.AGILITY_LAPS]++;
				} else {
					Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
				}
				player.logBalance = false;
				player.obstacleNetUp = false;
				player.treeBranchUp = false;
				player.balanceRope = false;
				player.treeBranchDown = false;
				player.obstacleNetOver = false;
				player.setDoingAgility(false);
			}
		}, 1);
	}
}
