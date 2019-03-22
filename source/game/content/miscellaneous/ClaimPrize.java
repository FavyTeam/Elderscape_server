package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.donator.DonatorMainTab;
import game.item.ItemAssistant;
import game.player.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import utility.FileUtility;

/**
 * Rewards for players who have won the Community event.
 *
 * @author MGT Madness, created on 14-02-2015.
 */
public class ClaimPrize {


	/**
	 * Add and delete event names to give rewards to.
	 */
	public static ArrayList<String> eventNames = new ArrayList<String>();

	/**
	 * Store playername-itemid-itemamount
	 */
	public static ArrayList<String> itemRewards = new ArrayList<String>();



	public static String eventFileLocation = "./backup/logs/event.txt";

	public static String itemRewardLocation = "./backup/logs/itemclaim.txt";

	public static void checkOnLogIn(Player player) {
		if (player.getHeight() == 20) {
			return;
		}
		for (int index = 0; index < ClaimPrize.eventNames.size(); index++) {
			String parse[] = ClaimPrize.eventNames.get(index).split("-");
			if (parse[0].toLowerCase().equals(player.getPlayerName().toLowerCase())) {
				int amount = Integer.parseInt(parse[1]);
				player.getPA().sendMessage("<col=005f00>You have x" + amount + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " reward ::claimevent");
				return;
			}
		}
		for (int index = 0; index < ClaimPrize.itemRewards.size(); index++) {
			String parse[] = ClaimPrize.itemRewards.get(index).split("-");
			if (parse[0].toLowerCase().equals(player.getPlayerName().toLowerCase())) {
				int itemId = Integer.parseInt(parse[1]);
				int amount = Integer.parseInt(parse[2]);
				double usdPayment = 0;
				if (parse.length == 4) {
					usdPayment = Double.parseDouble(parse[3]);
				}
				if (ItemAssistant.addItem(player, itemId, amount)) {
					if (usdPayment > 0) {
						DonatorMainTab.paymentClaimedInUsd(player, usdPayment);
					}
					player.getPA().sendMessage("<col=005f00>You have received x" + amount + " " + ItemAssistant.getItemName(itemId) + ".");
					ClaimPrize.itemRewards.remove(index);
					index--;
				} else {
					player.getPA().sendMessage(ServerConstants.RED_COL + "You have an item to be claimed, have a free inventory slot and log out and in.");
				}
			}
		}
	}

	public static void checkForReward(Player player, boolean login) {
		if (player.getHeight() == 20) {
			player.getPA().sendMessage("You cannot claim at the tournament area.");
			return;
		}
		for (int index = 0; index < ClaimPrize.eventNames.size(); index++) {
			String parse[] = ClaimPrize.eventNames.get(index).split("-");
			if (parse[0].toLowerCase().equals(player.getPlayerName().toLowerCase())) {
				int amount = Integer.parseInt(parse[1]);
				if (ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), amount)) {
					ClaimPrize.eventNames.remove(index);
					index--;
					player.getPA().sendMessage(
							ServerConstants.GREEN_COL + "Thank you for participating, your reward is x" + amount + " " + ServerConstants.getMainCurrencyName().toLowerCase() + "!");
				}
			}
		}
		if (!login) {
			player.getPA().sendMessage("No reward has been found for you.");
		}
	}

	public static void save() {
		FileUtility.deleteAllLines(eventFileLocation);
		FileUtility.saveArrayContents(eventFileLocation, ClaimPrize.eventNames);

		FileUtility.deleteAllLines(itemRewardLocation);
		FileUtility.saveArrayContents(itemRewardLocation, ClaimPrize.itemRewards);
	}

	public static void loadFile() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(eventFileLocation));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					ClaimPrize.eventNames.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BufferedReader file = new BufferedReader(new FileReader(itemRewardLocation));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					ClaimPrize.itemRewards.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
