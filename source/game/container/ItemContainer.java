package game.container;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;

/**
 * Created by Jason MacKeigan on 2018-04-20 at 10:15 AM
 */
public class ItemContainer {

	private final int capacity;

	private final ItemContainerStackPolicy stackPolicy;

	private final ItemContainerNotePolicy notePolicy;

	private final GameItem[] items;

	public ItemContainer(int capacity, ItemContainerStackPolicy stackPolicy,
						 ItemContainerNotePolicy notePolicy) {
		this.capacity = capacity;
		this.stackPolicy = stackPolicy;
		this.notePolicy = notePolicy;
		this.items = new GameItem[capacity];
	}

	public ItemContainer(int capacity, ItemContainerStackPolicy stackPolicy, ItemContainerNotePolicy notePolicy, GameItem[] items) {
		this(capacity, stackPolicy, notePolicy);

		for (int index = 0; index < items.length; index++) {
			if (index >= capacity) {
				break;
			}
			this.items[index] = items[index];
		}
	}

	public int delete(int slot) {
		GameItem item = items[slot];

		if (item == null) {
			return 0;
		}
		return delete(item, slot);
	}

	public int delete(GameItem item, int slot) {
		ItemDefinition definition = ItemDefinition.getDefinitions()[item.getId()];

		if (definition == null) {
			return 0;
		}
		int itemToDelete = item.getId();

		if (items[slot] == null || items[slot].getId() != itemToDelete) {
			return 0;
		}
		if (stackPolicy == ItemContainerStackPolicy.STACKABLE || definition.stackable) {
			final int amountInSlot = items[slot].getAmount();

			if (amountInSlot <= 0) {
				return 0;
			}
			int amountToDelete = item.getAmount();

			if (amountInSlot < item.getAmount()) {
				amountToDelete = item.getAmount();
			}

			if (amountInSlot == amountToDelete) {
				items[slot] = null;
				return amountToDelete;
			}
			items[slot] = new GameItem(itemToDelete, amountInSlot - amountToDelete);
			return amountToDelete;
		} else {
			int amountDeleted = 0;

			while (amountDeleted < item.getAmount()) {
				int slotForItem = findSlotOrNone(itemToDelete);

				if (slotForItem == -1) {
					break;
				}
				items[slotForItem] = null;
				amountDeleted++;
			}

			return amountDeleted;
		}
	}

	public int delete(GameItem item) {
		int slotForItem = findSlotOrNone(item.getId());

		if (slotForItem == -1) {
			return 0;
		}
		return delete(item, slotForItem);
	}

	public int add(GameItem item, int slot) {
		ItemDefinition itemDefinition = ItemDefinition.getDefinitions()[item.getId()];

		if (itemDefinition == null) {
			return 0;
		}
		int itemToAdd = item.getId();

		if (notePolicy == ItemContainerNotePolicy.DENOTE && itemDefinition.note) {
			itemToAdd = ItemAssistant.getUnNotedItem(itemToAdd);
		}
		itemDefinition = ItemDefinition.getDefinitions()[itemToAdd];

		if (itemDefinition == null) {
			return 0;
		}
		int slotForItem = findSlotOrNone(itemToAdd);

		if (stackPolicy == ItemContainerStackPolicy.STACKABLE && slotForItem != -1 && slot != slotForItem) {
			slot = slotForItem;
		}

		if (slot < 0 || slot > capacity - 1) {
			return 0;
		}

		if (items[slot] != null && items[slot].getId() != itemToAdd) {
			return 0;
		}

		if (items[slot] != null && !items[slot].isStackable() && stackPolicy == ItemContainerStackPolicy.UNSTACKABLE) {
			return 0;
		}

		if (itemDefinition.stackable || stackPolicy == ItemContainerStackPolicy.STACKABLE) {
			int existingAmount = amount(itemToAdd);

			int amountToAdd = item.getAmount();

			long augmentedAmount = (long) existingAmount + (long) amountToAdd;

			if (augmentedAmount > Integer.MAX_VALUE) {
				amountToAdd = Integer.MAX_VALUE - existingAmount;

				if (amountToAdd > item.getAmount()) {
					amountToAdd = item.getAmount();
				}
			}

			if (amountToAdd <= 0) {
				return 0;
			}
			items[slot] = new GameItem(itemToAdd, existingAmount + amountToAdd);

			return amountToAdd;
		} else {
			int amountAdded = 0;

			while (amountAdded < item.getAmount()) {
				try {
					items[items[slot] == null ? slot : findFirstOpenSlot()] = new GameItem(itemToAdd, 1);

					amountAdded++;
				} catch (ItemContainerFullException icfe) {
					return amountAdded;
				}
			}

			return amountAdded;
		}
	}

