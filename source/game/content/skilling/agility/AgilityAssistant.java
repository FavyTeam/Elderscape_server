package game.content.skilling.agility;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.OverlayTimer;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.object.custom.DoorEvent;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Agility related.
 *
 * @author MGT Madness.
 */
public class AgilityAssistant {

	public final static int LOG_BALANCE_ANIMATION = 762;

	public final static int OBSTACLE_PIPE_ANIMATION = 844;

	public final static int ROPE_SWING_ANIMATION = 751;

	public static void giveAgilityExperienceReward(Player player) {
		switch (player.currentAgilityArea) {
			case "WILDERNESS COURSE":
				scanAgilityExperienceArray(player, WildernessCourse.wildernessCourseExperience);
				break;
		}
	}

	public static void scanAgilityExperienceArray(Player player, int[][] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i][0] == player.getX() && array[i][1] == player.getY()) {
				Skilling.addSkillExperience(player, array[i][2], ServerConstants.AGILITY, false);
			}
		}
	}

	public static void agilityAction(Player player, int animation, boolean run, int xTravel, int yTravel, int endX, int endY) {

		AgilityAssistant.agilityAnimation(player, animation, run, xTravel, yTravel);
		atPositionEvent(player, endX, endY);
	}

	public static void atPositionEvent(final Player player, final int x, final int y) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getX() == x && player.getY() == y) {
					container.stop();
				}

				// Door exit.
				if (player.getY() == 3930 && player.getObjectId() == 2309) {
					DoorEvent.canUseAutomaticDoor(player, 2, false, 2307, 2998, 3931, 2, 3);
					DoorEvent.canUseAutomaticDoor(player, 2, false, 2308, 2997, 3931, 0, 3);
				}

				// Door entrance.
				if (player.getY() == 3917 && player.getObjectId() == 2307) {
					DoorEvent.canUseAutomaticDoor(player, 1, false, 2309, 2998, 3917, 2, 3);
				}
			}

			@Override
			public void stop() {
				sendAgilityObstacleCompletedMessage(player);
				giveAgilityExperienceReward(player);
			}
		}, 1);
	}

	public static void sendAgilityObstacleCompletedMessage(Player player) {
		if (player.getAgilityCourseCompletedMessage() == -1) {
			return;
		}
		if (player.currentAgilityArea.equals("WILDERNESS COURSE")) {
			player.playerAssistant.sendFilterableMessage(WildernessCourse.wildernessCourseObstacleCompletedMessage[player.getAgilityCourseCompletedMessage()]);
		}
		player.setAgilityCourseCompletedMessage(-1);
	}

	public static void logBalanceAction(Player player, int travelX, int travelY) {
		player.playerAssistant.sendFilterableMessage("You walk carefully across the slippery log...");
		player.setAgilityCourseCompletedMessage(3);
		player.resetPlayerTurn();
		agilityAction(player, AgilityAssistant.LOG_BALANCE_ANIMATION, false, travelX, travelY, player.getX() + travelX, player.getY() + travelY);
	}

	public static void resetAgilityWalk(final Player player) {
		player.runModeOn = true;
		player.getPA().sendFrame36(173, 1, false);
		player.playerWalkIndex = 0x333;
		player.playerRunIndex = 0x338;
		player.getPA().requestUpdates();
		Combat.updatePlayerStance(player);
	}

	public static void agilityActionCompleted(Player player) {
		if (player.getX() == player.agilityEndX && player.getY() == player.agilityEndY) {
			AgilityAssistant.resetAgilityWalk(player);
			player.setDoingAgility(false);
		}
	}

	public static void agilityAnimation(final Player player, final int walkAnimation, boolean run, final int x, final int y) {
		player.agilityEndX = player.getX() + x;
		player.agilityEndY = player.getY() + y;
		player.setDoingAgility(true);
		if (run) {
			if (!player.runModeOn) {
				player.runModeOn = true;
				player.getPA().sendFrame36(173, 0, false);
			}
			player.playerRunIndex = walkAnimation;
		} else {

			player.runModeOn = false;
			player.getPA().sendFrame36(173, 0, false);
			player.playerWalkIndex = walkAnimation;
		}
		player.getPA().requestUpdates();
		Movement.travelTo(player, x, y);
	}

	/**
	 * @return The drain rate of the run energy of the player, depending on agility level and agile top/bottom clothes.
	 */
	private static double runEnergyDrain(Player player) {
		double drainRate = 0;
		drainRate = 1.0 - ((player.baseSkillLevel[ServerConstants.AGILITY] / 190.0));
		double modifier = 1;

		// Boots of lightness.
		if (ItemAssistant.hasItemEquippedSlot(player, 88, ServerConstants.FEET_SLOT)) {
			modifier *= 0.95;
		}

		// Agility capes
		if (Skilling.hasMasterCapeWorn(player, 9771)) {
			modifier *= 0.9; // 10% double what a graceful cape is
		}

		if (GameType.isOsrs()) {
			// Graceful pieces
			for (int index = 0; index < ServerConstants.GRACEFUL_PIECES_OSRS.length; index++) {
				int gracefulId = ServerConstants.GRACEFUL_PIECES_OSRS[index][0];
				if (ItemAssistant.hasItemEquippedSlot(player, gracefulId, ServerConstants.GRACEFUL_PIECES_OSRS[index][1])) {
					modifier *= 0.95; //5% per piece worn, max of 30% reduction
				}
			}
		}

		if (player.getStaminaPotionTimer() > 0) {
			modifier *= 0.3; //Stamina potion decreases run energy used by 70%
		}

		if (System.currentTimeMillis() - player.drainRunEnergyFaster < Misc.getMinutesToMilliseconds(1)) {
			drainRate *= 4;
		}
		drainRate *= modifier;
		return drainRate;
	}

	public static void updateRunEnergyInterface(Player player) {
		int energy = (int) player.runEnergy;
		player.getPA().sendFrame126(energy + "%", 149);

		String status = player.runModeOn ? "on" : "off";
		player.getPA().sendFrame126("Run (" + status + ") Energy " + energy + "%", 22187);
	}

	public static void staminaEffectOn(Player player) {
		player.getPA().sendMessage(":packet:staminaeffecton:");
		OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_STAMINA, player.getStaminaPotionTimer() * 10);
	}

	public static void staminaEffectOff(Player player) {
		player.getPA().sendMessage(":packet:staminaeffectoff:");
		OverlayTimer.stopOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_STAMINA);
	}

	public static void removeStaminaEffect(Player player) {
		player.setStaminaPotionTimer(0);
		player.getPA().sendMessage(":packet:staminaeffectoff:");
	}

	/**
	 * Drain the run energy.
	 */
	public static void agilityDrain(Player player) {
		if (player.bot) {
			return;
		}
		if (player.getTank()) {
			return;
		}
		if (player.runEnergy > 1) {
			player.timeRan = System.currentTimeMillis();
			player.runEnergy -= runEnergyDrain(player);
			updateRunEnergyInterface(player);
			if (player.runEnergy == 0) {
				player.runModeOn = false;
				InterfaceAssistant.informClientRestingState(player, "off");
				player.getPA().sendFrame36(173, 0, false);
			}
		} else {
			player.runModeOn = false;
			InterfaceAssistant.informClientRestingState(player, "off");
			player.getPA().sendFrame36(173, 0, false);
		}
	}

	/**
	 * Gaining run energy.
	 */
	public static void runEnergyGain(final Player player) {
		if (player.bot) {
			return;
		}
		if (player.energyGainEvent) {
			return;
		}
		if (player.runEnergy >= 100) {
			return;
		}
		player.energyGainEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.runEnergy >= 100) {
					container.stop();
					return;
				}
				if (System.currentTimeMillis() > player.agilityRestoreDelay + player.lastRunRecovery && System.currentTimeMillis() - player.timeRan >= 500) {
					player.runEnergy++;
					if (player.resting) {
						player.runEnergy++;
					}
					if (player.runEnergy > 100) {
						player.runEnergy = 100;
					}
					player.lastRunRecovery = System.currentTimeMillis();
					updateRunEnergyInterface(player);
				}
			}

			@Override
			public void stop() {
				player.energyGainEvent = false;
			}
		}, 1);

	}

	/**
	 * Start resting.
	 */
	public static void startResting(Player player) {
		if (player.doingAnAction()) {
			return;
		}
		player.startAnimation(11001);
		player.getPA().closeInterfaces(true);
		Combat.resetPlayerAttack(player);
		player.resting = true;
		Movement.stopMovement(player);
		InterfaceAssistant.informClientRestingState(player, "on");
		player.agilityRestoreDelay = 700;
		restingEvent(player);
	}

	private static void restingEvent(final Player player) {
		if (player.restingEvent) {
			return;
		}
		player.restingEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.resting && !Combat.inCombat(player)) {
					player.startAnimation(11001);
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.restingEvent = false;
			}
		}, 10); // 10 Because, if someone enters the region, they will see this player using this emote.
	}

	/**
	 * Stop resting.
	 */
	public static void stopResting(Player player) {
		if (!player.resting) {
			return;
		}
		player.resting = false;
		player.agilityRestoreDelay = 5000;
		player.startAnimation(65535);
		InterfaceAssistant.informClientRestingState(player, "off");
	}

}
