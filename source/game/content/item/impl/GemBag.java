package game.content.item.impl;

import core.GameType;
import game.container.impl.BagItemContainer;
import game.content.item.ItemInteractionComponent;
import game.type.GameTypeIdentity;

/**
 * Handles the gem bag
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {18338})})
public class GemBag extends BagItemContainer {

	/**
	 * The max space
	 */
	private static final int MAX_SPACE = 100;

	/**
	 * Permitted gems
	 */
	private static final int[] PERMITTED = {1623, 1621, 1619, 1617,};

	/**
	 * Represents the gem bag
	 */
	public GemBag() {
		super(MAX_SPACE, PERMITTED);
	}
}