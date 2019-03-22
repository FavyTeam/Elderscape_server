package game.content.skilling.agility;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.object.custom.DoorEvent;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Wilderness agility course.
 *
 * @author MGT Madness, created on 29-07-2015.
 */
public class WildernessCourse {
	private final static int DOOR_ENTRANCE = 23555;

	private final static int DOOR_EXIT = 23552;

	private final static int DOOR_EXIT_NEXT = 23554;

	private final static int OBSTACLE_PIPE = 23137;

	private final static int ROPE_SWING = 23132;

	private final static int STEPPING_STONE = 23556;

	private final static int LOG_BALANCE = 23542;

	private final static int ROCKS = 23640;

	public final static int[][] wildernessCourseExperience =
			{
					{3004, 3950, 13
							// Obstacle pipe.
					},
					{3005, 3958, 20
							// Rope swing.
					},
					{2997, 3960, 20
							// Stepping stone.
					},
					{2994, 3945, 20
							// Log balance.
					}
			};

	public final static String[] wildernessCourseObstacleCompletedMessage =
			{
					"You skillfully balance across the ridge.", // Door entrance.
					"You skillfully swing across.",
					"...You safely cross to the other side.", // Stepping stone.
					"You skillfully edge across the gap.", // Log balance.
			};

	private static int[] wildernessCourseObjects =
			{DOOR_ENTRANCE, OBSTACLE_PIPE, ROPE_SWING, STEPPING_STONE, LOG_BALANCE, ROCKS, DOOR_EXIT, DOOR_EXIT_NEXT};

	public static boolean isWildernessCourseObject(Player player, int objectType) {
		if (!Area.inDangerousPvpArea(player)) {
			return false;
		}
		if (player.getTransformed() > 0) {
			return false;
		}
		for (int i = 0; i < wildernessCourseObjects.length; i++) {
			if (objectType == wildernessCourseObjects[i]) {
				if (RandomEvent.isBannedFromSkilling(player)) {
					return true;
				}
				return true;
			}
		}
		return false;
	}



	/**
	 * Handles Wilderness agility course objects.
	 *
	 * @param player The player
	 * @param objectType The object used by the player
	 */
	public static void startWildernessCourse(final Player player, final int objectType) {

		if (player.isFrozen()) {
			return;
		}
		player.currentAgilityArea = "WILDERNESS COURSE";
		switch (objectType) {

			case DOOR_ENTRANCE:
				doorEntrance(player);
				break;

			case DOOR_EXIT:
			case DOOR_EXIT_NEXT:
				doorExit(player);
				break;

			case OBSTACLE_PIPE:
				obstaclePipeAction(player);
				break;

			case ROPE_SWING:
				ropeSwingAction(player);
				break;

			case STEPPING_STONE:
				steppingStoneAction(player);
				break;

			case LOG_BALANCE:
				player.wildernessCourseLogBalance = true;
				AgilityAssistant.logBalanceAction(player, -8, 0);
				break;

			case ROCKS:
				rocksAction(player);
				break;
		}
	}

	/**
	 * Door entrance requirements and door handling.
	 *
	 * @param player The associated player.
	 */
	private static void doorEntrance(Player player) {
		if (player.baseSkillLevel[ServerConstants.AGILITY] < 80) {
			player.playerAssistant.sendMessage("80 agility is required to use this course.");
			player.playerAssistant.sendMessage("You cannot enter if you are in combat.");
			return;
		}
		if (Combat.inCombatAlert(player)) {
			return;
		}
		DoorEvent.openAutomaticDoor(player);
	}

	/**
	 * Door entrance player movement.
	 *
	 * @param player The associated player.
	 */
	public static void doorEntranceAction(Player player) {
		player.setAgilityCourseCompletedMessage(0);
		player.playerAssistant.sendFilterableMessage("You go through the gate and try to edge over the ridge...");
		player.resetPlayerTurn();
		AgilityAssistant.agilityAction(player, AgilityAssistant.LOG_BALANCE_ANIMATION, false, 0, 15, 2998, 3931);
	}

	/**
	 * Door exit door handling.
	 *
	 * @param player The associated player.
	 */
	private static void doorExit(Player player) {
		if (Combat.inCombatAlert(player)) {
			return;
		}
		DoorEvent.openAutomaticDoor(player);
	}

