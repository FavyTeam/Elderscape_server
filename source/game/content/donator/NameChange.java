package game.content.donator;

import java.io.File;
import java.util.ArrayList;

import core.Server;
import core.ServerConstants;
import game.content.clanchat.Clan;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.highscores.Highscores;
import game.content.interfaces.donator.DonatorMainTab.AccountOffersGiven;
import game.content.minigame.lottery.LotteryDatabase;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.HackLog;
import game.content.miscellaneous.ServerShutDownUpdate;
import game.content.miscellaneous.YoutubeRank;
import game.content.staff.StaffActivity;
import game.content.staff.StaffManagement;
import game.content.tradingpost.TradingPost;
import game.content.tradingpost.TradingPostItems;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.log.NewPlayerIpTracker;
import game.player.Player;
import game.player.PlayerSave;
import utility.FileUtility;
import utility.HackLogHistory;
import utility.Misc;

public class NameChange {

	public static int stage = 0;

	public static String name = "";

	public static void checkName(Player player, String newName) {
		player.getPA().closeInterfaces(true);
		if (!ItemAssistant.hasItemInInventory(player, 1505)) {
			return;
		}
		if (Combat.inCombatAlert(player)) {
			return;
		}
		if (newName.isEmpty()) {
			player.getPA().sendMessage("Cancelled.");
			return;
		}
		newName = newName.trim();
		if (!newName.matches("[A-Za-z0-9 ]+")) {
			player.getDH().sendStatement("Invalid characters.");
			return;
		}
		if (newName.length() > 12) {
			player.getDH().sendStatement("Your new name can't be longer than 12 characters.");
			return;
		}
	
		if (Misc.isFlaggedOffensiveName(newName)) {
			player.getDH().sendStatement("You can't use this toxic name.");
			return;
		}
	
		if (FileUtility.fileExists("backup/characters/bots/" + newName + ".txt")) {
			player.getDH().sendStatement("This name belongs to a bot!");
			return;
		}
		if (ServerShutDownUpdate.serverSaveActive) {
			player.getDH().sendStatement("Try again in 20 seconds.");
			return;
		}
		if (!DiceSystem.canWithdraw(player)) {
			player.getDH().sendStatement("You have recently gambled, please wait a few minutes");
			return;
		}
		if (FileUtility.fileExists(ServerConstants.getCharacterLocation() + newName + ".txt")) {
			player.getPA().sendFrame126("@red@Taken", 42085);
			player.getPA().sendFrame126("@whi@---", 42086);
			player.getPA().sendFrame126("Look up name", 42081);
			player.getDH().sendStatement("The account name " + newName + " already exists.");
			stage = 0;

		}
		else {
			player.getPA().sendFrame126("@gre@Available", 42085);
			player.getPA().sendFrame126("@gre@" + newName, 42086);
			player.getPA().sendFrame126("Another name", 42081);
			name = "" + newName;
			stage = 1;
		}
	}

