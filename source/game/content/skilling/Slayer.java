package game.content.skilling;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.position.Position;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.impl.superior.SuperiorNpc;
import game.npc.pet.BossPetDrops;
import game.object.clip.Region;
import game.player.Player;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import utility.Misc;

/**
 * Slayer skill.
 *
 * @author MGT Madness, created on 02-01-2015. Modified by Owain on 11-09-17
 */
public class Slayer {

	public static int SUPERIOR_PROBABILITY = 50;

	public static final int EASY_TASK = 1, MEDIUM_TASK = 2, HARD_TASK = 3;

	public static int getTaskResetCost() {
		return GameType.isOsrsEco() ? 750000 : 1000;
	}

	public static int getSlayerHelmUpgradeCost() {
		return GameType.isOsrsEco() ? 7500000 : 10000;
	}

	/**
	 * Slayer tasks. NPC, Least slayer level to access task, Highest slayer level to access task, 1 if required slayer lvl to attack.
	 */
	public enum Task {
		SKELETAL_WYVERN(new int[]
				                {465}, 72, 99, "the Wyvern Cave", true),
		ROCK_CRAB(new int[]
				          {100}, 1, 30, "Rellekka", false),
		CAVE_CRAWLER(new int[] {406}, 15, 30, "Fremennik dungeon", true, 7389, 600),
		ROCK_SLUG(new int[]
				          {421}, 20, 35, "Fremennik dungeon", true, 7392, 770),
		JELLY(new int[]
				      {437}, 52, 70, "Fremennik dungeon", true, 7399, 1900),
		TUROTH(new int[]
				       {428}, 55, 75, "Fremennik dungeon", true),
		DAGANNOTHS(new int[]
				           {3185, 2265, 2266, 2267}, 50, 80, "Dagannoth Kings boss teleport", false),
		ZOMBIE(new int[]
				       {26}, 15, 25, "Edgeville dungeon", false),
		CHAOS_DRUID(new int[]
				            {2878}, 10, 35, "Taverly Dungeon", false),
		BLACK_KNIGHT(new int[]
				             {4959}, 6, 35, "Taverly Dungeon", false),
		MAGIC_AXE(new int[]
				          {2844}, 20, 45, "Taverly Dungeon", false),
		ABYSSAL_DEMON(new int[]
				              {415}, 85, 99, "the Slayer Tower", true, 7410, 4200),
		BANSHEE(new int[]
				        {414}, 15, 45, "the Slayer Tower", true, 7390, 610),
		BASILISK(new int[]
				         {417}, 40, 65, "Fremennik Dungeon", true, 7395, 1700),
		COCKATRICE(new int[]
				           {419}, 25, 40, "Fremennik Dungeon", true, 7393, 950),
		KURASK(new int[]
				       {410}, 70, 85, "Fremennik Dungeon", true, 7405, 2767),
		PYREFIEND(new int[]
				          {433}, 30, 50, "Fremennik Dungeon", true, 7394, 1250),
		GIANT_BAT(new int[]
				          {2834}, 10, 35, "Taverley Dungeon", false),
		BLACK_DEMON(new int[]
				            {2048, 7144}, 60, 99, "Taverley Dungeon", false),
		BLOODVELD(new int[]
				          {484}, 50, 85, "the Slayer Tower", true, 7397, 2900),

		//Non metal dragons
		GREEN_DRAGON(new int[]
				             {5194, 5872, 5873, 264, 261, 262, 263, 260}, 40, 65, "the Wilderness, at ::wests", false),
		BLUE_DRAGON(new int[]
				            {241, 242, 243, 265, 7273, 243}, 70, 85, "Taverley Dungeon", false),
		RED_DRAGON(new int[]
				           {244, 245, 246, 247, 137, 7274}, 55, 80, "Brimhaven Dungeon", false),
		BLACK_DRAGON(new int[]
				             {252, 7275, 239}, 60, 99, "Taverley Dungeon", false),

		BRONZE_DRAGON(new int[]
				              {270}, 80, 95, "Brimhaven Dungeon", false),
		CRAWLING_HAND(new int[] {448}, 5, 35, "the Slayer Tower", true, 7388, 550),

