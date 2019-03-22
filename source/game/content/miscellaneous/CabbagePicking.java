package game.content.miscellaneous;

import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

import java.util.ArrayList;

/**
 * Cabbage picking.
 *
 * @author MGT Madness, created on 04-08-2016.
 */
public class CabbagePicking {
	private static ArrayList<String> cabbageRemovedList = new ArrayList<String>();

	public static boolean cabbageExists(Player player) {
		if (System.currentTimeMillis() - player.flaxDelay <= 2000) {
			return true;
		}
		long timeValue = 0;
		for (int index = 0; index < cabbageRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (cabbageRemovedList.get(index).contains(match)) {
				// Time ore removed.
				String time = cabbageRemovedList.get(index).replace(match + " ", "");
				timeValue = Long.parseLong(time);
				break;
			}
		}
		if (System.currentTimeMillis() - timeValue <= 30000) {
			player.playerAssistant.stopAllActions();
			return false;
		}
		deleteCabbage(player);
		return true;
	}

	public static void deleteCabbage(Player player) {
		int listIndex = -1;
		for (int index = 0; index < cabbageRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (cabbageRemovedList.get(index).contains(match)) {
				listIndex = index;
				break;
			}
		}

		if (listIndex >= 0) {
			cabbageRemovedList.remove(listIndex);
		}
		player.flaxDelay = System.currentTimeMillis();

		SoundSystem.sendSound(player, 358, 700);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				container.stop();
			}

			@Override
			public void stop() {
				ItemAssistant.addItem(player, 1965, 1);
			}
		}, 1);
		player.startAnimation(827);
		new Object(-1, player.getObjectX(), player.getObjectY(), player.getHeight(), 1, 10, player.getObjectId(), 50);
		cabbageRemovedList.add(player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY() + " " + System.currentTimeMillis());
		player.playerAssistant.stopAllActions();
	}

}
