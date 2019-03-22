package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Spinning wheel.
 *
 * @author MGT Madness, created on 04-08-2016.
 */
public class SpinningWheel {
	private static enum SpinningData {
		WOOL(102003, 1737, 1759, 1, 3),
		FLAX(102004, 1779, 1777, 1, 15),
		SINEW(102005, 9436, 9438, 10, 15);

		private int buttonId;

		private int used;

		private int result;

		private int level;

		private int experience;

		private SpinningData(final int buttonId, final int used, final int result, final int level, final int experience) {
			this.buttonId = buttonId;
			this.used = used;
			this.result = result;
			this.level = level;
			this.experience = experience;
		}

		public int getButtonId(final int button) {
			for (int i = 0; i < buttonId; i++) {
				if (button == buttonId) {
					return buttonId;
				}
			}
			return -1;
		}

		public int getRawMaterial() {
			return used;
		}

		public int getSpinnedProduct() {
			return result;
		}

		public int getLevelRequired() {
			return level;
		}

		public int getExperience() {
			return experience;
		}
	}

	public static void spinningWheel(final Player player, final int buttonId) {
		for (SpinningWheel.SpinningData data : SpinningWheel.SpinningData.values()) {
			if (buttonId == data.getButtonId(buttonId)) {
				SpinningWheel.spinningWheelAction(player, data);
				break;
			}
		}
	}

	private static void spinningWheelAction(final Player player, final SpinningData data) {

		if (player.baseSkillLevel[ServerConstants.CRAFTING] < data.getLevelRequired()) {
			player.getDH().sendStatement("You need a crafting level of " + data.getLevelRequired() + " to spin " + ItemAssistant.getItemName(data.getRawMaterial()) + ".");
			return;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
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
				if (!ItemAssistant.hasItemAmountInInventory(player, data.getRawMaterial(), 1)) {
					container.stop();
					return;
				}
				player.getPA().closeInterfaces(true);
				ItemAssistant.deleteItemFromInventory(player, data.getRawMaterial(), 1);
				ItemAssistant.addItem(player, data.getSpinnedProduct(), 1);
				Skilling.addSkillExperience(player, data.getExperience(), ServerConstants.CRAFTING, false);
				player.getPA().sendFilterableMessage("You spin some " + ItemAssistant.getItemName(data.getRawMaterial()) + ".");
				player.startAnimation(894);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 3);
	}
}
