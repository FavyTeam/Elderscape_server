package game.content.miscellaneous;

import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Watering can filling.
 *
 * @author MGT Madness, created on 09-08-2016.
 */
public class WateringCan {

	public static void refill(final Player player) {
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				int itemToDelete = 0;
				for (int i = 0; i < player.playerItems.length; i++) {
					if (player.playerItems[i] >= 5332 && player.playerItems[i] <= 5340 && player.playerItems[i] != 5333) {
						itemToDelete = player.playerItems[i] - 1;
					}
				}
				if (itemToDelete == 0) {
					container.stop();
					return;
				}
				ItemAssistant.deleteItemFromInventory(player, itemToDelete, 1);
				ItemAssistant.addItem(player, 5340, 1);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 1);

	}

}
