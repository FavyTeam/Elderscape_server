package game.shop;

import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;

/**
 * Created by Jason MacKeigan on 2018-04-24 at 10:51 AM
 */
public class ShopItemContainer extends ItemContainer {

	public ShopItemContainer() {
		super(40, ItemContainerStackPolicy.STACKABLE, ItemContainerNotePolicy.DENOTE);
	}

}
