package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.clanchat.ClanChatHandler;
import game.content.interfaces.InterfaceAssistant;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.movement.Follow;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import tools.EconomyScan;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.FileUtility;
import utility.Misc;

/**
 * Anti-dice scamming system.
 *
 * @author MGT Madness, created on 22-06-2017.
 */
public class DiceSystem {
	public static final String LOCATION = "backup/logs/dice/vault.txt";

	/**
	 * Store name-amount of bloodmoney deposited.
	 */
	public static ArrayList<String> depositVault = new ArrayList<String>();


	public static ArrayList<String> diceZoneChatLog = new ArrayList<String>();

	public static ArrayList<Follow> vaultCoorinates = new ArrayList<>();

	public static void populateVaultCoordinates() {
		vaultCoorinates.clear();
		vaultCoorinates.add(new Follow(1692, 4255, 0));
		vaultCoorinates.add(new Follow(1693, 4255, 0));
		vaultCoorinates.add(new Follow(1692, 4260, 0));
		vaultCoorinates.add(new Follow(1693, 4260, 0));
		vaultCoorinates.add(new Follow(1695, 4257, 0));
		vaultCoorinates.add(new Follow(1695, 4258, 0));
		vaultCoorinates.add(new Follow(1690, 4257, 0));
		vaultCoorinates.add(new Follow(1690, 4258, 0));
	}

	public static void showDepositInterface(Player player) {
		InterfaceAssistant.showAmountInterface(player, "DEPOSIT INTO VAULT", "Enter amount");
	}

	public static void showWithdrawInterface(Player player) {
		if (!canWithdraw(player)) {
			return;
		}
		InterfaceAssistant.showAmountInterface(player, "WITHDRAW FROM VAULT", "Enter amount");
	}

	public static void depositIntoVaultAction(Player player, int amount) {
		int bloodMoneyAmount = ItemAssistant.getItemAmount(player, 13307);
		if (amount > bloodMoneyAmount) {
			amount = bloodMoneyAmount;
		}
		if (amount == 0) {
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, 13307, amount);
		player.getDH().sendStatement("You have deposited x" + Misc.formatNumber(amount) + " " + ServerConstants.getMainCurrencyName() + " into your deposit vault.");
		boolean match = false;
		for (int index = 0; index < depositVault.size(); index++) {
			String parse[] = depositVault.get(index).split("-");
			if (parse[0].equalsIgnoreCase(player.getPlayerName())) {
				int amountLeft = Integer.parseInt(parse[1]);
				depositVault.remove(index);
				depositVault.add(player.getPlayerName() + "-" + (amountLeft + amount));
				match = true;
				break;
			}
		}
		if (!match) {
			depositVault.add(player.getPlayerName() + "-" + amount);
		}
		saveFile();
	}

	public static void withdrawFromVaultAction(Player player, int amount) {
		if (!canWithdraw(player)) {
			return;
		}
		int vaultAmount = getPlayerVaultAmount(player.getPlayerName());
		if (amount > vaultAmount) {
			amount = vaultAmount;
		}
		if (amount == 0) {
			return;
		}
		player.getDH().sendStatement("You have withdrawn x" + Misc.formatNumber(amount) + " " + ServerConstants.getMainCurrencyName() + " from your deposit vault.");
		ItemAssistant.addItemToInventoryOrDrop(player, 13307, amount);
		for (int index = 0; index < depositVault.size(); index++) {
			String parse[] = depositVault.get(index).split("-");
			if (parse[0].equalsIgnoreCase(player.getPlayerName())) {
				int amountLeft = Integer.parseInt(parse[1]) - amount;
				depositVault.remove(index);
				if (amountLeft == 0) {
				} else {
					depositVault.add(player.getPlayerName() + "-" + amountLeft);
				}
				break;
			}
		}
		saveFile();
	}

	public static boolean canWithdraw(Player player) {
		if (!Bank.hasBankingRequirements(player, true)) {
			player.getPA().closeInterfaces(true);
			return false;
		}
		int hours = 48;
		if (System.currentTimeMillis() - player.timeAcceptedTradeInDiceZone < (ServerConstants.MILLISECONDS_HOUR * hours)) {
			long waitTime = (ServerConstants.MILLISECONDS_HOUR * hours) - (System.currentTimeMillis() - player.timeAcceptedTradeInDiceZone);
			long original = waitTime;
			waitTime /= ServerConstants.MILLISECONDS_HOUR;

			if (waitTime == 0) {
				original /= ServerConstants.MILLISECONDS_MINUTE;
				player.getPA().sendMessage("Wait " + original + " minute" + Misc.getPluralS(original) + " to do this, do not accept any trade at dice");
				player.getPA().sendMessage("to avoid renewing the timer, staff are unable to remove this for you.");
				return false;
			}
			player.getPA().sendMessage("Wait " + waitTime + " hour" + Misc.getPluralS(waitTime) + " to do this, do not accept any trade at dice");
			player.getPA().sendMessage("to avoid renewing the timer, staff are unable to remove this for you.");
			return false;
		}
		return true;
	}

