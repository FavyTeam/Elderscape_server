package game.item;

import core.ServerConstants;
import game.player.Player;
import utility.Misc;

public class UntradeableRepairing {
	/**
	 * The list of untradeable items that break on death.
	 */
	public static enum Untradeables {
		FIRE_CAPE(20445, 6570, 50000),
		INFERNAL_CAPE(21287, 21295, 50000),
		FIRE_MAX_CAPE(20447, 13329, 50000),
		INFERNAL_MAX_CAPE(21289, 21285, 50000),
		BRONZE_DEFENDER(20449, 8844, 1000),
		IRON_DEFENDER(20451, 8845, 2000),
		STEEL_DEFENDER(20453, 8846, 2500),
		BLACK_DEFENDER(20455, 8847, 5000),
		MITH_DEFENDER(20457, 8848, 15000),
		ADDY_DEFENDER(20459, 8849, 25000),
		RUNE_DEFENDER(20461, 8850, 35000),
		DRAG_DEFENDER(20463, 12954, 40000),
		VOID_TOP(20465, 8839, 45000),
		ELITE_VOID_TOP(20467, 13072, 50000),
		VOID_ROBE(20469, 8840, 45000),
		ELITE_VOID_ROBE(20471, 13073, 50000),
		VOID_GLOVES(20475, 8842, 30000),
		VOID_MAGE_HELM(20477, 11663, 40000),
		VOID_RANGER_HELM(20479, 11664, 40000),
		VOID_MELEE_HELM(20481, 11665, 40000),
		FIGHTER_HAT(20507, 10548, 45000),
		RANGER_HAT(20509, 10550, 45000),
		HEALER_HAT(20511, 10547, 45000),
		FIGHTER_TORSO(20513, 10551, 50000),
		PENANCE_SKIRT(20515, 10555, 20000),
		SARA_HALO(20537, 12637, 25000),
		ZAMORAK_HALO(20539, 12638, 25000),
		GUTHIX_HALO(20541, 12639, 25000),
		VETERAN_CAPE(16264, 14013, 50000),
		COMP_CAPE(16265, 14011, 50000);

		private int brokenId;

		private int fixedId;

		private int cost;

		Untradeables(final int brokenId, final int fixedId, final int cost) {
			this.brokenId = brokenId;
			this.fixedId = fixedId;
			this.cost = cost;
		}

		public int getBrokenId() {
			return brokenId;
		}

		public int getFixedId() {
			return fixedId;
		}

		public int getCost() {
			return cost;
		}

	}

	/**
	 * Repair the equipment.
	 *
	 * @param player The associated player.
	 */

	public static void FixItem(Player player) {
		boolean repaired = false;
		for (final Untradeables data : Untradeables.values()) {
			int costPerItem = data.getCost();

			if (player.isLegendaryDonator()) {
				costPerItem = data.getCost() - data.getCost(); //100% discount
			} else if (player.isExtremeDonator()) {
				costPerItem = data.getCost() / 100 * 25; //75% discount
			} else if (player.isSuperDonator()) {
				costPerItem = data.getCost() / 100 * 50; //50% discount
			} else if (player.isDonator()) {
				costPerItem = data.getCost() / 100 * 75; //25% discount
			}
			if (!ItemAssistant.hasItemInInventory(player, data.getBrokenId())) {
				continue;
			}
			int currency = ServerConstants.getMainCurrencyId();
			int cashAmount = ItemAssistant.getItemAmount(player, currency);
			int amount = ItemAssistant.getItemAmount(player, data.getBrokenId());
			int totalPrice = costPerItem * amount;
			if (totalPrice > cashAmount) {
				player.getDH().sendNpcChat("You don't have enough " + ServerConstants.getMainCurrencyName() + " to do this.",
				                           "The total cost of repairing your items is " + Misc.formatRunescapeStyle(totalPrice) + " " + ServerConstants.getMainCurrencyName() + ".",
				                           591);
				return;
			}
			if (ItemAssistant.hasItemAmountInInventory(player, data.getBrokenId(), amount)) {
				ItemAssistant.deleteItemFromInventory(player, data.getBrokenId(), amount);
				ItemAssistant.deleteItemFromInventory(player, currency, totalPrice);
				ItemAssistant.addItem(player, data.getFixedId(), amount);
				player.getDH().sendNpcChat("All fixed!", 591);
				repaired = true;
			}
		}
		if (!repaired) {
			player.getDH().sendNpcChat("You don't have any untradeables for me to repair.", 591);
		}
	}

	public static void displayRepairableItemInterface(Player player) {
		for (int index = 0; index < ServerConstants.REPAIRABLE_ITEMS_OSRS.length; index++) {
			int item = ServerConstants.REPAIRABLE_ITEMS_OSRS[index] == 0 ? -1 : ServerConstants.REPAIRABLE_ITEMS_OSRS[index];
			player.getPA().sendFrame34(13506, item, index, 1);
			player.getPA().sendFrame126("This is a list of untradeable items that will break upon death\\n in PvP combat, below 30 Wilderness.", 13507);
		}
		player.getPA().displayInterface(13500);
	}
}
