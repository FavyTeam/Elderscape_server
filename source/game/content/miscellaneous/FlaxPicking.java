package game.content.miscellaneous;

import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

import java.util.ArrayList;

/**
 * Flax picking.
 *
 * @author MGT Madness, created on 04-08-2016.
 */
public class FlaxPicking {
	private static ArrayList<String> flaxRemovedList = new ArrayList<String>();

	public static boolean flaxExists(Player player) {
		if (System.currentTimeMillis() - player.flaxDelay <= 590) {
			return true;
		}
		long timeValue = 0;
		for (int index = 0; index < flaxRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (flaxRemovedList.get(index).contains(match)) {
				// Time ore removed.
				String time = flaxRemovedList.get(index).replace(match + " ", "");
				timeValue = Long.parseLong(time);
				break;
			}
		}
		if (System.currentTimeMillis() - timeValue <= 30000) {
			player.playerAssistant.stopAllActions();
			return false;
		}
		deleteFlax(player);
		return true;
	}

	public static void deleteFlax(Player player) {
		int listIndex = -1;
		for (int index = 0; index < flaxRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (flaxRemovedList.get(index).contains(match)) {
				listIndex = index;
				break;
			}
		}
		SoundSystem.sendSound(player, 358, 700);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				container.stop();
			}

			@Override
			public void stop() {
				ItemAssistant.addItem(player, 1779, 1);
			}
		}, 1);
		player.flaxDelay = System.currentTimeMillis();
		player.startAnimation(827);
		player.playerAssistant.stopAllActions();
		if (!Misc.hasOneOutOf(6)) {
			return;
		}

		if (listIndex >= 0) {
			flaxRemovedList.remove(listIndex);
		}
		new Object(-1, player.getObjectX(), player.getObjectY(), player.getHeight(), 1, 10, player.getObjectId(), 50);
		flaxRemovedList.add(player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY() + " " + System.currentTimeMillis());
	}

}
