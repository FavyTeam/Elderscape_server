package game.container.impl;

import java.util.Arrays;
import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;
import game.content.item.ItemInteraction;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;

/**
 * Represents a bag item container which carries permitted items only
 * 
 * @author 2012
 *
 */
public class BagItemContainer extends ItemContainer implements ItemInteraction {

	/**
	 * The permitted id
	 */
	private int[] permitted;

	/**
	 * Represents a bag item container
	 * 
	 * @param capacity the capacity
	 * @param permitted the permitted items
	 */
	public BagItemContainer(int capacity, int[] permitted) {
		super(capacity, ItemContainerStackPolicy.UNSTACKABLE, ItemContainerNotePolicy.DENOTE);
		this.permitted = permitted;
	}

	/**
	 * Inspecting the bag
	 * 
	 * @param player the player
	 */
	public void inspect(Player player) {
		/*
		 * The message
		 */
		String message = "";
		/*
		 * Checking permitted
		 */
		for (int i = 0; i < permitted.length; i++) {
			message += ItemAssistant.getItemName(permitted[i]) + ": " + amount(permitted[i]) + ".";
		}
		/*
		 * The collection
		 */
		player.getPA().sendMessage("Contents in bag: " + message);
	}

	/**
	 * Withdrawing
	 * 
	 * @param player the player
	 */
	public void withdraw(Player player) {
		/*
		 * The free slots
		 */
		int freeSlots = ItemAssistant.getFreeInventorySlots(player);
		/*
		 * No free slots
		 */
		if (freeSlots == 0) {
			player.getPA()
					.sendMessage("You don't have any inventory space to withdraw anymore content.");
			return;
		}
		/*
		 * Empty
		 */
		if (slotsUnavailable() == 0) {
			player.getPA().sendMessage("The bag is empty.");
			return;
		}
		/*
		 * More slots than available
		 */
		if (freeSlots > slotsUnavailable()) {
			freeSlots = slotsUnavailable();
		}
		/*
		 * Withdraws the item
		 */
		forNonNull((index, item) -> {
			if (ItemAssistant.getFreeInventorySlots(player) > 0) {
				/*
				 * Delete from container
				 */
				delete(item);
				/*
				 * Add to inventory
				 */
				ItemAssistant.addItem(player, item.getId(), item.getAmount());
			}
		});
		player.getPA().sendMessage("You withdraw content from the bag");
	}

	/**
	 * Adding content to bag
	 * 
	 * @param player the player
	 * @param id the id
	 */
	public void addContent(Player player, int id) {
		/*
		 * Only permitted content
		 */
		if (!isValid(id)) {
			player.getPA().sendMessage("That cannot be stored in this bag.");
			return;
		}
		/*
		 * The max space
		 */
		if (slotsUnavailable() == getCapacity()) {
			player.getPA().sendMessage("Your bag is full and cannot carry anymore content.");
			return;
		}
		/*
		 * Has item
		 */
		if (ItemAssistant.hasItemInInventory(player, id)) {
			ItemAssistant.deleteItemFromInventory(player, id, 1);
			add(new GameItem(id));
			player.getPA().sendMessage("You add some content to your bag.");
		}
	}

	/**
	 * Checking if valid
	 * 
	 * @param id the id
	 * @return valid
	 */
	public boolean isValid(int id) {
		return Arrays.stream(permitted).anyMatch(gem -> gem == id);
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		if (id == 18338 || id == 18339) {
			withdraw(player);
			return false;
		}
		return false;
	}

	@Override
	public void operate(Player player, int id) {

	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (id == 18338 || id == 18339) {
			if (type == 1) {
				inspect(player);
			} else if (type == 2) {
				withdraw(player);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	@Override
	public boolean useItemOnObject(Player player, int id, int object) {
		return false;
	}

	@Override
	public boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}