		DARK_BEAST(new int[]
				           {4005}, 90, 99, "the Slayer Tower", true, 7409, 6462),
		DUST_DEVIL(new int[]
				           {423}, 65, 85, "the Slayer Tower", true, 7404, 3000),
		EARTH_WARRIOR(new int[]
				              {2840}, 20, 35, "Edgeville Dungeon", false),
		FIRE_GIANT(new int[]
				           {7251}, 30, 60, "Brimhaven Dungeon", false),
		GARGOYLE(new int[]
				         {412}, 75, 90, "the Slayer Tower", true, 7407, 3044),
		GHOST(new int[]
				      {6815}, 1, 35, "Taverley Dungeon", false),
		//CAVE_HORROR(new int[] {3209}, 58, 75, "Fremennik Dungeon", true, 7401, 1300), // Does not exist in npc spawns
		//GREATER_DEMON(new int[] {2026}, 40, 65, "Brimhaven Dungeon", false), // Does not exist in npc spawns
		WILD_DOG(new int[]
				         {112}, 30, 45, "Brimhaven Dungeon", false),
		HELLHOUND(new int[]
				          {104, 105, 11009, 5862}, 65, 90, "Taverley Dungeon", false),
		HILL_GIANT(new int[]
				           {7261}, 20, 50, "Edgeville Dungeon", false),
		INFERNAL_MAGE(new int[]
				              {443}, 45, 70, "the Slayer Tower", true, 7396, 1750),
		IRON_DRAGON(new int[]
				            {272}, 70, 95, "Brimhaven Dungeon", false),
		MOSS_GIANT(new int[]
				           {7262}, 25, 55, "Brimhaven Dungeon", false),
		LESSER_DEMON(new int[]
				             {2005}, 50, 65, "Taverley Dungeon", false),
		NECHRYAELS(new int[]
				           {8}, 80, 95, "the Slayer Tower", true, 7411, 3068),
		SKELETON(new int[]
				         {70}, 1, 35, "Edgeville Dungeon", false),
		STEEL_DRAGON(new int[]
				             {274}, 70, 95, "Brimhaven Dungeon", false),
		TZHAAR(new int[]
				       {2167}, 50, 70, "the Tzhaar Cave", false);

		private int levelReq, levelCap;

		private int[] npcId;

		private String location;

		private boolean levelRequired;

		private int superiorNpc;

		private int superiorExperience;

		Task(int[] npcId, int levelReq, int levelCap, String location, boolean levelRequired) {
			this(npcId, levelReq, levelCap, location, levelRequired, -1, 0);
		}

		Task(int[] npcId, int levelReq, int levelCap, String location, boolean levelRequired, int superiorNpc, int superiorExperience) {
			this.npcId = npcId;
			this.levelReq = levelReq;
			this.levelCap = levelCap;
			this.location = location;
			this.levelRequired = levelRequired;
			this.superiorNpc = superiorNpc;
			this.superiorExperience = superiorExperience;
		}

		public boolean isLevelRequired() {
			return levelRequired;
		}

		public int[] getNpcId() {
			return npcId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getlevelCap() {
			return levelCap;
		}

		public String getLocation() {
			return location;
		}

		public int getSuperiorNpc() {
			return superiorNpc;
		}

		public int getSuperiorExperience() {
			return superiorExperience;
		}
	}

	/**
	 * Give the player a Slayer task.
	 *
	 * @param player The associated player.
	 * @param forceGiveTask True, to give player another task, ignoring if the player already has a task.
	 */
	public static void giveTask(Player player, boolean forceGiveTask) {
		if (player.slayerTaskNpcAmount != 0 && !forceGiveTask) {
			player.getDH().sendDialogues(184);
			return;
		}
		ArrayList<Integer> possibleTasksIndexList = new ArrayList<Integer>();
		for (final Task t : Task.values()) {
			if (player.baseSkillLevel[ServerConstants.SLAYER] >= t.getLevelReq() && player.baseSkillLevel[ServerConstants.SLAYER] <= t.getlevelCap()) {
				possibleTasksIndexList.add(t.ordinal());
			}
		}
		Task t = Task.values()[possibleTasksIndexList.get(Misc.random(possibleTasksIndexList.size() - 1))];
		player.slayerTaskNpcType = t.getNpcId()[0];
		// Gladiators get longer tasks because they have 6x slower xp rate.
		int baseAmount = GameMode.getDifficulty(player, "GLADIATOR") ? 30 : 10;
		player.slayerTaskNpcAmount = Misc.random(baseAmount) + (player.baseSkillLevel[ServerConstants.SLAYER] / 3) + baseAmount;
		player.getDH().sendDialogues(12);
	}

