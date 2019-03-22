package game.object.click;

import core.GameType;
import core.Plugin;
import game.content.bank.Bank;
import game.content.miscellaneous.CabbagePicking;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.FlaxPicking;
import game.content.miscellaneous.PotatoPicking;
import game.content.miscellaneous.SpellBook;
import game.content.packet.preeoc.ClickObjectPreEoc;
import game.content.skilling.ResourceWilderness;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.smithing.Smithing;
import game.content.skilling.thieving.Stalls;
import game.object.ObjectEvent;
import game.object.custom.DoorEvent;
import game.player.Area;
import game.player.Player;

public class SecondClickObject {

	public static void secondClickObjectOsrs(final Player player, int objectId, final int objectX,
			final int objectY) {
		if (ClickObjectPreEoc.secondClickObject(player, objectId, objectX, objectY)) {
			return;
		}
		if (!GameType.isOsrs()) {
			return;
		}
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.clickObjectType = 0;

		if (Stalls.isStallObject(player, objectId)) {
			return;
		}

		if (Smithing.isFurnace(player, objectId)) {
			player.setActionIdUsed(5);
			Smithing.sendSmelting(player);
		}

		switch (objectId) {
			case 9380:
				player.getPA().sendMessage("This trap is set up, and ready to catch chinchompas.");
				break;
			case 9345 :
				player.getPA().sendMessage("This trap is set up, and ready to catch birds.");
				break;
			//obelisks
			case 14829:
			case 14830:
			case 14827:
			case 14828:
			case 14826:
			case 14831:
				if (player.getWildernessKills(true) < 500) {
					player.getPA().sendMessage("You need 500 Wilderness kills to unlock this feature.");
					break;
				}
				player.getPA().displayInterface(24242);
				break;

			// Deposit vault
			case 29111:
				DiceSystem.showDepositInterface(player);
				break;
			case 11726:
				if (Area.inDangerousPvpArea(player)) {
					DoorEvent.openAutomaticDoor(player);
				}
				break;

			// Resource wilderness gate to enter/exit. Using the peek option.
			case 26760:
				ResourceWilderness.peakIntoResourceWildArea(player);
				break;

			// Door/gate at Pirate hut in wild and Magic axe hut in Wild.
			case 11834:
			case 11727:
				DoorEvent.openAutomaticDoor(player);
				break;
			case 2152:
				player.playerAssistant.sendMessage("Use a powered orb or battlestaff on the obelisk to note it.");
				break;

			// Stair case at Warrior's guild.
			case 16672:
				if (player.getPA().objectIsAt(2839, 3537, 1)) {
					player.getPA().movePlayer(2840, 3539, 2);
				}
				if (player.getX() == 3205 && player.getY() == 3209) {
					player.getPA().movePlayer(3205, 3209, 2);
				}
				if (player.getX() == 3205 && player.getY() == 3228) {
					player.getPA().movePlayer(3205, 3228, 2);
				}
				break;

			// Staircase climb up.
			case 11792:
				// Church in East varrock.
				if (player.getPA().objectIsAt(3258, 3487)) {
					if (player.getHeight() == 1) {
						player.getPA().movePlayer(3258, 3486, 2);
					} else if (player.getHeight() == 2) {
						player.getPA().movePlayer(3258, 3486, 3);
					}
				}
				break;

			// Ladder climb up.
			case 16684:
			case 12965:
			case 14747:
				ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), player.getHeight() + 1);
				break;
			// Altar of the occult.
			case 29150:
				SpellBook.switchToModern(player);
				break;
			// Zombies minigame chest.
			case 76:
				JewelryCrafting.jewelryInterface(player);
				break;

			// Lumbridge staircase.
			case 1739:
				player.getPA().movePlayer(player.getX(), player.getY(), 2);
				break;
			// Flax.
			case 14896:
				FlaxPicking.flaxExists(player);
				break;

			// Potato
			case 312:
				PotatoPicking.potatoExists(player);
				break;

			// Cabbage.
			case 1161:
				CabbagePicking.cabbageExists(player);
				break;

			// Spinning wheel.
			case 2644:
			case 25824:
			case 14889:
			case 21304:
				player.getPA().displayInterface(26110);
				break;
			// Bank
			case 6943:
			case 7409:
			case 24101:
			case 28861:
			case 10517:
			case 25808:
			case 18491:
			case 14367:
			case 16642:
			case 27718:
			case 27719:
			case 27720:
			case 27721:
			case 28430:
			case 28431:
			case 28432:
			case 28433:
			case 28546:
			case 28547:
			case 28548:
			case 28549:
			case 24347:
				player.setUsingBankSearch(false);
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
				break;

			// Ladder at Gnome tree.
			case 2884:
			case 1748:
				ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), player.getHeight() + 1);
				break;
			default:
				if (Plugin.execute("second_click_object_" + objectId, player)) {
					//player.getPA().sendMessage("working");
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;

		}
	}

}
