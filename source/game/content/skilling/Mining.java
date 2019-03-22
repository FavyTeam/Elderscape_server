package game.content.skilling;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.crafting.GemCrafting;
import game.item.ItemAssistant;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.npc.pet.PetData;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.ArrayList;
import utility.Misc;

/**
 * Mining.
 */
public class Mining {
	// Enum for pickaxe requirements and base mining timer and animation
	// enum for ores, ore base timer, ore id, xp, level requirement

	public static final int PURE_ESSENCE_ITEM = 7936;

	public static final int RUNE_ESSENCE_ITEM = 1436;

	public static final int ESSENCE_ORE = 1436;

	public static final int CLAY = 7454;

	public static final int CLAY_OTHER = 7487;

	private static final int COPPER_ORE = 7484;

	private static final int COPPER_ORE_OTHER = 7453;

	private static final int TIN_ORE = 7485;

	private static final int TIN_ORE_OTHER = 7486;

	private static final int IRON_ORE = 7455;

	private static final int IRON_ORE_OTHER = 7488;

	private static final int SILVER_ORE = 7457;

	private static final int SILVER_ORE_OTHER = 7490;

	private static final int COAL = 7489;

	private static final int COAL_OTHER = 7456;

	private static final int GOLD_ORE = 7491;

	private static final int GOLD_ORE_OTHER = 7458;

	private static final int MITHRIL_ORE = 7492;

	private static final int MITHRIL_ORE_OTHER = 7459;

	private static final int ADAMANT_ORE = 7493;

	private static final int ADAMANT_ORE_OTHER = 7460;

	private static final int RUNITE_ORE = 7494;

	private static final int RUNITE_ORE_OTHER = 7461;

	private static final int CHOCOLATE = 15301;

	private static final int CHOCOLATE_OTHER = 15299;


	/**
	 * Store the ore id and time removed, if 60 seconds between last remove, do not, if 7 seconds between last remove, then ore doesn't exist.
	 */
	public static ArrayList<String> oreRemovedList = new ArrayList<String>();

	/**
	 * Mining object identities.
	 */
	private static int[] miningObject =
			{
					ESSENCE_ORE,
					COPPER_ORE,
					CLAY,
					CLAY_OTHER,
					TIN_ORE,
					COPPER_ORE_OTHER,
					TIN_ORE_OTHER,
					IRON_ORE,
					IRON_ORE_OTHER,
					SILVER_ORE,
					SILVER_ORE_OTHER,
					COAL,
					COAL_OTHER,
					GOLD_ORE,
					GOLD_ORE_OTHER,
					MITHRIL_ORE,
					MITHRIL_ORE_OTHER,
					ADAMANT_ORE,
					ADAMANT_ORE_OTHER,
					RUNITE_ORE,
					RUNITE_ORE_OTHER,
					CHOCOLATE,
					CHOCOLATE_OTHER
			};

	public static int getRockGolemPetType(int objectId) {
		switch (objectId) {
			case SILVER_ORE:
			case SILVER_ORE_OTHER:
				return 21191;
			case COPPER_ORE:
			case COPPER_ORE_OTHER:
			case CLAY:
			case CLAY_OTHER:
				return 13321;
			case TIN_ORE:
			case TIN_ORE_OTHER:
				return 21187;
			case IRON_ORE:
			case IRON_ORE_OTHER:
				return 21189;
			case COAL:
			case COAL_OTHER:
				return 21192;
			case MITHRIL_ORE:
			case MITHRIL_ORE_OTHER:
				return 21194;
			case ADAMANT_ORE:
			case ADAMANT_ORE_OTHER:
				return 21196;
			case RUNITE_ORE:
			case RUNITE_ORE_OTHER:
				return 21197;
			case GOLD_ORE:
			case GOLD_ORE_OTHER:
				return 21193;
		}
		return 0;


	}

