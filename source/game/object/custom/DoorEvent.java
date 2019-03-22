package game.object.custom;

import core.Server;
import game.content.skilling.agility.WildernessCourse;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Door events, opening and closing.
 *
 * @author MGT Madness, created on 30-07-2015.
 */
public class DoorEvent {

	public static boolean[] doors = new boolean[50];

	public static boolean canUseAutomaticDoor(final Player player, final int door, boolean permission, final int objectOpenId, final int objectOpenX, final int objectOpenY,
	                                          int objectOpenFace, final int objectCloseFace) {
		player.cannotIssueMovement = true;
		if (doors[door] && permission) {
			player.playerAssistant.sendMessage("Door is busy.");
			return false;
		}
		Server.objectManager.spawnGlobalObject(objectOpenId, objectOpenX, objectOpenY, player.getHeight(), objectOpenFace, 0);
		doors[door] = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				Server.objectManager.spawnGlobalObject(objectOpenId, objectOpenX, objectOpenY, player.getHeight(), objectCloseFace, 0);
				doors[door] = false;
				player.cannotIssueMovement = false;
			}
		}, 2);
		return true;
	}

	/**
	 * Open the door, it will automatically close after the player has entered.
	 *
	 * @param player The associated player.
	 */
	public static void openAutomaticDoor(Player player) {
		if (player.isFrozen()) {
			return;
		}
		boolean lockPickDoor = false;
		@SuppressWarnings("unused")
		boolean otherHut = false;

		if (Area.isWithInArea(player, 3037, 3045, 3948, 3960)) {
			lockPickDoor = true;
		}
		if (Area.isWithInArea(player, 3186, 3195, 3956, 3962)) {
			otherHut = true;
			lockPickDoor = true;
		}

		if (lockPickDoor) {
			if (System.currentTimeMillis() - player.lastLockPick < 1100) {
				return;
			}
			player.lastLockPick = System.currentTimeMillis();
			if (!ItemAssistant.hasItemAmountInInventory(player, 1523, 1)) {
				player.playerAssistant.sendMessage("I need a lockpick to pick this lock.");
				return;
			}

			if (Misc.hasPercentageChance(40)) {
				player.playerAssistant.sendMessage("You fail to pick the lock.");
				return;
			}
		}
		int travelX = 0;
		int travelY = 0;
		int openFace = 0;
		int closeFace = 0;
		int door = 0;
		int objectX = player.getObjectX();
		int objectY = player.getObjectY();
		int playerX = player.getX();
		int playerY = player.getY();
		player.cannotIssueMovement = true;

		boolean doorUsed = false;
		if (objectX == 2998 && objectY == 3917) {
			openFace = 2;
			travelX = -10;
			travelY = -10;
			closeFace = 3;
			doorUsed = true;
			door = 1;
			WildernessCourse.doorEntranceAction(player);

		} else if (objectX == 2998 && objectY == 3931 || objectX == 2997 && objectY == 3931) {
			openFace = 2;
			travelX = -10;
			travelY = -10;
			closeFace = 3;
			doorUsed = true;
			door = 2;
			WildernessCourse.doorExitAction(player);
		} else if (objectX == 3044 && objectY == 3956) {
			if (playerX == 3045) {
				travelX = -1;
				travelY = 0;
				openFace = 1;
				closeFace = 2;
				doorUsed = true;
			} else if (playerX == 3044) {
				travelX = 1;
				travelY = 0;
				openFace = 1;
				closeFace = 2;
				doorUsed = true;
			}
			door = 3;

		} else if (objectX == 3038 && objectY == 3956) {
			if (playerX == 3037) {
				travelX = 1;
				travelY = 0;
				openFace = 3;
				closeFace = 0;
				doorUsed = true;
			} else if (playerX == 3038) {
				travelX = -1;
				travelY = 0;
				openFace = 3;
				closeFace = 0;
				doorUsed = true;
			}
			door = 4;
		} else if (objectX == 3041 && objectY == 3959) {
			if (playerY == 3960) {
				travelX = 0;
				travelY = -1;
				openFace = 0;
				closeFace = 1;
				doorUsed = true;
			} else if (playerY == 3959) {
				travelX = 0;
				travelY = 1;
				openFace = 0;
				closeFace = 1;
				doorUsed = true;
			}
			door = 5;
		} else if (objectX == 3191 && objectY == 3963) {
			if (playerY == 3963) {
				travelX = 0;
				travelY = -1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			} else if (playerY == 3962) {
				travelX = 0;
				travelY = 1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			}
			door = 6;
		} else if (objectX == 3190 && objectY == 3957) {
			if (playerY == 3957) {
				travelX = 0;
				travelY = 1;
				openFace = 2;
				closeFace = 1;
				doorUsed = true;
			} else if (playerY == 3958) {
				travelX = 0;
				travelY = -1;
				openFace = 2;
				closeFace = 1;
				doorUsed = true;
			}
			door = 7;
		} else if (objectX == 3143 && objectY == 3443) {
			if (playerY == 3443) {
				travelX = 0;
				travelY = 1;
				openFace = 2;
				closeFace = 1;
				doorUsed = true;
			} else if (playerY == 3444) {
				travelX = 0;
				travelY = -1;
				openFace = 2;
				closeFace = 1;
				doorUsed = true;
			}
			door = 8;
		} else if (objectX == 2611 && objectY == 3394) {
			if (playerY == 3393) {
				travelX = 0;
				travelY = 1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			} else {
				travelX = 0;
				travelY = -1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			}
			door = 9;
		} else if (objectX == 2611 && objectY == 3398) {
			if (playerY == 3398) {
				travelX = 0;
				travelY = 1;
				openFace = 4;
				closeFace = 1;
				doorUsed = true;
			} else {
				travelX = 0;
				travelY = -1;
				openFace = 4;
				closeFace = 1;
				doorUsed = true;
			}
			door = 9;
		} else if (objectX == 2933 && objectY == 3289) {
			if (playerY == 3289) {
				travelX = 0;
				travelY = -1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			} else {
				travelX = 0;
				travelY = 1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			}
			door = 10;
		} else if (objectX == 3015 && objectY == 3333) {
			if (playerX == 3016) {
				travelX = -1;
				travelY = 0;
				openFace = 3;
				closeFace = 2;
				doorUsed = true;
			} else {
				travelX = 1;
				travelY = 0;
				openFace = 3;
				closeFace = 2;
				doorUsed = true;
			}
			door = 11;
		} else if (objectX == 3013 && objectY == 3335) {
			if (playerY == 3334) {
				travelX = 0;
				travelY = 1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			} else {
				travelX = 0;
				travelY = -1;
				openFace = 2;
				closeFace = 3;
				doorUsed = true;
			}
			door = 12;
		} else if (objectX == 3014 && objectY == 3340) {
			if (playerX == 3013) {
				travelX = 1;
				travelY = 0;
				openFace = 3;
				closeFace = 0;
				doorUsed = true;
			} else {
				travelX = -1;
				travelY = 0;
				openFace = 3;
				closeFace = 0;
				doorUsed = true;
			}
			door = 13;
		} else if (objectX == 2854 && objectY == 3546) {
			if (playerY == 3547) {
				travelX = 0;
				travelY = -3;
				openFace = 3;
				closeFace = 0;
				doorUsed = true;
			}
			if (playerY == 3545) {
				travelX = 0;
				travelY = 3;
				openFace = 3;
				closeFace = 0;
				doorUsed = true;
			}
			door = 13;
		}
		if (!doorUsed) {
			return;
		}
		if (travelX != -10 && travelY != -10) {
			Movement.travelTo(player, travelX, travelY);
		}
		player.forceNoClip = true;
		DoorEvent.canUseAutomaticDoor(player, door, true, player.getObjectId(), objectX, objectY, openFace, closeFace);
		player.resetPlayerTurn();

		if (lockPickDoor) {
			player.playerAssistant.sendMessage("You lockpick the door and successfully enter.");
		}
	}
}
