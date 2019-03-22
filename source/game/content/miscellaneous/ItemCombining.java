package game.content.miscellaneous;

import core.GameType;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Handles item combining and dismantling, as used with items such as ornament kits.
 *
 * @author Owain, created on 08-02-18
 */

public class ItemCombining {

	/**
	 * The dragonbone upgrade kit
	 */
	private static final int DRAGONBONE_UPGRADE_KIT = 24_352;

	/**
	 * Combine two items together.
	 */

	public static void combine(Player player) {
		for (ItemCombination data : ItemCombination.values()) {
			if (ItemAssistant.hasItemInInventory(player, data.getItem1()) && ItemAssistant.hasItemInInventory(player, data.getItem2())) {
				if (!GameType.getGameType(data.getType())) {
					continue;
				}
				int slot = ItemAssistant.getItemSlot(player, data.getItem2());
				ItemAssistant.deleteItemFromInventory(player, data.getItem2(), slot, 1);
				ItemAssistant.deleteItemFromInventory(player, data.getItem1(), 1);
				ItemAssistant.addItem(player, data.getResult(), 1);
				if (data.statement) {
					player.getDH().sendItemChat("", "You combine the @blu@" + ItemAssistant.getItemName(data.getItem1()) + "@bla@ with the",
					                            "@blu@" + ItemAssistant.getItemName(data.getItem2()) + "@bla@ to create a ",
					                            "@blu@" + ItemAssistant.getItemName(data.getResult()) + "@bla@.", data.getResult(), 200, data.getOffset1(), data.getOffset2());
				} else {
					player.getPA().closeInterfaces(true);
					player.getPA().sendMessage(
							"You combine the " + ItemAssistant.getItemName(data.getItem1()) + " with the " + ItemAssistant.getItemName(data.getItem2()) + " to create a "
							+ ItemAssistant.getItemName(data.getResult()) + ".");
				}
				return;
			}
		}

	}

	/**
	 * The dialogue message that appears when a player clicks the dismantle option on the item.
	 */

	public static void showCombinationWarning(Player player, int item2) {
		for (ItemCombination data : ItemCombination.values()) {
			int itemReward = data.getResult();
			player.getDH()
			      .sendItemChat("" + ItemAssistant.getItemName(itemReward) + " creation.", "", "Are you sure you want to combine these items?", "", data.getItem2(), 200, 10, 0);
			player.nextDialogue = 701;
		}

	}

	/**
	 * Used in the drop item packet.
	 */

	public static boolean isDismantlable(Player player, int itemId, int itemSlot, boolean isThirdClickItemPacket) {

		for (ItemCombination data : ItemCombination.values()) {
			if (itemId == data.getResult() && data.canDismantle()) {
				if (!GameType.getGameType(data.getType())) {
					continue;
				}
				if (data.isThirdClickItemPacketDismantle() && !isThirdClickItemPacket) {
					return false;
				}
				if (ItemAssistant.getFreeInventorySlots(player) < 1) {
					player.getDH().sendStatement("You need at least one free inventory slot.");
					return true;
				}
				ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
				ItemAssistant.addItem(player, data.getItem2(), 1);
				ItemAssistant.addItem(player, data.getItem1(), 1);
				player.getPA().closeInterfaces(true);
				player.getPA().sendMessage("You dismantle the " + ItemAssistant.getItemName(itemId) + ".");
				return true;
			}
		}
		return false;
	}

	/**
	 * Used in the drop item packet.
	 */

