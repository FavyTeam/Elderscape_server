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
 * Handles the objects of Brimhaven dungeon
 *
 * @author MGT Madness 28-10-2013
 */
public class Karamja {

	/**
	 * @return true, if object is in Brimhaven dungeon.
	 */
	public static boolean isKaramjaObject(final Player player, final int objectType) {

		for (int i = 0; i < KaramjaObject.length; i++) {
			if (objectType == KaramjaObject[i]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Brimhaven dungeon object Identities.
	 */
	private static int[] KaramjaObject =
			{2333, 2335, 2332, 2259, 2260, 2262, 2261, 2216, 492, 9358,};

	public static void karamjaObjectAction(final Player player, int objectType) {
		switch (objectType) {
			case 492:
				player.getPA().movePlayer(2862, 9572, 0);
				player.playerAssistant.sendMessage("You climb down the rocks.");
				return;

			case 9358:
				player.getPA().movePlayer(2857, 3170, 0);
				player.playerAssistant.sendMessage("You exit the cave.");
				return;
			// Stepping stone at Karamja.
			case 2333:
				if (player.baseSkillLevel[ServerConstants.AGILITY] < 60) {
					player.playerAssistant.sendMessage("You need 60 agility to use this shortcut.");
					return;
				}
				player.playerAssistant.sendMessage("You carefully start crossing the stepping stones...");
				player.setDoingAgility(true);
				player.agilityEndX = 2925;
				player.agilityEndY = 2947;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						player.stoneTimer++;
						if (player.stoneTimer == 1) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 3) {
							player.getPA().movePlayer(2925, 2950, 0);

						} else if (player.stoneTimer == 4) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 6) {
							player.getPA().movePlayer(2925, 2949, 0);

						} else if (player.stoneTimer == 7) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 9) {
							player.getPA().movePlayer(2925, 2948, 0);

						} else if (player.stoneTimer == 10) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 12) {
							player.getPA().movePlayer(2925, 2947, 0);
							container.stop();
						}
					}

					@Override
					public void stop() {
						player.stoneTimer = 0;
						player.playerAssistant.sendMessage("...You safely cross to the other side.");
						AgilityAssistant.resetAgilityWalk(player);
					}
				}, 1);
				break;

			// Stepping stone at Karamja.
			case 2335:
				if (player.baseSkillLevel[ServerConstants.AGILITY] < 60) {
					player.playerAssistant.sendMessage("You need 60 agility to use this shortcut.");
					return;
				}
				player.playerAssistant.sendMessage("You carefully start crossing the stepping stones...");
				player.setDoingAgility(true);
				player.agilityEndX = 2925;
				player.agilityEndY = 2951;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						player.stoneTimer++;
						if (player.stoneTimer == 1) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 3) {
							player.getPA().movePlayer(2925, 2948, 0);

						} else if (player.stoneTimer == 4) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 6) {
							player.getPA().movePlayer(2925, 2949, 0);

						} else if (player.stoneTimer == 7) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 9) {
							player.getPA().movePlayer(2925, 2950, 0);

						} else if (player.stoneTimer == 10) {
							player.startAnimation(769);

						} else if (player.stoneTimer == 12) {
							player.getPA().movePlayer(2925, 2951, 0);
							container.stop();
						}
					}

					@Override
					public void stop() {
						player.stoneTimer = 0;
						player.playerAssistant.sendMessage("...You safely cross to the other side.");
						AgilityAssistant.resetAgilityWalk(player);
					}
				}, 1);
				break;

			//A wooden log, Karamja.
			case 2332:
				player.resetPlayerTurn();
				AgilityAssistant.agilityAnimation(player, 762, false, player.getX() >= 2910 ? -4 : 4, 0);
				break;

			// Metal Gate, Shilo village.
			case 2259:
			case 2260:
				DoorEvent.canUseAutomaticDoor(player, 1, false, 2260, 2875, 2953, 1, 0);
				DoorEvent.canUseAutomaticDoor(player, 1, false, 2259, 2875, 2952, 3, 0);
				player.forceNoClip = true;
				Movement.travelTo(player, player.getX() >= 2875 ? -1 : 1, 0);
				player.resetPlayerTurn();
				break;

			// Wooden gate, Shilo village.
			case 2262:
			case 2261:
				DoorEvent.canUseAutomaticDoor(player, 1, false, 2262, 2867, 2952, 3, 2);
				DoorEvent.canUseAutomaticDoor(player, 1, false, 2261, 2867, 2953, 1, 2);
				player.forceNoClip = true;
				Movement.travelTo(player, player.getX() >= 2868 ? -1 : 1, 0);
				player.resetPlayerTurn();
				break;

			// Broken cart, Shilo village.
			case 2216:
				player.getDH().sendDialogues(238);
				break;
		}

	}

}
