package game.content.commands;

import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.donator.AfkChair;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.donator.Yell;
import game.content.interfaces.ChangePasswordInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.interfaces.donator.DonatorShop;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.ClaimPrize;
import game.content.miscellaneous.EdgePvp;
import game.content.miscellaneous.GiveAway;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.NpcDropTableInterface;
import game.content.miscellaneous.PlayerGameTime;
import game.content.miscellaneous.PlayerRank;
import game.content.miscellaneous.PvpBlacklist;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.SpellBook;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.YoutubePaid;
import game.content.packet.PrivateMessagingPacket;
import game.content.profile.NpcKillTracker;
import game.content.quest.Quest;
import game.content.quest.QuestHandler;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Runecrafting;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.staff.PrivateAdminArea;
import game.content.staff.StaffManagement;
import game.content.wildernessbonus.WildernessRisk;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.LogOutUpdate;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.PlayerSave;
import game.player.punishment.Blacklist;
import game.player.punishment.WildernessData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import network.connection.DonationManager;
import network.sql.SQLConstants;
import network.sql.SQLNetwork;
import network.sql.query.impl.DoubleParameter;
import network.sql.query.impl.StringParameter;
import utility.AESencrp;
import utility.EmailSystem;
import utility.FileUtility;
import utility.HackLogHistory;
import utility.Misc;
import utility.WebsiteLogInDetails;
import utility.WebsiteSqlConnector;

/**
 * Normal player commands.
 *
 * @author MGT Madness, created on 12-12-2013.
 */
public class NormalCommand {

	/**
	 * Commands eligible for normal players only.
	 *
	 * @param player The player using the command.
	 */
	public static boolean normalCommands(Player player, String playerCommand) {
		if (playerCommand.equals("test1")) {
			test1(player, playerCommand);
			return true;
		} else if (playerCommand.contains("test2")) {
			test2(player, playerCommand);
			return true;
		} else if (playerCommand.equals("deletefriendslist")) {
			int deletedAmount = 0;
			for (int i = 0; i < player.friends.length; i++) {
				if (player.friends[i][0] != 0) {
					Player friendInstance = Misc.getPlayerByName(Misc.longToPlayerName(player.friends[i][0]));
					if (friendInstance == null) {
						player.friends[i][0] = 0;
						deletedAmount++;
					}
				}
			}
			if (deletedAmount > 0) {
				player.getPA().sendMessage(":packet:deletefriendslist");
				player.getPA().privateMessagingLogIn();
			}
			player.getPA().sendMessage("You have deleted " + deletedAmount + " friend" + Misc.getPluralS(deletedAmount) + " who are offline.");
			return true;
		} else if (playerCommand.equals("questcheat") && player.isAdministratorRank()) {
			if (!ServerConfiguration.DEBUG_MODE) {
				return true;
			}
			for (int i = 0; i <= Quest.totalQuests; i++) {
				player.getQuest(i).setStage(Quest.cachedQuestConfig[i].stages);
				QuestHandler.updateAllQuestTab(player);
				player.getPA().sendMessage("wat");
			}
		} else if (playerCommand.equals("test3")) {
			test3(player, playerCommand);
			return true;
		}
		switch (playerCommand) {
			case "drop":
			case "drops":
			case "droptable":
				NpcDropTableInterface.displayInterface(player, true);
				return true;

			case "staffzone":
			case "sz":
				if (player.isModeratorRank() || player.isSupportRank()) {
					AdministratorCommand.staffzone(player);
					return true;
				}
				return false;
			case "goldwebsite":
			case "goldwebsites":
			case "07websites":
			case "07website":
				player.getPA().openWebsite("www.dawntained.com/osrs", true);
				return true;
			case "07account":
				player.getPA().openWebsite("www.account4rs.com/sell_to_us.html", true);
				return true;
			case "priceguide":
			case "price":
			case "prices":
			case "pricelist":
			case "pricecheck":
				player.getPA().openWebsite("www.dawntained.com/forum/topic/1756-price-guide/", true);
				return true;
			case "multibug":
				if (!Area.inWilderness(player.getX(), player.getY(), player.getHeight())) {
					boolean match = false;
					for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
						Player loop = PlayerHandler.players[index];
						if (loop == null) {
							continue;
						}
						if (loop.addressIp.equals(player.addressIp) && Misc.uidMatches(player.addressUid, loop.addressUid)) {
							if (Area.inWilderness(loop.getX(), loop.getY(), loop.getHeight())) {
								match = true;
								break;
							}
						}
					}
					if (!match) {
						boolean fixed = false;
						for (int index = 0; index < WildernessData.wildernessData.size(); index++) {
							if (player.addressIp.equals(WildernessData.wildernessData.get(index).ip) && Misc.uidMatches(player.addressUid,
							                                                                                            WildernessData.wildernessData.get(index).uid)) {
								player.getPA().sendMessage("Multi log bug fixed.");
								WildernessData.wildernessData.remove(index);
								fixed = true;
								break;
							}
						}
						if (!fixed) {
							player.getPA().sendMessage("Your account is not bugged.");
						}
					} else {
						player.getPA().sendMessage("Multi logging is not allowed!");
					}
				} else {
					player.getPA().sendMessage("You cannot use this in the wild!");
				}
				return true;
			case "artifact":
			case "artefact":
			case "artifacts":
			case "artefacts":
			case "pkingloot":
				int frameIndex = 0;
				player.getPA().sendFrame126("Pking loot bonuses:", 25003);
				for (int index = 0; index < Artefacts.pkingLootData.size(); index++) {
					player.getPA().sendFrame126(Artefacts.pkingLootData.get(index), 25008 + frameIndex);
					frameIndex++;
				}
				double scrollAmount = frameIndex * 20.7;
				InterfaceAssistant.setFixedScrollMax(player, 25007, (int) Math.ceil(scrollAmount));
				InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
				player.getPA().displayInterface(25000);
				return true;

			case "doubler" :
			case "double" :
			case "npcdoubler" :
			case "itemdoubler" :
				Teleport.spellTeleport(player, 3091, 3505, 0, false);
				return true;
			case "edge":
			case "edgeville":
			case "home":
				Teleport.spellTeleport(player, 3084 + Misc.random(5), 3490 + Misc.random(7), 0, false);
				return true;

			case "market":
			case "fally":
			case "park":
			case "trading":
				Teleport.spellTeleport(player, 2999, 3383, 0, false);
				return true;
			case "barrow":
			case "barrows":
				Teleport.spellTeleport(player, 3565, 3315, 0, false);
				return true;

			case "duel":
			case "duelarena":
			case "dueling":
				Teleport.spellTeleport(player, 3366, 3266, 0, false);
				return true;
			case "cw":
			case "clanwars":
			case "clanwar":
			case "cws":
				Teleport.spellTeleport(player, 3327, 4758, 0, false);
				return true;

			case "skilling":
			case "skills":
			case "entrana":
			case "skill":
				Teleport.spellTeleport(player, 2866, 3337, 0, false);
				return true;

			case "edgewild":
			case "ditch":
			case "edgewildy":
			case "wildy":
			case "wild":
				Teleport.spellTeleport(player, 3087, 3517, 0, false);
				return true;

			case "corp":
			case "corporeal":
			case "corpbeast":
			case "corporealbeast":
				if (GameType.isOsrsEco()) {
					Teleport.spellTeleport(player, 2966, 4382, 2, false);
					return true;
				}
				return false;

			case "vetion":
			case "vet":
				warningTeleport(player, 3220, 3789, 0);
				return true;

			case "wests":
			case "west":
			case "13s":
				warningTeleport(player, 2979, 3594, 0);
				return true;
			case "gorillas":
				warningTeleport(player, 3108, 3674, 0);
				return true;
			case "chaosfanatic" :
				warningTeleport(player, 2979, 3848, 0);
				return true;
			case "elder":
			case "elders":
			case "chaosdruid":
			case "chaosdruids":
			case "elder druids":
			case "elder druid":
				warningTeleport(player, 3235, 3635, 0);
				return true;
			case "chin":
			case "chins":
				warningTeleport(player, 3138, 3785, 0);
				return true;
			case "easts":
			case "east":
			case "18s":
				warningTeleport(player, 3348, 3647, 0);
				return true;
			case "graves":
			case "grave":
				warningTeleport(player, 3146, 3671, 0);
				return true;
			case "td":
			case "tds":
				warningTeleport(player, 3260, 3705, 0);
				return true;
			case "rev":
			case "revs":
				warningTeleport(player, 2978, 3735, 0);
				return true;
			case "train":
				player.getPA().sendMessage("Click on a spell in your spellbook or talk to the Wizard at home to view teleports.");
				if (GameType.isOsrsEco()) {
					player.getPA().sendMessage("Select a teleport under the 'Monsters' tab to train up your combat.");
				}
				return true;
			case "ven":
			case "venenatis":
				warningTeleport(player, 3308, 3737, 0);
				return true;
			case "lavadrag":
			case "lavadragons":
			case "lavadrags":
				warningTeleport(player, 3070, 3760, 0);
				return true;

			case "callisto":
			case "calisto":
			case "callistos":
				warningTeleport(player, 3202, 3865, 0);
				return true;

			case "44":
			case "44s":
				warningTeleport(player, 2977, 3873, 0);
				return true;

			case "gdz":
			case "gds":
			case "demonics":
			case "demonicdemons":
				warningTeleport(player, 3288, 3886, 0);
				return true;

			case "kbd":
				Teleport.spellTeleport(player, 2271, 4680, 0, false);
				return true;

			case "chaosele":
			case "chaoseles":
			case "chaos":
			case "chaoselemental":
			case "52s":
				warningTeleport(player, 3307, 3916, 0);
				return true;

			case "arch":
			case "crazy":
			case "crazyarch":
			case "archa":
				warningTeleport(player, 2980, 3713, 0);
				return true;

			case "scorp":
			case "scorpia":
			case "scorpcave":
				warningTeleport(player, 3231, 3944, 0);
				return true;

			case "magearena":
			case "ma":
				warningTeleport(player, 3105, 3934, 0);
				return true;

			case "mb":
			case "magebank":
				Teleport.spellTeleport(player, 2537, 4714, 0, false);
				return true;
			case "tourny":
			case "tournament":
			case "tourn":
				Teleport.spellTeleport(player, 3106, 3498, 0, false);
				return true;

			case "shop":
			case "shops":
				Teleport.spellTeleport(player, 3080, 3510, 0, false);
				return true;

			case "char":
				player.getPA().sendMessage("Talk to Thessalia inside Edgeville bank.");
				return true;

			case "food":
			case "shark":
			case "sharks":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				ItemAssistant.addItem(player, 385, ItemAssistant.getFreeInventorySlots(player));
				return true;

			case "barrage":
			case "barragerunes":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				QuickSetUp.barrageRunes(player);
				return true;
			case "ancient":
			case "ancients":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				SpellBook.ancientMagicksSpellBook(player, true);
				player.getPA().sendMessage("You switch to the ancient magicks spellbook.");
				return true;

			case "veng":
			case "vengeance":
			case "vengrunes":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				QuickSetUp.vengeanceRunes(player);
				return true;

			case "lunar":
			case "lunars":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				SpellBook.lunarSpellBook(player, true);
				player.getPA().sendMessage("You switch to the lunar spellbook.");
				return true;
			case "modern":
			case "moderns":
			case "normal":
			case "normals":
			case "regular":
			case "regulars":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				SpellBook.modernSpellBook(player, true);
				player.getPA().sendMessage("You switch to the normal spellbook.");
				return true;

			case "unskull":
				player.getPA().sendMessage("Exit the clan wars portal. ::cw");
				return true;
			case "gamble":
			case "gambling":
			case "dice":
			case "dicing":
			case "fp":
			case "dicezone":
				if (ClanChatHandler.inDiceCc(player, false, true)) {
					Teleport.spellTeleport(player, 1690, 4250, 0, false);
				} else {
					player.getPA().sendMessage("Join 'Dice' cc to start dicing.");
				}
				return true;

			case "tb":
			case "teleblock":
				if (GameType.isOsrsEco()) {
					return false;
				}
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				QuickSetUp.teleBlockRunes(player);
				return true;

			case "task":
			case "tasks":
				player.getPA().sendMessage(Slayer.getTaskString(player));
				return true;


			case "help":
			case "ticket":
			case "staff":
			case "staffonline":
				ArrayList<String> staffOnline = new ArrayList<String>();
				boolean online = false;
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
					Player loop = PlayerHandler.players[index];
					if (loop == null) {
						continue;
					}
					if (!loop.isModeratorRank() && !loop.isSupportRank() && !loop.isAdministratorRank()) {
						continue;
					}

					boolean show = true;
					if (PrivateMessagingPacket.isIgnored(loop, player)) {
						show = false;
					} else if (loop.privateChat == ServerConstants.PRIVATE_FRIENDS) {
						if (!loop.getPA().hasFriend(Misc.playerNameToInt64(player.getPlayerName()))) {
							show = false;
						}
					}
					if (loop.privateChat == ServerConstants.PRIVATE_OFF) {
						show = false;
					}
					if (!show) {
						continue;
					}
					if (loop.isModeratorRank()) {
						online = true;
					}
					staffOnline.add(loop.getPlayerName().toLowerCase() + "-" + loop.playerRights);
				}

