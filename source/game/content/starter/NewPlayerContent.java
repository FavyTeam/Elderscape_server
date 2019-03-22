package game.content.starter;

import core.GameType;
import core.ServerConstants;
import game.content.miscellaneous.SpellBook;
import game.content.miscellaneous.XpBonus;
import game.content.quicksetup.Pure;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Skilling;
import game.content.starter.GameMode.PkerStarters;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.log.NewPlayerIpTracker;
import game.player.Player;
import game.player.PlayerSave;
import utility.Misc;

/**
 * Starter package.
 *
 * @author MGT Madness, created on 11-01-2014.
 */
public class NewPlayerContent {

	/**
	 * Buttons use-able while tutorial is not finished.
	 */
	public static int[] tutorialButtonExceptionList =
			{9167, 9168, 9169, 98017, 98021, 98025, 98029, 98033, 98037, 14067, 9154, 9158, 9157};

	/**
	 * Set the date of when the account is created on.
	 */
	private static void setDateCreated(Player player) {
		player.accountDateCreated = Misc.getDateAndTime();
		player.timeOfAccountCreation = System.currentTimeMillis();
	}

	/**
	 * New player logged in.
	 */
	public static void logIn(Player player) {
		if (player.isJailed()) {
			return;
		}
		if (!player.isTutorialComplete()) {
			if (GameType.isOsrsEco()) {
				player.setNpcType(306);
				player.getDH().sendDialogues(560);
				player.showTutorialArrows = true;
			} else {
				player.setNpcType(306);
				player.getDH().sendDialogues(270);
				player.showTutorialArrows = true;
			}
		}
	}

	public final static int[][] IRON_MAN_STARTER_KIT =
			{
					{995, 5000}, // Coins
					{554, 25}, // Fire rune
					{555, 25}, // Water rune
					{556, 25}, // Air rune
					{557, 25}, // Earth rune
					{558, 25}, // Mind rune
					{562, 25}, // Chaos rune
					{1205, 1}, // Bronze dagger
					{1171, 1}, // Wooden shield
					{1265, 1}, // Bronze pickaxe
					{1351, 1}, // Bronze axe
					{2347, 1}, // Hammer
					{590, 1}, // Tinderbox
					{1755, 1}, // Chisel
					{946, 1}, // Knife
					{303, 1}, // Small fishing net
			};

	public final static int[][] NORMAL_STARTER_KIT_ECO =
			{
					{995, 500000}, // Coins
					{1381, 1}, // Staff of air
					{554, 100}, // Fire rune
					{555, 100}, // Water rune
					{556, 100}, // Air rune
					{557, 100}, // Earth rune
					{558, 100}, // Mind rune
					{562, 100}, // Chaos rune
					{1731, 1}, // Amulet of power
					{1323, 1}, // Iron scimitar
					{1327, 1}, // Black scimitar
					{1329, 1}, // Mithril scimitar
					{1331, 1}, // Adamant scimitar
					{1333, 1}, // Rune scimitar
					{882, 100}, // Bronze arrow
					{841, 1}, // Shortbow
					{853, 1}, // Maple shortbow
					{380, 30}, // Lobster
					{379, 3}, // Lobster
					{1153, 1}, // Iron full helm
					{1115, 1}, // Iron platebody
					{1067, 1}, // Iron platelegs
					{1191, 1}, // Iron kiteshield
					{1061, 1}, // Leather boots
			};

	private final static int[] STANDARD_IRON_MAN_OUTFIT =
			{
					12810, // Ironman helm
					12811, // Ironman platebody
					12812, // Ironman platelegs
			};

	private final static int[] HARDCORE_IRON_MAN_OUTFIT =
			{
					20792, // Hardcore ironman helm
					20794, // Hardcore ironman platebody
					20796, // Hardcore ironman platelegs
			};

	private final static int[] ULTIMATE_IRON_MAN_OUTFIT =
			{
					12813, // Ultimate ironman helm
					12814, // Ultimate ironman platebody
					12815, // Ultimate ironman platelegs
			};

	// Second bank tab.
	private final static int[] secondTab =
			{
					// Super strength(3).
					157,
					// Super attack(3).
					145,
					// Ranging potion(3).
					169,
					// Magic potion(3).
					3042,
					// Super defence(3).
					163,
					// Saradomin brew(3).
					6687,
					// Super restore(3).
					3026,
					// Prayer potion(3).
					139,
					// Strength potion(3).
					115,
					// Antipoison(3).
					175,
					// Super strength(2).
					159,
					// Super attack(2).
					147,
					// Ranging potion(2).
					171,
					// Magic potion(2).
					3044,
					// Super defence(2).
					165,
					// Saradomin brew(2).
					6689,
					// Super restore(2).
					3028,
					// Prayer potion(2).
					141,
					// Strength potion(2).
					117,
					// Antipoison(2).
					177,
					// Super strength(1).
					161,
					// Super attack(1).
					149,
					// Ranging potion(1).
					173,
					// Magic potion(1).
					3046,
					// Super defence(1).
					167,
					// Saradomin brew(1).
					6691,
					// Super restore(1).
					3030,
					// Prayer potion(1).
					143,
					// Strength potion(1).
					119,
					// Antipoison(1).
					179,
					// Vial.
					229,
					// 1/2 anchovy pizza.
					2299,
			};

