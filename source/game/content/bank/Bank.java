package game.content.bank;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RunePouch;
import game.content.starter.GameMode;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.PermanentAttributeKey;
import game.entity.attributes.PermanentAttributeKeyComponent;
import game.item.BloodMoneyPrice;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.Arrays;
import utility.Misc;

public class Bank {

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Boolean> ALWAYS_PLACEHOLDER = new PermanentAttributeKey<>(false, "permanent-placeholder");

	public static void setAlwaysPlaceholder(Player player, boolean alwaysBank) {
		player.getAttributes().put(ALWAYS_PLACEHOLDER, alwaysBank);
	}

	private static void setBankScrollLengthNormal(Player player, boolean search, boolean mainTab) {
		int itemIndex = 0;

		double row = 0.0;

		if (search) {
			row = Misc.getDoubleRoundedUp((double) player.bankSearchedItems.size() / 10.0);
		} else if (!mainTab) {
			for (int index = 0; index < player.bankingItems.length; index++) {
				itemIndex = index + 1;
				int item = player.bankingItems[index];
				int amount = player.bankingItemsN[index];
				if (item > 0 && amount >= 0) {
					row = Misc.getDoubleRoundedUp((double) itemIndex / 10.0);
				}
			}
		}
		if (mainTab) {
			row += bankRowsUsed(player.bankItems, player.bankItemsN, true);
			row += bankRowsUsed(player.bankItems1, player.bankItems1N, false);
			row += bankRowsUsed(player.bankItems2, player.bankItems2N, false);
			row += bankRowsUsed(player.bankItems3, player.bankItems3N, false);
			row += bankRowsUsed(player.bankItems4, player.bankItems4N, false);
			row += bankRowsUsed(player.bankItems5, player.bankItems5N, false);
			row += bankRowsUsed(player.bankItems6, player.bankItems6N, false);
			row += bankRowsUsed(player.bankItems7, player.bankItems7N, false);
			row += bankRowsUsed(player.bankItems8, player.bankItems8N, false);
			/*
			 */
			int length = (int) row;
			if (length < 228) {
				length = 228;
			}
			InterfaceAssistant.setFixedScrollMax(player, 5385, length);
		} else {
			InterfaceAssistant.setFixedScrollMax(player, 5385, row, 38, 228);
		}
	}

	private static double bankRowsUsed(int[] arrayItems, int[] arrayAmounts, boolean isMainTab) {
		double row = 0.0;
		int itemIndex = 0;
		for (int index = 0; index < arrayItems.length; index++) {
			itemIndex = index + 1;
			int item = arrayItems[index];
			int amount = arrayAmounts[index];
			if (item > 0 && amount >= 0) {
				row = Misc.getDoubleRoundedUp((double) itemIndex / 10.0) * (isMainTab ? 38.0 : 44.0);
			}
		}
		return row;
	}

	public static void updateClientLastXAmount(Player player, int xAmount) {
		player.lastXAmount = xAmount;
		player.getPA().sendMessage(":packet:lastxamount " + player.lastXAmount);
	}

	public static void withdrawAllButOneAndLastX(Player player, String string) {
		if (!Bank.hasBankingRequirements(player, false)) {
			return;
		}
		String split[] = string.split(" ");

		int slot = Integer.parseInt(split[2]);

		int itemId = Integer.parseInt(split[3]);

		int interfaceId = split.length == 4 ? 5382 : Integer.parseInt(split[4]);

		if (string.contains("lastx")) {
			if (interfaceId == 5382) {
				Bank.withdrawFromBank(player, itemId, slot, player.lastXAmount, false, false);
			} else {
				int previous = player.bankingTab;

				int tab = interfaceId - 35001 + 1;

				Bank.openCorrectTab(player, tab, false);

				Bank.withdrawFromBank(player, itemId, slot, player.lastXAmount, true, false);

				Bank.openCorrectTab(player, previous, false);
			}
		} else if (string.contains("allbutone")) {
			player.withdrawAllButOne = true;
			if (interfaceId == 5382) {
				Bank.withdrawFromBank(player, itemId, slot, player.bankingItemsN[slot] - 1, false, false);
			} else {
				int previous = player.bankingTab;

				int tab = interfaceId - 35001 + 1;

				Bank.openCorrectTab(player, tab, false);

				Bank.withdrawFromBank(player, itemId, slot, player.bankingItemsN[slot] - 1, true, false);

				Bank.openCorrectTab(player, previous, false);
			}
			player.withdrawAllButOne = false;
		}
	}

	public static int getBankSizeAmount(Player player) {
		int extra = 0;
		if (GameMode.getDifficulty(player, "GLADIATOR")) {
			extra += 100;
		}

		if (player.isAdministratorRank()) {
			extra += 1000;
		}
		if (player.isSupremeDonator()) {
			extra += 800;
		} else if (player.isImmortalDonator()) {
			extra += 700;
		} else if (player.isUberDonator()) {
			extra += 600;
		} else if (player.isUltimateDonator()) {
			extra += 500;
		} else if (player.isLegendaryDonator()) {
			extra += 400;
		} else if (player.isExtremeDonator()) {
			extra += 300;
		} else if (player.isSuperDonator()) {
			extra += 200;
		} else if (player.isDonator()) {
			extra += 100;
		}
		return 300 + extra;
	}

	/**
	 * @param player
	 * @return True, if the player has all the requirements to use the bank system.
	 */
	public static boolean hasBankingRequirements(Player player, boolean ignoreUsingBankCheck) {
		if (player.isAdministratorRank()) {
			return true;
		}
		if (Area.inAreaWhereItemsGetDeletedUponExit(player)) {
			return false;
		}
		if (player.isInTrade() || player.getTradeStatus() == 1) {
			return false;
		}
		if (player.getDuelStatus() != 0) {
			return false;
		}
		if (!player.isUsingBankInterface() && !ignoreUsingBankCheck) {
			return false;
		}
		if (player.isTeleporting()) {
			return false;
		}
		if (Area.inDangerousPvpAreaOrClanWars(player)) {
			return false;
		}
		if (Combat.inCombat(player)) {
			if (ignoreUsingBankCheck) {
				Combat.inCombatAlert(player);
			}
			return false;
		}
		if (ignoreUsingBankCheck) {
			if (player.setPin && BankPin.getFullPin(player).equalsIgnoreCase("")) {
				player.getPA().sendMessage("You need to enter your pin first.");
				return false;
			}
		}
		return true;
	}