	public static void slayerTaskNPCKilled(Player player, Npc npc, int npcHP) {
		if (player == null) {
			return;
		}
		Task taskData = getTask(npc.npcType);

		boolean taskKilled = false;

		if (taskData != null) {
			if (player.slayerTaskNpcType == taskData.getNpcId()[0]) {
				taskKilled = true;
			}
		}

		for (int index = 0; index < BossPetDrops.NORMAL_BOSS_DATA.length; index++) {
			if (player.slayerTaskNpcType == BossPetDrops.NORMAL_BOSS_DATA[index] && npc.npcType == player.slayerTaskNpcType) {
				taskKilled = true;
				break;
			}
		}

		if (taskKilled) {
			player.slayerTaskNpcAmount--;

			if (taskData != null && taskData.superiorNpc != -1 && (
					ThreadLocalRandom.current().nextInt(0, SUPERIOR_PROBABILITY) == 0 || player.getPlayerName().equalsIgnoreCase("jason") && ServerConfiguration.DEBUG_MODE)) {
				Position tile = Region.nextOpenTileOrNull(player.getX(), player.getY(), player.getHeight());

				if (tile == null) {
					tile = new Position(player);
				}
				Npc superior = NpcHandler.spawnNpc(player, taskData.superiorNpc, tile.getX(), tile.getY(), tile.getZ(), false, false);

				if (superior != null) {
					if (superior instanceof SuperiorNpc) {
						superior.getAttributes().put(SuperiorNpc.SPAWNED_FOR, player.getPlayerName());
						player.getPA().sendMessage("A superior foe has appeared...");
					} else {
						superior.setItemsDroppable(false);
						superior.killIfAlive();
					}
				}
			}

			if (Skilling.hasMasterCapeWorn(player, 9786) && Misc.hasPercentageChance(10)) {
				Skilling.addSkillExperience(player, npcHP * 3, ServerConstants.SLAYER, false);
				player.getPA().sendMessage("<col=a54704>Your cape allows you to gain 3x the usual experience.");
			} else {
				Skilling.addSkillExperience(player, npcHP, ServerConstants.SLAYER, false);
			}
			if (player.slayerTaskNpcAmount <= 0) {
				player.slayerTaskNpcAmount = 0;
				player.slayerTaskNpcType = -1;
				player.skillingStatistics[SkillingStatistics.SLAYER_TASKS]++;
				if (GameType.isOsrsPvp()) {
					int bloodMoney = player.baseSkillLevel[ServerConstants.SLAYER] * 30;
					if (player.isSupremeDonator()) {
						bloodMoney += 6000;
					} else if (player.isImmortalDonator()) {
						bloodMoney += 5000;
					} else if (player.isUberDonator()) {
						bloodMoney += 4000;
					} else if (player.isUltimateDonator()) {
						bloodMoney += 3000;
					} else if (player.isLegendaryDonator()) {
						bloodMoney += 2000;
					}
					player.playerAssistant.sendMessage("You have completed your slayer assignment and receive " + Misc.formatNumber(bloodMoney) + " Blood money.");
					ItemAssistant.addItemToInventoryOrDrop(player, ServerConstants.getMainCurrencyId(), bloodMoney);
					CoinEconomyTracker.addIncomeList(player, "SKILLING " + bloodMoney);
				} else {
					int points = (int) (player.baseSkillLevel[ServerConstants.SLAYER] / 2) + 10;
					if (GameMode.getDifficulty(player, "GLADIATOR")) {
						points *= 1.2;
					}

					if (player.isSupremeDonator()) {
						points += 250;
					} else if (player.isImmortalDonator()) {
						points += 200;
					} else if (player.isUberDonator()) {
						points += 150;
					} else if (player.isUltimateDonator()) {
						points += 100;
					} else if (player.isLegendaryDonator()) {
						points += 50;
					}
					player.setSlayerPoints(player.getSlayerPoints() + points);
					player.playerAssistant.sendMessage("You have completed your slayer assignment and receive " + points + " Slayer points.");
				}
			}
		}
	}

