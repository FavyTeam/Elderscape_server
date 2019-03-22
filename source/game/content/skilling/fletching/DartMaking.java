package game.content.skilling.fletching;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;

public class DartMaking {
	private enum DartData {
		BRONZE_DART(806, 819, 314, 10, 15),
		IRON_DART(807, 820, 314, 22, 30),
		STEEL_DART(808, 821, 314, 37, 75),
		MITHRIL_DART(809, 822, 314, 52, 120),
		ADAMANT_DART(810, 823, 314, 67, 150),
		RUNE_DART(811, 824, 314, 81, 186),
		DRAGON_DART(11230, 11232, 314, 95, 225);

		private int product, startid, secondary, level;

		private int xp;

		private DartData(final int product, final int startid, final int secondary, final int level, final int xp) {

			this.product = product;
			this.startid = startid;
			this.secondary = secondary;
			this.level = level;
			this.xp = xp;
		}

		public int getproduct() {
			return product;
		}

		public int getstartid() {
			return startid;
		}

		public int getsecondary() {
			return secondary;
		}

		public int getlevel() {
			return level;
		}

		public int getxp() {
			return xp;
		}
	}

	public static void createDarts(final Player c, final int itemUsed, final int usedWith) {
		for (final DartData d : DartData.values()) {
			final int itemId = (itemUsed == 314 ? usedWith : itemUsed);
			if (itemId == d.getstartid()) {
				if (ItemAssistant.hasItemAmountInInventory(c, d.getstartid(), 15)) {
					if (ItemAssistant.hasItemAmountInInventory(c, d.getsecondary(), 15)) {
						if (c.baseSkillLevel[ServerConstants.FLETCHING] < d.getlevel()) {
							c.getPA().sendMessage("You need a fletching level of " + d.getlevel() + " to make " + ItemAssistant.getItemName(d.getproduct()) + "s.");
							return;
						}
						ItemAssistant.deleteItemFromInventory(c, d.getstartid(), 15);
						ItemAssistant.deleteItemFromInventory(c, d.getsecondary(), 15);
						ItemAssistant.addItem(c, d.getproduct(), 15);
						Skilling.addSkillExperience(c, d.getxp(), ServerConstants.FLETCHING, false);
					}
				}
			}
		}
	}
}
