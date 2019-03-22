package game.content.skilling.fishing;

import java.util.ArrayList;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Fishing.
 *
 * @author MGT Madness, re-written on 22-07-2014.
 **/

public class FishingOld {
	public static long timeOtherFishingSpotsChanged;

	public static long timeDarkCrabFishingSpotChanged;

	public static long timeAnglerfishFishingSpotChanged;

	public static long timeKarambwanFishingSpotChanged;

	public static final int RAW_SHRIMP = 317;

	public static final int RAW_ANCHOVIES = 321;

	public static final int RAW_TROUT = 335;

	public static final int RAW_SALMON = 331;

	public static final int RAW_LOBSTER = 377;

	public static final int RAW_TUNA = 359;

	public static final int RAW_SWORDFISH = 371;

	public static final int RAW_MONKFISH = 7944;

	public static final int RAW_KARAMBWAN = 3142;

	public static final int RAW_SHARK = 383;

	public static final int RAW_DARK_CRAB = 11934;

	public static final int RAW_ANGLERFISH = 13439;

	private static final int[] REQUIREMENTS =
			{1, 15, 20, 30, 35, 40, 50, 62, 65, 76, 85, 90};

	private static final int[] FISH_TYPES =
			{RAW_SHRIMP, RAW_ANCHOVIES, RAW_TROUT, RAW_SALMON, RAW_TUNA, RAW_LOBSTER, RAW_SWORDFISH,
					RAW_MONKFISH, RAW_KARAMBWAN, RAW_SHARK, RAW_DARK_CRAB, RAW_ANGLERFISH};

	private static final int[] EXPERIENCE =
			{10, 40, 50, 70, 80, 85, 90, 100, 120, 140, 190, 200};

	private enum Fish {
		SHRIMP(317, 1, 10, 621, 303, -1, 1),
		ANCHOVIES(321, 15, 40, 621, 303, -1, 1),
		TROUT(335, 20, 50, 622, 309, 314, 1),
		SALMON(331, 30, 70, 622, 309, 314, 1),
		TUNA(359, 35, 80, 618, 311, -1, 2),
		LOBSTER(377, 40, 85, 619, 301, -1, 3),
		SWORDFISH(371, 50, 90, 618, 311, -1, 3),
		MONKFISH(7944, 62, 100, 620, 303, -1, 4),
		KARAMBWAN(3142, 65, 120, 1193, 3159, -1, 5),
		SHARK(383, 76, 140, 618, 311, -1, 5),
		DARK_CRAB(11934, 85, 190, 619, 301, 11940, 8),
		ANGLERFISH(13439, 90, 200, 309, 303, 13431, 8);

		private int itemId, level, experience, animation, equipment, bait, timer;

		private Fish(int itemId, int level, int experience, int animation, int equipment, int bait, int timer) {
			this.itemId = itemId;
			this.level = level;
			this.experience = experience;
			this.animation = animation;
			this.equipment = equipment;
			this.bait = bait;
			this.timer = timer;
		}

		public int getItemId() {
			return itemId;
		}

		public int getRequiredLevel() {
			return level;
		}

		public int getExperience() {
			return experience;
		}

		public int getAnimation() {
			return animation;
		}

		public int getEquipment() {
			return equipment;
		}

		public int getBait() {
			return bait;
		}

		public int getTimer() {
			return timer;
		}
	}

	public final static int SANDWORMS = 13431;

	public static ArrayList<String> currentFishingSpots = new ArrayList<String>();

	public static ArrayList<String> darkCrabCurrentFishingSpots = new ArrayList<String>();

	public static ArrayList<String> darkCrabDonatorZoneCurrentFishingSpots = new ArrayList<String>();

	public static ArrayList<String> anglerfishCurrentFishingSpots = new ArrayList<String>();

	public static ArrayList<String> karambwanCurrentFishingSpots = new ArrayList<String>();

	public static ArrayList<String> karambwanDonatorZoneCurrentFishingSpots = new ArrayList<String>();

	public static void fillCurrentFishingSpots() {
		currentFishingSpots.add("2876 3342");
		currentFishingSpots.add("2879 3339");
		currentFishingSpots.add("2879 3335");
		currentFishingSpots.add("2876 3331");
		darkCrabCurrentFishingSpots.add("3045 3702");
		darkCrabDonatorZoneCurrentFishingSpots.add("2527 2711");
		anglerfishCurrentFishingSpots.add("2535 2713");
		karambwanCurrentFishingSpots.add("2899 3119");
		karambwanDonatorZoneCurrentFishingSpots.add("2532 2711");
	}