	public static void giveBossTask(Player player) {
		if (GameType.isOsrsEco()) {
			if (player.baseSkillLevel[ServerConstants.SLAYER] < 90) {
				player.getDH().sendStatement("You need 90 slayer to be assigned a boss task.");
				return;
			}
		}

		// You already have a task.
		if (player.slayerTaskNpcAmount != 0) {
			player.getDH().sendDialogues(184);
			return;
		}
		int random = Misc.random((BossPetDrops.NORMAL_BOSS_DATA.length - 1));
		player.slayerTaskNpcType = BossPetDrops.NORMAL_BOSS_DATA[random];
		player.slayerTaskNpcAmount = Misc.random(14, 22);
		player.getDH().sendDialogues(12);
	}

	public static void resetTask(Player player) {
		if (player.slayerTaskNpcType <= 0) {
			player.getPA().sendMessage("You do not have any assignment.");
			player.getPA().closeInterfaces(true);
			return;
		}
		if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), getTaskResetCost())) {
			player.getDH().sendDialogues(293);
			return;
		}


		player.getPA().closeInterfaces(true);
		player.getPA()
		      .sendMessage("Your slayer assignment has been reset for " + Misc.formatRunescapeStyle(Slayer.getTaskResetCost()) + " " + ServerConstants.getMainCurrencyName() + ".");
		player.slayerTaskNpcType = 0;
		player.slayerTaskNpcAmount = 0;

	}

	/**
	 * True if the npcType given is a slayer npc or slayer boss npc.
	 */
	public static boolean isSlayerNpc(int npcType) {
		if (getTask(npcType) != null) {
			return true;
		}

		for (int index = 0; index < BossPetDrops.NORMAL_BOSS_DATA.length; index++) {
			if (BossPetDrops.NORMAL_BOSS_DATA[index] == npcType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the enum task instance depending on the npcType given.
	 */
	public static Task getTask(int npcType) {
		for (final Task data : Task.values()) {
			for (int index = 0; index < data.getNpcId().length; index++) {
				if (npcType == data.getNpcId()[index]) {
					return data;
				}
			}
		}
		return null;
	}

	public static void dismantleSlayerHelm(Player player, int itemId) {
		if (!GameType.isOsrsPvp()) {
			if (ItemAssistant.getFreeInventorySlots(player) < 6) {
				player.getPA().sendMessage("You need at least 6 free inventory slots to do this.");
				return;
			}
			ItemAssistant.deleteItemFromInventory(player, itemId, 1); // Helm id
			ItemAssistant.addItem(player, 8921, 1); // Black mask
			ItemAssistant.addItem(player, 4166, 1); // Earmuffs
			ItemAssistant.addItem(player, 4164, 1); // Face mask
			ItemAssistant.addItem(player, 4168, 1); // Nose peg
			ItemAssistant.addItem(player, 4551, 1); // Spiny helmet
			ItemAssistant.addItem(player, 4155, 1); // Enchanted gem
			player.getDH().sendItemChat("", "You dismantle your Slayer helm.", itemId, 200, 10, 10);
		}
	}

	public static void checkSlayerHelmOption(Player player) {
		String taskName = "Nothing";
		boolean hasTask = player.slayerTaskNpcAmount > 0;
		if (player.slayerTaskNpcType >= 0) {
			if (NpcDefinition.getDefinitions()[player.slayerTaskNpcType] != null) {
				taskName = NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name;
			}
		}
		player.getPA().sendMessage(hasTask ? "Your slayer task is to kill " + player.slayerTaskNpcAmount + " " + taskName + "." : "You don't currently have a slayer task.");

	}

	public static String getTaskString(Player player) {
		if (player.slayerTaskNpcType <= 0) {
			return "You do not have a task.";
		}

		String taskName = "Nothing";
		if (player.slayerTaskNpcType >= 0) {
			if (NpcDefinition.getDefinitions()[player.slayerTaskNpcType] != null) {
				taskName = NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name;
			}
		}
		return "You currently have " + player.slayerTaskNpcAmount + " " + taskName + "s left.";
	}
}
