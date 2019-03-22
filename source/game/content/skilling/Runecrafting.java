package game.content.skilling;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.bank.Bank;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.Teleport;
import game.content.music.SoundSystem;
import game.item.ItemAssistant;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;


/**
 * Runecrafting skill.
 *
 * @author Russian & MGT Madness.
 */

public class Runecrafting {

	public static final int PURE_ESSENCE = 7936;

	public static final int RUNE_ESSENCE = 1436;

	private final static int[][] POUCH_DATA =
			{
					{5509, 1},
					{5510, 25},
					{5512, 50},
					{5514, 75}
			};

	private final static int[][] runecrafterRobesSet =
			{
					{13616, 13614, 13617, 13618},

					{13613, 13619, 13622, 13623},

					{13626, 13624, 13627, 13628}

			};

	public static void switchPetColour(Player player, Runes data) {
		boolean firstPet = player.getPetSummoned() && player.getPetId() >= 7337 && player.getPetId() <= 7350;
		boolean secondPet = player.getSecondPetSummoned() && player.getSecondPetId() >= 7337 && player.getSecondPetId() <= 7350;
		int petId = firstPet ? player.getPetId() : player.getSecondPetId();
		if (firstPet || secondPet) {
			int switchTo = getRiftGuardianType(data);

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
						player.getPA().sendMessage("Your rift guardian changes colour!");
						break;
					}
				}
			}
		}
	}

	public static int getRiftGuardianType(Runes data) {
		int switchTo = 0;
		switch (data.getId()) {
			case 556:
				switchTo = 7338;
				break;
			case 558:
				switchTo = 7339;
				break;
			case 555:
				switchTo = 7340;
				break;
			case 557:
				switchTo = 7341;
				break;
			case 554:
				switchTo = 7337;
				break;
			case 559:
				switchTo = 7342;
				break;
			case 564:
				switchTo = 7343;
				break;
			case 562:
				switchTo = 7344;
				break;
			case 9075:
				switchTo = 7349;
				break;
			case 561:
				switchTo = 7345;
				break;
			case 563:
				switchTo = 7346;
				break;
			case 560:
				switchTo = 7347;
				break;
			case 565:
				switchTo = 7350;
				break;
			case 566:
				switchTo = 7348;
				break;
		}

		return switchTo;
	}

	/**
	 * True, if the itemId matches a pouch.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity to find a match for.
	 * @return True, if the item identity matches a pouch.
	 */
	public static boolean isPouch(Player player, int itemId) {
		if (itemId >= 5509 && itemId <= 5514) {
			for (int i = 0; i < POUCH_DATA.length; i++) {
				if (itemId == POUCH_DATA[i][0] && player.baseSkillLevel[ServerConstants.RUNECRAFTING] < POUCH_DATA[i][1]) {
					player.playerAssistant.sendMessage("You need " + POUCH_DATA[i][1] + " Runecrafting to use this pouch.");
					return true;
				}
			}
			int pouch = -1;
			int a = itemId;
			if (a == 5509)
				pouch = 0;
			if (a == 5510)
				pouch = 1;
			if (a == 5512)
				pouch = 2;
			if (a == 5514)
				pouch = 3;
			Runecrafting.fillPouch(player, pouch);
			return true;
		}
		return false;
	}

	public static void emptyPouch(Player player, int i) {
		if (i < 0) {
			return;
		}
		boolean emptied = false;
		int toAdd = player.pouchesPure[i];
		if (toAdd > ItemAssistant.getFreeInventorySlots(player)) {
			toAdd = ItemAssistant.getFreeInventorySlots(player);
		}
		if (toAdd > 0) {
			ItemAssistant.addItem(player, PURE_ESSENCE, toAdd);
			player.pouchesPure[i] -= toAdd;
			emptied = true;
		}


		toAdd = player.pouchesRune[i];
		if (toAdd > ItemAssistant.getFreeInventorySlots(player)) {
			toAdd = ItemAssistant.getFreeInventorySlots(player);
		}
		if (toAdd > 0) {
			ItemAssistant.addItem(player, RUNE_ESSENCE, toAdd);
			player.pouchesRune[i] -= toAdd;
			emptied = true;
		}

		if (emptied) {
			player.getPA().sendMessage("You empty the pouch.");
		} else {
			player.getPA().sendMessage("The pouch is empty.");
		}
	}

	public final static int[] POUCH_SIZE =
			{3, 6, 9, 12};

	public static void fillPouch(Player player, int i) {
		if (i < 0) {
			return;
		}
		boolean added = false;
		int toAdd = POUCH_SIZE[i] - player.pouchesPure[i];
		if (toAdd > ItemAssistant.getItemAmount(player, PURE_ESSENCE)) {
			toAdd = ItemAssistant.getItemAmount(player, PURE_ESSENCE);
		}
		if (toAdd > POUCH_SIZE[i] - player.pouchesPure[i]) {
			toAdd = POUCH_SIZE[i] - player.pouchesPure[i];
		}
		if (toAdd > 0) {
			ItemAssistant.deleteItemFromInventory(player, PURE_ESSENCE, toAdd);
			added = true;
			player.pouchesPure[i] += toAdd;
		}

		toAdd = POUCH_SIZE[i] - player.pouchesRune[i] - player.pouchesPure[i];
		if (toAdd > ItemAssistant.getItemAmount(player, RUNE_ESSENCE)) {
			toAdd = ItemAssistant.getItemAmount(player, RUNE_ESSENCE);
		}
		if (toAdd > POUCH_SIZE[i] - player.pouchesRune[i]) {
			toAdd = POUCH_SIZE[i] - player.pouchesRune[i];
		}
		if (toAdd > 0) {
			ItemAssistant.deleteItemFromInventory(player, RUNE_ESSENCE, toAdd);
			added = true;
			player.pouchesRune[i] += toAdd;
		}

		if (added) {
			player.playerAssistant.sendFilterableMessage("You fill up the pouch.");
		} else {
			player.playerAssistant.sendMessage("The pouch is already full.");
		}
	}

	public static enum Runes {
		AIR(556, 1, 5, 1438, 5527, 2841, 4829, 14897, new int[]
				                                              {11, 22, 33, 44, 55, 66, 77, 88, 99}),
		MIND(558, 5, 6, 1448, 5529, 2793, 4828, 14898, new int[]
				                                               {14, 28, 42, 56, 70, 84, 98}),
		WATER(555, 9, 7, 1444, 5531, 2726, 4832, 14899, new int[]
				                                                {19, 38, 57, 76, 95}),
		EARTH(557, 14, 7, 1440, 5535, 2655, 4830, 14900, new int[]
				                                                 {26, 52, 78}),
		FIRE(554, 14, 7, 1442, 5537, 2574, 4849, 14901, new int[]
				                                                {35, 70}),
		BODY(559, 20, 8, 1446, 5533, 2521, 4834, 14902, new int[]
				                                                {46, 92}),
		COSMIC(564, 27, 8, 1454, 5539, 2122, 4833, 14903, new int[]
				                                                  {59}),
		CHAOS(562, 35, 9, 1452, 5543, 2281, 4837, 14906, new int[]
				                                                 {59}),
		ASTRAL(9075, 40, 10, 0, 9106, 2149, 3864, 14911, new int[]
				                                                 {82}),
		NATURE(561, 44, 11, 1462, 5541, 2400, 4835, 14905, new int[]
				                                                   {91}),
		LAW(563, 54, 13, 1458, 5545, 2464, 4818, 14904, new int[] {}),
		DEATH(560, 65, 15, 1456, 5547, 2208, 4830, 14907, new int[] {}),
		BLOOD(565, 77, 17, 16006, 5549, 1722, 3827, 27978, new int[] {}),
		SOUL(566, 90, 19, 16005, 5551, 1818, 3855, 27980, new int[] {});

		private int id;

		private int level;

		private int xp;

		private int talisman;

		private int tiara;

		private int teleportX;

		private int teleportY;

		private int objectId;

		/**
		 * Level, runes to produce.
		 */
		private int[] multipleRunes;

		private Runes(int id, int level, int xp, int talisman, int tiara, int teleportX, int teleportY, int objectId, int[] multipleRunes) {
			this.id = id;
			this.level = level;
			this.xp = xp;
			this.talisman = talisman;
			this.tiara = tiara;
			this.teleportX = teleportX;
			this.teleportY = teleportY;
			this.objectId = objectId;
			this.multipleRunes = multipleRunes;
		}

		public int getId() {
			return id;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

		public int getTalisman() {
			return talisman;
		}

		public int getTiara() {
			return tiara;
		}

		public int getTeleportX() {
			return teleportX;
		}

		public int getTeleportY() {
			return teleportY;
		}

		public int getObjectId() {
			return objectId;
		}

		public int[] getMultipleRunes() {
			return multipleRunes;
		}

	}

	public static boolean hasRequiredLevels(final Player player, int skillId, int lvlReq) {
		if (player.baseSkillLevel[skillId] < lvlReq) {
			player.getDH().sendStatement("You need at least " + lvlReq + " Runecrafting to craft this.");
			return false;
		}
		return true;
	}

	public static boolean canTeleportToAltar(Player player, Runes rune) {
		player.getPA().closeInterfaces(true);
		if (ItemAssistant.hasItemInInventory(player, rune.getTalisman()) || ItemAssistant.hasItemEquipped(player, rune.getTiara())) {
			Teleport.spellTeleport(player, rune.getTeleportX(), rune.getTeleportY(), 0, false);
			return true;
		}
		player.playerAssistant.sendMessage("You must have a " + ItemAssistant.getItemName(rune.getTalisman()) + " or " + ItemAssistant.getItemName(rune.getTiara()) + " to enter.");
		return false;
	}

	public static void craftRunes(Player player, Runes rune) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		int essence = rune.getLevel() >= 27 ? PURE_ESSENCE : RUNE_ESSENCE;
		if (essence == RUNE_ESSENCE && ItemAssistant.hasItemInInventory(player, PURE_ESSENCE)) {
			essence = PURE_ESSENCE;
		}
		int runeEssenceAmount = ItemAssistant.getItemAmount(player, essence);
		int originalAmount = runeEssenceAmount;
		if (runeEssenceAmount < 1) {
			player.playerAssistant.sendMessage("You have run out of " + ItemAssistant.getItemName(essence) + "!");
			return;
		}
		if (!hasRequiredLevels(player, ServerConstants.RUNECRAFTING, rune.getLevel())) {
			return;
		}

		if (Skilling.hasMasterCapeWorn(player, 9765) && Misc.hasPercentageChance(10)) {
			player.getPA().sendMessage("<col=a54704>Your cape allows you to save some essence from being used.");
		} else {
			for (int p = 0; p < 28; p++) {
				ItemAssistant.deleteItemFromInventory(player, essence, p, player.playerItems[p] - 1);
			}
		}
		if (rune.getMultipleRunes().length > 0) {
			for (int index = rune.getMultipleRunes().length - 1; index >= 0; index--) {
				if (player.baseSkillLevel[ServerConstants.RUNECRAFTING] >= rune.getMultipleRunes()[index]) {
					runeEssenceAmount *= index + 2;
					break;
				}
			}
		}

		player.skillingStatistics[SkillingStatistics.RUNE_ESSENCE_CRAFTED] += originalAmount;

		int chance = 0;

		if (Misc.hasPercentageChance(chance)) {
			runeEssenceAmount *= 2;
		}

		for (int index = 0; index < runecrafterRobesSet.length; index++) {
			if (player.playerEquipment[ServerConstants.HEAD_SLOT] == runecrafterRobesSet[index][0]) {
				if (player.playerEquipment[ServerConstants.BODY_SLOT] == runecrafterRobesSet[index][1]
				    && player.playerEquipment[ServerConstants.LEG_SLOT] == runecrafterRobesSet[index][2]
				    && player.playerEquipment[ServerConstants.HAND_SLOT] == runecrafterRobesSet[index][3]) {
					runeEssenceAmount *= 1.1;
				}
				break;
			}
		}
		ItemAssistant.addItem(player, rune.getId(), runeEssenceAmount);
		Skilling.addHarvestedResource(player, rune.getId(), runeEssenceAmount);
		player.runeEssenceCrafted += originalAmount;
		if (rune.id == Runes.LAW.id) {
			Achievements.checkCompletionSingle(player, 1028);
		} else if (rune.id == Runes.DEATH.id) {
			player.deathRunesCrafted += runeEssenceAmount;
			Achievements.checkCompletionMultiple(player, "1050");
		}
		player.timeCraftedRunes = System.currentTimeMillis();
		int experience = (int) ((rune.getXp() * originalAmount) * 1.20);
		switchPetColour(player, rune);
		Skilling.addSkillExperience(player, experience, ServerConstants.RUNECRAFTING, false);
		int maxExperience = (int) ((19 * 27) * 1.20);
		Skilling.petChance(player, experience, maxExperience, 480, ServerConstants.RUNECRAFTING, rune);
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.startAnimation(791);
		SoundSystem.sendSound(player, 481, 0);
		player.gfx100(186);
	}

	public static boolean runecraftAltar(Player player, int objectId) {
		if (player.getTransformed() > 0) {
			return true;
		}
		if (objectId == 14897 && player.getObjectX() == 2857 && player.getObjectY() == 3380) {

			for (Runecrafting.Runes data : Runecrafting.Runes.values()) {
				if (ItemAssistant.hasItemInInventory(player, data.getTalisman()) || ItemAssistant.hasItemEquippedSlot(player, data.getTiara(), ServerConstants.HEAD_SLOT)) {

					if (player.runeEssenceCrafted >= 25) {
						player.runeEssenceCrafted -= 25;
					} else if (player.runeEssenceCrafted >= 50) {
						player.runeEssenceCrafted -= 50;
					}
					Teleport.spellTeleport(player, data.getTeleportX(), data.getTeleportY(), 0, false);
					return true;
				}
			}
			player.getPA().sendMessage("You need to have a Talisman or wear a Tiara in order to teleport.");
			return true;
		}
		for (Runes data : Runes.values()) {
			if (objectId == data.getObjectId()) {
				craftRunes(player, data);
				return true;
			}
		}
		return false;
	}

	public static boolean isRunecraftingDonatorAbuseFlagged(Player player, boolean bank) {
		if (System.currentTimeMillis() - player.timeCraftedRunes <= 15000) {
			if (!bank) {
				player.getPA().sendMessage("You cannot do this too fast.");
				return true;
			}
			if (player.runecraftingBankEventRunning) {
				player.getPA().sendMessage("You cannot open up your bank so fast.");
				return true;
			}
			player.getPA().sendMessage("You have recently runecrafted, your bank will open shortly.");
			player.runecraftingBankEventRunning = true;
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (!player.runecraftingBankEventRunning) {
						container.stop();
						return;
					}
					if (System.currentTimeMillis() - player.timeCraftedRunes > 15000) {
						player.runecraftingBankEventRunning = false;
						player.getPA().stopAllActions();
						Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {
				}
			}, 1);
			return true;
		}
		return false;
	}
}
