package game.container.impl;

import core.GameType;
import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Handles the money pouch
 * 
 * @author 2012
 *
 */
public class MoneyPouch extends ItemContainer {

	/**
	 * The line for money pouch
	 */
	public static final int MONEY_POUCH_LINE = 38_333;

	/**
	 * The player
	 */
	private Player player;

	/**
	 * The coins to add
	 */
	private int coinsToAdd;

	/**
	 * Represents the money pouch
	 */
	public MoneyPouch(Player player) {
		super(1, ItemContainerStackPolicy.STACKABLE, ItemContainerNotePolicy.DENOTE);
		this.player = player;
	}

	/**
	 * Adding coins to pouch upon login
	 * 
	 * @param player the player
	 * @param amount the amount
	 */
	public static void onLogin(Player player) {
		if (!GameType.isPreEoc()) {
			return;
		}
		player.getMoneyPouch().add(new GameItem(995, player.getMoneyPouch().getCoinsToAdd()));
		player.getMoneyPouch().setCoinsToAdd(0);
		MoneyPouch.update(player);
	}

	/**
	 * Adding coins to money pouch
	 * 
	 * @param player the player
	 * @param slot the slot
	 */
	public static void addToPouch(Player player, int slot) {
		/*
		 * Not pre eoc
		 */
		if (!GameType.isPreEoc()) {
			return;
		}
		/*
		 * Not in wild
		 */
		if (Area.inDangerousPvpArea(player)) {
			player.getPA().sendMessage("You cannot deposit coins to your money pouch here.");
			return;
		}
		/*
		 * The amount to add
		 */
		int amount = ItemAssistant.getItemAmount(player, 995);
		/*
		 * Doesn't exist
		 */
		if (!ItemAssistant.playerHasItem(player, 995, amount, slot)) {
			return;
		}
		/*
		 * The existing amount
		 */
		int existing = player.getMoneyPouch().amount(995);
		/*
		 * Fix amount
		 */
		if (existing + amount < -1) {
			amount = Integer.MAX_VALUE - existing;
		}
		/*
		 * Can't add
		 */
		if (amount == 0) {
			player.getPA().sendMessage("You can't add anymore coins to your Money Pouch.");
			return;
		}
		/*
		 * The amount added
		 */
		player.getMoneyPouch().add(new GameItem(995, amount));
		/*
		 * Delete from inventory
		 */
		ItemAssistant.deleteItemFromInventory(player, 995, amount);
		player.getPA().sendMessage(":add_money_pouch:" + amount);
	}

	/**
	 * Withdrawing coins
	 * 
	 * @param player the player
	 * @param amount the amount
	 */
	public static void withdrawAmount(Player player, int amount) {
		if (!GameType.isPreEoc()) {
			return;
		}
		/*
		 * Not in wild
		 */
		if (Area.inDangerousPvpArea(player)) {
			player.getPA().sendMessage("You cannot withdraw coins from your money pouch here.");
			return;
		}
		/*
		 * Wrong interface
		 */
		if (player.getxInterfaceId() != MONEY_POUCH_LINE) {
			return;
		}
		/*
		 * Not coins
		 */
		if (player.xRemoveId != 995) {
			return;
		}
		/*
		 * Wrong slot
		 */
		if (player.xRemoveSlot != 0) {
			return;
		}
		/*
		 * The existing amount
		 */
		int existing = player.getMoneyPouch().amount(995);
		/*
		 * Empty pouch
		 */
		if (existing == 0) {
			player.getPA().sendMessage("Your Money Pouch is empty.");
			return;
		}
		/*
		 * Fix input
		 */
		if (amount < 1) {
			amount = 1;
		}
		/*
		 * Fix existing
		 */
		if (amount > existing) {
			amount = existing;
		}
		/*
		 * No amount
		 */
		if (amount == 0) {
			player.getPA().sendMessage("You can't withdraw anything");
			return;
		}
		/*
		 * The inventory amount
		 */
		int inventory = ItemAssistant.getItemAmount(player, 995);
		/*
		 * Fix amount
		 */
		if (inventory + amount < -1) {
			amount = Integer.MAX_VALUE - inventory;
		}
		/*
		 * Can't withdraw
		 */
		if (amount == 0) {
			player.getPA().sendMessage("You can't hold anymore coins");
			return;
		}
		/*
		 * No inventory space
		 */
		if (ItemAssistant.getFreeInventorySlots(player) == 0
				&& !ItemAssistant.hasItemInInventory(player, 995)) {
			player.getPA().sendMessage(
					"You don't have any inventory space to withdraw coins from your Money Pouch.");
			return;
		}
		/*
		 * Doesn't have
		 */
		if (!player.getMoneyPouch().contains(995, amount)) {
			return;
		}
		/*
		 * Delete from pouch
		 */
		player.getMoneyPouch().delete(new GameItem(995, amount));
		/*
		 * Add to inventory
		 */
		ItemAssistant.addItem(player, 995, amount);
		/*
		 * Notify
		 */
		player.getPA().sendMessage("You withdrew " + Misc.formatToK(amount) + " ("
				+ Misc.formatNumber(amount) + ") coins from your Money Pouch.");
		/*
		 * Update
		 */
		update(player);
		player.getPA().sendMessage(":deduct_money_pouch:" + amount);
	}

	/**
	 * Updates the money pouch
	 * 
	 * @param player the player
	 */
	public static void update(Player player) {
		player.getPA().sendFrame126(player.getMoneyPouch().amount(995) + "", MONEY_POUCH_LINE);
	}

	/**
	 * Withdrawing
	 * 
	 * @param player the player
	 */
	public static void sendWithdraw(Player player) {
		player.getOutStream().createFrame(27);
		player.getPA().sendMessage(":packet:enteramounttext Enter Amount to Withdraw");
		player.xRemoveSlot = 0;
		player.setxInterfaceId(MONEY_POUCH_LINE);
		player.xRemoveId = 995;
	}

	@Override
	public int add(GameItem item) {
		/*
		 * Can only add coins
		 */
		if (item.getId() != 995) {
			return 0;
		}
		/*
		 * Added to pouch
		 */
		if (super.add(item) > 0) {
			player.getPA().sendMessage(
					Misc.formatToK(item.getAmount()) + " (" + Misc.formatNumber(item.getAmount())
							+ ") coins have been added to your Money Pouch");
			update(player);
		}
		return 0;
	}

	/**
	 * Sets the coinsToAdd
	 *
	 * @return the coinsToAdd
	 */
	public int getCoinsToAdd() {
		return coinsToAdd;
	}

	/**
	 * Sets the coinsToAdd
	 * 
	 * @param coinsToAdd the coinsToAdd
	 */
	public void setCoinsToAdd(int coinsToAdd) {
		this.coinsToAdd = coinsToAdd;
	}
}
