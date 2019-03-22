package game.content.skilling;


import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.combat.Combat;
import game.content.consumable.RegenerateSkill;
import game.content.donator.AfkChair;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.XpBonus;
import game.content.profile.ProfileRank;
import game.content.skilling.Runecrafting.Runes;
import game.content.skilling.fishing.Fishing;
import game.content.starter.GameMode;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.npc.pet.BossPetDrops;
import game.npc.pet.PetData;
import game.player.Area;
import game.player.Player;
import java.util.ArrayList;
import utility.Misc;

/**
 * Skilling related.
 *
 * @author MGT Madness.
 */
public class Skilling {

	public static void decreaseCombatSkill(Player player, int skill, int amount) {
		player.currentCombatSkillLevel[skill] -= amount;
		if (player.currentCombatSkillLevel[skill] < 1) {
			player.currentCombatSkillLevel[skill] = 1;
		}
		Skilling.updateSkillTabFrontTextMain(player, skill);
		RegenerateSkill.storeBoostedTime(player, skill);
	}

	public static boolean decreaseCombatSkill(Player player, int skill, int amount, int min) {
		int originalLevel = player.currentCombatSkillLevel[skill];
		player.currentCombatSkillLevel[skill] -= amount;
		if (player.currentCombatSkillLevel[skill] < 1) {
			player.currentCombatSkillLevel[skill] = 1;
		}
		if (player.currentCombatSkillLevel[skill] < min) {
			player.currentCombatSkillLevel[skill] = min;
		}
		if (player.currentCombatSkillLevel[skill] != originalLevel) {
			Skilling.updateSkillTabFrontTextMain(player, skill);
			RegenerateSkill.storeBoostedTime(player, skill);
			return true;
		}
		return false;
	}

	public static void increaseCombatSkill(Player player, int skill, int amount, int boost) {
		player.currentCombatSkillLevel[skill] += amount;
		int max = Skilling.getLevelForExperience(player.skillExperience[skill]);
		if (player.currentCombatSkillLevel[skill] > max + boost) {
			player.currentCombatSkillLevel[skill] = max + boost;
		}
		Skilling.updateSkillTabFrontTextMain(player, skill);
		RegenerateSkill.storeBoostedTime(player, skill);
	}
	public static void bannedMessage(Player player, long parseLong) {
		int minutes = (int) ((43200000 - (System.currentTimeMillis() - parseLong)) / 60000);
		String string = "minutes";
		if (minutes > 59) {
			minutes /= 60;
			string = "hours";
			if (minutes <= 1) {
				string = "hour";
			}
		}
		player.getPA().sendMessage("You are banned from skilling for another " + minutes + " " + string + ".");
	}

	/**
	 * @param player
	 * @param currentExperience The current experience gained per resource
	 * @param maximumExperience The maximum experience gained possible for this resource
	 * @param chanceAtMaximumExperience Lowest chance can get the pet at.
	 * <p>
	 * So if the *currentExperience* is the same as *maximumExperience*, then the chance is *chanceAtMaximumExperience*.
	 * If the currentExperience is 1, then the chance is *chanceAtMaximumExperience* times 3.5.
	 */
	public static void petChance(Player player, int currentExperience, int maximumExperience, int chanceAtMaximumExperience, int skillIndex, Runes data) {
		chanceAtMaximumExperience *= 1.5;
		if (currentExperience > maximumExperience) {
			currentExperience = maximumExperience;
		}
		double highestChance = ((double) chanceAtMaximumExperience * 5.0);
		double chanceDifference = highestChance - (double) chanceAtMaximumExperience;
		double value = 1.0 - ((double) currentExperience / (double) maximumExperience);
		double chanceAddition = chanceDifference * value;
		double petChance = ((double) chanceAtMaximumExperience + chanceAddition);

		if (Misc.hasOneOutOf(GameMode.getDropRate(player, petChance))) {
			int petInventoryId = 0;
			int originalPetInventory = 0;
			if (skillIndex == ServerConstants.WOODCUTTING) {
				petInventoryId = 13322;
			} else if (skillIndex == ServerConstants.FISHING) {
				petInventoryId = 13320;
			} else if (skillIndex == ServerConstants.MINING) {
				petInventoryId = Mining.getRockGolemPetType(player.getObjectId());
				originalPetInventory = 13321;
			} else if (skillIndex == ServerConstants.AGILITY) {
				petInventoryId = 20659;
			} else if (skillIndex == ServerConstants.FARMING) {
				petInventoryId = 20661;
			} else if (skillIndex == ServerConstants.HERBLORE) {
				petInventoryId = 21509;
			} else if (skillIndex == ServerConstants.FIREMAKING) {
				petInventoryId = 20693;
			} else if (skillIndex == ServerConstants.RUNECRAFTING) {
				for (int index = 0; index < PetData.petData.length; index++) {
					if (Runecrafting.getRiftGuardianType(data) == PetData.petData[index][0]) {
						petInventoryId = PetData.petData[index][1];
						originalPetInventory = 20665;
						break;
					}
				}
			} else if (skillIndex == ServerConstants.THIEVING) {
				petInventoryId = 20663;
			}
			if (originalPetInventory == 0) {
				originalPetInventory = petInventoryId;
			}
			BossPetDrops.awardBoss(player, petInventoryId, originalPetInventory, 0, ServerConstants.SKILL_NAME[skillIndex]);
		}
	}