	private static void moveFishingSpot(Player player, Npc npc) {
		stopFishing(player);
		ArrayList<String> newArray1 = new ArrayList<String>();
		// Dark crab
		if (npc.npcType == 3915 && Area.inDonatorZone(player.getX(), player.getY())) {
			newArray1.add("2532 2711");
			newArray1.add("2532 2711");
		}
		// Dark crab
		else if (npc.npcType == 3915) {
			newArray1.add("3050 3704");
			newArray1.add("3052 3705");
			newArray1.add("3044 3700");
			newArray1.add("3047 3699");
			newArray1.add("3052 3697");
		}
		// Anglerfish
		else if (npc.npcType == 4082) {
			newArray1.add("2527 2711");
			newArray1.add("2532 2711");
		}
		// Karambwan
		else if (npc.npcType == 4712 && Area.inDonatorZone(player.getX(), player.getY())) {
			newArray1.add("2527 2711");
			newArray1.add("2535 2713");
		}
		// Karambwan
		else if (npc.npcType == 4712) {
			newArray1.add("2896 3120");
			newArray1.add("2911 3119");
			newArray1.add("2912 3119");
		}
		else {
			newArray1.add("2875 3342");
			newArray1.add("2876 3342");
			newArray1.add("2877 3342");
			newArray1.add("2879 3339");
			newArray1.add("2879 3338");
			newArray1.add("2879 3335");
			newArray1.add("2879 3334");
			newArray1.add("2877 3331");
			newArray1.add("2876 3331");
			newArray1.add("2875 3331");
		}

		String currentCoordinates = Integer.toString(npc.getX()) + " " + Integer.toString(npc.getY());
		String newCoordinate = "";

		// Dark crab
		if (npc.npcType == 3915 && Area.inDonatorZone(player.getX(), player.getY())) {
			for (int i = 0; i < darkCrabDonatorZoneCurrentFishingSpots.size(); i++) {
				if (newArray1.contains(darkCrabDonatorZoneCurrentFishingSpots.get(i))) {
					newArray1.remove(darkCrabDonatorZoneCurrentFishingSpots.get(i));
				}
			}
			darkCrabDonatorZoneCurrentFishingSpots.remove(currentCoordinates);
			if (!newArray1.isEmpty()) {
				newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
				darkCrabDonatorZoneCurrentFishingSpots.add(newCoordinate);
			}
		}
		// Karambwan
		if (npc.npcType == 4712 && Area.inDonatorZone(player.getX(), player.getY())) {
			for (int i = 0; i < karambwanDonatorZoneCurrentFishingSpots.size(); i++) {
				if (newArray1.contains(karambwanDonatorZoneCurrentFishingSpots.get(i))) {
					newArray1.remove(karambwanDonatorZoneCurrentFishingSpots.get(i));
				}
			}
			karambwanDonatorZoneCurrentFishingSpots.remove(currentCoordinates);
			if (!newArray1.isEmpty()) {
				newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
				karambwanDonatorZoneCurrentFishingSpots.add(newCoordinate);
			}
		}
		// Dark crab
		else if (npc.npcType == 3915) {
			for (int i = 0; i < darkCrabCurrentFishingSpots.size(); i++) {
				if (newArray1.contains(darkCrabCurrentFishingSpots.get(i))) {
					newArray1.remove(darkCrabCurrentFishingSpots.get(i));
				}
			}
			darkCrabCurrentFishingSpots.remove(currentCoordinates);
			if (!newArray1.isEmpty()) {
				newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
				darkCrabCurrentFishingSpots.add(newCoordinate);
			}
		} else if (npc.npcType == 4082) {
			for (int i = 0; i < anglerfishCurrentFishingSpots.size(); i++) {
				if (newArray1.contains(anglerfishCurrentFishingSpots.get(i))) {
					newArray1.remove(anglerfishCurrentFishingSpots.get(i));
				}
			}
			anglerfishCurrentFishingSpots.remove(currentCoordinates);
			if (!newArray1.isEmpty()) {
				newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
				anglerfishCurrentFishingSpots.add(newCoordinate);
			}
		} else {
			for (int i = 0; i < currentFishingSpots.size(); i++) {
				if (newArray1.contains(currentFishingSpots.get(i))) {
					newArray1.remove(currentFishingSpots.get(i));
				}
			}
			currentFishingSpots.remove(currentCoordinates);
			if (!newArray1.isEmpty()) {
				newCoordinate = newArray1.get(Misc.random(0, newArray1.size() - 1));
				currentFishingSpots.add(newCoordinate);
			}
		}

		if (newCoordinate.isEmpty()) {
			return;
		}
		int newX = Integer.parseInt(newCoordinate.substring(0, 4));
		int newY = Integer.parseInt(newCoordinate.substring(5, 9));
		int npcType = npc.npcType;
		Pet.deletePet(npc);
		Pet.summonNpc(player, npcType, newX, newY, 0, false, false);
	}

