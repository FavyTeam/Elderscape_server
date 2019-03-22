package game.content.miscellaneous;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.commands.NormalCommand;
import game.content.interfaces.InterfaceAssistant;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;
import utility.Misc;

import java.util.ArrayList;

/**
 * Clue scroll system.
 *
 * @author MGT Madness, created on 03-01-2015.
 */
public class ClueScroll {

	private static enum ClueScrollData {
		// @formatter:off.
		DRAYNOR(3085, 3256, "Aggie I see", "Lonely and southern I feel", "I am neither inside nor outside", "the house, yet no house would", "be complete without me.",
		        "Your treasure waits beneath me.", " ", " "),
		PORT_KHAZARD(2656, 3161, "After trawling for bars, go to", "the nearest place to smith them and", "dig by the door.", " ", " ", " ", " ", " "),
		VARROCK_DUNGEON(3192, 9825, "I have no beginning or end.", "I am a token of the greatest love.", "My eye is red, I can fit like a glove.",
		                "Go to the place where money they lend.", "And dig by the gate to be my friend.", " ", " ", " "),
		WILDERNESS_LEVER(3153, 3923, "If you didn't want to be here and", "in danger, you should lever things", "well enough alone.", " ", " ", " ", " ", " "),
		CHURCH(2993, 3177, "While a sea view is nice,", "it seems this church has", "not seen visitors in a while.", "Dig outside the rim of the", "window for a reward.", " ", " ",
		       " "),
		NARDAH(3397, 2915, "As you desert this town,", "keep an eye out for a set of spines", "that could ruin nearby rugs:", "dig carefully around the greenery.", " ", " ", " ",
		       " "),
		QUARRY(3178, 2917, "Brush off the sand and then dig in the", "quarry.", "There is a wheely handy barrow to the east.", "Don't worry, its coal to dig there - in fact,",
		       "it's all oclay.", " ", " ", " "),
		PHASMATY(3647, 3497, "By the town of the dead, walk south down a", "rickety bridge, then dig near the slime-", "covered tree.", " ", " ", " ", " ", " "),
		EDGEVILLE(3088, 3469, "Come to the evil ledge.", "Yew know yew want to.", "Try not to get stung.", " ", " ", " ", " ", " "),
		MORTTON(3489, 3288, "Covered in shadows, the centre of the circle", "is where you will find the answer.", " ", " ", " ", " ", " ", " "),
		POLLVINEACH(3359, 2972, "Dig here if you are not feeling too well after", "travelling through the desert. Ali heartily", "recommends it.", " ", " ", " ", " ", " "),
		GNOME_VILLAGE(2460, 3505, "Dig near some giant mushrooms behind the", "Grand Tree.", " ", " ", " ", " ", " ", " "),
		WILDNERESS_GRAVEYARD(3174, 3664, "I lie lonely and forgotten in mid wilderness,", "where the dead rise from their beds.", "Feel free to quarrel and wind me up,",
		                     "and dig while you shoot their heads.", " ", " ", " ", " "),
		KARAMJA(2832, 9586, "Mine was the strangest birth under the sun.", "I left the crimson sack, Yet life had not", "begun.", "Entered the world, and yet was seen by", "none.",
		        " ", " ", " "),
		VARROCK_SEWERS(3161, 9904, "My giant guardians below", "the market streets would", "be fans of rock and roll,", "if only they could grab hold of it.",
		               "Dig near my purple smoke!", " ", " ", " ");

		// @formatter:on.
		private int digX;

		private int digY;

		private String line1;

		private String line2;

		private String line3;

		private String line4;

		private String line5;

		private String line6;

		private String line7;

		private String line8;


		private ClueScrollData(int digX, int digY, String line1, String line2, String line3, String line4, String line5, String line6, String line7, String line8) {
			this.digX = digX;
			this.digY = digY;
			this.line1 = line1;
			this.line2 = line2;
			this.line3 = line3;
			this.line4 = line4;
			this.line5 = line5;
			this.line6 = line6;
			this.line7 = line7;
			this.line8 = line8;
		}

		public int getDigX() {
			return digX;
		}

		public int getDigY() {
			return digY;
		}

		public String getLine1() {
			return line1;
		}

		public String getLine2() {
			return line2;
		}

		public String getLine3() {
			return line3;
		}

		public String getLine4() {
			return line4;
		}

		public String getLine5() {
			return line5;
		}

		public String getLine6() {
			return line6;
		}

		public String getLine7() {
			return line7;
		}

		public String getLine8() {
			return line8;
		}

	}

	//@formatter:off

	public static int[] casketRare1 =
			{
					20005, // Ring of nature
					12424, // 3rd age bow
					12422, // 3rd age wand
			};