	public static String sellHarvestedResource(Player player, int itemId, int amount) {
		itemId = ItemAssistant.getUnNotedItem(itemId);
		String edited = "false";
		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		for (int index = 0; index < player.resourcesHarvested.size(); index++) {
			String currentString = player.resourcesHarvested.get(index);
			String parse[] = currentString.split(" ");
			if (parse[0].equals("" + itemId)) {
				int originalAmount = Integer.parseInt(parse[1]);
				if (originalAmount < amount) {
					amount = originalAmount;
					originalAmount = 0;
				} else {
					originalAmount -= amount;
				}
				edited = "true";
				player.resourcesHarvested.remove(index);
				if (originalAmount > 0) {
					player.resourcesHarvested.add(itemId + " " + originalAmount);
				}
				break;
			}
		}
		return amount + " " + edited;
	}

	public static void addHarvestedResource(Player player, int itemId, int amount) {
		player.resourcesHarvested.add(itemId + " " + amount);
		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		ArrayList<String> finalIncomeList = new ArrayList<String>();
		for (int index = 0; index < player.resourcesHarvested.size(); index++) {
			String currentString = player.resourcesHarvested.get(index);
			int lastIndex = currentString.lastIndexOf(" ");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;
			for (int i = 0; i < finalIncomeList.size(); i++) {
				int lastIndex1 = finalIncomeList.get(i).lastIndexOf(" ");
				String matchToFind1 = finalIncomeList.get(i).substring(0, lastIndex1);
				if (matchToFind1.equals(matchToFind)) {
					int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
					int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).substring(lastIndex + 1));
					int finalValueAdded = (finalNumberValue + numberValue);
					finalIncomeList.remove(i);
					finalIncomeList.add(i, matchToFind + " " + finalValueAdded);
					finalIncomeListHas = true;
				}
			}

