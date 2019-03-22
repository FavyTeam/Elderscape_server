package game.content.skilling.summoning.familiar.special.impl;

import core.GameType;
import core.Server;
import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;
import game.content.skilling.summoning.familiar.SummoningFamiliar;
import game.content.skilling.summoning.familiar.special.SummoningFamiliarSpecialAbility;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.player.Player;

/**
 * Represents the Beast of Burden familiar special ability
 *
 * @author 2012
 */
public class BoBSummoningFamiliarSpecialAbility implements SummoningFamiliarSpecialAbility {

	/**
	 * The bob storage interface id
	 */
	private static final int BOB_STORAGE_INTERFACE = 33_500;

	/**
	 * The bob storage item container
	 */
	public static final int BOB_STORAGE_CONTAINER = 33_502;

	/**
	 * The storage capacity for pack yack
	 */
	private static final int PACK_YAK_CAPACITY = 30;

	/**
	 * The times for items to disappear
	 */
	public static final int TIME_TILL_ITEMS_DISAPPEAR = 100 * 5;

	/**
	 * The container
	 */
	private ItemContainer container;

	/**
	 * The allowed items
	 */
	private int[] allowed;

	/**
	 * Represents a BoB storage
	 * 
	 * @param container the container
	 * @param allowed the items allowed
	 */
	private BoBSummoningFamiliarSpecialAbility(ItemContainer container, int[] allowed) {
		this.container = container;
		this.allowed = allowed;
	}

	/**
	 * Gets the pack yak BoB
	 * 
	 * @return the storage
	 */
	public static BoBSummoningFamiliarSpecialAbility getPackYak() {
		return new BoBSummoningFamiliarSpecialAbility(new ItemContainer(PACK_YAK_CAPACITY,
				ItemContainerStackPolicy.UNSTACKABLE, ItemContainerNotePolicy.PERMITTED), null);
	}

	/**
	 * Opening the bob storage
	 * 
	 * @param player the player
	 */
	public void openBoBStorage(Player player) {
		/*
		 * Displays items
		 */
		displayItems(player);
		/*
		 * Displays interface
		 */
		player.getPA().sendFrame248(BOB_STORAGE_INTERFACE, 5063);
	}

	/**
	 * Dropping the BoB storage items
	 * 
	 * @param player the player
	 * @param npc the npc
	 */
	public void dropItems(Player player, Npc npc) {
		/*
		 * Notify
		 */
		player.getPA().sendMessage("Your familiar has dropped all the items it was holding.");
		/*
		 * Loop the items
		 */
		getContainer().forAll((index, item) -> Server.itemHandler.createGroundItem(player,
				item.getId(), npc.getX(), npc.getY(), npc.getHeight(), item.getAmount(), false,
				TIME_TILL_ITEMS_DISAPPEAR, false, player.getPlayerName(), "", "", "", "bob-items"));
	}

