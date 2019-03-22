package game.content.item.chargeable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import core.Server;
import core.ServerConstants;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import utility.Misc;

/**
 * Created by Jason MK on 2018-09-17 at 10:43 AM
 */
public class ChargeableCollection {
    /**
     * A mapping of key value pairs where the key is an integer representing the unique Chargeable element,
     * and the value being a collection of existing charges for this Chargeable element.
     */
    private final Map<Integer, Collection<GameItem>> charges;

    /**
     * Creates a new collection using an empty map.
     */
    public ChargeableCollection() {
        this(new HashMap<>());
    }

    /**
     * Creates a new collection using the given map.
     *
     * @param charges
     *            the charges being added to this collection.
     */
    public ChargeableCollection(Map<Integer, Collection<GameItem>> charges) {
        this.charges = charges;
    }

    /**
     * Adds as much of a charge as possible to the given Chargeable.
     *
     * @param player
     *            the player charging the item.
     * @param chargeable
     *            the type of item being charged.
     * @param item
     *            the item being added as a charge.
     */
    public void addCharge(Player player, Chargeable chargeable, int item) {
        if (isFull(chargeable)) {
            player.getPA().sendMessageF("Your %s is full and cannot be charged anymore.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
            return;
        }
        Collection<GameItem> resources = getResourcesInInventory(player, chargeable);

        if (resources.isEmpty()) {
            player.getPA().sendMessageF("You don't have any of the resources to charge your %s.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
            return;
        }

        if (chargeable.getResources().stream().noneMatch(i -> i.getId() == item)) {
            player.getPA().sendMessageF("You cannot add this item to your %s.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
            return;
        }
        GameItem maximum = chargeable.getMaximumResources().stream().filter(i -> i.getId() == item).findAny().orElse(null);

        if (maximum == null) {
            return;
        }
        int amount = ItemAssistant.getItemAmount(player, item);

        if (amount > maximum.getAmount()) {
            amount = maximum.getAmount();
        }
        GameItem current = getCharges(chargeable).stream().filter(i -> i.getId() == item).findAny().orElse(null);

        if (current != null) {
            if (amount + current.getAmount() > maximum.getAmount()) {
                amount = maximum.getAmount() - current.getAmount();
            }
        }
        ItemAssistant.deleteItemFromInventory(player, item, amount);
        increase(chargeable, new GameItem(item, amount));
		player.getPA().sendMessageF("You add <col=00a000>%s <col=000000>%s to your %s.", Misc.formatNumber(amount), ItemDefinition.getDefinitions()[item].name, ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
    }

    /**
     * Attempts to charge a specific item with the required resources. If charges already
     * exist for this item the player will receive them back before charging.
     *
     * @param player
     *            the player charging the uncharged item.
     * @param chargeable
     *            the item being charged.
     */
    public void charge(Player player, Chargeable chargeable) {
        Collection<GameItem> charges = getCharges(chargeable);

        if (!charges.isEmpty()) {
            charges.forEach(item -> ItemAssistant.addItemToInventoryOrDrop(player, item.getId(), item.getAmount()));
            player.getPA().sendMessage("You already had an uncharged item so you retrieved the resources. Your items");
            player.getPA().sendMessage("are either in your inventory or on the ground. Try and charge again.");
            this.charges.remove(chargeable.getId());
            return;
        }

        if (!ItemAssistant.hasItemInInventory(player, chargeable.getUnchargedId())) {
            player.getPA().sendMessageF("You don't have a %s to charge.", ItemDefinition.getDefinitions()[chargeable.getUnchargedId()].name);
            return;
        }
        Collection<GameItem> resources = getResourcesInInventory(player, chargeable);

        if (resources.isEmpty()) {
            player.getPA().sendMessageF("You don't have any resources to charge your %s.",
                    ItemDefinition.getDefinitions()[chargeable.getUnchargedId()].name);
            return;
        }

        Collection<GameItem> available = getResourcesInInventory(player, chargeable);

        if (available.isEmpty()) {
            return;
        }
        for (GameItem required : chargeable.getRequiredResources()) {
            if (available.stream().filter(item -> item.getId() == required.getId()).mapToInt(GameItem::getAmount).sum() < required.getAmount()) {
				player.getPA().sendMessageF("You need at least <col=00a000>%s <col=000000>%s to charge this item.", required.getAmount(), ItemDefinition.getDefinitions()[required.getId()].name);
                return;
            }
        }
        for (GameItem item : chargeable.getRequiredResources()) {
            ItemAssistant.deleteItemFromInventory(player, item.getId(), item.getAmount());
            increase(chargeable, item);
        }
        ItemAssistant.deleteItemFromInventory(player, chargeable.getUnchargedId(), 1);

        ItemAssistant.addItem(player, chargeable.getChargedId(), 1);

        player.getAttributes().put(Player.CHARGEABLE_COLLECTION_KEY, this);
		for (GameItem item : chargeable.getRequiredResources()) {
			player.getPA().sendMessageF("You charge your %s with <col=00a000>%s <col=000000>%s.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name, Misc.formatNumber(item.getAmount()), item.getDefinition().name);
		}
    }

    /**
     * Attempts to uncharge the the item and return the resources to the player.
     *
     * @param player
     *            the player uncharging the item.
     * @param chargeable
     *            the type of item being uncharged.
     */
    public void uncharge(Player player, Chargeable chargeable) {
        if (!ItemAssistant.hasItemInInventory(player, chargeable.getChargedId())) {
            player.getPA().sendMessageF("You do not have a %s to uncharge.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
            return;
        }
        Collection<GameItem> charges = getCharges(chargeable);

        if (!charges.isEmpty()) {
            charges.forEach(item -> ItemAssistant.addItemToInventoryOrDrop(player, item.getId(), item.getAmount()));
        }
		for (GameItem charge : charges) {
			player.getPA().sendMessage("You remove <col=00a000>" + Misc.formatNumber(charge.getAmount()) + "<col=000000> "
                    + ItemDefinition.getDefinitions()[charge.getId()].name + " from your " +
                    ItemAssistant.getItemName(chargeable.getChargedId()) + ".");
		}
        charges.clear();
        this.charges.remove(chargeable.getId());
        ItemAssistant.deleteItemFromInventory(player, chargeable.getChargedId(), 1);
        ItemAssistant.addItem(player, chargeable.getUnchargedId(), 1);
        player.getAttributes().put(Player.CHARGEABLE_COLLECTION_KEY, this);
    }

    /**
     * Checks the current amount of charges in the item.
     *
     * @param player
     *            the player checking the item charges.
     * @param chargeable
     *            the chargeable item being checked.
     */
    public void check(Player player, Chargeable chargeable) {
        Collection<GameItem> charges = getCharges(chargeable);

        if (charges.isEmpty()) {
            player.getPA().sendMessageF("You don't have any charges in your %s.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
            return;
        }
        for (GameItem charge : charges) {
			player.getPA().sendMessageF("Your %s currently has a charge of <col=00a000>%s<col=000000> %s.",
                    ItemDefinition.getDefinitions()[chargeable.getChargedId()].name, Misc.formatNumber(charge.getAmount()),
                    ItemDefinition.getDefinitions()[charge.getId()].name);
        }
    }

    /**
     * Determines if the chargeable is empty, meaning without charges.
     *
     * @param chargeable
     *            the chargeable we're checking is empty or not.
     * @return {@code true} if the chargeable is empty.
     */
    public boolean isEmpty(Chargeable chargeable) {
        return getCharges(chargeable).isEmpty();
    }

	/**
	 * Attempts to replace the equipped charged item with the uncharged variant.
	 *
	 * @param player
	 *            the player uncharging the item.
	 * @param chargeable
	 *            the type of item being uncharged.
	 */
	public void switchToUncharged(Player player, Chargeable chargeable) {
		ItemAssistant.deleteEquipment(player, chargeable.getChargedId(), ServerConstants.WEAPON_SLOT);
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.WEAPON_SLOT, chargeable.getUnchargedId(), 1, false);
		player.getPA().sendMessageF("Your %s has run out of charges.", ItemDefinition.getDefinitions()[chargeable.getChargedId()].name);
	}

	/**
	 * When a player dies with a chargeable item, this will check if the killer receives the charges as its raw item form or not.
	 *
	 * @param killer
	 *            the player that is the killer.
	 * @param victim
	 *            the player that is the victim.
	 */
	public void onDeath(Player killer, Player victim) {
		for (Chargeable chargeable : Chargeable.values()) {
			if (killer == null) {
				return;
			}
			if (victim == null) {
				return;
			}
			final ChargeableCollection collection = victim.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY);

			final Collection<GameItem> charges = collection.getCharges(chargeable);

			boolean dropped = chargeable.isResourcesDroppedOnDeath();

			final int amountInInventory = ItemAssistant.getItemAmount(victim, chargeable.getChargedId());

			boolean worn = ItemAssistant.wearing(victim, chargeable.getChargedId(), ServerConstants.WEAPON_SLOT);

			if (worn) {
				ItemAssistant.deleteEquipment(victim, chargeable.getChargedId(), ServerConstants.WEAPON_SLOT);
				ItemAssistant.addItem(victim, chargeable.getUnchargedId(), 1);
			}
			if (amountInInventory > 0) {
				ItemAssistant.deleteItemFromInventory(victim, chargeable.getChargedId(), 1);

				if (ItemAssistant.getItemAmount(victim, chargeable.getChargedId()) < amountInInventory) {
					ItemAssistant.addItem(victim, chargeable.getUnchargedId(), 1);
				}
			}
			if (dropped) {
				charges.forEach(item -> {
					Server.itemHandler.createGroundItem(killer, item.getId(), victim.getX(), victim.getY(), victim.getHeight(), item.getAmount(), false, 0, false, victim.getPlayerName(), killer.getPlayerName(), "", "", "");
				});
				killer.getPA().sendMessage("" + dropped);
			}
			collection.clear(chargeable);
			victim.getAttributes().put(Player.CHARGEABLE_COLLECTION_KEY, collection);
		}
	}

    /**
     * Increases the number of charges for a given item in a chargeable.
     *
     * @param chargeable
     *            the item having the charge increased.
     * @param item
     *            the item we're increasing by.
     */
    public void increase(Chargeable chargeable, GameItem item) {
        Collection<GameItem> charges = getCharges(chargeable);

        GameItem existing = charges.stream().filter(i -> i.getId() == item.getId()).findAny().orElse(null);

        if (existing == null) {
            existing = item;
        } else {
            existing = existing.increment(item);
        }
        charges.removeIf(charge -> charge.getId() == item.getId());
        charges.add(existing);

        this.charges.put(chargeable.getId(), charges);
    }

    /**
     * Attempts to decrease all charges in this collection by a specified amount.
     *
     * @param chargeable
     *            the chargeable item having the charges decreased.
     */
    public void decreaseAll(Chargeable chargeable, int amount) {
        chargeable.getResources().forEach(item -> decrease(chargeable, item.setAmount(amount)));
    }

    /**
     * Decreases the charge of a chargeable by the given parameter.
     *
     * @param chargeable
     *            the type of item we're decreasing
     * @param item
     *            the item we're decreasing by
     */
    public void decrease(Chargeable chargeable, GameItem item) {
        Collection<GameItem> charges = getCharges(chargeable);

        if (charges.isEmpty()) {
            return;
        }
        GameItem existing = charges.stream().filter(i -> i.getId() == item.getId()).findAny().orElse(null);

        if (existing == null) {
            return;
        }
        existing = existing.decrement(item);

        charges.removeIf(c -> c.getId() == item.getId());

        if (existing.getAmount() > 0) {
            charges.add(existing);
        }
        this.charges.put(chargeable.getId(), charges);
    }

    /**
     * Removes all charges from the chargeable item.
     *
     * @param chargeable
     *            the type of item that is having the charges removed.
     */
    public void clear(Chargeable chargeable) {
        getCharges(chargeable).clear();
        charges.remove(chargeable.getId());
    }

    /**
     * A collection of charges for a given chargeable.
     *
     * @param chargeable
     *            the chargeable we're checking for charges.
     * @return a collection of charges for a given item.
     */
    public Collection<GameItem> getCharges(Chargeable chargeable) {
        return charges.getOrDefault(chargeable.getId(), new HashSet<>());
    }

    /**
     * Attempts to retrieve the existing resources in the inventory that the player has.
     *
     * @param player
     *            the player we're checking for resources.
     * @param chargeable
     *            the item being charged.
     * @return a collection of resources in the players inventory.
     */
    private Collection<GameItem> getResourcesInInventory(Player player, Chargeable chargeable) {
        Collection<GameItem> resources = new HashSet<>();

        for (GameItem item : chargeable.getResources()) {
            int amount = ItemAssistant.getItemAmount(player, item.getId());

            if (amount == 0) {
                continue;
            }
            resources.add(new GameItem(item.getId(), amount));
        }

        return resources;
    }

    /**
     * Determines if a chargeable is full by ensuring that each resources available isn't full.
     *
     * @param chargeable
     *            the type of item we're checking is charged or not.
     * @return whether or not the item is full of charges.
     */
    private boolean isFull(Chargeable chargeable) {
        Collection<GameItem> charges = getCharges(chargeable);

        if (charges.isEmpty()) {
            return false;
        }
        for (GameItem item : charges) {
            GameItem maximum = chargeable.getMaximumResources().stream().filter(i -> i.getId() == item.getId()).findAny().orElse(null);

            if (maximum == null) {
                continue;
            }
            if (item.getAmount() < maximum.getAmount()) {
                return false;
            }
        }
        return true;
    }

}