			if (!finalIncomeListHas) {
				finalIncomeList.add(currentString);
			}
		}
		player.resourcesHarvested = finalIncomeList;
	}

	public static void addTokens(Player player, int amount, int skill) {
		ItemAssistant.addItemToInventoryOrDrop(player, 20527, amount);//TODO - ADD PRE_EOC SKILLING TOKENS
		player.getPA().sendMessage("<col=890caf>You receive " + amount + " skilling tokens for your efforts while training " + ServerConstants.SKILL_NAME[skill] + ".");
	}

	/**
	 * True if the player has the given itemId worn in the cape slot or has Max cape worn.
	 * Use the untrimmed itemId version, because this will check for the trimmed version too which is +1
	 */
	public static boolean hasMasterCapeWorn(Player player, int itemId) {

		// Untrimmed version.
		if (player.playerEquipment[ServerConstants.CAPE_SLOT] == itemId) {
			return true;
		}

		// Trimmed version.
		if (player.playerEquipment[ServerConstants.CAPE_SLOT] == itemId + 1) {
			return true;
		}
		if (hasMaxCapeWorn(player)) {
			return true;
		}

		return false;
	}

	/*
	 * True if the player has a Max cape worn. Used primarily for the skill cape emote button
	 */
	public static boolean hasMaxCapeWorn(Player player) {
		int[] maxCapes =
		{
			13280, // Max cape.
			13329, // Fire max cape.
			13331, // Saradomin max cape.
			13333, // Zamorak max cape.
			13335, // Guthix max cape.
			13337, // Ava's max cape.
			20760, // Ardougne max cape.
			21776, // Imbued sara max cape.
			21780, // Imbued zammy max cape.
			21784, // Imbued guthix max cape.
			21285, // Infernal max cape.
			21898, // Assembler max cape
			14011, // Completionist cape

		};

		for (int index = 0; index < maxCapes.length; index++) {
			if (player.playerEquipment[ServerConstants.CAPE_SLOT] == maxCapes[index]) {
				return true;
			}
		}

		return false;
	}

	public enum SkillCapes {
		ATTACK(9747, 9748, 4959, 823, 5),
		STRENGTH(9750, 9751, 4981, 828, 16),
		DEFENCE(9753, 9754, 4961, 824, 9),
		RANGING(9756, 9757, 4973, 832, 8),
		PRAYER(9759, 9760, 4979, 829, 10),
		MAGIC(9762, 9763, 4939, 813, 5),
		RUNECRAFTING(9765, 9766, 4947, 817, 10),
		HITPOINTS(9768, 9769, 4971, 833, 6),
		AGILITY(9771, 9772, 4977, 830, 7),
		HERBLORE(9774, 9775, 4969, 835, 14),
		THIEVING(9777, 9778, 4965, 826, 5),
		CRAFTING(9780, 9781, 4949, 818, 13),
		FLETCHING(9783, 9784, 4937, 812, 13),
		SLAYER(9786, 9787, 4967, 827, 4),
		CONSTRUCTION(9789, 9790, 4953, 820, 12),
		MINING(9792, 9793, 4941, 814, 7),
		SMITHING(9795, 9796, 4943, 815, 19),
		FISHING(9798, 9799, 4951, 819, 13),
		COOKING(9801, 9802, 4955, 821, 24),
		FIREMAKING(9804, 9805, 4975, 831, 7),
		WOODCUTTING(9807, 9808, 4957, 822, 20),
		FARMING(9810, 9811, 4963, 825, 12),
		QUEST(9813, 13068, 4945, 816, 14),
		HUNTER(9948, 9949, 5158, 907, 11);

		private int untrimmed, trimmed, animation, graphic, duration;

		private SkillCapes(int untrimmed, int trimmed, int animation, int graphic, int duration) {
			this.untrimmed = untrimmed;
			this.trimmed = trimmed;
			this.animation = animation;
			this.graphic = graphic;
			this.duration = duration;
		}

		public int getUntrimmedId() {
			return untrimmed;
		}

		public int getTrimmedId() {
			return trimmed;
		}

		public int getAnimation() {
			return animation;
		}

		public int getGraphic() {
			return graphic;
		}

		public int getDuration() {
			return duration;
		}
	}

	public static boolean hasPyromancerPiece(Player player) {
		if (!GameType.isOsrs()) {
			return false;
		}
		return false;
	}

	public static boolean hasLumberjackPiece(Player player) {
		if (!GameType.isOsrs()) {
			return false;
		}
		return false;
	}

	public static boolean hasAnglerPiece(Player player) {
		if (!GameType.isOsrs()) {
			return false;
		}
		return false;
	}

	public static boolean hasProspectorPiece(Player player) {
		if (!GameType.isOsrs()) {
			return false;
		}
		return false;
	}

	public static void sendXpDropAmount(Player player) {
		if (player.xpDropAmount > 0) {
			player.playerAssistant.sendMessage(":xpdisplay " + player.xpDropAmount + " " + player.xpDropSkills);
		}
		player.xpDropAmount = 0;
		player.xpDropSkills = "";
	}

	public static boolean cannotActivateNewSkillingEvent(Player player) {
		// Why do we need
		if (player.forceStopSkillingEvent && player.isUsingSkillingEvent) {
			player.forceStopSkillingEvent = false;
			return true;
		}
		if (player.isUsingSkillingEvent) {
			return true;
		}
		player.forceStopSkillingEvent = false;
		player.isUsingSkillingEvent = true;
		return false;
	}

	public static void setLevel(Player player, int skill, int level) {
		player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
		player.baseSkillLevel[skill] = level;
		if (skill <= 6) {
			player.currentCombatSkillLevel[skill] = level;
		}
		player.getPA().setSkillLevel(skill, player.baseSkillLevel[skill], player.skillExperience[skill]);
		Combat.resetPrayers(player);
		player.setHitPoints(player.getBaseHitPointsLevel());
		player.playerAssistant.calculateCombatLevel();
		InterfaceAssistant.updateCombatLevel(player);
		Skilling.updateTotalLevel(player);
		Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
		Skilling.updateSkillTabFrontTextMain(player, skill);
		player.setVengeance(false);
	}

	public static void endSkillingEvent(Player player) {
		player.isUsingSkillingEvent = false;
	}

	public static boolean forceStopSkillingEvent(Player player) {
		return player.forceStopSkillingEvent;
	}

	/**
	 * Set the client xp bar total to my current total experience.
	 */
	public static void sendXpToDisplay(Player player) {
		int amount = 0;
		if (player.xpBarShowType.equals("TOTAL")) {
			amount = (int) player.getXpTotal();
			player.playerAssistant.sendMessage(":xpshowtotal");
		} else if (player.xpBarShowType.equals("SESSION")) {
			amount = player.currentSessionExperience;
			player.playerAssistant.sendMessage(":xpshowsession");
		} else if (player.xpBarShowType.equals("COMBAT")) {
			for (int index = 0; index < 7; index++) {
				amount += player.combatExperienceGainedAfterMaxed[index];
			}
			player.playerAssistant.sendMessage(":xpshowcombat");
		}
		player.playerAssistant.sendMessage(":xptotal" + amount);
	}

	/**
	 * @param player The associated player.
	 * @return Get the total level, ignoring the decreased combat stats from being editted.
	 */
	public static int getOriginalTotalLevel(Player player) {
		int total = Skilling.getTotalLevel(player, true);
		if (GameType.isOsrsPvp()) {
			if (player.getAbleToEditCombat()) {
				total += 693;
			} else {
				total += player.getBaseAttackLevel();
				total += player.getBaseStrengthLevel();
				total += player.getBaseDefenceLevel();
				total += player.getBaseHitPointsLevel();
				total += player.getBaseRangedLevel();
				total += player.getBasePrayerLevel();
				total += player.getBaseMagicLevel();
			}
		}
		return total;
	}

	private static void updateSkillTabFrontTextMethod(Player player, int skill, int first, int second) {
		if (skill <= 6) {
			player.getPA().sendFrame126("" + player.getCurrentCombatSkillLevel(skill) + "", first);
		} else {
			player.getPA().sendFrame126("" + player.baseSkillLevel[skill], first);
		}

		player.getPA().sendFrame126("" + player.baseSkillLevel[skill] + "", second);
	}

	public static void resetCombatSkills(Player player) {
		for (int i = 0; i <= 6; i++) {
			player.currentCombatSkillLevel[i] = player.baseSkillLevel[i];
			Skilling.updateSkillTabFrontTextMain(player, i);
		}
	}

	public static void updateSkillTabFrontTextMain(Player player, int skill) {
		if (skill == 3) {
			player.getPA().sendFrame126("" + player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS), 19001); // Hitpoints orb.
		}
		if (skill == 5) {
			player.getPA().sendFrame126("" + player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) + " / " + player.baseSkillLevel[ServerConstants.PRAYER] + "",
			                            687); // Prayer spellbook text.
		}
		updateSkillTabExperienceHover(player, skill, false);
		updateSkillTabFrontTextMethod(player, skill, skillTabLevelNumberInterfaceIds[skill][0], skillTabLevelNumberInterfaceIds[skill][1]);
	}

	public static int getExperienceForLevel(int level) {
		if (level < 1) {
			level = 1;
		}
		return experienceArray[--level > 98 ? 98 : level];
	}

	public static int getExprienceForLevelDifferent(int level) {
		if (level > 99) {
			return 0;
		}
		return experienceArray[--level > 98 ? 98 : level];
	}

	public static int getLevelForExperience(int exp) {
		for (int j = 98; j != -1; j--) {
			if (experienceArray[j] <= exp) {
				return j + 1;
			}
		}
		return 0;
	}

	public static void updateTotalSkillExperience(Player player, long amount) {
		player.setXpTotal(amount);
		player.getPA().sendFrame126("Total Xp: " + (player.getXpTotal() / 1000000) + " million", 24363);
	}

	private static boolean isCombatSkill(int skillIndex) {
		if (skillIndex <= ServerConstants.MAGIC) {
			return true;
		}
		return false;
	}

	private static boolean isSkillingSkill(int skillIndex) {
		if (skillIndex > ServerConstants.MAGIC) {
			return true;
		}
		return false;
	}

	/**
	 * Get the base experience multiplier.
	 */
	private static int getBaseExperience(Player player, int skill) {
		if (GameType.isOsrsPvp()) {
			return 25;
		}

		if (isCombatSkill(skill)) {
			return GameMode.getDifficulty(player, "GLADIATOR") ? 80 : 500;
		}
		return GameMode.getDifficulty(player, "GLADIATOR") ? 10 : 60;
	}

	/**
	 * @param usingXpLamp It becomes true for Xp lamp only. This will skip the multiplier bonuses i've added.
	 */
	public static void addSkillExperience(Player player, int experience, int skill, boolean usingXpLamp) {
		int orginalExperience = experience;
		if (!usingXpLamp) {
			if (isSkillingSkill(skill)) {
				GameTimeSpent.increaseGameTime(player, GameTimeSpent.SKILLING);
			}
			experience *= getBaseExperience(player, skill);
			if (Area.inResourceWildernessOsrs(player)) {
				experience *= ResourceWilderness.EXPERIENCE_MULTIPLIER;
				ResourceWilderness.skilledInside(player);
			}
			if (skill == ServerConstants.AGILITY) {
				experience *= 1.6;
			} else if (skill == ServerConstants.MINING || skill == ServerConstants.SMITHING) {
				experience *= 1.2;
			} else if (skill == ServerConstants.FARMING) {
				experience *= 1.6;
			}
			if (Area.inDonatorZone(player.getX(), player.getY())) {
				if (player.isSupremeDonator()) {
					experience *= 1.35;
				} else if (player.isImmortalDonator()) {
					experience *= 1.30;
				} else if (player.isUberDonator()) {
					experience *= 1.25;
				} else if (player.isUltimateDonator()) {
					experience *= 1.20;
				} else if (player.isLegendaryDonator()) {
					experience *= 1.15;
				} else if (player.isExtremeDonator()) {
					experience *= 1.10;
				} else if (player.isSuperDonator()) {
					experience *= 1.05;
				}
			}
			if (!isCombatSkill(skill)) {
				if (skill != ServerConstants.SLAYER) {
					player.timeSkilled = System.currentTimeMillis();
				}
				if (skill != ServerConstants.SLAYER) {
					RandomEvent.randomEvent(player, skill == ServerConstants.RUNECRAFTING ? 60 : 300);
				}
			} else if (skill == ServerConstants.PRAYER) {
				if (!Combat.inCombat(player)) {
					RandomEvent.randomEvent(player, 300);
				}
			}
			if (GameType.isOsrsPvp()) {
				if (isCombatSkill(skill)) {
					experience = orginalExperience;
				}
			}
			if (WorldEvent.getCurrentEvent().equals(ServerConstants.SKILL_NAME[skill] + " skill")) {
				experience *= WorldEvent.SKILLING_EVENT_EXPERIENCE_MULTIPLIER;
			}
			experience = XpBonus.getNewExperienceIfOnXpBonus(player, experience);
		}
		player.getPA().sendMessage(":xpdisplaybar" + experience);
		if (experience + player.skillExperience[skill] < 0) {
			return;
		}
		player.xpDropAmount += experience;
		player.xpDropSkills = player.xpDropSkills + (player.xpDropSkills.isEmpty() ? "" : " ") + skill;
		if (player.xpLock) {
			return;
		}
		if (player.usingMaxHitDummy) {
			player.usingMaxHitDummy = false;
			return;
		}
		int maximumExperienceCap = 200000000;
		player.currentSessionExperience += experience;
		int oldLevel = player.baseSkillLevel[skill];
		updateTotalSkillExperience(player, player.getXpTotal() + experience);

		// Only add experience to skill level if not a combat skill or is a combat skill and the combat skill level is 99.
		// So 1 defence pures do not mess up if they train defence by accident.
		if (isCombatSkill(skill) && player.baseSkillLevel[skill] < 99 && !player.getAbleToEditCombat() || isCombatSkill(skill) && player.baseSkillLevel[skill] == 99
		    || isSkillingSkill(skill)) {
			player.skillExperience[skill] += experience;
		}
		if (isCombatSkill(skill) && player.getAbleToEditCombat()) {
			player.combatExperienceGainedAfterMaxed[skill] += experience;
		}
		if (player.skillExperience[skill] > maximumExperienceCap) {
			player.skillExperience[skill] = maximumExperienceCap;
		}
		if (player.baseSkillLevel[skill] < 99) {
			int newLevel = getLevelForExperience(player.skillExperience[skill]); // No need to go through this big looping method.
			if (newLevel == 250 || newLevel == 500 || newLevel == 750 || newLevel == 1000 || newLevel == 1250 || newLevel == 1500 || newLevel == 1750 || newLevel == 2000
			    || newLevel == 2025 || newLevel == 2050 || newLevel == 2078 || newLevel == 2079) {
				player.getPA().sendScreenshot(player.getTotalLevel() + " total level", 2);
			}
			if (newLevel > oldLevel) {
				if (skill <= 6) {
					if (player.getCurrentCombatSkillLevel(skill) < newLevel && skill != 3 && skill != 5) {
						player.currentCombatSkillLevel[skill] = newLevel;
					}
				}
				player.baseSkillLevel[skill] = newLevel;
				levelUp(player, skill);
				player.getPA().requestUpdates();
			}
		}
		player.getPA().setSkillLevel(skill, player.baseSkillLevel[skill], player.skillExperience[skill]);
		updateSkillTabFrontTextMain(player, skill);
		announceMaxExperienceInASkill(player, skill);
	}

	public static int getTotalLevel(Player player, boolean excludeCombat) {
		int total = 0;
		if (GameType.isOsrsPvp()) {
			for (int i = excludeCombat ? 7 : 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
				total += player.baseSkillLevel[i];
			}
		}
		if (GameType.isOsrsEco()) {
			for (int i = 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
				total += player.baseSkillLevel[i];
			}
		}
		return total;
	}

	public static long getExperienceTotal(Player player) {
		boolean reAdd = false;
		long xp = 0;
		if (player.getAbleToEditCombat()) {
			for (int i = 0; i <= 6; i++) {
				xp += (13034431 + player.combatExperienceGainedAfterMaxed[i]);
			}
			reAdd = true;
		}
		for (int i = reAdd ? 7 : 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
			xp += player.skillExperience[i];
		}
		return xp;
	}

	public static String getDarkBlue() {
		return "@dbl@";
	}

	public static void levelUpMessage(Player player, int skill, int first, int second, int third) {
		player.getPA().sendFrame126(Skilling.getDarkBlue() + "Congratulations, you just advanced " + Misc.getAorAn(ServerConstants.SKILL_NAME[skill]) + " level!", first);
		player.getPA().sendFrame126("Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".", second);
		player.getPA().sendFrame164(third);
	}

	public static void levelUp(Player player, int skill) {
		Skilling.updateTotalLevel(player);
		if (skill == ServerConstants.FARMING) {
			player.getDH().sendItemChat("", Skilling.getDarkBlue() + "Congratulations, you just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "",
			                            "Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".", "", 5340, 250, 30, 0);
		} else if (skill == ServerConstants.RUNECRAFTING) {
			player.getDH().sendItemChat("", Skilling.getDarkBlue() + "Congratulations, you just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "",
			                            "Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".", "", 556, 250, 0, -5);
		} else if (skill == ServerConstants.COOKING) {
			player.getDH().sendItemChat("", Skilling.getDarkBlue() + "Congratulations, you just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "",
			                            "Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".", "", 977, 220, 45, 0);
		} else if (skill == ServerConstants.HUNTER) {
			//InterfaceAssistant.changeInterfaceModel(player, 4266, 19980, 750, 0, 512, 0, -15);
			player.playerAssistant.sendFilterableMessage("Congratulations! Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".");
			player.getDH().sendItemChat("", Skilling.getDarkBlue() + "Congratulations, you just advanced a " + ServerConstants.SKILL_NAME[skill] + " level!", "",
			                            "Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".", "", 9951, 200, -15, -17);
		} else {
			levelUpMessage(player, skill, levelUpIds2007[skill][0], levelUpIds2007[skill][1], levelUpIds2007[skill][2]);
		}
		player.playerAssistant.sendFilterableMessage("Congratulations! Your " + ServerConstants.SKILL_NAME[skill] + " level is now " + player.baseSkillLevel[skill] + ".");
		player.setDialogueAction(0);
		player.nextDialogue = 0;
		player.playerAssistant.calculateCombatLevel();
		InterfaceAssistant.updateCombatLevel(player);
		GameMode.announceMaxedCombatOrMaxedTotal(player);
		announce99Achieved(player, skill);
		ProfileRank.rankPopUp(player, "ADVENTURER");
		if (ServerConfiguration.DEBUG_MODE) {
			for (tokenData data : tokenData.values()) {
				if (player.baseSkillLevel[skill] == data.getLevel() && Skilling.isSkillingSkill(skill)) {
					Skilling.addTokens(player, data.getAmount(), skill);
				}
			}
		}
		if (player.baseSkillLevel[skill] < 99) {
			player.gfx100(199); //levels 1-98
		} else {
			player.gfx100(1388); //Gfx for 99
		}

	}

	private static enum tokenData {
		A(10, 1),
		B(20, 2),
		C(30, 4),
		D(40, 8),
		E(50, 16),
		F(60, 32),
		G(70, 64),
		H(80, 128),
		I(90, 256),
		J(99, 512);

		private int level, amount;

		tokenData(int level, int amount) {
			this.level = level;
			this.amount = amount;
		}

		public int getLevel() {
			return level;
		}

		public int getAmount() {
			return amount;
		}
	}


	/**
	 * First index is currentLevel, second index is baseLevel. So 118/99 for example.
	 */
	private final static int[][] skillTabLevelNumberInterfaceIds =
			{
					{4004, 4005},
					{4008, 4009},
					{4006, 4007},
					{4016, 4017},
					{4010, 4011},
					{4012, 4013},
					{4014, 4015},
					{4034, 4035},
					{4038, 4039},
					{4026, 4027},
					{4032, 4033},
					{4036, 4037},
					{4024, 4025},
					{4030, 4031},
					{4028, 4029},
					{4020, 4021},
					{4018, 4019},
					{4022, 4023},
					{12166, 12167},
					{13926, 13927},
					{4152, 4153},
					{26380, 26381},

					{26380, 26381}, // TODO: Summoning
			};

	public final static int[][] levelUpIds2007 =
			{
					{6248, 6249, 6247},
					{6254, 6255, 6253},
					{6207, 6208, 6206},
					{6217, 6218, 6216},
					{5453, 6114, 4443},
					{6243, 6244, 6242},
					{6212, 6213, 6211},
					{6227, 6228, 6226},
					{4273, 4274, 4272},
					{6232, 6233, 6231},
					{6259, 6260, 6258},
					{4283, 4284, 4282},
					{6264, 6265, 6263},
					{6222, 6223, 6221},
					{4417, 4438, 4416},
					{6238, 6239, 6237},
					{4278, 4279, 4277}, // Agility is missing
					{4263, 4264, 4261},
					{12123, 12124, 12122},
					{12123, 12124, 5267},
					{4268, 4269, 4267},
					{4268, 4269, 8267}, // Hunter
			};

	private static int[] skillTabHoverFrames =
			{
					//@formatter:off
					4040,
					4052,
					4046,
					4076,
					4058,
					4064,
					4070,
					4130,
					4142,
					4106,
					4124,
					4136,
					4100,
					4118,
					4112,
					4088,
					4082,
					4094,
					2832,
					13917,
					4160,
					26375,
					26375, // TODO: Summoning
					//@formatter:on
			};

	private static int experienceArray[] =
			{
					0,
					83,
					174,
					276,
					388,
					512,
					650,
					801,
					969,
					1154,
					1358,
					1584,
					1833,
					2107,
					2411,
					2746,
					3115,
					3523,
					3973,
					4470,
					5018,
					5624,
					6291,
					7028,
					7842,
					8740,
					9730,
					10824,
					12031,
					13363,
					14833,
					16456,
					18247,
					20224,
					22406,
					24815,
					27473,
					30408,
					33648,
					37224,
					41171,
					45529,
					50339,
					55649,
					61512,
					67983,
					75127,
					83014,
					91721,
					101333,
					111945,
					123660,
					136594,
					150872,
					166636,
					184040,
					203254,
					224466,
					247886,
					273742,
					302288,
					333804,
					368599,
					407015,
					449428,
					496254,
					547953,
					605032,
					668051,
					737627,
					814445,
					899257,
					992895,
					1096278,
					1210421,
					1336443,
					1475581,
					1629200,
					1798808,
					1986068,
					2192818,
					2421087,
					2673114,
					2951373,
					3258594,
					3597792,
					3972294,
					4385776,
					4842295,
					5346332,
					5902831,
					6517253,
					7195629,
					7944614,
					8771558,
					9684577,
					10692629,
					11805606,
					13034431,
			};


	public static enum SkillCapeMasterData {
		ATTACK("Ajjat", "Warriors' Guild", 9747, 4288),
		DEFENCE("Melee combat tutor", "Lumbridge", 9753, 705),
		STRENGTH("Sloane", "Warriors' Guild", 9750, 4297),
		HITPOINTS("Surgeon General Tafani", "Duel Arena", 9768, 961),
		RANGED("Armour salesman", "Ranging Guild", 9756, 682),
		PRAYER("Brother Jered", "Monastery", 9759, 802),
		MAGIC("Robe Store Owner", "Wizards' Guild", 9762, 1658),
		COOKING("Head chef", "Cooks' Guild", 9801, 847),
		WOODCUTTING("Wilfred", "Lumbridge", 9807, 4906),
		FLETCHING("Hickton", "Catherby", 9783, 575),
		FISHING("Master Fisher", "Fishing Guild", 9798, 308),
		FIREMAKING("Ignatius Vulcan", "South of Seers' Village", 9804, 4946),
		CRAFTING("Master Crafter", "Crafting Guild", 9780, 805),
		SMITHING("Thurgo", "Mudskipper Point", 9795, 604),
		MINING("Dwarf", "Mining Guild", 9792, 3295),
		HERBLORE("Kaqemeex", "Taverley", 9774, 455),
		AGILITY("Cap'n Izzy No-Beard", "Brimhaven Agility Arena", 9771, 437),
		THIEVING("Martin Thwait", "Rogues' Den", 9777, 2270),
		SLAYER("Duradel", "Shilo Village", 9786, 8275),
		FARMING("Martin the Master Gardener", "Entrana", 9810, 5832),
		RUNECRAFTING("Aubury", "South-East Varrock", 9765, 553),
		HUNTER("Master Hunter", "South-East Varrock", 9948, 553);
		//CONSTRUCTION("Estate Agent", "South-East Varrock", 9765, 553); // Removed to prevent error when equipping any skill cape

		private String npc;

		private String location;

		private int skillCapeId;

		private int npcId;


		private SkillCapeMasterData(String npc, String location, int skillCapeId, int npcId) {
			this.npc = npc;
			this.location = location;
			this.skillCapeId = skillCapeId;
			this.npcId = npcId;
		}

		public String getNpc() {
			return npc;
		}

		public String getLocation() {
			return location;
		}

		public int getUntrimmedSkillCapeId() {
			return skillCapeId;
		}

		public int getNpcId() {
			return npcId;
		}
	}

	/**
	 * Announce the player for reaching 99 in a skill.
	 *
	 * @param skill The skill type.
	 * @param skillName The name of the skill.
	 */
	public static void announce99Achieved(Player player, int skill) {
		if (player.baseSkillLevel[skill] < 99) {
			return;
		}
		if (skill <= 6 && player.combatSkillsAnnounced[skill]) {
			return;
		}
		if (skill <= 6) {
			player.combatSkillsAnnounced[skill] = true;
		}
		player.playerAssistant.announce(GameMode.getGameModeName(player) + " has just achieved 99 " + ServerConstants.SKILL_NAME[skill] + ".");
		player.getDH().sendItemChat("", "Congratulations! You are now an expert of " + ServerConstants.SKILL_NAME[skill] + ".", "Why not visit the Wise Old Man in Edgeville?",
		                            "He has something special that is only available for", "true experts of the " + ServerConstants.SKILL_NAME[skill] + " skill!",
		                            SkillCapeMasterData.values()[skill].getUntrimmedSkillCapeId(), 200, 0, -16);
		player.getPA().sendScreenshot("99 " + ServerConstants.SKILL_NAME[skill], 2);
	}

	/**
	 * Update all the text in the skill tab.
	 *
	 * @param player The associated player.
	 */
	public static void updateAllSkillTabFrontText(Player player) {
		for (int i = 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
			player.getPA().setSkillLevel(i, player.baseSkillLevel[i], player.skillExperience[i]);
			updateSkillTabFrontTextMain(player, i);
		}
	}

	/**
	 * Stop all on-going skilling.
	 *
	 * @param player The associated player.
	 */
	public static void stopAllSkilling(Player player) {
		player.yewLog = false;
		player.rawBeef = false;
		player.barsToMake = 0;
		player.miningTimer = 0;
		AfkChair.resetAfk(player, false);
		player.isFarming = false;
		Cooking.resetCooking(player);
		player.smeltInterface = false;
		player.forceStopSkillingEvent = true;
		Fishing.stopFishing(player);
		Woodcutting.stopWoodcutting(player);
		player.oreInformation[0] = player.oreInformation[1] = player.oreInformation[2] = 0;
		player.playerSkillProp = new int[20][15];
		player.skillingData = new int[10];
	}

	/**
	 * Announce 100 million experience in a skill.
	 *
	 * @param player The associated player.
	 * @param skill The skill that has reached 100 million experience.
	 */
	public static void announceMaxExperienceInASkill(Player player, int skill) {
		boolean announce100m = player.skillExperience[skill] >= 100000000 && !player.skillMilestone100mAnnounced[skill];
		boolean announce200m = player.skillExperience[skill] == 200000000 && !player.skillMilestone200mAnnounced[skill];
		if (announce100m || announce200m) {
			String textM = announce100m ? "100m" : "200m";
			String text[] =
					{GameMode.getGameModeName(player) + " has achieved " + textM + " experience in " + ServerConstants.SKILL_NAME[skill] + "."};
			player.playerAssistant.announce(text[0]);
			if (announce100m) {
				player.skillMilestone100mAnnounced[skill] = true;
			} else {
				player.skillMilestone200mAnnounced[skill] = true;
			}
			PlayerTitle.checkCompletionSingle(player, 32 + skill);
			Achievements.checkCompletionSingle(player, 1117);
			Achievements.checkCompletionSingle(player, 1095 + skill);
			player.getDH().sendItemChat("", "Congratulations! You are now a legend of " + ServerConstants.SKILL_NAME[skill] + ".", "Why not visit the Wise Old Man in Edgeville?",
			                            "He has something special that is only available for", "true legends of the " + ServerConstants.SKILL_NAME[skill] + " skill!",
			                            SkillCapeMasterData.values()[skill].getUntrimmedSkillCapeId() + 1, 200, 0, -16);
			player.gfx100(1389);
			player.getPA().sendScreenshot(textM + " in " + ServerConstants.SKILL_NAME[skill], 2);
		}
	}

	private static String getHoverText(Player player, int skillId) {
		int maximumExperienceCap = 200000000;
		int currentExp = player.skillExperience[skillId];
		String currentExperience = "Current Xp: " + Misc.formatNumber(currentExp) + "\\n";
		String[] message = new String[4];
		if (skillId <= 6) {
			message[0] = ServerConstants.SKILL_NAME[skillId] + ": " + player.currentCombatSkillLevel[skillId] + "/" + player.baseSkillLevel[skillId] + "\\n";
		} else {
			message[0] = ServerConstants.SKILL_NAME[skillId] + ": " + player.baseSkillLevel[skillId] + "/" + player.baseSkillLevel[skillId] + "\\n";
		}
		message[1] = currentExperience;
		message[2] = player.baseSkillLevel[skillId] >= 99 ?
				             "Next lvl at: " + Misc.formatNumber(maximumExperienceCap) + "\\n" :
				             "Next lvl at: " + Misc.formatNumber(Skilling.getExperienceForLevel(player.baseSkillLevel[skillId] + 1)) + "\\n";
		if (currentExp == maximumExperienceCap) {
			message[3] = "Remainder: 0\\n";
		} else {
			message[3] = player.baseSkillLevel[skillId] >= 99 ?
					             "Remainder: " + Misc.formatNumber((maximumExperienceCap - player.skillExperience[skillId])) + "\\n" :
					             "Remainder: " + Misc.formatNumber(Skilling.getExprienceForLevelDifferent(player.baseSkillLevel[skillId] + 1) - player.skillExperience[skillId])
					             + "\\n";
		}
		return message[0] + message[1] + message[2] + message[3];
	}

	/**
	 * Update the "Current Xp:" in the skill tab hovers.
	 *
	 * @param player The associated player.
	 * @param skill The skill being used.
	 * @param updateAll True, to update all the skills.
	 */
	public static void updateSkillTabExperienceHover(Player player, int skill, boolean updateAll) {
		if (updateAll) {
			for (int index = 0; index < ServerConstants.getTotalSkillsAmount(); index++) {
				player.getPA().sendFrame126(getHoverText(player, index), skillTabHoverFrames[index]);
			}
			return;
		}
		String text = getHoverText(player, skill);
		player.getPA().sendFrame126(text, skillTabHoverFrames[skill]);
	}

	public static final boolean view190 = true;

	public static boolean hasRequiredLevel(final Player c, int id, int lvlReq, String skill, String event) {
		if (c.baseSkillLevel[id] < lvlReq) {
			c.playerAssistant.sendMessage("You dont't have a high enough " + skill + " level to " + event + "");
			c.playerAssistant.sendMessage("You at least need the " + skill + " level of " + lvlReq + ".");
			c.getDH().sendStatement("You need a " + skill + " level of " + lvlReq + " to " + event + ".");
			return false;
		}
		return true;
	}

	public static void deleteTime(Player c) {
		c.doAmount--;
	}

	/**
	 * Update the total level text on the skill tab interface.
	 *
	 * @param player The associated player.
	 */
	public static void updateTotalLevel(Player player) {
		player.setTotalLevel(getOriginalTotalLevel(player));
		player.getPA().sendFrame126("Total level: " + player.getTotalLevel(), 3984);
		//player.getPA().sendFrame126(" ", 3985); // QP: text on the 474 skill tab.
	}

}