	/**
	 * Storing an item to BoB
	 * 
	 * @param player the player
	 * @param item the item
	 * @param slot the slot
	 * @return stored item
	 */
	public static boolean store(Player player, GameItem item, int slot) {
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = getFamiliar(player);
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return false;
		}
		/*
		 * No stackables
		 */
		if (ItemDefinition.DEFINITIONS[item.getId()].stackable) {
			player.getPA().sendMessage("You cannot store stackable items.");
			return true;
		}
		/*
		 * Can't store
		 */
		if (ItemAssistant.cannotTradeAndStakeItemItem(item.getId())) {
			player.getPA().sendMessage("This item cannot be stored.");
			return false;
		}
		/*
		 * No notes
		 */
		if (ItemDefinition.DEFINITIONS[item.getId()].note) {
			player.getPA().sendMessage("You cannot store noted items.");
			return true;
		}
		/*
		 * The amount
		 */
		int amount = item.getAmount();
		/*
		 * Fix amount
		 */
		if (item.getAmount() > ItemAssistant.getItemAmount(player, item.getId())) {
			amount = ItemAssistant.getItemAmount(player, item.getId());
		}
		/*
		 * Doesn't have the item
		 */
		if (!ItemAssistant.playerHasItem(player, item.getId(), amount, slot)) {
			return true;
		}
		/*
		 * The container
		 */
		ItemContainer container = familiar.getBoB().getContainer();
		/*
		 * To store
		 */
		if (container.add(new GameItem(item.getId(), amount)) > 0) {
			ItemAssistant.deleteItemFromInventory(player, item.getId(), amount);
			familiar.getBoB().displayItems(player);
		} else {
			player.getPA().sendMessage("You can't store any items in your Beast of Burden.");
		}
		return true;
	}

	/**
	 * Withdraws the item from the BoB
	 * 
	 * @param player the player
	 * @param item the item
	 * @return withdraws item
	 */
	public static boolean withdraw(Player player, GameItem item) {
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = getFamiliar(player);
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return false;
		}
		/*
		 * The amount
		 */
		int amount = item.getAmount();
		/*
		 * The item container
		 */
		ItemContainer container = familiar.getBoB().getContainer();
		/*
		 * Fixes amount
		 */
		if (amount > container.amount(item.getId())) {
			amount = container.amount(item.getId());
		}
		/*
		 * No amount
		 */
		if (amount == 0) {
			return true;
		}
		/*
		 * Doesn't exist
		 */
		if (!container.contains(item.getId(), amount)) {
			return true;
		}
		/*
		 * Removes the item
		 */
		if (ItemAssistant.addItem(player, item.getId(), amount)) {
			familiar.getBoB().getContainer().delete(new GameItem(item.getId(), amount));
		}
		/*
		 * Displays items
		 */
		familiar.getBoB().displayItems(player);
		return true;
	}

	/**
	 * Withdraws all items
	 * 
	 * @param player the player
	 */
	public static void withdrawAll(Player player) {
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = getFamiliar(player);
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return;
		}
		/*
		 * Withdraw items
		 */
		familiar.getBoB().getContainer().forNonNull((index, item) -> withdraw(player, item));
		/*
		 * Displays items
		 */
		familiar.getBoB().clearInterface(player);
	}

	/**
	 * Clears the interface
	 * 
	 * @param player the player
	 */
	private void clearInterface(Player player) {
		/*
		 * Clears the interface
		 */
		for (int i = 0; i < 30; i++) {
			player.getPA().sendFrame34(BOB_STORAGE_CONTAINER, -1, i, 1);
		}
	}

	/**
	 * Displays items
	 * 
	 * @param player the player
	 */
	private void displayItems(Player player) {
		/*
		 * The slot
		 */
		int slot = 0;
		/*
		 * Displays the items
		 */
		getContainer().forAll((index, item) -> sendItems(player, item, slot));
		/*
		 * Sends inventory items
		 */
		ItemAssistant.resetItems(player, 5064);
	}

	/**
	 * Sending the items
	 * 
	 * @param player the player
	 * @param item the item
	 * @param slot the slot
	 */
	private void sendItems(Player player, GameItem item, int slot) {
		if (item == null) {
			player.getPA().sendFrame34(BOB_STORAGE_CONTAINER, -1, slot++, 0);
		} else {
			player.getPA().sendFrame34(BOB_STORAGE_CONTAINER, item.getId(), slot++, item.getAmount());
		}
	}

	/**
	 * Gets the familiar
	 * 
	 * @param player the player
	 * @return the familiar
	 */
	public static SummoningFamiliar getFamiliar(Player player) {
		/*
		 * Not pre eoc
		 */
		if (!GameType.isPreEoc()) {
			return null;
		}
		/*
		 * No familiar
		 */
		if (player.getSummoning().getFamiliar() == null) {
			return null;
		}
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = player.getSummoning().getFamiliar().getFamiliar();
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return null;
		}
		/*
		 * Not BoB
		 */
		if (!familiar.isBoB()) {
			return null;
		}
		return familiar;
	}

	@Override
	public boolean sendScroll(Player player) {
		return false;
	}

	@Override
	public void cycle(Player player) {}

	@Override
	public double getSkillBonus(Player player, int skill) {
		return 1.0;
	}

	@Override
	public int getPercentageRequired() {
		return 0;
	}

	/**
	 * Sets the container
	 *
	 * @return the container
	 */
	public ItemContainer getContainer() {
		return container;
	}

	/**
	 * Sets the allowed
	 *
	 * @return the allowed
	 */
	public int[] getAllowed() {
		return allowed;
	}
}
