package game.content.miscellaneous;

import java.util.ArrayList;

import core.Server;
import game.content.clanchat.ClanChatHandler;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Player;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Mithril seeds.
 *
 * @author MGT Madness, created on 13-04-2017
 */
public class MithrilSeeds {
	/**
	 * Flower object id, item id, chance
	 */
	public static int flowerData[][] =
			{
					//@formatter:off
					{2980, 2460, 108},
					{2981, 2462, 249},
					{2982, 2464, 402},
					{2983, 2466, 549},
					{2984, 2468, 698},
					{2985, 2470, 852},
					{2986, 2472, 998},
					{2988, 2476, 1000},
					{2987, 2474, 1001},
					//@formatter:on
			};

	public final static String[] flowerNames =
			{
					//@formatter:off
					"Assorted flowers",
					"Red flowers",
					"Blue flowers",
					"Yellow flowers",
					"Purple flowers",
					"Orange flowers",
					"Mixed flowers",
					"Black flowers",
					"White flowers",
					//@formatter:on
			};

	public static ArrayList<String> flowerSpots = new ArrayList<String>();

	// Cannot flower on flower or fire on flower or flower on fire
	// Logout after planting flower
	public static void plantSeed(Player player) {
		if (!ClanChatHandler.inDiceCc(player, true, false)) {
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 16004)) {
			player.getPA().sendMessage("You need a dice bag in your inventory.");
			return;
		}
		if (!canPlantFlower(player)) {
			player.getPA().sendMessage("You cannot plant a flower here.");
			return;
		}
		player.getPA().closeInterfaces(true);
		ItemAssistant.deleteItemFromInventory(player, 299, 1);

		int random = Misc.random(1, 1001);
		for (

				int index = 0; index < flowerData.length; index++) {
			if (random <= flowerData[index][2]) {
				player.flower = flowerData[index][1];
				player.flowerX = player.getX();
				player.flowerY = player.getY();
				player.flowerHeight = player.getHeight();
				player.forceNoClip = true;
				new Object(player, flowerData[index][0], player.getX(), player.getY(), player.getHeight(), 2, 10, -1, 100);
				flowerSpots.add(player.getX() + " " + player.getY() + " " + player.getHeight());
				int faceX = 0;
				int faceY = 0;
				if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST")) {
					Movement.travelTo(player, -1, 0);
					faceX = 1;
				} else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST")) {
					Movement.travelTo(player, 1, 0);
					faceX = -1;
				} else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH")) {
					Movement.travelTo(player, 0, -1);
					faceY = 1;
				} else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH")) {
					Movement.travelTo(player, 0, 1);
					faceY = -1;
				}
				player.turnPlayerTo(player.getX() + faceX, player.getY() + faceY);
				player.getDH().sendDialogues(285);
				if (ClanChatHandler.inDiceCc(player, true, false)) {
					ClanChatHandler.sendDiceClanMessage(player.getPlayerName(), player.getClanId(), "I have planted " + flowerNames[index] + ".");
				}
				break;
			}
		}
	}

	private static boolean canPlantFlower(Player player) {
		for (int i = 0; i < flowerSpots.size(); i++) {
			if (flowerSpots.get(i).equals(player.getX() + " " + player.getY() + " " + player.getHeight())) {
				return false;
			}
		}
		if (Region.objectExistsHere(player)) {
			return false;
		}
		return true;
	}

	public static void pickUpPlant(Player player) {
		for (int i = 0; i < flowerSpots.size(); i++) {
			if (flowerSpots.get(i).equals(player.flowerX + " " + player.flowerY + " " + player.flowerHeight)) {
				for (int index = 0; index < player.toRemove.size(); index++) {
					if (player.toRemove.get(index).objectX == player.flowerX && player.toRemove.get(index).objectY == player.flowerY) {
						ItemAssistant.addItemToInventoryOrDrop(player, player.flower, 1);
						player.startAnimation(827);
						resetPlayerPlantData(player);
						Server.objectManager.toRemove.add(player.toRemove.get(index));
						break;
					}
				}
				break;
			}
		}
		player.getPA().closeInterfaces(true);
	}

	public static void resetPlayerPlantData(Player player) {

		player.flower = 0;
		player.flowerX = 0;
		player.flowerY = 0;
		player.flowerHeight = 0;

	}

	public static void deletePlant(Object object) {
		boolean foundMatch = false;
		for (int index = 0; index < flowerData.length; index++) {
			if (flowerData[index][0] == object.objectId) {
				foundMatch = true;
			}
		}
		if (!foundMatch) {
			return;
		}
		for (int i = 0; i < flowerSpots.size(); i++) {
			if (flowerSpots.get(i).equals(object.objectX + " " + object.objectY + " " + object.height)) {
				flowerSpots.remove(i);
				break;
			}
		}

	}
}
