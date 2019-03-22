package game.content.skilling;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.ArrayList;
import utility.Misc;

/**
 * Woodcutting skill.
 */

public class Woodcutting {
	/**
	 * x seconds have to pass before the tree can be deleted.
	 */
	private final static int TREE_IMMUNE_TIME = 120;


	private static enum Hatchet {
		BRONZE(1351, 1, 879, 13, 3291),
		IRON(1349, 1, 877, 12, 3290),
		STEEL(1353, 6, 875, 11, 3289),
		BLACK(1361, 6, 873, 10, 3288),
		MITHRIL(1355, 21, 871, 8, 3287),
		ADAMANT(1357, 31, 869, 6, 3286),
		RUNE(1359, 41, 867, 4, 3285),
		DRAGON(6739, 61, 2846, 3, 3292),
		INFERNAL(13241, 61, 2117, 2, 2116),
		THIRD_AGE(20011, 61, 7264, 1, 7263);

		private int id, req, anim, timer, entAnim;

		private Hatchet(int id, int level, int animation, int timer, int entAnim) {
			this.id = id;
			this.req = level;
			this.anim = animation;
			this.timer = timer;
			this.entAnim = entAnim;
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

		public int getTimer() {
			return timer;
		}

		public int getEntAnim() {
			return entAnim;
		}
	}


	public static enum TreeData {
		//@formatter:off
		NORMAL_TREE(1, 25, 1511, 1342, 1, new int[] {1276, 1278, 1279}, 25, 1),
		DYING_TREE(1, 25, 1511, 3649, 1, new int[] {3648}, 25, 1),
		DEAD_TREE(1, 25, 1511, 1341, 1, new int[] {1284}, 25, 1),
		DEAD1_TREE(1, 25, 1511, 1347, 1, new int[] {1282, 1283}, 25, 1),
		DEAD2_TREE(1, 25, 1511, 1351, 1, new int[] {1286}, 25, 1),
		DEAD3_TREE(1, 25, 1511, 1352, 1, new int[] {1365}, 25, 1),
		DEAD4_TREE(1, 25, 1511, 1353, 1, new int[] {1289}, 25, 1),
		DEAD5_TREE(1, 25, 1511, 1349, 1, new int[] {1285}, 25, 1),
		JUNGLE_TREE(1, 25, 1511, 4819, 1, new int[] {4818}, 25, 1),
		JUNGLE_TREE1(1, 25, 1511, 4820, 1, new int[] {4821}, 25, 1),
		STRANGE_TREE(1, 25, 1511, 3880, 1, new int[] {3881}, 25, 1),
		STRANGE_TREE1(1, 25, 1511, 3884, 1, new int[] {3883}, 25, 1),
		ARCTIC_PINE(1, 25, 10810, 21274, 1, new int[] {3037}, 25, 1),
		ACHEY_TREE(1, 25, 2862, 3371, 1, new int[] {2023}, 25, 1),
		OAK_TREE(15, 38, 1521, 1356, 2, new int[] {1751}, 30, 0),
		WILLOW_TREE(30, 68, 1519, 9471, 3, new int[] {1756, 1758, 1760}, 35, 0),
		WILLOW_TREE1(30, 68, 1519, 9711, 3, new int[] {1750}, 35, 0),
		TEAK_TREE(35, 85, 6333, 9037, 3, new int[] {9036}, 37, 0),
		MAPLE_TREE(45, 100, 1517, 9712, 4, new int[] {1759}, 40, 0),
		HOLLOW_TREE(45, 82, 3239, 2310, 4, new int[] {1752}, 42, 0),
		HOLLOW1_TREE(45, 82, 3239, 4061, 4, new int[] {1757}, 42, 0),
		MAHOGANY_TREE(50, 85, 6332, 9035, 4, new int[] {9034}, 45, 0),
		YEW_TREE(60, 175, 1515, 9714, 5, new int[] {1753, 1754}, 50, 0),
		MAGIC_TREE(75, 250, 1513, 9713, 6, new int[] {1761}, 55, 0),
		REDWOOD_TREE(90, 380, 19669, 28860, 7, new int[] {28859}, 60, 0);

		//@formatter:on
		public int woodcutLevelRequirement, experienceReward, logItemId, treeStumpObjectId, treeTimer, respawnTime, isOneLog;

		public int[] treeObjectId;

		private TreeData(int woodcutLevelRequirement, int experienceReward, int logItemId, int treeStumpObjectId, int treeTimer, int[] treeObjectId, int respawnTime,
		                 int isOneLog) {
			this.woodcutLevelRequirement = woodcutLevelRequirement;
			this.experienceReward = experienceReward;
			this.logItemId = logItemId;
			this.treeStumpObjectId = treeStumpObjectId;
			this.treeObjectId = treeObjectId;
			this.treeTimer = treeTimer;
			this.respawnTime = respawnTime;
			this.isOneLog = isOneLog;

		}