	public static int[] casketRare2 =
			{
					20014, // 3rd age pickaxe
					12426, // 3rd age longsword
					20017, // Ring of blood money
			};

	/**
	 * Osrs clue items worth 1-20m
	 */
	public static int[] casketRare3 =
			{
					20095, // Ankou mask
					20098, // Ankou top
					20101, // Ankou gloves
					20104, // Ankou leggings
					20107, // Ankou socks
					12437, // 3rd age cloak
					20011, //3rd age axe
			};

	// 106 items
	public static int[] casketNormal1 =
			{
					12205, 12207, 12209, 12211, 12213, // Full bronze (g)
					12215, 12217, 12219, 12221, 12223, // Full bronze (t)
					12225, 12227, 12229, 12231, 12233, // Full iron (t)
					12235, 12237, 12239, 12241, 12243, // Full iron (g)
					20169, 20172, 20175, 20178, 20181, // Full steel (g)
					20184, 20187, 20190, 20193, 20196, // Full steel (t)
					2633, 2635, 2637, 12247,// Beret
					2631, // Highwayman mask.
					12245, // Beanie
					12447, 12451, 12455, // Black wizard (t)
					12445, 12449, 12453, // Black wizard (g)
					7362, 7366, // Studded (g)
					7364, 7368, // Studded (t)
					10306, 10308, 10310, 10312, 10314, // Black helm (h)
					7332, 7338, 7344, 7350, 7356, // Black shield (h)
					10400, 10402, // Elegant black
					10420, 10422, // Elegant white
					10404, 10406, 10424, 10426,  // Elegant red
					10408, 10410, 10428, 10430, // Elegant blue
					10412, 10414, 10432, 10434, // Elegant green
					12249, 12251, // Imp mask & goblin mask
					20166, // Wooden shield
					20272, // Cabbage round shield
					12498, 12500, 12502, 12504, // Bandos d'hide
					12363, 12365, 12367, 12369, 12371, // Metal dragon masks
					19973, 19976, 19979, 19982, 19985, // Light tuxedo
					20050, // Obsidian cape (r)
					19991, // Bucket helm
					10398, // Sleeping cap
					12265, 12267, 12269, 12271, 12273, 12275, // Bandos robes
					12193, 12195, 12197, 12199, 12201, 12203, // Ancient robes
					19730, // Bloodhound
			};

	// 104 items
	public static int[] casketNormal2 =
			{
					10416, 10418, 10436, 10438, // Elegant purple
					12315, 12317, 12339, 12341, // Elegant pink
					12343, 12345, 12347, 12349, // Elegant gold
					10316, 10318, 10320, 10322, 10324, // Bob's shirts
					10392, // Powdered wig
					10396, // Pantaloons
					10366, // Amulet of magic (t)
					12253, 12255, 12257, 12259, 12261, 12263, // Armadyl robes
					20164, // Large spade
					12277, 12279, 12281, 12283, 12285, // Mithril (g)
					12287, 12289, 12291, 12293, 12295, // Mithril (t)
					12598, // Holy sandals
					2645, 2647, 2649, 12299, 12301, 12303, 12305, 12307,  // Headbands
					7319, 7321, 7323, 7325, 7327, 12309, 12311, 12313, // Boater set.
					7370, 7378, // Green d'hide (g)
					7372, 7380, // Green d'hide (t)
					10296, 10298, 10300, 10302, 10304, // Adamant helm (h)
					7334, 7340, 7346, 7352, 7358, // Adamant shield (h)
					10364, // Strength amulet (t)
					12361, // Cat mask
					12428, // Penguin mask
					12319, 20240, 20243, // Crier set
					20246, 12359, // Lemprechaun hat
					20266, 20269, // Unicorn mask
					20059, // Bucket helm (g)
					20251, 20254, 20257, 20260, 20263, // Kourend house banners
					20249, // Clueless scroll
					20080, 20083, 20086, 20089, 20092, // Mummy
					12373, // Dragon cane
					20020, 20023, 20026, 20029, 20032, // Demon masks
					19918, // Nunchaku
					12351, 12441, 12443, // Musketeer set
					12956, 12957, 12958, 12959, 11919,// Cow set
			};

