package game.content.minigame.single_minigame.vorkath;

import game.container.ItemContainer;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.minigame.*;
import game.item.ItemAssistant;
import game.player.Boundary;
import game.player.Player;

import java.util.Set;

/**
 * Created by Jason MacKeigan on 2018-06-14 at 10:17 AM
 */
public class VorkathMinigameArea extends MinigameArea {

	private static final Boundary BOUNDARY = new Boundary(2253, 4053, 2290, 4086);

	public VorkathMinigameArea(int height) {
		super(MinigameAreaKey.VORKATH, MinigameAreaCombatSafety.SAFE, MinigameAreaDeathSafety.UNSAFE, BOUNDARY.withHeight(height));
	}

	/**
	 * Referenced only when {@link #managesItemsForUnsafeDeath()} returns true.
	 *
	 * @param playerParticipant the participant.
	 */
	@Override
	public void onManageItemsForUnsafeDeath(MinigamePlayerParticipant playerParticipant) {
		super.onManageItemsForUnsafeDeath(playerParticipant);

		Player player = playerParticipant.getEntity();

		ItemsKeptOnDeath.getItemsKeptOnDeath(player, false, false);

		ItemsKeptOnDeath.deleteItemsKeptOnDeath(player);

		ItemContainer itemsLost = player.getVorkathLostItems();

		player.itemsToContainer(itemsLost);

		ItemAssistant.deleteAllItems(player);
	}

	/**
	 * Determines whether or not the items removal portion of an unsafe death is managed by
	 * this minigame area.
	 *
	 * @return by default false.
	 */
	@Override
	public boolean managesItemsForUnsafeDeath() {
		return true;
	}
}
