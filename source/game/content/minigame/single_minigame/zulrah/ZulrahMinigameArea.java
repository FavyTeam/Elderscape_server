package game.content.minigame.single_minigame.zulrah;

import game.container.ItemContainer;
import game.content.combat.Death;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.minigame.*;
import game.content.worldevent.BloodKey;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.player.Boundary;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-04 at 1:18 PM
 */
public class ZulrahMinigameArea extends MinigameArea {

	public ZulrahMinigameArea(Boundary boundary) {
		super(MinigameAreaKey.ZULRAH, MinigameAreaCombatSafety.SAFE, MinigameAreaDeathSafety.UNSAFE, boundary);
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

		ItemContainer itemsLost = player.getZulrahLostItems();

		for (int index = 0; index < player.playerItems.length; index++) {
			int item = player.playerItems[index] - 1;

			int amount = player.playerItemsN[index];

			if (item <= 0 || amount <= 0) {
				continue;
			}
			itemsLost.add(new GameItem(item, amount));
		}

		for (int index = 0; index < player.playerEquipment.length; index++) {
			int item = player.playerEquipment[index];

			int amount = player.playerEquipmentN[index];

			if (item <= 0 || amount <= 0) {
				continue;
			}
			itemsLost.add(new GameItem(item, amount));
		}

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