	public static int getPlayerVaultAmount(String name) {
		for (int index = 0; index < depositVault.size(); index++) {
			String parse[] = depositVault.get(index).split("-");
			if (parse[0].equalsIgnoreCase(name)) {
				return Integer.parseInt(parse[1]);
			}
		}
		return 0;
	}

	public static void checkVault(Player player) {
		player.getDH().sendItemChat("Deposit vault balance", "",
		                            "You currently have x" + Misc.formatNumber(getPlayerVaultAmount(player.getPlayerName())) + " " + ServerConstants.getMainCurrencyName()
		                            + " in your", "deposit vault.", "", GameType.isOsrsPvp() ? 13316 : 1004, 200, 20, 0);
	}

	public static void readDepositVaultFile() {
		populateVaultCoordinates();
		depositVault.clear();
		try {
			BufferedReader file = new BufferedReader(new FileReader(EconomyScan.diceVaultLocation.isEmpty() ? LOCATION : EconomyScan.diceVaultLocation));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					depositVault.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void transferVault(Player player, String command) {
		try {
			command = command.substring(14);
			String parseCommand[] = command.split("-");
			if (parseCommand[0].equalsIgnoreCase(parseCommand[1])) {
				player.getPA().sendMessage("Unlocking the vault of a player for the player to use it is not allowed!");
				return;
			}
			for (int index = 0; index < depositVault.size(); index++) {
				String parse[] = depositVault.get(index).split("-");
				if (parse[0].equalsIgnoreCase(parseCommand[0])) {
					int amount = Integer.parseInt(parse[1]);
					depositVault.remove(index);
					ItemAssistant.addItemReward(null, parseCommand[1], ServerConstants.getMainCurrencyId(), amount, false, -1);
					FileUtility.addLineOnTxt("backup/logs/dice/vault_transfer.txt",
					                         Misc.getDateAndTime() + " " + player.getPlayerName() + ": " + Misc.formatRunescapeStyle(amount) + " removed from " + parse[0]
					                         + " and transferred to " + parseCommand[1] + ".");

					DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL,
					                           player.getPlayerName() + " has transferred vault " + Misc.formatRunescapeStyle(amount) + " of '" + parse[0] + "' to '"
					                           + parseCommand[1] + "'");
					player.getPA().sendMessage(Misc.formatRunescapeStyle(amount) + " removed from " + parse[0] + " and transferred to " + parseCommand[1] + ".");
					return;
				}
			}
			player.getPA().sendMessage("No match found for: " + parseCommand[0]);
		} catch (Exception e) {
			player.getPA().sendMessage("If Mgt Madness scammed Ronald, use as ::transfervault mgt madness-ronald");
		}

		saveFile();

	}

	public static void saveFile() {
		FileUtility.deleteAllLines(DiceSystem.LOCATION);
		FileUtility.saveArrayContentsSilent(DiceSystem.LOCATION, DiceSystem.depositVault);

	}

	public static void saveDiceLogs() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		FileUtility.saveArrayContents("backup/logs/dice/history/full_" + dateFormat.format(cal.getTime()) + ".txt", DiceSystem.diceZoneChatLog);
		DiceSystem.diceZoneChatLog.clear();

		FileUtility.saveArrayContents("backup/logs/dice/history/tldr_" + dateFormat.format(cal.getTime()) + ".txt", ClanChatHandler.diceLog);
		ClanChatHandler.diceLog.clear();

	}

	public static boolean preventTradeScam(Player player, Player other, int itemId, int amount) {
		long myTradedWealth = 0;
		for (int index = 0; index < player.getTradeAndDuel().offeredItems.size(); index++) {
			myTradedWealth += ServerConstants.getItemValue(player.getTradeAndDuel().offeredItems.get(index).getId()) * player.getTradeAndDuel().offeredItems.get(index).getAmount();
		}

		if (ClanChatHandler.inDiceCc(player, false, false) && ClanChatHandler.inDiceCc(other, false, false) && Area.inDiceZone(player) && Area.inDiceZone(other)) {

			// Dice bag
			if (!ItemAssistant.hasItemInInventory(player, 16004)) {
				int otherPlayerVault = DiceSystem.getPlayerVaultAmount(other.getPlayerName());
				double number = 1.0;
				long tradeTotalAfterAddition = myTradedWealth + (ServerConstants.getItemValue(itemId) * amount);
				long amountLeft = (long) ((otherPlayerVault * number) - myTradedWealth);
				if (tradeTotalAfterAddition > (otherPlayerVault * number)) {
					player.getPA().sendMessage(ServerConstants.RED_COL + "You can only give to " + other.getPlayerName() + " 100% of what his vault his worth.");
					player.getPA().sendMessage("We are preventing you from getting scammed. The vault system is a guarantee.");
					player.getPA().sendMessage(
							"You can add another " + Misc.formatNumber(amountLeft) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ". " + other.getPlayerName()
							+ " has a " + Misc.formatRunescapeStyle(otherPlayerVault) + " vault.");
					return true;
				}
			}
		}
		return false;
	}

	public static void serverSave() {
		DiceSystem.saveDiceLogs();
		DiceSystem.saveFile();
	}
}
