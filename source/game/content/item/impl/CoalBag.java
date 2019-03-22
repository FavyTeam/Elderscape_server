package game.content.item.impl;

import core.GameType;
import game.container.impl.BagItemContainer;
import game.content.item.ItemInteractionComponent;
import game.type.GameTypeIdentity;

/**
 * Handles the coal bag
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {18339})})
public class CoalBag extends BagItemContainer {

	/**
	 * The max space
	 */
	private static final int MAX_SPACE = 81;

	/**
	 * Permitted gems
	 */
	private static final int[] PERMITTED = {453};

	/**
	 * The coal bag
	 */
	public CoalBag() {
		super(MAX_SPACE, PERMITTED);
	}
}
