package game.content.skilling.agility;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Handles the Barbarian agility course
 *
 * @author MGT Madness
 * @see modified: 26-10-2013
 */
public class BarbarianCourse {

	private final static int OBSTACLE_PIPE = 20210;

	private final static int ROPE_SWING = 23131;

	private final static int LOG_BALANCE = 23144;

	private final static int OBSTACLE_NET = 20211;

	private final static int CRUMBLING_WALL = 1948;

	private final static int BALANCING_LEDGE = 23547;

	/**
	 * Obstacle pipe.
	 *
	 * @param player The associated player.
	 */
	private static void obstaclePipe(final Player player) {
		if (player.baseSkillLevel[ServerConstants.AGILITY] < 50) {
			player.playerAssistant.sendMessage("You need at least 50 agility to use this course.");
			return;
		}
		int move = 0;

		if (player.getX() == 2552 && player.getY() == 3561) {
			move = 1;
		} else if (player.getX() == 2552 && player.getY() == 3558) {
			move = 2;
		}
		if (move == 0) {
			return;
		}
		if (System.currentTimeMillis() - player.agility1 < 3000) {
			return;
		}
		final int move1 = move;
		player.agility1 = System.currentTimeMillis();
		AgilityAssistant.agilityAnimation(player, 844, false, 0, move == 1 ? -3 : 3);

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getY() == (move1 == 1 ? 3558 : 3561)) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.turnPlayerTo(player.getX(), player.getY() - 1);
				AgilityAssistant.resetAgilityWalk(player);
				Skilling.addSkillExperience(player, 10, ServerConstants.AGILITY, false);
			}
		}, 1);

	}

	private static void ropeSwingAction(final Player player) {

		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (System.currentTimeMillis() - player.agility1 < 3000) {
			return;
		}
		player.agility1 = System.currentTimeMillis();
		player.ropeSwing = true;
		AgilityAssistant.agilityAnimation(player, AgilityAssistant.ROPE_SWING_ANIMATION, true, 0, -6);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getY() == 3548) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.turnPlayerTo(player.getX(), player.getY() - 1);
				AgilityAssistant.resetAgilityWalk(player);
				Skilling.addSkillExperience(player, 22, ServerConstants.AGILITY, false);
			}
		}, 1);
	}

	/**
	 * Handles Barbarian agility course objects.
	 *
	 * @param player The player
	 * @param objectType The object used by the player
	 */
	public static boolean isBarbarianCourseObject(final Player player, final int objectType) {
		if (player.getTransformed() > 0) {
			return true;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return false;
		}

		switch (objectType) {

			case OBSTACLE_PIPE:
				obstaclePipe(player);
				return true;

			case ROPE_SWING:
				ropeSwingAction(player);
				return true;

			case LOG_BALANCE:
				logBalanceAction(player);
				return true;

			case OBSTACLE_NET:
				if (player.getX() != 2539) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility1 < 3000) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();

				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				player.startAnimation(828);
				player.obstacleNet = true;

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						player.startAnimation(65535);
						player.getPA().movePlayer(2537, 3546, 1);
						Skilling.addSkillExperience(player, 8, ServerConstants.AGILITY, false);
					}
				}, 1);

				return true;

			case BALANCING_LEDGE:
				if (player.getX() == 2536 && player.getY() == 3547) {
					if (System.currentTimeMillis() - player.agility2 < 6000) {
						return true;
					}
					player.agility2 = System.currentTimeMillis();
					player.balancingLedge = true;
					AgilityAssistant.agilityAnimation(player, 756, false, -4, 0);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getX() == 2532) {
								Movement.travelTo(player, 0, -1);
							}
							if (player.getY() == 3546) {

								ladderAction(player);
								container.stop();
							}
						}

						@Override
						public void stop() {
							Skilling.addSkillExperience(player, 22, ServerConstants.AGILITY, false);
						}
					}, 1);
				}

				return true;


			case CRUMBLING_WALL:
				/* Start of first object. */
				if (player.getObjectX() == 2536 && player.getObjectY() == 3553) {
					firstCrumblingWallAction(player);
				}
				/* End of first object. */


				else if (player.getX() == 2538 && player.getY() == 3553) {
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();

					player.resetPlayerTurn();
					AgilityAssistant.agilityAnimation(player, 839, false, 2, 0);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getX() == 2540) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
						}
					}, 1);
				} else if (player.getX() == 2541 && player.getY() == 3553) {
					if (System.currentTimeMillis() - player.agility2 < 3000) {
						return true;
					}
					player.agility2 = System.currentTimeMillis();

					player.resetPlayerTurn();
					AgilityAssistant.agilityAnimation(player, 839, false, 2, 0);

					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getX() == 2543) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							if (player.ropeSwing && player.logBalance1 && player.obstacleNet && player.balancingLedge && player.Ladder) {
								int chance = 0;
								int amount = 2;
								amount = amount * (Misc.hasPercentageChance(chance) ? 2 : 1);
								player.ropeSwing = false;
								player.logBalance1 = false;
								player.obstacleNet = false;
								player.balancingLedge = false;
								player.Ladder = false;
								//player.agilityPoints += amount;
								ItemAssistant.addItemToInventoryOrDrop(player, 11849, amount);
								player.skillingStatistics[SkillingStatistics.AGILITY_LAPS]++;
								String s = amount == 1 ? "" : "s";
								player.playerAssistant.sendFilterableMessage("<col=0008f7>The barbarians award you " + amount + " Mark" + s + " of grace for your efforts.");
								Skilling.addSkillExperience(player, 200, ServerConstants.AGILITY, false);
								Skilling.petChance(player, 200, 499, 640, ServerConstants.AGILITY, null);
								Achievements.checkCompletionSingle(player, 1029);
							} else {
								Skilling.addSkillExperience(player, 14, ServerConstants.AGILITY, false);
							}
						}
					}, 1);
				}

				return true;

		}
		return false;

	}

	private static void logBalanceAction(final Player player) {
		if (System.currentTimeMillis() - player.agility2 < 3000) {
			return;
		}
		player.agility2 = System.currentTimeMillis();
		player.logBalance1 = true;
		AgilityAssistant.agilityAnimation(player, 762, false, -10, 0);

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getX() == 2541) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.turnPlayerTo(player.getX() - 1, player.getY());
				AgilityAssistant.resetAgilityWalk(player);
				Skilling.addSkillExperience(player, 14, ServerConstants.AGILITY, false);
			}
		}, 8);
	}

	public static void firstCrumblingWallAction(final Player player) {
		if (System.currentTimeMillis() - player.agility2 < 3000) {
			return;
		}
		player.agility2 = System.currentTimeMillis();
		player.resetPlayerTurn();
		AgilityAssistant.agilityAnimation(player, 839, false, 2, 0);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getX() == 2537) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				AgilityAssistant.resetAgilityWalk(player);
			}
		}, 1);
	}

	private static void ladderAction(final Player player) {
		player.Ladder = true;
		player.startAnimation(827);

		player.setDoingAgility(true);
		player.agilityEndX = 2532;
		player.agilityEndY = 3546;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.getPA().movePlayer(2532, 3546, 0);
				player.setDoingAgility(false);
				AgilityAssistant.resetAgilityWalk(player);
			}
		}, 2);
	}
}
