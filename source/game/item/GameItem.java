package game.item;


public class GameItem {

	private final int id;

	private final int amount;

	private final boolean stackable;

	private final transient ItemDefinition definition;

	public GameItem(int id, int amount) {
		ItemDefinition definition = id < 0 || id > ItemDefinition.getDefinitions().length - 1 ? null : ItemDefinition.getDefinitions()[id];

		this.stackable = definition != null && definition.stackable;
		this.id = id;
		this.amount = amount;
		this.definition = definition;
	}

	public GameItem(int id, int amount, boolean stackable) {
		ItemDefinition definition = id < 0 || id > ItemDefinition.getDefinitions().length - 1 ? null
				: ItemDefinition.getDefinitions()[id];

		this.id = id;
		this.amount = amount;
		this.stackable = stackable;
		this.definition = definition;
	}

	public GameItem(int id) {
		this(id, 1);
	}

	public GameItem setAmount(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be less than zero.");
		}
		return new GameItem(id, amount);
	}

	public GameItem decrement(GameItem item) {
		if (item.getId() != id) {
			throw new IllegalArgumentException("The item to decrement by does not match this item id.");
		}
		return new GameItem(id, Math.max(0, amount - item.getAmount()));
	}

	public GameItem increment(GameItem item) {
		if (item.getId() != id) {
			throw new IllegalArgumentException("The item to increment by does not match this item id.");
		}
		return new GameItem(id, amount + item.getAmount());
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isStackable() {
		return stackable;
	}

	@Override
	public String toString() {
		return "GameItem{" +
		       "id=" + id +
		       ", amount=" + amount +
		       ", stackable=" + stackable +
		       '}';
	}

	/**
	 * Gets the definition
	 *
	 * @return the definition
	 */
	public ItemDefinition getDefinition() {
		return definition;
	}
}
