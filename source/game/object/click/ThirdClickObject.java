package game.object.click;

import core.GameType;
import core.Plugin;
import game.content.bank.Bank;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.SpellBook;
import game.content.packet.preeoc.ClickObjectPreEoc;
import game.object.ObjectEvent;
import game.player.Player;

public class ThirdClickObject {

	public static void thirdClickObjectOsrs(Player player, int objectType, int objectX, int objectY) {
		if (ClickObjectPreEoc.thirdClickObject(player, objectType, objectX, objectY)) {
			return;
		}
		if (!GameType.isOsrs()) {
			return;
		}
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.clickObjectType = 0;
		switch (objectType) {
			// Bank.
			case 6943:
				player.setUsingBankSearch(false);
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
				break;

			// Deposit vault
			case 29111:
				DiceSystem.showWithdrawInterface(player);
				break;
			// Stair case at Warrior's guild. also lumbridge castle stairs
			case 16672:
				if (player.getPA().objectIsAt(2839, 3537, 1)) {
					player.getPA().movePlayer(2840, 3539, 0);
				}
				if (player.getX() == 3205 && player.getY() == 3209) {
					player.getPA().movePlayer(3205, 3209, 0);
				}
				if (player.getX() == 3205 && player.getY() == 3228) {
					player.getPA().movePlayer(3205, 3228, 0);
				}
				break;
			// Staircase climb down.
			case 11792:
				// Church in East varrock.
				if (player.getPA().objectIsAt(3258, 3487) && player.getHeight() == 1) {
					player.getPA().movePlayer(3257, 3487, 0);
				} else if (player.getPA().objectIsAt(3258, 3487) && player.getHeight() == 2) {
					player.getPA().movePlayer(3258, 3486, 1);
				}
				break;
			// Ladder climb down.
			case 16684:
			case 12965:
				ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
				break;

			// Altar of the occult.
			case 29150:
				SpellBook.switchToAncients(player);
				break;
			// Lumbridge staircase.
			case 1739:
				player.getPA().movePlayer(player.getX(), player.getY(), 0);
				break;

			case 14747:
				ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), 0);
				break;
			case 10177:
				// Dagganoth ladder 1st level
				player.getPA().movePlayer(1798, 4407, 3);
				break;

			// Ladder at Gnome tree.
			case 2884:
			case 1748:
				ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
				break;
			default:
				if (Plugin.execute("third_click_object_" + objectType, player)) {
					//player.getPA().sendMessage("working");
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}
	}

}
