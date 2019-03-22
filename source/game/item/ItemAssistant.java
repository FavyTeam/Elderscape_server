package game.item;

import java.util.stream.IntStream;

import com.twilio.twiml.Play;
import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotContent;
import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;
import game.content.achievement.AchievementShop;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.commands.NormalCommand;
import game.content.degrading.DegradingItem;
import game.content.degrading.DegradingItemJSONLoader;
import game.content.donator.AfkChair;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.item.ItemInteractionManager;
import game.content.miscellaneous.ClaimPrize;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.TradeAndDuel;
import game.content.miscellaneous.Transform;
import game.content.miscellaneous.TransformedOnDeathItems;
import game.content.skilling.Skilling;
import game.content.tradingpost.TradingPost;
import game.content.tradingpost.TradingPostItems;
import game.npc.pet.PetData;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;

public class ItemAssistant {

	public static void addAnywhere(Player player, ItemContainer container) {
		container.forNonNull((index, item) -> addAnywhere(player, item));
	}

	public static void addAnywhere(Player player, GameItem item) {
		int added = addItemGetAmount(player, item.getId(), item.getAmount());

		if (added == item.getAmount()) {
			return;
		}
		int remaining = item.getAmount() - added;

		int addedToBank = Bank.addItemToBankGetAmount(player, item.getId(), remaining, false);

		if (addedToBank == remaining) {
			return;
		}
		giveItemUnderPlayer(player, item.getId(), remaining - addedToBank);
	}

	public static boolean isImmortalDonatorItem(int itemId) {
		if (Misc.arrayHasNumber(ServerConstants.getImmortalDonatorItems(), itemId)) {
			return true;
		}
		return false;
	}

	/**
	 * Add it to the player's inventory, if full, add to bank, if full, drop on floor only if player is not in an instance such as tournament, if they are, add to item pending list.
	 */
	public static void giveItemToOnlinePlayer(Player player, int itemId, int amount) {
		if (!Area.inAreaWhereItemsGetDeletedUponExit(player)) {
			if (ItemAssistant.addItem(player, itemId, amount)) {
				return;
			}
		}
		if (Bank.addItemToBank(player, itemId, amount, false)) {
			return;
		}
		if (!Area.inAreaWhereItemsGetDeletedUponExit(player)) {
			giveItemUnderPlayer(player, itemId, amount);
			return;
		}
		ClaimPrize.itemRewards.add(player.getLowercaseName() + "-" + itemId + "-" + amount + "-" + -1);
	}

	public static boolean isNoted(int itemId) {
		if (ItemAssistant.nulledItem(itemId)) {
			return false;
		}
		return ItemDefinition.getDefinitions()[itemId].note;
	}

	public static boolean wearing(Player player, int item, int slot) {
		return wearing(player, item, 1, slot);
	}

	public static boolean wearing(Player player, int item, int amount, int slot) {
		if (slot < 0 || slot > player.playerEquipment.length - 1) {
			return false;
		}
		return player.playerEquipment[slot] == item && player.playerEquipmentN[slot] >= amount;
	}