		public int getRespawnTime() {
			return respawnTime;
		}
	}
	//Tree timer is the chance of receiving a log, is also based off wc level.

	/**
	 * Store tree object id, coords and time it was removed.
	 */
	public static ArrayList<String> treeRemovedList = new ArrayList<String>();

	public static boolean isWoodcuttingObject(final Player player, int objectId) {
		for (TreeData treeData : TreeData.values()) {
			if (treeData == TreeData.REDWOOD_TREE && GameType.isPreEoc()) {
				return false;
			}
			for (int index = 0; index < treeData.treeObjectId.length; index++) {
				if (treeData.treeObjectId[index] == objectId) {
					startWoodcutting(player, treeData.woodcutLevelRequirement, treeData.experienceReward, treeData.logItemId, treeData.treeStumpObjectId, treeData.treeTimer);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Start the woodcutting procedure.
	 */
	public static void startWoodcutting(Player player, final int woodcutLevelRequirement, final int experienceReward, final int logItemId, final int treeStumpObjectId,
	                                    final int treeTimer) {

		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		if (player.baseSkillLevel[ServerConstants.WOODCUTTING] < woodcutLevelRequirement) {
			stopWoodcutting(player);
			player.getDH().sendStatement("You need a woodcutting level of " + woodcutLevelRequirement + " to cut this tree.");
			return;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("You don't have enough inventory space.");
			return;
		}
		if (!hasCorrectHatchet(player)) {
			return;
		}
		if (Area.inDonatorZone(player.getX(), player.getY())) {
			if (!player.isExtremeDonator()) {
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.EXTREME_DONATOR) + "Only Extreme Donators can access this!");
				return;
			}
		}
		performAnimation(player);
		startWcTimerEvent(player, experienceReward, logItemId, treeStumpObjectId, treeTimer);
	}

	/**
	 * Force stop woodcutting.
	 *
	 * @param player The associated player.
	 */
	public static void stopWoodcutting(Player player) {
		if (player.woodCuttingEventTimer > 0) {
			player.startAnimation(65535);
		}
		player.woodCuttingEventTimer = -1;
	}

	/**
	 * Perform the woodcutting hatchet animation.
	 *
	 * @param player The associated player.
	 */
	public static void performAnimation(Player player) {
		for (Hatchet data : Hatchet.values()) {
			if (data.getItemId() == player.hatchetUsed) {
				player.startAnimation(data.getAnim());
			}
		}
		SoundSystem.sendSound(player, Misc.random(471, 473), 400);
		treeExists(player);
	}

	private static void treeExists(Player player) {
		long timeValue = 0;
		for (int index = 0; index < treeRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (treeRemovedList.get(index).contains(match)) {
				// Time tree removed.
				String time = treeRemovedList.get(index).replace(match + " ", "");
				timeValue = Long.parseLong(time);
				break;
			}
		}
		if (System.currentTimeMillis() - timeValue <= 7000) {
			player.playerAssistant.stopAllActions();
		}
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player has the correct hatchet.
	 */
	public static boolean hasCorrectHatchet(Player player) {
		player.hatchetUsed = 0;
		for (Hatchet data : Hatchet.values()) {
			if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId()))
			    && player.baseSkillLevel[ServerConstants.WOODCUTTING] >= data.getRequiredLevel()) {
				player.hatchetUsed = data.getItemId();
			}
		}
		if (player.hatchetUsed == 0) {
			for (Hatchet data : Hatchet.values()) {
				if (ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId())) {
					if (player.baseSkillLevel[ServerConstants.WOODCUTTING] < data.getRequiredLevel()) {
						player.getPA().sendMessage("You need a woodcutting level of " + data.getRequiredLevel() + " to use this axe.");
						return false;
					}
				}
			}
			player.getPA().sendMessage("You need an axe to chop this tree.");
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
	public static void receiveLogFromTree(Player player, int experienceReward, final int logItemId, final int treeStumpObjectId, final int treeTimer) {
		// Oak tree.
		if (logItemId == TreeData.OAK_TREE.logItemId) {
			Achievements.checkCompletionSingle(player, 1009);
		}
		// Yew log.
		else if (logItemId == TreeData.YEW_TREE.logItemId) {
			Achievements.checkCompletionMultiple(player, "1040");
		}
		if (!ItemAssistant.addItem(player, logItemId, 1)) {
			player.playerAssistant.sendMessage("You have run out of inventory space.");
			return;
		}
		if (Skilling.hasMasterCapeWorn(player, 9807) && Misc.hasPercentageChance(10)) {
			Skilling.addHarvestedResource(player, logItemId, 1);
			ItemAssistant.addItemToInventoryOrDrop(player, logItemId, 1);
			player.getPA().sendMessage("<col=a54704>Your cape allows you to chop an extra log.");
		}
		Skilling.addHarvestedResource(player, logItemId, 1);
		performAnimation(player);
		player.skillingStatistics[SkillingStatistics.LOGS_GAINED]++;
		if (logItemId == 771) {
			player.getPA().sendFilterableMessage("You cut a branch from the Dramen tree.");
		} else {
			player.getPA().sendFilterableMessage("You get some " + ItemAssistant.getItemName(logItemId) + ".");
		}
		int boostedExperience = experienceReward;
		if (GameType.isOsrs()) {
			for (int index = 0; index < ServerConstants.LUMBERJACK_PIECES.length; index++) {
				int itemId = ServerConstants.LUMBERJACK_PIECES[index][0];
				if (ItemAssistant.hasItemEquippedSlot(player, itemId, ServerConstants.LUMBERJACK_PIECES[index][1])) {
					boostedExperience *= ServerConstants.SKILLING_SETS_EXPERIENCE_BOOST_PER_PIECE;
				}
			}
		}
		Skilling.petChance(player, experienceReward, 250, 6500, ServerConstants.WOODCUTTING, null);
		Skilling.addSkillExperience(player, boostedExperience, ServerConstants.WOODCUTTING, false);
		startWcTimerEvent(player, experienceReward, logItemId, treeStumpObjectId, treeTimer);
		createTreeStump(player, treeStumpObjectId);
		if (Misc.random(250) == 1 && GameType.isOsrsEco()) {
			Woodcutting.birdsNest(player);
		}
	}

	public static void birdsNest(Player player) {
		Server.itemHandler.createGroundItem(player, 5070 + Misc.random(4), player.getX(), player.getY(), player.getHeight(), 1, false, 0, true, player.getPlayerName(), "", "", "", "birdsNest");
		player.getPA().sendMessage("<col=ff0000>A bird's nest falls out of the tree.");
	}


	private static int setWoodcuttingTimer(Player player, int treeTimer) {
		int timer = 0;

		for (Hatchet data : Hatchet.values()) {
			if (player.hatchetUsed == data.getItemId()) {
				timer = data.getTimer();
			}
		}
		timer += treeTimer;

		int baseMinimum = 1;
		timer = Misc.random(baseMinimum, (int) ((17 + baseMinimum) - (player.baseSkillLevel[ServerConstants.WOODCUTTING] * 0.17)) + timer);
		return timer;
	}

	/**
	 * Decrease the wcTimer variable until it reaches 0.
	 */
	private static void startWcTimerEvent(final Player player, final int experienceReward, final int logItemId, final int treeStumpObjectId, final int treeTimer) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		player.woodCuttingEventTimer = setWoodcuttingTimer(player, treeTimer);
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
					receiveLogFromTree(player, experienceReward, logItemId, treeStumpObjectId, treeTimer);
				}
			}
		}, 1);

	}

	private static void createTreeStump(Player player, final int treeStumpObjectId) {
		boolean normalTree = false;
		TreeData data = null;
		for (TreeData treeData : TreeData.values()) {
			for (int index = 0; index < treeData.treeObjectId.length; index++) {
				if (treeData.treeObjectId[index] == player.getObjectId()) {
					data = treeData;
					break;
				}
			}
		}
		if (data == null) {
			return;
		}
		if (data.isOneLog > 0) {
			normalTree = true;
		}
		if (!Misc.hasOneOutOf(20) && !normalTree) {
			return;
		}

		long timeValue = 0;
		int listIndex = -1;

		for (int index = 0; index < treeRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (treeRemovedList.get(index).contains(match)) {
				// Time tree removed.
				String time = treeRemovedList.get(index).replace(match + " ", "");
				timeValue = Long.parseLong(time);
				listIndex = index;
				break;
			}
		}

		if (System.currentTimeMillis() - timeValue <= (TREE_IMMUNE_TIME * 1000) && !normalTree) {
			return;
		}

		if (listIndex >= 0) {
			treeRemovedList.remove(listIndex);
		}
		SoundSystem.sendSound(player, 1312, 0);
		treeRemovedList.add(player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY() + " " + System.currentTimeMillis());
		new Object(treeStumpObjectId, player.getObjectX(), player.getObjectY(), player.getHeight(), 1, 10, player.getObjectId(), data.getRespawnTime() / 2);
		player.playerAssistant.stopAllActions();
	}
}