	// 103 items
	public static int[] casketNormal3 =
			{
					12480, 12482, 12484, 12486, 12488, // Full bandos rune
					12391, 20146, 20149, 20152, 20158, 20161,// Gilded med helm, boots, chainbody, sq shield, spear, hasta
					10286, 10288, 10290, 10292, 10294, // Rune helm (h)
					7336, 7342, 7348, 7354, 7360, // Rune shield (h)
					8950, // Pirate hat.
					2639, 2641, 2643, 11277, 12321, 12323, 12325, // Cavalier set.
					7374, 7382, // Blue d'hide (g)
					7376, 7384, // Blue d'hide (t)
					12327, 12329, // Red d'hide (g)
					12331, 12333, // Red d'hide (t)
					10362, // Amulet of glory (t)
					19921, 19924, 19927, 19930, 19933, 19936, // God d'hide boots
					12490, 12492, 12494, 12496,  // Ancient d'hide
					12518, 12520, 12522, 12524, // Dragon mask
					2599, 2601, 2603, 2605, // Full adamant (t)
					2607, 2609, 2611, 2613, // Full adamant (g)
					12516, // Pith helmet
					12514, // Explorer backpack
					19912, // Zombie head
					19915, // Cyclops head
					19958, 19961, 19964, 19967, 19970, // Dark tuxedo
					12337, // Sagacious spectacles
					12335, // Briefcase
					12353, // Monocle
					12355, // Big pirate hat
					12357, // Katana
					12430, // Afro
					19943, 19946, 19949, 19952, 19955, // Kourend house scarves
					19988, // Blacksmith's helm
					19997, // Holy wraps
					19941, // Heavy casket
					20113, 20116, 20119, 20122, 20125, // Kourend house hoods
					20035, 20038, 20041, 20044, 20047, // Samurai
					20128, 20131, 20134, 20137, 20140, // Robes of darkness
					19724, // Left eye patch
					20008, // Fancy tiara
					20053, // Half moon spectacles
					20056, // Ale of the gods
					20110, // Bowl wig
			};


	//@formatter:on

	/**
	 * Open the clue scroll and show the interface riddle.
	 *
	 * @param player The associated player.
	 */
	public static void openClueScroll(Player player) {
		if (player.clueScrollType == -1) {
			player.clueScrollType = Misc.random((ClueScrollData.values().length) - 1);
		}
		showAppropriateClueScroll(player);
		player.getPA().displayInterface(6965);
	}

