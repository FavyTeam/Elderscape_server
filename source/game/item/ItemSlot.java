package game.item;

import core.GameType;
import core.ServerConstants;

/**
 * Item equipment slot data.
 *
 * @author MGT Madness, created on 12-10-2015.
 */
public class ItemSlot {

	/**
	 * @param item
	 * @param itemId
	 * @return The equipment slot which the item belongs to.
	 */
	public static int getItemEquipmentSlot(String item, int itemId) {
		item = item.toLowerCase();
		int slot = -1;
		slot = setPreEocItemEquipmentSlot(item, itemId, slot);
		if (slot >= 0) {
			return slot;
		}
		slot = setOsrsItemEquipmentSlot(item, itemId, slot);
		if (slot >= 0) {
			return slot;
		}

		if (item.contains("tentacle")) {
			return ServerConstants.WEAPON_SLOT;
		}

		switch (itemId) {
			case 4166: // Earmuffs.
			case 9472: // Gnome goggles.
			case 1025: // Eye patch.
				return ServerConstants.HEAD_SLOT;

			case 7918: // Bonesack.
			case 7535: // Diving apparatus.
			case 5607: // Grain.
			case 10556: // Attacker icon
			case 10557: // Collector icon
			case 10559: // Healer icon
			case 10558: // Defender icon
				return ServerConstants.CAPE_SLOT;

			case 4566: // Rubber chicken.
			case 4565: // Basket of eggs.
			case 7053: // Lit bug lantern
			case 7671: // Boxing gloves.
			case 7673: // Boxing gloves.
			case 10840: // A jester stick.
			case 4084: // Sled.
			case 4024: // Monkey greegree.
			case 6525: // Toktz-xil-ek.
			case 10501: // Snowball.
			case 6522: // Toktz-xil-ul.
			case 2963: // Silver sickle.
			case 6526: // Toktz-mej-tal.
			case 4037: // Saradomin banner.
			case 10010: // Butterfly net
			case 4170: // Slayer's staff
			case 4214: // Crystal bow full
			case 6523: // Toktz-xil-ak
			case 22552: // Thammaron's sceptre (u)
			case 22555: // Thammaron's sceptre
				return ServerConstants.WEAPON_SLOT;

			case 1035: // Zamorak robe.
			case 9070: // Moonclan armour.
			case 6129: // Rock-shell plate.
			case 426: // Priest gown.
			case 577: // Wizard robe.
			case 544: // Monk's robe.
			case 430: // Doctor's gown.
			case 11020: // Chicken wings.
			case 10378: // Guthix dragonhide
			case 10370: // Zamorak d'hide
			case 10386: // Saradomin d'hide
				return ServerConstants.BODY_SLOT;

			case 4225: // Crystal shield
				return ServerConstants.SHIELD_SLOT;

			case 1033: // Zamorak robe.
			case 10838: // Silly jester tights.
			case 542: // Monk's robe.
			case 4300: // H.a.m. robe.
			case 6396: // Menaphite robe.
			case 6404: // Menaphite robe.
			case 6752: // Black desert robe.
				return ServerConstants.LEG_SLOT;

			case 11019: // Chicken feet.
				return ServerConstants.FEET_SLOT;

			case 7462: // Barrows gloves
				return ServerConstants.HAND_SLOT;
		}

		for (int i = 0; i < ARROWS.length; i++) {
			if (item.contains(ARROWS[i])) {
				return ServerConstants.ARROW_SLOT;
			}
		}
		for (int i = 0; i < CAPES.length; i++) {
			if (item.contains(CAPES[i])) {
				return ServerConstants.CAPE_SLOT;
			}
		}
		for (int i = 0; i < HATS.length; i++) {
			if (item.contains(HATS[i]) && !item.contains("hatchet")) {
				return ServerConstants.HEAD_SLOT;
			}
		}

		for (int i = 0; i < AMULETS.length; i++) {
			if (item.contains(AMULETS[i])) {
				return ServerConstants.AMULET_SLOT;
			}
		}
		for (int i = 0; i < WEAPONS.length; i++) {
			if (item.contains(WEAPONS[i]) && !item.contains("arcane")) {
				return ServerConstants.WEAPON_SLOT;
			}
		}
		for (int i = 0; i < BODY.length; i++) {
			if (item.contains(BODY[i])) {
				return ServerConstants.BODY_SLOT;
			}
		}
		for (int i = 0; i < SHIELDS.length; i++) {
			if (item.contains(SHIELDS[i])) {
				return ServerConstants.SHIELD_SLOT;
			}
		}
		for (int i = 0; i < LEGS.length; i++) {
			if (item.contains(LEGS[i])) {
				return ServerConstants.LEG_SLOT;
			}
		}
		for (int i = 0; i < GLOVES.length; i++) {
			if (item.contains(GLOVES[i])) {
				return ServerConstants.HAND_SLOT;
			}
		}
		for (int i = 0; i < BOOTS.length; i++) {
			if (item.contains(BOOTS[i])) {
				return ServerConstants.FEET_SLOT;
			}
		}
		for (int i = 0; i < RINGS.length; i++) {
			if (item.contains(RINGS[i])) {
				return ServerConstants.RING_SLOT;
			}
		}
		return slot;
	}