	/**
	 * Door exit player movement.
	 *
	 * @param player The associated player.
	 */
	public static void doorExitAction(Player player) {
		if (!DoorEvent.canUseAutomaticDoor(player, 2, true, 23552, 2998, 3931, 2, 3)) {
			return;
		}
		DoorEvent.canUseAutomaticDoor(player, 2, false, 23552, 2997, 3931, 4, 3);
		player.resetPlayerTurn();
		AgilityAssistant.agilityAction(player, AgilityAssistant.LOG_BALANCE_ANIMATION, false, 0, -15, 2998, 3916);
	}

	private static void obstaclePipeAction(Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		player.resetPlayerTurn();
		AgilityAssistant.agilityAction(player, AgilityAssistant.OBSTACLE_PIPE_ANIMATION, false, 0, 13, 3004, 3950);
		player.wildernessCourseObstaclePipe = true;
	}

	private static void ropeSwingAction(Player player) {
		player.setAgilityCourseCompletedMessage(1);
		player.resetPlayerTurn();
		AgilityAssistant.agilityAction(player, AgilityAssistant.ROPE_SWING_ANIMATION, true, 0, 5, 3005, 3958);
		player.wildernessCourseRopeSwing = true;
	}

	private static void steppingStoneAction(final Player player) {
		player.playerAssistant.sendFilterableMessage("You carefully start crossing the stepping stones...");
		player.setAgilityCourseCompletedMessage(2);
		player.setDoingAgility(true);
		player.agilityEndX = 2996;
		player.agilityEndY = 3960;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.stoneTimer++;
				if (player.stoneTimer == 1) {
					player.startAnimation(769);

				} else if (player.stoneTimer == 3) {
					player.getPA().movePlayer(3001, 3960, 0);

				} else if (player.stoneTimer == 4) {
					player.startAnimation(769);

				} else if (player.stoneTimer == 6) {
					player.getPA().movePlayer(3000, 3960, 0);

				} else if (player.stoneTimer == 7) {
					player.startAnimation(769);

				} else if (player.stoneTimer == 9) {
					player.getPA().movePlayer(2999, 3960, 0);

				} else if (player.stoneTimer == 10) {
					player.startAnimation(769);

				} else if (player.stoneTimer == 12) {
					player.getPA().movePlayer(2998, 3960, 0);
				} else if (player.stoneTimer == 13) {
					player.startAnimation(769);
				} else if (player.stoneTimer == 15) {
					player.getPA().movePlayer(2997, 3960, 0);
				} else if (player.stoneTimer == 16) {
					player.startAnimation(769);
				} else if (player.stoneTimer == 18) {
					player.getPA().movePlayer(2996, 3960, 0);
				} else if (player.stoneTimer == 19) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.wildernessCourseSteppingStone = true;
				AgilityAssistant.sendAgilityObstacleCompletedMessage(player);
				AgilityAssistant.giveAgilityExperienceReward(player);
				player.stoneTimer = 0;
				AgilityAssistant.resetAgilityWalk(player);
				player.setDoingAgility(false);
			}
		}, 1);

	}

	private static void rocksAction(Player player) {
		player.resetPlayerTurn();
		player.playerAssistant.sendFilterableMessage("You reach the top.");
		player.getPA().movePlayer(2996, 3933, 0);
		if (player.wildernessCourseObstaclePipe && player.wildernessCourseLogBalance && player.wildernessCourseSteppingStone && player.wildernessCourseRopeSwing) {
			Skilling.petChance(player, 499, 499, 640, ServerConstants.AGILITY, null);
			Skilling.addSkillExperience(player, 499, ServerConstants.AGILITY, false);
			int chance = 0;
			int amount = 3;
			amount = amount * (Misc.hasPercentageChance(chance) ? 2 : 1);
			Skilling.addSkillExperience(player, 47, ServerConstants.AGILITY, false);
			ItemAssistant.addItemToInventoryOrDrop(player, 11849, amount);
			String s = amount == 1 ? "" : "s";
			player.playerAssistant.sendFilterableMessage("<col=0008f7>The wizards award you " + amount + " mark" + s + " of grace for your efforts.");
			player.skillingStatistics[SkillingStatistics.AGILITY_LAPS]++;
		}
		player.wildernessCourseObstaclePipe = false;
		player.wildernessCourseLogBalance = false;
		player.wildernessCourseSteppingStone = false;
		player.wildernessCourseRopeSwing = false;
		Achievements.checkCompletionMultiple(player, "1071");
	}
}
