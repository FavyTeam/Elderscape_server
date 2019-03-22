package game.content.skilling;

import core.ServerConstants;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class EntChopping {
	/**
	 * x seconds have to pass before the tree can be deleted.
	 */
	private final static int IMMUNE_TIME = 60;


	public static enum AxeData {
		BRONZE(1351, 1, 3291),
		IRON(1349, 1, 3290),
		STEEL(1353, 6, 3289),
		BLACK(1361, 6, 3288),
		MITHRIL(1355, 21, 3287),
		ADAMANT(1357, 31, 3286),
		RUNE(1359, 41, 3285),
		DRAGON(6739, 61, 3292),
		INFERNAL(13241, 75, 2116),
		THIRD_AGE(20011, 85, 7263);

		private int id, req, anim;

		private AxeData(int id, int level, int animation) {
			this.id = id;
			this.req = level;
			this.anim = animation;
		}

		public int getItemId() {
			return id;
		}

		public int getRequiredLevel() {
			return req;
		}

		public int getAnim() {
			return anim;
		}
	}


	public static enum LogData {
		NORMAL_TREE(1, 1511),
		OAK_TREE(15, 1521),
		WILLOW_TREE(30, 1519),
		MAPLE_TREE(45, 1517),
		YEW_TREE(60, 1515),
		MAGIC_TREE(75, 1513);

		public int woodcutLevelRequirement;

		public int logItemId;

		private LogData(int woodcutLevelRequirement, int logItemId) {
			this.woodcutLevelRequirement = woodcutLevelRequirement;
			this.logItemId = logItemId;

		}
	}

	/**
	 * Start the chopping procedure.
	 */
	public static void startChopping(Player player, final int woodcutLevelRequirement, final int logItemId) {
		if (!hasCorrectAxe(player)) {
			return;
		}
		performAnimation(player);
		startTimerEvent(player, logItemId);
	}

	public static boolean isEntStump(final Player player, int npcType) {
		for (LogData logData : LogData.values()) {
			if (npcType == 6595) {
				startChopping(player, logData.woodcutLevelRequirement, logData.logItemId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Force stop woodcutting.
	 *
	 * @param player The associated player.
	 */
	public static void stopChopping(Player player) {
		if (player.woodCuttingEventTimer > 0) {
			player.startAnimation(65535);
		}
		player.woodCuttingEventTimer = -1;
	}

	/**
	 * Perform the woodcutting axe animation.
	 *
	 * @param player The associated player.
	 */
	public static void performAnimation(Player player) {
		for (AxeData data : AxeData.values()) {
			if (data.getItemId() == player.hatchetUsed) {
				player.startAnimation(data.getAnim());
			}
		}
		SoundSystem.sendSound(player, 472, 400);
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player has the correct axe.
	 */
	public static boolean hasCorrectAxe(Player player) {
		player.hatchetUsed = 0;
		for (AxeData data : AxeData.values()) {
			if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId()))
			    && player.baseSkillLevel[ServerConstants.WOODCUTTING] >= data.getRequiredLevel()) {
				player.hatchetUsed = data.getItemId();
			}
		}
		if (player.hatchetUsed == 0) {
			for (AxeData data : AxeData.values()) {
				if (ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId())) {
					if (player.baseSkillLevel[ServerConstants.WOODCUTTING] < data.getRequiredLevel()) {
						player.getPA().sendMessage("You need a woodcutting level of " + data.getRequiredLevel() + " to use this axe.");
						return false;
					}
				}
			}
			player.getPA().sendMessage("You need an axe to do this.");
			return false;
		}
		return true;
	}

	/**
	 * Receive the logs.
	 *
	 * @param player The associated player.
	 * @param logType The log identity.
	 * @param experience Experience to gain from the log.
	 */
	public static void receiveLog(Player player, final int logItemId) {
		int chance = 0;

		int amount = 1 * (Misc.hasPercentageChance(chance) ? 2 : 1);
		if (!ItemAssistant.addItem(player, logItemId, amount)) {
			player.playerAssistant.sendMessage("You have run out of inventory space.");
			return;
		}
		Skilling.addHarvestedResource(player, logItemId, amount);
		performAnimation(player);
		player.playerAssistant.sendFilterableMessage("You manage to chop some " + ItemAssistant.getItemName(logItemId) + " from the carcass.");
		Skilling.petChance(player, 25, 250, 3700, ServerConstants.WOODCUTTING, null);
		Skilling.addSkillExperience(player, 25, ServerConstants.WOODCUTTING, false);
		startTimerEvent(player, logItemId);
		if (Misc.random(250) == 1) {
			Woodcutting.birdsNest(player);
		}
	}

	/**
	 * Decrease the wcTimer variable until it reaches 0.
	 */
	private static void startTimerEvent(final Player player, final int logItemId) {
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
				if (player.woodCuttingEventTimer > 0) {
					player.woodCuttingEventTimer--;
					performAnimation(player);
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
				player.startAnimation(65535);
				if (player.woodCuttingEventTimer == 0) {
					receiveLog(player, logItemId);
				}
			}
		}, 8);

	}
}