	public static final int END_TUTORIAL_X_COORDINATE = 3080;

	public static final int END_TUTORIAL_Y_COORDINATE = 3504;

	/**
	 * Add the items associated with the starter package to the bank.
	 * <p>It will read the data from the starter_package.txt file and add the items to the bank.
	 *
	 * @param player The associated player.
	 */
	public static void givePvpStarterKit(Player player) {
		int randomOutFitSet = Misc.random(Pure.robeAndHatSet.length - 1);
		QuickSetUp.setCombatSkills(player, "MAIN", false, null);
		SpellBook.lunarSpellBook(player, true);
		Skilling.updateSkillTabExperienceHover(player, 0, true);
		Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
		player.getPA().sendMessage("Check the guide button on your quest tab!");
		player.getPA().sendMessage("Your bank has a starter kit.");

		int[][] bank =
				{
						{13307, 2}, // Blood money
						{385, 2000}, // Shark
						{373, 1000}, // Swordfish
						{2297, 150}, // Anchovy pizza
						{333, 100}, // Trout
						{2440, 200}, // Super strength(4)
						{2436, 200}, // Super attack(4)
						{2444, 200}, // Ranging potion(4)
						{3040, 200}, // Magic potion(4)
						{2442, 200}, // Super defence(4)
						{1127, 20}, // Rune platebody
						{8850, 2}, // Rune defender
						{2503, 20}, // Black d'hide body
						// Mystic robe top.
						{QuickSetUp.getRandomMysticTop(), 20},
						{4675, 20}, // Ancient staff
						{6685, 200}, // Saradomin brew(4)
						{3024, 200}, // Super restore(4)
						{2434, 200}, // Prayer potion(4)
						{113, 200}, // Strength potion(4)
						{2446, 200}, // Antipoison(4)
						{1079, 20}, // Rune platelegs
						{4587, 20}, // Dragon scimitar
						{2497, 20}, // Black d'hide chaps
						// Mystic robe bottom.
						{QuickSetUp.getRandomMysticBottom(), 20},
						{3842, 2}, // Unholy book
						// Team cape.
						{QuickSetUp.getTeamCape(false), 100},
						{1434, 20}, // Dragon mace
						{3204, 20}, // Dragon halberd
						{9075, 4000}, // Astral rune
						{565, 5200}, // Blood rune
						{3105, 20}, // Rock climbing boots
						{1712, 20}, // Amulet of glory(4)
						{10828, 20}, // Helm of neitiznot
						{7461, 2}, // Gloves
						// Random god cape.
						{QuickSetUp.getRandomGodCape(), 2},
						{9244, 500}, // Dragon bolts (e)
						{1215, 20}, // Dragon dagger
						{1305, 20}, // Dragon longsword
						{557, 10000}, // Earth rune
						{560, 13400}, // Death rune
						{4131, 20}, // Rune boots
						{1725, 20}, // Amulet of strength
						{3751, 20}, // Berserker helm
						{1201, 20}, // Rune kiteshield
						{10499, 2}, // Ava's accumulator
						{9185, 20}, // Rune crossbow
						{861, 20}, // Magic shortbow
						{562, 1000}, // Chaos rune
						{563, 1000}, // Law rune
						{555, 15600}, // Water rune
						// Hat.
						{Pure.robeAndHatSet[randomOutFitSet][0], 20},
						// Robe top.
						{Pure.robeAndHatSet[randomOutFitSet][1], 20},
						// Robe bottoms.
						{Pure.robeAndHatSet[randomOutFitSet][2], 20},
						{6528, 20}, // Tzhaar-ket-om
						{7458, 2}, // Mithril gloves
						{868, 1000}, // Rune knife
						{892, 1000}, // Rune arrow
						{6328, 20}, // Snakeskin boots
						{952, 20}, // Spade
						{1523, 20}, // Lockpick
						{6568, 20}, // Obsidian cape
						{544, 20}, // Monk's robe
						{542, 20}, // Monk's robe
						{1333, 20}, // Rune scimitar
						{1319, 20}, // Rune 2h sword
						{853, 20}, // Maple shortbow
						{890, 1000}, // Adamant arrow
						{1065, 20}, // Green d'hide vamb
						{1059, 20}, // Leather gloves
						{1061, 20}, // Leather boots
				};

		for (int index = 0; index < bank.length; index++) {
			player.bankItems[index] = bank[index][0] + 1;
			player.bankItemsN[index] = bank[index][1];
		}



		for (int index = 0; index < secondTab.length; index++) {
			player.bankItems1[index] = secondTab[index] + 1;
			player.bankItems1N[index] = 1;
		}
	}