	public int add(GameItem item) {
		try {
			int slot = -1;

			ItemDefinition definition = ItemDefinition.getDefinitions()[item.getId()];

			if (definition == null) {
				return 0;
			}
			GameItem itemToAdd = new GameItem(item.getId(), item.getAmount());

			if (notePolicy == ItemContainerNotePolicy.DENOTE && definition.note) {
				itemToAdd = new GameItem(ItemAssistant.getUnNotedItem(item.getId()), item.getAmount());
			}

			if (itemToAdd.isStackable() || stackPolicy == ItemContainerStackPolicy.STACKABLE) {
				slot = findSlotOrNone(item.getId());
			}

			if (slot == -1) {
				slot = findFirstOpenSlot();
			}
			return add(item, slot);
		} catch (ItemContainerFullException icfe) {
			return 0;
		}
	}

	public int amount(int id) {
		int amount = 0;

		for (int index = 0; index < capacity; index++) {
			GameItem itemAtIndex = items[index];

			if (itemAtIndex == null) {
				continue;
			}

			if (itemAtIndex.getId() == id) {
				amount += itemAtIndex.getAmount();
			}
		}
		return amount;
	}

	public boolean contains(int id, int amount, int slot) {
		return items[slot] != null && items[slot].getId() == id && items[slot].getAmount() >= amount;
	}

	public boolean contains(int id, int amount) {
		return amount(id) >= amount;
	}

	public boolean contains(int id) {
		return contains(id, 1);
	}

	public void forNonNull(BiConsumer<Integer, GameItem> itemConsumer) {
		forEach(itemConsumer, (index, item) -> item != null);
	}

	public void forAll(BiConsumer<Integer, GameItem> itemConsumer) {
		forEach(itemConsumer, (index, item) -> true);
	}

	public void forEach(BiConsumer<Integer, GameItem> itemBiConsumer, BiPredicate<Integer, GameItem> itemPredicate) {
		for (int index = 0; index < capacity; index++) {
			GameItem item = items[index];

			if (itemPredicate.test(index, item)) {
				itemBiConsumer.accept(index, item);
			}
		}
	}

	public int findSlotOrNone(int id) {
		for (int index = 0; index < capacity; index++) {
			GameItem itemAtIndex = items[index];

			if (itemAtIndex != null && itemAtIndex.getId() == id) {
				return index;
			}
		}
		return -1;
	}

	public boolean isEmpty() {
		return slotsAvailable() == capacity;
	}

	public int slotsAvailable() {
		int amount = 0;

		for (GameItem item : items) {
			if (item == null) {
				amount++;
			}
		}
		return amount;
	}

	public int slotsUnavailable() {
		return capacity - slotsAvailable();
	}

	public void clear() {
		for (int index = 0; index < capacity; index++) {
			if (items[index] !=  null) {
				items[index] = null;
			}
		}
	}

	private int findFirstOpenSlot() throws ItemContainerFullException {
		for (int index = 0; index < capacity; index++) {
			if (items[index] == null) {
				return index;
			}
		}
		throw new ItemContainerFullException("Cannot find any open slots.");
	}

	public int getCapacity() {
		return capacity;
	}

	public ItemContainerStackPolicy getStackPolicy() {
		return stackPolicy;
	}

	public ItemContainerNotePolicy getNotePolicy() {
		return notePolicy;
	}

	@Override
	public String toString() {
		return "ItemContainer{" +
		       "capacity=" + capacity +
		       ", stackPolicy=" + stackPolicy +
		       ", notePolicy=" + notePolicy +
		       ", items=" + Arrays.toString(items) +
		       '}';
	}
}