	public static boolean hasItemInBankAndDelete(Player player, int itemId, int amount) {
		boolean free = false;
		if (GameType.isOsrsPvp()) {
			if (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)] != null) {
				free = BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)].spawnFree;
			}
			if (BloodMoneyPrice.getBloodMoneyPrice(ItemAssistant.getUnNotedItem(itemId)) <= 0 && free) {
				return true;
			}
		}
		itemId += 1;
		for (int i = 0; i < player.bankItems.length; i++) {
			if (player.bankItems[i] == itemId && player.bankItemsN[i] >= amount) {
				player.bankItemsN[i] -= amount;
				if (player.bankItemsN[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems1.length; i++) {

			if (player.bankItems1[i] == itemId && player.bankItems1N[i] >= amount) {
				player.bankItems1N[i] -= amount;
				if (player.bankItems1N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems1[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems2.length; i++) {

			if (player.bankItems2[i] == itemId && player.bankItems2N[i] >= amount) {
				player.bankItems2N[i] -= amount;
				if (player.bankItems2N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems2[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems3.length; i++) {
			if (player.bankItems3[i] == itemId && player.bankItems3N[i] >= amount) {
				player.bankItems3N[i] -= amount;
				if (player.bankItems3N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems3[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems4.length; i++) {
			if (player.bankItems4[i] == itemId && player.bankItems4N[i] >= amount) {
				player.bankItems4N[i] -= amount;
				if (player.bankItems4N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems4[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems5.length; i++) {
			if (player.bankItems5[i] == itemId && player.bankItems5N[i] >= amount) {
				player.bankItems5N[i] -= amount;
				if (player.bankItems5N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems5[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems6.length; i++) {
			if (player.bankItems6[i] == itemId && player.bankItems6N[i] >= amount) {
				player.bankItems6N[i] -= amount;
				if (player.bankItems6N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems6[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems7.length; i++) {
			if (player.bankItems7[i] == itemId && player.bankItems7N[i] >= amount) {
				player.bankItems7N[i] -= amount;
				if (player.bankItems7N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems7[i] = 0;
				}
				return true;
			}
		}
		for (int i = 0; i < player.bankItems8.length; i++) {
			if (player.bankItems8[i] == itemId && player.bankItems8N[i] >= amount) {
				player.bankItems8N[i] -= amount;
				if (player.bankItems8N[i] == 0 && !player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false)) {
					player.bankItems8[i] = 0;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * True, if the player has the item in the bank.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity to check for.
	 */
	public static boolean hasItemInBank(Player player, int itemId) {

		//Item id has to be +1
		for (int i = 0; i < player.bankItems.length; i++) {
			if (player.bankItems[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems1.length; i++) {
			if (player.bankItems1[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems2.length; i++) {
			if (player.bankItems2[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems3.length; i++) {
			if (player.bankItems3[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems4.length; i++) {
			if (player.bankItems4[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems5.length; i++) {
			if (player.bankItems5[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems6.length; i++) {
			if (player.bankItems6[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems7.length; i++) {
			if (player.bankItems7[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		for (int i = 0; i < player.bankItems8.length; i++) {
			if (player.bankItems8[i] == itemId) {
				player.itemInBankSlot = i;
				return true;
			}
		}
		return false;
	}

	public static int getItemAmountInBank(Player player, int itemId) {
		itemId++;
		for (int i = 0; i < player.bankItems.length; i++) {
			if (player.bankItems[i] == itemId) {
				return player.bankItemsN[i];
			}
		}
		for (int i = 0; i < player.bankItems1.length; i++) {
			if (player.bankItems1[i] == itemId) {
				return player.bankItems1N[i];
			}
		}
		for (int i = 0; i < player.bankItems2.length; i++) {
			if (player.bankItems2[i] == itemId) {
				return player.bankItems2N[i];
			}
		}
		for (int i = 0; i < player.bankItems3.length; i++) {
			if (player.bankItems3[i] == itemId) {
				return player.bankItems3N[i];
			}
		}
		for (int i = 0; i < player.bankItems4.length; i++) {
			if (player.bankItems4[i] == itemId) {
				return player.bankItems4N[i];
			}
		}
		for (int i = 0; i < player.bankItems5.length; i++) {
			if (player.bankItems5[i] == itemId) {
				return player.bankItems5N[i];
			}
		}
		for (int i = 0; i < player.bankItems6.length; i++) {
			if (player.bankItems6[i] == itemId) {
				return player.bankItems6N[i];
			}
		}
		for (int i = 0; i < player.bankItems7.length; i++) {
			if (player.bankItems7[i] == itemId) {
				return player.bankItems7N[i];
			}
		}
		for (int i = 0; i < player.bankItems8.length; i++) {
			if (player.bankItems8[i] == itemId) {
				return player.bankItems8N[i];
			}
		}
		return 0;
	}


	public static int getTabCount(Player player) {
		// count tabs
		int tabs = 0;
		if (!checkEmpty(player.bankItems1))
			tabs++;
		if (!checkEmpty(player.bankItems2))
			tabs++;
		if (!checkEmpty(player.bankItems3))
			tabs++;
		if (!checkEmpty(player.bankItems4))
			tabs++;
		if (!checkEmpty(player.bankItems5))
			tabs++;
		if (!checkEmpty(player.bankItems6))
			tabs++;
		if (!checkEmpty(player.bankItems7))
			tabs++;
		if (!checkEmpty(player.bankItems8))
			tabs++;
		return tabs;
	}

	public static boolean checkEmpty(int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] > 0) {
				return false;
			}
		}
		return true;
	}

	public static int getBankItems(Player player, int tab) {
		int ta = 0, tb = 0, tc = 0, td = 0, te = 0, tf = 0, tg = 0, th = 0, ti = 0;
		for (int i = 0; i < player.bankItems.length; i++)
			if (player.bankItems[i] > 0)
				ta++;
		for (int i = 0; i < player.bankItems1.length; i++)
			if (player.bankItems1[i] > 0)
				tb++;
		for (int i = 0; i < player.bankItems2.length; i++)
			if (player.bankItems2[i] > 0)
				tc++;
		for (int i = 0; i < player.bankItems3.length; i++)
			if (player.bankItems3[i] > 0)
				td++;
		for (int i = 0; i < player.bankItems4.length; i++)
			if (player.bankItems4[i] > 0)
				te++;
		for (int i = 0; i < player.bankItems5.length; i++)
			if (player.bankItems5[i] > 0)
				tf++;
		for (int i = 0; i < player.bankItems6.length; i++)
			if (player.bankItems6[i] > 0)
				tg++;
		for (int i = 0; i < player.bankItems7.length; i++)
			if (player.bankItems7[i] > 0)
				th++;
		for (int i = 0; i < player.bankItems8.length; i++)
			if (player.bankItems8[i] > 0)
				ti++;
		if (tab == 0)
			return ta;
		if (tab == 1)
			return tb;
		if (tab == 2)
			return tc;
		if (tab == 3)
			return td;
		if (tab == 4)
			return te;
		if (tab == 5)
			return tf;
		if (tab == 6)
			return tg;
		if (tab == 7)
			return th;
		if (tab == 8)
			return ti;
		return ta + tb + tc + td + te + tf + tg + th + ti; // return total
	}

	/**
	 * Open bank
	 **/
	public static void openUpBank(Player player, int tab, boolean openInterface, boolean reArrangeBank) {
		if (GameMode.getGameMode(player, "ULTIMATE IRON MAN")) {
			player.getDH().sendStatement("As an Ultimate Ironman, you cannot bank.");
			return;
		}
		if (player.isTeleporting()) {
			return;
		}
		if (Area.inDangerousPvpAreaOrClanWars(player) && !player.isAdministratorRank()) {
			return;
		}
		if (player.setPin && BankPin.getFullPin(player).equalsIgnoreCase("")) {
			BankPin.open(player);
			return;
		}

		if (player.getObjectY() == 3338 && player.getObjectX() >= 2860 && player.getObjectX() <= 2862) {
			Achievements.checkCompletionSingle(player, 1005);
		}
		player.setUsingBankInterface(true);

		player.setLastBankTabOpened((byte) tab);
		player.getPA().sendFrame36(116, 0, false);
		if (player.takeAsNote) {
			player.getPA().sendFrame36(115, 1, false);
		} else {
			player.getPA().sendFrame36(115, 0, false);
		}
		if (player.isInTrade() || player.getTradeStatus() == 1) {
			Player o = player.getTradeAndDuel().getPartnerTrade();
			if (o != null) {
				o.getTradeAndDuel().declineTrade1(true);
			}
		}
		if (player.getDuelStatus() == 1) {
			Player o = player.getTradeAndDuel().getPartnerDuel();
			if (o != null) {
				o.getTradeAndDuel().resetDuel();
			}
		}
		if (player.getOutStream() != null) {
			player.getPA().sendFrame36(835, player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false) ? 1 : 0, false);
			ItemAssistant.resetItems(player, 5064); // Spawning items while banking. Must be kept here to update inventory once.
			player.bankingTab = tab;
			sendTabs(player, false);
			updateBankingArrayDependingOnTab(player.bankingTab, player);
			if (reArrangeBank) {
				rearrangeBank(player);
			}
			if (openInterface) {
				updateBankValue(player);
				player.bankUpdated = true;
				if (player.bot) {
					return;
				}
				if (!player.firstBankOpened) {
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {

							player.getPA().sendFrame248(24959, 5063);
							player.firstBankOpened = true;
							container.stop();
						}

						@Override
						public void stop() {
						}
					}, 1);
				} else {
					player.getPA().sendFrame248(24959, 5063);
				}
			}
		}
	}

	public static void updateBankingArrayDependingOnTab(int tab, Player player) {
		if (tab < 0) {
			return;
		}
		if (tab == 0) {
			player.bankingItems = player.bankItems;
			player.bankingItemsN = player.bankItemsN;
		}
		if (tab == 1) {
			player.bankingItems = player.bankItems1;
			player.bankingItemsN = player.bankItems1N;
		}
		if (tab == 2) {
			player.bankingItems = player.bankItems2;
			player.bankingItemsN = player.bankItems2N;
		}
		if (tab == 3) {
			player.bankingItems = player.bankItems3;
			player.bankingItemsN = player.bankItems3N;
		}
		if (tab == 4) {
			player.bankingItems = player.bankItems4;
			player.bankingItemsN = player.bankItems4N;
		}
		if (tab == 5) {
			player.bankingItems = player.bankItems5;
			player.bankingItemsN = player.bankItems5N;
		}
		if (tab == 6) {
			player.bankingItems = player.bankItems6;
			player.bankingItemsN = player.bankItems6N;
		}
		if (tab == 7) {
			player.bankingItems = player.bankItems7;
			player.bankingItemsN = player.bankItems7N;
		}
		if (tab == 8) {
			player.bankingItems = player.bankItems8;
			player.bankingItemsN = player.bankItems8N;
		}

	}

	private static void updateBankValue(Player player) {
		player.getPA().sendFrame126("The Bank of " + ServerConstants.getServerName() + " - Account value: " + Misc.formatRunescapeStyle(ItemAssistant.getAccountBankValueLongWithDelay(player)), 5383);

	}

	public static int[] getBankArrayByBankingTab(Player player, int tab) {

		switch (tab) {
			case 0:
				return player.bankItems;
			case 1:
				return player.bankItems1;
			case 2:
				return player.bankItems2;
			case 3:
				return player.bankItems3;
			case 4:
				return player.bankItems4;
			case 5:
				return player.bankItems5;
			case 6:
				return player.bankItems6;
			case 7:
				return player.bankItems7;
			case 8:
				return player.bankItems8;
		}
		return null;
	}

	public static void openCorrectTab(Player player, int tab, boolean updateBank) {
		if (!hasBankingRequirements(player, false)) {
			return;
		}
		if (player.setPin && BankPin.getFullPin(player).equalsIgnoreCase("")) {
			BankPin.open(player);
			return;
		}

		player.setLastBankTabOpened((byte) tab);
		if (player.isInTrade() || player.getTradeStatus() == 1) {
			Player o = player.getTradeAndDuel().getPartnerTrade();
			if (o != null) {
				if (!o.getPlayerName().equals(player.getLastDueledWithName())) {
					o = null;
				}
			}
			if (o != null) {
				o.getTradeAndDuel().declineTrade1(true);
			}
		}
		if (player.getDuelStatus() == 1) {
			Player o = player.getTradeAndDuel().getPartnerDuel();
			if (o != null) {
				o.getTradeAndDuel().resetDuel();
			}
		}
		if (player.getOutStream() != null && player != null) {
			player.bankingTab = tab;
			if (player.bankingTab == 0) {
				player.bankingItems = player.bankItems;
				player.bankingItemsN = player.bankItemsN;
			}
			if (player.bankingTab == 1) {
				player.bankingItems = player.bankItems1;
				player.bankingItemsN = player.bankItems1N;
			}
			if (player.bankingTab == 2) {
				player.bankingItems = player.bankItems2;
				player.bankingItemsN = player.bankItems2N;
			}
			if (player.bankingTab == 3) {
				player.bankingItems = player.bankItems3;
				player.bankingItemsN = player.bankItems3N;
			}
			if (player.bankingTab == 4) {
				player.bankingItems = player.bankItems4;
				player.bankingItemsN = player.bankItems4N;
			}
			if (player.bankingTab == 5) {
				player.bankingItems = player.bankItems5;
				player.bankingItemsN = player.bankItems5N;
			}
			if (player.bankingTab == 6) {
				player.bankingItems = player.bankItems6;
				player.bankingItemsN = player.bankItems6N;
			}
			if (player.bankingTab == 7) {
				player.bankingItems = player.bankItems7;
				player.bankingItemsN = player.bankItems7N;
			}
			if (player.bankingTab == 8) {
				player.bankingItems = player.bankItems8;
				player.bankingItemsN = player.bankItems8N;
			}
			if (updateBank) {
				player.bankUpdated = true;
			}
		}
	}

	public static void sendTabs(Player player, boolean forceSetToMainTab) {
		if (!hasBankingRequirements(player, false)) {
			return;
		}
		if (player.doNotSendTabs) {
			player.doNotSendTabs = false;
			return;
		}

		// remove empty tab
		boolean moveRest = false;
		if (checkEmpty(player.bankItems1)) { // tab 1 empty
			player.bankItems1 = Arrays.copyOf(player.bankItems2, player.bankingItems.length);
			player.bankItems1N = Arrays.copyOf(player.bankItems2N, player.bankingItems.length);
			player.bankItems2 = new int[ServerConstants.BANK_SIZE];
			player.bankItems2N = new int[ServerConstants.BANK_SIZE];
			moveRest = true;
		}
		if (checkEmpty(player.bankItems2) || moveRest) {
			player.bankItems2 = Arrays.copyOf(player.bankItems3, player.bankingItems.length);
			player.bankItems2N = Arrays.copyOf(player.bankItems3N, player.bankingItems.length);
			player.bankItems3 = new int[ServerConstants.BANK_SIZE];
			player.bankItems3N = new int[ServerConstants.BANK_SIZE];
			moveRest = true;
		}
		if (checkEmpty(player.bankItems3) || moveRest) {
			player.bankItems3 = Arrays.copyOf(player.bankItems4, player.bankingItems.length);
			player.bankItems3N = Arrays.copyOf(player.bankItems4N, player.bankingItems.length);
			player.bankItems4 = new int[ServerConstants.BANK_SIZE];
			player.bankItems4N = new int[ServerConstants.BANK_SIZE];
			moveRest = true;
		}
		if (checkEmpty(player.bankItems4) || moveRest) {
			player.bankItems4 = Arrays.copyOf(player.bankItems5, player.bankingItems.length);
			player.bankItems4N = Arrays.copyOf(player.bankItems5N, player.bankingItems.length);
			player.bankItems5 = new int[ServerConstants.BANK_SIZE];
			player.bankItems5N = new int[ServerConstants.BANK_SIZE];
			moveRest = true;
		}
		if (checkEmpty(player.bankItems5) || moveRest) {
			player.bankItems5 = Arrays.copyOf(player.bankItems6, player.bankingItems.length);
			player.bankItems5N = Arrays.copyOf(player.bankItems6N, player.bankingItems.length);
			player.bankItems6 = new int[ServerConstants.BANK_SIZE];
			player.bankItems6N = new int[ServerConstants.BANK_SIZE];
			moveRest = true;
		}
		if (checkEmpty(player.bankItems6) || moveRest) {
			player.bankItems6 = Arrays.copyOf(player.bankItems7, player.bankingItems.length);
			player.bankItems6N = Arrays.copyOf(player.bankItems7N, player.bankingItems.length);
			player.bankItems7 = new int[ServerConstants.BANK_SIZE];
			player.bankItems7N = new int[ServerConstants.BANK_SIZE];
			moveRest = true;
		}
		if (checkEmpty(player.bankItems7) || moveRest) {
			player.bankItems7 = Arrays.copyOf(player.bankItems8, player.bankingItems.length);
			player.bankItems7N = Arrays.copyOf(player.bankItems8N, player.bankingItems.length);
			player.bankItems8 = new int[ServerConstants.BANK_SIZE];
			player.bankItems8N = new int[ServerConstants.BANK_SIZE];
		}
		if (player.bankingTab > getTabCount(player)) {
			player.bankingTab = getTabCount(player);
		}

		if (player.isUsingBankSearch()) {
			player.bankingTab = player.originalTab;
		}
		if (moveRest) {
			player.doNotSendTabs = true;
			Bank.openUpBank(player, player.bankingTab, false, false);
		}
		player.getPA().sendFrame126(Integer.toString(getTabCount(player)), 27001);
		if (!player.ignoreReOrder) {
			player.getPA().sendFrame126(Integer.toString(forceSetToMainTab ? 0 : player.bankingTab), 27002);
		} else {
			player.ignoreReOrder = false;
		}

		// Item amount on the tab item.

		itemOnInterface(player, 22035, 0, getInterfaceModel(0, player.bankItems1, player.bankItems1N), getAmount(player.bankItems1[0], player.bankItems1N[0]));
		itemOnInterface(player, 22036, 0, getInterfaceModel(0, player.bankItems2, player.bankItems2N), getAmount(player.bankItems2[0], player.bankItems2N[0]));
		itemOnInterface(player, 22037, 0, getInterfaceModel(0, player.bankItems3, player.bankItems3N), getAmount(player.bankItems3[0], player.bankItems3N[0]));
		itemOnInterface(player, 22038, 0, getInterfaceModel(0, player.bankItems4, player.bankItems4N), getAmount(player.bankItems4[0], player.bankItems4N[0]));
		itemOnInterface(player, 22039, 0, getInterfaceModel(0, player.bankItems5, player.bankItems5N), getAmount(player.bankItems5[0], player.bankItems5N[0]));
		itemOnInterface(player, 22040, 0, getInterfaceModel(0, player.bankItems6, player.bankItems6N), getAmount(player.bankItems6[0], player.bankItems6N[0]));
		itemOnInterface(player, 22041, 0, getInterfaceModel(0, player.bankItems7, player.bankItems7N), getAmount(player.bankItems7[0], player.bankItems7N[0]));
		itemOnInterface(player, 22042, 0, getInterfaceModel(0, player.bankItems8, player.bankItems8N), getAmount(player.bankItems8[0], player.bankItems8N[0]));
		player.getPA().sendFrame126("1", 27000);
		if (player.getInterfaceIdOpened() == 24959) {
			player.getPA().sendMessage(":packet:updatebank");
			player.bankUpdated = true;
		}
	}

	public static int tabIdForTabContainer(int container) {
		if (container < 22035 || container > 22042) {
			throw new IllegalArgumentException(String.format("Container is not within the range of possible containers; %s", container));
		}
		return 1 + (container - 22035);
	}


	public static void itemOnInterface(Player player, int frame, int slot, int id, int amount) {
		player.getPA().sendFrame34(frame, id, slot, amount);
	}

	public static int getInterfaceModel(int slot, int[] array, int[] arrayN) {
		int model = array[slot] - 1;
		if (model == 995) {
			if (arrayN[slot] > 9999) {
				model = 1004;
			} else if (arrayN[slot] > 999) {
				model = 1003;
			} else if (arrayN[slot] > 249) {
				model = 1002;
			} else if (arrayN[slot] > 99) {
				model = 1001;
			} else if (arrayN[slot] > 24) {
				model = 1000;
			} else if (arrayN[slot] > 4) {
				model = 999;
			} else if (arrayN[slot] > 3) {
				model = 998;
			} else if (arrayN[slot] > 2) {
				model = 997;
			} else if (arrayN[slot] > 1) {
				model = 996;
			}
		}
		return model;
	}

	public static int getAmount(int itemId, int amount) {
		itemId--;
		if (itemId <= 0) {
			return 1;
		}
		return amount;
	}

	public static boolean bankItem(Player player, int itemId, int amount, int[] toTabBankTabItems, int[] toTabBankTabItemsAmount, int toTab) {
		if (!hasBankingRequirements(player, false)) {
			return false;
		}
		if (itemId <= 0 || amount <= 0) {
			return false;
		}
		if (ItemAssistant.nulledItem(itemId - 1)) {
			return false;
		}

		int itemExistsInTabIndex = 0;
		boolean alreadyInBank = false;
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (toTabBankTabItems[i] == itemId) {
				alreadyInBank = true;
				itemExistsInTabIndex = i;
				break;
			}
		}
		if (toTab == 0) {
			player.bankingItems = player.bankItems;
			player.bankingItemsN = player.bankItemsN;
		}
		if (toTab == 1) {
			player.bankingItems = player.bankItems1;
			player.bankingItemsN = player.bankItems1N;
		}
		if (toTab == 2) {
			player.bankingItems = player.bankItems2;
			player.bankingItemsN = player.bankItems2N;
		}
		if (toTab == 3) {
			player.bankingItems = player.bankItems3;
			player.bankingItemsN = player.bankItems3N;
		}
		if (toTab == 4) {
			player.bankingItems = player.bankItems4;
			player.bankingItemsN = player.bankItems4N;
		}
		if (toTab == 5) {
			player.bankingItems = player.bankItems5;
			player.bankingItemsN = player.bankItems5N;
		}
		if (toTab == 6) {
			player.bankingItems = player.bankItems6;
			player.bankingItemsN = player.bankItems6N;
		}
		if (toTab == 7) {
			player.bankingItems = player.bankItems7;
			player.bankingItemsN = player.bankItems7N;
		}
		if (toTab == 8) {
			player.bankingItems = player.bankItems8;
			player.bankingItemsN = player.bankItems8N;
		}

		if (alreadyInBank) {
			if ((toTabBankTabItemsAmount[itemExistsInTabIndex] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (toTabBankTabItemsAmount[itemExistsInTabIndex] + amount) > -1) {
				player.bankingItemsN[itemExistsInTabIndex] += amount;
			} else {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
				return false;
			}
			player.bankUpdated = true;
			return true;
		} else if (!alreadyInBank && freeBankSlots(player) > 0) {
			for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
				if (toTabBankTabItems[i] <= 0) {
					itemExistsInTabIndex = i;
					break;
				}
			}
			if ((toTabBankTabItemsAmount[itemExistsInTabIndex] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (toTabBankTabItemsAmount[itemExistsInTabIndex] + amount) > -1) {
				player.bankingItems[itemExistsInTabIndex] = itemId;
				player.bankingItemsN[itemExistsInTabIndex] += amount;
			} else {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
				return false;
			}
			player.bankUpdated = true;
			return true;
		} else {
			player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
			return false;
		}
	}

	public static void shouldRearrangePreviousTab(Player player) {
		switch (player.bankingTab) {
			case 1:
				if (player.bankItems1[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 2:
				if (player.bankItems2[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 3:
				if (player.bankItems3[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 4:
				if (player.bankItems4[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 5:
				if (player.bankItems5[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 6:
				if (player.bankItems6[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 7:
				if (player.bankItems7[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
			case 8:
				if (player.bankItems8[0] == 0) {
					Bank.rearrangeBank(player);
				}
				break;
		}
	}

	public static int getTabforItemOrNone(Player player, int itemId) {
		itemId = ItemAssistant.getUnNotedItem(itemId);

		itemId = itemId + 1;

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems[i] == itemId) {
				return 0;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems1[i] == itemId) {
				return 1;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems2[i] == itemId) {
				return 2;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems3[i] == itemId) {
				return 3;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems4[i] == itemId) {
				return 4;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems5[i] == itemId) {
				return 5;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems6[i] == itemId) {
				return 6;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems7[i] == itemId) {
				return 7;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems8[i] == itemId) {
				return 8;
			}
		}

		return -1;
	}

	public static int getTabforItem(Player player, int itemId) {
		int tab = getTabforItemOrNone(player, itemId);

		return tab == -1 ? player.originalTab : tab; // if not in bank add to current tab
	}

	public static boolean enableDupe = false;

	public static boolean bankItem(Player player, int itemId, int fromSlot, int amount, boolean updateBank) {

		if (!hasBankingRequirements(player, false)) {
			return false;
		}
		if (ItemAssistant.isNulledSlot(fromSlot)) {
			return false;
		}
		if (ItemAssistant.nulledItem(itemId - 1)) {
			return false;
		}
		int itemStockInInventory = ItemAssistant.getItemAmount(player, itemId - 1);
		if (amount > itemStockInInventory) {
			amount = itemStockInInventory;
		}

		if (!ItemAssistant.hasItemInInventory(player, itemId - 1)) {
			return false;
		}
		if (getBankAmount(player, "REMAINING") == 0) {
			int unnoted = ItemAssistant.getUnNotedItem(itemId - 1);
			if (!hasItemInBank(player, unnoted + 1)) {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
				return false;
			}
		}
		int originalItemId = itemId;
		itemId = ItemAssistant.getUnNotedItem(itemId - 1) + 1;
		int initialTab = player.bankingTab;

		// Move to tab item is in before adding
		openCorrectTab(player, getTabforItem(player, itemId - 1), updateBank);

		int itemExistsInTabIndex = 0;
		boolean alreadyInBank = false;
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] == itemId) {
				alreadyInBank = true;
				itemExistsInTabIndex = i;
				break;
			}
		}

		if (alreadyInBank) {
			if ((player.bankingItemsN[itemExistsInTabIndex] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (player.bankingItemsN[itemExistsInTabIndex] + amount) > -1) {
				player.bankingItemsN[itemExistsInTabIndex] += amount;
				ItemAssistant.deleteItemFromInventory(player, originalItemId - 1, amount);
				if (updateBank) {
					player.bankUpdated = true;
					openUpBank(player, initialTab, false, false);
				}
			} else {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
				return false;
			}
			player.bankUpdated = true;
			return true;
		} else if (!alreadyInBank && freeBankSlots(player) > 0) {
			for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
				if (player.bankingItems[i] <= 0) {
					itemExistsInTabIndex = i;
					break;
				}
			}
			if ((player.bankingItemsN[itemExistsInTabIndex] + amount) <= ServerConstants.MAX_ITEM_AMOUNT && (player.bankingItemsN[itemExistsInTabIndex] + amount) > -1) {
				player.bankingItems[itemExistsInTabIndex] = itemId;
				player.bankingItemsN[itemExistsInTabIndex] += amount;
				ItemAssistant.deleteItemFromInventory(player, originalItemId - 1, amount);
				if (updateBank) {
					player.bankUpdated = true;
					openUpBank(player, initialTab, false, false);
				}
			} else {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
				return false;
			}
			player.bankUpdated = true;
			return true;
		} else {
			if (Bank.addItemToBank(player, itemId - 1, amount, true, initialTab)) {
				player.bankUpdated = true;
				int itemToDelete = originalItemId - 1;
				ItemAssistant.deleteItemFromInventory(player, itemToDelete, amount);
				return true;
			} else {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId - 1) + ".");
				return false;
			}
		}
	}

	public static int freeBankSlots(Player player) {
		int freeS = 0;
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public static void toTab(Player player, int tab, int fromSlot, boolean openInterface, boolean forceSetToMainTab) {
		if (!hasBankingRequirements(player, false)) {
			return;
		}
		if (tab == player.bankingTab) {
			return;
		}
		if (tab > getTabCount(player) + 1) {
			return;
		}
		if (getBankItems(player, tab) >= 352) {
			player.playerAssistant.sendMessage("You can't store any more items in this tab!");
			return;
		}

		int id = player.bankingItems[fromSlot];

		int amount = player.bankingItemsN[fromSlot];

		player.bankingItems[fromSlot] = -1;

		player.bankingItemsN[fromSlot] = 0;

		if (tab == 0)
			bankItem(player, id, amount, player.bankItems, player.bankItemsN, tab);
		else if (tab == 1)
			bankItem(player, id, amount, player.bankItems1, player.bankItems1N, tab);
		else if (tab == 2)
			bankItem(player, id, amount, player.bankItems2, player.bankItems2N, tab);
		else if (tab == 3)
			bankItem(player, id, amount, player.bankItems3, player.bankItems3N, tab);
		else if (tab == 4)
			bankItem(player, id, amount, player.bankItems4, player.bankItems4N, tab);
		else if (tab == 5)
			bankItem(player, id, amount, player.bankItems5, player.bankItems5N, tab);
		else if (tab == 6)
			bankItem(player, id, amount, player.bankItems6, player.bankItems6N, tab);
		else if (tab == 7)
			bankItem(player, id, amount, player.bankItems7, player.bankItems7N, tab);
		else if (tab == 8)
			bankItem(player, id, amount, player.bankItems8, player.bankItems8N, tab);

		openUpBank(player, forceSetToMainTab ? 0 : player.bankingTab, openInterface, false); // refresh
		// is the following really necessary? it seems pointless and wasteful. - jason (march 27 2018)
		//openUpBank(player, player.bankingTab, true, false); // refresh twice to ensure update
		player.setInventoryUpdate(true);
	}

	public static void moveFromTabToTab(Player player, int fromTab, int fromSlot, int toTab, int toSlot, boolean openInterface) {
		if (!hasBankingRequirements(player, false)) {
			return;
		}
		if (fromTab == toTab) {
			return;
		}
		if (toTab > getTabCount(player) + 1) {
			return;
		}
		int previousTab = player.bankingTab;

		openCorrectTab(player, fromTab, false);

		int idFrom = player.bankingItems[fromSlot];

		int amountFrom = player.bankingItemsN[fromSlot];

		if (idFrom == -1 || amountFrom <= 0) {
			openCorrectTab(player, previousTab, false);
			return;
		}
		openCorrectTab(player, toTab, false);

		int idTo = player.bankingItems[toSlot];

		int amountTo = player.bankingItemsN[toSlot];

		if (idTo < 0 || amountTo <= 0) {
			openCorrectTab(player, fromTab, false);
			toTab(player, toTab, fromSlot, false, previousTab == 0 ? true : false);
			openCorrectTab(player, previousTab, false);
			return;
		}
		player.bankingItems[toSlot] = idFrom;
		player.bankingItemsN[toSlot] = amountFrom;

		openCorrectTab(player, fromTab, false);

		player.bankingItems[fromSlot] = idTo;
		player.bankingItemsN[fromSlot] = amountTo;

		openUpBank(player, previousTab, openInterface, false); // refresh
		// is the following really necessary? it seems pointless and wasteful. - jason (march 27 2018)
		//openUpBank(player, player.bankingTab, true, false); // refresh twice to ensure update
		player.setInventoryUpdate(true);
	}

	public static void withdrawFromBank(Player player, int itemId, int fromSlot, int amount, boolean forceSetToMainTab, boolean placeholder) {
		if (!hasBankingRequirements(player, false)) {
			return;
		}
		if (fromSlot > player.bankingItems.length - 1) {
			return;
		}
		if (!placeholder) {
			if (player.getAttributes().getOrDefault(ALWAYS_PLACEHOLDER, false)) {
				placeholder = true;
			}
		}
		if (player.isUsingBankSearch()) {
			if (fromSlot > player.bankSearchedItems.size() - 1) {
				return;
			}
			itemId = player.bankSearchedItems.get(fromSlot).getId() - 1;
			player.bankingTab = getTabforItem(player, itemId);
			hasItemInBank(player, itemId + 1);

			// fromSlot is currently the slot of where the item is on the player's search screen.
			// Now we are changing it to the slot of where the item is in the tab the item resides in.
			fromSlot = player.itemInBankSlot;
			updateBankingArrayDependingOnTab(player.bankingTab, player);
			if (player.withdrawAllButOne) {
				amount = player.bankingItemsN[fromSlot] - 1;
			}

			// Withdraw all
			if (amount == Integer.MAX_VALUE) {
				amount = player.bankingItemsN[fromSlot];
			}
		}
		int tempT = player.bankingTab;

		int collect = amount;

		for (int i = 0; i < player.tempItems.length; i++) {
			if (player.tempItems[i] == itemId + 1 || player.tempItems[i] == itemId) {
				int count = Math.min(player.tempItemsN[i], collect);
				if (collect == -1) {
					count = player.tempItemsN[i];
				}
				player.bankingTab = (player.tempItemsT[i]);
				updateBankingArrayDependingOnTab(player.bankingTab, player);
				withdrawFromBank(player, itemId + 1, player.tempItemsS[i], count, forceSetToMainTab, false);
				collect -= count;
			}
		}

		player.bankingTab = tempT;
		if (amount > 0) {
			if (player.bankingItems[fromSlot] > 0) {
				if (!player.takeAsNote) {
					if (ItemDefinition.getDefinitions()[player.bankingItems[fromSlot] - 1].stackable) {
						if (player.bankingItemsN[fromSlot] > amount) {
							if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), amount)) {
								player.bankingItemsN[fromSlot] -= amount;
								player.bankUpdated = true;
								if (player.bankingItemsN[fromSlot] <= 0 && !placeholder) {
									player.bankingItems[fromSlot] = 0;
								}
								if (itemId == 12791) {
									RunePouch.updateRunePouchMainStorage(player, false);
								}

							}
						} else {
							if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), player.bankingItemsN[fromSlot])) {
								if (!placeholder) {
									player.bankingItems[fromSlot] = 0;
								}
								player.bankingItemsN[fromSlot] = 0;
								player.bankUpdated = true;
								if (itemId == 12791) {
									RunePouch.updateRunePouchMainStorage(player, false);
								}
							}
						}
					} else {
						if (player.bankingItemsN[fromSlot] > 0) {
							int amountCanWithdraw = ItemAssistant.getFreeInventorySlots(player);
							if (player.bankingItemsN[fromSlot] - amountCanWithdraw < 0) {
								amountCanWithdraw = player.bankingItemsN[fromSlot];
							}
							if (amountCanWithdraw > amount) {
								amountCanWithdraw = amount;
							}
							if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), amountCanWithdraw)) {
								player.bankingItemsN[fromSlot] -= amountCanWithdraw;
								if (itemId == 12791) {
									RunePouch.updateRunePouchMainStorage(player, false);
								}
								if (player.bankingItemsN[fromSlot] <= 0 && !placeholder) {
									player.bankingItems[fromSlot] = 0;
								}
							}
						}
						player.bankUpdated = true;
					}
				} else if (player.takeAsNote && ItemDefinition.getDefinitions()[ItemAssistant.getNotedItem(player.bankingItems[fromSlot] - 1)].note) {
					if (player.bankingItemsN[fromSlot] > amount) {
						if (ItemAssistant.addItem(player, ItemAssistant.getNotedItem(player.bankingItems[fromSlot] - 1), amount)) {
							player.bankingItemsN[fromSlot] -= amount;
							player.bankUpdated = true;
							if (player.bankingItemsN[fromSlot] <= 0 && !placeholder) {
								player.bankingItems[fromSlot] = 0;
							}
							if (itemId == 12791) {
								RunePouch.updateRunePouchMainStorage(player, false);
							}
						}
					} else {
						if (ItemAssistant.addItem(player, ItemAssistant.getNotedItem(player.bankingItems[fromSlot] - 1), player.bankingItemsN[fromSlot])) {
							if (!placeholder) {
								player.bankingItems[fromSlot] = 0;
							}
							player.bankingItemsN[fromSlot] = 0;
							player.bankUpdated = true;
							if (itemId == 12791) {
								RunePouch.updateRunePouchMainStorage(player, false);
							}
						}
					}
				} else {
					player.playerAssistant.sendMessage("This item can't be withdrawn as a note.");
					if (ItemDefinition.getDefinitions()[player.bankingItems[fromSlot] - 1].stackable) {
						if (player.bankingItemsN[fromSlot] > amount) {
							if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), amount)) {
								player.bankingItemsN[fromSlot] -= amount;
								if (player.bankingItemsN[fromSlot] <= 0 && !placeholder) {
									player.bankingItems[fromSlot] = 0;
								}
								player.bankUpdated = true;
								if (itemId == 12791) {
									RunePouch.updateRunePouchMainStorage(player, false);
								}
							}
						} else {
							if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), player.bankingItemsN[fromSlot])) {
								if (!placeholder) {
									player.bankingItems[fromSlot] = 0;
								}
								player.bankingItemsN[fromSlot] = 0;
								player.bankUpdated = true;
								if (itemId == 12791) {
									RunePouch.updateRunePouchMainStorage(player, false);
								}
							}
						}
					} else {
						if (player.bankingItemsN[fromSlot] > 0) {
							int amountCanWithdraw = ItemAssistant.getFreeInventorySlots(player);
							if (player.bankingItemsN[fromSlot] - amountCanWithdraw < 0) {
								amountCanWithdraw = player.bankingItemsN[fromSlot];
							}
							if (amountCanWithdraw > amount) {
								amountCanWithdraw = amount;
							}
							if (ItemAssistant.addItem(player, (player.bankingItems[fromSlot] - 1), amountCanWithdraw)) {
								player.bankingItemsN[fromSlot] -= amountCanWithdraw;
								if (itemId == 12791) {
									RunePouch.updateRunePouchMainStorage(player, false);
								}
								if (player.bankingItemsN[fromSlot] <= 0 && !placeholder) {
									player.bankingItems[fromSlot] = 0;
								}
							}
						}
						player.bankUpdated = true;
					}
				}
			}
			if (!placeholder) {
				sendTabs(player, forceSetToMainTab);
			}
		}
		updateBankingArrayDependingOnTab(player.bankingTab, player);
		player.bankUpdated = true;
	}

	static long start;

	public static void rearrangeBank(Player player) {
		boolean arranged = false;
		int highestSlot = 0;
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] != 0) {
				if (highestSlot <= i) {
					highestSlot = i;
				}
			}
		}
		for (int i = 0; i <= highestSlot; i++) {
			if (player.bankingItems[i] == 0) {
				boolean stop = false;

				for (int k = i; k <= highestSlot; k++) {
					if (player.bankingItems[k] != 0 && !stop) {
						int spots = k - i;
						for (int j = k; j <= highestSlot; j++) {
							player.bankingItems[j - spots] = player.bankingItems[j];
							player.bankingItemsN[j - spots] = player.bankingItemsN[j];
							stop = true;
							player.bankingItems[j] = 0;
							player.bankingItemsN[j] = 0;
							arranged = true;
						}
					}
				}
			}
		}
		if (arranged) {
			sendTabs(player, false);
		}
	}

	public static void updateContainer(Player player, int containerId, int[] items, int[] amounts) {
		if (items.length != amounts.length) {
			throw new IllegalArgumentException("Length of items and amount of items must be the same size.");
		}
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(containerId);
		player.getOutStream().writeWord(ServerConstants.BANK_SIZE);

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (amounts[i] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(amounts[i]);
			} else {
				player.getOutStream().writeByte(amounts[i]);
			}
			if (items[i] > ServerConstants.MAX_ITEM_ID || items[i] < 0) {
				items[i] = 0;
			}
			player.getOutStream().writeWordBigEndianA(items[i]);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	public static void resetBank(Player player, boolean ignoreSearchCheck) {
		if (player.bot) {
			return;
		}
		if (player.isUsingBankSearch() && !ignoreSearchCheck) {
			search(player, player.bankSearchString, true);
			return;
		}
		setBankScrollLengthNormal(player, false, player.bankingTab == 0 ? true : false);
		updateContainer(player, 5382, player.bankingItems, player.bankingItemsN);
		if (player.bankingTab == 0) {
			for (int tabContainerId = 35001; tabContainerId < 35009; tabContainerId++) {
				int[] itemsForContainer = getItemsForContainer(player, tabContainerId);

				int[] amountsForContainer = getAmountsForContainer(player, tabContainerId);

				if (itemsForContainer == null || amountsForContainer == null) {
					continue;
				}
				updateContainer(player, tabContainerId, itemsForContainer, amountsForContainer);
			}
			player.getPA().updateBankSeparators(true);
		} else {
			player.getPA().updateBankSeparators(false);
		}
		updateAmount(player);
	}

	public static void updateAmount(Player player) {
		player.getPA().sendFrame126(Integer.toString(getBankAmount(player, "AMOUNT")), 22033);
		player.getPA().sendFrame126(Integer.toString(getBankSizeAmount(player)), 22034);
	}

	private static int[] getItemsForTabId(Player player, int tabId) {
		if (tabId < 0 || tabId > 8) {
			throw new IllegalArgumentException("Tab is must be between 0 and 8 inclusive.");
		}
		return getItemsForContainer(player, tabId == 0 ? 5382 : 35000 + tabId);
	}

	private static int[] getAmountsForTabId(Player player, int tabId) {
		if (tabId < 0 || tabId > 8) {
			throw new IllegalArgumentException("Tab is must be between 0 and 8 inclusive.");
		}
		return getAmountsForContainer(player, tabId == 0 ? 5382 : 35000 + tabId);
	}

	public static int[] getItemsForContainer(Player player, int container) {
		return container == 5382 ? player.bankItems :
				       container == 35_001 ? player.bankItems1 :
						       container == 35_002 ? player.bankItems2 :
								       container == 35_003 ? player.bankItems3 :
										       container == 35_004 ? player.bankItems4 :
												       container == 35_005 ? player.bankItems5 :
														       container == 35_006 ? player.bankItems6 :
																       container == 35_007 ? player.bankItems7 :
																		       container == 35_008 ? player.bankItems8 : null;

	}

	public static int[] getAmountsForContainer(Player player, int container) {
		return container == 5382 ? player.bankItemsN :
				       container == 35_001 ? player.bankItems1N :
						       container == 35_002 ? player.bankItems2N :
								       container == 35_003 ? player.bankItems3N :
										       container == 35_004 ? player.bankItems4N :
												       container == 35_005 ? player.bankItems5N :
														       container == 35_006 ? player.bankItems6N :
																       container == 35_007 ? player.bankItems7N :
																		       container == 35_008 ? player.bankItems8N : null;

	}

	public static int getBankAmount(Player player, String type) {
		int tab0 = 0;
		int tab1 = 0;
		int tab2 = 0;
		int tab3 = 0;
		int tab4 = 0;
		int tab5 = 0;
		int tab6 = 0;
		int tab7 = 0;
		int tab8 = 0;

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems[i] > 0) {
				tab0++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems1[i] > 0) {
				tab1++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems2[i] > 0) {
				tab2++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems3[i] > 0) {
				tab3++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems4[i] > 0) {
				tab4++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems5[i] > 0) {
				tab5++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems6[i] > 0) {
				tab6++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems7[i] > 0) {
				tab7++;
			}
		}

		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankItems8[i] > 0) {
				tab8++;
			}
		}

		int total = tab0 + tab1 + tab2 + tab3 + tab4 + tab5 + tab6 + tab7 + tab8;
		if (type.equals("AMOUNT")) {
			return total;
		} else if (type.equals("REMAINING")) {
			return getBankSizeAmount(player) - total;
		}

		return 0;
	}

	public static void swapBankItem(Player player, int from, int to) {
		if (player.bankingTab == 0) {
			int tempI = player.bankItems[from];
			int tempN = player.bankItemsN[from];
			player.bankItems[from] = player.bankItems[to];
			player.bankItemsN[from] = player.bankItemsN[to];
			player.bankItems[to] = tempI;
			player.bankItemsN[to] = tempN;
		} else if (player.bankingTab == 1) {
			int tempI = player.bankItems1[from];
			int tempN = player.bankItems1N[from];
			player.bankItems1[from] = player.bankItems1[to];
			player.bankItems1N[from] = player.bankItems1N[to];
			player.bankItems1[to] = tempI;
			player.bankItems1N[to] = tempN;
		} else if (player.bankingTab == 2) {
			int tempI = player.bankItems2[from];
			int tempN = player.bankItems2N[from];
			player.bankItems2[from] = player.bankItems2[to];
			player.bankItems2N[from] = player.bankItems2N[to];
			player.bankItems2[to] = tempI;
			player.bankItems2N[to] = tempN;
		} else if (player.bankingTab == 3) {
			int tempI = player.bankItems3[from];
			int tempN = player.bankItems3N[from];
			player.bankItems3[from] = player.bankItems3[to];
			player.bankItems3N[from] = player.bankItems3N[to];
			player.bankItems3[to] = tempI;
			player.bankItems3N[to] = tempN;
		} else if (player.bankingTab == 4) {
			int tempI = player.bankItems4[from];
			int tempN = player.bankItems4N[from];
			player.bankItems4[from] = player.bankItems4[to];
			player.bankItems4N[from] = player.bankItems4N[to];
			player.bankItems4[to] = tempI;
			player.bankItems4N[to] = tempN;
		} else if (player.bankingTab == 5) {
			int tempI = player.bankItems5[from];
			int tempN = player.bankItems5N[from];
			player.bankItems5[from] = player.bankItems5[to];
			player.bankItems5N[from] = player.bankItems5N[to];
			player.bankItems5[to] = tempI;
			player.bankItems5N[to] = tempN;
		} else if (player.bankingTab == 6) {
			int tempI = player.bankItems6[from];
			int tempN = player.bankItems6N[from];
			player.bankItems6[from] = player.bankItems6[to];
			player.bankItems6N[from] = player.bankItems6N[to];
			player.bankItems6[to] = tempI;
			player.bankItems6N[to] = tempN;
		} else if (player.bankingTab == 7) {
			int tempI = player.bankItems7[from];
			int tempN = player.bankItems7N[from];
			player.bankItems7[from] = player.bankItems7[to];
			player.bankItems7N[from] = player.bankItems7N[to];
			player.bankItems7[to] = tempI;
			player.bankItems7N[to] = tempN;
		} else if (player.bankingTab == 8) {
			int tempI = player.bankItems8[from];
			int tempN = player.bankItems8N[from];
			player.bankItems8[from] = player.bankItems8[to];
			player.bankItems8N[from] = player.bankItems8N[to];
			player.bankItems8[to] = tempI;
			player.bankItems8N[to] = tempN;
		}
	}

	public static boolean addItemToBank(Player player, int itemId, int amount, boolean updateBankVisual) {
		return addItemToBank(player, itemId, amount, updateBankVisual, -1);
	}
	/**
	 * Add item/s to the player's bank.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity to add to the player's bank.
	 * @param amount The amount of the itemID.
	 */
	public static boolean addItemToBank(Player player, int itemId, int amount, boolean updateBankVisual, int initialTab) {
		if (amount <= 0) {
			return false;
		}
		itemId++;
		// Items saved in bank are +1, so coins is 996 instead.
		itemId = ItemAssistant.getUnNotedItem(itemId - 1);
		if (getBankAmount(player, "REMAINING") == 0) {
			if (!hasItemInBank(player, itemId + 1)) {
				player.bankIsFullWhileUsingPreset = true;
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId) + ".");
				return false;
			}
		}
		int tab = getTabforItem(player, itemId);
		String result = bankItemInSpecificTab(player, tab, itemId, amount, updateBankVisual);
		updateBankingArrayDependingOnTab(initialTab, player);
		if (result.equals("TRUE")) {
			return true;
		} else if (result.equals("MAX STACK")) {
			return false;
		} else {
			boolean itemBanked = false;
			for (int index = 0; index < 9; index++) {
				if (index == tab) {
					continue;
				}
				result = bankItemInSpecificTab(player, index, itemId, amount, updateBankVisual);
				updateBankingArrayDependingOnTab(initialTab, player);
				if (result.equals("MAX STACK")) {
					return false;
				}
				if (result.equals("TRUE")) {
					itemBanked = true;
					break;
				}
			}
			return itemBanked;
		}
	}

	public static int addItemToBankGetAmount(Player player, int itemId, int amount, boolean updateBankVisual) {
		if (amount <= 0) {
			return 0;
		}
		itemId++;
		// Items saved in bank are +1, so coins is 996 instead.
		itemId = ItemAssistant.getUnNotedItem(itemId - 1);
		if (getBankAmount(player, "REMAINING") == 0) {
			if (!hasItemInBank(player, itemId + 1)) {
				player.bankIsFullWhileUsingPreset = true;
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank full, cannot bank: " + ItemAssistant.getItemName(itemId) + ".");
				return 0;
			}
		}
		int tab = getTabforItem(player, itemId);

		int amountAdded = bankItemInSpecificTabGetAmount(player, tab, itemId, amount, updateBankVisual);

		if (amountAdded > 0) {
			return amountAdded;
		} else if (amountAdded == 0) {
			return 0;
		} else {
			for (int index = 0; index < 9; index++) {
				if (index == tab) {
					continue;
				}
				int added = bankItemInSpecificTabGetAmount(player, index, itemId, amount, updateBankVisual);

				if (added == 0) {
					return 0;
				}
				if (added > 0) {
					return added;
				}
			}
			return 0;
		}
	}

	private static String bankItemInSpecificTab(Player player, int tab, int itemId, int amount, boolean updateBankVisual) {
		int bankingItems[] = new int[ServerConstants.BANK_SIZE];

		int bankingItemsN[] = new int[ServerConstants.BANK_SIZE];
		bankingItems = player.bankingItems;
		bankingItemsN = player.bankingItemsN;
		switch (tab) {
			case 0:
				player.bankingItems = player.bankItems;
				player.bankingItemsN = player.bankItemsN;
				break;
			case 1:
				player.bankingItems = player.bankItems1;
				player.bankingItemsN = player.bankItems1N;
				break;
			case 2:
				player.bankingItems = player.bankItems2;
				player.bankingItemsN = player.bankItems2N;
				break;
			case 3:
				player.bankingItems = player.bankItems3;
				player.bankingItemsN = player.bankItems3N;
				break;
			case 4:
				player.bankingItems = player.bankItems4;
				player.bankingItemsN = player.bankItems4N;
				break;
			case 5:
				player.bankingItems = player.bankItems5;
				player.bankingItemsN = player.bankItems5N;
				break;
			case 6:
				player.bankingItems = player.bankItems6;
				player.bankingItemsN = player.bankItems6N;
				break;
			case 7:
				player.bankingItems = player.bankItems7;
				player.bankingItemsN = player.bankItems7N;
				break;
			case 8:
				player.bankingItems = player.bankItems8;
				player.bankingItemsN = player.bankItems8N;
				break;
		}
		boolean bankedItem = false;

		// Attempt to bank in the existing item first.
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] == itemId + 1) {
				int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[i];
				if (amount > maximumAmount) {
					player.bankIsFullWhileUsingPreset = true;
					player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank stack amount is too much, cannot bank: " + ItemAssistant.getItemName(player.bankingItems[i] - 1) + ".");
					return "MAX STACK";
				}
				player.bankingItems[i] = itemId + 1;
				player.bankingItemsN[i] += amount;
				bankedItem = true;
				player.bankingItems = bankingItems;
				player.bankingItemsN = bankingItemsN;
				if (updateBankVisual) {
					player.bankUpdated = true;
				}
				return "TRUE";
			}
		}

		// Since there is no existing item, find an empty slot.
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] <= 0) {
				int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[i];
				if (amount > maximumAmount) {
					player.bankIsFullWhileUsingPreset = true;
					player.playerAssistant
							.sendMessage(ServerConstants.RED_COL + "Bank stack amount is too much, cannot bank: " + ItemAssistant.getItemName(player.bankingItems[i] - 1) + ".");
					return "MAX STACK";
				}
				player.bankingItems[i] = itemId + 1;
				player.bankingItemsN[i] += amount;
				bankedItem = true;
				player.bankingItems = bankingItems;
				player.bankingItemsN = bankingItemsN;
				if (updateBankVisual) {
					player.bankUpdated = true;
				}
				return "TRUE";
			}
		}
		if (bankedItem) {
			openUpBank(player, tab, false, false);
		}
		return Boolean.toString(bankedItem);
	}

	private static int bankItemInSpecificTabGetAmount(Player player, int tab, int itemId, int amount, boolean updateBankVisual) {
		int bankingItems[] = new int[ServerConstants.BANK_SIZE];

		int bankingItemsN[] = new int[ServerConstants.BANK_SIZE];
		bankingItems = player.bankingItems;
		bankingItemsN = player.bankingItemsN;
		switch (tab) {
			case 0:
				player.bankingItems = player.bankItems;
				player.bankingItemsN = player.bankItemsN;
				break;
			case 1:
				player.bankingItems = player.bankItems1;
				player.bankingItemsN = player.bankItems1N;
				break;
			case 2:
				player.bankingItems = player.bankItems2;
				player.bankingItemsN = player.bankItems2N;
				break;
			case 3:
				player.bankingItems = player.bankItems3;
				player.bankingItemsN = player.bankItems3N;
				break;
			case 4:
				player.bankingItems = player.bankItems4;
				player.bankingItemsN = player.bankItems4N;
				break;
			case 5:
				player.bankingItems = player.bankItems5;
				player.bankingItemsN = player.bankItems5N;
				break;
			case 6:
				player.bankingItems = player.bankItems6;
				player.bankingItemsN = player.bankItems6N;
				break;
			case 7:
				player.bankingItems = player.bankItems7;
				player.bankingItemsN = player.bankItems7N;
				break;
			case 8:
				player.bankingItems = player.bankItems8;
				player.bankingItemsN = player.bankItems8N;
				break;
		}
		boolean bankedItem = false;

		// Attempt to bank in the existing item first.
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] == itemId + 1) {
				int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[i];
				if (amount > maximumAmount) {
					player.bankIsFullWhileUsingPreset = true;
					player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Bank stack amount is too much, cannot bank: " + ItemAssistant.getItemName(player.bankingItems[i] - 1) + ".");
					return 0;
				}
				player.bankingItems[i] = itemId + 1;
				player.bankingItemsN[i] += amount;
				bankedItem = true;
				player.bankingItems = bankingItems;
				player.bankingItemsN = bankingItemsN;
				if (updateBankVisual) {
					player.bankUpdated = true;
				}
				return amount;
			}
		}

		// Since there is no existing item, find an empty slot.
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (player.bankingItems[i] <= 0) {
				int maximumAmount = Integer.MAX_VALUE - player.bankingItemsN[i];
				if (amount > maximumAmount) {
					player.bankIsFullWhileUsingPreset = true;
					player.playerAssistant
							.sendMessage(ServerConstants.RED_COL + "Bank stack amount is too much, cannot bank: " + ItemAssistant.getItemName(player.bankingItems[i] - 1) + ".");
					return 0;
				}
				player.bankingItems[i] = itemId + 1;
				player.bankingItemsN[i] += amount;
				bankedItem = true;
				player.bankingItems = bankingItems;
				player.bankingItemsN = bankingItemsN;
				if (updateBankVisual) {
					player.bankUpdated = true;
				}
				return amount;
			}
		}
		if (bankedItem) {
			openUpBank(player, tab, false, false);
		}
		return bankedItem ? amount : 0;
	}

	public static void search(Player player, String string, boolean ignoreSameString) {
		if (string.isEmpty()) {
			player.bankSearchString = "";
			player.setUsingBankSearch(false);
			Bank.resetBank(player, true);
			return;
		}
		if (string.equals(player.bankSearchString) && !ignoreSameString) {
			return;
		}
		player.getPA().updateBankSeparators(false);
		string = string.toLowerCase();
		String search = string;
		player.bankSearchString = string;
		player.bankSearchedItems.clear();
		player.setUsingBankSearch(true);
		for (int i = 0; i < player.bankItems.length; i++) {
			if (player.bankItems[i] != 0 && player.bankItemsN[i] > 0 && ItemAssistant.getItemName(player.bankItems[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems[i], player.bankItemsN[i]));
			}
		}
		for (int i = 0; i < player.bankItems1.length; i++) {
			if (player.bankItems1[i] != 0 && player.bankItems1N[i] > 0 && ItemAssistant.getItemName(player.bankItems1[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems1[i], player.bankItems1N[i]));
			}
		}
		for (int i = 0; i < player.bankItems2.length; i++) {
			if (player.bankItems2[i] != 0 && player.bankItems2N[i] > 0 && ItemAssistant.getItemName(player.bankItems2[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems2[i], player.bankItems2N[i]));
			}
		}
		for (int i = 0; i < player.bankItems3.length; i++) {
			if (player.bankItems3[i] != 0 && player.bankItems3N[i] > 0 && ItemAssistant.getItemName(player.bankItems3[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems3[i], player.bankItems3N[i]));
			}
		}
		for (int i = 0; i < player.bankItems4.length; i++) {
			if (player.bankItems4[i] != 0 && player.bankItems4N[i] > 0 && ItemAssistant.getItemName(player.bankItems4[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems4[i], player.bankItems4N[i]));
			}
		}
		for (int i = 0; i < player.bankItems5.length; i++) {
			if (player.bankItems5[i] != 0 && player.bankItems5N[i] > 0 && ItemAssistant.getItemName(player.bankItems5[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems5[i], player.bankItems5N[i]));
			}
		}
		for (int i = 0; i < player.bankItems6.length; i++) {
			if (player.bankItems6[i] != 0 && player.bankItems6N[i] > 0 && ItemAssistant.getItemName(player.bankItems6[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems6[i], player.bankItems6N[i]));
			}
		}
		for (int i = 0; i < player.bankItems7.length; i++) {
			if (player.bankItems7[i] != 0 && player.bankItems7N[i] > 0 && ItemAssistant.getItemName(player.bankItems7[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems7[i], player.bankItems7N[i]));
			}
		}
		for (int i = 0; i < player.bankItems8.length; i++) {
			if (player.bankItems8[i] != 0 && player.bankItems8N[i] > 0 && ItemAssistant.getItemName(player.bankItems8[i] - 1).toLowerCase().contains(search)) {
				player.bankSearchedItems.add(new GameItem(player.bankItems8[i], player.bankItems8N[i]));
			}
		}
		setBankScrollLengthNormal(player, true, false);
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(5382);
		player.getOutStream().writeWord(ServerConstants.BANK_SIZE);
		int amount = 0;
		int itemId = 0;
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			if (i < player.bankSearchedItems.size()) {
				itemId = player.bankSearchedItems.get(i).getId();
				amount = player.bankSearchedItems.get(i).getAmount();
			}

			// When withdrawing the last stack of an item, the quantity will go to 0, but not the bankItem number.
			// So we are required to set it to 0, maybe because of potential dupes.
			if (amount < 1) {
				itemId = 0;
				amount = 0;
			}
			if (amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(amount);
			} else {
				player.getOutStream().writeByte(amount);
			}
			if (i < player.bankSearchedItems.size()) {
				player.getOutStream().writeWordBigEndianA(itemId);
			} else {
				player.getOutStream().writeWordBigEndianA(0);
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
		Bank.updateAmount(player);

	}

	public static void stopSearch(Player player, boolean openTab) {
		player.setUsingBankSearch(false);
		if (openTab) {
			Bank.openCorrectTab(player, player.originalTab, true);
			player.bankUpdated = true;
			;
		}
	}

	public static void createPlaceholder(Player player, int containerIndex) {
		if (!hasBankingRequirements(player, false)) {
			return;
		}
		if (containerIndex < 0 || containerIndex > player.bankingItems.length - 1) {
			return;
		}
		int itemId = player.bankingItems[containerIndex];

		int itemAmount = player.bankingItemsN[containerIndex];
		if (player.isUsingBankSearch()) {
			if (containerIndex > player.bankSearchedItems.size() - 1) {
				return;
			}
			itemId = player.bankSearchedItems.get(containerIndex).getId();
			itemAmount = player.bankSearchedItems.get(containerIndex).getAmount();
		}
		if (itemId < 0) {
			return;
		}
		if (itemAmount <= 0) {
			player.getPA().sendMessage("There is already a place holder here.");
			return;
		}
		withdrawFromBank(player, itemId, containerIndex, itemAmount, false, true);

		int itemAmountAfterWithdraw = player.bankingItemsN[containerIndex];

		if (itemAmountAfterWithdraw > 0) {
			return;
		}
		if (player.isUsingBankSearch()) {
			hasItemInBank(player, itemId + 1);
			containerIndex = player.itemInBankSlot;
		}
		player.bankingItems[containerIndex] = itemId;
		player.bankingItemsN[containerIndex] = 0;
		//resetBank(player, false);
	}

	public static boolean placeholderExists(Player player, int containerIndex) {
		if (!hasBankingRequirements(player, false)) {
			return false;
		}
		if (containerIndex < 0 || containerIndex > player.bankingItems.length - 1) {
			return false;
		}
		int itemAtIndex = player.bankingItems[containerIndex];

		int amountAtIndex = player.bankingItemsN[containerIndex];

		return itemAtIndex != -1 && amountAtIndex == 0;
	}

	public static void releasePlaceholder(Player player, int containerIndex, int tabIdToUpdate) {
		if (containerIndex < 0 || containerIndex > player.bankingItems.length - 1) {
			return;
		}
		int itemAtIndex = player.bankingItems[containerIndex];

		int amountAtIndex = player.bankingItemsN[containerIndex];

		if (amountAtIndex > 0 || itemAtIndex == -1) {
			return;
		}
		player.bankingItems[containerIndex] = 0;
		player.bankingItemsN[containerIndex] = 0;
		//player.bankUpdated = false;
		//player.bankingTab = 0;
		//resetBank(player, false, tabIdToUpdate);
	}
	
	public static void sendDepositBox(Player player) {
		player.setActionIdUsed((15000 + 6948));
		player.getPA().sendFrame126("                                Deposit Box", 7421);
		player.getPA().sendFrame248(GameType.isPreEoc() ? 33_444  : 4465, 197);
		ItemAssistant.resetItems(player, 7423);
	}
}