	/**
	 * Show the correct Clue scroll interface text, depending on the player's Clue scroll.
	 *
	 * @param player The associated player.
	 */
	private static void showAppropriateClueScroll(Player player) {
		int clueScrollType = player.clueScrollType;
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine1(), 6968);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine2(), 6969);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine3(), 6970);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine4(), 6971);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine5(), 6972);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine6(), 6973);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine7(), 6974);
		player.getPA().sendFrame126(ClueScrollData.values()[clueScrollType].getLine8(), 6975);
	}

	/**
	 * Dig for a casket.
	 *
	 * @param player The associated player.
	 */
	public static void dig(Player player) {
		if (player.clueScrollType <= -1) {
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 2677)) {
			return;
		}
		if (player.getPA()
		          .withInDistance(player.getX(), player.getY(), ClueScrollData.values()[player.clueScrollType].getDigX(), ClueScrollData.values()[player.clueScrollType].getDigY(),
		                          6)) {
			player.clueScrollType = -1;
			ItemAssistant.addItemToInventoryOrDrop(player, 2740, 1);
			ItemAssistant.deleteItemFromInventory(player, 2677, 1);
			if (ItemAssistant.hasSingularUntradeableItem(player, 2677)) {
				player.singularUntradeableItemsOwned.remove(Integer.toString(2677));
			}
			Achievements.checkCompletionMultiple(player, "1044 1067 1118");
			player.getDH().sendItemChat("", "You have found a casket!", 2740, 200, 14, 0);
		}
	}

	/**
	 * Open the casket.
	 *
	 * @param player The associated player.
	 */
	public static void openCasket(Player player, int itemId, int[] casketNormalArray, int[] casketRareArray) {
		ArrayList<String> list = new ArrayList<String>();
		int item3 = 0;
		if (Misc.hasOneOutOf(5)) {
			int random = GameType.isOsrsEco() ? Misc.random(100000, 1500000) : Misc.random(100, 2000);
			list.add(ServerConstants.getMainCurrencyId() + " " + random);
		}
		if (Misc.hasOneOutOf(15)) {
			item3 = casketNormalArray[Misc.random(casketNormalArray.length - 1)];
			if (item3 == 19730) {
				player.singularUntradeableItemsOwned.add(Integer.toString(item3));
				RareDropLog.appendRareDrop(player, "Clue scroll: " + ItemAssistant.getItemName(item3));
				if (!player.profilePrivacyOn) {
					Announcement
							.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received " + ItemAssistant.getItemName(item3) + " from a clue casket!");
				}
				player.getPA().sendScreenshot(ItemAssistant.getItemName(item3), 2);
			}
			list.add(item3 + " " + 1);
			if (Misc.hasOneOutOf(5)) {
				item3 = casketNormalArray[Misc.random(casketNormalArray.length - 1)];
				if (item3 == 19730) {
					player.singularUntradeableItemsOwned.add(Integer.toString(item3));
					RareDropLog.appendRareDrop(player, "Clue scroll: " + ItemAssistant.getItemName(item3));
					if (!player.profilePrivacyOn) {
						Announcement.announce(
								ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received " + ItemAssistant.getItemName(item3) + " from a clue casket!");
					}
					player.getPA().sendScreenshot(ItemAssistant.getItemName(item3), 2);
				}
				list.add(item3 + " " + 1);
			}
		}
		if (Misc.hasOneOutOf(200)) {
			item3 = casketRareArray[Misc.random(casketRareArray.length - 1)];
			RareDropLog.appendRareDrop(player, "Clue scroll: " + ItemAssistant.getItemName(item3));
			if (!player.profilePrivacyOn) {
				Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received " + ItemAssistant.getItemName(item3) + " from a clue casket!");
			}
			player.getPA().sendScreenshot(ItemAssistant.getItemName(item3), 2);
			list.add(item3 + " " + 1);
		} else {
			item3 = casketNormalArray[Misc.random(casketNormalArray.length - 1)];

			// Bloodhound
			if (item3 == 19730) {
				player.singularUntradeableItemsOwned.add(Integer.toString(item3));
				RareDropLog.appendRareDrop(player, "Clue scroll: " + ItemAssistant.getItemName(item3));
				if (!player.profilePrivacyOn) {
					Announcement
							.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received " + ItemAssistant.getItemName(item3) + " from a clue casket!");
				}
				player.getPA().sendScreenshot(ItemAssistant.getItemName(item3), 2);
			}
			list.add(item3 + " " + 1);
		}
		displayReward(player, itemId, list);
	}

	/**
	 * Display the casket reward on an interface.
	 *
	 * @param player The associated player.
	 * @param item2 The item identity of the first reward.
	 * @param amount2 The item amount of the first reward.
	 * @param item3 The item identity of the second reward.
	 * @param amount3 The item amount of the second reward.
	 */
	private static void displayReward(Player player, int itemId, ArrayList<String> list) {
		//player.setClueScrollsCompleted(player.getClueScrollsCompleted() + 1);
		//player.getPA().sendFilterableMessage("Your clue scroll count is: " + ServerConstants.RED_COL + player.getClueScrollsCompleted() + ".");
		//ProfileRank.rankPopUp(player, "ADVENTURER");
		ItemAssistant.deleteItemFromInventory(player, itemId, 1);
		for (int i = 0; i < list.size(); i++) {
			String[] args = ((String) list.get(i)).split(" ");
			int item = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			ItemAssistant.addItemToInventoryOrDrop(player, item, amount);
		}
		InterfaceAssistant.displayReward(player, list);
	}

	public static void dropClueScroll(Npc npc, Player player) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		if (player == null) {
			return;
		}

		if (ItemAssistant.hasSingularUntradeableItem(player, 2677)) {
			return;
		}
		if (player.getPlayerName().equals("Arab Unity")) {
			NormalCommand.clueScrollDebug.add("Give clue scroll1.");

			for (int index = 0; index < player.singularUntradeableItemsOwned.size(); index++) {
				int itemList = Integer.parseInt(player.singularUntradeableItemsOwned.get(index));
				NormalCommand.clueScrollDebug.add("'" + itemList + "'");
			}
			NormalCommand.clueScrollDebug.add("List end.");
		}
		double chance = 0;
		int npcHp = npc.maximumHitPoints;
		if (npcHp > 250) {
			npcHp = 250;
		}
		chance = 270.0 - (double) npcHp;
		chance /= 2.6;


		boolean ring = ItemAssistant.hasItemEquippedSlot(player, 2572, ServerConstants.RING_SLOT);
		boolean imbuedRing = ItemAssistant.hasItemEquippedSlot(player, 12785, ServerConstants.RING_SLOT);
		chance *= (ring ? 0.96 : imbuedRing ? 0.90 : 1.0);
		if (chance <= 20) {
			chance = 20;
		}
		if (Misc.hasOneOutOf(chance)) {
			int npcX = npc.getVisualX();
			int npcY = npc.getVisualY();
			Server.itemHandler.createGroundItem(player, 2677, npcX, npcY, npc.getHeight(), 1, false, 0, true, "", "", "", "", "dropClueScroll 2677 " + npc.name);
			player.playerAssistant.sendMessage(ServerConstants.GREEN_COL + "You have received a clue scroll drop.");
		}
	}

}
