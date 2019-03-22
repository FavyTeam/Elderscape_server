package game.shop;

import game.container.ItemContainer;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 11:13 AM
 */
public class Shop {

	public static final boolean ENABLED = false;

	private final ItemContainer container = new ShopItemContainer();

	public void buy(Player player, int id, int amount, int slot) {
		if (!container.contains(id)) {
			return;
		}
		int bought = container.delete(new GameItem(id, amount), slot);

		if (bought == 0) {
			return;
		}
		ItemAssistant.addItem(player, id, amount);
		player.getPA().sendContainer(container, 3900);
		ItemAssistant.resetItems(player, 3823);
	}

	public void sell(Player player, int id, int amount, int slot) {
		int sold = container.add(new GameItem(id, amount));

		if (sold == 0) {
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, id, sold);
		player.getPA().sendContainer(container, 3900);
		ItemAssistant.resetItems(player, 3823);
	}

	public ItemContainer getContainer() {
		return container;
	}

}