	public static boolean hasRoomInInventory(Player player, GameItem item) {
		int freeSlots = getFreeInventorySlots(player);

		if (!item.isStackable() && (freeSlots == 0 || freeSlots < item.getAmount())) {
			return false;
		}

		if (item.isStackable()) {
			int amountInInventory = getItemAmount(player, item.getAmount());

			if (amountInInventory == Integer.MAX_VALUE) {
				return false;
			}
			if (amountInInventory == 0 && freeSlots == 0) {
				return false;
			}
			if ((long) amountInInventory + (long) item.getAmount() > Integer.MAX_VALUE) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean canAdd(Player player, int itemId, int amount) {
		if(ItemDefinition.getDefinitions()[itemId].stackable) {
			if (hasItemInInventory(player, itemId) || getFreeInventorySlots(player) > 0) {
				return true;
			}
		} else {
			return getFreeInventorySlots(player) > 0;
		}
		return false;
	}


	public static String getAccountBankValue(Player player) {
		return Misc.formatRunescapeStyle(getAccountBankValueLong(player));
	}

	public static long getAccountBankValueLongWithDelay(Player player) {
		if (System.currentTimeMillis() - player.bankWealthCheckedTime <= 120_000) {
			return player.bankWealthCheckedAmount;
		}
		player.bankWealthCheckedTime = System.currentTimeMillis();
		player.bankWealthCheckedAmount = getAccountBankValueLong(player);
		return player.bankWealthCheckedAmount;
	}
	public static long getAccountBankValueLong(Player player) {
		int itemId = 0;
		int amount = 0;
		long totalAccountWealth = 0;
		for (int i = 0; i < player.playerEquipment.length; i++) {
			itemId = player.playerEquipment[i];
			amount = player.playerEquipmentN[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			itemId = player.playerItems[i] - 1;
			amount = player.playerItemsN[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < player.lootingBagStorageItemId.length; i++) {
			itemId = player.lootingBagStorageItemId[i];
			amount = player.lootingBagStorageItemAmount[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int index = 0; index < DiceSystem.depositVault.size(); index++) {
			String parse[] = DiceSystem.depositVault.get(index).split("-");
			if (parse[0].equalsIgnoreCase(player.getPlayerName())) {
				int vaultAmount = Integer.parseInt(parse[1]);
				totalAccountWealth += vaultAmount;
				break;
			}
		}
		for (TradingPost data : TradingPost.tradingPostData) {
			if (data.getName().equals(player.getLowercaseName())) {
				if (data.getAction().equals("SELLING")) {
					totalAccountWealth += (data.getItemAmountLeft() * ServerConstants.getItemValue(7478));
				} else {
					totalAccountWealth += (data.getItemAmountLeft() * data.getPrice());
				}
			}
		}

		for (TradingPostItems data : TradingPostItems.tradingPostItemsData) {
			if (data.getName().equals(player.getLowercaseName())) {
				totalAccountWealth += ServerConstants.getItemValue(data.getItemId()) * data.getItemAmount();
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems[i] - 1;
			amount = player.bankItemsN[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems1[i] - 1;
			amount = player.bankItems1N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems2[i] - 1;
			amount = player.bankItems2N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems3[i] - 1;
			amount = player.bankItems3N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems4[i] - 1;
			amount = player.bankItems4N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems5[i] - 1;
			amount = player.bankItems5N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems6[i] - 1;
			amount = player.bankItems6N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems7[i] - 1;
			amount = player.bankItems7N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			itemId = player.bankItems8[i] - 1;
			amount = player.bankItems8N[i];
			if (itemId > 0 && amount > 0) {
				totalAccountWealth += ServerConstants.getItemValue(itemId) * amount;
			}
		}
		return totalAccountWealth;
	}

	/**
	 * Combine two items together.
	 */
	public static boolean combineTwoItems(Player player, int itemUsedId, int itemUsedWithId, int item1, int item2, int result, String text, boolean statement, int offset1,
	                                      int offset2) {
		if (ItemAssistant.hasTwoItems(player, itemUsedId, itemUsedWithId, item1, item2)) {
			ItemAssistant.deleteItemFromInventory(player, item1, 1);
			ItemAssistant.deleteItemFromInventory(player, item2, 1);
			ItemAssistant.addItem(player, result, 1);
			if (statement) {
				player.getDH().sendItemChat("", text, result, 200, offset1, offset2);
			} else {
				player.getPA().sendMessage(text);
			}
			return true;
		}
		return false;
	}

	public static boolean nulledItem(int itemId) {
		if (itemId <= 0) {
			return true;
		}
		if (itemId > ItemDefinition.getDefinitions().length - 1) {
			return true;
		}
		if (ItemDefinition.getDefinitions()[itemId] == null) {
			if (ServerConfiguration.DEBUG_MODE) {
				Misc.print("case " + itemId + ": // " + ItemAssistant.getItemName(itemId));
			}
			return true;
		}
		return false;
	}

	/**
	 * True, to update equipment. To avoid unnecessary update duo to withdrawing items from bank etc..
	 */
	public static boolean updateEquipment(Player player) {
		boolean updated = false;
		for (int equipmentSlot = 0; equipmentSlot < player.playerEquipment.length; equipmentSlot++) {
			if (player.playerEquipmentAfterLastTick[equipmentSlot] != player.playerEquipment[equipmentSlot]
			    || player.playerEquipmentAmountAfterLastTick[equipmentSlot] != player.playerEquipmentN[equipmentSlot]) {
				updated = true;
				if (equipmentSlot == ServerConstants.WEAPON_SLOT) {
					BotContent.adjustWeaponAttackStyle(player);
					if (player.getWieldedWeapon() != player.weaponSpecialUpdatedId) {
						CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
					}
				}
				if (!player.bot) {
					updateSlot(player, equipmentSlot);
				}
			}
		}
		return updated;
	}

	/**
	 * Save toggle of equipment after last update.
	 *
	 * @param player
	 */
	public static void saveEquipment(Player player) {
		for (int i = 0; i < player.playerEquipment.length; i++) {
			player.playerEquipmentAfterLastTick[i] = player.playerEquipment[i];
			player.playerEquipmentAmountAfterLastTick[i] = player.playerEquipmentN[i];
		}
	}


	/**
	 * @param itemId The itemId to check if noted.
	 * @return The un-noted form of the itemId given.
	 */
	public static int getUnNotedItem(int itemId) {
		ItemDefinition instance = ItemDefinition.getDefinitions()[itemId];
		if (instance == null) {
			return itemId;
		}
		if (instance.unNotedId == 0 && instance.note) {
			return itemId - 1;
		}

		if (instance.unNotedId > 0) {
			return instance.unNotedId;
		}
		return itemId;
	}

	public static int getNotedItem(int itemId) {
		ItemDefinition instance = ItemDefinition.getDefinitions()[itemId];
		ItemDefinition secondInstance = ItemDefinition.getDefinitions()[itemId + 1];
		if (secondInstance != null) {
			if (instance.notedId == 0 && secondInstance.note) {
				return itemId + 1;
			}
		}

		if (instance.notedId > 0) {
			return instance.notedId;
		}

		return itemId;
	}

	/**
	 * True, if the player is wearing equipment.
	 *
	 * @param player The associated player.
	 * @return True, if the player is wearing equipment.
	 */
	public static boolean hasEquipment(Player player) {
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] > 0 && player.playerEquipmentN[i] > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the player has the item in the inventory, if so, then delete the specified amount.
	 * <p>
	 * Only use for stackable items, if not stackable item, it will delete all in inventory.
	 *
	 * @param player
	 * @param itemId
	 * @param itemAmount
	 */
	public static boolean checkAndDeleteStackableFromInventory(Player player, int itemId, int itemAmount) {
		boolean found = false;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId + 1 && player.playerItemsN[i] >= itemAmount) {
				player.playerItemsN[i] -= itemAmount;
				if (player.playerItemsN[i] <= 0) {
					player.playerItemsN[i] = 0;
					player.playerItems[i] = 0;
				}
				found = true;
				break;
			}
		}
		if (found) {
			player.setInventoryUpdate(true);
		}
		return found;
	}

	/**
	 * Give the specified itemId on the ground, under the player.
	 * <p>
	 * Note: the item will not appear instantly to other players.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity to show on ground.
	 * @param itemAmount Amount of the item identity to show on ground.
	 */
	public static void giveItemUnderPlayer(Player player, int itemId, int itemAmount) {
		player.getPA().sendMessage(ServerConstants.RED_COL + "The " + ItemAssistant.getItemName(itemId) + " has been dropped on the floor!");
		Server.itemHandler
				.createGroundItem(player, itemId, player.getX(), player.getY(), player.getHeight(), itemAmount, false, 0, true, player.getPlayerName(), player.getPlayerName(),
						player.addressIp, player.addressUid, "giveItemUnderPlayer");
	}

	/**
	 * @return True, if the player has an untradeable item that can only have 1 quantity on the account.
	 * Such as Max capes, Clue scroll, God capes, Boss pets etc.
	 */
	public static boolean hasSingularUntradeableItem(Player player, int itemId) {
		for (int index = 0; index < player.singularUntradeableItemsOwned.size(); index++) {
			int itemList = Integer.parseInt(player.singularUntradeableItemsOwned.get(index));
			if (itemId == itemList) {
				return true;
			}
		}
		return false;
	}


	public static void pickUpSingularUntradeableItem(Player player, int itemId) {
		if (itemId != 2677) {
			return;
		}

		if (player.getPlayerName().equals("Arab Unity")) {
			NormalCommand.clueScrollDebug.add("Pickup1.");
		}

		if (hasSingularUntradeableItem(player, itemId)) {
			return;
		}

		if (player.getPlayerName().equals("Arab Unity")) {
			NormalCommand.clueScrollDebug.add("Pickup2.");
		}
		player.singularUntradeableItemsOwned.add(Integer.toString(itemId));
	}

	/**
	 * When using this and it drops to ground, Iron men can pick it up.
	 * Give the player an item in the inventory, if the inventory is full, drop the item under the player.
	 *
	 * @param player The associated player.
	 * @param itemId The identity of the item.
	 * @param amount The amount of the item.
	 */
	public static void addItemToInventoryOrDrop(Player player, int itemId, int amount) {
		if (ItemAssistant.addItem(player, itemId, amount)) {
		} else if (ItemAssistant.hasItemInInventory(player, itemId) && ItemDefinition.getDefinitions()[itemId].stackable && ItemAssistant.addItem(player, itemId, amount)) {
		} else {
			// Add as much to inventory before dropping on floor.
			int toDrop = amount;
			if (!ItemAssistant.isStackable(itemId)) {
				int space = ItemAssistant.getFreeInventorySlots(player);
				if (space > 0) {
					if (space > toDrop) {
						space = toDrop;
					}
					ItemAssistant.addItem(player, itemId, space);
					toDrop -= space;
				}
			}
			giveItemUnderPlayer(player, itemId, toDrop);
		}
	}

	/**
	 * @param player The associated player.
	 * @param itemId The item identity to search for.
	 * @param equipmentSlot The equipment slot to search in.
	 * @return True, if the player has the itemId equipped in the required equipmentSlot
	 */
	public static boolean hasItemEquippedSlot(Player player, int itemId, int equipmentSlot) {
		return player.playerEquipment[equipmentSlot] == itemId;
	}

	/**
	 * @param player The associated player.
	 * @param itemId The itemId to check for in the player's equipped items.
	 * @return True, if the player has the itemId equipped.
	 */
	public static boolean hasItemEquipped(Player player, int itemId) {
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] == itemId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Replace the player's chosen equipment slot with an item.
	 *
	 * @param player The associated player.
	 * @param equipmentSlot The equipment slot ID to put the itemId in.
	 * @param itemId The item ID to put in the equipmentSlot
	 */
	public static void replaceEquipmentSlot(Player player, int equipmentSlot, int itemId, int amount, boolean checkIfSlotEmpty) {
		if (player.playerEquipment[equipmentSlot] <= 0 && checkIfSlotEmpty) {
			return;
		}
		player.playerEquipment[equipmentSlot] = itemId;
		player.playerEquipmentN[equipmentSlot] = amount;
		if (itemId <= 0) {
			player.clearEquipmentSlot(player, equipmentSlot);
		}
	}

	/**
	 * @param itemId The item identity to check.
	 * @return The amount of itemId the player has equipped.
	 */
	public static int getWornItemAmount(Player player, int itemId) {
		for (int i = 0; i < 12; i++) {
			if (player.playerEquipment[i] == itemId) {
				return player.playerEquipmentN[i];
			}
		}
		return 0;
	}

	/**
	 * Update the writeFrame.
	 * <p> Also used for updating the inventory visuals.
	 *
	 * @param player The associated player.
	 * @param writeFrame The ID to update, 3214 is inventory.
	 */
	public static void resetItems(Player player, int writeFrame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrameVarSizeWord(53);
			player.getOutStream().writeWord(writeFrame);
			player.getOutStream().writeWord(player.playerItems.length);
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItemsN[i] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.playerItemsN[i]);
				} else {
					player.getOutStream().writeByte(player.playerItemsN[i]);
				}
				player.getOutStream().writeWordBigEndianA(player.playerItems[i]);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	/**
	 * Update equipment interface.
	 */
	public static void updateEquipmentBonusInterface(Player player) {
		int offset = 0;
		String send = "";
		for (int i = 0; i < player.playerBonus.length; i++) {
			send = (player.playerBonus[i] >= 0) ?
					       ServerConstants.EQUIPMENT_BONUS[i] + ": +" + player.playerBonus[i] :
					       ServerConstants.EQUIPMENT_BONUS[i] + ": -" + java.lang.Math.abs(player.playerBonus[i]);
			if (i == 10) {
				offset = 1;
			}
			player.getPA().sendFrame126(send, (1675 + i + offset));
		}
		player.getPA().sendFrame126("Ranged strength: +" + RangedFormula.getRangedStrength(player), 15115);
		player.getPA().sendFrame126("Magic damage: +" + (int) (MagicFormula.getMagicPercentageDamageBonus(player) * 100.0) + "%", 15116);
	}

	/**
	 * Delete all worn items and items in inventory.
	 **/
	public static void deleteAllItems(Player player) {
		deleteEquipment(player);
		deleteInventory(player);
	}

	public static void deleteInventory(Player player) {
		for (int i = 0; i < player.playerItems.length; i++) {
			deleteItemFromInventory(player, player.playerItems[i] - 1, getItemSlot(player, player.playerItems[i] - 1), player.playerItemsN[i]);
		}
	}

	public static void deleteEquipment(Player player) {
		for (int i1 = 0; i1 < player.playerEquipment.length; i1++) {
			deleteEquipment(player, player.playerEquipment[i1], i1);
		}
	}

	/**
	 * Replace the coloured item/imbued ring with a normal version on death.
	 *
	 * @param killer The player who got the kill.
	 * @param victim The player who died.
	 * @param itemId The item id that could be transformed into another item.
	 */
	public static int getNormalItemVersion(Player killer, Player victim, int itemId) {
		if (killer.getPlayerName().equals(victim.getPlayerName())) {
			return itemId;
		}

		for (TransformedOnDeathItems.TransformedOnDeathData data : TransformedOnDeathItems.TransformedOnDeathData.values()) {
			if (itemId == data.getSpecialId()) {
				itemId = data.getNormalId();
				break;
			}
		}

		return itemId;
	}

	public static boolean isItemToUntradeableShopOnDeath(int itemId) {
		if (GameType.isOsrsPvp() && Misc.arrayHasNumber(ServerConstants.ITEMS_DROP_BLOOD_MONEY_OSRS_PVP, itemId)) {
			return true;
		}
		return false;
	}

	private static boolean isItemUntradeable(int itemId) {
		return GameType.isOsrsPvp() ? ItemDefinition.getDefinitions()[itemId].untradeableOsrsPvp : GameType.isOsrsEco() ? ItemDefinition.getDefinitions()[itemId].untradeableOsrsEco : ItemDefinition.getDefinitions()[itemId].untradeablePreEoc;
	}

	public static boolean isItemToInventoryOnDeath(int itemId) {
		if (ItemAssistant.isImmortalDonatorItem(itemId)) {
			return true;
		}
		if (isItemUntradeable(itemId)) {
			return true;
		}
		if (GameType.isOsrsPvp() && ItemDefinition.getDefinitions()[itemId].toInventoryOnDeathOsrsPvp) {
			return true;
		}
		if (GameType.isPreEoc() && ItemDefinition.getDefinitions()[itemId].toInventoryOnDeathPreEoc) {
			return true;
		}
		for (int a = 0; a < PetData.petData.length; a++) {
			if (PetData.petData[a][1] == itemId) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDestroyItem(int itemId) {
		if (ItemAssistant.isImmortalDonatorItem(itemId)) {
			return true;
		}

		if (ItemDefinition.getDefinitions()[itemId].destroyOnDrop) {
			return true;
		}
		/*
		 * The definition
		 */
		DegradingItem item = DegradingItemJSONLoader.getDegradables().get(itemId);
		/*
		 * The item
		 */
		if (item != null) {
			return item.getDropItem() > 0;
		}
		return false;
	}

	public static boolean itemDropsOnFloorAndOnlyAppearsToOwner(int itemId) {
		if (isItemUntradeable(itemId)) {
			return true;
		}
		return false;
	}

	public static boolean cannotTradeAndStakeItemItem(int itemId) {
		if (ItemAssistant.isImmortalDonatorItem(itemId)) {
			return true;
		}
		if (isItemUntradeable(itemId)) {
			return true;
		}
		for (int a = 0; a < PetData.petData.length; a++) {
			if (PetData.petData[a][1] == itemId) {
				return true;
			}
		}
		if (GameType.isOsrsEco()) {
			for (TransformedOnDeathItems.TransformedOnDeathData data : TransformedOnDeathItems.TransformedOnDeathData.values()) {
				if (itemId == data.getSpecialId()) {
					return true;
				}
			}
		}
		return false;
	}

	public static void addItemToInventory(Player player, int itemId, int amount, int slot, boolean update) {
		if (itemId <= 0) {
			return;
		}
		if (amount <= 0) {
			return;
		}
		// Incase item is stackable.
		if (ItemAssistant.hasItemInInventory(player, itemId) && ItemDefinition.getDefinitions()[itemId].stackable) {
			ItemAssistant.addItem(player, itemId, amount);
		} else {
			player.playerItems[slot] = (itemId + 1);
			player.playerItemsN[slot] = amount;
		}

		if (update) {
			player.setInventoryUpdate(true);
		}
	}


	/**
	 * Add item/s to the player's inventory.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity to add to the inventory.
	 * @param amount The amount of the itemId.
	 * @return True if the player has enough free slots to receive the items.
	 */
	public static boolean addItem(Player player, int itemId, int amount) {
		boolean message = true;
		if (amount <= 0) {
			return false;
		}
		if (itemId <= 0) {
			return false;
		}
		if (ItemDefinition.getDefinitions()[itemId] == null) {
			if (player.isAdministratorRank()) {
				for (int i = 0; i < player.playerItems.length; i++) {
					if (player.playerItems[i] <= 0) {
						player.setInventoryUpdate(true);
						player.playerItems[i] = itemId + 1;
						if (amount < ServerConstants.MAX_ITEM_AMOUNT && amount > -1) {
							player.playerItemsN[i] = 1;
							if (amount > 1) {
								addItem(player, itemId, amount - 1);
								return true;
							}
						} else {
							player.playerItemsN[i] = ServerConstants.MAX_ITEM_AMOUNT;
						}
						return true;
					}
				}
			}
			Misc.printDontSave("Item is null: " + itemId);
			return false;
		}
		if (getFreeInventorySlots(player) == 0 && !ItemDefinition.getDefinitions()[itemId].stackable) {
			if (message) {
				player.playerAssistant.sendMessage("Not enough inventory space.");
			}
			return false;
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId + 1 && ItemDefinition.getDefinitions()[itemId].stackable && player.playerItems[i] > 0) {
				if (player.getOutStream() != null && player != null && !player.bot) {
					player.setInventoryUpdate(true);
				}
				player.playerItems[i] = (itemId + 1);
				if (player.playerItemsN[i] + amount <= ServerConstants.MAX_ITEM_AMOUNT && player.playerItemsN[i] + amount > -1) {
					player.playerItemsN[i] += amount;
				} else {
					if (message) {
						player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Maximum item stack, cannot add anymore: " + ItemAssistant.getItemName(player.playerItems[i] - 1) + ".");
					}
					return false;
				}
				return true;
			}
		}
		if (amount > 1 && !ItemDefinition.getDefinitions()[itemId].stackable) {
			int space = ItemAssistant.getFreeInventorySlots(player);
			if (amount > space) {
				if (message) {
					player.playerAssistant.sendMessage("Not enough inventory space.");
				}
				return false;
			}
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] <= 0) {
				player.setInventoryUpdate(true);
				player.playerItems[i] = itemId + 1;
				if (amount <= ServerConstants.MAX_ITEM_AMOUNT && amount > -1) {
					player.playerItemsN[i] = 1;
					if (amount > 1) {
						addItem(player, itemId, amount - 1);
						return true;
					}
				}
				return true;
			}
		}
		if (message) {
			player.playerAssistant.sendMessage("Not enough inventory space.");
		}
		return false;
	}

	public static int addItemGetAmount(Player player, int itemId, int amount) {
		if (amount <= 0) {
			return 0;
		}
		if (itemId <= 0) {
			return 0;
		}
		if (ItemDefinition.getDefinitions()[itemId] == null) {
			if (player.isAdministratorRank()) {
				for (int i = 0; i < player.playerItems.length; i++) {
					if (player.playerItems[i] <= 0) {
						player.setInventoryUpdate(true);
						player.playerItems[i] = itemId + 1;
						if (amount < ServerConstants.MAX_ITEM_AMOUNT) {
							player.playerItemsN[i] = 1;
							if (amount > 1) {
								return addItemGetAmount(player, itemId, amount - 1);
							}
						} else {
							player.playerItemsN[i] = ServerConstants.MAX_ITEM_AMOUNT;
						}
						return amount - player.playerItemsN[i];
					}
				}
			}
			Misc.printDontSave("Item is null: " + itemId);
			return 0;
		}
		if (getFreeInventorySlots(player) == 0 && !ItemDefinition.getDefinitions()[itemId].stackable) {
			player.playerAssistant.sendMessage("Not enough inventory space.");
			return 0;
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId + 1 && ItemDefinition.getDefinitions()[itemId].stackable && player.playerItems[i] > 0) {
				if (player.getOutStream() != null && !player.bot) {
					player.setInventoryUpdate(true);
				}
				if (player.playerItemsN[i] == Integer.MAX_VALUE) {
					return 0;
				}
				player.playerItems[i] = (itemId + 1);

				if (player.playerItemsN[i] + amount <= ServerConstants.MAX_ITEM_AMOUNT && player.playerItemsN[i] + amount > -1) {
					player.playerItemsN[i] += amount;
					return amount;
				} else {
					player.playerItemsN[i] += Integer.MAX_VALUE - player.playerItemsN[i];
					return Integer.MAX_VALUE - player.playerItemsN[i];
				}
			}
		}

		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] <= 0) {
				player.setInventoryUpdate(true);
				player.playerItems[i] = itemId + 1;
				player.playerItemsN[i] = 1;
				if (amount > 1) {
					return 1 + addItemGetAmount(player, itemId, amount - 1);
				}
				return 1;
			}
		}
		player.playerAssistant.sendMessage("Not enough inventory space.");
		return 0;
	}

	/**
	 * Calculate the equipment bonuses.
	 */
	public static void calculateEquipmentBonuses(Player player) {
		for (int i = 0; i < player.playerBonus.length; i++) {
			player.playerBonus[i] = 0;
		}

		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] > -1) {
				int itemId = player.playerEquipment[i];
				if (ItemDefinition.getDefinitions()[itemId] != null) {
					for (int k = 0; k < player.playerBonus.length; k++) {
						player.playerBonus[k] += ItemDefinition.getDefinitions()[itemId].bonuses[k];
					}
				}
			}
		}
	}

	private static final String[] TWO_HANDED_WEAPONS = {"hand cannon", "heavy casket",
			"clueless scroll", "large spade", "dinh's bulwark", "3rd age bow", "toxic blowpipe",
			"twisted bow", "ballista", "halberd", "karil", "verac", "guthan", "dharok", "torag",
			"longbow", "shortbow", "dark bow", "godsword", "saradomin sword", "blessed sword", "2h",
			"spear", "maul", "tzhaar-ket-om", "dragon claws", "barrelchest anchor", "boxing gloves",
		"toktz-mej-tal",
		"giant present",
		"club",
		"scythe",
		"crystal bow",
		"craw's bow"};
	/**
	 * @param itemId The ID of the item to check if it's 2-handed.
	 * @return True, if the itemId is a 2-handed weapon.
	 */
	public static boolean is2handed(int itemId) {
		String itemName = ItemAssistant.getItemName(itemId);
		itemName = itemName.toLowerCase();
		for (int i = 0; i < TWO_HANDED_WEAPONS.length; i++) {
			if (itemName.contains(TWO_HANDED_WEAPONS[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean canWearSpecialItemOsrs(Player player, int itemId) {
		if (!GameType.isOsrs()) {
			return true;
		}
		switch (itemId) {
			// Bone bracelet, h'ween reward
			case 16096:
				Transform.npcTransform(player, 11073 + Misc.random(4));
				return true;
			// Easter ring
			case 7927:
				Transform.npcTransform(player, 5538 + Misc.random(5));
				return true;
			// Ring of nature
			case 20005:
				Transform.npcTransform(player, 7314);
				return true;
			// Ring of nature
			case 20017:
				Transform.npcTransform(player, 7315);
				return true;
			// Ring of stone
			case 6583:
				Transform.npcTransform(player, 2188);
				return true;
			// Ring of planking
			case 16076:
				Transform.npcTransform(player, 11061);
				player.forcedChat("Hello everyone, I'm " + ServerConstants.getServerName() + "'s #1 planker", false, false);
				return true;
			// Bonemeal, so players cannot wear it.
			case 6810:
				return false;

			// Max capes and hoods.
			case 13281:
			case 13280:
			case 13338:
			case 13337:
			case 13332:
			case 13331:
			case 13329:
			case 13330:
			case 13334:
			case 13333:
			case 13336:
			case 13335:
			case 21282:
			case 21285:
			case 21776: //Imbued sara max cape
			case 21784: //Imbued guthix max cape
			case 21780: //Imbued zammy max cape
			case 21778: //Imbued sara max hood
			case 21786: //Imbued guthix max hood
			case 21782: //Imbued zammy max hood
				if (!player.announceMaxLevel && !player.isUberDonator() && !player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You need 2080 total level to wear this cape.");
					return false;
				}
				break;
		}
		return true;
	}

	public static boolean wearItem(Player player, int itemId, int slot) {
		if (ItemAssistant.isNulledSlot(slot)) {
			return false;
		}
		int itemEquipmentSlot = 0;

		player.justTransformed = false;
		if (!canWearSpecialItemOsrs(player, itemId)) {
			return false;
		}
		if (player.playerItems[slot] != (itemId + 1)) {
			return false;
		}
		String itemName = getItemName(itemId).toLowerCase();
		ItemRequirement.setItemRequirements(player, itemName, itemId);
		itemEquipmentSlot = ItemSlot.getItemEquipmentSlot(itemName, itemId);

		if (!ItemInteractionManager.handleEquipItem(player, itemId, slot)) {
			player.sendDebugMessage("This item does not support item interactions!");
			return false;
		}
		if (ServerConfiguration.DEBUG_MODE && itemEquipmentSlot == -1) {
			Misc.print("case " + itemId + ": // " + ItemAssistant.getItemName(itemId));
		}
		// Flag for wearing invalid items like coins from packet abuse.
		if (itemEquipmentSlot == -1) {
			return false;
		}

		int wearAmount = player.playerItemsN[slot];
		if (wearAmount < 1) {
			return false;
		}
		if (player.duelRule[11] && itemEquipmentSlot == ServerConstants.HEAD_SLOT) {
			player.playerAssistant.sendMessage("Wearing hats has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[12] && itemEquipmentSlot == ServerConstants.CAPE_SLOT) {
			player.playerAssistant.sendMessage("Wearing capes has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[13] && itemEquipmentSlot == ServerConstants.AMULET_SLOT) {
			player.playerAssistant.sendMessage("Wearing amulets has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[14] && itemEquipmentSlot == ServerConstants.WEAPON_SLOT) {
			player.playerAssistant.sendMessage("Wielding weapons has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[15] && itemEquipmentSlot == ServerConstants.BODY_SLOT) {
			player.playerAssistant.sendMessage("Wearing bodies has been disabled in this duel!");
			return false;
		}
		if ((player.duelRule[16] && itemEquipmentSlot == ServerConstants.SHIELD_SLOT) || (player.duelRule[16] && is2handed(itemId))) {
			player.playerAssistant.sendMessage("Wearing shield has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[17] && itemEquipmentSlot == ServerConstants.LEG_SLOT) {
			player.playerAssistant.sendMessage("Wearing legs has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[18] && itemEquipmentSlot == ServerConstants.HAND_SLOT) {
			player.playerAssistant.sendMessage("Wearing gloves has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[19] && itemEquipmentSlot == ServerConstants.FEET_SLOT) {
			player.playerAssistant.sendMessage("Wearing boots has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[20] && itemEquipmentSlot == ServerConstants.RING_SLOT) {
			player.playerAssistant.sendMessage("Wearing rings has been disabled in this duel!");
			return false;
		}
		if (player.duelRule[21] && itemEquipmentSlot == ServerConstants.ARROW_SLOT) {
			player.playerAssistant.sendMessage("Wearing arrows has been disabled in this duel!");
			return false;
		}

		if (player.duelRule[TradeAndDuel.ANTI_SCAM]) {
			if (player.duelRule[TradeAndDuel.NO_SPECIAL_ATTACK]) {
				if (itemId == 5698) {
					player.getPA().sendMessage("Special attacks are turned off, you cannot wear a Dds.");
					return false;
				}
			}
		}

		if (!ItemRequirement.hasItemRequirement(player, itemEquipmentSlot)) {
			return false;
		}

		if (!AchievementShop.hasAchievementItemRequirements(player, itemId, false, true)) {
			return false;
		}
		if (itemId >= 9747 && itemId <= 9812 && !player.isInfernalAndMaxCapesUnlockedScrollConsumed() && !player.isUberDonator()) {
			for (Skilling.SkillCapeMasterData data : Skilling.SkillCapeMasterData.values()) {
				if (data.getUntrimmedSkillCapeId() == itemId || data.getUntrimmedSkillCapeId() + 1 == itemId || data.getUntrimmedSkillCapeId() + 2 == itemId) {
					if (player.baseSkillLevel[data.ordinal()] < 99) {
						player.getPA().sendMessage("You need 99 " + ServerConstants.SKILL_NAME[data.ordinal()] + " to wear this.");
						return false;
					}
				}
			}
		}
		int extraItem = 0;
		int extraItemAmount = 0;
		if (slot >= 0 && itemId >= 0) {
			int toEquipN = player.playerItemsN[slot];
			if ((player.getWieldedWeapon() == 4151 || player.getWieldedWeapon() == 12773 || player.getWieldedWeapon() == 12774 || ItemAssistant
					                                                                                                                      .getItemName(player.getWieldedWeapon())
							.contains("tentacle")
					|| ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase()
							.contains("abyssal"))
			    && player.getCombatStyle(ServerConstants.CONTROLLED)) {
				player.wasWearingAggressiveSharedXpWeapon = true;
			}

			// Dragon spear & Guthan's warspear
			else if ((player.getWieldedWeapon() == 1249
					|| player.getWieldedWeapon() == 11889 || player.getWieldedWeapon() == 11824
					|| player.getWieldedWeapon() == 4726)
			         && player.getCombatStyle(ServerConstants.CONTROLLED)) {
				player.wasWearingAggressiveSharedXpWeapon = true;
			}
			// If player is weilding trident and is switching to not trident.
			else if (player.getWieldedWeapon() == 12899 && itemId != 12899 && itemEquipmentSlot == ServerConstants.WEAPON_SLOT) {
				AutoCast.resetAutocast(player, true);
			} else if (player.getWieldedWeapon() == 22_494 && itemId != 22_494
					&& itemEquipmentSlot == ServerConstants.WEAPON_SLOT) {
				AutoCast.resetAutocast(player, true);
			}
			player.setInventoryUpdate(true);
			int toEquip = player.playerItems[slot];
			int toRemove = player.playerEquipment[itemEquipmentSlot];
			int toRemoveN = player.playerEquipmentN[itemEquipmentSlot];
			if (toEquip == toRemove + 1 && ItemDefinition.getDefinitions()[toRemove].stackable) {
				int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
				if (toEquipN > maximumAmount) {
					player.playerAssistant.sendMessage(
							ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot]) + ".");
					return false;
				}
				deleteItemFromInventory(player, toRemove, getItemSlot(player, toRemove), toEquipN);
				player.playerEquipmentN[itemEquipmentSlot] += toEquipN;
			} else if (itemEquipmentSlot != ServerConstants.SHIELD_SLOT && itemEquipmentSlot != ServerConstants.WEAPON_SLOT) {
				int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
				if (toEquipN > maximumAmount && player.playerEquipment[itemEquipmentSlot] == toEquip) {
					player.playerAssistant.sendMessage(
							ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot]) + ".");
					return false;
				}
				boolean added = false;
				if (!ItemAssistant.nulledItem(toRemove)) {
					if (ItemDefinition.getDefinitions()[toRemove].stackable) {
						if (ItemAssistant.hasItemInInventory(player, toRemove)) {
							if (ItemAssistant.addItem(player, toRemove, toRemoveN)) {
								player.playerItems[slot] = 0;
								player.playerItemsN[slot] = 0;
								added = true;
							} else {
								return false;
							}
						}
					}
				}
				if (!added) {
					player.playerItems[slot] = toRemove + 1;
					player.playerItemsN[slot] = toRemoveN;
				}
				player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
				player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
			} else if (itemEquipmentSlot == ServerConstants.SHIELD_SLOT) {
				int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
				if (toEquipN > maximumAmount) {
					player.playerAssistant.sendMessage(
							ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot]) + ".");
					return false;
				}
				boolean wearing2h = is2handed(player.getWieldedWeapon());
				if (wearing2h) {
					toRemove = player.getWieldedWeapon();
					toRemoveN = player.playerEquipmentN[ServerConstants.WEAPON_SLOT];
					player.playerEquipment[ServerConstants.WEAPON_SLOT] = -1;
					player.playerEquipmentN[ServerConstants.WEAPON_SLOT] = 0;
				}
				player.playerItems[slot] = toRemove + 1;
				player.playerItemsN[slot] = toRemoveN;
				player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
				player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
			} else if (itemEquipmentSlot == ServerConstants.WEAPON_SLOT) {
				boolean is2h = is2handed(itemId);
				boolean wearingShield = player.playerEquipment[ServerConstants.SHIELD_SLOT] > 0;
				boolean wearingWeapon = player.getWieldedWeapon() > 0;
				if (is2h) {
					if (wearingShield && wearingWeapon) {
						if (getFreeInventorySlots(player) > 0) {
							int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
							if (toEquipN > maximumAmount) {
								player.playerAssistant.sendMessage(
										ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot])
										+ ".");
								return false;
							}
							boolean added = false;
							//Prevent 2 stacks of an item, to prevent dupes.
							if (!ItemAssistant.nulledItem(toRemove)) {
								if (ItemDefinition.getDefinitions()[toRemove].stackable) {
									if (ItemAssistant.hasItemInInventory(player, toRemove)) {
										if (ItemAssistant.addItem(player, toRemove, toRemoveN)) {
											player.playerItems[slot] = 0;
											player.playerItemsN[slot] = 0;
											added = true;
										} else {
											return false;
										}
									}
								}
							}
							if (!added) {
								player.playerItems[slot] = toRemove + 1;
								player.playerItemsN[slot] = toRemoveN;
							}
							player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
							player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
							removeItem(player, player.playerEquipment[ServerConstants.SHIELD_SLOT], ServerConstants.SHIELD_SLOT, true);
						} else {
							player.getPA().sendMessage("You do not have enough inventory space.");
						}
					} else if (wearingShield && !wearingWeapon) {
						int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
						if (toEquipN > maximumAmount) {
							player.playerAssistant.sendMessage(
									ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot])
									+ ".");
							return false;
						}
						player.playerItems[slot] = player.playerEquipment[ServerConstants.SHIELD_SLOT] + 1;
						player.playerItemsN[slot] = player.playerEquipmentN[ServerConstants.SHIELD_SLOT];
						player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
						player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
						player.playerEquipment[ServerConstants.SHIELD_SLOT] = -1;
						player.playerEquipmentN[ServerConstants.SHIELD_SLOT] = 0;
					} else {
						int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
						if (toEquipN > maximumAmount) {
							player.playerAssistant.sendMessage(
									ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot])
									+ ".");
							return false;
						}
						boolean added = false;
						//Prevent 2 stacks of an item, to prevent dupes.
						if (!ItemAssistant.nulledItem(toRemove)) {
							if (ItemDefinition.getDefinitions()[toRemove].stackable) {
								if (ItemAssistant.hasItemInInventory(player, toRemove)) {
									if (ItemAssistant.addItem(player, toRemove, toRemoveN)) {
										player.playerItems[slot] = 0;
										player.playerItemsN[slot] = 0;
										added = true;
									} else {
										return false;
									}
								}
							}
						}
						if (!added) {
							player.playerItems[slot] = toRemove + 1;
							player.playerItemsN[slot] = toRemoveN;
						}
						player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
						player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
					}
				} else {
					int maximumAmount = Integer.MAX_VALUE - player.playerEquipmentN[itemEquipmentSlot];
					if (toEquipN > maximumAmount) {
						player.playerAssistant.sendMessage(
								ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.playerEquipment[itemEquipmentSlot]) + ".");
						return false;
					}
					boolean added = false;
					//Prevent 2 stacks of an item, to prevent dupes.
					if (!ItemAssistant.nulledItem(toRemove)) {
						if (ItemDefinition.getDefinitions()[toRemove].stackable) {
							if (ItemAssistant.hasItemInInventory(player, toRemove)) {
								if (ItemAssistant.addItem(player, toRemove, toRemoveN)) {
									player.playerItems[slot] = 0;
									player.playerItemsN[slot] = 0;
									added = true;
								} else {
									return false;
								}
							}
						}
					}
					if (!added) {
						player.playerItems[slot] = toRemove + 1;
						player.playerItemsN[slot] = toRemoveN;
					}
					player.playerEquipment[itemEquipmentSlot] = toEquip - 1;
					player.playerEquipmentN[itemEquipmentSlot] = toEquipN;
					added = false;
				}
			}
		}

		if (itemEquipmentSlot == ServerConstants.WEAPON_SLOT) {
			player.setUsingSpecialAttack(false);
			player.botUsedSpecialAttack = false;
		}
		if (extraItem > 0) {
			ItemAssistant.addItem(player, extraItem, extraItemAmount);
		}

		// Dinh's bulwark
		if (itemId == 21015 || itemId == 16259) {
			player.timeSwitchedToBulwark = System.currentTimeMillis();
		}

		if (!player.justTransformed) {
			Transform.unTransform(player);
		}
		if (itemEquipmentSlot == ServerConstants.WEAPON_SLOT) {
			if (player.getAutoCasting() && player.usingOldAutocast) {
				AutoCast.resetAutocast(player, true);
			}
		}
		player.aggressiveType = "DEFAULT"; // Reset to normal.
		player.justTransformed = false;
		AfkChair.resetAfk(player, false);
		Combat.updatePlayerStance(player);
		player.switches++;
		player.itemWorn = true;
		player.graniteMaulSpecialAttackClicks = 0;
		return true;
	}

	public static void updateSlot(Player player, int slot) {
		if (player.bot) {
			return;
		}
		player.getPA().sendFrame34(1688, player.playerEquipment[slot], slot, player.playerEquipmentN[slot]);
	}

	/**
	 * Remove Item from equipment tab
	 **/
	public static void removeItem(Player player, int wearId, int slot, boolean ignoreBusy) {
		if (!ignoreBusy) {
			if (player.doingAnAction() || player.getDoingAgility() || player.isTeleporting()) {
				return;
			}
		}
		if (!ItemAssistant.hasItemEquipped(player, wearId)) {
			return;
		}
		if (player.playerEquipment[slot] > -1) {
			if (addItem(player, player.playerEquipment[slot], player.playerEquipmentN[slot])) {
				if (player.getAutoCasting() && player.usingOldAutocast && slot == ServerConstants.WEAPON_SLOT) {
					AutoCast.resetAutocast(player, true);
				}
				Transform.unTransform(player);
				player.switches++;
				player.clearEquipmentSlot(player, slot);
				Combat.updatePlayerStance(player);
				player.itemWorn = true;
			}
		}
	}

	public static int itemAmount(Player player, int itemId) {
		int tempAmount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId) {
				tempAmount += player.playerItemsN[i];
			}
		}
		return tempAmount;
	}

	public static boolean isStackable(int itemId) {
		return ItemDefinition.getDefinitions()[itemId].stackable;
	}

	/**
	 * Update the equipment interface to show the items in the slots.
	 */
	public static void updateEquipmentInterface(Player player) {
		setEquipment(player, player.playerEquipment[ServerConstants.HEAD_SLOT], 1, ServerConstants.HEAD_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.CAPE_SLOT], 1, ServerConstants.CAPE_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.AMULET_SLOT], 1, ServerConstants.AMULET_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.ARROW_SLOT], player.playerEquipmentN[ServerConstants.ARROW_SLOT], ServerConstants.ARROW_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.BODY_SLOT], 1, ServerConstants.BODY_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.SHIELD_SLOT], 1, ServerConstants.SHIELD_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.LEG_SLOT], 1, ServerConstants.LEG_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.HAND_SLOT], 1, ServerConstants.HAND_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.FEET_SLOT], 1, ServerConstants.FEET_SLOT);
		setEquipment(player, player.playerEquipment[ServerConstants.RING_SLOT], 1, ServerConstants.RING_SLOT);
		setEquipment(player, player.getWieldedWeapon(), player.playerEquipmentN[ServerConstants.WEAPON_SLOT], ServerConstants.WEAPON_SLOT);
	}

	/**
	 * Update Equip tab
	 **/
	public static void setEquipment(Player player, int wearID, int amount, int targetSlot) {
		player.playerEquipment[targetSlot] = wearID;
		player.playerEquipmentN[targetSlot] = amount;
		if (player.bot) {
			return;
		}
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}

	/**
	 * Move Items
	 **/
	public static void moveItems(Player player, int from, int to, int moveWindow, byte insert) {
		if ((moveWindow == 5382 || moveWindow >= 35_001 && moveWindow <= 35_008) && from >= 0 && to >= 0 && from < ServerConstants.BANK_SIZE && to < ServerConstants.BANK_SIZE
		    && to < ServerConstants.BANK_SIZE) {
			if (Bank.hasBankingRequirements(player, false)) {
				int tab = moveWindow == 5382 ? 0 : moveWindow - 35_001 + 1;
				boolean onMainTab = moveWindow >= 35_001 && moveWindow <= 35_008;
				if (tab < 0 || tab > 8) {
					player.bankUpdated = true;
					return;
				}
				int previous = player.bankingTab;

				if (previous != tab && tab != 0) {
					Bank.openCorrectTab(player, tab, false);
				}

				if (insert == 0) {
					int tempI;
					int tempN;
					tempI = player.bankingItems[from];
					tempN = player.bankingItemsN[from];
					player.bankingItems[from] = player.bankingItems[to];
					player.bankingItemsN[from] = player.bankingItemsN[to];
					player.bankingItems[to] = tempI;
					player.bankingItemsN[to] = tempN;
					Bank.openUpBank(player, onMainTab ? 0 : player.bankingTab, false, false);
				} else if (insert == 1) {
					int tempFrom = from;

					for (int tempTo = to; tempFrom != tempTo; ) {
						if (tempFrom > tempTo) {
							Bank.swapBankItem(player, tempFrom, tempFrom - 1);
							tempFrom--;
						} else if (tempFrom < tempTo) {
							Bank.swapBankItem(player, tempFrom, tempFrom + 1);
							tempFrom++;
						}
					}
					Bank.openUpBank(player, onMainTab ? 0 : player.bankingTab, true, false);
				}
				if (previous != tab && tab != 0) {
					Bank.openCorrectTab(player, previous, false);
				}
				player.bankUpdated = true;
			}
		}
		if (moveWindow == 3214 && !ItemAssistant.isNulledSlot(from) && !ItemAssistant.isNulledSlot(to)) {
			int tempI;
			int tempN;
			tempI = player.playerItems[from];
			tempN = player.playerItemsN[from];
			player.playerItems[from] = player.playerItems[to];
			player.playerItemsN[from] = player.playerItemsN[to];
			player.playerItems[to] = tempI;
			player.playerItemsN[to] = tempN;
			player.setInventoryUpdate(true);
		}
		if ((moveWindow == 18579 || moveWindow == 5064) && !ItemAssistant.isNulledSlot(from) && !ItemAssistant.isNulledSlot(to)) {
			int tempI;
			int tempN;
			tempI = player.playerItems[from];
			tempN = player.playerItemsN[from];
			player.playerItems[from] = player.playerItems[to];
			player.playerItemsN[from] = player.playerItemsN[to];
			player.playerItems[to] = tempI;
			player.playerItemsN[to] = tempN;
			player.setInventoryUpdate(true);
		}
	}

	/**
	 * delete Item
	 **/
	public static void deleteEquipment(Player player, int itemId, int itemSlot) {
		if (PlayerHandler.players[player.getPlayerId()] == null) {
			return;
		}
		if (itemId < 0) {
			return;
		}
		player.clearEquipmentSlot(player, itemSlot);
	}

	public static void deleteEquipment(Player player, int itemId, int itemAmount, int itemSlot) {
		if (PlayerHandler.players[player.getPlayerId()] == null) {
			return;
		}
		if (itemId < 0) {
			return;
		}
		player.playerEquipmentN[itemSlot] -= itemAmount;
		if (player.playerEquipmentN[itemSlot] <= 0) {
			player.clearEquipmentSlot(player, itemSlot);
		}
	}

	/**
	 * Search all equipment slots and delete the item id.
	 */
	public static void deleteEquipment(Player player, int itemId) {
		if (PlayerHandler.players[player.getPlayerId()] == null) {
			return;
		}
		if (itemId < 0) {
			return;
		}
		for (int index = 0; index < player.playerEquipment.length; index++) {
			if (player.playerEquipment[index] != itemId) {
				continue;
			}
			player.clearEquipmentSlot(player, index);
			break;
		}
	}

	public static int deleteItemFromInventory(Player player, int itemId, int amount) {
		if (itemId <= 0) {
			return amount;
		}
		if (ItemDefinition.getDefinitions()[itemId].stackable) {
			return deleteStackableItemFromInventory(player, itemId, amount);
		}
		for (int j = 0; j < player.playerItems.length; j++) {
			if (player.playerItems[j] == itemId + 1) {
				player.playerItems[j] = 0;
				player.playerItemsN[j] = 0;
				amount--;
				if (amount == 0) {
					break;
				}
			}
		}
		player.setInventoryUpdate(true);
		return amount;
	}

	/**
	 * Find the item in the inventory and delete it.
	 */
	public static int deleteStackableItemFromInventory(Player player, int id, int amount) {
		for (int j = 0; j < player.playerItems.length; j++) {
			if (player.playerItems[j] == id + 1) {
				if (amount >= player.playerItemsN[j]) {
					player.playerItems[j] = 0;
					amount -= player.playerItemsN[j];
					player.playerItemsN[j] = 0;
					player.setInventoryUpdate(true);
				} else {
					player.playerItemsN[j] -= amount;
					player.setInventoryUpdate(true);
					amount = 0;
				}
				break;
			}
		}
		return amount;
	}

	public static void deleteItemFromInventory(Player player, int id, int slot, int amount) {
		if (id <= 0 || slot < 0) {
			return;
		}
		if (player.playerItems[slot] == (id + 1)) {
			if (player.playerItemsN[slot] > amount) {
				player.playerItemsN[slot] -= amount;
			} else {
				player.playerItemsN[slot] = 0;
				player.playerItems[slot] = 0;
			}
			player.setInventoryUpdate(true);
		}
	}

	public static int getFreeInventorySlots(Player player) {
		int freeS = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	/**
	 * @return True if the player has enough inventory slots. If false, it will send a message to the player.
	 */
	public static boolean hasInventorySlotsAlert(Player player, int amount) {
		if (getFreeInventorySlots(player) >= amount) {
			return true;
		}
		player.getPA().sendMessage("You need at least " + amount + " inventory slots to continue.");
		return false;
	}

	public static String getItemName(int itemId) {
		if (itemId <= 0) {
			return "Unarmed";
		}
		if (ItemDefinition.getDefinitions()[itemId] != null) {
			return ItemDefinition.getDefinitions()[itemId].name;
		}
		return "Unarmed";
	}

	public static int getItemId(String itemName, boolean notedOnly) {
		for (int i = 0; i < ServerConstants.MAX_ITEM_ID; i++) {
			if (ItemDefinition.getDefinitions()[i] != null) {
				if (ItemDefinition.getDefinitions()[i].name.equalsIgnoreCase(itemName)) {
					if (notedOnly && !ItemDefinition.getDefinitions()[i].note) {
						continue;
					}
					return i;
				}
			}
		}
		return -1;
	}

	public static int getItemSlot(Player player, int itemId) {
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) == itemId) {
				return i;
			}
		}
		return -1;
	}

	public static int getItemAmount(Player player, int itemId) {
		int itemCount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) == itemId) {
				itemCount += player.playerItemsN[i];
			}
		}
		return itemCount;
	}

	public static int getItemAmount(Player player, int itemId, int slot) {
		int itemCount = 0;
		if ((player.playerItems[slot] - 1) == itemId) {
			itemCount += player.playerItemsN[slot];
		}
		return itemCount;
	}

	public static boolean playerHasItem(Player player, int itemId, int amt, int slot) {
		if (isNulledSlot(slot)) {
			return false;
		}
		itemId++;
		int found = 0;
		if (player.playerItems[slot] == (itemId)) {
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] == itemId) {
					if (player.playerItemsN[i] >= amt) {
						return true;
					} else {
						found++;
					}
				}
			}
			if (found >= amt) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @param itemId The item identity to search for.
	 * @return True, if the player has the item in inventory.
	 */
	public static boolean hasItemInInventory(Player player, int itemId) {
		itemId++;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasItemAmountInInventory(Player player, int itemId, int amount) {
		itemId++;
		int found = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId) {
				if (player.playerItemsN[i] >= amount) {
					return true;
				} else {
					found++;
				}
			}
		}
		if (found >= amount) {
			return true;
		}
		return false;
	}

	public static int getEquipmentSlotsOccupied(Player player) {
		return (int) IntStream.of(player.playerEquipment).filter(equipment -> equipment > 0).count();
	}

	/**
	 * Drop Item
	 **/
	public static void createGroundItem(Player player, int itemId, int itemX, int itemY, int itemAmount) {
		if (player == null) {
			return;
		}
		if (player.bot) {
			return;
		}
		itemY = (itemY - 8 * player.getMapRegionY());
		itemX = (itemX - 8 * player.getMapRegionX());
		player.sendGroundItemPacket.add(itemY + " " + itemX + " " + itemId + " " + itemAmount);
		if (player.getOutStream() == null) {
			return;
		}
	}

	public static void sendGroundItemCustompacket(Player player) {
		int count = 0;
		String data = "";
		if (player.sendGroundItemPacket.isEmpty()) {
			return;
		}
		for (int index = 0; index < player.sendGroundItemPacket.size(); index++) {
			if (player.sendGroundItemPacket.get(index).isEmpty()) {
				continue;
			}
			count++;
			data = data + player.sendGroundItemPacket.get(index) + "#";
			if (count == 9) {
				player.getPA().sendMessage(":packet:showgrounditem:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:showgrounditem:" + data);
		}
		player.sendGroundItemPacket.clear();
	}

	public static void sendGroundItemRemoveCustompacket(Player player) {
		int count = 0;
		String data = "";
		if (player.sendGroundItemPacketRemove.isEmpty()) {
			return;
		}
		for (int index = 0; index < player.sendGroundItemPacketRemove.size(); index++) {
			count++;
			data = data + player.sendGroundItemPacketRemove.get(index) + "#";
			if (count == 9) {
				player.getPA().sendMessage(":packet:removegrounditem:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:removegrounditem:" + data);
		}
		player.sendGroundItemPacketRemove.clear();
	}

	/**
	 * Pickup Item
	 **/
	public static void removeGroundItem(Player player, int itemId, int itemX, int itemY, int Amount) {
		if (player == null) {
			return;
		}
		if (player.bot) {
			return;
		}
		itemY = (itemY - 8 * player.getMapRegionY());
		itemX = (itemX - 8 * player.getMapRegionX());
		player.sendGroundItemPacketRemove.add(itemY + " " + itemX + " " + itemId + " " + Amount);
		if (player.getOutStream() == null) {
			return;
		}
	}

	/**
	 * True if the player has an item equipped in the given slot.
	 */
	public static boolean hasItemInEquipmentSlot(Player player, int slot) {
		if (player.playerEquipment[slot] > 0 && player.playerEquipmentN[slot] > 0) {
			return true;
		}
		return false;
	}

	public static void updateEquipmentLogIn(Player player) {
		for (int equipmentSlot = 0; equipmentSlot < player.playerEquipment.length; equipmentSlot++) {
			updateSlot(player, equipmentSlot);
		}

	}

	public static boolean isNulledSlot(int itemSlot) {
		if (itemSlot < 0 || itemSlot > 27) {
			return true;
		}
		return false;
	}

	/**
	 * @param usdPayment Keep -1 if the reward is not a payment made by the player.
	 */
	public static void addItemReward(Player staffPlayer, String playerBeingGivenName, int itemId, int amount, boolean log, double usdPayment) {
		if (ItemDefinition.getDefinitions()[itemId] == null || itemId <= 0 || amount <= 0) {
			return;
		}
		playerBeingGivenName = playerBeingGivenName.toLowerCase();
		boolean online = false;
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player loop = PlayerHandler.players[i];
			if (loop == null) {
				continue;
			}
			if (!loop.getPlayerName().toLowerCase().equals(playerBeingGivenName)) {
				continue;
			}
			if (Area.inDangerousPvpAreaOrClanWars(loop) || loop.getHeight() == 20) {
				if (Bank.addItemToBank(loop, itemId, amount, false)) {
					online = true;
					if (usdPayment > 0) {
						DonatorMainTab.paymentClaimedInUsd(loop, usdPayment);
					}
					loop.getPA().sendMessage(ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount) + " has been added to your bank.");
					if (staffPlayer != null) {
						staffPlayer.getPA().sendMessage(
								"Added online to bank " + playerBeingGivenName + " with item: " + ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount));
					}
				} else {
					online = false;
				}
			} else {
				if (ItemAssistant.addItem(loop, itemId, amount)) {
					if (usdPayment > 0) {
						DonatorMainTab.paymentClaimedInUsd(loop, usdPayment);
					}
					loop.getPA().sendMessage(ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount) + " has been added to your inventory.");
					if (log) {
						FileUtility.addLineOnTxt("backup/logs/given_items.txt",
						                         Misc.getDateAndTime() + ", " + playerBeingGivenName + ": x" + Misc.formatRunescapeStyle(amount) + " " + ItemAssistant.getItemName(
										itemId) + ", " + itemId);
					}
					if (staffPlayer != null) {
						staffPlayer.getPA().sendMessage(
								"Added online to inventory " + playerBeingGivenName + " with item: " + ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(
										amount));
					}
					online = true;
				} else if (Bank.addItemToBank(loop, itemId, amount, false)) {
					online = true;
					if (usdPayment > 0) {
						DonatorMainTab.paymentClaimedInUsd(loop, usdPayment);
					}
					loop.getPA().sendMessage(ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount) + " has been added to your bank.");
					if (staffPlayer != null) {
						staffPlayer.getPA().sendMessage(
								"Added online to bank " + playerBeingGivenName + " with item: " + ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount));
					}
				}
			}

			if (!online) {
				loop.getPA().sendMessage(ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount) + " has been added to account, relog with space to receive.");
				if (staffPlayer != null) {
				staffPlayer.getPA()
				           .sendMessage(playerBeingGivenName + " needs to relog to receive: " + ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount));
				}
			}
			break;
		}

		if (!online) {
			ClaimPrize.itemRewards.add(playerBeingGivenName + "-" + itemId + "-" + amount + "-" + usdPayment);
			if (log) {
				FileUtility.addLineOnTxt("backup/logs/given_items.txt",
						Misc.getDateAndTime() + ", " + playerBeingGivenName + ": x" + Misc.formatRunescapeStyle(amount) + " " + ItemAssistant.getItemName(itemId) + ", " + itemId);
			}
			if (staffPlayer != null) {
				staffPlayer.getPA()
				           .sendMessage("Added offline " + playerBeingGivenName + " with item: " + ItemAssistant.getItemName(itemId) + ", amount: " + Misc.formatNumber(amount));
			}
		}

	}

	public static boolean hasTwoItems(Player player, int itemUsed, int usedWith, int item1, int item2) {
		if (item1 == itemUsed && item2 == usedWith || item1 == usedWith && item2 == itemUsed) {
			return true;
		}
		return false;
	}

	/**
	 * Delete the specific item from the inventory and then return the amount that has been managed to
	 * be deleted.
	 */
	public static int getAmountLeftToDeleteFromInventory(Player player, int itemId, int itemAmount) {
		return deleteItemFromInventory(player, itemId, itemAmount);
	}
}
