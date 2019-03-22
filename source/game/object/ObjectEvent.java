package game.object;

import game.object.click.FirstClickObject;
import game.object.click.FourthClickObject;
import game.object.click.SecondClickObject;
import game.object.click.ThirdClickObject;
import game.object.clip.Region;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;

/**
 * Handle the object events.
 *
 * @author MGT Madness
 */

public class ObjectEvent {

	/**
	 * Climb down the ladder event.
	 *
	 * @param player The associated player.
	 * @param moveToX The arrival destination after climbing down ladder, x co-ordinate.
	 * @param moveToY The arrival destination after climbing down ladder, y co-ordinate.
	 */
	public static void climbDownLadder(final Player player, final int moveToX, final int moveToY, final int moveToHeight) {
		if (System.currentTimeMillis() - player.agility5 < 1700) {
			return;
		}
		player.agility5 = System.currentTimeMillis();
		player.startAnimation(827);

		if (player.ladderEvent) {
			return;
		}
		player.ladderEvent = true;
		player.timeUsedLadder = System.currentTimeMillis();
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.getPA().movePlayer(moveToX, moveToY, moveToHeight);
				player.ladderEvent = false;

			}
		}, 2);
	}

	/**
	 * Climb up the ladder event.
	 *
	 * @param player The associated player.
	 * @param moveToX The arrival destination after climbing up ladder, x co-ordinate.
	 * @param moveToY The arrival destination after climbing up ladder, y co-ordinate.
	 */
	public static void climbUpLadder(final Player player, final int moveToX, final int moveToY, final int moveToHeight) {
		if (System.currentTimeMillis() - player.agility5 < 1700) {
			return;
		}
		player.agility5 = System.currentTimeMillis();
		player.startAnimation(828);

		if (player.ladderEvent) {
			return;
		}
		player.ladderEvent = true;
		player.timeUsedLadder = System.currentTimeMillis();
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.getPA().movePlayer(moveToX, moveToY, moveToHeight);
				player.ladderEvent = false;

			}
		}, 2);
	}

	/**
	 * Start an event to find the first click object action.
	 */
	public static void clickObjectType1Event(final Player player) {

		if (player.doingClickObjectType1Event) {
			return;
		}
		player.doingClickObjectType1Event = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (player.clickObjectType != 1) {
					container.stop();
				}

			}

			@Override
			public void stop() {
				player.doingClickObjectType1Event = false;
			}
		}, 1);

	}

	/**
	 * Start an event to find the second click object action.
	 */
	public static void clickObjectType2Event(final Player player) {

		if (player.doingClickObjectType2Event) {
			return;
		}
		player.doingClickObjectType2Event = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				/*
					if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving)
					{
							SecondClickObject.secondClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
					}
					else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 2 && !player.tempMoving)
					{
							SecondClickObject.secondClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
					}
					*/

				if (player.clickObjectType != 2) {
					container.stop();
				}

			}

			@Override
			public void stop() {
				player.doingClickObjectType2Event = false;
			}
		}, 1);

	}

	/**
	 * Start an event to find the third click object action.
	 */
	public static void clickObjectType3Event(final Player player) {

		if (player.doingClickObjectType3Event) {
			return;
		}
		player.doingClickObjectType3Event = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				/*
					if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 3 && !player.tempMoving)
					{
							ThirdClickObject.thirdClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
					}
					*/

				if (player.clickObjectType != 3) {
					container.stop();
				}

			}

			@Override
			public void stop() {
				player.doingClickObjectType3Event = false;
			}
		}, 1);

	}

	/**
	 * Start an event to find the fourth click object action.
	 */
	public static void clickObjectType4Event(final Player player) {

		if (player.doingClickObjectType4Event) {
			return;
		}
		player.doingClickObjectType4Event = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				/*
					if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 4 && !player.tempMoving)
					{
							FourthClickObject.fourthClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
					}
					*/

				if (player.clickObjectType != 4) {
					container.stop();
				}

			}

			@Override
			public void stop() {
				player.doingClickObjectType4Event = false;
			}
		}, 1);

	}

	public static void stoppedMovingObjectAction(Player player) {
//		if (player.getInteractingObjectDefinition() != null
//				&& !Region.inDistance(player.getInteractingObjectDefinition(), 0, player.getObjectX(), player.getObjectY(), player.getX(), player.getY())) {
//			player.setWalkingPacketQueue(player.getObjectX(), player.getObjectY(), ObjectEvent::stoppedMovingObjectAction);
//			return;
//		}
		player.tempMoving = false;
		if (player.doingClickObjectType1Event) {
			if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving) {
				FirstClickObject.firstClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
			} else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 1 && !player.tempMoving) {
				FirstClickObject.firstClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
			}
		} else if (player.doingClickObjectType2Event) {
			if (ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.getX() == player.specialObjectActionPoint[3] && player.getY() == player.specialObjectActionPoint[4] && !player.tempMoving) {
				SecondClickObject.secondClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
			} else if (!ObjectRePathing.collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY()) && player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 2 && !player.tempMoving) {
				SecondClickObject.secondClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
			}
		} else if (player.doingClickObjectType3Event) {
			if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 3 && !player.tempMoving) {
				ThirdClickObject.thirdClickObjectOsrs(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
			}
		} else if (player.doingClickObjectType4Event) {
			if (player.playerAssistant.withInDistance(player.getObjectX() + player.objectXOffset, player.getObjectY() + player.objectYOffset, player.getX(), player.getY(), player.objectDistance) && player.clickObjectType == 4 && !player.tempMoving) {
				FourthClickObject.fourthClickObject(player, player.getObjectId(), player.getObjectX(), player.getObjectY());
			}
		}
	}

}
