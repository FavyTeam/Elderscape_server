package game.object.areas;

import core.ServerConstants;
import game.player.Player;

/**
 * Handles the objects of Slayer tower
 *
 * @author MGT Madness 27-10-2013
 */
public class SlayerTower {

	/**
	 * Perform actions of the objects in Slayer tower.
	 */
	public static boolean isSlayerTowerObject(final Player player, int objectType) {

		int playerX = player.getX();
		int playerY = player.getY();
		switch (objectType) {

			// Staircase
			case 2114:
				if (playerY >= 3538) {
					player.getPA().movePlayer(3433, 3538, 1);
				} else if (playerY <= 3537) {
					player.getPA().movePlayer(3433, 3537, 1);
				}
				return true;

			// Staircase
			case 2118:
				if (playerX == 3433 && playerY == 3538) {
					player.getPA().movePlayer(3438, 3538, 0);
				} else if (playerX == 3433 && playerY == 3537) {
					player.getPA().movePlayer(3438, 3537, 0);
				}
				return true;

			// Spikey chain
			case 16537:
				if (player.getHeight() == 0) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 61) {
						player.playerAssistant.sendMessage("You need 61 agility to use this shortcut.");
						return true;
					}
					player.getPA().movePlayer(player.getX(), player.getY(), 1);
				} else if (player.getHeight() == 1) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 81) {
						player.playerAssistant.sendMessage("You need 81 agility to use this shortcut.");
						return true;
					}
					player.getPA().movePlayer(player.getX(), player.getY(), 2);
				}
				return true;

			// Spikey chain
			case 16538:

				if (player.getHeight() == 1) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 61) {
						player.playerAssistant.sendMessage("You need 61 agility to use this shortcut.");
						return true;
					}
					player.getPA().movePlayer(player.getX(), player.getY(), 0);
				} else if (player.getHeight() == 2) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 81) {
						player.playerAssistant.sendMessage("You need 81 agility to use this shortcut.");
						return true;
					}

					player.getPA().movePlayer(player.getX(), player.getY(), 1);
				}
				return true;

			// Staircase
			case 2119:
				if (playerX == 3412 && playerY == 3541) {
					player.getPA().movePlayer(3417, 3541, 2);
				} else if (playerX == 3412 && playerY == 3540) {
					player.getPA().movePlayer(3417, 3540, 2);
				}
				return true;

			// Staircase
			case 2120:
				if (playerX == 3417 && playerY == 3541) {
					player.getPA().movePlayer(3412, 3541, 1);
				} else if (playerX == 3417 && playerY == 3540) {
					player.getPA().movePlayer(3412, 3540, 1);
				}
				return true;

		}

		return false;
	}

}