	public static void nameChange(Player player, String newName) {
		String oldName = player.getPlayerName();
			File file = new File(ServerConstants.getCharacterLocation() + oldName + ".txt");
	
			if (!file.delete()) {
				player.getPA().sendMessage("Name change failed, try again in a few minutes or after relogging.");
				return;
			}
			String rareDropLocation = "backup/logs/rare drop log/" + oldName + ".txt";
			if (FileUtility.fileExists(rareDropLocation)) {
				try {
					new File(rareDropLocation).renameTo(new File("backup/logs/rare drop log/" + Misc.capitalize(newName) + ".txt"));
				} catch (Exception e) {
					player.getPA().sendMessage("Name change failed, try again in a few minutes or after relogging.");
					return;
				}
			}
			FileUtility.addLineOnTxt("backup/logs/namechange.txt", Misc.getDateAndTime() + ": [" + oldName + "] to [" + Misc.capitalize(newName) + "]");
			ItemAssistant.deleteItemFromInventory(player, 1505, 1);
			player.getPA().sendMessage("Your name has been changed successfully!");
			player.getDH().sendStatement("Congratulations, your new name is " + newName + ".");
		name = "";
			player.setPlayerName(Misc.capitalize(newName));
			PlayerSave.saveGame(player);
		player.getPA().setSidebarInterface(9, 42006);
			player.getPA().sendScreenshot("Name change: " + player.getPlayerName(), 3);
			player.getPA().requestUpdates();
	
			ServerShutDownUpdate.serverSaveActive = true;
			for (int index = 0; index < YoutubeRank.youtubeRankData.size(); index++) {
				String parse[] = YoutubeRank.youtubeRankData.get(index).split("#");
				String userName = parse[0];
				if (oldName.equals(userName)) {
					String old = YoutubeRank.youtubeRankData.get(index);
					YoutubeRank.youtubeRankData.remove(index);
					old = old.replaceAll(userName, Misc.capitalize(newName));
					YoutubeRank.youtubeRankData.add(old);
					break;
				}
			}
			for (int index = 0; index < DiceSystem.depositVault.size(); index++) {
				String parse[] = DiceSystem.depositVault.get(index).split("-");
				if (parse[0].equalsIgnoreCase(oldName)) {
					String old = DiceSystem.depositVault.get(index);
					DiceSystem.depositVault.remove(index);
					DiceSystem.depositVault.add(old.replace(oldName, Misc.capitalize(newName)));
					break;
				}
			}
			for (int index = 0; index < TradingPost.tradingPostData.size(); index++) {
				if (index > TradingPost.tradingPostData.size() - 1) {
					break;
				}
				if (TradingPost.tradingPostData.get(index).getName().equals(oldName.toLowerCase())) {
					TradingPost data = TradingPost.tradingPostData.get(index);
					TradingPost.tradingPostData.remove(index);
					index--;
					TradingPost.tradingPostData.add(new TradingPost(newName.toLowerCase(), data.getAction(), data.getItemAmountLeft(), data.getPrice(), data.getTime(), data.getAmountTraded(), data.getInitialAmount()));
				}
			}
	
			for (int index = 0; index < TradingPostItems.tradingPostItemsData.size(); index++) {
				if (index > TradingPostItems.tradingPostItemsData.size() - 1) {
					break;
				}
				if (TradingPostItems.tradingPostItemsData.get(index).getName().equals(oldName.toLowerCase())) {
					TradingPostItems data = TradingPostItems.tradingPostItemsData.get(index);
					TradingPostItems.tradingPostItemsData.remove(index);
					index--;
					TradingPostItems.tradingPostItemsData.add(new TradingPostItems(data.getAction(), newName.toLowerCase(), data.getPartnerName(), data.getItemId(), data.getItemAmount(), data.getItemPrice(), data.getItemSold(), data.getItemSoldAmount()));
				}
			}
	
			for (int index = 0; index < AccountOffersGiven.accountOffersGiven.size(); index++) {
				AccountOffersGiven instance = AccountOffersGiven.accountOffersGiven.get(index);
				if (instance.name.equals(oldName)) {
					instance.name = Misc.capitalize(newName);
					break;
				}
			}
			LotteryDatabase data = LotteryDatabase.getPlayerLotteryInstance(oldName);
			if (data != null) {
				data.setPlayerName(Misc.capitalize(newName));
			}
	
			for (int index = 0; index < Tournament.tournamentTitleWinners.size(); index++) {
				String parse[] = Tournament.tournamentTitleWinners.get(index).split("-");
				String name = parse[0];
				String title = parse[1];
				if (name.equalsIgnoreCase(oldName)) {
					Tournament.tournamentTitleWinners.remove(index);
					Tournament.tournamentTitleWinners.add(newName.toLowerCase() + "-" + title);
					break;
				}
			}
	
			String newName1 = newName;
			for (int i = 0; i < ClanChatHandler.clans.length; i++) {
				Clan instance = ClanChatHandler.clans[i];
				if (ClanChatHandler.clans[i] == null) {
					continue;
				}
				boolean stop = false;
				if (instance.ownerName.equalsIgnoreCase("dice")) {
					for (int a = 0; a < instance.banned.size(); a++) {
						if (instance.banned.get(a).equalsIgnoreCase(oldName)) {
							stop = true;
							instance.banned.remove(a);
							instance.banned.add(Misc.capitalize(newName));
							break;
						}
					}
				}
				if (stop) {
					break;
				}
			}
			Highscores.nameChangeUpdateHighscores(oldName, newName1);
			for (int index = 0; index < StaffManagement.staffRanks.size(); index++) {
				String[] split = StaffManagement.staffRanks.get(index).split("-");
				String name = split[0].toLowerCase();
				String rank = split[1].toLowerCase();
				if (name.equalsIgnoreCase(oldName)) {
					StaffManagement.staffRanks.remove(index);
					StaffManagement.staffRanks.add(newName1 + "-" + rank);
					break;
				}
			}
			for (int index = 0; index < StaffActivity.staffActivityList.size(); index++) {
				StaffActivity instance = StaffActivity.staffActivityList.get(index);
				if (instance.getName().equalsIgnoreCase(oldName)) {
					instance.setName(Misc.capitalize(newName1));
					break;
				}
			}
	
			new Thread(new Runnable() {
				public void run() {
					for (int index = 0; index < HackLogHistory.hackLogList.size(); index++) {
						HackLogHistory instance = HackLogHistory.hackLogList.get(index);
						if (instance.account.equals(oldName)) {
							instance.setAccount(Misc.capitalize(newName1));
						}
					}
	
					ArrayList<String> arraylist = FileUtility.readFile("backup/logs/bruteforce/hacklog.txt");
					for (int index = 0; index < arraylist.size(); index++) {
						if (arraylist.get(index).startsWith("[" + oldName + "]")) {
							String string = arraylist.get(index).replace("[" + oldName + "]", "[" + Misc.capitalize(newName1) + "]");
							arraylist.add(index, string);
							arraylist.remove(index + 1);
						}
					}
					FileUtility.deleteAllLines("backup/logs/bruteforce/hacklog.txt");
					FileUtility.saveArrayContentsSilent("backup/logs/bruteforce/hacklog.txt", arraylist);
	
				}
			}).start();
	
			for (int index = 0; index < HackLog.hackLog.size(); index++) {
				if (HackLog.hackLog.get(index).startsWith("[" + oldName + "]")) {
					String string = HackLog.hackLog.get(index).replace("[" + oldName + "]", "[" + Misc.capitalize(newName) + "]");
					HackLog.hackLog.add(index, string);
					HackLog.hackLog.remove(index + 1);
				}
			}
	
			for (int index = 0; index < NewPlayerIpTracker.ipCollectionListIpName.size(); index++) {
				String[] parse = NewPlayerIpTracker.ipCollectionListIpName.get(index).split("-");
				if (parse[1].equalsIgnoreCase(oldName)) {
					NewPlayerIpTracker.ipCollectionListIpName.remove(index);
					NewPlayerIpTracker.ipCollectionListIpName.add(parse[0] + "-" + Misc.capitalize(newName));
					break;
				}
			}
			new Thread(new Runnable() {
				public void run() {
	
					ArrayList<String> arraylist = FileUtility.readFile("backup/logs/player base/collection ip name.txt");
					for (int index = 0; index < arraylist.size(); index++) {
						try {
							String[] parse = arraylist.get(index).split("-");
							if (parse[1].equalsIgnoreCase(oldName)) {
								arraylist.remove(index);
								arraylist.add(parse[0] + "-" + Misc.capitalize(newName1));
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							Misc.print("Wrong parse: " + arraylist.get(index));
							arraylist.remove(index);
							index--;
						}
					}
					FileUtility.deleteAllLines("backup/logs/player base/collection ip name.txt");
					FileUtility.saveArrayContentsSilent("backup/logs/player base/collection ip name.txt", arraylist);
	
					ServerShutDownUpdate.serverSaveActive = false;
	
				}
			}).start();
		if (player.getClanId() >= 0) {
			Server.clanChat.updateClanChat(player.getClanId());
		}
	}

}