	private static int setPreEocItemEquipmentSlot(String item, int itemId, int slot) {
		if (!GameType.isPreEoc()) {
			return slot;
		}

		switch (itemId) {
			case 20_714: // Bouquet
			case 14_728: // Easter carrot.
			case 15_426: // Candy cane.
			case 12_844: // Toy kite.
				return ServerConstants.WEAPON_SLOT;
			case 19_889:
				return ServerConstants.SHIELD_SLOT;
			case 15_673: // Squirrel ears.
				return ServerConstants.HEAD_SLOT;
			case 3_695:
			case 15_353:
				return ServerConstants.WEAPON_SLOT;

		}
		return slot;
		
	}

	private static int setOsrsItemEquipmentSlot(String item, int itemId, int slot) {
		if (!GameType.isOsrs()) {
			return slot;
		}

		String itemName = ItemAssistant.getItemName(itemId);
		if (itemName.equals("Heavy ballista") || itemName.equals("Light ballista")) {
			return ServerConstants.WEAPON_SLOT;
		}
		if (itemName.equals("Justiciar faceguard")) {
			return ServerConstants.HEAD_SLOT;
		}
		if (itemName.equals("Justiciar chestguard")) {
			return ServerConstants.BODY_SLOT;
		}

		if (itemName.equals("Justiciar legguards")) {
			return ServerConstants.LEG_SLOT;
		}


		switch (itemId) {

			case 12638: // Zamorak halo
			case 12637: // Saradomin halo
			case 12639: // Guthix halo
			case 14824: // Octopus.
			case 13353: // Double eyepatches.
			case 13355: // Left eyepatch.
			case 15490: // Focus sight.
			case 12337: // Sagacious spectacles.
			case 20035: // Samurai kasa
			case 20053: // Half moon spectacles
			case 20110: // Bowl head
			case 11990: // Fedora
			case 12245: // Beanie
			case 12353: // Monocle
			case 19724: // Left eye patch
			case 22482:
			case 12855: // Hunter's honour.
			case 12856: // Rogue's revenge.
			case 16392 : // Defender icon
				return ServerConstants.HEAD_SLOT;

			case 19970 : // Dark bow tie
			case 19985 : // Light bow tie
			case 12851 : // Amulet of the damned (full)
				return ServerConstants.AMULET_SLOT;

			case 22494 : // Sinhaza shroud tier 1
			case 22496 : // Sinhaza shroud tier 2
			case 22498 : // Sinhaza shroud tier 3
			case 22500 : // Sinhaza shroud tier 4
			case 22502 : // Sinhaza shroud tier 5
			case 20834 : // Sack of presents
			case 22109 : // Ava's assembler
			case 12514 : // Explorer backpack.
			case 19556 : // Monkey
			case 14011 : // Completionist cape
				return ServerConstants.CAPE_SLOT;

			case 20240: // Crier coat
			case 20436: // Evil chicken wings
			case 12492: // Ancient d'hide
			case 12500: // Bandos d'hide
			case 12508: // Armadyl d'hide
			case 12441: // Musketeer tabard
			case 20704: // Pyromancer garb
			case 22490:
			case 21468:
				return ServerConstants.BODY_SLOT;

			case 20044: // Samurai greaves
			case 20202: // Monk's robe (g)
			case 20520: // Elder chaos robe
			case 20777: // Banshee robe
			case 13073: // Elite void robe
			case 12443: // Musketeer pants
			case 13260: // Angler waders
			case 20706: // Pyromancer robe
				return ServerConstants.LEG_SLOT;

			case 13237: // Pegasian boots.
			case 20107: // Ankou socks
			case 20433: // Evil chicken feet
			case 20092: // Mummy's feet
				return ServerConstants.FEET_SLOT;

			case 20590 : // Stale baguette
			case 19912 : // Zombie head
			case 20836 : // Giant present
			case 21865 : // Fine mesh net
			case 20756 : // Hill giant club
			case 12899 : // Trident of the swamp.
			case 12926 : // Toxic blowpipe.
			case 13263 : // Abyssal buldgeon.
			case 19918 : // Nunchaku
			case 19941 : // Heavy casket
			case 20056 : // Ale of the gods
			case 20164 : // Large spade
			case 20243 : // Crier bell
			case 20249 : // Clueless scroll
			case 21031 : // Infernal harpoon
			case 21028 : // Dragon harpoon
			case 11905 : // Trident of the seas (full)
			case 12357 : // Katana
			case 11959 : // Black chinchompa
			case 12808 : // Sara's blessed sword
			case 21015 : // Dinh's bulwark
			case 16259 : // Dhin's bulwark
			case 12727 : // Event rpg
			case 12439 : // Royal sceptre.
				return ServerConstants.WEAPON_SLOT;

			case 16359 : // Attacker icon
			case 16360 : // Attacker icon
			case 12807 : // Odium ward
			case 12806 : // Malediction ward
			case 12335 : // Briefcase.
			case 22002 : // Dragonfire ward
			case 22003 : // Dragonfire ward
			case 16093 : // Black Defender (Oatrix)
			case 12612 : // Book of darkness
			case 12610 : // Book of law
			case 21000 : // Twisted buckler
			case 11924 : // Malediction ward
			case 11926 : // Odium ward
			case 18340 : // anti poison totem
				return ServerConstants.SHIELD_SLOT;

			case 19961 : // Dark tuxedo cuffs
			case 19976 : // Light tuxedo cuffs
			case 19997 : // Holy wraps
			case 20086 : // Mummy's hands
			case 13665 : // Bunny paws
				return ServerConstants.HAND_SLOT;
		}
		return slot;

	}

