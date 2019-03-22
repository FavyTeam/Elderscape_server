package game.content.skilling.fletching;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Bowstringing.
 *
 * @author MGT Madness, created on 11-05-2016.
 */
public class BowStringFletching {
	public static enum UnStrungBowData {
		SHORTBOW(50, 841, 5, 1, 6678),
		LONGBOW(48, 839, 10, 10, 6684),
		OAK_SHORTBOW(54, 843, 17, 20, 6679),
		OAK_LONGBOW(56, 845, 25, 25, 6685),
		WILLOW_SHORTBOW(60, 849, 34, 35, 6680),
		WILLOW_LONGBOW(58, 847, 42, 40, 6686),

		MAPLE_SHORTBOW(64, 853, 50, 50, 6681),
		MAPLE_LONGBOW(62, 851, 58, 55, 6687),

		YEW_SHORTBOW(68, 857, 68, 65, 6682),
		YEW_LONGBOW(66, 855, 75, 70, 6688),
		MAGIC_SHORTBOW(72, 861, 83, 80, 6683),
		MAGIC_LONGBOW(70, 859, 92, 85, 6689);

		private int unStrungBowId;

		private int strungBowId;

		private int experience;

		private int levelRequired;

		private int animation;

		private UnStrungBowData(int unStrungBowId, int strungBowId, int experience, int levelRequired, int animation) {
			this.unStrungBowId = unStrungBowId;
			this.strungBowId = strungBowId;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.animation = animation;
		}

		public int getUnStrungBowId() {
			return unStrungBowId;
		}

		public int getStrungBowId() {
			return strungBowId;
		}

		public int getExperience() {
			return experience;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getAnimation() {
			return animation;
		}
	}

	public static boolean useBowStringOnLeather(Player player, int itemUsedId, int itemUsedWith) {
		int bowString = 1777;
		if (player.isInZombiesMinigame()) {
			bowString = 4051;
		}
		if (itemUsedId != bowString && itemUsedWith != bowString) {
			return false;
		}
		if (itemUsedId == bowString) {
			itemUsedId = itemUsedWith;
		}
		UnStrungBowData bow = null;
		for (UnStrungBowData data : UnStrungBowData.values()) {
			if (itemUsedId == data.getUnStrungBowId()) {
				bow = data;
				break;
			}
		}
		if (bow == null) {
			return false;
		}
		if (player.baseSkillLevel[ServerConstants.FLETCHING] < bow.getLevelRequired()) {
			player.getDH().sendStatement("You need " + bow.getLevelRequired() + " Fletching to string this bow.");
			return false;
		}
		player.strungBowId = bow.getStrungBowId();
		player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.FLETCHING];
		InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 250, bow.getStrungBowId(), 0, -10);
		return true;
	}

	public static void fletchingInterfaceAction(Player player, int amount) {
		player.getPA().closeInterfaces(true);
		player.skillingData[4] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, ServerConstants.SKILL_NAME[ServerConstants.FLETCHING], "Enter amount");
			return;
		}
		for (UnStrungBowData data : UnStrungBowData.values()) {
			if (player.strungBowId == data.getStrungBowId()) {
				stringBowEvent(player, data.getUnStrungBowId(), data.getStrungBowId(), data.getExperience(), data.getLevelRequired(), data.getAnimation());
				break;
			}
		}
	}

	public static void xAmountFletchingAction(Player player, int amount) {
		player.skillingData[4] = amount;
		for (UnStrungBowData data : UnStrungBowData.values()) {
			if (player.strungBowId == data.getStrungBowId()) {
				stringBowEvent(player, data.getUnStrungBowId(), data.getStrungBowId(), data.getExperience(), data.getLevelRequired(), data.getAnimation());
				break;
			}
		}
	}

	private static void stringBowEvent(final Player player, final int unStrungBowId, final int strungBowId, final int experience, int levelRequired, final int animation) {
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
				if (player.skillingData[4] <= 1) {
					container.stop();
				}
				if (player.skillingData[4] <= 0) {
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, unStrungBowId)) {
					container.stop();
					return;
				}

				// Bowstring.
				if (!ItemAssistant.hasItemInInventory(player, player.isInZombiesMinigame() ? 4051 : 1777)) {
					container.stop();
					return;
				}
				if (unStrungBowId == UnStrungBowData.YEW_SHORTBOW.getUnStrungBowId()) {
					Achievements.checkCompletionMultiple(player, "1034");
				} else if (strungBowId == UnStrungBowData.MAGIC_SHORTBOW.getStrungBowId()) {
					Achievements.checkCompletionMultiple(player, "1055");
				}
				player.startAnimation(animation);
				ItemAssistant.deleteItemFromInventory(player, unStrungBowId, 1);
				if (!player.isInZombiesMinigame()) {
					if (Skilling.hasMasterCapeWorn(player, 9783) && Misc.hasPercentageChance(10)) {
						player.getPA().sendMessage("<col=a54704>Your cape allows you to save a bowstring.");
					} else {
						ItemAssistant.deleteItemFromInventory(player, 1777, 1);
					}
				}
				ItemAssistant.addItem(player, strungBowId, 1);
				Skilling.addHarvestedResource(player, strungBowId, 1);
				Skilling.addSkillExperience(player, experience, ServerConstants.FLETCHING, false);
				player.skillingStatistics[SkillingStatistics.BOWS_MADE]++;
				player.skillingData[4]--;
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}
}