	private static int getAnimation(int fish) {
		switch (fish) {

			case RAW_SHARK:
			case RAW_TUNA:
			case RAW_SWORDFISH:
				return 618;

			case RAW_SHRIMP:
			case RAW_ANCHOVIES:
				return 621;

			case RAW_TROUT:
			case RAW_SALMON:
			case RAW_ANGLERFISH:
				return 622;

			case RAW_LOBSTER:
			case RAW_DARK_CRAB:
				return 619;

			case RAW_MONKFISH:
				return 620;

			case RAW_KARAMBWAN:
				return 1193;
		}

		return 0;

	}

	private static String getName(int fish) {
		switch (fish) {
			case RAW_SHRIMP:
			case RAW_ANCHOVIES:
				return "some";
		}

		return "a";

	}

	private static int getItemRequirement(int fish) {
		switch (fish) {

			case RAW_SHARK:
			case RAW_TUNA:
			case RAW_SWORDFISH:
				return 311;

			case RAW_TROUT:
			case RAW_SALMON:
				return 309;

			case RAW_ANGLERFISH:
				return 307;

			case RAW_SHRIMP:
			case RAW_ANCHOVIES:
				return 303;

			case RAW_LOBSTER:
			case RAW_DARK_CRAB:
				return 301;

			case RAW_MONKFISH:
				return 303;

			case RAW_KARAMBWAN:
				return 3159;
		}

		return 0;

	}

	/**
	 * Start fishing.
	 *
	 * @param player The associated player.
	 * @param fishType The type of fish being fished.
	 */
	public static void startFishing(Player player, int fishType) {
		int fishOrder = 0;
		for (int b = 0; b < FISH_TYPES.length; b++) {
			if (FISH_TYPES[b] == fishType) {
				fishOrder = b;
				break;
			}
		}
		setInitialFishingSpotCoordinates(player);
		if (!fishingRequirements(player, fishOrder)) {
			stopFishing(player);
			player.startAnimation(65535);
			return;
		}
		SoundSystem.sendSound(player, 289, 500);
		startFishTimerEvent(player, fishOrder);

	}