	private static String[] HATS =
			{
					"boater",
					"wig",
					"cowl",
					"head",
					"peg",
					"coif",
					"helm",
					"mask",
					"hat",
					"headband",
					"hood",
					"disguise",
					"cavalier",
					"full",
					"tiara",
					"helmet",
					"ears",
					"crown",
					"partyhat",
					"helm(t)",
					"helm(g)",
					"beret",
					"facemask",
					"sallet",
					"hat(g)",
					"hat(t)",
					"bandana",
					"mitre",
					"afro",
					"Afro",
					"Lord marshal cap",
					"cap",
			};

	private static String[] CAPES =
			{"cape", "accumulator", "attractor", "cloak", "alerter", "kal", "master cape", "tail"};

	private static String[] AMULETS =
			{"amulet", "scarf", "necklace", "pendant", "symbol", "stole", "ammy"};

	private static String[] WEAPONS =
			{
					"banner",
					"hasta",
					"hand",
					"mace",
					"dart",
					"knife",
					"javelin",
					"scythe",
					"claws",
					"bow",
					"crossbow",
					"c' bow",
					"adze",
					"axe",
					"hatchet",
					"sword",
					"rapier",
					"scimitar",
					"spear",
					"dagger",
					"staff",
					"wand",
					"blade",
					"whip",
					"silverlight",
					"darklight",
					"maul",
					"halberd",
					"anchor",
					"tzhaar-ket-om",
					"hammer",
					"hand cannon",
					"flail",
					"crozier",
					"cane",
					"flower",
					"flag",
					"greegree",
					"hook"
			};

	private static String[] BODY =
			{
					"varrock armour",
					"body",
					"top",
					"Priest gown",
					"apron",
					"shirt",
					"platebody",
					"robetop",
					"body(g)",
					"body(t)",
					"wizard robe (g)",
					"wizard robe (t)",
					"body",
					"brassard",
					"blouse",
					"tunic",
					"leathertop",
					"chainbody",
					"hauberk",
					"shirt",
					"torso",
					"chestplate",
					"jacket",
					"runecrafter robe",
			};

	private static String[] SHIELDS =
			{"kiteshield", "book", "Kiteshield", "toktz-ket-xil", "Toktz-ket-xil", "shield", "kite", "defender", "tome"};

	private static String[] LEGS =
			{
					"tassets",
					"chaps",
					"bottoms",
					"gown",
					"trousers",
					"platelegs",
					"robebottoms",
					"plateskirt",
					"legs",
					"leggings",
					"shorts",
					"skirt",
					"cuisse",
					"trousers",
					"pantaloons",
					"tasset",
					"robe bottom",
					"enchanted robe",
					"villager robe",
					"navy slacks"
			};

	private static String[] GLOVES =
			{"glove", "vamb", "gauntlets", "bracers", "vambraces", "bracelet", "villager armband",};

	private static String[] BOOTS =
			{"boots", "shoes", "flipper", "sandal", "feet"};

	private static String[] RINGS =
			{"ring"};

	private static String[] ARROWS =
			{"bolts", "arrow", "bolt rack", "hand cannon shot", "dragon javelin", "blessing"};

}
