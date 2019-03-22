package game.content.skilling.herblore;

import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

public class SecondaryGrinding {
	public enum Grindables {
		UNICORN_HORN(237, 235),
		CHOCOLATE_BAR(1973, 1975),
		BIRD_NEST(5075, 6693),
		KEBBIT_TEETH(10109, 10111),
		BLUE_DRAG_SCALE(243, 241),
		DESERT_GOAT_HORN(9735, 9736),
		RUNE_SHARDS(6466, 6467),
		ASHES(592, 8865),
		SEAWEED(401, 6683),
		SEAWEED1(403, 6683),
		BAT_BONES(530, 9594),
		CHARCOAL(973, 704),
		MUD_RUNE(4698, 9594),
		COD(341, 7528),
		KELP(7516, 7517),
		CRAB(7518, 7527),
		SUQAH(9079, 9082),
		THISTLE(3263, 3264),
		GARLIC(1550, 4668),
		BLACK_MUSHROOM(4620, 4622),
		LAVA_SCALE(11992, 11994);

		private int primary;

		private int product;

		Grindables(int primary, int product) {
			this.primary = primary;
			this.product = product;
		}

		public int getPrimary() {
			return primary;
		}

		public int getProduct() {
			return product;
		}
	}

	public static void GrindItem(final Player player, final int itemUsed, final int usedWith) {
		if (!ItemAssistant.hasItemInInventory(player, 233)) {
			return;
		}
		for (final Grindables g : Grindables.values()) {
			final int itemId = (itemUsed == 233 ? usedWith : itemUsed);
			if (itemId == g.getPrimary()) {
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
						if (ItemAssistant.hasItemInInventory(player, g.getPrimary()) && ItemAssistant.hasItemInInventory(player, 233)) {
							ItemAssistant.deleteItemFromInventory(player, g.getPrimary(), 1);
							ItemAssistant.addItem(player, g.getProduct(), 1);
							player.startAnimation(363);
						} else {
							container.stop();
						}
					}

					@Override
					public void stop() {
						Skilling.endSkillingEvent(player);
					}
				}, 1);
				return;
			}
		}
	}

	public static boolean UsingItemOnPestle(Player player, int itemUsedId, int itemUsedWithId) {
		if (itemUsedId == 233 || itemUsedWithId == 233) {
			if (RandomEvent.isBannedFromSkilling(player)) {
				return true;
			}
			GrindItem(player, itemUsedId, itemUsedWithId);
			return true;
		}
		return false;
	}
}
