package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Created by Owain on 13/08/2017.
 */
public class OrbNoting {

	public enum Notables {

		AIR_ORB(573, 574),
		WATER_ORB(571, 572),
		EARTH_ORB(575, 576),
		FIRE_ORB(569, 570);

		private int unnotedID, notedID;

		Notables(final int unnotedID, final int notedID) {
			this.unnotedID = unnotedID;
			this.notedID = notedID;
		}

		public int getUnnotedID() {
			return unnotedID;
		}

		public int getNotedID() {
			return notedID;
		}
	}

	public static void noteOrbs(final Player c) {
		for (final Notables g : Notables.values()) {
			if (!ItemAssistant.hasItemInInventory(c, g.getUnnotedID())) {
				ItemAssistant.deleteItemFromInventory(c, g.getUnnotedID(), ItemAssistant.itemAmount(c, g.getNotedID()));
				ItemAssistant.addItem(c, g.getNotedID(), ItemAssistant.getFreeInventorySlots(c));
			}
		}
	}
}