				frameIndex = 0;
				player.getPA().sendFrame126("Staff list", 25003);
				Collections.shuffle(staffOnline);
				for (int a = 0; a < staffOnline.size(); a++) {
					String[] parse = staffOnline.get(a).split("-");
					player.getPA().sendFrame126(PlayerRank.getIconText(Integer.parseInt(parse[1]), true) + "" + Misc.capitalize(parse[0]) + " is online.", 25008 + frameIndex);
					frameIndex++;
				}
				player.getPA().sendFrame126("", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("@dre@Offline staff", 25008 + frameIndex);
				frameIndex++;
				for (int index = 0; index < StaffManagement.staffRanks.size(); index++) {
					String[] split = StaffManagement.staffRanks.get(index).split("-");
					String name = split[0].toLowerCase();
					String rankName = split[1];
					boolean match = false;
					for (int a = 0; a < staffOnline.size(); a++) {
						String[] otherSplit = staffOnline.get(a).split("-");
						if (name.equals(otherSplit[0])) {
							match = true;
							break;
						}
					}
					if (match) {
						continue;
					}
					player.getPA().sendFrame126(Misc.capitalize(name) + " (" + rankName + ") is offline.", 25008 + frameIndex);
					frameIndex++;
				}
				if (!online) {
					player.getPA().sendFrame126("No Mod is currently online, contact them on ::discord", 25008 + frameIndex);
					frameIndex++;
				}
				InterfaceAssistant.setFixedScrollMax(player, 25007, (int) (frameIndex * 22.5));
				InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
				player.getPA().displayInterface(25000);
				player.getPA().sendMessage("Please be respectful and patient to the staff team. Check ::rules");
				return true;
			case "kdr":
				player.getPA().quickChat("My kdr is: " + Misc.getKDR(player.getWildernessKills(false), player.getWildernessDeaths(false)));
				return true;

			case "f2pkdr":
				player.getPA().quickChat("My f2p kdr is: " + Misc.getKDR(player.getF2pKills(), player.deathTypes[5]));
				return true;

			case "bridkdr":
			case "hybridkdr":
				player.getPA().quickChat("My hybrid kdr is: " + Misc.getKDR(player.getHybridKills(), player.deathTypes[1]));
				return true;

			case "purekdr":
				player.getPA().quickChat("My pure kdr is: " + Misc.getKDR(player.getPureKills(), player.deathTypes[3]));
				return true;

			case "zerkkdr":
			case "berserkerkdr":
			case "zerkerkdr":
				player.getPA().quickChat("My berserker kdr is: " + Misc.getKDR(player.getBerserkerPureKills(), player.deathTypes[2]));
				return true;

			case "rangedkdr":
			case "rangedtankkdr":
				player.getPA().quickChat("My ranged tank kdr is: " + Misc.getKDR(player.getRangedTankKills(), player.deathTypes[4]));
				return true;

			case "killstreak":
			case "streak":
			case "ks":
				player.getPA().quickChat("My current killstreak is: " + player.currentKillStreak);
				return true;

			case "bosskills":
			case "bosskc":
				player.getPA().quickChat("My total boss killcount is: " + NpcKillTracker.getAllBossKills(player) + ". Boss score: " + player.bossScoreUnCapped);
				return true;

			case "snowballs":
			case "snowball":
			case "sb":
				try {
					double percentDodged = (double) player.snowBallsLandedOnMe / (double) player.snowBallsThrownAtMe;
					percentDodged *= 100.0;
					if (player.snowBallsLandedOnMe == 0) {
						player.getPA().quickChat("I have dodged every single snowball, call me king. ::snowballs");
					} else {
						int dodged = 100 - (int) percentDodged;
						player.getPA().quickChat(player.snowBallsThrownAtMe + " snow balls were thrown at me, of which " + dodged + "% i dodged! ::snowballs");
					}
				} catch (Exception e) {
					player.getPA().quickChat("No one wants to play with me :(");
				}
				return true;


			case "kill":
			case "kills":
				player.getPA().quickChat("My kills are: " + player.getWildernessKills(true));
				return true;

			case "death":
			case "deaths":
				player.getPA().quickChat("My deaths are: " + player.getWildernessDeaths(true));
				return true;

			case "resetkdr":
			case "reset kdr":
			case "kdrreset":
			case "kdr reset":
				player.getDH().sendDialogues(490);
				return true;

			case "vials":
			case "smashvials":
			case "smashvial":
				player.getDH().sendDialogues(635);
				return true;

			case "old":
			case "age":
			case "date":
			case "join":
			case "joined":
			case "time":
				player.getPA().quickChat("I joined " + PlayerGameTime.getDaysSinceAccountCreated(player) + " days ago on the " + player.accountDateCreated + ". ::old");
				return true;

			case "gameplay":
			case "gamemode":
			case "difficulty":
				if (GameType.isOsrsEco()) {
					String ending = " as " + Misc.getAorAn(Misc.capitalize(player.getGameMode())) + " player";
					if (player.getGameMode().equals("NONE")) {
						ending = "";
					}
					player.getPA().quickChat("I am using the " + Misc.capitalize(player.getDifficultyChosen()) + " difficulty" + ending + ". ::gameplay");
					return true;
				}
				return false;
			case "event":
			case "events":
				InterfaceAssistant.returnActiveEventOrUpdateEventInterface(player, false);
				return true;

			case "veteran":
				int highestDaysPlayed = 0;
				String highestUser = "";
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
					Player loop = PlayerHandler.players[index];
					if (loop == null) {
						continue;
					}
					if (loop.isAdministratorRank()) {
						continue;
					}
					if (loop.bot) {
						continue;
					}
					int days = PlayerGameTime.getDaysSinceAccountCreated(loop);
					if (days > highestDaysPlayed) {
						highestDaysPlayed = days;
						highestUser = loop.getPlayerName();
					}

				}
				player.getPA().quickChat("The oldest online player is " + highestUser + " who is " + highestDaysPlayed + " days old! ::veteran");
				return true;