	private static void setInitialFishingSpotCoordinates(Player player) {
		for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				continue;
			}
			if (NpcHandler.npcs[i].npcType == player.getNpcType() && !Area.inWilderness(NpcHandler.npcs[i].getX(), NpcHandler.npcs[i].getY(), NpcHandler.npcs[i].getHeight())) {
				player.lastFishingSpotX = NpcHandler.npcs[i].getX();
				player.lastFishingSpotY = NpcHandler.npcs[i].getY();
			}
		}
	}

	private static void findNewFishingSpot(Player player) {
		if (Area.inResourceWildernessOsrs(player)) {
			return;
		}
		if (Misc.hasPercentageChance(90)) {
			return;
		}
		if (System.currentTimeMillis() - (player.getNpcType() == 3915 ?
				                                  timeDarkCrabFishingSpotChanged :
				                                  player.getNpcType() == 4082 ? timeAnglerfishFishingSpotChanged : timeOtherFishingSpotsChanged) < 40000) {
			return;
		}

		if (player.getNpcType() == 3915) {
			timeDarkCrabFishingSpotChanged = System.currentTimeMillis();
		}
		if (player.getNpcType() == 4712) {
			timeKarambwanFishingSpotChanged = System.currentTimeMillis();
		}
		if (player.getNpcType() == 4082) {
			timeAnglerfishFishingSpotChanged = System.currentTimeMillis();
		} else {
			timeOtherFishingSpotsChanged = System.currentTimeMillis();
		}
		for (int j = 0; j < NpcHandler.npcs.length; j++) {
			if (NpcHandler.npcs[j] != null) {
				if (NpcHandler.npcs[j].npcType == player.getNpcType() && NpcHandler.npcs[j].getX() == player.lastFishingSpotX && NpcHandler.npcs[j].getY() == player.lastFishingSpotY) {
					moveFishingSpot(player, NpcHandler.npcs[j]);
					break;
				}
			}
		}
	}

	/**
	 * @param player The associated player.
	 * @param fishOrder The fish being fished.
	 * @return True, if the player has all the requirements to start fishing.
	 */
	public static boolean fishingRequirements(Player player, int fishOrder) {
		boolean needFeather = FISH_TYPES[fishOrder] == RAW_TROUT || FISH_TYPES[fishOrder] == RAW_SALMON || FISH_TYPES[fishOrder] == RAW_SALMON ? true : false;
		boolean needsSandworms = FISH_TYPES[fishOrder] == RAW_ANGLERFISH ? true : false;
		if (Area.inDonatorZone(player.getX(), player.getY())) {
			if (!DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.EXTREME_DONATOR)) {
				return false;
			}
		}
		if (!ItemAssistant.hasItemInInventory(player, getItemRequirement(FISH_TYPES[fishOrder]))) {
			player.getDH().sendStatement(
					"You need a " + ItemAssistant.getItemName(getItemRequirement(FISH_TYPES[fishOrder])) + " to fish " + ItemDefinition.getDefinitions()[FISH_TYPES[fishOrder]].name
					+ ".");
			return false;
		}


		if (needFeather && !ItemAssistant.hasItemInInventory(player, 314)) {
			player.getDH().sendStatement("You have run out of feathers.");
			return false;
		}


		if (needsSandworms && !ItemAssistant.hasItemInInventory(player, SANDWORMS)) {
			player.getDH().sendStatement("You have run out of sandworms.");
			return false;
		}

		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("You do not have free inventory space.");
			return false;
		}
		if (player.baseSkillLevel[ServerConstants.FISHING] < REQUIREMENTS[fishOrder]) {
			player.getDH().sendStatement("You need a fishing level of " + REQUIREMENTS[fishOrder] + " to catch this fish.");
			return false;
		}
		if (!Area.inResourceWildernessOsrs(player)) {
			boolean notFound = true;
			for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
				if (NpcHandler.npcs[i] == null) {
					continue;
				}
				if (NpcHandler.npcs[i].npcType == player.getNpcType() && !Area.inWilderness(NpcHandler.npcs[i].getX(), NpcHandler.npcs[i].getY(), NpcHandler.npcs[i].getHeight())) {
					if (NpcHandler.npcs[i].getX() == player.lastFishingSpotX && NpcHandler.npcs[i].getY() == player.lastFishingSpotY) {
						notFound = false;
					}
				}
			}
			if (notFound) {
				return false;
			}
		}
		return true;
	}

	private static int setFishingTimer(Player player) {
		int timer = 0;

		switch (player.getNpcType()) {

			// Shrimp.
			case 3913:
				timer += 1;
				break;

			// Lobster.
			case 3914:
				timer += 3;
				break;

			// Monk fish.
			case 635:
				timer += 4;
				break;

			//Sharks
			case 1506:
				timer += 5;
				break;

			// Karambwan.
			case 4712:
				timer += 16;
				break;

			// Dark crab.
			case 3915:
				timer += 8;
				break;

			// Anglerfish
			case 4082:
				timer += 8;
				break;
		}

		int value = 30;
		int maximum = (int) (value - (player.baseSkillLevel[ServerConstants.FISHING] * (value / 99.0))) + timer;
		int baseMinimum = maximum / 2;
		timer = Misc.random(baseMinimum, maximum);
		return timer;
	}

	/**
	 * Start the fishing cycle event.
	 *
	 * @param player The associated player.
	 * @param fishOrder The order of the fish in the array being fished.
	 */
	private static void startFishTimerEvent(final Player player, final int fishOrder) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		player.fishTimerAmount = setFishingTimer(player);
		player.startAnimation(getAnimation(FISH_TYPES[fishOrder]));
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (player.fishTimerAmount > 0) {
					player.fishTimerAmount--;
					player.startAnimation(getAnimation(FISH_TYPES[fishOrder]));
				} else {
					container.stop();
					Skilling.endSkillingEvent(player);
					if (player.fishTimerAmount == 0) {
						catchFish(player, fishOrder);
					}
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 1);

	}

	/**
	 * Force stop fishing.
	 *
	 * @param player The associated player.
	 */
	public static void stopFishing(Player player) {
		player.fishTimerAmount = -1;
	}

	/**
	 * Successfully catch the fish.
	 *
	 * @param player The associated player.
	 * @param fishOrder The order of the fish used in the fish array.
	 */
	public static void catchFish(Player player, int fishOrder) {
		if (!fishingRequirements(player, fishOrder)) {
			stopFishing(player);
			player.startAnimation(65535);
			return;
		}
		if (FISH_TYPES[fishOrder] == RAW_TROUT || FISH_TYPES[fishOrder] == RAW_SALMON) {
			int chanceForSalmon = 0;
			int level = player.baseSkillLevel[ServerConstants.FISHING];
			if (level >= 30) {
				chanceForSalmon = 99 - level;
				chanceForSalmon += 15;
				chanceForSalmon = 100 - chanceForSalmon;
				if (Misc.hasPercentageChance(chanceForSalmon)) {
					fishOrder = 3; // Changed to Salmon.
				} else {
					fishOrder = 2;
				}
			}
		}
		if (FISH_TYPES[fishOrder] == RAW_SHRIMP || FISH_TYPES[fishOrder] == RAW_ANCHOVIES) {
			int chanceForAnchovies = 0;
			int level = player.baseSkillLevel[ServerConstants.FISHING];
			if (level >= 15) {
				chanceForAnchovies = 99 - level;
				chanceForAnchovies += 5;
				chanceForAnchovies = 100 - chanceForAnchovies;
				if (Misc.hasPercentageChance(chanceForAnchovies)) {
					fishOrder = 1;
				} else {
					fishOrder = 0;
				}
			}
		}
		if (FISH_TYPES[fishOrder] == RAW_TUNA || FISH_TYPES[fishOrder] == RAW_SWORDFISH) {
			int chanceForSwordy = 0;
			int level = player.baseSkillLevel[ServerConstants.FISHING];
			if (level >= 50) {
				chanceForSwordy = 99 - level;
				chanceForSwordy += 15;
				chanceForSwordy = 100 - chanceForSwordy;
				if (Misc.hasPercentageChance(chanceForSwordy)) {
					fishOrder = 6; // Changed to Swordfish.
				} else {
					fishOrder = 4;
				}
			}
		}
		if (FISH_TYPES[fishOrder] == RAW_SHARK) {
			Achievements.checkCompletionMultiple(player, "1036");
		} else if (FISH_TYPES[fishOrder] == RAW_DARK_CRAB) {
			Achievements.checkCompletionMultiple(player, "1062 1128");
		}


		boolean needsSandworms = FISH_TYPES[fishOrder] == RAW_ANGLERFISH ? true : false;
		if (needsSandworms) {
			ItemAssistant.deleteItemFromInventory(player, SANDWORMS, 1);
		}

		boolean needFeather = FISH_TYPES[fishOrder] == RAW_TROUT || FISH_TYPES[fishOrder] == RAW_SALMON ? true : false;
		if (needFeather) {
			ItemAssistant.deleteItemFromInventory(player, 314, 1);
		}
		player.skillingStatistics[SkillingStatistics.FISH_CAUGHT]++;


		SoundSystem.sendSound(player, 378, 500);
		ItemAssistant.addItem(player, FISH_TYPES[fishOrder], 1);
		if (Skilling.hasMasterCapeWorn(player, 9798) && Misc.hasPercentageChance(10)) {
			Skilling.addHarvestedResource(player, FISH_TYPES[fishOrder], 1);
			ItemAssistant.addItemToInventoryOrDrop(player, FISH_TYPES[fishOrder], 1);
			player.getPA().sendMessage("<col=a54704>Your cape allows you to catch an extra fish.");
		}
		Skilling.addHarvestedResource(player, FISH_TYPES[fishOrder], 1);
		int experience = (int) (EXPERIENCE[fishOrder] * 1.05);
		if (GameType.isOsrs()) {
			for (int index = 0; index < ServerConstants.ANGLER_PIECES.length; index++) {
				int itemId = ServerConstants.ANGLER_PIECES[index][0];
				if (ItemAssistant.hasItemEquippedSlot(player, itemId, ServerConstants.ANGLER_PIECES[index][1])) {
					experience *= ServerConstants.SKILLING_SETS_EXPERIENCE_BOOST_PER_PIECE;
				}
			}
		}
		Skilling.addSkillExperience(player, experience, ServerConstants.FISHING, false);
		player.playerAssistant.sendFilterableMessage("You catch " + getName(FISH_TYPES[fishOrder]) + " " + ItemAssistant.getItemName(FISH_TYPES[fishOrder]) + ".");
		Skilling.petChance(player, experience, 200, 4800, ServerConstants.FISHING, null); // 1 in 8 hours at 99
		findNewFishingSpot(player);
		startFishing(player, FISH_TYPES[fishOrder]);
	}
}
