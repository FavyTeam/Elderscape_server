package game.content.skilling.agility;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.entity.Entity;
import game.entity.MovementState;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

public class DonatorCourse {

	private static int ROPE_ANIMATION = 762;

	private static int SWIMMING_ANIMATION = 772;

	private static int ROPE_FAIL_ANIMATION = 764;

	private static int STEPPING_STONE_ANIMATION = 769;

	/**
	 * Apply the balancing rope action.
	 *
	 * @param player The associated player.
	 */
	private static void rockClimbAction(Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.getDoingAgility()) {
			return;
		}
		if (!DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.EXTREME_DONATOR)) {
			return;
		}
		if (player.baseSkillLevel[ServerConstants.AGILITY] < 75) {
			player.getPA().sendMessage("You need an agility level of at least 75 to use this course.");
			return;
		}
		if (player.getX() == 2553 && player.getY() == 2714) {
			player.setDoingAgility(true);
			player.agilityEndX = 2553;
			player.agilityEndY = 2712;
			AgilityAssistant.agilityAnimation(player, ROPE_ANIMATION, false, 0, -2);
			rockClimbReward(player);
		}
	}

	/**
	 * Apply the balancing rope action.
	 *
	 * @param player The associated player.
	 */
	private static void balancingRopeAction(Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.getDoingAgility()) {
			return;
		}
		if (player.getX() == 2556 && player.getY() == 2708) {
			player.setDoingAgility(true);
			player.agilityEndX = 2556;
			player.agilityEndY = 2700;
			if (Misc.hasPercentageChance(10)) {
				failBalancingRope(player);
			}
			else {
				AgilityAssistant.agilityAnimation(player, ROPE_ANIMATION, false, 0, -8);
				balancingRopeReward(player);
			}
		}
	}

	/**
	 * Apply the swimming action.
	 *
	 * @param player The associated player.
	 */
	private static void swimToRockAction(Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.getDoingAgility()) {
			return;
		}
		if (player.getX() == 2553 && player.getY() == 2697) {
			player.setDoingAgility(true);
			player.agilityEndX = 2547;
			player.agilityEndY = 2698;
			swimToRockCycle(player);
		}
	}

	private static void swimToRockCycle(Player player) {
		player.turnPlayerTo(player.getX(), player.getY() + 1);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {

					case 1:
						AgilityAssistant.agilityAnimation(player, 821, false, 0, 1);
						break;

					case 2:
						player.turnPlayerTo(player.getX() - 1, player.getY());
						break;

					case 3:
						AgilityAssistant.agilityAnimation(player, SWIMMING_ANIMATION, false, -6, 0);
						break;

					case 9:
						swimToRockReward(player);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	/**
	 * Apply the swimming action.
	 *
	 * @param player The associated player.
	 */
	private static void steppingStoneAction(Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (player.getDoingAgility()) {
			return;
		}
		if (player.getX() == 2553 && player.getY() == 2712) {
			player.setDoingAgility(true);
			player.agilityEndX = 2556;
			player.agilityEndY = 2708;
			player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
				int i = 0;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					i++;
					switch (i) {
						case 1:
							steppingStoneCycle(player, player.getX(), player.getY() - 1, 0, -1);
							break;

						case 5:
							steppingStoneCycle(player, player.getX() + 1, player.getY(), 1, 0);
							break;

						case 9:
							steppingStoneCycle(player, player.getX() + 1, player.getY(), 1, 0);
							break;

						case 13:
							steppingStoneCycle(player, player.getX(), player.getY() - 1, 0, -1);
							break;

						case 17:
							steppingStoneCycle(player, player.getX(), player.getY() - 1, 0, -1);
							break;

						case 21:
							steppingStoneCycle(player, player.getX() + 1, player.getY(), 1, 0);
							break;

						case 25:
							steppingStoneCycle(player, player.getX(), player.getY() - 1, 0, -1);
							break;

						case 29:
							steppingStoneReward(player, false);
							container.stop();
							break;
					}
				}

				@Override
				public void stop() {
				}
			}, 1);
		}
		else if (player.getX() == 2538 && player.getY() == 2704) {
			player.setDoingAgility(true);
			player.agilityEndX = 2530;
			player.agilityEndY = 2704;
			player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
				int i = 0;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					i++;
					switch (i) {
						case 1:
							steppingStoneCycle(player, player.getX() - 2, player.getY(), -2, 0);
							break;

						case 5:
							steppingStoneCycle(player, player.getX() - 1, player.getY(), -1, 0);
							break;

						case 9:
							steppingStoneCycle(player, player.getX() - 1, player.getY(), -1, 0);
							break;

						case 13:
							steppingStoneCycle(player, player.getX() - 1, player.getY(), -1, 0);
							break;

						case 17:
							steppingStoneCycle(player, player.getX() - 1, player.getY(), -1, 0);
							break;

						case 21:
							steppingStoneCycle(player, player.getX() - 1, player.getY(), -1, 0);
							break;

						case 25:
							steppingStoneCycle(player, player.getX() - 1, player.getY(), -1, 0);
							break;

						case 29:
							steppingStoneReward(player, false);
							container.stop();
							break;
					}
				}

				@Override
				public void stop() {
				}
			}, 1);
		}

		else if (player.getX() == 2527 && player.getY() == 2706) {
			player.setDoingAgility(true);
			player.agilityEndX = 2527;
			player.agilityEndY = 2712;
			player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
				int i = 0;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					i++;
					switch (i) {
						case 1:
							steppingStoneCycle(player, player.getX(), player.getY() + 1, 0, 1);
							break;

						case 5:
							steppingStoneCycle(player, player.getX(), player.getY() + 1, 0, 1);
							break;

						case 9:
							steppingStoneCycle(player, player.getX(), player.getY() + 1, 0, 1);
							break;

						case 13:
							steppingStoneCycle(player, player.getX(), player.getY() + 1, 0, 1);
							break;

						case 17:
							steppingStoneCycle(player, player.getX(), player.getY() + 2, 0, 2);
							break;

						case 21:
							steppingStoneReward(player, true);
							container.stop();
							break;
					}
				}

				@Override
				public void stop() {
				}
			}, 1);
		}
	}

	private static void steppingStoneCycle(Player player, int turnToX, int turnToY, int x, int y) {
		player.turnPlayerTo(turnToX, turnToY);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {

					case 1:
						player.startAnimation(STEPPING_STONE_ANIMATION);
						break;

					case 2:
						player.getPA().movePlayer(player.getX() + x, player.getY() + y, 0);
						Skilling.addSkillExperience(player, 15, ServerConstants.AGILITY, false);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	/**
	 * Apply the action for failing to complete the rope balance.
	 *
	 * @param player The associated player.
	 */
	private static void failBalancingRope(Player player) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {
					case 1:
						AgilityAssistant.agilityAnimation(player, ROPE_ANIMATION, false, 0, -3);
						break;

					case 4:
						player.setMovementState(MovementState.DISABLED);
						player.startAnimation(ROPE_FAIL_ANIMATION);
						break;

					case 6:
						player.getPA().movePlayer(2557, 2705, 0);
						player.setMovementState(MovementState.WALKABLE);
						player.startAnimation(765);
						player.getPA().createPlayersStillGfx(68, player.getX(), player.getY(), player.getHeight(), 1);
						break;

					case 7:
						player.resetAnimation();
						AgilityAssistant.agilityAnimation(player, SWIMMING_ANIMATION, false, 0, -5);
						player.forcedChatUpdateRequired = true;
						player.forcedChat("*Glug*", false, false);
						break;

					case 12:
						int hp = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
						int amount = hp / 10 + 3;
						Combat.createHitsplatOnPlayerNormal(player, amount, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
						player.getPA().sendMessage("You swallow some of the salty water and feel rather ill...");
						player.forcedChatUpdateRequired = true;
						player.forcedChat("Ugh, that didn't taste very good...", false, false);
						player.setDoingAgility(false);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	/**
	 * Reward the player for completing the rock climbing action.
	 *
	 * @param player The associated player.
	 */
	private static void rockClimbReward(final Player player) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (player.getY() == 2712) {
					player.setDoingAgility(false);
					container.stop();
				}
			}

			@Override
			public void stop() {
				AgilityAssistant.resetAgilityWalk(player);
				player.turnPlayerTo(player.getX(), player.getY() - 1);
				Skilling.addSkillExperience(player, 25, ServerConstants.AGILITY, false);
				player.setDoingAgility(false);
			}
		}, 1);
	}

	/**
	 * Reward the player for completing the stepping stone obstacle.
	 *
	 * @param player The associated player.
	 */
	private static void steppingStoneReward(final Player player, boolean finishedCourse) {
		player.setDoingAgility(false);
		AgilityAssistant.resetAgilityWalk(player);
		Skilling.addSkillExperience(player, finishedCourse ? 500 : 20, ServerConstants.AGILITY, false);
		if (finishedCourse) {
			player.getPA().sendMessage("<col=0008f7>You receive 5 marks of grace for your efforts.");
			ItemAssistant.addItemToInventoryOrDrop(player, 11849, 5);
			if (Misc.hasPercentageChance(1)) {
				player.getPA().sendMessage("<col=0008f7>You also receive some " + ServerConstants.getMainCurrencyName() + " as a bonus reward.");
				ItemAssistant.addItemToInventoryOrDrop(player, ServerConstants.getMainCurrencyId(), GameType.isOsrsEco() ? 5000000 : 25000);
			}
		}
	}

	/**
	 * Reward the player for completing the balancing rope once the player reaches the other end.
	 *
	 * @param player The associated player.
	 */
	private static void balancingRopeReward(final Player player) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (player.getY() == 2700) {
					player.setDoingAgility(false);
					container.stop();
				}
			}

			@Override
			public void stop() {
				AgilityAssistant.resetAgilityWalk(player);
				player.turnPlayerTo(player.getX(), player.getY() - 1);
				Skilling.addSkillExperience(player, 35, ServerConstants.AGILITY, false);
				player.setDoingAgility(false);
			}
		}, 1);
	}

	/**
	 * Reward the player for completing the balancing rope once the player reaches the other end.
	 *
	 * @param player The associated player.
	 */
	private static void swimToRockReward(final Player player) {
		AgilityAssistant.resetAgilityWalk(player);
		player.turnPlayerTo(player.getX() - 1, player.getY());
		Skilling.addSkillExperience(player, 30, ServerConstants.AGILITY, false);
		player.setDoingAgility(false);
	}

	public static boolean isDonatorCourseObject(final Player player, final int objectType) {
		if (player.getTransformed() > 0) {
			return true;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return false;
		}

		if (!GameType.isOsrs()) {
			return false;
		}

		switch (objectType) {

			case 3551:
				balancingRopeAction(player);
				return true;

			case 1996:
				swimToRockAction(player);
				return true;

			case 23640:
				rockClimbAction(player);
				return true;

			case 16466:
				steppingStoneAction(player);
				return true;
		}

		return false;
	}

}