	/**
	 * End the tutorial.
	 *
	 * @param player The associated player.
	 */
	public static void endTutorial(Player player) {
		if (GameType.isOsrsPvp()) {
			givePvpStarterKit(player);
		}
		setDateCreated(player);
		NewPlayerIpTracker.saveIp(player.addressIp, player.getPlayerName());
		player.originalIp = player.addressIp;
		player.originalUid = player.addressUid;
		player.originalMac = player.addressMac;
		PlayerSave.saveGame(
				player); // Left here so if a new player logs in and commits a crime, the moderator can ipmute them. because before punishing a player, it checks if their account exists.
		player.getPA().requestUpdates();
		if (GameType.isOsrsPvp()) {
			completelyEndTutorial(player);
		} else {
			// Select gameplay interface
			player.selectedDifficulty = "NORMAL";
			GameMode.sendDescription(player, "NORMAL");
			player.getPA().setInterfaceClicked(23439, 23447, true);

			player.selectedGameMode = "NONE";
			GameMode.sendDescription(player, "NONE");
			player.getPA().setInterfaceClicked(23440, 23453, true);
			player.getPA().displayInterface(23439);
		}
	}

	public static void completelyEndTutorial(Player player) {
		player.getPA().displayInterface(3559);
		player.canChangeAppearance = true;
		player.showTutorialArrows = false;
		player.playerAssistant.sendMessage(":tutorial:");
		player.timeFinishedTutorial = System.currentTimeMillis();
		player.setTutorialComplete(true);
	}

	static void giveEcoStarterKit(Player player) {

		int[][] starterKit = null;
		if (!GameMode.getGameMode(player, "PKER")) {
			XpBonus.giveXpBonus(player, XpBonus.NEW_PLAYER_XP_BONUS_MINUTES);
		}
		if (GameMode.getGameModeContains(player, "IRON MAN")) {
			starterKit = IRON_MAN_STARTER_KIT;
			int[] ironManOutfitArray = null;
			switch (player.getGameMode()) {
				case "STANDARD IRON MAN":
					ironManOutfitArray = STANDARD_IRON_MAN_OUTFIT;
					break;
				case "HARDCORE IRON MAN":
					ironManOutfitArray = HARDCORE_IRON_MAN_OUTFIT;
					break;
				case "ULTIMATE IRON MAN":
					ironManOutfitArray = ULTIMATE_IRON_MAN_OUTFIT;
					break;
			}
			ItemAssistant.replaceEquipmentSlot(player, ServerConstants.HEAD_SLOT, ironManOutfitArray[0], 1, false);
			ItemAssistant.replaceEquipmentSlot(player, ServerConstants.BODY_SLOT, ironManOutfitArray[1], 1, false);
			ItemAssistant.replaceEquipmentSlot(player, ServerConstants.LEG_SLOT, ironManOutfitArray[2], 1, false);
		} else if (GameMode.getGameMode(player, "PKER")) {

			boolean give = true;
			for (int index = 0; index < PkerStarters.pkerStarters.size(); index++) {
				PkerStarters instance = PkerStarters.pkerStarters.get(index);
				if (Misc.uidMatches(player.addressUid, instance.uid) || instance.ip.equals(player.addressIp)) {
					give = false;
					break;
				}
			}
			if (give) {
				PkerStarters.pkerStarters.add(new PkerStarters("3", "4"));
				QuickSetUp.setCombatSkills(player, "PURE", false, null);
				int randomOutFitSet = Misc.random(Pure.robeAndHatSet.length - 1);
				starterKit = new int[][]
						             {
								             {995, 350_000},
								             {4587, 1},
								             {1215, 1},
								             {7458, 1},
								             {3105, 1},
								             {1725, 1},
								             {QuickSetUp.getRandomGodCape(), 1},
								             {QuickSetUp.getTeamCape(false), 1},
								             // Hat.
								             {Pure.robeAndHatSet[randomOutFitSet][0], 1},
								             // Robe top.
								             {Pure.robeAndHatSet[randomOutFitSet][1], 1},
								             // Robe bottoms.
								             {Pure.robeAndHatSet[randomOutFitSet][2], 1},
						             };
				CoinEconomyTracker.addIncomeList(player, "STARTER-NORMAL " + starterKit[0][1] + 150_000);
				PkerStarters.pkerStarters.add(new PkerStarters(player.addressIp, player.addressUid));
			} else {
				player.getPA().sendMessage("You were not given a pking starter kit because you already received one.");
			}
		} else {
			starterKit = NORMAL_STARTER_KIT_ECO;
			CoinEconomyTracker.addIncomeList(player, "STARTER-NORMAL " + starterKit[0][1]);
		}
		if (starterKit != null) {
			for (int a = 0; a < starterKit.length; a++) {
				ItemAssistant.addItemToInventoryOrDrop(player, starterKit[a][0], starterKit[a][1]);
			}
		}

	}

}