			case "osrs":
			case "07":
				frameIndex = 0;
				player.getPA().sendFrame126("Donate with 07 gold", 25003);
				if (!DonationManager.EVENT_SALE.isEmpty()) {
					player.getPA().sendFrame126(
							"@dre@Donate now for " + ((int) (DonationManager.EVENT_SALE_MULTIPLIER * 100.0) - 100) + "% extra Donator Tokens! " + DonationManager.EVENT_SALE
							+ " offer!", 25008 + frameIndex);
					frameIndex++;
					player.getPA().sendFrame126("", 25008 + frameIndex);
					frameIndex++;
				}
				for (int i = 0; i < getOsrsText().length; i++) {
					player.getPA().sendFrame126(getOsrsText()[i], 25008 + frameIndex);
					frameIndex++;
				}
				InterfaceAssistant.setFixedScrollMax(player, 25007, (int) (frameIndex * 21.5));
				InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
				player.getPA().displayInterface(25000);
				return true;

		}
		if (playerCommand.startsWith("recoverpass")) {
			try {
				String accountName = playerCommand.substring(12);
				boolean notSamePerson = true;

				boolean canAccessAccount = HackLogHistory.isOriginalOwner(accountName, player.addressIp, player.addressUid);
				if (canAccessAccount) {
					String originalPass = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + accountName + ".txt", "Password", 3);
					String bankPin = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + accountName + ".txt", "bankPin", 3);
					originalPass = AESencrp.decrypt(originalPass);
					player.getPA().sendMessage("Your password for " + accountName + " is: '" + originalPass + "'");
					if (!bankPin.isEmpty()) {
						player.getPA().sendMessage("Your bank pin for " + accountName + " is: " + bankPin);
					}
					notSamePerson = false;
				}
				if (notSamePerson) {
					player.getPA().sendMessage("Your ip address & pc is not the same as the account: '" + accountName + "'");
				}
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::recoverpass owain");
			}
			return true;
		}
		if (playerCommand.startsWith("donate07") || playerCommand.startsWith("donateusd")) {
			String command = "donateusd";
			if (playerCommand.contains("donate07")) {
				command = "donate07";
			}
			try {
				double amount = Double.parseDouble(playerCommand.substring(command.contains("usd") ? 10 : 9));
				double usdAmount = amount;
				if (!command.contains("usd")) {
					usdAmount *= DonationManager.OSRS_DONATION_MULTIPLIER;
				}
				int tokens = DonationManager.getTokensAmountForUsd(usdAmount);
				String currency = "USD";
				String fullCurrency = "USD";
				if (playerCommand.contains("donate07")) {
					currency = "07 gold";
					fullCurrency = "m 07 gold";
				}
				int frameIndex = 0;


				player.getPA().sendFrame126(currency + " for Donator tokens", 25003);

				if (!DonationManager.EVENT_SALE.isEmpty()) {
					player.getPA().sendFrame126(
							"@dre@" + ((int) (DonationManager.EVENT_SALE_MULTIPLIER * 100.0) - 100) + "% bonus rewards for " + DonationManager.EVENT_SALE + " is included!",
							25008 + frameIndex);
					frameIndex++;
					player.getPA().sendFrame126("", 25008 + frameIndex);
					frameIndex++;
				}

				if (!command.contains("usd")) {
					player.getPA().sendFrame126("1m 07 is the same as " + 1 * DonationManager.OSRS_DONATION_MULTIPLIER + "$ payment which is " + (int) (10 * DonationManager.OSRS_DONATION_MULTIPLIER) + " Donator tokens", 25008 + frameIndex);
					frameIndex++;
				}
				player.getPA()
						.sendFrame126("You will receive x" + Misc.formatNumber(tokens) + " Donator tokens if you donate " + amount + " " + fullCurrency + (command.contains("usd") ? "." : ""), 25008 + frameIndex);
				frameIndex++;
				if (!command.contains("usd")) {
					player.getPA().sendFrame126("which counts as a " + Misc.roundDoubleToNearestTwoDecimalPlaces(usdAmount) + "$ payment", 25008 + frameIndex);
					frameIndex++;
				}
				player.getPA().sendFrame126("Type in ::" + command + " amountHere if you want to donate a diff amount", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("Type in ::osrs for more information", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("@dre@[Bonus tokens amount table]", 25008 + frameIndex);
				frameIndex++;
				String text = command.contains("usd") ? "" : "07";
				player.getPA().sendFrame126("300$+ " + text + " payment = 30% bonus Donator tokens", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("200$+ " + text + " payment = 25% bonus Donator tokens", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("100$+ " + text + " payment = 20% bonus Donator tokens", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("50$+ " + text + " payment = 15% bonus Donator tokens", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("35$+ " + text + " payment = 10% bonus Donator tokens", 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("20$+ " + text + " payment = 5% bonus Donator tokens", 25008 + frameIndex);
				frameIndex++;

				InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
				InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
				player.getPA().displayInterface(25000);
			} catch (Exception e) {
				player.getPA().sendMessage("use as ::" + command + " 50 for example");
			}
			return true;
		}

		if (playerCommand.equals("empty")) {
			if (player.isAdministratorRank()) {
				return false;
			}
			if (Combat.inCombatAlert(player)) {
				return true;
			}
			player.getDH().sendDialogues(286);
			return true;
		} else if (playerCommand.equals("edgepvp") || playerCommand.equals("pvp") || playerCommand.equals("hybridpvp") || playerCommand.equals("brid")) {
			if (GameType.isOsrsPvp()) {
				if (Teleport.spellTeleport(player, 3094, 3496, 4, false)) {
					EdgePvp.edgePvpTeleportedTo(player);
				}
				return true;
			} else {
				if (Teleport.spellTeleport(player, 2710, 2664, 0, false)) {
					EdgePvp.edgePvpTeleportedTo(player);
				}
				return true;
			}

		} else if (playerCommand.startsWith("changepass") || playerCommand.startsWith("pass") && !player.isAdministratorRank()) {
			ChangePasswordInterface.display(player);
			return true;
		} else if (playerCommand.startsWith("yt")) {
			player.getPA().openWebsite("www.dawntained.com/forum/topic/551-youtuber-rank-is-now-here", true);
			return true;
		} else if (playerCommand.startsWith("thread")) {
			try {
				int threadId = Integer.parseInt(playerCommand.substring(7));
				player.getPA().openWebsite("www.dawntained.com/forum/topic/" + threadId + "-dawntained", true);
			} catch (Exception e) {
				player.getPA().sendMessage("Wrong input, use ::thread 591 for example.");
			}
			return true;
		} else if (playerCommand.startsWith("board")) {
			try {
				int threadId = Integer.parseInt(playerCommand.substring(6));
				player.getPA().openWebsite("www.dawntained.com/forum/forum/" + threadId + "-dawntained", true);
			} catch (Exception e) {
				player.getPA().sendMessage("Wrong input, use ::board 8 for example.");
			}
			return true;
		} else if (playerCommand.startsWith("changepass")) {
			player.getPA().sendMessage("Type in ::changepassword YourNewPassHere");
			return true;
		} else if (playerCommand.startsWith("admin") && PlayerRank.isDeveloper(player) && ServerConfiguration.DEBUG_MODE) {
			String name = playerCommand.replace("admin ", "").toLowerCase();
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.getPlayerName().toLowerCase().equals(name)) {
					loop.playerRights = 2;
					player.getPA().sendMessage(name + " promoted to Administrator.");
					loop.setUpdateRequired(true);
					loop.setAppearanceUpdateRequired(true);
					break;
				}

			}
			return true;
		} else if (playerCommand.startsWith("player")) {
			if (Misc.serverRecentlyLaunched()) {
				player.getPA().sendMessage("Dawntained has been recently restarted for an update.");
				return true;
			}
			int frameIndex = 0;
			player.getPA().sendFrame126("Players online: " + PlayerHandler.getBoostedPlayerCount(), 25003);
			player.getPA().sendFrame126("Join the '" + ServerConstants.getServerName() + "' cc and check your Quest tab.", 25008 + frameIndex);
			frameIndex++;
			/*
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.bot) {
					continue;
				}
				boolean show = true;
				if (PrivateMessagingPacket.isIgnored(loop, player)) {
					show = false;
				}
				else if (loop.privateChat == ServerConstants.PRIVATE_FRIENDS) {
					if (!loop.getPA().hasFriend(Misc.playerNameToInt64(player.getPlayerName()))) {
						show = false;
					}
				}
				if (show || player.isModeratorRank()) {
					player.getPA().sendFrame126(PlayerRank.getIconText(loop.playerRights, true) + loop.getPlayerName(), 25008 + frameIndex);
					frameIndex++;
				}
			}
			*/
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return true;
		} else if (playerCommand.startsWith("/")) {
			slash(player, playerCommand);
			return true;
		} else if (playerCommand.startsWith("skull")) {
			skull(player, playerCommand);
			return true;
		} else if (playerCommand.startsWith("risk")) {
			player.timeUsedRiskCommand = System.currentTimeMillis();
			WildernessRisk.carriedWealth(player, true);
			player.getPA().quickChat(
					"I am currently risking " + ServerConstants.getFormattedNumberString(player.riskedWealth) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
			return true;
		} else if (playerCommand.startsWith("redskull") || playerCommand.startsWith("highrisk")) {
			if (player.getHeight() == 20) {
				return true;
			}
			if (player.headIconPk == 2) {
				return true;
			}
			if (player.getDead()) {
				return true;
			}
			Skull.redSkull(player);
			player.getPA().sendMessage(ServerConstants.RED_COL + "You have been red skulled! You cannot protect item.");
			player.getPA().quickChat("I have used the redskull command.");
			return true;
		} else if (playerCommand.startsWith("forum")) {
			player.getPA().openWebsite("www.dawntained.com/forum", true);
			return true;
		} else if (playerCommand.startsWith("donate") || playerCommand.startsWith("store") || playerCommand.startsWith("token")) {
			if (!Bank.hasBankingRequirements(player, true)) {
				player.getPA().sendMessage("You cannot do this here.");
				return true;
			}
			DonatorShop.openLastDonatorShopTab(player);
			return true;
		} else if (playerCommand.equals("bogla")) {
			player.getPA().openWebsite("www.boglagold.com/buy-runescape-gold/", true);
			return true;
		} else if (playerCommand.equals("partypete")) {
			player.getPA().openWebsite("www.partypeteshop.com/buy-osrs-gold/", true);
			return true;
		} else if (playerCommand.equals("arcus")) {
			player.getPA().openWebsite("www.arcusgold.com/cheap-rs-2007-runescape-gold/buy-rs-2007-gold", true);
			return true;
		} else if (playerCommand.equals("vote")) {
			player.getPA().openWebsite("www.dawntained.com/vote", true);
			player.getPA().sendMessage("Type in ::claimvote when you have voted.");
			return true;
		} else if (playerCommand.equals("fb")) {
			player.getPA().openWebsite("www.dawntained.com/fb", true);
			return true;
		} else if (playerCommand.startsWith("bug")) {
			player.getPA().openWebsite("www.dawntained.com/forum/forum/51-submit-a-bugclient-issue", true);
			return true;
		} else if (playerCommand.startsWith("discord")) {
			player.getPA().openWebsite("www.discord.gg/3uVTg4W", true);
			return true;
		} else if (playerCommand.startsWith("guide")) {
			player.getPA().openWebsite("www.dawntained.com/forum/forum/14-guides/", false);
			GuideBook.displayGuideInterface(player);
			return true;
		} else if (playerCommand.startsWith("updates")) {
			player.getPA().openWebsite("www.dawntained.com/forum/forum/10-dawntained-updates/", true);
			return true;
		} else if (playerCommand.startsWith("bots")) {
			player.getPA().toggleBots(false);
			return true;
		} else if (playerCommand.equals("claim") || playerCommand.equals("claimvote") || playerCommand.equals("voteclaim") || playerCommand.equals("claimvotes") || playerCommand
				                                                                                                                                                            .equals("voted")
		           || playerCommand.equals("claimreward") || playerCommand.equals("redeem")) {
			new Thread(new WebsiteSqlConnector(player, "CLAIM VOTE", WebsiteLogInDetails.VOTE_DATABASE_NAME,
			                                   "SELECT * FROM fx_votes WHERE username='" + player.getPlayerName().replaceAll(" ", "_")
			                                   + "' AND claimed=0 AND callback_date IS NOT NULL", "There are no votes for you to claim, make sure you voted on all sites ::vote",
			                                   WebsiteLogInDetails.IP_ADDRESS, WebsiteLogInDetails.SQL_USERNAME, WebsiteLogInDetails.SQL_PASSWORD)).start();
			return true;
		} else if (playerCommand.equals("claimevent")) {
			ClaimPrize.checkForReward(player, false);
			return true;
		} else if (playerCommand.startsWith("claimdonat") || playerCommand.equals("donated") || playerCommand.startsWith("claimtoken") || playerCommand.startsWith("checkdon")) {
			DonationManager.claimDonation(player);
			return true;
		} else if (playerCommand.startsWith("rule")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Rules list", 25003);
			player.getPA().sendFrame126("Please be friendly to everyone to lighten up the community.", 25008 + frameIndex);
			frameIndex++;
			player.getPA().sendFrame126("Staying positive and helpful will not get you in trouble.", 25008 + frameIndex);
			frameIndex++;
			player.getPA().sendFrame126("@red@Action will be taken if you constantly break rules.", 25008 + frameIndex);
			frameIndex++;
			player.getPA().sendFrame126("@red@Can result in mute/IP ban depending on severity.", 25008 + frameIndex);
			frameIndex++;
			for (int index = 0; index < rules.length; index++) {
				player.getPA().sendFrame126(rules[index], 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			player.getPA().openWebsite("www.dawntained.com/forum/topic/246-official-server-rules", false);
			return true;
		} else if (playerCommand.startsWith("command")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Commands list", 25003);
			for (int index = 0; index < commandsList.size(); index++) {
				player.getPA().sendFrame126(commandsList.get(index), 25008 + frameIndex);
				frameIndex++;
			}
			double scrollAmount = frameIndex * 20.7;
			InterfaceAssistant.setFixedScrollMax(player, 25007, (int) Math.ceil(scrollAmount));
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return true;
		} else if (playerCommand.startsWith("clan")) {
			player.getPA().openWebsite("www.dawntained.com/forum/forum/22-clan-recruitment/", true);
			return true;
		} else if (playerCommand.startsWith("forcelogout")) {
			LogOutUpdate.manualLogOut(player);
			return true;
		} else if (!ServerConfiguration.DEBUG_MODE && (playerCommand.startsWith("level") || playerCommand.startsWith("max") || playerCommand.startsWith("master"))) {
			player.getPA().sendMessage("Click on the skill tab icon to change your levels.");
			return true;
		} else if (playerCommand.equals("master") && ServerConfiguration.DEBUG_MODE) {
			for (int skill = 0; skill < ServerConstants.getTotalSkillsAmount(); skill++) {
				Skilling.addSkillExperience(player, Skilling.getExperienceForLevel(99), skill, false);
			}
			player.getPA().sendMessage("You have been maxed out in every skill.");
			return true;
		} else if (playerCommand.equals("max_health")) {
			if (!player.isAdministratorRank()) {
				return true;
			}

			if (!ServerConfiguration.DEBUG_MODE) {
				return true;
			}

			player.currentCombatSkillLevel[3] = 1_000;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
			return true;
		} else if (playerCommand.startsWith("item") || playerCommand.startsWith("pickup")) {
			if (player.isAdministratorRank()) {
				return false;
			}
			player.getPA().sendMessage("Use the shops at home to buy items or check your bank.");
			return true;
		} else if (playerCommand.startsWith("blacklist")) {
			Player playerAttackedMe = PlayerHandler.players[player.getLastAttackedBy()];
			if (Area.inEdgevilleWilderness(player)) {
				player.getPA().sendMessage("You can't blacklist players while in the Wilderness!");
				return true;
			}
			if (playerAttackedMe != null) {
				PvpBlacklist.addPvpBlacklist(player, "addpvpblacklist" + playerAttackedMe.getPlayerName(), false);
			}
			PvpBlacklist.displayPvpBlacklistInterface(player);
			return true;

		} else if (playerCommand.equals("don") || playerCommand.equals("dz") || playerCommand.equals("donatorzone") || playerCommand.equals("dzone")) {
			if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.DONATOR)) {
				player.getPA().sendMessage("You have been teleported to the Donator zone.");
				if (ServerConfiguration.DEBUG_MODE) {
					Teleport.spellTeleport(player, 2530, 2724, 0, false);
				}
				else {
					Teleport.spellTeleport(player, 2198, 3249, 0, false);
				}
			}
			return true;
		} else if (playerCommand.startsWith("afk")) {
			if (player.getPlayerName().equalsIgnoreCase("jason") && ServerConfiguration.DEBUG_MODE || DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR)) {
				AfkChair.afk(player);
			}
			return true;
		} else if (playerCommand.startsWith("xteletome") && !player.isAdministratorRank()) {
			if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.IMMORTAL_DONATOR)) {
				try {
					String name = playerCommand.substring(10);
					Player target = Misc.getPlayerByName(name);
					if (target == null) {
						player.getPA().sendMessage("Player is not online: " + name + ".");
						return true;
					}
					if (player.getMinigame() != null) {
						return true;
					}
					if (target.getDuelStatus() >= 1) {
						player.playerAssistant.sendMessage(target.getPlayerName() + " is dueling.");
						return true;
					}
					if (target.getHeight() == 20) {
						player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
						return true;
					}
					if (target.isJailed()) {
						player.getPA().sendMessage(target.getPlayerName() + " is jailed.");
						return true;
					}
					if (Area.inDangerousPvpAreaOrClanWars(target)) {
						player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
						return true;
					}
					if (Area.inStaffZone(player)) {
						player.getPA().sendMessage("You cannot do this here.");
						return true;
					}
					if (player.isJailed()) {
						return true;
					}
					if (Area.inDonatorZone(player.getX(), player.getY())) {
						if (!DonatorContent.canUseFeature(target, DonatorTokenUse.DonatorRankSpentData.DONATOR)) {
							player.getPA().sendMessage("You cannot teleport non donators to donator zone.");
							return true;
						}
					}
					if (player.getHeight() != 0) {
						player.getPA().sendMessage("You cannot teleport players to this height.");
						return true;
					}

					if (!Bank.hasBankingRequirements(player, true)) {
						return true;
					}
					target.teleToNamePermission = player.getPlayerName();
					target.teleToXPermission = player.getX();
					target.teleToYPermission = player.getY();
					target.teleToHeightPermission = player.getHeight();
					target.getDH().sendDialogues(477);
					player.playerAssistant.sendMessage("Sent teleport request to: " + target.getPlayerName() + ".");
				} catch (Exception e) {
					player.getPA().sendMessage("Wrong usage. Correct is ::xteletome mgt madness");
				}
			}
			return true;
		} else if (playerCommand.startsWith("xteleto") && !player.isAdministratorRank()) {
			if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.IMMORTAL_DONATOR)) {
				try {
					String name = playerCommand.substring(8);
					Player target = Misc.getPlayerByName(name);
					if (target == null) {
						player.getPA().sendMessage("Player is not online: " + name + ".");
						return true;
					}
					int teleportToX = target.getX();
					int teleportToY = target.getY();
					if (PrivateAdminArea.playerIsInAdminArea(target)) {
						player.getPA().sendMessage("Player is not online: " + name + ".");
						return true;
					}
					if (Area.inDuelArenaRing(target)) {
						teleportToX = 3346;
						teleportToY = 3250;
					}
					if (Area.inDangerousPvpAreaOrClanWars(target)) {
						player.getPA().sendMessage("This player is in the Wilderness.");
						return true;
					}
					if (target.getHeight() == 20) {
						player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
						return true;
					}

					if (player.isJailed()) {
						return true;
					}
					if (target.getMinigame() != null) {
						player.playerAssistant.sendMessage(target.getPlayerName() + " is in a minigame.");
						return true;
					}
					if (Area.inJailArea(target)) {
						player.getPA().sendMessage(target.getPlayerName() + " is jailed.");
						return true;
					}
					if (Area.inStaffZone(target)) {
						player.getPA().sendMessage(target.getPlayerName() + " is at the staff zone.");
						return true;
					}
					if (!Bank.hasBankingRequirements(player, true)) {
						return true;
					}

					if (Runecrafting.isRunecraftingDonatorAbuseFlagged(player, false)) {
						return true;
					}
					player.playerAssistant.sendMessage("Teleported to: " + Misc.capitalize(name) + ".");
					Teleport.spellTeleport(player, teleportToX, teleportToY, target.getHeight(), false);
				} catch (Exception e) {
					player.getPA().sendMessage("Wrong usage. Correct is ::xteleto mgt madness");
				}
			}
			return true;
		} else if (playerCommand.startsWith("yell ")) {
			Yell.yellCommand(player, playerCommand);
			return true;
		} else if (playerCommand.startsWith("setyelltag")) {
			Yell.setYellTag(player, playerCommand);
			return true;
		} else if (playerCommand.startsWith("settitle")) {
			if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR)) {
				try {
					String title = playerCommand.substring(9);
					if (Misc.containsPassword(player.playerPass, title)) {
						return true;
					}
					PlayerTitle.setTitle(player, title, false, false);
				} catch (Exception e) {
				}
			}
			return true;
		} else if (playerCommand.equals("titleaftername")) {
			if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR)) {
				PlayerTitle.setTitle(player, player.playerTitle, true, false);
				player.getPA().sendMessage("Your title will now appear after your name.");
			}
			return true;
		} else if (playerCommand.equals("titlebeforename")) {

			if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR)) {
				PlayerTitle.setTitle(player, player.playerTitle, false, false);
				player.getPA().sendMessage("Your title will now appear before your name.");
			}
			return true;
		}

		if (GiveAway.isGiveawayCommand(player, playerCommand)) {
			return true;
		}

		if (player.getPlayerName().equals("Mgt Madness")) {
			if (playerCommand.startsWith("savehax")) {
				AdministratorCommand.packetLogSave(player);
				return true;
			} else if (playerCommand.equals("packetlogclear")) {
				AdministratorCommand.packetLogClear(player);
				return true;
			} else if (playerCommand.startsWith("packetlogadd")) {
				AdministratorCommand.packetLogAdd(player, playerCommand);
				return true;
			} else if (playerCommand.equals("packetlogsave")) {
				AdministratorCommand.packetLogSave(player);
				return true;
			} else if (playerCommand.equals("packetlogview")) {
				AdministratorCommand.packetLogView(player);
				return true;
			} else if (playerCommand.equals("balance07")) {
				player.getPA().sendMessage(ServerConstants.PURPLE_COL + "07 inventory: " + DonationManager.getOsrsInInventory());
				return true;
			} else if (playerCommand.startsWith("subtract07")) {
				try {
					int amount = Integer.parseInt(playerCommand.substring(playerCommand.indexOf(" ") + 1));
					player.getPA().sendMessage(
							ServerConstants.PURPLE_COL + "07 Subtract " + amount + " from " + DonationManager.getOsrsInInventory() + " = " + (DonationManager.getOsrsInInventory() - amount));

					// Email
					ArrayList<String> content = new ArrayList<String>();
					content.add("Name: " + player.getPlayerName());
					content.add("Ip: " + player.addressIp);
					content.add("Uid: " + player.addressUid);
					content.add("Subtract " + amount + " from " + DonationManager.getOsrsInInventory() + " = " + (DonationManager.getOsrsInInventory() - amount));
					EmailSystem.addPendingEmail("Osrs: " + (DonationManager.getOsrsInInventory() - amount) + "m, subtracted: " + amount + " from " + DonationManager.getOsrsInInventory(),
					                            content, "mgtdt@yahoo.com");

					DonationManager.setOsrsInInventory(Misc.roundDoubleToNearestOneDecimalPlace(DonationManager.getOsrsInInventory() - amount));
					SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_OSRS_HISTORY) + " (time, entity, action, total_expected) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, "Admin"), new StringParameter(3, player.getPlayerName() + " has subtracted " + amount + "m"), new DoubleParameter(4, DonationManager.getOsrsInInventory()));
				} catch (Exception e) {
					player.getPA().sendMessage("Use as ::subtract07 50");
				}
				return true;
			} else if (playerCommand.startsWith("add07")) {
				try {
					int amount = Integer.parseInt(playerCommand.substring(playerCommand.indexOf(" ") + 1));
					player.getPA().sendMessage(
							ServerConstants.PURPLE_COL + "07 Add " + amount + " to " + DonationManager.getOsrsInInventory() + " = " + (DonationManager.getOsrsInInventory() + amount));

					// Email
					ArrayList<String> content = new ArrayList<String>();
					content.add("Name: " + player.getPlayerName());
					content.add("Ip: " + player.addressIp);
					content.add("Uid: " + player.addressUid);
					content.add("Add " + amount + " to " + DonationManager.getOsrsInInventory() + " = " + (DonationManager.getOsrsInInventory() + amount));
					EmailSystem.addPendingEmail("Osrs: " + (DonationManager.getOsrsInInventory() + amount) + "m, added: " + amount + " to " + DonationManager.getOsrsInInventory(), content,
					                            "mgtdt@yahoo.com");

					DonationManager.setOsrsInInventory(Misc.roundDoubleToNearestOneDecimalPlace(DonationManager.getOsrsInInventory() + amount));
					SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_OSRS_HISTORY) + " (time, entity, action, total_expected) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, "Admin"), new StringParameter(3, player.getPlayerName() + " has added " + amount + "m"), new DoubleParameter(4, DonationManager.getOsrsInInventory()));
				} catch (Exception e) {
					player.getPA().sendMessage("Use as ::add07 50");
				}
				return true;
			}
			if (playerCommand.startsWith("haxonline")) {
				player.getPA().sendMessage("---------");
				String name = playerCommand.substring(10);
				boolean online = false;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
					Player loop = PlayerHandler.players[i];
					if (loop == null) {
						continue;
					}
					if (loop.addressIp.contains(name) || loop.addressUid.toLowerCase().contains(name)) {
						player.getPA().sendMessage(loop.getPlayerName());
						player.getPA().sendMessage(loop.addressIp);
						player.getPA().sendMessage(loop.addressUid);
						online = true;
					}
				}
				if (!online) {
					player.getPA().sendMessage("No match found for: '" + name + "'");
				}
				return true;
			}
			if (playerCommand.startsWith("savechar")) {
				String name = playerCommand.substring(9);
				boolean online = false;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name)) {
							player.getPA().sendMessage(PlayerHandler.players[i].getPlayerName() + " is online and has been saved.");
							PlayerSave.saveGame(PlayerHandler.players[i]);
							online = true;
							break;
						}
					}
				}
				if (!online) {
					player.getPA().sendMessage(name + " is not online.");
				}
				return true;
			}
			if (playerCommand.startsWith("hax")) {
				String name = playerCommand.substring(4);
				boolean online = false;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name)) {
							player.getPA().sendMessage(name + " is online and has been saved.");
							player.getPA().sendMessage(PlayerHandler.players[i].addressIp);
							player.getPA().sendMessage(PlayerHandler.players[i].addressUid);
							PlayerSave.saveGame(player);
							online = true;
							break;
						}
					}
				}

				if (!online) {
					String text1 = "";
					String text2 = "";
					String text3 = "";
					try {
						BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + name + ".txt"));
						String line;
						while ((line = file.readLine()) != null) {
							if (line.startsWith("Password = ")) {
								String password = line.substring(11);
								try {
									text1 = name + " is offline password of: '" + AESencrp.decrypt(password) + "'";
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (line.startsWith("lastSavedIpAddress = ")) {
								text2 = line.substring(20);
							} else if (line.startsWith("addressUid = ")) {
								text3 = line.substring(13);
							}
						}
						file.close();
					} catch (Exception e) {
					}
					player.getPA().sendMessage(text1);
					player.getPA().sendMessage(text2);
					player.getPA().sendMessage(text3);
				}
				return true;
			}
		}
		if (YoutubePaid.blackSkullCommand(player, playerCommand)) {
			return true;
		}
		return false;

	}

	/**
	 * Store the commands.
	 */
	private static ArrayList<String> commandsList = new ArrayList<String>();

	/**
	 * Load the commands list for the players to know via ::commands
	 */
	public static void loadCommandsList() {
		commandsList.add("@dre@[Links]");
		commandsList.add("vote (tonnes of " + ServerConstants.getMainCurrencyName().toLowerCase() + " reward!)");
		commandsList.add("donate (donate for perks and items)");
		commandsList.add("discord");
		commandsList.add("guide");
		commandsList.add("forum");
		commandsList.add("updates");
		commandsList.add("bugs");
		commandsList.add("clan");
		commandsList.add("thread threadNumberHere");
		commandsList.add("board boardNumberHere");
		commandsList.add("yt");
		commandsList.add("osrs (to donate osrs gold)");
		commandsList.add("price");
		commandsList.add("");
		commandsList.add("@dre@[Claim]");
		commandsList.add("claimvote");
		commandsList.add("claimdonation");
		commandsList.add("claimevent");
		commandsList.add("");
		commandsList.add("@dre@[Information]");
		commandsList.add("rules");
		commandsList.add("staff");
		commandsList.add("events");
		commandsList.add("pkingloot");
		commandsList.add("");
		commandsList.add("@dre@[Settings]");
		commandsList.add("drag 15 (use number 5-20 to adjust switching ease)");
		commandsList.add("bots (turns wild bots on/off)");
		commandsList.add("togglemaximizable (enables your fixed client to maximize)");
		commandsList.add("angle (camera angles for videos)");
		commandsList.add("toggletitles (turns player titles in wild on/off)");
		commandsList.add("fps");
		commandsList.add("snow");
		commandsList.add("snowparticles");
		commandsList.add("lightsnow");
		commandsList.add("toggleclickfix (fixes right and left click)");
		commandsList.add("");
		commandsList.add("@dre@[Player]");
		commandsList.add("changepassword");
		commandsList.add("::recoverpass lostAccountNameHere (will give pass & bank pin)");
		commandsList.add("empty");
		commandsList.add("smashvial");
		commandsList.add("skull");
		commandsList.add("redskull");
		commandsList.add("tokens");
		commandsList.add("veteran");
		commandsList.add("old");
		if (GameType.isOsrsEco()) {
			commandsList.add("difficulty");
		}
		commandsList.add("bosskills");
		commandsList.add("kdr");
		commandsList.add("purekdr");
		commandsList.add("hybridkdr");
		commandsList.add("f2pkdr");
		commandsList.add("rangedkdr");
		commandsList.add("zerkkdr");
		commandsList.add("resetkdr");
		commandsList.add("task");
		commandsList.add("::deletefriendslist");
		commandsList.add("modern");
		commandsList.add("ks");
		commandsList.add("kills");
		commandsList.add("deaths");
		commandsList.add("players");
		commandsList.add("risk");
		commandsList.add("blacklist");
		commandsList.add("");
		commandsList.add("@dre@[Donator commands]");
		commandsList.add("yell");
		commandsList.add("dz");
		commandsList.add("");
		commandsList.add("@dre@[Legendary Donator commands]");
		commandsList.add("settitle titleHere");
		commandsList.add("titleaftername");
		commandsList.add("titlebeforename");
		commandsList.add("");
		commandsList.add("@dre@[Ultimate Donator commands]");
		commandsList.add("bank");
		commandsList.add("");
		commandsList.add("@dre@[Uber Donator commands]");
		commandsList.add("setyelltag");
		commandsList.add("");
		commandsList.add("@dre@[Immortal Donator commands]");
		commandsList.add("xteletome playernamehere");
		commandsList.add("xteleto playernamehere");
		commandsList.add("");
		commandsList.add("@dre@[Teleports]");
		commandsList.add("home");
		commandsList.add("edgewild");
		commandsList.add("duel");
		commandsList.add("cw");
		commandsList.add("wests");
		commandsList.add("easts");
		commandsList.add("graves");
		commandsList.add("gorillas");
		commandsList.add("chaosfanatic");
		commandsList.add("crazyarch");
		commandsList.add("scorpia");
		commandsList.add("tds");
		commandsList.add("elders");
		commandsList.add("revs");
		commandsList.add("chins");
		commandsList.add("venenatis");
		commandsList.add("vetion");
		commandsList.add("lavadrags");
		commandsList.add("callisto");
		commandsList.add("44s");
		commandsList.add("gdz");
		commandsList.add("chaosele");
		commandsList.add("magearena");
		commandsList.add("mb");
		commandsList.add("kbd");
		commandsList.add("tournament");
		commandsList.add("shops");
		commandsList.add("edgepvp");
		commandsList.add("skilling");
		commandsList.add("market");
		commandsList.add("dice");
		commandsList.add("");
		commandsList.add("@dre@[Spawn]");
		commandsList.add("barrage");
		commandsList.add("tb");
		commandsList.add("veng");
		commandsList.add("ancients");
		commandsList.add("lunars");
		commandsList.add("tb");
		commandsList.add("food");
	}

	public static void warningTeleport(Player player, int x, int y, int height) {
		if (player.teleportWarning) {
			player.getDH().sendDialogues(457);
			player.teleportWarningX = x;
			player.teleportWarningY = y;
			player.teleportWarningHeight = height;
			return;
		}
		Teleport.spellTeleport(player, x, y, height, false);
	}

	private final static String[] rules =
			{
					"No Real World Trading",
					"No Racism",
					"No Scamming",
					"No Offensive language",
					"No Spamming staff",
					"No Bug abusing",
					"No Advertising",
					"No Encouraging rule breaking",
					"No botting",
					"No Impersonation",
					"No selling accounts",
					"No hacking and account hijacking",
					"No stealing wealth from other players accounts",
					"No ragging/rushing with overheads/insta tab",
					"No doxing of any sorts",
					"Yell only used for informing all players. Spam/flame/lying is a No"
			};

	private static void skull(Player player, String playerCommand) {
		if (player.getHeight() == 20) {
			return;
		}
		if (player.getRedSkull() && (Area.inDangerousPvpArea(player) || Combat.inCombat(player))) {
			player.playerAssistant.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.getDead()) {
			return;
		}
		Skull.whiteSkull(player);
		ItemsKeptOnDeath.updateInterface(player);
		player.getPA().sendMessage("You have been skulled.");
	}

	/*
	CustomBot bot = new CustomBot("speaking bot", "password");
	bot.attatchEvent(new CustomBotEvent(bot.getClient()));
	bot.startEvent();
	BotUtils.addBot(bot);
	*/
	/*// Update prices.txt
	try
			{
					BufferedWriter bw = null;
					bw = new BufferedWriter(new FileWriter("prices.txt", true));
					for (int index = 0; index < ItemDefinition.getDefinitions().length; index++)
					{
							if (ItemDefinition.getDefinitions()[index] == null)
							{
									continue;
							}
							int price = ItemDefinition.getDefinitions()[index].price;
							if (BloodMoneyPrice.getBloodMoneyPrice(index) > 0)
							{
									price = 200000000 + BloodMoneyPrice.getBloodMoneyPrice(index);
							}
							bw.write(index + " " + price);
							bw.newLine();
	
					}
					bw.flush();
					bw.close();
			}
			catch (IOException ioe)
			{
					ioe.printStackTrace();
			}
	
	URL tmp;
			try
			{
					// I cannot send more than 150 requests a minute or i get ip banned.
					String ip = "70.72.54.82";
					tmp = new URL("http://ip-api.com/line/" + ip);
					BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
					br.readLine();
					Misc.print(br.readLine());
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
			}
			catch (MalformedURLException e)
			{
					e.printStackTrace();
			}
			catch (IOException e)
			{
					e.printStackTrace();
			}
			
			
			// Find what section of game tick lags the most.
			double npc = 0;
			double player1 = 0;
			double event = 0;
			try
			{
					BufferedReader file = new BufferedReader(new FileReader("lag spike.txt"));
					String line;
					while ((line = file.readLine()) != null)
					{
							if (!line.isEmpty())
							{
									if (line.contains("Cycle event"))
									{
											event += Integer.parseInt(line.substring(13));
									}
									else if (line.contains("Player"))
									{
											player1 += Integer.parseInt(line.substring(8));
									}
									else if (line.contains("NPC"))
									{
											npc += Integer.parseInt(line.substring(5));
									}
							}
					}
					file.close();
			}
			catch (Exception e)
			{
			}
			double total = event + player1 + npc;
			Misc.print("Event: " + (event / total) * 100);
			Misc.print("Player: " + (player1 / total) * 100);
			Misc.print("Npc: " + (npc / total) * 100);
			
			
			final File folder = new File("players");
			int amount = 0;
			for (final File fileEntry : folder.listFiles())
			{
					// If the location is not a folder directory, then it has to be a file.
					if (!fileEntry.isDirectory())
					{
							String result = "";
							String name = "";
							String ip = "";
							try
							{
									BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
									String line;
									while ((line = file.readLine()) != null)
									{
											if (line.contains("Username ="))
											{
													name = line.substring(line.indexOf("=") + 2);
											}
											else if (line.contains("172.58.12.62"))
											{
													Misc.print(name + ", " + line);
											}
									}
									file.close();
							}
							catch (Exception e)
							{
							}
					}
			}
			
			
			final File folder = new File("players");
			int amount = 0;
			for (final File fileEntry : folder.listFiles())
			{
					// If the location is not a folder directory, then it has to be a file.
					if (!fileEntry.isDirectory())
					{
							String result = "";
							String name = "";
							String ip = "";
							try
							{
									BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
									String line;
									while ((line = file.readLine()) != null)
									{
											if (line.contains("Username ="))
											{
													name = line.substring(line.indexOf("=") + 2);
											}
											else if (line.contains("lastSavedIpAddress"))
											{
													String string = line.substring(line.lastIndexOf("=") + 2);
													if (string.isEmpty())
													{
															continue;
													}
													ip = string;
											}
											else if (line.contains("accountDateCreated"))
											{
													String string = line.substring(line.lastIndexOf("=") + 2);
													if (string.isEmpty())
													{
															continue;
													}
													if (string.contains("29/10/2016"))
													{
															Misc.print(ip);
															amount++;
													}
											}
									}
									file.close();
							}
							catch (Exception e)
							{
							}
					}
			}
			Misc.print("Amount: " + amount);


	/*
	for (int index = 0; index < player.playerItems.length; index++)
	{
			Misc.printDontSave("{" + (player.playerItems[index] - 1) + ", 1}, // " + ItemAssistant.getItemName(player.playerItems[index] - 1));
	}
	
	for (int index = 0; index < player.playerEquipment.length; index++)
	{
			Misc.printDontSave("{" + (player.playerEquipment[index]) + ", 1}, // " + ItemAssistant.getItemName(player.playerEquipment[index]));
	}
	*/


	/*
	ArrayList<String> data = new ArrayList<String>();
	ArrayList<String> kills = new ArrayList<String>();
	try
	{
			BufferedReader file = new BufferedReader(new FileReader("kills.txt"));
			String line;
			while ((line = file.readLine()) != null)
			{
					if (!line.isEmpty() && line.contains("Reward: [") && line.contains("="))
					{
							String name = line.substring(line.indexOf("]") + 2);
							name = name.substring(0, name.indexOf(" killed "));
							String amountString = line.substring(line.indexOf(" with ") + 6, line.indexOf(" damage at "));
							int damage = Integer.parseInt(amountString);
							data.add(name + "-" + damage);
							kills.add(name + "-" + 1);
	
					}
			}
			file.close();
	}
	catch (Exception e)
	{
	}
	
	kills = sort(kills, "-");
	Misc.printDontSave(kills + "");
	data = sort(data, "-");
	Misc.printDontSave(data + "");
	Misc.printDontSave(kills.size() + ", " + data.size());
	
	for (int index = 0; index < kills.size(); index++)
	{
			String parse[] = kills.get(index).split("-");
			for (int a = 0; a < data.size(); a++)
			{
					String aParse[] = data.get(a).split("-");
					if (aParse[0].equals(parse[0]))
					{
							String old = kills.get(index);
							kills.remove(index);
							String parseOld[] = old.split("-");
							kills.add(index, old + "-" + (Integer.parseInt(aParse[1]) / Integer.parseInt(parseOld[1])));
							break;
					}
			}
	}
	for (int index = 0; index < kills.size(); index++)
	{
			String parse[] = kills.get(index).split("-");
			int kills1 = Integer.parseInt(parse[1]);
			int averageDamage1 = Integer.parseInt(parse[2]);
			if (kills1 > 50 && averageDamage1 <= 160)
			{
					Misc.printDontSave(kills.get(index));
			}
			//Misc.printDontSave(kills.get(index));
	}
	/*
	
	/*
	final File folder = new File("players");
	int amount = 0;
	ArrayList<String> data = new ArrayList<String>();
	for (final File fileEntry : folder.listFiles())
	{
			// If the location is not a folder directory, then it has to be a file.
			if (!fileEntry.isDirectory())
			{
					String result = "";
					String name = "";
					String ip = "";
					try
					{
							BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
							String line;
							while ((line = file.readLine()) != null)
							{
									if (line.contains("Username ="))
									{
											name = line.substring(line.indexOf("=") + 2);
									}
									else if (line.startsWith("voteTotalPoints"))
									{
											int variableAmount = Integer.parseInt(line.substring(line.indexOf("=") + 2));
											data.add(name + "=" + (variableAmount / 5));
	
											//sort part
											Map<String, Integer> valueTest = new HashMap<String, Integer>();
											for (int i = 0; i < data.size(); i++)
											{
													String[] args = data.get(i).split("=");
													valueTest.put(args[0], Integer.parseInt(args[1]));
											}
	
											List list = new LinkedList(valueTest.entrySet());
	
											Collections.sort(list, new Comparator()
											{
													@Override
													public int compare(Object o1, Object o2)
													{
															// Swap o2 and o1 below to reverse the order.
															return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
													}
											});
	
											Map sortedMap = new LinkedHashMap();
											for (Iterator it = list.iterator(); it.hasNext();)
											{
													Map.Entry entry = (Map.Entry) it.next();
													sortedMap.put(entry.getKey(), entry.getValue());
											}
											data.clear();
											for (Object string : sortedMap.keySet())
											{
													Object kills = sortedMap.get(string);
													data.add(string.toString() + "=" + kills.toString());
											}
	
									}
							}
							file.close();
					}
					catch (Exception e)
					{
					}
			}
	}
	Misc.print(data + "");
	*/


	@SuppressWarnings(
			{"rawtypes", "unchecked"})
	public static ArrayList<String> sort(ArrayList<String> kills, String splitString) {
		ArrayList<String> finalIncomeList = new ArrayList<String>();
		for (int index = 0; index < kills.size(); index++) {
			String currentString = kills.get(index);
			int lastIndex = currentString.lastIndexOf(splitString);
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;
			for (int i = 0; i < finalIncomeList.size(); i++) {
				int lastIndex1 = finalIncomeList.get(i).lastIndexOf(splitString);
				String matchToFind1 = finalIncomeList.get(i).substring(0, lastIndex1);
				if (matchToFind1.equals(matchToFind)) {
					long numberValue = Long.parseLong(currentString.substring(lastIndex + 1));
					long finalNumberValue = Long.parseLong(finalIncomeList.get(i).substring(lastIndex + 1));
					long finalValueAdded = (finalNumberValue + numberValue);
					finalIncomeList.remove(i);
					finalIncomeList.add(i, matchToFind + splitString + finalValueAdded);
					finalIncomeListHas = true;
				}
			}

			if (!finalIncomeListHas) {
				finalIncomeList.add(currentString);
			}
		}
		kills = finalIncomeList;
		// Sorting. in order.
		Map<String, Long> valueTest = new LinkedHashMap<String, Long>();
		for (int i = 0; i < kills.size(); i++) {
			String[] args = kills.get(i).split(splitString);
			valueTest.put(args[0], Long.parseLong(args[1]));
		}
		List list = new LinkedList(valueTest.entrySet());

		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// Swap o2 and o1 below to reverse the order.
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		kills.clear();
		for (Object string1 : sortedMap.keySet()) {
			Object kills1 = sortedMap.get(string1);
			kills.add(string1.toString() + splitString + kills1.toString());
		}

		return kills;

	}


	public static ArrayList<String> clueScrollDebug = new ArrayList<String>();


	public static int totalDamage;

	public static int hits;

	public static int total0s;

	public static int count = 0;

	/**
	 * test1 command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void test1(Player player, String playerCommand) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}

		/*
		  Interface:
		    - It will tell player 383 lumbridge bank (f2p area, up the stairs)
		    - Player can click call bot button, a dialogue will appear asking player if they are at the correct location.
		    - Then player will click yes/no
		    - if player clicks yes, it will say, are you sure you will donate at least 1 million coins.
		    - If player clicks yes, it will say, abusing this feature will result in an ip-ban.
		    - If player clicks yes, summon the bot and tell the player to trade 'player 06'
		  */
		/*
		if (OsBotFlaggedPlayers.isBanned(player)) {
			return;
		}
		if (!OsBotCommunication.useBot || OsBotCommunication.forceDisableBot) {
			player.getPA().sendMessage("The 07 bot collector is offline, type in ::07 instead.");
			return;
		}
		if (player.secondsBeenOnline < OsBotCommunication.HOURS_ONLINE_TO_CALL_BOT * 3600) {
			player.getPA().sendMessage("You have not played enough to call the bot.");
			return;
		}
		String botReadyStatus = FileUtility.readFirstLine(OsBotCommunication.BOT_READY_FILE_LOCATION);
		
		if (botReadyStatus.isEmpty() || botReadyStatus.contains("false") || !Misc.timeElapsed(OsBotCommunication.timeBotCalledUsed, Misc.getSecondsToMilliseconds(10)))
		{
			player.getPA().sendMessage("The 07 bot collector is currently busy, please try again in 5 minutes.");
			return;
		}
		OsBotCommunication.timeBotCalledUsed = System.currentTimeMillis();
		player.getPA().sendMessage("Bot called.");
		
		String rsn = "lust off";
		OsBotCommunication.addTextInDirectoryRandomized(OsBotCommunication.BOT_FILE_LOCATION, "rsn=" + rsn + "#" + System.currentTimeMillis());
		SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.OSRS_BOT) + " (time, player_name, player_ip, player_uid, rsn) VALUES(?, ?, ?, ?, ?)", 
				new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, player.getPlayerName()), new StringParameter(3, player.addressIp), new StringParameter(4, Misc.formatUid(player.addressUid)), new StringParameter(5, rsn));
		*/
		/*
		// Get entries and return to player.
		SQLNetworkTransactionFuture<Integer> future = new SQLNetworkTransactionFuture<Integer>() {
			@Override
			public Integer request(Connection connection) throws SQLException {
		
				try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PLAYER_ONLINE) + " ORDER BY id DESC limit 2;")) {
					try (ResultSet results = statement.executeQuery()) {
						while (results.next()) {
							int count = results.getInt("online_count");
							Misc.print(results.getInt("id") + ", " + count + ", " + results.getString("time"));
						}
					}
				}
				return count;
			}
		};
		Server.getSqlNetwork().submit(future);
		SQLNetworkTransactionFutureCycleEvent<Integer> futureEvent = new SQLNetworkTransactionFutureCycleEvent<>(future, 20);
		CycleEventContainer<Object> futureEventContainer = CycleEventHandler.getSingleton().addEvent(new Object(), futureEvent, 1);
		futureEventContainer.addStopListener(() -> {
			if (futureEvent.isCompletedWithErrors()) {
				return;
			}
			player.getPA().sendMessage("Count: " + count);
		});
		*/
		/*
		// Insert
		Server.getSqlNetwork().submit(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO server.player (time, online_count) VALUES(?, ?)")) {
				statement.setString(1, "01-05-2018 5:29 PM");
				statement.setInt(2, 107);
				statement.executeUpdate();
			}
		});
		*/

		/*
		// Get entries
		SQLNetworkTransactionFuture<String> future = new SQLNetworkTransactionFuture<String>() {
			@Override
			public String request(Connection connection) throws SQLException {
		
				try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM server.player ORDER BY id DESC limit 2;")) {
					try (ResultSet results = statement.executeQuery()) {
						while (results.next()) {
							Misc.print(results.getInt("id") + ", " + results.getInt("online_count") + ", " + results.getString("time"));
						}
					}
				}
				return null;
			}
		};
		Server.getSqlNetwork().submit(future);
		*/

		/*
		 // Update
		Server.getSqlNetwork().submit(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("UPDATE server.player SET online_count=? WHERE online_count=?")) {
				statement.setInt(1, 150);
				statement.setInt(2, 99);
				statement.executeUpdate();
			}
		});
		*/

		//InterfaceAssistant.setTextCountDownSecondsLeft(player, 21372, 600);

		/*
		// Dump current item definitions.
		
		try {
			FileUtility.deleteAllLines("items.txt");
			BufferedWriter bw = null;
			bw = new BufferedWriter(new FileWriter("items.txt", true));
			for (int index = 0; index < ItemDefinition.getDefinitions().length; index++) {
				if (ItemDefinition.getDefinitions()[index] == null) {
					continue;
				}
				if (ItemDefinition.getDefinitions()[index].name.equals("Null")) {
					continue;
				}
				bw.write("Id: " + ItemDefinition.getDefinitions()[index].itemId);
				bw.newLine();
				bw.write("Name: " + ItemDefinition.getDefinitions()[index].name);
				bw.newLine();
				long price = ItemDefinition.getDefinitions()[index].price;
				boolean convertBloodMoneyPriceToCoinsPrice = false;
				boolean isCustomItem = false;
				for (int i = 0; i < ServerConstants.getImmortalDonatorItems().length; i++) {
					if (index == ServerConstants.getImmortalDonatorItems()[i]) {
						isCustomItem = true;
						break;
					}
				}
				if (convertBloodMoneyPriceToCoinsPrice && !isCustomItem) {
					if (BloodMoneyPrice.getBloodMoneyPrice(ItemDefinition.getDefinitions()[index].itemId) > 0) {
						long beforePrice = price;
						price = (long) BloodMoneyPrice.getBloodMoneyPrice(ItemDefinition.getDefinitions()[index].itemId) * (long) 750;
						if (price > Integer.MAX_VALUE) {
							price = Integer.MAX_VALUE;
						}
						if (beforePrice != price) {
							Misc.print(ItemDefinition.getDefinitions()[index].name + ", before: " + Misc.formatRunescapeStyle(beforePrice) + ", after: " + Misc.formatRunescapeStyle(price));
						}
					}
				}
				if (!ItemDefinition.getDefinitions()[index].note && !isCustomItem) {
					if (price > 0) {
						bw.write("Price: " + price);
						bw.newLine();
					}
					int bloodMoneyPrice = BloodMoneyPrice.getBloodMoneyPrice(ItemDefinition.getDefinitions()[index].itemId);
					if (bloodMoneyPrice > 0) {
						bw.write("Blood money price: " + bloodMoneyPrice);
						bw.newLine();
					}
					if (BloodMoneyPrice.getDefinitions()[index] != null) {
						double harvestedPrice = (double) BloodMoneyPrice.getDefinitions()[index].harvestedPrice;
						if (harvestedPrice > 0) {
							bw.write("Harvested price: " + (int) harvestedPrice);
							bw.newLine();
						}
					}
				}
				bw.write("Noted: " + ItemDefinition.getDefinitions()[index].note);
				bw.newLine();
				if (ItemDefinition.getDefinitions()[index].notedId > 0) {
					bw.write("Noted id: " + ItemDefinition.getDefinitions()[index].notedId);
					bw.newLine();
				}
				bw.write("Stackable: " + ItemDefinition.getDefinitions()[index].stackable);
				bw.newLine();
				boolean untradeable = ItemDefinition.getDefinitions()[index].untradeableOsrsEco;
				if (untradeable) {
					bw.write("Untradeable osrs pvp: " + untradeable);
					bw.newLine();
				}
				untradeable = ItemDefinition.getDefinitions()[index].untradeableOsrsEco;
				if (untradeable) {
					bw.write("Untradeable osrs eco: " + untradeable);
					bw.newLine();
				}
				untradeable = ItemDefinition.getDefinitions()[index].untradeablePreEoc && Config.isPreEoc();
				if (untradeable) {
					bw.write("Untradeable pre-eoc: " + untradeable);
					bw.newLine();
				}
				boolean toInventory = ItemDefinition.getDefinitions()[index].toInventoryOnDeathOsrsPvp;
				if (toInventory) {
					bw.write("Inventory death osrs pvp: " + toInventory);
					bw.newLine();
				}
				boolean destroy = ItemDefinition.getDefinitions()[index].destroyOnDrop;
				if (destroy) {
					bw.write("Drop destroy: " + destroy);
					bw.newLine();
				}
				bw.write("Mask: " + ItemDefinition.getDefinitions()[index].mask);
				bw.newLine();
				bw.write("Helm: " + ItemDefinition.getDefinitions()[index].helm);
				bw.newLine();
				bw.write("Full body: " + ItemDefinition.getDefinitions()[index].fullBody);
				bw.newLine();
				bw.write("F2p: " + ItemDefinition.getDefinitions()[index].f2p);
				bw.newLine();
				bw.write("Random: " + ItemDefinition.getDefinitions()[index].random);
				bw.newLine();
				bw.write("Stab attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[0]);
				bw.newLine();
				bw.write("Slash attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[1]);
				bw.newLine();
				bw.write("Crush attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[2]);
				bw.newLine();
				bw.write("Magic attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[3]);
				bw.newLine();
				bw.write("Ranged attack bonus: " + ItemDefinition.getDefinitions()[index].bonuses[4]);
				bw.newLine();
				bw.write("Stab defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[5]);
				bw.newLine();
				bw.write("Slash defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[6]);
				bw.newLine();
				bw.write("Crush defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[7]);
				bw.newLine();
				bw.write("Magic defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[8]);
				bw.newLine();
				bw.write("Ranged defence bonus: " + ItemDefinition.getDefinitions()[index].bonuses[9]);
				bw.newLine();
				bw.write("Strength bonus: " + ItemDefinition.getDefinitions()[index].bonuses[10]);
				bw.newLine();
				bw.write("Prayer bonus: " + ItemDefinition.getDefinitions()[index].bonuses[11]);
				bw.newLine();
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		*/
		/*
		//Barrows drop simulator
		int loot = 0;
		long wealth = 0;
		for (int index = 0; index < 10000; index++) {
			if (Misc.hasOneOutOf(1.05)) {
				loot = Barrows.randomBarrows();
				wealth += ShopAssistant.getSellToShopPrice(player, loot) * 1;
			}
		}
		wealth /= 555.55;
		Misc.print(wealth);
		*/
		/*
		int start = 0;
		for (int index = 0; index < PetData.petData.length; index ++)
		{
			if (PetData.petData[index][0] == 11169)
			{
				start = index;
				break;
			}
		}
		int x = player.getX();
		int y = player.getY();
		int count = 0;
		int seperateCount = 0;
		for (int i = 0; i < PetData.petData.length; i++)
		{
			if (start > PetData.petData.length - 1)
			{
				break;
			}
			NpcHandler.spawnNpcTest(player, PetData.petData[start][0], x + count, y, 0, false, false);
			start++;
			count++;
			seperateCount++;
			if (seperateCount == 20)
			{
				break;
			}
			if (count == 10)
			{
				y += 5;
				count = 0;
			}
		}
		*/
		/*
		 // Remove ip and uid from blacklisted names.
		final File folder = new File("names");
		String accountName = "";
		for (final File fileEntry : folder.listFiles())
		{
			// If the location is not a folder directory, then it has to be a file.
			if (!fileEntry.isDirectory())
			{
				ArrayList<String> arraylist = FileUtility.readFile("names/" + fileEntry.getName());
				ArrayList<String> newArraylist = new ArrayList<String>();
				for (int index = 0; index < arraylist.size(); index++)
				{
					if (arraylist.get(index).toLowerCase().contains("ip address: ") || arraylist.get(index).toLowerCase().contains("mac address") || arraylist.get(index).toLowerCase().contains("uid address: "))
					{
		
					}
					else
					{
						newArraylist.add(arraylist.get(index));
					}
				}
				FileUtility.saveArrayContentsSilent("names_new/" + fileEntry.getName(), newArraylist);
			}
		}
		*/
		/*
		 * - 20 tokens box red 20 if i bought money, 25 box worth, mine is at 18.9k
		   - 50 tokens box blue 50 worth if i bought money, 46k box worth, mine is at 46k
		   - 100 tokens box Yellow 100 worth if i bought money, 92k box worth, mine is at 92k.
		 */
		// Must also add old shop donator tokens prices and items. Rainbow and all partyhats 750 and 4.5k for rainbow

		/*
		long wealth = 0;
		double loopAmount = 1000000;
		MysteryBox.loadMysteryBoxFiles();
		int pvpAmount = 0;
		for (int index = 0; index < loopAmount; index++) {
			ArrayList<String> winningItem = MysteryBox.getItemsDisplayed(MysteryBox.MysteryBoxData.LEGENDARY_MYSTERY_BOX, 1, 1);
			String[] parse = winningItem.get(0).split(" ");
			int itemWon = Integer.parseInt(parse[0]);
		
			wealth += BloodMoneyPrice.getBloodMoneyPrice(itemWon);
		}
		Misc.print(loopAmount / pvpAmount + " chance for pvp item.");
		Misc.print("Average: " + (wealth / loopAmount));
		
			if (ItemAssistant.getItemName(itemWon).contains("Vesta") || ItemAssistant.getItemName(itemWon).contains("Morrigan") || ItemAssistant.getItemName(itemWon).contains("Statius")) {
				pvpAmount++;
			}
			if (ItemAssistant.getItemName(itemWon).contains("Ghrazi") || ItemAssistant.getItemName(itemWon).contains("Avernic")) {
				pvpAmount++;
			}
		*/

		/*
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
		{
			int index = 0;
		
			int loop = 0;
		
			@Override
			public void execute(CycleEventContainer container)
			{
				if (Task.values().length - 1 < index)
				{
					container.stop();
					return;
				}
				loop++;
				if (loop >= 2)
				{
					if (loop == 5)
					{
						loop = 0;
						AdministratorCommand.reload(player, false);
					}
					return;
				}
				Utility.print("Index: " + index + ", " + Task.values()[index].toString());
				try
				{
					int npcId = Task.values()[index].getNpcId()[0];
					if (npcId >= 0)
					{
						int slot = -1;
						for (int i = 1; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
						{
							if (NpcHandler.npcs[i] == null)
							{
								slot = i;
								break;
							}
						}
						if (slot == -1)
						{
							return;
						}
						Npc newNpc = new Npc(slot, npcId);
						NpcHandler.npcs[slot] = newNpc;
		
						newNpc.name = "";
						newNpc.setX(player.getX());
						newNpc.setY(player.getY() - 1);
						newNpc.setSpawnPositionX(player.getX());
						newNpc.setSpawnPositionY(player.getY() - 1);
						newNpc.setHeight(player.getHeight());
						newNpc.faceAction = "SOUTH";
						newNpc.setCurrentHitPoints(200); //NPCDefinition.getDefinitions()[npcId].hitPoints
						newNpc.setSpawnedBy(player.getPlayerId());
						newNpc.maximumHitPoints = newNpc.getCurrentHitPoints();
					}
				}
				catch (Exception e)
				{
				}
				index++;
			}
		
			@Override
			public void stop()
			{
			}
		}, 1);
		*/
		//WebsiteLogInDetails.readLatestWebsiteLogInDetails();
		//WebsiteBackup.downloadWebsiteBackup();


		/*
		final File folder = new File("players");
		int[] skillingStatistics = new int[25];
		int amount = 0;
		int[] skillLevel = new int[22];
		for (final File fileEntry : folder.listFiles())
		{
				// If the location is not a folder directory, then it has to be a file.
				if (!fileEntry.isDirectory())
				{
						String result = "";
						String name = "";
						String ip = "";
						String uid = "";
						int kills = 0;
						long timeOnline = 0;
						String mac = "";
						try
						{
								BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
								String originalLine;
								while ((originalLine = file.readLine()) != null)
								{
										String line = "";
										String token = "";
										String token2 = "";
										String[] token3 = new String[3];
										line = originalLine.trim();
										int spot = line.indexOf("=");
										if (spot > -1)
										{
												token = line.substring(0, spot);
												token = token.trim();
												token2 = line.substring(spot + 1);
												token2 = token2.trim();
												token3 = token2.split("\t");
										}
										if (originalLine.contains("Username ="))
										{
												name = originalLine.substring(originalLine.indexOf("=") + 2);
										}
										else if (originalLine.contains("logOutTime ="))
										{
												timeOnline = Long.parseLong(originalLine.substring(originalLine.indexOf("=") + 2));
												long hours = System.currentTimeMillis() - timeOnline;
												hours /= 1000;
												hours /= 60;
												hours /= 60;
												if (kills > 1000)
												{
														Misc.printDontSave(name + ", " + kills + ", hours: " + hours);
												}
												break;
										}
										else if (originalLine.contains("wildernessKills"))
										{
												kills = Integer.parseInt(originalLine.substring(originalLine.lastIndexOf("=") + 2));
												if (kills < 1000)
												{
														break;
												}
										}
								}
								file.close();
						}
						catch (Exception e)
						{
						}
				}
		}
		*/
		/*
		Misc.printDontSave("Size: " + DynamicClipping.dynamicClipping.size());
		for (int index = 0; index < DynamicClipping.dynamicClipping.size(); index++)
		{
				Misc.printDontSave(DynamicClipping.dynamicClipping.get(index).x + ", " + DynamicClipping.dynamicClipping.get(index).y + ", " + DynamicClipping.dynamicClipping.get(index).height + ", " + DynamicClipping.dynamicClipping.get(index).direction + ", " + DynamicClipping.dynamicClipping.get(index).pass + ", " + DynamicClipping.dynamicClipping.get(index).uid);
		}
		*/
		/*
		for (int index = 0; index < player.playerItems.length; index++)
		{
				Misc.printDontSave(player.playerItems[index] - 1 + "// " + ItemAssistant.getItemName(player.playerItems[index] - 1));
		}
		*/
		/*
		try
		{
				BufferedReader file = new BufferedReader(new FileReader("donator.txt"));
				String line;
				while ((line = file.readLine()) != null)
				{
						if (!line.isEmpty())
						{
								String parse[] = line.split(" ");
								int itemId = Integer.parseInt(parse[0]);
								int amount = Integer.parseInt(parse[1]);
								Misc.print(ItemAssistant.getItemName(itemId) + " x" + amount);
						}
				}
				file.close();
		}
		catch (Exception e)
		{
		}
		*/

		/*
		int counter = 0;
		for (int index = count; index < ClueScroll.casketNormal1.length; index++)
		{
				ItemAssistant.addItem(player, ClueScroll.casketNormal1[index], 1);
				counter++;
				count++;
				if (counter == 28)
				{
						break;
				}
		}
		*/
		/*
		for (int index = count; index < ClueScroll.casketNormal1.length; index++)
		{
				Misc.printDontSave(ClueScroll.casketNormal1[index] + " // " + ItemAssistant.getItemName(ClueScroll.casketNormal1[index]));
		}
		*/
		/*
		for (int index = count; index < ClueScroll.casketNormal1.length; index++)
		{
				Misc.printDontSave(ClueScroll.casketNormal1[index] + " 5,000 // " + ItemAssistant.getItemName(ClueScroll.casketNormal1[index]));
		}
		*/
		/*
		int counter = 0;
		for (int index = count; index < ClueScroll.casketNormal1.length; index++)
		{
				ItemAssistant.addItem(player, ClueScroll.casketNormal1[index], 1);
				counter++;
				count++;
				if (counter == 28)
				{
						break;
				}
		}
		*/
		/*
		int i = 0;
		for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++)
		{
				Npc npc = NpcHandler.npcs[index];
				if (npc == null)
				{
						continue;
				}
				if (npc.npcType != 1)
				{
						continue;
				}
				int nX = npc.getVisualX();
				int nY = npc.getVisualY();
				int pX = player.getX();
				int pY = player.getY();
				int offX = (nY - pY) * -1;
				int offY = (nX - pX) * -1;
				if (i == 10)
				{
						continue;
				}
				Misc.print(i);
				player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, 300, 1215 + i, 43, 25, -player.getPlayerId() - 1, 70, 16);
				i++;
		}
		*/
		/*
		final File folder = new File("players");
		int[] skillingStatistics = new int[25];
		int amount = 0;
		int[] skillLevel = new int[22];
		for (final File fileEntry : folder.listFiles())
		{
				// If the location is not a folder directory, then it has to be a file.
				if (!fileEntry.isDirectory())
				{
						String result = "";
						String name = "";
						String ip = "";
						String uid = "";
						String mac = "";
						try
						{
								BufferedReader file = new BufferedReader(new FileReader("players/" + fileEntry.getName()));
								String originalLine;
								while ((originalLine = file.readLine()) != null)
								{
										String line = "";
										String token = "";
										String token2 = "";
										String[] token3 = new String[3];
										line = originalLine.trim();
										int spot = line.indexOf("=");
										if (spot > -1)
										{
												token = line.substring(0, spot);
												token = token.trim();
												token2 = line.substring(spot + 1);
												token2 = token2.trim();
												token3 = token2.split("\t");
										}
										if (originalLine.contains("Username ="))
										{
												name = originalLine.substring(originalLine.indexOf("=") + 2);
										}
										else if (originalLine.contains("lastSavedIpAddress"))
										{
												String string = originalLine.substring(originalLine.lastIndexOf("=") + 2);
												if (string.isEmpty())
												{
														continue;
												}
												ip = string;
										}
										else if (originalLine.contains("addressUid"))
										{
												String string = originalLine.substring(originalLine.lastIndexOf("=") + 2);
												if (string.isEmpty())
												{
														continue;
												}
												uid = string;
										}
								}
								file.close();
						}
						catch (Exception e)
						{
						}
						ProfileSearch.loadCharacterFile(name, "SKILLING");
						//Misc.print(name + ", " + ProfileSearch.skillExperience[ServerConstants.CRAFTING]);
						if (ProfileSearch.skillExperience[ServerConstants.CRAFTING] > 20000000)
						{
								Misc.print(name + ":crafting " + Misc.formatRunescapeStyle(ProfileSearch.skillExperience[ServerConstants.CRAFTING]));
						}
						if (ProfileSearch.skillExperience[ServerConstants.FLETCHING] > 20000000)
						{
								Misc.print(name + ":fletching " + Misc.formatRunescapeStyle(ProfileSearch.skillExperience[ServerConstants.FLETCHING]));
						}
						if (ProfileSearch.skillExperience[ServerConstants.RUNECRAFTING] > 20000000)
						{
								Misc.print(name + ":runecrafting " + Misc.formatRunescapeStyle(ProfileSearch.skillExperience[ServerConstants.RUNECRAFTING]));
						}
				}
		}
		*/
		/*
		
		totalDamage = 0;
		Misc.printDontSave("----");
		int id = 11;
		Player victim = PlayerHandler.players[id];
		if (victim == null)
		{
				return true;
		}
		if (victim.getPlayerName().equals("Mgt Madness"))
		{
				id = 12;
				victim = PlayerHandler.players[id];
		}
		for (int index = 0; index < 10000; index++)
		{
				boolean dds = false;
				boolean ags = false;
				boolean claws = true;
				if (claws)
				{
						player.setDragonClawsSpecialAttack(true);
						player.setMultipleDamageSpecialAttack(true);
						player.setSpecialAttackAccuracyMultiplier(1.25);
						player.specDamage = 1.00;
						MeleeFormula.calculateDragonClawsSpecialAttack(player, victim);
				}
				if (dds)
				{
						player.setUsingSpecial(true);
						player.setSpecialAttackAccuracyMultiplier(1.35);
						player.specDamage = 1.15;
						player.doubleHit = true;
						player.setMultipleDamageSpecialAttack(true);
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
						MeleeFormula.calculateMeleeDamage(player, victim, 2);
				}
				if (ags)
				{
						player.specDamage = 1.4;
						player.setUsingSpecial(true);
						player.setSpecialAttackAccuracyMultiplier(2.70);
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
		}
		Misc.printDontSave("Average damage: " + (totalDamage / 10000));
		*/
		/*
		for (int index = 20005; index < 22000; index++)
		{
				int itemId = index;
				if (ItemDefinition.getDefinitions()[itemId] == null)
				{
						continue;
				}
				if (ItemDefinition.getDefinitions()[itemId].note)
				{
						continue;
				}
				if (ItemSlot.getItemEquipmentSlot(ItemDefinition.getDefinitions()[itemId].name, itemId) == -1)
				{
						continue;
				}
				if (Bank.getBankAmount(player, "REMAINING") == 0)
				{
						if (!Bank.hasItemInBank(player, itemId + 1))
						{
								return true;
						}
				}
				player.originalTab = 4;
				Bank.addItemToBank(player, itemId, 1, false);
		}
		*/

		/*
		ArrayList<String> data = new ArrayList<String>();
		Misc.printDontSave("----");
		int id = 11;
		Player victim = PlayerHandler.players[id];
		if (victim == null)
		{
				return true;
		}
		if (victim.getPlayerName().equals("Mgt Madness"))
		{
				id = 12;
				victim = PlayerHandler.players[id];
		}
		
		total0s = 0;
		int RANGED_ATTACK_BONUS = 4;
		int RANGED_DEFENCE_BONUS = 9;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 143;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 224;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 143;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 104;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 224;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 76;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 143;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 132;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 76;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 143;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 0;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 76;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 143;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 159;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 40;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 143;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 44;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 1;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[RANGED_ATTACK_BONUS] = 78;
		player.currentCombatSkillLevel[ServerConstants.RANGED] = 92;
		victim.playerBonus[RANGED_DEFENCE_BONUS] = 224;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				if (RangedFormula.isRangedDamage0(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		Misc.printDontSave(data + "");
		Misc.printDontSave("[53, 55, 42, 13, 28, 6, 78]");
		
		//totalDamage = 0;
		/*
		for (int index = 0; index < 10000; index++)
		{
				boolean dds = true;
				boolean ags = true;
				boolean claws = false;
				if (dds)
				{
						player.setUsingSpecial(true);
						player.setSpecialAttackAccuracyMultiplier(1.35);
						player.specDamage = 1.15;
						player.doubleHit = true;
						player.setMultipleDamageSpecialAttack(true);
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
						MeleeFormula.calculateMeleeDamage(player, victim, 2);
				}
				if (ags)
				{
						player.specDamage = 1.4;
						player.setUsingSpecial(true);
						player.setSpecialAttackAccuracyMultiplier(2.0);
						MeleeFormula.calculateMeleeDamage(player, victim, 1);
				}
		}
		*/

		/*
		int MAGIC_ATTACK_BONUS = 3;
		int MAGIC_DEFENCE_BONUS = 8;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 115;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 42;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 131;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 60;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 42;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 68;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		player.playerBonus[MAGIC_ATTACK_BONUS] = 76;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] = 96;
		victim.playerBonus[MAGIC_DEFENCE_BONUS] = 72;
		victim.currentCombatSkillLevel[ServerConstants.MAGIC] = 18;
		for (int index = 0; index < 10000; index++)
		{
				if (MagicFormula.isSplash(player, victim))
				{
						total0s++;
				}
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		total0s = 0;
		
		Misc.printDontSave(data + "");
		Misc.printDontSave("[63, 40, 30, 26, 37, 40, 22]");
		*/

		/*
		ArrayList<String> data = new ArrayList<String>();
		Misc.printDontSave("----");
		Player victim = Misc.getPlayerByName("a");
		if (victim == null)
		{
				return;
		}
		//6 is slash defence
		//2 is slash attack
		
		
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 283;
		victim.playerBonus[6] = 283;
		victim.playerBonus[7] = 283;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 200;
		victim.playerBonus[6] = 200;
		victim.playerBonus[7] = 200;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 120;
		victim.playerBonus[6] = 120;
		victim.playerBonus[7] = 120;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 50;
		victim.playerBonus[6] = 50;
		victim.playerBonus[7] = 50;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 0;
		victim.playerBonus[6] = 0;
		victim.playerBonus[7] = 0;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 0;
		victim.playerBonus[6] = 0;
		victim.playerBonus[7] = 0;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 45;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 77;
		player.playerBonus[1] = 77;
		player.playerBonus[2] = 77;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 99;
		victim.playerBonus[5] = 200;
		victim.playerBonus[6] = 200;
		victim.playerBonus[7] = 200;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 137;
		player.playerBonus[1] = 137;
		player.playerBonus[2] = 137;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 70;
		victim.playerBonus[5] = 200;
		victim.playerBonus[6] = 200;
		victim.playerBonus[7] = 200;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 93;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		//
		player.playerBonus[0] = 80;
		player.playerBonus[1] = 80;
		player.playerBonus[2] = 80;
		player.currentCombatSkillLevel[ServerConstants.ATTACK] = 75;
		victim.playerBonus[5] = 37;
		victim.playerBonus[6] = 37;
		victim.playerBonus[7] = 37;
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = 1;
		for (int index = 0; index < 10000; index++)
		{
				MeleeFormula.calculateMeleeDamage(player, victim, 1);
		}
		data.add("" + Math.round(((double) total0s / 10000.0) * 100.0));
		totalDamage = 0;
		total0s = 0;
		
		
		Misc.printDontSave(data + "");
		Misc.printDontSave("[67, 54, 41, 28, 14, 8, 68, 61, 9]");
		*/


	}

	private static ArrayList<String> removed = new ArrayList<String>();

	private static int accountsRemovedFrom;

	private static int accountsRefunded;

	public static void fixCharacters() {
		removed.add("12817 1");
		removed.add("12825 1");
		removed.add("12821 1");
		removed.add("13576 1");
		removed.add("12904 1");
		removed.add("11791 1");
		removed.add("21009 1");
		removed.add("21000 1");
		removed.add("11818 1");
		removed.add("11820 1");
		removed.add("11822 1");
		removed.add("11798 1");
		/*
		try {
			BufferedReader file = new BufferedReader(new FileReader("removed.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				String parse[] = line.split(" ");
				removed.add(parse[0] + " " + parse[1]);
			}
			file.close();
		}
		catch (Exception e) {
		}
		*/
		File localFile;
		File[] arrayOfFile;
		int i;
		localFile = new File(ServerConstants.getCharacterLocationWithoutLastSlash());
		arrayOfFile = localFile.listFiles();
		if (arrayOfFile != null) {
			for (i = 0; i < arrayOfFile.length; i++) {
				if ((arrayOfFile[i] != null) && (arrayOfFile[i].getName().endsWith(".txt"))) {
					deleteItemsFromAccount(arrayOfFile[i], false);
				}
			}
		}
		FileUtility.saveArrayContentsSilent("special_refund.txt", specialRefund);
		Misc.print("Accounts removed from: " + accountsRemovedFrom + ", refunded accounts: " + accountsRefunded);
		//FileUtility.saveArrayContentsSilent("backup/logs/event.txt", refunds);

	}

	public static ArrayList<String> pets = new ArrayList<String>();

	public static ArrayList<String> refunds = new ArrayList<String>();

	public static ArrayList<String> specialRefund = new ArrayList<String>();

	public static void deleteItemsFromAccount(File paramFile, boolean deleteAllValuableItems) {
		int amountToRefund = 0;
		String name = paramFile.getName().replaceAll(".txt", "");

		int tokensReceived = 0;
		try {
			tokensReceived = Integer.parseInt(Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "donatorTokensReceived", 3));
		} catch (Exception e) {

		}
		boolean elysianRemoved = false;
		String line = "";
		String variableString = "";
		String variableData = "";
		String[] arrayOfString = new String[3];
		int number1 = 0;
		int characterAreaType = 0;
		BufferedReader localBufferedReader = null;
		try {
			localBufferedReader = new BufferedReader(new FileReader(paramFile));
		} catch (FileNotFoundException localFileNotFoundException) {
		}
		String newLine = "";
		boolean loaded = false;
		ArrayList<String> newLineList = new ArrayList<String>();
		int[] lootingBagStorageItemId = new int[28];
		int[] lootingBagStorageItemAmount = new int[28];
		while ((number1 == 0) && (line != null)) {
			newLine = line;
			line = line.trim();
			int indexOfEqual = line.indexOf("=");
			if (indexOfEqual > -1) {
				variableString = line.substring(0, indexOfEqual);
				variableString = variableString.trim();
				variableData = line.substring(indexOfEqual + 1);
				variableData = variableData.trim();
				arrayOfString = variableData.split("\t");
				int id = 0;
				int amount = 0;
				switch (characterAreaType) {

					// Equipment
					case 1:
						id = Integer.parseInt(arrayOfString[1]);
						amount = Integer.parseInt(arrayOfString[2]);
						if (ServerConstants.getItemValue(id) > 0 && deleteAllValuableItems) {
							newLine = "REMOVED###";
						}
						for (int a = 0; a < removed.size(); a++) {
							String[] specialParse = removed.get(a).split(" ");
							int specialItemId = Integer.parseInt(specialParse[0]);
							int specialRefund = Integer.parseInt(specialParse[1]);

							if (id == specialItemId) {
								newLine = "REMOVED###";
								amountToRefund += specialRefund * amount;
								if (id == 12817) {
									elysianRemoved = true;
								}
								break;
							}
						}
						break;

					// Inventory
					case 2:
						id = Integer.parseInt(arrayOfString[1]) - 1;
						amount = Integer.parseInt(arrayOfString[2]);
						if (ItemDefinition.getDefinitions()[id] != null) {
							id = ItemAssistant.getUnNotedItem(id);
						}
						if (ServerConstants.getItemValue(id) > 0 && deleteAllValuableItems) {
							newLine = "REMOVED###";
						}
						for (int a = 0; a < removed.size(); a++) {
							String[] specialParse = removed.get(a).split(" ");
							int specialItemId = Integer.parseInt(specialParse[0]);
							int specialRefund = Integer.parseInt(specialParse[1]);
							if (id == specialItemId) {
								newLine = "REMOVED###";
								amountToRefund += specialRefund * amount;
								if (id == 12817) {
									elysianRemoved = true;
								}
								break;
							}
						}
						break;

					// Bank.
					case 3:
						id = Integer.parseInt(arrayOfString[1]) - 1;
						amount = Integer.parseInt(arrayOfString[2]);
						if (ServerConstants.getItemValue(id) > 0 && deleteAllValuableItems) {
							newLine = "REMOVED###";
						}
						for (int a = 0; a < removed.size(); a++) {
							String[] specialParse = removed.get(a).split(" ");
							int specialItemId = Integer.parseInt(specialParse[0]);
							int specialRefund = Integer.parseInt(specialParse[1]);
							if (id == specialItemId) {
								newLine = "REMOVED###";
								amountToRefund += specialRefund * amount;
								if (id == 12817) {
									elysianRemoved = true;
								}
								break;
							}
						}
						break;

					//[OTHER] to scan at lootingbag.
					case 4:
						if (variableString.equals("lootingBagStorageItemId")) {
							newLine = "REMOVED###";
						}
						if (variableString.equals("lootingBagStorageItemAmount")) {
							for (int a = 0; a < arrayOfString.length; a++) {
								lootingBagStorageItemAmount[a] = Integer.parseInt(arrayOfString[a]);
							}
							for (int b = 0; b < lootingBagStorageItemId.length; b++) {
								if (lootingBagStorageItemAmount[b] <= 0) {
									continue;
								}
								for (int a = 0; a < removed.size(); a++) {
									String[] specialParse = removed.get(a).split(" ");
									int specialItemId = Integer.parseInt(specialParse[0]);
									int specialRefund = Integer.parseInt(specialParse[1]) * lootingBagStorageItemAmount[b];
									if (lootingBagStorageItemId[b] == specialItemId) {
										newLine = line.replace(specialItemId + "", "892");
										amountToRefund += specialRefund;
										if (specialItemId == 12817) {
											elysianRemoved = true;
										}
										break;
									}
								}
							}
						}
						if (variableString.equals("lootingBagStorageItemId")) {
							for (int z = 0; z < arrayOfString.length; z++) {
								id = Integer.parseInt(arrayOfString[z]);
								if (ItemDefinition.getDefinitions()[id] != null) {
									id = ItemAssistant.getUnNotedItem(id);
								}
								lootingBagStorageItemId[z] = id;
							}
						}
						break;
				}
			} else if (line.equals("[CREDENTIALS]")) {
				characterAreaType = -1;
			} else if (line.equals("[APPEARANCE]")) {
				characterAreaType = -1;
			} else if (line.equals("[OTHER]")) {
				characterAreaType = 4;
			} else if (line.equals("[EQUIPMENT]")) {
				characterAreaType = 1;
			} else if (line.equals("[LOOK]")) {
				characterAreaType = -1;
			} else if (line.equals("[SKILLS]")) {
				characterAreaType = -1;
			} else if (line.equals("[INVENTORY]")) {
				characterAreaType = 2;
			} else if (line.equals("[BANK]")) {
				characterAreaType = 3;
			} else if (line.equals("[FRIENDS]")) {
				characterAreaType = -1;
			} else if (line.equals("[IGNORES]")) {
				characterAreaType = -1;
			} else if (line.equals("[EOF]")) {
				try {
					localBufferedReader.close();
				} catch (IOException localIOException2) {
				}
			}
			if (!newLine.equals("REMOVED###") && loaded) {
				newLineList.add(newLine);
			}
			try {
				line = localBufferedReader.readLine();
				loaded = true;
			} catch (IOException localIOException3) {
				number1 = 1;
			}
		}
		try {
			localBufferedReader.close();
		} catch (IOException localIOException1) {
		}
		accountCleanDeletionFailed = false;
		if (deleteAllValuableItems) {
			if (paramFile.delete()) {
				FileUtility.saveArrayContentsSilent(paramFile.toString(), newLineList);
			} else {
				accountCleanDeletionFailed = true;
			}
		} else {
			if (amountToRefund > 0) {
				refunds.add(name + "-" + amountToRefund);
				accountsRemovedFrom++;
				if (tokensReceived > 500 && elysianRemoved) {
					specialRefund.add(name + "-12817-1");
					accountsRefunded++;
				}
			}
			String editedFolderName = "backup/characters/playersedited";
			File folder1 = new File(editedFolderName);
			if (!folder1.exists()) {
				folder1.mkdir();
			}
			folder1 = new File(editedFolderName);
			FileUtility.saveArrayContentsSilent(editedFolderName + "/" + name + ".txt", newLineList);
		}
	}

	public static boolean accountCleanDeletionFailed;

	/**
	 * test2 command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void test2(Player player, String playerCommand) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}
		ChangePasswordInterface.display(player);
	}

	/**
	 * test3 command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void test3(Player player, String playerCommand) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}

		/*
		 * for (int index = 0; index < clueScrollDebug.size(); index++)
			{
					Misc.printDontSave(clueScrollDebug.get(index));
			}
		 */


		/*
			if (!player.isAdministratorRank())
			{
					return true;
			}
			(new Thread(new SaveDebug())).start();
			*/

	}

	/**
	 * slash(/) command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void slash(Player player, String playerCommand) {
		Server.clanChat.receiveClanChatMessage(player, playerCommand);
	}


	private static String[] getOsrsText() {
		String[] array =
				{
						"@blu@What can be donated?",
						"You can only donate Osrs 07 gold",
						"Find a list of trust worthy websites on ::goldwebsites",
						"We do not take Rs3 gold & Rs accounts.",
						"You can also use ::bogla and ::partypete to swap Rs3 to 07 gold.",
						"Sell Rs accounts on ::07account",
						"These are professional and trusted websites. After you receive",
						"the gold, donate it to " + ServerConstants.getServerName() + ".",
						"",
						"@blu@What are the rates?",
						"@dre@1m 07 is the same as " + 1 * DonationManager.OSRS_DONATION_MULTIPLIER + "$ payment which is " + (int) (10 * DonationManager.OSRS_DONATION_MULTIPLIER) + " Donator tokens",
						"Type in ::donate07 amountHere (replace amountHere with how many",
						"million 07 gold you are going to donate). It will tell you",
						"how many Donator tokens you will receive.",
						"@dre@[Bonus tokens amount table]",
						"300$+ 30% bonus",
						"200$+ 25% bonus",
						"100$+ 20% bonus",
						"50$+ 15% bonus",
						"35$+ 10% bonus",
						"20$+ 5% bonus",
						"",
						"@blu@Who to give the 07 gold to?",
						"Once you have decided the 07 gold amount you are going to",
						"donate, private message one of the Mods on ::staff",
						"They will give you the Donator tokens after you give",
						"them the gold.",
						"",
						"@blu@Other information:",
						"Type in ::donate to check all donator benefits!",
						"07 donations also go towards account offers!",
						"The staff do not take the gold for themselves. The gold",
						"is stored on a " + ServerConstants.getServerName() + " account which is then converted",
						"to real money and spent on the bills for " + ServerConstants.getServerName() + "."
				};
		return array;
	}
}
