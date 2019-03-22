package game.object.areas;

import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.Web;
import game.object.ObjectEvent;
import game.object.custom.DoorEvent;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Handles objects of Wilderness
 *
 * @author MGT Madness 27-10-2013
 */
public class WildernessObjects {


	/**
	 * Perform actions of the objects in Wilderness.
	 */
	public static boolean doWildernessObject(final Player player, int objectType) {
		if (!Area.inDangerousPvpArea(player)) {
			return false;
		}

		int playerX = player.getX();
		int playerY = player.getY();

		switch (objectType) {

			// Crevice at Mage bank stairs dungeon where Hill giants, Fire giants and Chaos dwarfs are at
			case 19043:
				if (System.currentTimeMillis() - player.agility4 < 1700) {
					return true;
				}
				player.agility4 = System.currentTimeMillis();
				if (player.getObjectX() == 3046 && player.getObjectY() == 10327) {
					player.startAnimation(844);
					player.doingActionEvent(2);
					player.wildCrevice = true;
					player.timeUsedLadder = System.currentTimeMillis();
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							player.getPA().movePlayer(3048, 10336, 0);
							player.wildCrevice = false;
						}
					}, 2);
				} else if (player.getObjectX() == 3048 && player.getObjectY() == 10335) {
					player.timeUsedLadder = System.currentTimeMillis();
					player.startAnimation(844);
					player.doingActionEvent(2);
					player.wildCrevice = true;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							player.getPA().movePlayer(3046, 10326, 0);
							player.wildCrevice = false;
						}
					}, 2);
				}
				return true;

			// Crevice that leads to inside wilderness gwd dungeon, east of revenants and south of Hobgoblins at 30 wilderness.
			case 26766:
				if (System.currentTimeMillis() - player.agility4 < 1700) {
					return true;
				}
				player.agility4 = System.currentTimeMillis();
				if (player.getObjectX() == 3016 && player.getObjectY() == 3738) {
					player.startAnimation(844);
					player.doingActionEvent(2);
					player.wildCrevice = true;
					player.timeUsedLadder = System.currentTimeMillis();
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							player.getPA().movePlayer(3065, 10159, 3);
							player.wildCrevice = false;
						}
					}, 2);
				}
				return true;

			// Crevice that leads to 28 wilderness south of hobgoblins
			case 26767:
				if (System.currentTimeMillis() - player.agility4 < 1700) {
					return true;
				}
				player.agility4 = System.currentTimeMillis();
				if (player.getObjectX() == 3065 && player.getObjectY() == 10160) {
					player.startAnimation(844);
					player.doingActionEvent(2);
					player.wildCrevice = true;
					player.timeUsedLadder = System.currentTimeMillis();
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							player.getPA().movePlayer(3017, 3740, 0);
							player.wildCrevice = false;
						}
					}, 2);
				}
				return true;

			// Ship's ladder, first floor.
			case 245:
				if (player.getObjectX() == 3019 && player.getObjectY() == 3959) {
					return false;
				}
				if (System.currentTimeMillis() - player.agility4 < 1100) {
					return true;
				}
				player.agility4 = System.currentTimeMillis();
				player.timeUsedLadder = System.currentTimeMillis();
				player.getPA().movePlayer(playerX, playerY + 2, 2);
				return true;

			// Ship's ladder, second floor.
			case 246:
				if (System.currentTimeMillis() - player.agility4 < 1100) {
					return true;
				}
				player.agility4 = System.currentTimeMillis();
				player.timeUsedLadder = System.currentTimeMillis();
				player.getPA().movePlayer(playerX, playerY - 2, 1);
				return true;


			// Ladder.
			case 272:
				ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), 1);
				return true;

			// Ladder.
			case 273:
				ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), 0);
				return true;

			// Mage bank lever that leads to inside Mage bank
			case 5959:
				if (player.getX() != 3090) {
					return true;
				}
				player.turnPlayerTo(player.getObjectX() - 1, player.getObjectY());

				Teleport.startTeleport(player, 2539, 4712, 0, "LEVER");
				return true;

			// Lever at Mage arena that leads to inside Mage arena
			case 9706:
				if (player.getX() == 3105 && player.getY() == 3956) {
					Teleport.startTeleport(player, 3105, 3951, 0, "LEVER");
				}
				return true;

			// Lever at Mage arena that leads to outside Mage arena.
			case 9707:
				if (player.getX() == 3105 && player.getY() == 3951) {
					Teleport.startTeleport(player, 3105, 3956, 0, "LEVER");
				}
				return true;

			//Ladder at Lesser demon area outside Kbd
			case 18987:
				if (player.getX() == 3016 && player.getY() == 3849 || player.getX() == 3017 && player.getY() == 3848 || player.getX() == 3017 && player.getY() == 3850) {
					ObjectEvent.climbDownLadder(player, 3069, 10255, 0);
				}
				return true;

			//Ladder, leads to lesser demon area outside Kbd area.
			case 18988:
				ObjectEvent.climbUpLadder(player, 3016, 3849, 0);
				return true;

			// Lever located at 42 wilderness, beside the spiders, This is the lever to enter KBD area.
			case 1816:
				player.turnPlayerTo(3067, 10252);
				Teleport.startTeleport(player, 2271, 4680, 0, "LEVER");
				return true;

			// Lever located at 50 wild that leads to Ardougne
			case 1814:
				if (player.getX() != 3153 && player.getX() != 3923) {
					return true;
				}
				Teleport.startTeleport(player, 2561, 3311, 0, "LEVER");
				return true;

			// Staircase, west of outside of Magebank.
			case 16664:
				if (System.currentTimeMillis() - player.agility1 < 1100) {
					return true;
				}
				if (player.getX() != 3045 && player.getX() != 3044) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();
				if (player.getObjectX() == 3044 && player.getObjectY() == 3924) {
					player.timeUsedLadder = System.currentTimeMillis();
					player.getPA().movePlayer(3044, 10322, 0);
				}
				return true;

			// Staircase
			case 16665:
				if (System.currentTimeMillis() - player.agility1 < 1100) {
					return true;
				}
				if (player.getX() != 3045 && player.getX() != 3044) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();
				player.timeUsedLadder = System.currentTimeMillis();
				player.getPA().movePlayer(3044, 3927, 0);
				return true;

			// Web
			case 733:
				Web.slash(player, 946);
				return true;

			case 11834:
			case 11727:
			case 11726:
				DoorEvent.openAutomaticDoor(player);
				return true;

			case 411:
				PlayerMiscContent.prayAtAltar(player);
				return true;

		}
		return false;
	}

}
