package game.object.areas;

import core.ServerConstants;
import game.content.skilling.agility.AgilityAssistant;
import game.object.custom.DoorEvent;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;

/**
 * Handles the objects of Taverly dungeon
 *
 * @author MGT Madness 29-10-2013
 */
public class TaverlyDungeon {

	/**
	 * Perform actions of the objects in Taverly dungeon.
	 */
	public static boolean isTaverlyDungeonObject(final Player player, int objectType) {

		int playerX = player.getX();
		int playerY = player.getY();

		switch (objectType) {

			case 2143:
			case 2144:
				if (player.getObjectX() == 2889) {

					DoorEvent.canUseAutomaticDoor(player, 1, false, 2143, 2889, 9831, 1, 0);
					DoorEvent.canUseAutomaticDoor(player, 1, false, 2144, 2889, 9830, 3, 0);
					player.forceNoClip = true;
					Movement.travelTo(player, player.getX() == 2888 ? 1 : -1, 0);
					player.resetPlayerTurn();
					return true;
				}
				break;

			// Strange floor
			case 16510:
				if (player.baseSkillLevel[ServerConstants.AGILITY] < 80) {
					player.playerAssistant.sendMessage("You need 80 agility to use this shortcut.");
					return true;
				}
				if (playerX == 2880 && playerY == 9814) {
					player.getPA().movePlayer(2878, 9813, 0);
					return true;
				}
				if (playerX == 2878 && playerY == 9812) {
					player.getPA().movePlayer(2880, 9813, 0);
					return true;
				}
				if (playerX < player.getObjectX()) {
					player.getPA().movePlayer(player.getObjectX() + 1, player.getY(), 0);
				} else if (playerX > player.getObjectX()) {
					player.getPA().movePlayer(player.getObjectX() - 1, player.getY(), 0);
				}
				return true;

			// Obstacle pipe
			case 16509:
				if (playerX == 2886 && playerY == 9799) {

					if (player.baseSkillLevel[ServerConstants.AGILITY] < 70) {
						player.playerAssistant.sendMessage("You need 70 agility to use this shortcut.");
						return true;
					}

					if (System.currentTimeMillis() - player.agility7 < 3000) {
						return true;
					}
					player.agility7 = System.currentTimeMillis();
					AgilityAssistant.agilityAnimation(player, 844, false, 6, 0);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getX() == 2892 && player.getY() == 9799) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);

				} else if (playerX == 2892 && playerY == 9799) {

					if (player.baseSkillLevel[ServerConstants.AGILITY] < 70) {
						player.playerAssistant.sendMessage("You need 70 agility to use this shortcut.");
						return true;
					}

					if (System.currentTimeMillis() - player.agility7 < 3000) {
						return true;
					}
					player.agility7 = System.currentTimeMillis();
					AgilityAssistant.agilityAnimation(player, 844, false, -6, 0);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getX() == 2886 && player.getY() == 9799) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);

				}
				return true;

		}
		return false;
	}


}