	public static boolean isCombinable(Player player, int itemUsedId, int itemUsedWithId) {
		for (ItemCombination data : ItemCombination.values()) {
			if (itemUsedId == data.getItem1() && itemUsedWithId == data.getItem2() || itemUsedId == data.getItem2() && itemUsedWithId == data.getItem1()) {
				if (!GameType.getGameType(data.getType())) {
					continue;
				}
				ItemAssistant.deleteItemFromInventory(player, data.getItem2(), 1);
				ItemAssistant.deleteItemFromInventory(player, data.getItem1(), 1);
				ItemAssistant.addItem(player, data.getResult(), 1);
				if (data.statement) {
					player.getDH().sendItemChat("", "You combine the @blu@" + ItemAssistant.getItemName(data.getItem1()) + "@bla@ with the",
					                            "@blu@" + ItemAssistant.getItemName(data.getItem2()) + "@bla@ to create a ",
					                            "@blu@" + ItemAssistant.getItemName(data.getResult()) + "@bla@.", data.getResult(), 200, data.getOffset1(), data.getOffset2());
				} else {
					player.getPA().closeInterfaces(true);
					player.getPA().sendMessage(
							"You combine the " + ItemAssistant.getItemName(data.getItem1()) + " with the " + ItemAssistant.getItemName(data.getItem2()) + " to create a "
							+ ItemAssistant.getItemName(data.getResult()) + ".");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * The dialogue message that appears when a player clicks the dismantle option on the item.
	 */

	public static void showDismantleWarning(Player player, int result) {
		player.getDH().sendItemChat("", "Are you sure you want to dismantle this item?", "You will get both original items back in return.", result, 200, 10, 0);
		player.nextDialogue = 700;
	}

	/**
	 * Dismantles the ornament item, giving the player the kit and primary item back.
	 *
	 * @param player The associated player.
	 */
	public static void dismantle(Player player) {
		for (ItemCombination data : ItemCombination.values()) {
			if (!GameType.getGameType(data.getType())) {
				continue;
			}
			if (ItemAssistant.getFreeInventorySlots(player) < 1) {
				player.getDH().sendStatement("You need at least one free inventory slot.");
				return;
			}
			int result = data.getResult();
			if (ItemAssistant.hasItemInInventory(player, result)) {
				int slot = ItemAssistant.getItemSlot(player, result);
				ItemAssistant.deleteItemFromInventory(player, result, slot, 1);
				ItemAssistant.addItem(player, data.getItem2(), 1);
				ItemAssistant.addItem(player, data.getItem1(), 1);
				player.getDH().sendItemChat("", "You dismantle the " + ItemAssistant.getItemName(data.getResult()) + ".", data.getItem2(), 200, 10, 0);
				return;
			}
		}
	}

	/**
	 * The list of item data for item combining and dismantling.
	 */
	public enum ItemCombination {
		// item1, item2, result, offset1, offset2, itemChat, canDismantle,
		// isThirdClickItemPacketDismantle
		DARK_INFINITY_HAT(12528, 6918, 12457, 10, 0, true, true, false),
		DARK_INFINITY_TOP(12528, 6916, 12458, 10, 0, true, true, false),
		DARK_INFINITY_BOTTOMS(12528, 6924, 12459, 10, 0, true, true, false),
		LIGHT_INFINITY_HAT(12530, 6918, 12419, 10, 0, true, true, false),
		LIGHT_INFINITY_TOP(12530, 6916, 12420, 10, 0, true, true, false),
		LIGHT_INFINITY_BOTTOMS(12530, 6924, 12421, 10, 0, true, true, false),
		VOLCANIC_WHIP(12771, 4151, 12773, 10, 0, true, false, false),
		FROZEN_WHIP(12769, 4151, 12774, 10, 0, true, false, false),
		DRAGON_FULL_HELM(12538, 11335, 12417, 25, 0, true, true, false),
		DRAGON_CHAINBODY(12534, 3140, 12414, 0, -15, true, true, false),
		DRAGON_PLATELEGS(12536, 4087, 12415, 0, -15, true, true, false),
		DRAGON_SQ_SHIELD(12532, 1187, 12418, 25, 0, true, true, false),
		DRAGON_PLATESKIRT(12536, 4585, 12416, 25, 0, true, true, false),
		DRAGON_SCIMITAR(20002, 4587, 20000, 25, 0, true, true, false),
		DRAGON_DEFENDER(20143, 12954, 19722, 25, 0, true, true, false),
		DRAGON_BOOTS(22231, 11840, 22234, 10, 0, true, true, false),
		DRAGON_PLATEBODY(22236, 21892, 22242, 0, -15, true, true, false),
		DRAGON_KITESHIELD(22239, 21895, 22244, 0, -15, true, true, false),
		ARMADYL_GODSWORD(20068, 11802, 20368, 0, -25, true, true, false),
		BANDOS_GODSWORD(20071, 11804, 20370, 0, -25, true, true, false),
		SARADOMIN_GODSWORD(20074, 11806, 20372, 0, -25, true, true, false),
		ZAMORAK_GODSWORD(20077, 11808, 20374, 0, -25, true, true, false),
		NECKLACE_OF_ANGUISH(22246, 19547, 22249, 0, -15, true, true, false),
		AMULET_OF_FURY(12526, 6585, 12436, 0, 0, true, true, false),
		AMULET_OF_TORTURE(20062, 19553, 20366, -35, 0, true, true, false),
		OCCULT_NECKLACE(20065, 12002, 19720, -35, 0, true, true, false),
		ODIUM_WARD(12802, 11926, 12807, 0, -15, true, true, false),
		MALEDICTION_WARD(12802, 11924, 12806, 0, -15, true, true, false),
		GRANITE_MAUL_OR(4153, 12849, 12848, 0, -50, true, true, false),
		BLESSED_SARA_SWORD(11838, 12804, 12808, 0, -18, true, true, false),
		BLACK_SLAYER_HELM(11864, 7980, 19639, 20, -30, true, true, true),
		RED_SLAYER_HELM(11864, 7979, 19647, 20, -30, true, true, true),
		GREEN_SLAYER_HELM(11864, 7981, 19643, 20, -30, true, true, true),
		PURPLE_SLAYER_HELM(11864, 21275, 21264, 20, -30, true, true, true),
		RING_OF_WEALTH_I(2572, 12783, 12785, 0, -18, true, false, false),
		MAGIC_SHORTBOW_I(861, 12786, 12788, 0, -18, true, false, false),
		STAFF_OF_LIGHT(13256, 11791, 22296, 0, -5, true, false, false),
		GUARDIAN_BOOTS(21730, 11836, 21733, 10, 0, true, true, false),
		LAVA_BATTLESTAFF(21202, 3053, 21198, 10, 0, true, true, false),

		MAGIC_LONGBOW_SIGHT(18330, 859, 18332, 0, -5, true, true, false, GameType.PRE_EOC),
		MAPLE_LONGBOW_SIGHT(18330, 851, 18331, 0, -5, true, true, false, GameType.PRE_EOC),
		DRAGONBONE_HELM(DRAGONBONE_UPGRADE_KIT, 11_335, 24_359, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_PLATEBODY(DRAGONBONE_UPGRADE_KIT, 14_479, 24_360, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_PLATELEGS(DRAGONBONE_UPGRADE_KIT, 4_087, 24_363, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_BOOTS(DRAGONBONE_UPGRADE_KIT, 11_732, 24_362, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_GLOVES(DRAGONBONE_UPGRADE_KIT, 7_461, 24_361, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_PLATESKIRT(DRAGONBONE_UPGRADE_KIT, 4_585, 24_364, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_MAGE_HAT(DRAGONBONE_UPGRADE_KIT, 6918, 24_354, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_MAGE_TOP(DRAGONBONE_UPGRADE_KIT, 6916, 24_355, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_MAGE_BOTTOM(DRAGONBONE_UPGRADE_KIT, 6924, 24_356, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_MAGE_BOOTS(DRAGONBONE_UPGRADE_KIT, 6920, 24_358, 0, -5, true, false, false, GameType.PRE_EOC),
		DRAGONBONE_MAGE_GLOVES(DRAGONBONE_UPGRADE_KIT, 6922, 24_357, 0, -5, true, false, false,
				GameType.PRE_EOC),

		FISHBOWL_SEAWEED(6668, 401, 6669, 0, -5, true, false, false, GameType.PRE_EOC),
		;

		private int item1, item2, result, offset1, offset2;

		private boolean statement, canDismantle, isThirdClickItemPacketDismantle;
		
		private GameType type;

		ItemCombination(final int item1, final int item2, final int result, final int offset1,
				final int offset2, final boolean statement, final boolean canDismantle,
				final boolean isThirdClickItemPacketDismantle) {
			this(item1, item2, result, offset1, offset2, statement, canDismantle,
					isThirdClickItemPacketDismantle, GameType.OSRS);
		}

		ItemCombination(final int item1, final int item2, final int result, final int offset1, final int offset2, final boolean statement, final boolean canDismantle,
		         final boolean isThirdClickItemPacketDismantle, GameType type) {
			this.item1 = item1;
			this.item2 = item2;
			this.result = result;
			this.offset1 = offset1;
			this.offset2 = offset2;
			this.statement = statement;
			this.canDismantle = canDismantle;
			this.isThirdClickItemPacketDismantle = isThirdClickItemPacketDismantle;
			this.type = type;
		}

		public int getItem1() {
			return item1;
		}

		public int getItem2() {
			return item2;
		}

		public int getResult() {
			return result;
		}

		public int getOffset1() {
			return offset1;
		}

		public int getOffset2() {
			return offset2;
		}

		public boolean isStatement() {
			return statement;
		}

		public boolean canDismantle() {
			return canDismantle;
		}

		public boolean isThirdClickItemPacketDismantle() {
			return isThirdClickItemPacketDismantle;
		}

		public GameType getType() {
			return type;
		}
	}

}