	public static void switchPetColour(Player player) {
		boolean firstPet = player.getPetSummoned() && player.getPetId() >= 7439 && player.getPetId() <= 7450;
		boolean secondPet = player.getSecondPetSummoned() && player.getSecondPetId() >= 7439 && player.getSecondPetId() <= 7450;
		int petId = firstPet ? player.getPetId() : player.getSecondPetId();
		if (firstPet || secondPet) {
			int switchTo = getRockGolemPetType(player.getObjectId());

			for (int index = 0; index < PetData.petData.length; index++) {
				if (switchTo == PetData.petData[index][1]) {
					switchTo = PetData.petData[index][0];
					break;
				}
			}
			if (switchTo > 0 && switchTo != (firstPet ? player.getPetId() : player.getSecondPetId())) {
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
					if (NpcHandler.npcs[i] == null) {
						continue;
					}
					if (NpcHandler.npcs[i].getNpcPetOwnerId() == player.getPlayerId() && NpcHandler.npcs[i].npcType == petId) {
						int oldX = NpcHandler.npcs[i].getX();
						int oldY = NpcHandler.npcs[i].getY();
						int oldHeight = NpcHandler.npcs[i].getHeight();
						Pet.deletePet(NpcHandler.npcs[i]);
						Pet.summonNpc(player, switchTo, oldX, oldY, oldHeight, true, secondPet);
						player.getPA().sendMessage("Your rock golem changes material!");
						break;
					}
				}
			}
		}
	}

	/**
	 * Start the mining procedure.
	 *
	 * @param player The player mining.
	 */
	public static void startMining(Player player) {
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.pickAxeUsed = getHighestPickaxeId(player);
		if (!hasUseAblePickaxe(player)) {
			return;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.getDH().sendStatement("You don't have enough inventory space.");
			return;
		}
		if (player.baseSkillLevel[ServerConstants.MINING] >= player.oreInformation[1]) {
			if (player.getObjectId() == CHOCOLATE || player.getObjectId() == CHOCOLATE_OTHER) {
				player.playerAssistant.sendFilterableMessage("You swing your pickaxe at the chocolate...");
			} else {
				player.playerAssistant.sendFilterableMessage("You swing your pickaxe at the rock.");
			}
			player.miningTimer = setMiningTimer(player);
			startMiningTimerEvent(player);
			player.startAnimation(pickAxeAnimation(player));
		} else {
			player.getDH().sendStatement("You need a mining level of " + player.oreInformation[1] + " to mine this rock.");
			player.startAnimation(65535);
		}
	}

	public enum Pickaxes {
		BRONZE(1, 625, 1265, 9),
		IRON(1, 626, 1267, 8),
		STEEL(6, 627, 1269, 7),
		MITHRIL(21, 629, 1273, 6),
		ADAMANT(31, 628, 1271, 5),
		RUNE(41, 624, 1275, 4),
		DRAGON(61, 7139, 11920, 2),
		DRAGON_ORNAMENTAL(61, 643, 12797, 2),
		INFERNAL(61, 4483, 13243, 1),
		THIRD_AGE_PICK(61, 7283, 20014, 1);

		/**
		 * Ints for enum
		 */
		private int levelRequired;

		private int animation;

		private int itemId;

		private int timer;

		/**
		 * Constructor for enum
		 *
		 * @param levelRequired
		 * @param animation
		 * @param itemId
		 */
		Pickaxes(final int levelRequired, final int animation, final int itemId, final int timer) {
			this.levelRequired = levelRequired;
			this.animation = animation;
			this.itemId = itemId;
			this.timer = timer;
		}

		/**
		 * Getter for level
		 *
		 * @return
		 */
		public int getLevelRequired() {
			return levelRequired;
		}

		/**
		 * Getter for animation
		 *
		 * @return
		 */
		public int getAnimation() {
			return animation;
		}

		/**
		 * Getter for itemId
		 *
		 * @return
		 */
		public int getItemId() {
			return itemId;
		}

		public int getTimer() {
			return timer;
		}
	}

	private static int getHighestPickaxeId(Player player) {
		int highest = 0;
		for (Pickaxes data : Pickaxes.values()) {
			if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId()))
			    && player.baseSkillLevel[ServerConstants.MINING] >= data.getLevelRequired()) {
				highest = data.getItemId();
			}
		}
		return highest;
	}

	private static int setMiningTimer(Player player) {
		int base = 0;
		for (Pickaxes data : Pickaxes.values()) {
			if (player.pickAxeUsed == data.getItemId()) {
				base = data.getTimer();
			}
		}

		switch (player.getObjectId()) {
			// Rune/pure essence.
			case ESSENCE_ORE:
				base -= 4;
				break;
			// Copper.
			case COPPER_ORE:
			case COPPER_ORE_OTHER:
			case TIN_ORE:
			case TIN_ORE_OTHER:
				base += 2;
				break;

			// Iron.
			case IRON_ORE:
			case IRON_ORE_OTHER:
				base += 1;
				break;

			// Coal.
			case COAL:
			case COAL_OTHER:
			case SILVER_ORE:
			case SILVER_ORE_OTHER:
				base += 3;
				break;

			case GOLD_ORE:
			case GOLD_ORE_OTHER:
				base += 3;
				break;

			// Mithril.
			case MITHRIL_ORE:
			case MITHRIL_ORE_OTHER:
				base += 4;
				break;

			// Adamant.
			case ADAMANT_ORE:
			case ADAMANT_ORE_OTHER:
				base += 5;
				break;

			// Runite.
			case RUNITE_ORE:
			case RUNITE_ORE_OTHER:
				base += 6;
				break;

			// Easter event.
			case CHOCOLATE:
			case CHOCOLATE_OTHER:
				base += 7;
				break;
		}
		int randomMaximum = (int) (18 - (player.baseSkillLevel[ServerConstants.MINING] * 0.17));
		int random = Misc.random(0, randomMaximum);
		int finalTimer = base + random;
		if (finalTimer < 1) {
			finalTimer = 1;
		}
		return finalTimer;
	}

	/**
	 * Find the correct pick axe animation.
	 *
	 * @param player The player mining.
	 * @return The pick axe animation.
	 */
	public static int pickAxeAnimation(Player player) {
		int animation = 0;
		for (Pickaxes data : Pickaxes.values()) {
			if (player.pickAxeUsed == data.getItemId()) {
				return data.getAnimation();
			}
		}

		return animation;
	}

	private static void createEmptyOre(Player player) {
		// Rune essence object.
		if (player.getObjectId() == ESSENCE_ORE || player.getObjectId() == CHOCOLATE || player.getObjectId() == CHOCOLATE_OTHER) {
			return;
		}
		boolean tinOrCopper =
				player.getObjectId() == TIN_ORE || player.getObjectId() == COPPER_ORE || player.getObjectId() == COPPER_ORE_OTHER || player.getObjectId() == TIN_ORE_OTHER;
		if (!tinOrCopper && !Misc.hasOneOutOf(20)) {
			return;
		}
		long timeValue = 0;
		int listIndex = -1;
		for (int index = 0; index < oreRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (oreRemovedList.get(index).contains(match)) {
				// Time ore removed.
				String time = oreRemovedList.get(index).replace(match + " ", "");
				timeValue = Long.parseLong(time);
				listIndex = index;
				break;
			}
		}

		if (System.currentTimeMillis() - timeValue <= 60000 && !tinOrCopper) {
			return;
		}

		if (listIndex >= 0) {
			oreRemovedList.remove(listIndex);
		}
		new Object(10081, player.getObjectX(), player.getObjectY(), player.getHeight(), 1, 10, player.getObjectId(), 12);
		oreRemovedList.add(player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY() + " " + System.currentTimeMillis());
		player.playerAssistant.stopAllActions();
	}

	public static boolean oreExists(Player player) {
		long timeValue = 0;
		for (int index = 0; index < oreRemovedList.size(); index++) {
			String match = player.getObjectId() + " " + player.getObjectX() + " " + player.getObjectY();
			if (oreRemovedList.get(index).contains(match)) {
				// Time ore removed.
				String time = oreRemovedList.get(index).replace(match + " ", "");
				timeValue = Long.parseLong(time);
				break;
			}
		}
		if (System.currentTimeMillis() - timeValue <= 7000) {
			player.playerAssistant.stopAllActions();
			return false;
		}
		return true;
	}

	/**
	 * Mine the ore and resume mining.
	 *
	 * @param player The player mining.
	 */
	public static void mineOre(Player player) {
		if (player.oreInformation[0] == RUNE_ESSENCE_ITEM && player.baseSkillLevel[ServerConstants.MINING] >= 30) {
			player.oreInformation[0] = PURE_ESSENCE_ITEM;
		}


		if (ItemAssistant.addItem(player, player.oreInformation[0], 1)) {
			// Adamant ore.
			if (player.oreInformation[0] == 449) {
				Achievements.checkCompletionMultiple(player, "1039");
			}
			player.startAnimation(pickAxeAnimation(player));
			if (player.getObjectId() == 2491) {
				player.playerAssistant.sendFilterableMessage("You manage to mine some essence.");
			}
			if (player.getObjectId() == 7454 || player.getObjectId() == 7487) {
				player.playerAssistant.sendFilterableMessage("You manage to mine some clay.");
			}
			if (player.getObjectId() == 15301 || player.getObjectId() == 15299) {
				player.playerAssistant.sendFilterableMessage("You manage to chip away some chocolate chunks.");
			} else {
				player.playerAssistant.sendFilterableMessage("You manage to mine some ore.");
			}
			if (Skilling.hasMasterCapeWorn(player, 9792) && Misc.hasPercentageChance(10)) {
				Skilling.addHarvestedResource(player, player.oreInformation[0], 1);
				ItemAssistant.addItemToInventoryOrDrop(player, player.oreInformation[0], 1);
				player.getPA().sendMessage("<col=a54704>Your cape allows you to mine an extra ore.");
			}
			Skilling.addHarvestedResource(player, player.oreInformation[0], 1);
			int experience = player.oreInformation[2];
			if (GameType.isOsrs()) {
				for (int index = 0; index < ServerConstants.PROSPECTOR_PIECES.length; index++) {
					int itemId = ServerConstants.PROSPECTOR_PIECES[index][0];
					if (ItemAssistant.hasItemEquippedSlot(player, itemId, ServerConstants.PROSPECTOR_PIECES[index][1])) {
						experience *= ServerConstants.SKILLING_SETS_EXPERIENCE_BOOST_PER_PIECE;
					}
				}
			}
			Skilling.addSkillExperience(player, experience, ServerConstants.MINING, false);
			Skilling.petChance(player, experience, 125, 3800, ServerConstants.MINING, null);
			switchPetColour(player);
			player.miningTimer = setMiningTimer(player);
			startMiningTimerEvent(player);
			player.skillingStatistics[SkillingStatistics.ORES_MINED]++;
			createEmptyOre(player);
			giveGem(player);
		} else {
			player.playerAssistant.stopAllActions();
			player.startAnimation(65535);
		}
	}

	private static void giveGem(Player player) {
		if (!Misc.hasPercentageChance(player.getObjectId() == 2491 ? 1 : 3)) {
			return;
		}
		int random = Misc.random(1, 100);
		int item = 0;
		if (random < 10) {
			item = GemCrafting.gemData[3][0];
		} else if (random < 30) {
			item = GemCrafting.gemData[2][0];
		} else if (random < 60) {
			item = GemCrafting.gemData[1][0];
		} else {
			item = GemCrafting.gemData[0][0];
		}
		ItemAssistant.addItem(player, item, 1);
	}

	public static boolean hasUseAblePickaxe(Player player) {
		if (player.pickAxeUsed == 0) {
			player.getPA().sendMessage("You do not have a pickaxe.");
			return false;
		}
		int highest = 0;
		for (Pickaxes data : Pickaxes.values()) {
			if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId()))
			    && player.baseSkillLevel[ServerConstants.MINING] >= data.getLevelRequired()) {
				highest = data.getItemId();
			}
		}
		if (highest == 0) {
			for (Pickaxes data : Pickaxes.values()) {
				if ((ItemAssistant.hasItemInInventory(player, data.getItemId()) || ItemAssistant.hasItemEquipped(player, data.getItemId()))
				    && player.baseSkillLevel[ServerConstants.MINING] < data.getLevelRequired()) {
					player.getDH().sendStatement("You need a mining level of " + data.getLevelRequired() + " to use this pickaxe.");
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @return true, if the object is an ore.
	 */
	public static boolean isMiningObject(final int objectType) {
		for (int i = 0; i < miningObject.length; i++) {
			if (objectType == miningObject[i]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Perform actions of mining related objects.
	 */
	public static void doMiningObject(final Player player, int objectType) {
		if (player.getTransformed() > 0) {
			return;
		}

		if (objectType == CHOCOLATE || objectType == CHOCOLATE_OTHER) {
			player.oreInformation[0] = 22345;
			player.oreInformation[1] = 1;
			player.oreInformation[2] = 0;
			startMining(player);
		} else if (objectType == 2491) {
			player.oreInformation[0] = RUNE_ESSENCE_ITEM; //Ore id
			player.oreInformation[1] = 1; //Level required
			player.oreInformation[2] = 5; //Xp
			startMining(player);
		} else if (objectType == CLAY || objectType == CLAY_OTHER) {
			player.oreInformation[0] = 434; //Ore id
			player.oreInformation[1] = 1; //Level required
			player.oreInformation[2] = 5; //Xp
			startMining(player);
		} else if (objectType == COPPER_ORE || objectType == COPPER_ORE_OTHER) {
			player.oreInformation[0] = 436; //Ore id
			player.oreInformation[1] = 1; //Level required
			player.oreInformation[2] = 18; //Xp
			startMining(player);
		} else if (objectType == TIN_ORE || objectType == TIN_ORE_OTHER) {
			player.oreInformation[0] = 438;
			player.oreInformation[1] = 1;
			player.oreInformation[2] = 18;
			startMining(player);
		} else if (objectType == IRON_ORE || objectType == IRON_ORE_OTHER) {
			player.oreInformation[0] = 440;
			player.oreInformation[1] = 15;
			player.oreInformation[2] = 35;
			startMining(player);
		} else if (objectType == SILVER_ORE || objectType == SILVER_ORE_OTHER) {
			player.oreInformation[0] = 442;
			player.oreInformation[1] = 20;
			player.oreInformation[2] = 40;
			startMining(player);
		} else if (objectType == COAL || objectType == COAL_OTHER) {
			player.oreInformation[0] = 453;
			player.oreInformation[1] = 30;
			player.oreInformation[2] = 50;
			startMining(player);
		} else if (objectType == MITHRIL_ORE || objectType == MITHRIL_ORE_OTHER) {
			player.oreInformation[0] = 447;
			player.oreInformation[1] = 50;
			player.oreInformation[2] = 80;
			startMining(player);
		} else if (objectType == ADAMANT_ORE || objectType == ADAMANT_ORE_OTHER) {
			player.oreInformation[0] = 449;
			player.oreInformation[1] = 70;
			player.oreInformation[2] = 95;
			startMining(player);
		} else if (objectType == RUNITE_ORE || objectType == RUNITE_ORE_OTHER) {
			player.oreInformation[0] = 451;
			player.oreInformation[1] = 85;
			player.oreInformation[2] = 125;
			startMining(player);
		} else if (objectType == GOLD_ORE || objectType == GOLD_ORE_OTHER) {
			player.oreInformation[0] = 444;
			player.oreInformation[1] = 40;
			player.oreInformation[2] = 65;
			startMining(player);
		}
	}

	/**
	 * Decrease the miningTimer variable until it reaches 0.
	 */
	private static void startMiningTimerEvent(final Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		/* The event is continious untill wcTimer reaches 0. */
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (player.miningTimer > 0 && player.oreInformation[0] > 0 && oreExists(player)) {
					player.miningTimer--;
					if (player.miningTimer == 0) {
						mineOre(player);
					}
					player.startAnimation(pickAxeAnimation(player));
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
				player.startAnimation(65535);
			}
		}, 1);

	}



}
