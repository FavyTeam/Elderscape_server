package game.content.skilling;

import game.player.Player;

/**
 * @author Sanity
 * Edits by Owain
 */

public class SkillMenu {

	private Player c;

	public int selected;

	private int item[] = new int[40];

	public SkillMenu(Player client) {
		this.c = client;
	}

	/*
	 * @param screen
	 *
	 * @return Used to call all of the menus, redundant No change needed to the
	 * method no matter How many menus added
	 */
	public void menuCompilation(int screen) {
		if (selected == 0)
			attackComplex(screen);
		else if (selected == 1)
			strengthComplex(screen);
		else if (selected == 2)
			defenceComplex(screen);
		else if (selected == 3)
			rangedComplex(screen);
		else if (selected == 4)
			prayerComplex(screen);
		else if (selected == 5)
			magicComplex(screen);
		else if (selected == 6)
			runecraftingComplex(screen);
		else if (selected == 7)
			hitpointsComplex(screen);
		else if (selected == 8)
			agilityComplex(screen);
		else if (selected == 9)
			herbloreComplex(screen);
		else if (selected == 10)
			thievingComplex(screen);
		else if (selected == 11)
			craftingComplex(screen);
		else if (selected == 12)
			fletchingComplex(screen);
		else if (selected == 13)
			slayerComplex(screen);
		else if (selected == 14)
			miningComplex(screen);
		else if (selected == 15)
			smithingComplex(screen);
		else if (selected == 16)
			fishingComplex(screen);
		else if (selected == 17)
			cookingComplex(screen);
		else if (selected == 18)
			firemakingComplex(screen);
		else if (selected == 19)
			woodcuttingComplex(screen);
		else if (selected == 20)
			farmingComplex(screen);
		else if (selected == 21)
			hunterComplex(screen);
		else if (selected == 22)
			constructionComplex(screen);
	}

	/**
	 * @param title
	 * @param currentTab
	 * @param write []
	 * @return Used to shorten the sidebar tab texts, shortens by up to 12x,
	 * Also includes the title of the menu and the current tab
	 */
	private void optionTab(String title, String currentTab, String op1, String op2, String op3, String op4, String op5, String op6, String op7, String op8, String op9, String op10,
	                       String op11, String op12, String op13) {
		c.getPA().sendFrame126(title, 8716);
		c.getPA().sendFrame126(currentTab, 8849);
		c.getPA().sendFrame126(op1, 8846);
		c.getPA().sendFrame126(op2, 8823);
		c.getPA().sendFrame126(op3, 8824);
		c.getPA().sendFrame126(op4, 8827);
		c.getPA().sendFrame126(op5, 8837);
		c.getPA().sendFrame126(op6, 8840);
		c.getPA().sendFrame126(op7, 8843);
		c.getPA().sendFrame126(op8, 8859);
		c.getPA().sendFrame126(op9, 8862);
		c.getPA().sendFrame126(op10, 8865);
		c.getPA().sendFrame126(op11, 15303);
		c.getPA().sendFrame126(op12, 15306);
		c.getPA().sendFrame126(op13, 15309);
		c.getPA().displayInterface(8714);
	}

	/**
	 * @param levels
	 * @param lines
	 * @param ids
	 * @param lineCounter
	 * @return Used to reduce code by 3x. Contains the item on interface, Level
	 * text, and the item description, along with the line Counter to
	 * ensure it is placed in the right spot
	 */
	private void menuLine(final String levels, final String lines, final int ids, final int lineCounter) {
		c.getPA().sendFrame126(lines, 8760 + lineCounter);
		c.getPA().sendFrame126(levels, 8720 + lineCounter);

		if (0 + lineCounter > item.length - 1) {
			return;
		}
		item[0 + lineCounter] = ids;
		writeInterfaceItem(item);
	}

	/**
	 * Clears the menus
	 */
	private void clearMenu() {
		for (int i = 0; i < 39; i++) {
			item[i] = 0;
		}
		for (int i = 8720; i < 8799; i++) {
			c.getPA().sendFrame126("", i);
		}
	}

	/**
	 * @param id []
	 * @return Used to place the item on the interface
	 */
	private void writeInterfaceItem(int id[]) {
		//synchronized (c) {
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(8847); // 8847
		c.getOutStream().writeWord(id.length);
		for (int i = 0; i < id.length; i++) {
			c.getOutStream().writeByte(1);
			if (id[i] > 0) {
				c.getOutStream().writeWordBigEndianA(id[i] + 1);
			} else {
				c.getOutStream().writeWordBigEndianA(0);
			}
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		//}
	}

	/**
	 * Skill ID: 0
	 *
	 * @param screen
	 * @return
	 */
	public void attackComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Bronze", 1205, 0);
			menuLine("1", "Iron", 1203, 1);
			menuLine("5", "Steel", 1207, 2);
			menuLine("10", "Black", 1217, 3);
			menuLine("20", "Mithril", 1209, 4);
			menuLine("30", "Adamant", 1211, 5);
			menuLine("30", "Battlestaves", 1391, 6);
			menuLine("40", "Rune", 1213, 7);
			menuLine("40", "Brine sabre", 11037, 8);
			menuLine("40", "Mystic staves (with 40 Magic)", 1405, 9);
			menuLine("50", "Granite maul (with 50 Strength)", 4153, 10);
			menuLine("60", "Dragon", 1215, 11);
			menuLine("60", "Barrelchest anchor (with 40 Strength)", 10887, 12);
			menuLine("60", "Obsidian weapons", 6523, 13);
			menuLine("65", "3rd age weapons", 12426, 14);
			menuLine("70", "Crystal weaponry (with 50 Agility)", 13080, 15);
			menuLine("70", "Saradomin sword", 11838, 16);
			menuLine("70", "Zamorakian spear", 11824, 17);
			menuLine("70", "Abyssal whip", 4151, 18);
			menuLine("70", "Abyssal dagger", 13265, 19);
			menuLine("70", "Abyssal bludgeon (with 70 Strength)", 13263, 20);
			menuLine("70", "Dharok's greataxe (with 70 Strength)", 4718, 21);
			menuLine("70", "Torag's hammers (with 70 Strength)", 4747, 22);
			menuLine("70", "Verac's flail", 4755, 23);
			menuLine("70", "Guthan's warspear", 4726, 24);
			menuLine("75", "Arclight", 19675, 25);
			menuLine("75", "Godswords", 11808, 26);
			menuLine("75", "Staff of the Dead (with 75 Magic)", 11791, 27);
			menuLine("75", "Abyssal tentacle", 12006, 28);
			menuLine("75", "Blessed saradomin sword", 12809, 29);
			optionTab("Attack", "Weapons", "Weapons", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9747, 0);
			optionTab("Attack", "Milestones", "Weapons", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 1
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void strengthComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("5", "Black halberd (with 10 Attack)", 3196, 0);
			menuLine("5", "White halberd (with 10 Attack)", 6599, 1);
			menuLine("10", "Mithril halberd (with 20 Attack)", 3198, 2);
			menuLine("15", "Adamant halberd (with 30 Attack)", 3200, 3);
			menuLine("20", "Rune halberd (with 40 Attack)", 3202, 4);
			menuLine("30", "Dragon halberd (with 60 Attack)", 3204, 5);
			menuLine("40", "Barrelchest anchor (with 60 Attack)", 3204, 6);
			menuLine("50", "Granite armour (with 50 Defence)", 10589, 7);
			menuLine("50", "Granite maul (with 50 Attack)", 4153, 8);
			menuLine("60", "TzHaar-ket-om", 6528, 9);
			menuLine("70", "Dharok's greataxe (with 70 Attack)", 4718, 10);
			menuLine("70", "Torag's hammers (with 70 Attack)", 4747, 11);
			menuLine("70", "Abyssal bludgeon (with 70 Attack)", 13263, 12);
			menuLine("75", "Primordial boots (with 75 Defence)", 13239, 13);
			optionTab("Strength", "Equipment", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9750, 0);
			optionTab("Strength", "Milestones", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 2
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void defenceComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Bronze", 1139, 0);
			menuLine("1", "Iron", 1137, 1);
			menuLine("5", "Steel", 1141, 2);
			menuLine("10", "Black", 1151, 3);
			menuLine("10", "Slayer helm", 11864, 4);
			menuLine("20", "Mithril", 1143, 5);
			menuLine("20", "Yak-hide", 10822, 6);
			menuLine("20", "Initiate armour (with 10 Prayer)", 5574, 7);
			menuLine("30", "Adamantite", 1145, 8);
			menuLine("30", "Proselyte armour (with 20 Prayer)", 9672, 9);
			menuLine("35", "Samurai armour", 20035, 10);
			menuLine("40", "Rune", 1147, 11);
			menuLine("40", "Rock-shell armour", 6128, 12);
			menuLine("45", "Fremennik helmets", 3751, 13);
			menuLine("45", "Spirit shield (with 55 Prayer)", 12829, 14);
			menuLine("50", "Granite", 3122, 15);
			menuLine("55", "Helm of neitiznot", 10828, 16);
			menuLine("60", "Dragon", 1149, 17);
			menuLine("60", "Toktz-ket-xil", 6524, 18);
			menuLine("60", "Odium & Malediction wards", 11924, 19);
			menuLine("65", "Bandos armour", 11832, 20);
			menuLine("65", "3rd age melee armour", 10350, 21);
			menuLine("70", "Barrows armour", 4745, 22);
			menuLine("70", "Elf crystal (with 50 Agility)", 11759, 23);
			menuLine("70", "Armadyl armour (with 70 Ranged)", 11826, 24);
			menuLine("70", "Blessed spirit shield (with 60 Prayer)", 12831, 25);
			menuLine("75", "Dragonfire shield", 11283, 26);
			menuLine("75", "Serpentine helm", 12931, 27);
			menuLine("75", "Elysian spirit shield (with 75 Prayer)", 12817, 28);
			menuLine("75", "Arcane & Spectral spirit shields (with 70 Prayer & 65 Magic)", 12825, 29);
			menuLine("75", "Primordial boots (with 75 Strength)", 13239, 30);
			menuLine("75", "Eternal boots (with 75 Magic)", 13235, 31);
			menuLine("75", "Pegasian boots (with 75 Ranged)", 13237, 32);
			optionTab("Defence", "Armour", "Armour", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9753, 0);// Skill mastery
			optionTab("Defence", "Milestones", "Armour", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 3
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void rangedComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Standard bows", 839, 0);
			menuLine("5", "Oak bows", 845, 1);
			menuLine("20", "Willow bows", 847, 2);
			menuLine("30", "Maple bows", 851, 3);
			menuLine("30", "Ogre bows", 2883, 4);
			menuLine("30", "Ogre composite bow", 4827, 5);
			menuLine("40", "Yew bows", 855, 6);
			menuLine("50", "Magic bows", 859, 7);
			menuLine("50", "Seercull", 6724, 8);
			menuLine("60", "Dark bow", 11235, 9);
			menuLine("65", "3rd age bow", 12424, 10);
			menuLine("70", "Crystal bows (with 50 Agility)", 4212, 11);
			optionTab("Ranged", "Bows", "Bows", "Thrown", "Armour", "Crossbows", "Ballistae", "Milestones", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Bronze items", 864, 0);
			menuLine("1", "Iron items", 863, 1);
			menuLine("5", "Steel items", 865, 2);
			menuLine("10", "Black items", 869, 3);
			menuLine("20", "Mithril items", 866, 4);
			menuLine("30", "Adamantite items", 867, 5);
			menuLine("40", "Rune items", 868, 6);
			menuLine("45", "Chinchompas", 9976, 7);
			menuLine("55", "Carnivorous chinchompas", 9977, 8);
			menuLine("60", "Dragon items", 11230, 9);
			menuLine("60", "TokTz-xil-ul (obsidian rings)", 6522, 10);
			menuLine("65", "Black chinchompas", 11959, 11);
			menuLine("75", "Toxic blowpipe", 12926, 12);
			optionTab("Ranged", "Thrown", "Bows", "Thrown", "Armour", "Crossbows", "Ballistae", "Milestones", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("1", "Plain leather items", 1129, 0);
			menuLine("1", "Hardleather body (with 10 Defence)", 1131, 1);
			menuLine("20", "Studded leather body (with 20 Defence)", 1133, 2);
			menuLine("20", "Studded leather chaps", 1097, 3);
			menuLine("20", "Coif", 1169, 4);
			menuLine("30", "Snakeskin armour (with 30 Defence)", 6322, 5);
			menuLine("30", "Ava's attractor", 10498, 6);
			menuLine("40", "Ranger boots", 2577, 7);
			menuLine("40", "Robin hood hat", 2581, 8);
			menuLine("40", "Rangers' tunic", 12596, 9);
			menuLine("40", "Ranger gloves", 19994, 10);
			menuLine("40", "Spined armour", 6133, 11);
			menuLine("40", "Green dragonhide vambraces", 1065, 12);
			menuLine("40", "Green dragonhide chaps", 1099, 13);
			menuLine("40", "Green dragonhide body (with 40 Defence)", 1135, 14);
			menuLine("50", "Ava's accumulator", 10499, 15);
			menuLine("50", "Blue dragonhide vambraces", 2487, 16);
			menuLine("50", "Blue dragonhide chaps", 2493, 17);
			menuLine("50", "Blue dragonhide body(with 40 Defence)", 2499, 18);
			menuLine("60", "Red dragonhide vambraces", 2489, 19);
			menuLine("60", "Red dragonhide chaps", 2495, 20);
			menuLine("60", "Red dragonhide body(with 40 Defence)", 2501, 21);
			menuLine("65", "3rd age range armour (with 45 Defence)", 10330, 22);
			menuLine("70", "Black dragonhide vambraces", 2491, 23);
			menuLine("70", "Black dragonhide chaps", 2497, 24);
			menuLine("70", "Black dragonhide body (with 40 Defence)", 2503, 25);
			menuLine("70", "God dragonhide armour (with 40 Defence)", 10370, 26);
			menuLine("70", "Armadyl armour (with 70 Defence)", 11826, 27);
			menuLine("70", "Karil's leather armour (with 70 Defence)", 4736, 28);
			menuLine("70", "Pegasian boots (with 75 Defence)", 13237, 29);
			optionTab("Ranged", "Armour", "Bows", "Thrown", "Armour", "Crossbows", "Ballistae", "Milestones", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("1", "Crossbow", 837, 0);
			menuLine("1", "Pheonix crossbow", 767, 1);
			menuLine("40", "Runite crossbow", 9185, 2);
			menuLine("70", "Armadyl crossbow", 11785, 3);
			menuLine("70", "Karil's crossbow", 4734, 4);
			optionTab("Ranged", "Crossbows", "Bows", "Thrown", "Armour", "Crossbows", "Ballistae", "Milestones", "", "", "", "", "", "", "");
		} else if (screen == 5) {
			clearMenu();
			menuLine("30", "Light ballista (Ammo: Javelins up to adamant)", 19478, 0);
			menuLine("65", "Heavy ballista (Ammo: Javelins up to dragon)", 19481, 1);
			optionTab("Ranged", "Ballistae", "Bows", "Thrown", "Armour", "Crossbows", "Ballistae", "Milestones", "", "", "", "", "", "", "");
		} else if (screen == 6) {
			clearMenu();
			menuLine("40", "Ranging guild", 1464, 0);
			menuLine("99", "Skill mastery", 9756, 1);
			optionTab("Ranged", "Milestones", "Bows", "Thrown", "Armour", "Crossbows", "Ballistae", "Milestones", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 4
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void prayerComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Thick Skin", 1714, 0);
			menuLine("4", "Burst of Strength", 1714, 1);
			menuLine("7", "Clarity of Thought", 1714, 2);
			menuLine("8", "Sharp Eye", 1714, 3);
			menuLine("9", "Mystic Will", 1714, 4);
			menuLine("10", "Rock Skin", 1714, 5);
			menuLine("13", "Superhuman Strength", 1714, 6);
			menuLine("16", "Improved Reflexes", 1714, 7);
			menuLine("19", "Rapid Restore", 1714, 8);
			menuLine("22", "Rapid Heal", 1714, 9);
			menuLine("25", "Protect Item", 1714, 10);
			menuLine("26", "Hawk Eye", 1714, 11);
			menuLine("27", "Mystic Lore", 1714, 12);
			menuLine("28", "Steel Skin", 1714, 13);
			menuLine("31", "Ultimate Strength", 1714, 14);
			menuLine("34", "Incredible Reflexes", 1714, 15);
			menuLine("37", "Protect from Magic", 1714, 16);
			menuLine("40", "Protect from Missles", 1714, 17);
			menuLine("43", "Protect from Melee", 1714, 18);
			menuLine("44", "Eagle Eye", 1714, 19);
			menuLine("44", "Mystic Might", 1714, 20);
			menuLine("46", "Retribution", 1714, 21);
			menuLine("49", "Redemption", 1714, 22);
			menuLine("52", "Smite", 1714, 23);
			menuLine("60", "Chivalry", 1714, 24);
			menuLine("70", "Piety", 1714, 25);
			optionTab("Prayer", "Prayers", "Prayers", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("10", "Initiate armour (with 20 Defence)", 5574, 0);
			menuLine("20", "Proselte armour (with 20 Defence)", 9672, 1);
			menuLine("20", "Vestment robe top", 10458, 2);
			menuLine("20", "Vestment robe legs", 10464, 3);
			menuLine("31", "Holy wraps", 19997, 4);
			menuLine("31", "Holy sandles", 12598, 5);
			menuLine("40", "Vestment cloak", 10446, 6);
			menuLine("40", "Vestment mitre", 10452, 7);
			menuLine("50", "Enchant unholy and holy symbols", 1724, 8);
			menuLine("55", "Spirit shield (with 45 Defence)", 12829, 9);
			menuLine("60", "Vestment stole", 10470, 10);
			menuLine("60", "Crozier", 10440, 11);
			menuLine("60", "Blessed spirit shield (with 70 Defence)", 12831, 12);
			menuLine("70", "Arcane & Spectral spirit shields (with 70 Defence and 65 Magic)", 12825, 13);
			menuLine("75", "Elysian spirit shield (with 75 Defence)", 12817, 14);
			optionTab("Prayer", "Equipment", "Prayers", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("31", "Monastery", 4674, 0);
			menuLine("99", "Skill Mastery", 9759, 1);
			optionTab("Prayer", "Milestones", "Prayers", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 5
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void magicComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Wind strike", 1391, 0);
			menuLine("5", "Water strike", 1391, 1);
			menuLine("9", "Earth strike", 1391, 2);
			menuLine("13", "Fire strike", 1391, 3);
			menuLine("17", "Wind bolt", 1391, 4);
			menuLine("21", "Low level alchemy", 1391, 5);
			menuLine("23", "Water bolt", 1391, 6);
			menuLine("29", "Earth bolt", 1391, 7);
			menuLine("35", "Fire bolt", 1391, 8);
			menuLine("41", "Wind blast", 1391, 9);
			menuLine("43", "Superheat item", 1391, 10);
			menuLine("47", "Water blast", 1391, 11);
			menuLine("50", "Snare", 1391, 12);
			menuLine("53", "Earth blast", 1391, 13);
			menuLine("55", "High level alchemy", 1391, 14);
			menuLine("56", "Charge water orb", 1391, 15);
			menuLine("59", "Fire blast", 1391, 16);
			menuLine("60", "Charge earth orb", 1391, 17);
			menuLine("60", "God spells", 1391, 18);
			menuLine("62", "Wind wave", 1391, 19);
			menuLine("63", "Charge fire orb", 1391, 20);
			menuLine("65", "Water wave", 1391, 21);
			menuLine("66", "Charge air orb", 1391, 22);
			menuLine("70", "Earth wave", 1391, 23);
			menuLine("75", "Fire wave", 1391, 24);
			menuLine("79", "Entangle", 1391, 25);
			menuLine("85", "Tele block", 1391, 26);
			optionTab("Magic", "Normal", "Normal", "Ancients", "Armour", "Weapons", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("50", "Smoke rush", 4675, 0);
			menuLine("52", "Shadow rush", 4675, 1);
			menuLine("54", "Paddewwa teleport", 4675, 2);
			menuLine("56", "Blood rush", 4675, 3);
			menuLine("58", "Ice rush", 4675, 4);
			menuLine("60", "Senntisten teleport", 4675, 5);
			menuLine("62", "Smoke burst", 4675, 6);
			menuLine("64", "Shadow burst", 4675, 7);
			menuLine("66", "Kharyrll teleport", 4675, 8);
			menuLine("68", "Blood burst", 4675, 9);
			menuLine("70", "Ice burst", 4675, 10);
			menuLine("72", "Lassar teleport", 4675, 11);
			menuLine("74", "Smoke blitz", 4675, 12);
			menuLine("76", "Shadow blitz", 4675, 13);
			menuLine("78", "Dareeyak teleport", 4675, 14);
			menuLine("80", "Blood blitz", 4675, 15);
			menuLine("82", "Ice blitz", 4675, 16);
			menuLine("84", "Carrallangar teleport", 4675, 17);
			menuLine("86", "Smoke barrage", 4675, 18);
			menuLine("88", "Shadow barrage", 4675, 19);
			menuLine("90", "Annakarl teleport", 4675, 20);
			menuLine("92", "Blood barrage", 4675, 21);
			menuLine("94", "Ice barrage", 4675, 22);
			menuLine("96", "Ghorrock teleport", 4675, 23);
			optionTab("Magic", "Ancients", "Normal", "Ancients", "Armour", "Weapons", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("20", "Wizard boots", 2579, 0);
			menuLine("20", "Xerician armour (with 10 Defence)", 13385, 1);
			menuLine("40", "Mystic robes (with 20 Defence)", 4091, 2);
			menuLine("40", "Enchanted robes (with 20 Defence)", 7399, 3);
			menuLine("40", "Robes of darkness (with 20 Defence)", 20131, 3);
			menuLine("40", "Splitbark armour (with 40 Defence)", 3387, 4);
			menuLine("40", "Skeletal armour (with 40 Defence)", 6139, 5);
			menuLine("50", "Infinity robes (with 25 Defence)", 6916, 6);
			menuLine("60", "God capes", 2413, 7);
			menuLine("65", "Lunar armour (with 40 Defence)", 9097, 8);
			menuLine("65", "3rd age robes (with 30 Defence)", 10338, 9);
			menuLine("70", "Occult necklace", 12002, 10);
			menuLine("70", "Ahrims robes (with 70 Defence)", 4712, 11);
			menuLine("75", "Eternal boots (with 75 Defence)", 13235, 12);
			optionTab("Magic", "Armour", "Normal", "Ancients", "Armour", "Weapons", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("30", "Battlestaves (with 30 Attack)", 1391, 0);
			menuLine("40", "Mystic staves (with 40 Attack)", 1405, 1);
			menuLine("45", "Beginner wand", 6908, 2);
			menuLine("50", "Apprentice wand", 6910, 3);
			menuLine("50", "Ancient staff(with 50 Attack)", 4675, 4);
			menuLine("55", "Teacher wand", 6912, 5);
			menuLine("60", "Master wand", 6914, 6);
			menuLine("60", "God staves", 2415, 7);
			menuLine("60", "TokTz-Mej-Tal (with 60 Attack)", 6526, 8);
			menuLine("65", "3rd age wand", 12422, 9);
			menuLine("75", "Staff of the Dead (with 75 Attack)", 11791, 10);
			menuLine("75", "Trident of the Seas", 11907, 11);
			optionTab("Magic", "Weapons", "Normal", "Ancients", "Armour", "Weapons", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 5) {
			clearMenu();
			menuLine("66", "Magic Guild", 4675, 0);
			menuLine("99", "Skill Mastery", 9762, 1);
			optionTab("Magic", "Milestones", "Normal", "Ancients", "Armour", "Weapons", "Milestones", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 6
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void runecraftingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Air runes", 556, 0);
			menuLine("2", "Mind runes", 558, 1);
			menuLine("5", "Water runes", 555, 2);
			menuLine("6", "Mist runes", 4695, 3);
			menuLine("9", "Earth runes", 557, 4);
			menuLine("10", "Dust runes", 4696, 5);
			menuLine("13", "Mud runes", 4698, 6);
			menuLine("14", "Fire runes", 554, 7);
			menuLine("15", "Smoke runes", 4697, 8);
			menuLine("19", "Steam runes", 4694, 9);
			menuLine("20", "Body runes", 559, 10);
			menuLine("23", "Lava runes", 4699, 11);
			menuLine("27", "Cosmic runes", 564, 12);
			menuLine("35", "Chaos runes", 562, 13);
			menuLine("44", "Nature runes", 561, 14);
			menuLine("54", "Law runes", 563, 15);
			menuLine("65", "Death runes", 560, 16);
			menuLine("77", "Blood runes", 565, 17);
			menuLine("90", "Soul runes", 566, 18);
			optionTab("Runecraft", "Runes", "Runes", "Multiples", "Pouches", "Infusing", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("11", "2 Air runes per essence", 556, 0);
			menuLine("14", "2 Mind runes per essence", 558, 1);
			menuLine("19", "2 Water runes per essence", 555, 2);
			menuLine("22", "3 Air runes per essence", 556, 3);
			menuLine("26", "2 Earth runes per essence", 557, 4);
			menuLine("28", "3 Mind runes per essence", 558, 5);
			menuLine("33", "4 Air runes per essence", 556, 6);
			menuLine("35", "2 Fire runes per essence", 554, 7);
			menuLine("38", "3 Water runes per essence", 555, 8);
			menuLine("42", "4 Mind runes per essence", 558, 9);
			menuLine("44", "5 Air runes per essence", 556, 10);
			menuLine("46", "2 Body runes per essence", 559, 11);
			menuLine("52", "3 Earth runes per essence", 557, 12);
			menuLine("55", "6 Air runes per essence", 556, 13);
			menuLine("56", "5 Mind runes per essence", 558, 14);
			menuLine("57", "4 Water runes per essence", 555, 15);
			menuLine("59", "2 Cosmic runes per essence", 564, 16);
			menuLine("66", "7 Air runes per essence", 556, 17);
			menuLine("70", "6 Mind runes per essence", 558, 18);
			menuLine("74", "3 Fire runes per essence", 554, 19);
			menuLine("76", "2 Chaos runes per essence", 562, 20);
			menuLine("77", "5 Water runes per essence", 555, 21);
			menuLine("78", "8 Air runes per essence", 556, 22);
			menuLine("82", "4 Earth runes per essence", 557, 23);
			menuLine("84", "7 Mind runes per essence", 558, 24);
			menuLine("88", "9 Air runes per essence", 556, 25);
			menuLine("91", "2 Nature runes per essence", 561, 26);
			menuLine("92", "3 Body runes per essence", 559, 27);
			menuLine("95", "6 Water runes per essence", 555, 28);
			menuLine("95", "2 Law runes per essence", 563, 29);
			menuLine("98", "8 Mind runes per essence", 558, 30);
			menuLine("99", "10 Air runes per essence", 556, 31);
			menuLine("99", "2 Air runes per essence", 560, 32);
			optionTab("Runecraft", "Multiples", "Runes", "Multiples", "Pouches", "Infusing", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("1", "Small pouch: Holds 3 Essence", 5509, 0);
			menuLine("25", "Medium pouch: Holds 6 Essence", 5510, 1);
			menuLine("50", "Large pouch: Holds 9 Essence", 5512, 2);
			menuLine("75", "Giant pouch: Holds 12 Essence", 5514, 3);
			optionTab("Runecraft", "Pouches", "Runes", "Multiples", "Pouches", "Infusing", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("60", "Eternal boots (with 60 Magic)", 13235, 0);
			menuLine("60", "Pegasian boots (with 60 Magic)", 13237, 1);
			menuLine("60", "Primordial boots (with 60 Magic)", 13239, 2);
			optionTab("Runecraft", "Infusing", "Runes", "Multiples", "Pouches", "Infusing", "Milestones", "", "", "", "", "", "", "", "");
		} else if (screen == 5) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9765, 0);
			optionTab("Runecraft", "Milestones", "Runes", "Multiples", "Pouches", "Infusing", "Milestones", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 7
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void hitpointsComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("", "Hitpoints tell you how healthy your", 4049, 0);
			menuLine("", "character is. A character who reaches 0", 0, 1);
			menuLine("", "Hitpoints has died, but will reappear at", 0, 2);
			menuLine("", "their chosen respawn location.", 0, 3);
			menuLine("", "", 0, 4);
			menuLine("", "If you see any red 'hit splats' during", 4049, 5);
			menuLine("", "combat, the number shown corresponds", 0, 6);
			menuLine("", "to the number of Hitpoints lost as a", 0, 7);
			menuLine("", "result of that strike.", 0, 8);
			menuLine("", "", 0, 9);
			menuLine("", "Blue hit splats mean no damage has", 4049, 10);
			menuLine("", "been dealt.", 0, 11);
			menuLine("", "", 0, 12);
			menuLine("", "Green hit spats are poison damage.", 4049, 13);
			menuLine("", "", 0, 14);
			menuLine("", "Dark green hit splats are venom damage.", 4049, 15);
			menuLine("", "", 0, 16);
			menuLine("", "Orange hit splats are disease damage.", 4049, 17);
			optionTab("Hitpoints", "Hitpoints", "Hitpoints", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9768, 0);
			optionTab("Hitpoints", "Milestones", "Hitpoints", "Milestones", "", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 8
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void agilityComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Gnome Stronghold Agility Course", 2150, 0);
			menuLine("1", "Gnomeball game", 751, 1);
			menuLine("1", "Low-Level Agility Arena obstacles", 2996, 2);
			menuLine("10", "Draynor Village rooftop course", 11849, 3);
			menuLine("20", "Al Kharid rooftop course", 11849, 4);
			menuLine("20", "Medium-Level Agility Arena obstacles", 2996, 5);
			menuLine("25", "Werewolf Skullball game", 1061, 6);
			menuLine("30", "Varrock rooftop course", 11849, 7);
			menuLine("35", "Barbarian Outpost Agility Course", 1365, 8);
			menuLine("40", "Canifis rooftop course", 11849, 9);
			menuLine("40", "High-Level Agility Area obstacles", 2996, 10);
			menuLine("48", "Ape Atoll Agility Course", 4024, 11);
			menuLine("50", "Use crystal equipment", 6103, 12);
			menuLine("50", "Falador rooftop course", 11849, 13);
			menuLine("52", "Wilderness Course", 964, 14);
			menuLine("60", "Werewolf Agility Course", 4179, 15);
			menuLine("60", "Seers' Village rooftop course", 11849, 16);
			menuLine("70", "Pollnivneach rooftop course", 11849, 17);
			menuLine("80", "Rellekka rooftop course", 11849, 18);
			menuLine("90", "Ardougne rooftop course", 11849, 19);
			optionTab("Agility", "Courses", "Courses", "Areas", "Shortcuts", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("10", "Rope-swing to Moss Giant Island", 6518, 0);
			menuLine("12", "Stepping stones in Karamja dungeon", 6518, 1);
			menuLine("15", "Monkey bars under Edgeville", 6518, 2);
			menuLine("22", "Pipe contortion in Karamja Dungeon", 6520, 3);
			menuLine("30", "Stepping stones in south-eastern Karamja", 6518, 4);
			menuLine("34", "Pipe contortion in Karamja Dungeon", 6520, 5);
			menuLine("45", "Elf area log balance", 6519, 6);
			menuLine("49", "Yanille Dungeon contortion", 6520, 7);
			menuLine("50", "Rogues' Den(with 50 Thieving)", 6518, 8);
			menuLine("67", "Yanille Dungeon rubble climb", 6521, 9);
			optionTab("Agility", "Areas", "Courses", "Areas", "Shortcuts", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("5", "Falador Agility Shortcut", 6517, 0);
			menuLine("8", "River Crossing To Al Kharid", 6515, 1);
			menuLine("11", "Falador Wall", 6517, 2);
			menuLine("13", "Varrock South Fence Jump", 6514, 3);
			menuLine("16", "Yanille Agility Shortcut", 6516, 4);
			menuLine("20", "Coal Truck Log Balance", 6515, 5);
			menuLine("21", "Varrock Agility Shortcut", 6516, 6);
			menuLine("26", "Falador wall Crawl", 6516, 7);
			menuLine("28", "Draynor Manor Broken Railing", 6516, 8);
			menuLine("29", "Draynor Manor Stones To The Champions' Guild", 6516, 9);
			menuLine("31", "Catherby Cliff", 6515, 10);
			menuLine("32", "Ardougne Log Balance Shortcut", 6517, 11);
			menuLine("33", "Water Obelisk Island Escape", 6516, 12);
			menuLine("36", "Gnome Stronghold Shortcut", 6517, 13);
			menuLine("37", "Al Kharid Mining Pit Sliffside Scramble", 6517, 14);
			menuLine("39", "Yanille Wall", 6517, 15);
			menuLine("40", "Trollheim Easy Cliffside Scramble", 6517, 16);
			menuLine("41", "Dwarven Mine Narrow Crevice", 6517, 17);
			menuLine("42", "Trollheim Medium Cliffside Scramble", 6516, 18);
			menuLine("43", "Trollheim Advanced Cliffside Scramble", 6517, 19);
			menuLine("44", "Cosmic Temple Medium Narrow Walkway", 6517, 20);
			menuLine("46", "Trollheim Hard Cliffside Scramble", 6516, 21);
			menuLine("47", "Log Balance To The Fremennik Province", 6517, 22);
			menuLine("48", "Edgeville Dungeon To Varrock Sewers Pipe", 6515, 23);
			menuLine("51", "Karamja Crossing, South Of The Volcano", 6516, 24);
			menuLine("53", "Port Phasmatys Ectopool Shortcut", 6517, 25);
			menuLine("58", "Elven Overpass Easy Cliffside Scramble", 6517, 26);
			menuLine("59", "Slayer Tower Medium Spiked Chain Climb", 6517, 27);
			menuLine("61", "Slayer Dungon Narrow Crevice", 6517, 28);
			menuLine("62", "Trollheim Wilderness Route", 6516, 29);
			menuLine("64", "Paterdomus Temple To Morytania Shortcut", 6517, 30);
			menuLine("66", "Cosmic Temple Advanced Narrow Walkway", 6517, 31);
			menuLine("68", "Elven Overpass Medium Cliffside Scramble", 6517, 32);
			menuLine("70", "Taverly Dungeon Pipe Squeeze", 6516, 33);
			menuLine("71", "Slayer Tower Advanced Spiked Chain Climb", 6517, 34);
			menuLine("74", "Shilo Village Stepping Stone", 6514, 35);
			menuLine("80", "Taverly Dungeon Spiked Blade Jump", 6514, 36);
			menuLine("81", "Slayer Dungeon Chasm Jump", 6514, 37);
			menuLine("85", "Elven Overpass Advanced Cliff Scramble", 6517, 38);
			optionTab("Agility", "Shortcuts", "Courses", "Areas", "Shortcuts", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("50", "Crystal Equipment", 4207, 0);
			menuLine("99", "Skill Mastery", 9771, 1);
			optionTab("Agility", "Milestones", "Courses", "Areas", "Shortcuts", "Milestones", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 9
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void herbloreComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Attack potion", 121, 0);
			menuLine("5", "Anti-poison", 175, 1);
			menuLine("12", "Strength potion", 115, 2);
			menuLine("26", "Energy potion", 3010, 3);
			menuLine("38", "Prayer restore potion", 139, 4);
			menuLine("45", "Super attack potion", 145, 5);
			menuLine("52", "Super energy potion", 3018, 6);
			menuLine("55", "Super strength potion", 157, 7);
			menuLine("63", "Super restore potion", 3026, 8);
			menuLine("66", "Super defence potion", 163, 9);
			menuLine("72", "Ranging potion", 169, 10);
			menuLine("76", "Magic potion", 3042, 11);
			menuLine("77", "Stamina potion", 12625, 12);
			menuLine("78", "Zamorak brew", 189, 13);
			menuLine("81", "Saradomin brew", 6687, 14);
			menuLine("84", "Extended antifire", 11953, 15);
			menuLine("87", "Anti-venom", 12907, 16);
			menuLine("90", "Super combat potion", 12697, 17);
			menuLine("94", "Super anti-venom", 12915, 18);
			optionTab("Herblore", "Potions", "Potions", "Herbs", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Guam leaf", 249, 0);
			menuLine("5", "Marrentill", 251, 1);
			menuLine("11", "Tarromin", 253, 2);
			menuLine("20", "Harralander", 255, 3);
			menuLine("25", "Ranarr", 257, 4);
			menuLine("30", "Toadflax", 2998, 5);
			menuLine("40", "Irit leaf", 259, 6);
			menuLine("48", "Avantoe", 261, 7);
			menuLine("54", "Kwuarm", 263, 8);
			menuLine("59", "Snapdragon", 3000, 9);
			menuLine("65", "Cadantine", 265, 10);
			menuLine("67", "Lantadyme", 2481, 11);
			menuLine("70", "Dwarf weed", 267, 12);
			menuLine("75", "Torstol", 269, 13);
			optionTab("Herblore", "Herbs", "Potions", "Herbs", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9774, 0);
			optionTab("Herblore", "Milestones", "Potions", "Herbs", "Milestones", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 10
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void thievingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Man", 3241, 0);
			menuLine("10", "Farmer", 3243, 1);
			menuLine("15", "Female H.A.M. follower", 4295, 2);
			menuLine("20", "Male H.A.M. follower", 4297, 3);
			menuLine("25", "Warrior", 3245, 4);
			menuLine("32", "Rogue", 3247, 5);
			menuLine("38", "Master farmer", 5068, 6);
			menuLine("40", "Guard", 3249, 7);
			menuLine("45", "Fremennik Citizen", 3686, 8);
			menuLine("45", "Beared pollnivnian bandit", 6781, 9);
			menuLine("53", "Desert bandit", 4625, 10);
			menuLine("55", "Knight", 3251, 11);
			menuLine("55", "Pollnivnian bandit", 6782, 12);
			menuLine("65", "Watchman", 3253, 13);
			menuLine("65", "Menaphite thug", 6780, 14);
			menuLine("70", "Paladin", 3255, 15);
			menuLine("75", "Gnome", 3257, 16);
			menuLine("80", "Hero", 3259, 17);
			menuLine("85", "Elf", 6105, 18);
			optionTab("Thieving", "Pickpocket", "Pickpocket", "Stalls", "Chests", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Rotten tomato crate", 2518, 0);
			menuLine("2", "Vegetable stall", 1965, 1);
			menuLine("5", "Cake stall", 2309, 2);
			menuLine("5", "Tea stall", 1978, 3);
			menuLine("5", "Crafting stall", 1755, 4);
			menuLine("5", "Monkey food stall", 1963, 5);
			menuLine("20", "Silk stall", 950, 6);
			menuLine("22", "Wine stall", 1993, 7);
			menuLine("27", "Seed stall", 5318, 8);
			menuLine("35", "Fur stall", 958, 9);
			menuLine("42", "Fish stall", 333, 10);
			menuLine("50", "Silver stall", 2355, 11);
			menuLine("65", "Magic stall", 6422, 12);
			menuLine("65", "Scimitar stall", 1323, 13);
			menuLine("65", "Spices stall", 2007, 14);
			menuLine("75", "Gems stall", 1607, 15);
			optionTab("Thieving", "Stalls", "Pickpocket", "Stalls", "Chests", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("13", "Ardougne, Rellekka, and the Wilderness", 617, 0);
			menuLine("28", "Upstairs in Ardougne and Rellekka", 561, 1);
			menuLine("43", "Upstairs in Ardougne", 617, 2);
			menuLine("47", "Hemenster", 41, 3);
			menuLine("47", "Rellekka", 617, 4);
			menuLine("59", "Chaos Druid Tower north of Ardougne", 565, 5);
			menuLine("72", "King Lathas's castle in Ardougne", 383, 6);
			optionTab("Thieving", "Chests", "Pickpocket", "Stalls", "Chests", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("99", "Skill mastery", 9778, 0);
			optionTab("Thieving", "Milestones", "Pickpocket", "Stalls", "Chests", "Milestones", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 11
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void craftingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("21", "Vegetable sack", 5418, 0);
			menuLine("36", "Fruit basket", 5376, 1);
			optionTab("Crafting", "Weaving", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Leather gloves", 1059, 0);
			menuLine("7", "Leather boots", 1061, 1);
			menuLine("9", "Leather cowl", 1167, 2);
			menuLine("11", "Leather vambraces", 1063, 3);
			menuLine("14", "Leather body", 1129, 4);
			menuLine("18", "Leather chaps", 1095, 5);
			menuLine("28", "Hard leather body", 1131, 6);
			menuLine("35", "Broodoo shield", 6257, 7);
			menuLine("38", "Coif", 1169, 8);
			menuLine("41", "Studded body", 1133, 9);
			menuLine("44", "Studded chaps", 1097, 10);
			menuLine("45", "Snakeskin boots", 6328, 11);
			menuLine("47", "Snakeskin vambraces", 6330, 12);
			menuLine("48", "Snakeskin Bandana", 6326, 13);
			menuLine("51", "Snakeskin chaps", 6324, 14);
			menuLine("52", "Serpentine helm", 12931, 15);
			menuLine("53", "Snakeskin body", 6322, 16);
			menuLine("55", "Slayer helm", 11864, 17);
			menuLine("57", "Green dragonhide vambraces", 1065, 18);
			menuLine("60", "Green dragonhide chaps", 1099, 19);
			menuLine("63", "Green dragonhide body", 1135, 20);
			menuLine("66", "Blue dragonhide vambraces", 2487, 21);
			menuLine("68", "Blue dragonhide chaps", 2493, 22);
			menuLine("71", "Blue dragonhide body", 2499, 23);
			menuLine("73", "Red dragonhide vambraces", 2489, 24);
			menuLine("75", "Red dragonhide chaps", 2495, 25);
			menuLine("77", "Red dragonhide body", 2501, 26);
			menuLine("79", "Black dragonhide vambraces", 2491, 27);
			menuLine("82", "Black dragonhide chaps", 2497, 28);
			menuLine("84", "Black dragonhide body", 2503, 29);
			optionTab("Crafting", "Armour", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("1", "Wool", 1759, 0);
			menuLine("10", "Flax into bow strings", 1777, 1);
			menuLine("10", "Sinew into crossbow strings", 9438, 2);
			menuLine("19", "Magic tree roots into magic strings", 6038, 3);
			menuLine("30", "Yak hair into rope", 954, 4);
			optionTab("Crafting", "Spinning", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("1", "Pot", 1931, 0);
			menuLine("7", "Pie dish", 2313, 1);
			menuLine("8", "Bowl", 1923, 2);
			menuLine("19", "Plant pot", 5350, 3);
			menuLine("25", "Pot lid", 4440, 4);
			optionTab("Crafting", "Pottery", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 5) {
			clearMenu();
			menuLine("1", "Beer glass", 1919, 0);
			menuLine("4", "Candle lantern", 4527, 1);
			menuLine("12", "Oil lamp", 4525, 2);
			menuLine("26", "Oil lantern", 4535, 3);
			menuLine("33", "Vial", 229, 4);
			menuLine("42", "Fishbowl", 6667, 5);
			menuLine("46", "Glass orb", 567, 6);
			menuLine("49", "Bullseye lantern lens", 4542, 7);
			menuLine("87", "Dorgeshuun light orb", 10973, 8);
			optionTab("Crafting", "Glass", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 6) {
			clearMenu();
			menuLine("1", "Cut opal", 1609, 0);
			menuLine("3", "Polished buttons", 10496, 1);
			menuLine("5", "Gold ring", 1635, 2);
			menuLine("6", "Gold necklace", 1654, 3);
			menuLine("7", "Gold bracelet", 11068, 4);
			menuLine("8", "Gold amulet", 1673, 5);
			menuLine("13", "Cut jade", 1611, 6);
			menuLine("16", "Holy symbol", 1714, 7);
			menuLine("16", "Cut red topaz", 1613, 8);
			menuLine("17", "Unholy symbol", 1724, 9);
			menuLine("20", "Cut sapphire", 1607, 10);
			menuLine("20", "Sapphire ring", 1637, 11);
			menuLine("22", "Sapphire necklace", 1656, 12);
			menuLine("23", "Tiara", 5525, 13);
			menuLine("23", "Sapphire bracelet", 11071, 14);
			menuLine("24", "Sapphire amulet", 1675, 15);
			menuLine("27", "Cut emerald", 1605, 16);
			menuLine("27", "Emerald ring", 1639, 17);
			menuLine("29", "Emerald necklace", 1658, 18);
			menuLine("31", "Emerald amulet", 1677, 19);
			menuLine("34", "Cut ruby", 1603, 20);
			menuLine("34", "Ruby ring", 1641, 21);
			menuLine("40", "Ruby necklace", 1660, 22);
			menuLine("43", "Cut diamond", 1601, 23);
			menuLine("43", "Diamond ring", 1643, 24);
			menuLine("50", "Ruby amulet", 1679, 25);
			menuLine("55", "Cut dragonstone", 1615, 26);
			menuLine("55", "Dragonstone ring", 1645, 27);
			menuLine("56", "Diamond necklace", 1662, 28);
			menuLine("67", "Cut onyx", 6573, 29);
			menuLine("67", "Onyx ring", 6575, 30);
			menuLine("70", "Diamond amulet", 1681, 31);
			menuLine("75", "Slayer ring", 11866, 32);
			menuLine("80", "Dragonstone amulet", 1683, 33);
			menuLine("82", "Onyx necklace", 6577, 34);
			menuLine("89", "Cut zenyte", 19493, 35);
			menuLine("89", "Zenyte ring", 19538, 36);
			menuLine("90", "Onyx amulet", 6579, 37);
			menuLine("92", "Zenyte necklace", 19535, 38);
			menuLine("95", "Zenyte bracelet", 19544, 39);
			menuLine("98", "Zenyte amulet", 19541, 40);
			optionTab("Crafting", "Jewellery", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 7) {
			clearMenu();
			menuLine("54", "Water battlestaff", 1395, 0);
			menuLine("58", "Earth battlestaff", 1399, 1);
			menuLine("59", "Toxic staff of the Dead", 12904, 2);
			menuLine("59", "Trident of the Swamp", 12899, 3);
			menuLine("62", "Fire battlestaff", 1393, 4);
			menuLine("66", "Air battlestaff", 1397, 5);
			optionTab("Crafting", "Weaponry", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		} else if (screen == 8) {
			clearMenu();
			menuLine("40", "Crafting Guild", 1757, 0);
			menuLine("99", "Skill Mastery", 9780, 1);
			optionTab("Crafting", "Milestones", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Milestones", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 12
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void fletchingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Bronze arrows", 882, 0);
			menuLine("15", "Iron arrows", 884, 1);
			menuLine("30", "Steel arrows", 886, 2);
			menuLine("45", "Mithril arrows", 888, 3);
			menuLine("52", "Broad arrows", 4150, 4);
			menuLine("60", "Adamant arrows", 890, 5);
			menuLine("75", "Rune arrows", 892, 6);
			menuLine("90", "Dragon arrows", 11212, 7);
			optionTab("Fletching", "Arrows", "Arrows", "Bows", "Darts", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("5", "Shortbows", 841, 0);
			menuLine("10", "Longbows", 839, 1);
			menuLine("20", "Oak shortbows", 843, 2);
			menuLine("25", "Oak longbows", 845, 3);
			menuLine("30", "Ogre composite bows", 4827, 4);
			menuLine("35", "Willow shortbow", 849, 5);
			menuLine("40", "Willow longbow", 847, 6);
			menuLine("50", "Maple shortbow", 853, 7);
			menuLine("55", "Maple longbow", 851, 8);
			menuLine("65", "Yew shortbow", 857, 9);
			menuLine("70", "Yew longbow", 855, 10);
			menuLine("80", "Magic shortbow", 861, 11);
			menuLine("85", "Magic longbow", 859, 12);
			optionTab("Fletching", "Bows", "Arrows", "Bows", "Darts", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("1", "Bronze darts", 806, 0);
			menuLine("22", "Iron darts", 807, 1);
			menuLine("37", "Steel darts", 808, 2);
			menuLine("52", "Mithril darts", 809, 3);
			menuLine("53", "Toxic blowpipe", 12926, 4);
			menuLine("67", "Adamant darts", 810, 5);
			menuLine("81", "Rune darts", 811, 6);
			menuLine("95", "Dragon darts", 11230, 7);
			optionTab("Fletching", "Darts", "Arrows", "Bows", "Darts", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9783, 0);
			optionTab("Fletching", "Milestones", "Arrows", "Bows", "Darts", "Milestones", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 13
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void slayerComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("5", "Crawling Hand", 4133, 0);
			menuLine("7", "Cave Bug", 4521, 1);
			menuLine("10", "Cave Crawler", 4134, 2);
			menuLine("15", "Banshee", 4135, 3);
			menuLine("17", "Cave Slime", 4520, 4);
			menuLine("20", "Rockslug", 4136, 5);
			menuLine("25", "Cockatrice", 4137, 6);
			menuLine("30", "Pyrefiend", 4138, 7);
			menuLine("40", "Basalisk", 4139, 8);
			menuLine("45", "Infernal Mage", 4140, 9);
			menuLine("50", "Bloodveld", 4141, 10);
			menuLine("52", "Jelly", 4142, 11);
			menuLine("55", "Turoth", 4143, 12);
			menuLine("60", "Aberrent Spectre", 4144, 13);
			menuLine("65", "Dust Devil", 4145, 14);
			menuLine("70", "Kurask", 4146, 15);
			menuLine("72", "Skeletal Wyvern", 6811, 16);
			menuLine("75", "Gargoyle", 4147, 17);
			menuLine("80", "Nechrael", 4148, 18);
			menuLine("85", "Abyssal Demon", 4149, 19);
			menuLine("90", "Dark Beast", 6637, 20);
			optionTab("Slayer", "Monsters", "Monsters", "Equipment", "Masters", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Enchanted gem", 4155, 0);
			menuLine("1", "Bag of salt", 4161, 1);
			menuLine("1", "Ice Cooler", 6696, 2);
			menuLine("1", "Spiny Helmet", 4551, 3);
			menuLine("1", "Rock Hammer", 4162, 4);
			menuLine("10", "Facemask", 4164, 5);
			menuLine("15", "Earmuffs", 4166, 6);
			menuLine("25", "Mirror Shield(with 20 Defence)", 4156, 7);
			menuLine("32", "Fishing Explosive", 6660, 8);
			menuLine("33", "Harpie Bug Lantern", 7053, 9);
			menuLine("37", "Insulated Boots", 7159, 10);
			menuLine("42", "Slayer Gloves", 6708, 11);
			menuLine("55", "Leaf-Bladed Spear", 4158, 12);
			menuLine("55", "Broad Arrow", 4150, 13);
			menuLine("55", "Slayer's staff(with 50 Magic)", 4170, 14);
			menuLine("57", "Fungicide Spray", 7421, 15);
			menuLine("60", "Nose Peg", 4168, 16);
			optionTab("Slayer", "Equipment", "Monsters", "Equipment", "Masters", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("1", "Burthorpe", 4155, 0);
			menuLine("1", "Canifis(Level 20 Combat)", 4155, 1);
			menuLine("1", "Edgeville Dungeon(Level 40 Combat)", 4155, 2);
			menuLine("1", "Zanaris(Level 70 Combat)", 4155, 3);
			menuLine("50", "Shilo Village(Level 100 Combat)", 4155, 4);
			optionTab("Slayer", "Masters", "Monsters", "Equipment", "Masters", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9786, 0);
			optionTab("Slayer", "Milestones", "Monsters", "Equipment", "Masters", "Milestones", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 14
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void miningComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Rune essence", 1436, 0);
			menuLine("1", "Clay", 434, 1);
			menuLine("1", "Copper ore", 436, 2);
			menuLine("1", "Tin ore", 438, 3);
			menuLine("10", "Blurite ore", 668, 4);
			menuLine("10", "Limestone", 3211, 5);
			menuLine("15", "Iron ore", 440, 6);
			menuLine("20", "Elemental ore", 2892, 7);
			menuLine("20", "Silver ore", 442, 8);
			menuLine("30", "Coal", 453, 9);
			menuLine("30", "Pure essence", 7936, 10);
			menuLine("30", "Motherlode mine (lower level)", 12011, 11);
			menuLine("35", "Sandstone", 6977, 12);
			menuLine("38", "Dense essence (with 38 Crafting)", 13445, 13);
			menuLine("40", "Gold", 444, 14);
			menuLine("40", "Gem rocks", 1603, 15);
			menuLine("42", "Volcanic sulphur", 13571, 16);
			menuLine("43", "Lovakengj blast mine", 13573, 17);
			menuLine("45", "Granite", 6983, 18);
			menuLine("55", "Mithril ore", 447, 19);
			menuLine("65", "Lovakite ore", 13356, 20);
			menuLine("70", "Adamantite ore", 449, 21);
			menuLine("72", "Motherlode mine (upper level)", 12011, 22);
			menuLine("85", "Runite ore", 451, 23);
			optionTab("Mining", "Ores", "Ores", "Pickaxes", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Bronze pickaxe", 1265, 0);
			menuLine("1", "Iron pickaxe", 1267, 1);
			menuLine("6", "Steel pickaxe", 1269, 2);
			menuLine("11", "Black pickaxe", 12297, 3);
			menuLine("21", "Mithril pickaxe", 1271, 4);
			menuLine("31", "Adamant pickaxe", 1273, 5);
			menuLine("41", "Rune pickaxe", 1275, 6);
			menuLine("61", "Dragon pickaxe", 11920, 7);
			menuLine("61", "Dragon pickaxe (or)", 12797, 8);
			menuLine("75", "Infernal pickaxe", 13243, 9);
			menuLine("90", "3rd age pickaxe", 20014, 10);
			optionTab("Mining", "Pickaxes", "Ores", "Pickaxes", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("60", "Mining Guild", 447, 0);
			menuLine("99", "Skill Mastery", 9792, 1);
			optionTab("Mining", "Milestones", "Ores", "Pickaxes", "Milestones", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 15
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void smithingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Bronze (1 tin ore + 1 copper ore)", 2349, 0);
			menuLine("15", "Iron (50% chance of success)", 2351, 1);
			menuLine("20", "Silver", 2355, 2);
			menuLine("30", "Steel (2 coal + 1 iron ore)", 2353, 3);
			menuLine("40", "Gold", 2357, 4);
			menuLine("45", "Lovakite (2 coal + 1 lovakite ore)", 13354, 5);
			menuLine("50", "Mithril (4 coal + 1 mithril ore)", 2359, 6);
			menuLine("70", "Adamant (6 coal + 1 adamantite ore)", 2361, 7);
			menuLine("85", "Runite (8 coal + 1 runite ore)", 2363, 8);
			optionTab("Smithing", "Smelting", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 2) {
			clearMenu();
			String type = "Bronze";
			menuLine("1", type + " dagger - 1 Bar", 1205, 0);
			menuLine("1", type + " Hatchet - 1 Bar", 1351, 1);
			menuLine("2", type + " mace - 1 Bar", 1422, 2);
			menuLine("3", type + " Med Helm - 1 Bar", 1139, 3);
			menuLine("4", type + " sword - 1 Bar", 1277, 4);
			menuLine("4", type + " Dart Tips - 1 Bar makes 10", 819, 5);
			menuLine("4", type + " Wire - 1 Bar", 1794, 6);
			menuLine("4", type + " Nails - 1 Bar makes 15", 4819, 7);
			menuLine("5", type + " Scimitar - 2 Bars", 1321, 8);
			menuLine("5", type + " Spear - 1 Bar + 1 Log", 1237, 9);
			menuLine("5", type + " Arrowhead - 1 Bar makes 15", 39, 10);
			menuLine("6", type + " Longsword - 2 Bars", 1291, 11);
			menuLine("7", type + " full helm - 2 Bars", 1155, 12);
			menuLine("7", type + " Throwing Knife", 864, 13);
			menuLine("8", type + " square shield - 2 Bars", 1173, 14);
			menuLine("9", type + " Warhammer - 3 Bars", 1337, 15);
			menuLine("10", type + " Battleaxe - 3 Bars", 1375, 16);
			menuLine("11", type + " chainbody - 3 Bars", 1103, 17);
			menuLine("12", type + " kiteshield - 3 Bars", 1189, 18);
			menuLine("13", type + " claws(After Death Plateu) - 2 Bars", 3095, 19);
			menuLine("14", type + " Two-Handed sword - 3 Bars", 1307, 20);
			menuLine("16", type + " platelegs - 3 Bars", 1075, 21);
			menuLine("16", type + " Plateskirt - 3 Bars", 1087, 22);
			menuLine("18", type + " platebody - 5 Bars", 1117, 23);
			optionTab("Smithing", "Bronze", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 3) {
			clearMenu();
			String type = "Iron";
			menuLine("15", type + " dagger - 1 Bar", 1203, 0);
			menuLine("16", type + " Hatchet - 1 Bar", 1349, 1);
			menuLine("17", type + " mace - 1 Bar", 1420, 2);
			menuLine("17", type + " Spit - 1 Bar", 7225, 3);
			menuLine("18", type + " Med Helm - 1 Bar", 1137, 4);
			menuLine("19", type + " sword - 1 Bar", 1279, 5);
			menuLine("19", type + " Dart Tips - 1 Bar makes 10", 820, 6);
			menuLine("19", type + " Nails - 1 Bar makes 15", 4819, 7);
			menuLine("20", type + " Scimitar - 2 Bars", 1323, 8);
			menuLine("20", type + " Spear - 1 Bar + 1 Log", 1239, 9);
			menuLine("20", type + " Arrowhead - 1 Bar makes 15", 40, 10);
			menuLine("21", type + " Longsword - 2 Bars", 1293, 11);
			menuLine("22", type + " full helm - 2 Bars", 1153, 12);
			menuLine("22", type + " Throwing Knife", 863, 13);
			menuLine("23", type + " square shield - 2 Bars", 1175, 14);
			menuLine("24", type + " Warhammer - 3 Bars", 1335, 15);
			menuLine("25", type + " Battleaxe - 3 Bars", 1363, 16);
			menuLine("26", type + " chainbody - 3 Bars", 1101, 17);
			menuLine("26", "Oil Lantern Frame - 1 Bar", 4540, 18);
			menuLine("27", type + " kiteshield - 3 Bars", 1191, 19);
			menuLine("28", type + " claws(After Death Plateu) - 2 Bars", 3096, 20);
			menuLine("29", type + " Two-Handed sword - 3 Bars", 1309, 21);
			menuLine("31", type + " platelegs - 3 Bars", 1077, 22);
			menuLine("31", type + " Plateskirt - 3 Bars", 1081, 23);
			menuLine("33", type + " platebody - 5 Bars", 1115, 24);
			optionTab("Smithing", "Iron", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 4) {
			clearMenu();
			String type = "Steel";
			menuLine("30", type + " dagger - 1 Bar", 1207, 0);
			menuLine("31", type + " Hatchet - 1 Bar", 1353, 1);
			menuLine("32", type + " mace - 1 Bar", 1424, 2);
			menuLine("33", type + " Med Helm - 1 Bar", 1141, 3);
			menuLine("34", type + " sword - 1 Bar", 1281, 4);
			menuLine("34", type + " Dart Tips - 1 Bar makes 10", 821, 5);
			menuLine("34", type + " Nails - 1 Bar makes 15", 1539, 6);
			menuLine("35", type + " Scimitar - 2 Bars", 1325, 7);
			menuLine("35", type + " Spear - 1 Bar + 1 Log", 1241, 8);
			menuLine("35", type + " Arrowhead - 1 Bar makes 15", 41, 9);
			menuLine("36", type + " Longsword - 2 Bars", 1295, 10);
			menuLine("36", type + " Studs - 1 Bar", 2370, 11);
			menuLine("37", type + " full helm - 2 Bars", 1157, 12);
			menuLine("37", type + " Throwing Knife", 865, 13);
			menuLine("38", type + " square shield - 2 Bars", 1177, 14);
			menuLine("39", type + " Warhammer - 3 Bars", 1339, 15);
			menuLine("40", type + " Battleaxe - 3 Bars", 1365, 16);
			menuLine("41", type + " chainbody - 3 Bars", 1105, 17);
			menuLine("42", type + " kiteshield - 3 Bars", 1193, 18);
			menuLine("43", type + " claws(After Death Plateu) - 2 Bars", 3097, 19);
			menuLine("44", type + " Two-Handed sword - 3 Bars", 1311, 20);
			menuLine("46", type + " platelegs - 3 Bars", 1069, 21);
			menuLine("46", type + " Plateskirt - 3 Bars", 1083, 22);
			menuLine("48", type + " platebody - 5 Bars", 1119, 23);
			optionTab("Smithing", "Steel", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 5) {
			clearMenu();
			String type = "Mithril";
			menuLine("50", type + " dagger - 1 Bar", 1209, 0);
			menuLine("51", type + " Hatchet - 1 Bar", 1355, 1);
			menuLine("52", type + " mace - 1 Bar", 1428, 2);
			menuLine("53", type + " Med Helm - 1 Bar", 1143, 3);
			menuLine("54", type + " sword - 1 Bar", 1285, 4);
			menuLine("54", type + " Dart Tips - 1 Bar makes 10", 822, 5);
			menuLine("54", type + " Nails - 1 Bar makes 15", 4822, 6);
			menuLine("55", type + " Scimitar - 2 Bars", 1329, 7);
			menuLine("55", type + " Spear - 1 Bar + 1 Log", 1243, 8);
			menuLine("55", type + " Arrowhead - 1 Bar makes 15", 42, 9);
			menuLine("56", type + " Longsword - 2 Bars", 1299, 10);
			menuLine("57", type + " full helm - 2 Bars", 1159, 11);
			menuLine("57", type + " Throwing Knife", 866, 12);
			menuLine("58", type + " square shield - 2 Bars", 1181, 13);
			menuLine("59", type + " Warhammer - 3 Bars", 1343, 14);
			menuLine("60", type + " Battleaxe - 3 Bars", 1369, 15);
			menuLine("61", type + " chainbody - 3 Bars", 1109, 16);
			menuLine("62", type + " kiteshield - 3 Bars", 1197, 17);
			menuLine("63", type + " claws(After Death Plateu) - 2 Bars", 3099, 18);
			menuLine("64", type + " Two-Handed sword - 3 Bars", 1315, 19);
			menuLine("66", type + " platelegs - 3 Bars", 1071, 20);
			menuLine("66", type + " Plateskirt - 3 Bars", 1085, 21);
			menuLine("68", type + " platebody - 5 Bars", 1121, 22);
			optionTab("Smithing", "Mithril", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 6) {
			clearMenu();
			String type = "Adamant";
			menuLine("70", type + " dagger - 1 Bar", 1211, 0);
			menuLine("71", type + " Hatchet - 1 Bar", 1357, 1);
			menuLine("72", type + " mace - 1 Bar", 1430, 2);
			menuLine("73", type + " Med Helm - 1 Bar", 1145, 3);
			menuLine("74", type + " sword - 1 Bar", 1287, 4);
			menuLine("74", type + " Dart Tips - 1 Bar makes 10", 823, 5);
			menuLine("74", type + " Nails - 1 Bar makes 15", 4823, 6);
			menuLine("75", type + " Scimitar - 2 Bars", 1331, 7);
			menuLine("75", type + " Spear - 1 Bar + 1 Log", 1245, 8);
			menuLine("75", type + " Arrowhead - 1 Bar makes 15", 43, 9);
			menuLine("76", type + " Longsword - 2 Bars", 1301, 10);
			menuLine("77", type + " full helm - 2 Bars", 1161, 11);
			menuLine("77", type + " Throwing Knife", 867, 12);
			menuLine("78", type + " square shield - 2 Bars", 1183, 13);
			menuLine("79", type + " Warhammer - 3 Bars", 1343, 14);
			menuLine("80", type + " Battleaxe - 3 Bars", 1371, 15);
			menuLine("81", type + " chainbody - 3 Bars", 1111, 16);
			menuLine("82", type + " kiteshield - 3 Bars", 1199, 17);
			menuLine("83", type + " claws(After Death Plateu) - 2 Bars", 3100, 18);
			menuLine("84", type + " Two-Handed sword - 3 Bars", 1317, 19);
			menuLine("86", type + " platelegs - 3 Bars", 1073, 20);
			menuLine("86", type + " Plateskirt - 3 Bars", 1091, 21);
			menuLine("88", type + " platebody - 5 Bars", 1123, 22);
			optionTab("Smithing", "Adamantite", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 7) {
			clearMenu();
			String type = "Rune";
			menuLine("85", type + " dagger - 1 Bar", 1213, 0);
			menuLine("86", type + " Hatchet - 1 Bar", 1359, 1);
			menuLine("87", type + " mace - 1 Bar", 1432, 2);
			menuLine("88", type + " Med Helm - 1 Bar", 1147, 3);
			menuLine("89", type + " sword - 1 Bar", 1289, 4);
			menuLine("89", type + " Dart Tips - 1 Bar makes 10", 824, 5);
			menuLine("89", type + " Nails - 1 Bar makes 15", 4824, 6);
			menuLine("90", type + " Scimitar - 2 Bars", 1333, 7);
			menuLine("90", type + " Spear - 1 Bar + 1 Log", 1247, 8);
			menuLine("90", type + " Arrowhead - 1 Bar makes 15", 44, 9);
			menuLine("91", type + " Longsword - 2 Bars", 1303, 10);
			menuLine("92", type + " full helm - 2 Bars", 1163, 11);
			menuLine("92", type + " Throwing Knife", 868, 12);
			menuLine("93", type + " square shield - 2 Bars", 1185, 13);
			menuLine("94", type + " Warhammer - 3 Bars", 1347, 14);
			menuLine("95", type + " Battleaxe - 3 Bars", 1373, 15);
			menuLine("96", type + " chainbody - 3 Bars", 1113, 16);
			menuLine("97", type + " kiteshield - 3 Bars", 1201, 17);
			menuLine("98", type + " claws(After Death Plateu) - 2 Bars", 3101, 18);
			menuLine("99", type + " Two-Handed sword - 3 Bars", 1319, 19);
			menuLine("99", type + " platelegs - 3 Bars", 1079, 20);
			menuLine("99", type + " Plateskirt - 3 Bars", 1093, 21);
			menuLine("99", type + " platebody - 5 Bars", 1127, 22);
			optionTab("Smithing", "Runite", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 8) {
			clearMenu();
			menuLine("50", "Gold bowl(After Starting Legends' Quest", 721, 0);
			menuLine("50", "Gold Helmet(After starting Between a Rock...", 4567, 1);
			optionTab("Smithing", "Gold", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 9) {
			clearMenu();
			menuLine("20", "Elemental Shield(After Elemental Workshop)", 2890, 0);
			optionTab("Smithing", "Elemental", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 10) {
			clearMenu();
			menuLine("60", "Dragon square shield", 1187, 0);
			optionTab("Smithing", "Other", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		} else if (screen == 11) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9795, 0);
			optionTab("Smithing", "Milestones", "Smelting", "Bronze", "Iron", "Steel", "Mithril", "Adamantite", "Runite", "Gold", "Elemental", "Other", "Milestones", "", "");
		}
	}

	/*
	 * Skill ID: 16
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void fishingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Small net", 303, 0);
			menuLine("5", "Bait fishing", 307, 1);
			menuLine("16", "Big net fishing", 305, 2);
			menuLine("20", "Fly fishing rod", 309, 3);
			menuLine("35", "Harpoon", 311, 4);
			menuLine("40", "Lobster pot", 301, 5);
			menuLine("65", "Vessel fishing", 3157, 6);
			optionTab("Fishing", "Techniques", "Techniques", "Catches", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Shrimp - Net fishing", 317, 0);
			menuLine("5", "Sardine - Sea bait fishing", 327, 1);
			menuLine("5", "Karambwanji - Net fishing", 3150, 2);
			menuLine("10", "Herring - Sea bait fishing", 347, 3);
			menuLine("15", "Anchovie - Net fishing", 321, 4);
			menuLine("16", "Mackerel - Big net fishing", 353, 5);
			menuLine("16", "Oyster - Big net fishing", 407, 6);
			menuLine("16", "Casket - Big net fishing", 405, 7);
			menuLine("16", "Seaweed - Big net fishing", 401, 8);
			menuLine("20", "Trout - Fly fishing", 335, 9);
			menuLine("23", "Cod - Big net fishing", 341, 10);
			menuLine("25", "Pike - River bait fishing", 349, 11);
			menuLine("28", "Slimy Eel - River bait fishing", 3379, 12);
			menuLine("30", "Salmon - Fly fishing", 331, 13);
			menuLine("35", "Tuna - Harpoon fishing", 359, 14);
			menuLine("38", "Cave Eel - River bait fishing", 5001, 15);
			menuLine("40", "Lobster - Lobster pot fishing", 377, 16);
			menuLine("46", "Bass - Big net fishing", 363, 17);
			menuLine("50", "swordfish - Harpoon fishing", 371, 18);
			menuLine("53", "Lava eel - Bait fishing", 2148, 19);
			menuLine("62", "Monkfish - Net fishing", 7944, 20);
			menuLine("65", "Karambwan - Vessel fishing", 3142, 21);
			menuLine("76", "Shark - Harpoon fishing", 383, 22);
			menuLine("79", "Sea turtle - Fishing trawler", 395, 23);
			menuLine("81", "Manta ray - Fishing trawler", 389, 24);
			menuLine("90", "Anglerfish - Sandworm bait fishing", 13439, 25);
			menuLine("85", "Dark crab - Bait pot fishing", 11934, 26);
			menuLine("87", "Sacred eel - Swamp bait fishing", 13339, 27);
			optionTab("Fishing", "Catches", "Techniques", "Catches", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("68", "Fishing Guild", 385, 0);
			menuLine("99", "Skill Mastery", 9798, 1);
			optionTab("Fishing", "Milestones", "Techniques", "Catches", "Milestones", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 17
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void cookingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Meat", 2142, 0);
			menuLine("1", "Shrimp", 315, 1);
			menuLine("1", "Chicken", 2140, 2);
			menuLine("1", "Rabbit", 3228, 3);
			menuLine("1", "Anchovies", 319, 4);
			menuLine("1", "Sardine", 325, 5);
			menuLine("1", "Karambwanji", 3151, 6);
			menuLine("1", "Karambwan", 3144, 7);
			menuLine("1", "Ugthanki kebab", 1883, 8);
			menuLine("5", "Herring", 347, 9);
			menuLine("10", "Mackerel", 355, 10);
			menuLine("12", "Thin snail", 3363, 11);
			menuLine("15", "Trout", 333, 12);
			menuLine("16", "Spider", 6293, 13);
			menuLine("16", "Roasted rabbit", 7223, 14);
			menuLine("17", "Lean Snail", 3365, 15);
			menuLine("18", "Cod", 339, 16);
			menuLine("20", "Pike", 351, 17);
			menuLine("22", "Fat Snail", 3367, 18);
			menuLine("25", "Salmon", 329, 19);
			menuLine("28", "Slimy eel", 3379, 20);
			menuLine("30", "Tuna", 361, 21);
			menuLine("30", "Roasted chompy", 2878, 22);
			menuLine("31", "Fishcake", 7530, 23);
			menuLine("38", "Cave Eel", 5003, 24);
			menuLine("40", "Lobster", 379, 25);
			menuLine("41", "Jubbly", 7568, 26);
			menuLine("43", "Bass", 365, 27);
			menuLine("45", "Swordfish", 373, 28);
			menuLine("53", "Lava eel", 2149, 29);
			menuLine("62", "Monkfish", 7946, 30);
			menuLine("80", "Shark", 385, 31);
			menuLine("82", "Sea turtle", 397, 32);
			menuLine("84", "Anglerfish", 13441, 33);
			menuLine("90", "Dark crab", 11936, 34);
			menuLine("91", "Manta Ray", 391, 35);
			optionTab("Cooking", "Meats", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Bread", 2309, 0);
			menuLine("58", "Pitta Bread", 1865, 1);
			menuLine("", "To make bread:", 0, 2);
			menuLine("", "1.Pick some grain and use a hopper to make flour", 0, 3);
			menuLine("", "2.Use a pot to collect the flour you have made", 0, 4);
			menuLine("", "3.Fill a bucket or jug with water from a sink", 0, 5);
			menuLine("", "4.Mix the flour and water to make some dough", 0, 6);
			menuLine("", "5.Cook the dough by using it with a stove", 0, 7);
			optionTab("Cooking", "Bread", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 3) {
			clearMenu();
			menuLine("10", "Redberry Pie", 2325, 0);
			menuLine("20", "Meat Pie", 2327, 1);
			menuLine("29", "Mud Pie", 7170, 2);
			menuLine("30", "Apple Pie", 2323, 3);
			menuLine("34", "Garden Pie", 7178, 4);
			menuLine("47", "Fish Pie", 7188, 5);
			menuLine("70", "Admiral Pie", 7198, 6);
			menuLine("85", "Wild Pie", 7208, 7);
			menuLine("95", "Summer Pie", 7218, 8);
			menuLine("", "To make a pie:", 0, 9);
			menuLine("", "1.Mixe flour and water to make pastry dough", 0, 10);
			menuLine("", "2.Place the dough in an empty pie dish", 0, 11);
			menuLine("", "3.Use our choice of filling with the empty pie", 0, 12);
			menuLine("", "4.Cook the pie by using it with a stove", 0, 13);
			optionTab("Cooking", "Pies", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 4) {
			clearMenu();
			menuLine("25", "Stew", 2003, 0);
			menuLine("60", "Curry", 2011, 1);
			menuLine("", "To make stew:", 0, 2);
			menuLine("", "1.Obtain a bowl and fill it with water", 0, 3);
			menuLine("", "2.Pick a potato and place it in the bowl.", 0, 4);
			menuLine("", "3.Cook some meat and place it in the bowl", 0, 5);
			menuLine("", "4.Cook the stew by using it with a stove or fire", 0, 6);
			menuLine("", "To make curry:", 0, 7);
			menuLine("", "Make uncooked stew as above.", 0, 8);
			menuLine("", "Before cooking, add some spices or curry leaves", 0, 9);
			optionTab("Cooking", "Stews", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 5) {
			clearMenu();
			menuLine("35", "Plain Pizza", 2289, 0);
			menuLine("45", "Meat Pizza", 2293, 1);
			menuLine("55", "Anchovy Pizza", 2297, 2);
			menuLine("65", "Pineapple Pizza", 2301, 3);
			menuLine("", "To make a pizza:", 0, 4);
			menuLine("", "1.Mix flour and water to make a pizza base", 0, 5);
			menuLine("", "2.Add a tomato to the pizza", 0, 6);
			menuLine("", "3.Add some cheese to the pizza", 0, 7);
			menuLine("", "4.Cook the pizza by using it with a stove", 0, 8);
			menuLine("", "5.Add your choice of topping to the pizza", 0, 9);
			optionTab("Cooking", "Pizzas", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 6) {
			clearMenu();
			menuLine("40", "Cake", 1891, 0);
			menuLine("50", "Chocolate Cake", 1897, 1);
			menuLine("", "To make a cake:", 0, 2);
			menuLine("", "1.Mix flour, eggs and milk together in a cake tin", 0, 3);
			menuLine("", "2.Cook the cake by using it with a stove", 0, 4);
			menuLine("", "3.Optional:Buy some chocolate and add", 0, 5);
			menuLine("", "it to the cake to make a choclate cake", 0, 6);
			optionTab("Cooking", "Cakes", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 7) {
			clearMenu();
			menuLine("35", "Wine", 1993, 0);
			menuLine("", "To make wine:", 0, 1);
			menuLine("", "1.Fill a jug with water", 0, 2);
			menuLine("", "2.Use grapes with the jug of water", 0, 3);
			menuLine("", "3.Wait until the wine ferments", 0, 4);
			menuLine("", "4.The wine will ferment while left in your", 0, 5);
			menuLine("", "inventory or the bank", 0, 6);
			optionTab("Cooking", "Wine", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 8) {
			clearMenu();
			menuLine("20", "Nettle Tea", 1978, 0);
			menuLine("", "To make nettle tea:", 0, 1);
			menuLine("", "1.Fill a bowl with water", 0, 2);
			menuLine("", "2.Put some picked nettles into the bowl of water", 0, 3);
			menuLine("", "3.Boil the nettle-water by using it with a range", 0, 4);
			menuLine("", "4.Use the bowl of nettle tea with a cup", 0, 5);
			menuLine("", "5.If you take milk, use some milk on the tea", 0, 6);
			optionTab("Cooking", "Hot Drinks", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 9) {
			clearMenu();
			menuLine("14", "Cider(4 Apple Mush)", 5763, 0);
			menuLine("19", "Dwarven Stout(4 Hammerstone Hops)", 1913, 1);
			menuLine("24", "Asgarnian Ale(4 Asgarnian Hops)", 1905, 2);
			menuLine("29", "Greenman's Ale(4 Harralander Leaves)", 1909, 3);
			menuLine("34", "Wizard's Mind Bomb(4 Yanillian Hops)", 1907, 4);
			menuLine("39", "Dragon Bitter(4 Krandorian Hops)", 1911, 5);
			menuLine("44", "Moonlight Mead(4 Bittercap Mushrooms)", 2955, 6);
			menuLine("49", "axeman's Folly(1 Oak Root)", 5751, 7);
			menuLine("54", "Chef's Delight(4 Portions of Chocolate Dust)", 5755, 8);
			menuLine("59", "Slayer's Respite(4 Wildblood Hops)", 5759, 9);
			optionTab("Cooking", "Brewing", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 10) {
			clearMenu();
			menuLine("7", "Baked Potato", 6701, 0);
			menuLine("9", "Spicy Sauce(Topping Ingredient)", 7072, 1);
			menuLine("11", "Chilli Con Carne(Topping)", 7062, 2);
			menuLine("13", "Scrambled Egg(Topping Ingredient)", 7078, 3);
			menuLine("23", "Scrambled Egg and Tomato(Topping)", 7064, 4);
			menuLine("28", "Sweetcorn", 5988, 5);
			menuLine("39", "Baked Potato with Butter", 6703, 6);
			menuLine("41", "Baked Potato with Chilli Con Carne", 7054, 7);
			menuLine("42", "Fried Onion(Topping Ingredient)", 7084, 8);
			menuLine("46", "Fried Mushroom(Topping Ingredient)", 7082, 9);
			menuLine("47", "Baked Potato with Butter and Cheese", 6705, 10);
			menuLine("51", "Baked Potato with Egg and Tomato", 7056, 11);
			menuLine("57", "Fried Mushroom and Onion(Topping)", 7066, 12);
			menuLine("64", "Baked Potato with Mushroom and Onion", 7058, 13);
			menuLine("67", "Tuna and Sweetcorn(Topping)", 7068, 14);
			menuLine("68", "Baked Potato with Tuna and Sweetcorn", 7060, 15);
			menuLine("", "To make baked potatoes with toppings:", 0, 16);
			menuLine("", "1.Bake the potato on a range", 0, 17);
			menuLine("", "2.Add some butter", 0, 18);
			menuLine("", "3.If needed, combine topping ingredients", 0, 19);
			menuLine("", "by chopping them into a bowl", 0, 20);
			menuLine("", "Ingredients for toppings:", 0, 21);
			menuLine("", "1.Chilli con carne: Meat & spicy sauce", 0, 22);
			menuLine("", "2.Egg and tomato: Scrambled egg & tomato", 0, 23);
			menuLine("", "3.Mushroom and onion: Fried mushroom & onion", 0, 24);
			menuLine("", "4.Tuna and sweetcorn: Tuna & cooked sweetcorn", 0, 25);
			optionTab("Cooking", "Potatoes", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 11) {
			clearMenu();
			menuLine("4", "Chocolate Milk", 1977, 0);
			menuLine("21", "Cream", 2130, 1);
			menuLine("38", "Butter", 6697, 2);
			menuLine("48", "Cheese", 1985, 3);
			menuLine("", "To make churned dairy products:", 0, 4);
			menuLine("", "1.Get a bucket of milk, a pot of cream or butter", 0, 5);
			menuLine("", "2.Use the milk, cream or butter in a churn", 0, 6);
			menuLine("", "3.Milk can be churned into cream, ", 0, 7);
			menuLine("", "then into butter, then into cheese", 0, 8);
			optionTab("Cooking", "Dairy", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 12) {
			clearMenu();
			menuLine("6", "Fruit Blast", 2034, 0);
			menuLine("8", "Pineapple Punch", 2036, 1);
			menuLine("10", "Toad Crunchies", 2217, 2);
			menuLine("12", "Spicy Crunchies", 2213, 3);
			menuLine("14", "Worm Crunchies", 2237, 4);
			menuLine("16", "Chocolate Chip Crunchies", 2239, 5);
			menuLine("18", "Wizard Blizzard", 2040, 6);
			menuLine("20", "Short Green Guy(SGG)", 2038, 7);
			menuLine("25", "Fruit Batta", 2225, 8);
			menuLine("26", "Toad Batta", 2221, 9);
			menuLine("27", "Worm Batta", 2219, 10);
			menuLine("28", "Vegetable Batta", 2227, 11);
			menuLine("29", "Cheese and Tomato Batta", 2223, 12);
			menuLine("30", "Worm Hole", 2191, 13);
			menuLine("32", "Drunk Dragon", 2032, 14);
			menuLine("33", "Chocolate Saturday", 2030, 15);
			menuLine("35", "Vegetable Ball", 2195, 16);
			menuLine("37", "Blurberry Special", 2028, 17);
			menuLine("40", "Tangled Toads' Legs", 2187, 18);
			menuLine("42", "Chocolate Bomb", 2185, 19);
			optionTab("Cooking", "Gnome", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		} else if (screen == 13) {
			clearMenu();
			menuLine("32", "Chefs' Guild", 1949, 0);
			menuLine("99", "Skill Mastery", 9801, 1);
			optionTab("Cooking", "Milestones", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Potatoes", "Diary", "Gnome", "Milestones");
		}
	}

	/*
	 * Skill ID: 18
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void firemakingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Normal logs", 1511, 0);
			menuLine("1", "Achey logs", 2862, 1);
			menuLine("5", "Pyre logs", 3438, 2);
			menuLine("15", "Oak logs", 1521, 3);
			menuLine("20", "Oak pyre logs", 3440, 4);
			menuLine("30", "Willow logs", 1519, 5);
			menuLine("35", "Teak logs", 6333, 6);
			menuLine("35", "Willow pyre logs", 3442, 7);
			menuLine("40", "Teak pyre logs", 6211, 8);
			menuLine("45", "Maple logs", 1517, 9);
			menuLine("50", "Mahogany logs", 6332, 10);
			menuLine("50", "Maple pyre logs", 3444, 11);
			menuLine("55", "Mahogany pyre logs", 6213, 12);
			menuLine("60", "Yew logs", 1515, 13);
			menuLine("65", "Yew pyre logs", 3446, 14);
			menuLine("75", "Magic logs", 1513, 15);
			menuLine("80", "Magic pyre logs", 3448, 16);
			menuLine("90", "Redwood logs", 19669, 17);
			menuLine("95", "Redwood pyre logs", 19672, 18);
			optionTab("Firemaking", "Burning", "Burning", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Tinderbox", 590, 0);
			optionTab("Firemaking", "Equipment", "Burning", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9804, 0);
			optionTab("Firemaking", "Milestones", "Burning", "Equipment", "Milestones", "", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 19
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void woodcuttingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Normal trees", 1511, 0);
			menuLine("1", "Achey trees", 2862, 1);
			menuLine("10", "Light jungle", 6281, 2);
			menuLine("15", "Oak trees", 1521, 3);
			menuLine("20", "Medium jungle", 6283, 4);
			menuLine("30", "Willow trees", 1519, 5);
			menuLine("35", "Dense jungle", 6285, 6);
			menuLine("35", "Teak trees", 6333, 7);
			menuLine("45", "Maple trees", 1517, 8);
			menuLine("45", "Hollow trees", 3239, 9);
			menuLine("50", "Mahogany trees", 6332, 10);
			menuLine("54", "Arctic pine trees", 10810, 11);
			menuLine("60", "Yew trees", 1515, 12);
			menuLine("75", "Magic trees", 1513, 13);
			menuLine("90", "Redwood trees", 19669, 14);
			optionTab("Woodcutting", "Trees", "Trees", "Hatchets", "Canoes", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Bronze axe", 1351, 0);
			menuLine("1", "Iron axe", 1349, 1);
			menuLine("6", "Steel axe", 1353, 2);
			menuLine("6", "Black axe", 1361, 3);
			menuLine("21", "Mithril axe", 1355, 4);
			menuLine("31", "Adamant axe", 1357, 5);
			menuLine("41", "Rune axe", 1359, 6);
			menuLine("61", "Dragon axe", 6739, 7);
			menuLine("75", "Infernal axe", 13241, 8);
			menuLine("90", "3rd axe axe", 20011, 9);
			optionTab("Woodcutting", "Hatchets", "Trees", "Hatchets", "Canoes", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("12", "Log Canoe", 7414, 0);
			menuLine("27", "Dugout Canoe", 7414, 1);
			menuLine("42", "Stable Dugout Canoe", 7414, 2);
			menuLine("57", "Waka Canoe", 7414, 3);
			optionTab("Woodcutting", "Canoes", "Trees", "Hatchets", "Canoes", "Milestones", "", "", "", "", "", "", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9807, 0);
			optionTab("Woodcutting", "Milestones", "Trees", "Hatchets", "Canoes", "Milestones", "", "", "", "", "", "", "", "", "");
		}
	}

	/*
	 * Skill ID: 20
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void farmingComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Potato", 1942, 0);
			menuLine("5", "Onion", 1957, 1);
			menuLine("7", "Cabbage", 1965, 2);
			menuLine("12", "Tomato", 1982, 3);
			menuLine("20", "Sweetcorn", 5986, 4);
			menuLine("31", "Strawberry", 5504, 5);
			menuLine("47", "Watermelon", 5982, 6);
			optionTab("Farming", "Allotments", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("3", "Barley", 6006, 0);
			menuLine("4", "Hammerstone Hop", 5994, 1);
			menuLine("8", "Asgarnian Hop", 5996, 2);
			menuLine("13", "Jute Plant", 5931, 3);
			menuLine("16", "Yanillian Hop", 5998, 4);
			menuLine("21", "Krandorian Hop", 6000, 5);
			menuLine("28", "Wildblood Hop", 6002, 6);
			optionTab("Farming", "Allotments", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("15", "Oak Tree", 1521, 0);
			menuLine("30", "Willow Tree", 1519, 1);
			menuLine("45", "Maple Tree", 1517, 2);
			menuLine("60", "Yew Tree", 1515, 3);
			menuLine("75", "Magic Tree", 1513, 4);
			optionTab("Farming", "Trees", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("27", "Apple Tree", 1955, 0);
			menuLine("33", "Banana Tree", 1963, 1);
			menuLine("39", "Orange Tree", 2108, 2);
			menuLine("42", "Curry Tree", 5970, 3);
			menuLine("51", "Pineapple Plant", 2114, 4);
			menuLine("57", "Papaya Tree", 5972, 5);
			menuLine("68", "Palm Tree", 5974, 6);
			optionTab("Farming", "Fruit Trees", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 5) {
			clearMenu();
			menuLine("10", "Redberry Bush", 1951, 0);
			menuLine("22", "Cadavaberry Bush", 753, 1);
			menuLine("36", "Dwellberry Bush", 2126, 2);
			menuLine("48", "Jangerberry Bush", 247, 3);
			menuLine("59", "White Berry Bush", 239, 4);
			menuLine("70", "Poison Ivy Bush", 6018, 5);
			optionTab("Farming", "Bushes", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 6) {
			clearMenu();
			menuLine("2", "Marigold(Protects low level crops from Disease)", 6010, 0);
			menuLine("11", "Rosemary(Protects Cabbages from Disease)", 6014, 1);
			menuLine("24", "Nasturtium(Protects Watermelons from Disease)", 6012, 2);
			menuLine("25", "Woad", 1793, 3);
			menuLine("26", "Limpwurt", 225, 4);
			optionTab("Farming", "Flowers", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 7) {
			clearMenu();
			menuLine("9", "Guam", 249, 0);
			menuLine("14", "Marrentill", 251, 1);
			menuLine("19", "Tarromin", 253, 2);
			menuLine("26", "Harralander", 255, 3);
			menuLine("32", "Ranarr", 257, 4);
			menuLine("38", "Toadflax", 2998, 5);
			menuLine("44", "Irit", 259, 6);
			menuLine("50", "Avantoe", 261, 7);
			menuLine("56", "Kwuarm", 263, 8);
			menuLine("62", "Snapdragon", 3000, 9);
			menuLine("67", "Cadantine", 265, 10);
			menuLine("73", "Lantadyme", 2481, 11);
			menuLine("79", "Dwarf Weed", 267, 12);
			menuLine("85", "Torstol", 269, 13);
			optionTab("Farming", "Herbs", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 8) {
			clearMenu();
			menuLine("55", "Cactus", 6016, 0);
			menuLine("63", "Belladonna", 5281, 1);
			menuLine("72", "Calquat Tree", 5980, 2);
			menuLine("83", "Spirit Tree", 6063, 3);
			optionTab("Farming", "Special", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 9) {
			clearMenu();
			menuLine("23", "Able to make and place a scarecrow", 6059, 0);
			menuLine("", "", 0, 1);
			menuLine("", "Scarecrows help to stop sweetcorn from", 0, 2);
			menuLine("", "being attacked by birds, while also", 0, 3);
			menuLine("", "helping to prevent disease", 0, 4);
			menuLine("", "", 0, 5);
			menuLine("", "How to make a scarecrow:", 0, 6);
			menuLine("", "", 0, 7);
			menuLine("", "1.Fill an empty sack with straw.", 0, 8);
			menuLine("", "2.Drive a hay sack onto a bronze spear", 0, 9);
			menuLine("", "3.Place a watermelon at the top as a head", 0, 10);
			menuLine("", "4.Stand the scarecrow in a flower patch", 0, 11);
			optionTab("Farming", "Scarecrows", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		} else if (screen == 10) {
			clearMenu();
			menuLine("99", "Skill Mastery", 9810, 0);
			optionTab("Farming", "Milestones", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Milestones", "", "", "");
		}
	}

	/*
	 * Skill ID: 21
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void hunterComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Polar kebbits (snow)", 9953, 0);
			menuLine("3", "Common kebbits (woodland)", 9954, 1);
			menuLine("7", "Feldip weasels (jungle)", 9955, 2);
			menuLine("13", "Desert devils (desert)", 9956, 3);
			menuLine("49", "Razor-backed kebbits (woodland)", 9957, 4);
			optionTab("Hunter", "Tracking", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 2) {
			clearMenu();
			menuLine("1", "Crimson swift (jungle)", 9965, 0);
			menuLine("5", "Golden warblers (desert)", 9968, 1);
			menuLine("9", "Copper longtails (woodland)", 9966, 2);
			menuLine("11", "Cerulean twitches (snow)", 9967, 3);
			menuLine("19", "Tropical wagtails (jungle)", 9969, 4);
			optionTab("Hunter", "Birds", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 3) {
			clearMenu();
			menuLine("17", "Baby implings", 11238, 0);
			menuLine("22", "Young implings", 11240, 1);
			menuLine("28", "Gourmet implings", 11242, 2);
			menuLine("36", "Earth implings", 11244, 3);
			menuLine("42", "Essence implings", 11246, 4);
			menuLine("50", "Ecletic implings", 11248, 5);
			menuLine("58", "Nature implings", 11250, 6);
			menuLine("65", "Magpie implings", 11252, 7);
			menuLine("74", "Ninja implings", 11254, 8);
			menuLine("83", "Dragon implings", 11256, 9);
			optionTab("Hunter", "Butterfly Net", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 4) {
			clearMenu();
			menuLine("23", "Wild kebbits (woodland)", 9962, 0);
			menuLine("", "Preferred bait: Raw meat", 0, 1);
			menuLine("33", "Barb-tailed kebbits (jungle)", 9958, 2);
			menuLine("", "Preferred bait: Raw rainbow fish", 0, 3);
			menuLine("38", "Prickly kebbits (northern woodland)", 9957, 4);
			menuLine("", "Preferred bait: Barley", 0, 5);
			menuLine("51", "Sabre-toothed kebbits (snow)", 9959, 6);
			menuLine("", "Preferred bait: Raw meat", 0, 7);
			optionTab("Hunter", "Deadfall", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 5) {
			clearMenu();
			menuLine("27", "Ferrets (woodland)", 10092, 0);
			menuLine("", "(After Eagles' Peak quest)", 0, 1);
			menuLine("53", "Chinchompas (woodland)", 9976, 2);
			menuLine("63", "Red chinchompas (jungle)", 9977, 3);
			menuLine("73", "Black chinchompas (wilderness)", 11959, 4);
			optionTab("Hunter", "Box Trap", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 6) {
			clearMenu();
			menuLine("29", "Swamp lizards (swamp)", 10149, 0);
			menuLine("", "Preferred bait: Guam tar", 0, 1);
			menuLine("47", "Orange salamanders (desert)", 10146, 2);
			menuLine("", "Preferred bait: Marrental tar", 0, 3);
			menuLine("59", "Red salamanders (lava)", 10147, 4);
			menuLine("", "Preferred bait: Tarromin tar", 0, 5);
			menuLine("67", "Black salamanders (lava)", 10148, 6);
			menuLine("", "Preferred bait: Harralander tar", 0, 7);
			optionTab("Hunter", "Net Trap", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 7) {
			clearMenu();
			menuLine("31", "Spined larupias (jungle)", 10045, 0);
			menuLine("41", "Horned graahks (karamja)", 10051, 1);
			menuLine("55", "Sabre-toothed kyatts (snow)", 10039, 2);
			optionTab("Hunter", "Pit Falls", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 8) {
			clearMenu();
			menuLine("43", "Spotted kebbits (woodland)", 9964, 0);
			menuLine("57", "Dark kebbits (woodland)", 9963, 1);
			menuLine("69", "Dashing kebits (woodland)", 9964, 2);
			optionTab("Hunter", "Falconry", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 9) {
			clearMenu();
			menuLine("71", "Imps (worldwide)", 9952, 0);
			menuLine("", "Preferred bait: Magical beads", 0, 1);
			optionTab("Hunter", "Imp Box", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 10) {
			clearMenu();
			menuLine("27", "White rabits (woodland)", 9975, 0);
			menuLine("", "Use a ferret to flush the rabbit", 0, 1);
			menuLine("", "out of its hole", 0, 2);
			optionTab("Hunter", "Rabbits", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 11) {
			clearMenu();
			menuLine("27", "Giant eagle (various)", 9974, 0);
			optionTab("Hunter", "Lasso", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 12) {
			clearMenu();
			menuLine("1", "Lay 1 trap at a time", 9951, 0);
			menuLine("1", "Set a bird trap", 10006, 1);
			menuLine("1", "Use a noose wand in tracking", 10150, 2);
			menuLine("15", "Net butterflies", 10010, 3);
			menuLine("20", "Lay up to 2 traps at a time", 9951, 4);
			menuLine("23", "Set a deadfall trap (limit of 1)", 1511, 5);
			menuLine("27", "Set a box trap", 10008, 6);
			menuLine("27", "Set a rabbit snare", 10031, 7);
			menuLine("29", "Set a net trap", 303, 8);
			menuLine("31", "Set a pitfall trap", 10029, 9);
			menuLine("39", "Use smoke to mask the scent on a trap", 594, 10);
			menuLine("40", "Lay up to 3 traps at a time", 9951, 11);
			menuLine("43", "Hunt with a falcon", 10023, 12);
			menuLine("60", "Lay up to 4 traps at a time", 9951, 13);
			menuLine("71", "Set a imp trap", 10025, 14);
			menuLine("80", "Lay up to 5 traps at a time", 9951, 15);
			optionTab("Hunter", "Traps", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		} else if (screen == 13) {
			clearMenu();
			menuLine("1", "Polar kebbit fur clothing (snow)", 10065, 0);
			menuLine("1", "Common kebbit fur clothing (woodland)", 10053, 1);
			menuLine("4", "Feldip weasel fur clothing (jungle)", 10057, 2);
			menuLine("10", "Desert devil fur clothing (desert)", 10061, 3);
			menuLine("24", "Lucky rabbit's foot", 10132, 4);
			menuLine("28", "Larupia fur clothing", 10045, 5);
			menuLine("38", "Graahk hide clothing", 10051, 6);
			menuLine("40", "Spotted capes", 10069, 7);
			menuLine("52", "Kyatt fur clothing", 10039, 8);
			menuLine("54", "Gloves of silence", 10075, 9);
			menuLine("66", "Spottier capes", 10071, 10);
			optionTab("Hunter", "Clothing", "Tracking", "Birds", "B. Net", "Deadfall", "Box Trap", "Net Trap", "Pit Falls", "Falconry", "Imp Box", "Rabbits", "Lasso", "Traps",
			          "Clothing");
		}
	}

	/*
	 * Skill ID: 22
	 *
	 * @param screen
	 *
	 * @return
	 */
	public void constructionComplex(int screen) {
		if (screen == 1) {
			clearMenu();
			menuLine("1", "Garden", 8415, 0);
			menuLine("1", "Parlour", 8395, 1);
			menuLine("5", "Kitchen", 8396, 2);
			menuLine("10", "Dining room", 8397, 3);
			menuLine("15", "Workshop", 8406, 4);
			menuLine("20", "Bedroom", 8398, 5);
			menuLine("25", "Hall (skill trophies)", 8401, 6);
			menuLine("30", "Games room", 8399, 7);
			menuLine("32", "Combat room", 8400, 8);
			menuLine("35", "Hall (quest trophies)", 8402, 9);
			menuLine("40", "Study", 8407, 10);
			menuLine("42", "Costume room", 9842, 11);
			menuLine("45", "Chapel", 8405, 12);
			menuLine("50", "Portal chamber", 8408, 13);
			menuLine("55", "Formal garden", 8416, 14);
			menuLine("60", "Throne room", 8409, 15);
			menuLine("65", "Oubliette", 8410, 16);
			menuLine("70", "Dungeon", 8411, 17);
			menuLine("75", "Treasure room", 8414, 18);
			optionTab("Construction", "Rooms", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 2) {
			clearMenu();
			menuLine("3", "Clay fireplace", 8325, 0);
			menuLine("5", "Firepit", 8216, 1);
			menuLine("7", "Pump and drain", 8230, 2);
			menuLine("11", "Firepit with hook", 8217, 3);
			menuLine("15", "Repair bench", 8389, 4);
			menuLine("16", "Plumbing stand", 8392, 5);
			menuLine("16", "Crafting table 1", 8380, 6);
			menuLine("17", "Firepit with pot", 8218, 7);
			menuLine("17", "Wooden workbench", 8375, 8);
			menuLine("24", "Small oven", 8219, 9);
			menuLine("25", "Crafting table 2", 8381, 10);
			menuLine("27", "Pump and tub", 8231, 11);
			menuLine("29", "Large oven", 8220, 12);
			menuLine("32", "Oak workbench", 8376, 13);
			menuLine("33", "Stone fireplace", 8326, 14);
			menuLine("34", "Crafting table 3", 8382, 15);
			menuLine("34", "Steel range", 8221, 16);
			menuLine("35", "Whetstone", 8390, 17);
			menuLine("41", "Shield easel", 8393, 18);
			menuLine("42", "Fancy range", 8222, 19);
			menuLine("42", "Crafting table 4", 8383, 20);
			menuLine("46", "Steel framed workbench", 8377, 21);
			menuLine("55", "Armor stand", 8391, 22);
			menuLine("62", "Workbench with vice", 8378, 23);
			menuLine("63", "Marble fireplace", 8327, 24);
			menuLine("66", "Banner easel", 8394, 25);
			menuLine("77", "Workbench with lathe", 8379, 26);
			optionTab("Construction", "Skills", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 3) {
			clearMenu();
			menuLine("1", "Crude wooden chair", 8309, 0);
			menuLine("8", "Wooden chair", 8310, 1);
			menuLine("10", "Wooden dining table", 8115, 2);
			menuLine("10", "Wooden dining bench", 8108, 3);
			menuLine("12", "Wooden kitchen table", 8246, 4);
			menuLine("14", "Rocking chair", 8311, 5);
			menuLine("19", "Oak chair", 8312, 6);
			menuLine("20", "Wooden bed", 8031, 7);
			menuLine("22", "Oak dining table", 8116, 8);
			menuLine("22", "Oak dining bench", 8109, 9);
			menuLine("26", "Oak armchair", 8313, 10);
			menuLine("30", "Oak bed", 8032, 11);
			menuLine("31", "Carved oak dining table", 8117, 12);
			menuLine("31", "Carved oak dining bench", 8110, 13);
			menuLine("32", "Oak kitcen table", 8247, 14);
			menuLine("34", "Large oak bed", 8033, 15);
			menuLine("35", "Teak armchair", 8314, 16);
			menuLine("38", "Teak dining table", 8118, 17);
			menuLine("38", "Teak dining bench", 8111, 18);
			menuLine("40", "Teak bed", 8034, 19);
			menuLine("44", "Carved teak dining bench", 8112, 20);
			menuLine("44", "Carved teak dining table", 8119, 21);
			menuLine("45", "Large teak bed", 8035, 22);
			menuLine("50", "Mahogany armchair", 8315, 23);
			menuLine("52", "Mahogany dining table", 8120, 24);
			menuLine("52", "Mahogany dining bench", 8113, 25);
			menuLine("52", "Teak kitchen table", 8248, 26);
			menuLine("52", "Mahogany four-poster bed", 8036, 27);
			menuLine("60", "Oak throne", 8357, 28);
			menuLine("60", "Gilded Mahogany four-poster bed", 8037, 29);
			menuLine("61", "Gilded Mahogany dining bench", 8114, 30);
			menuLine("67", "Teak throne", 8358, 31);
			menuLine("72", "Gilded mohagany and marble table", 8121, 32);
			menuLine("74", "Mohagany throne", 8359, 33);
			menuLine("81", "Gilded Mohagany throne", 8360, 34);
			menuLine("88", "Skeleton throne", 8361, 35);
			menuLine("95", "Crystal throne", 8362, 36);
			menuLine("99", "Demonic throne", 8363, 37);
			optionTab("Construction", "Surfaces", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 4) {
			clearMenu();
			menuLine("4", "Wooden bookcase", 8319, 0);
			menuLine("6", "Wooden shelves 1", 8223, 1);
			menuLine("7", "Beer barrel", 8239, 2);
			menuLine("9", "Woodel larder", 8233, 3);
			menuLine("12", "Cider barrel", 8240, 4);
			menuLine("12", "Wooden shelves 2", 8224, 5);
			menuLine("15", "Tool store 1", 8384, 6);
			menuLine("18", "Asgarnian Ale barrel", 8241, 7);
			menuLine("20", "Shoe box", 8038, 8);
			menuLine("21", "Wooden shaving stand", 8045, 9);
			menuLine("23", "Wooden shelves 3", 8225, 10);
			menuLine("25", "Tool store 2", 8385, 11);
			menuLine("26", "Greenman's Ale barrel", 8242, 12);
			menuLine("27", "Oak chest of draws", 8039, 13);
			menuLine("29", "Oak shaving stand", 8046, 14);
			menuLine("29", "Oak bookcase", 8320, 15);
			menuLine("33", "Oak larder", 8234, 16);
			menuLine("34", "Oak shelves 1", 8226, 17);
			menuLine("35", "Tool store 3", 8386, 18);
			menuLine("36", "Dragon Bitter barrel", 8243, 19);
			menuLine("37", "Oak dresser", 8047, 20);
			menuLine("39", "Oak wardrobe (bedroom)", 8040, 21);
			menuLine("40", "Mahogany bookcase", 8321, 22);
			menuLine("43", "Teak larder", 8235, 23);
			menuLine("44", "Tool store 4", 8387, 24);
			menuLine("45", "Oak shelves 2", 8227, 25);
			menuLine("46", "Teak dresser", 8048, 26);
			menuLine("48", "Chef's Delight barrel", 8244, 27);
			menuLine("51", "Teak chest of draws", 8041, 28);
			menuLine("55", "Tool store 5", 8388, 29);
			menuLine("56", "Fancy teak dresser", 8049, 30);
			menuLine("56", "Teak shelves 1", 8228, 31);
			menuLine("63", "Teak wardobe (bedroom)", 8042, 32);
			menuLine("64", "Mahogany dresser", 8050, 33);
			menuLine("67", "Teak shelves 2", 8229, 34);
			menuLine("74", "Gilded mohagany dresser", 8051, 35);
			menuLine("75", "Mahogany wardrobe (bedroom)", 8043, 36);
			menuLine("87", "Gilded mahogany wardrobe (bedroom)", 8044, 37);
			optionTab("Construction", "Storage", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 5) {
			clearMenu();
			menuLine("2", "Brown rug", 8316, 0);
			menuLine("2", "Torn curtains", 8322, 1);
			menuLine("13", "Rug", 8317, 2);
			menuLine("18", "Curtains", 8323, 3);
			menuLine("25", "Oak clock", 8052, 4);
			menuLine("40", "Oak lectern", 8334, 5);
			menuLine("40", "Opulent curtains", 8324, 6);
			menuLine("41", "Globe", 8341, 7);
			menuLine("43", "Alchemical chart", 8354, 8);
			menuLine("44", "Wooden telescope", 8348, 9);
			menuLine("47", "Oak eagle lectern", 8335, 10);
			menuLine("47", "Oak demon lectern", 8336, 11);
			menuLine("50", "Ornamental globe", 8342, 12);
			menuLine("55", "Teak clock", 8053, 13);
			menuLine("57", "Teak eagle lectern", 8337, 14);
			menuLine("57", "Teak demon lectern", 8338, 15);
			menuLine("59", "Lunar globe", 8343, 16);
			menuLine("63", "Astranomical chart", 8355, 17);
			menuLine("64", "Teak telescope", 8349, 18);
			menuLine("65", "Opulent rug", 8318, 19);
			menuLine("67", "Mahogany eagle lectern", 8339, 20);
			menuLine("67", "Mahogany demon lectern", 8340, 21);
			menuLine("68", "Celestial globe", 8344, 22);
			menuLine("72", "Dungeon candles", 8128, 23);
			menuLine("72", "Decorative dungeon blood stain", 8125, 24);
			menuLine("77", "Armillary sphere", 8345, 25);
			menuLine("83", "Infernal chart", 8356, 26);
			menuLine("83", "Decorative dungeon pipe", 8126, 27);
			menuLine("84", "Dungeon torches", 8129, 28);
			menuLine("84", "Mahogany 'scope", 8350, 29);
			menuLine("85", "Gilded mahogany clock", 8054, 30);
			menuLine("86", "Small orrery", 8346, 31);
			menuLine("94", "Hanging dungeon skeleton", 8127, 32);
			menuLine("94", "Dungeon skull torches", 8130, 33);
			menuLine("95", "Large orrery", 8347, 34);
			optionTab("Construction", "Decorative", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other",
			          "Cost. Room", "");
		} else if (screen == 6) {
			clearMenu();
			menuLine("16", "Oak wall decoration", 1, 0);
			menuLine("28", "Suit of armour", 1, 1);
			menuLine("35", "Small portrait", 1, 2);
			menuLine("36", "Mounted bass", 1, 3);
			menuLine("36", "Teak wall decoration", 1, 4);
			menuLine("38", "Minor slayer monster head", 1, 5);
			menuLine("38", "Small map", 1, 6);
			menuLine("41", "Rune display case", 1, 7);
			menuLine("42", "Mounted sword", 1, 8);
			menuLine("44", "Small landscape", 1, 9);
			menuLine("47", "Guild trophy", 1, 10);
			menuLine("55", "Large portrait", 1, 11);
			menuLine("56", "Gilded mohagany wall decoration", 1, 12);
			menuLine("56", "Mounted swordfish", 1, 13);
			menuLine("58", "Medium map", 1, 14);
			menuLine("58", "Medium slayer monster head", 1, 15);
			menuLine("65", "Large landscape", 1, 16);
			menuLine("66", "Round wall-mounted shield", 1, 17);
			menuLine("76", "Mounted shark", 1, 18);
			menuLine("76", "Square wall-mounted shield", 1, 19);
			menuLine("78", "Major slayer monster head", 1, 20);
			menuLine("78", "Large map", 1, 21);
			menuLine("86", "Wall-mounted kiteshield", 1, 22);
			optionTab("Construction", "Trophies", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 7) {
			clearMenu();
			menuLine("30", "Hoop-and-stick game", 1, 0);
			menuLine("32", "Boxing ring", 1, 1);
			menuLine("34", "Boxing glove rack", 1, 2);
			menuLine("34", "Oak prize chest", 1, 3);
			menuLine("37", "Lesser magical balance", 1, 4);
			menuLine("39", "Jester game", 1, 5);
			menuLine("39", "Clay attack stone", 1, 6);
			menuLine("41", "Fencing ring", 1, 7);
			menuLine("44", "Weapons rack", 1, 8);
			menuLine("44", "Teak prize chest", 1, 9);
			menuLine("49", "Treasure hunt fairy house", 1, 10);
			menuLine("51", "Combat ring", 1, 11);
			menuLine("54", "Dartboard", 1, 12);
			menuLine("54", "Extra weapons rack", 1, 13);
			menuLine("54", "Mohagany prize chest", 1, 14);
			menuLine("57", "Medium balance", 1, 15);
			menuLine("59", "Lime attack stone", 1, 16);
			menuLine("59", "Hangman game", 1, 17);
			menuLine("71", "Ranging pedestals", 1, 18);
			menuLine("77", "Greater magical balance", 1, 19);
			menuLine("79", "Marble attack stone", 1, 20);
			menuLine("81", "Archery target", 1, 21);
			menuLine("81", "Balance beam", 1, 22);
			optionTab("Construction", "Games", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 8) {
			clearMenu();
			menuLine("1", "Exit portal", 1, 0);
			menuLine("1", "Low-level plants", 1, 1);
			menuLine("5", "Decorative rock", 1, 2);
			menuLine("5", "Tree", 1, 3);
			menuLine("6", "Mid-level plants", 1, 4);
			menuLine("10", "Pond", 1, 5);
			menuLine("10", "Nice tree", 1, 6);
			menuLine("12", "High-level plants", 1, 7);
			menuLine("15", "Imp statue", 1, 8);
			menuLine("15", "Oak tree", 1, 9);
			menuLine("30", "Willow tree", 1, 10);
			menuLine("45", "Maple tree", 1, 11);
			menuLine("55", "Boundary stones", 1, 12);
			menuLine("56", "Thorny hedge", 1, 13);
			menuLine("59", "Wooden fence", 1, 14);
			menuLine("60", "Nice hedge", 1, 15);
			menuLine("60", "Yew tree", 1, 16);
			menuLine("63", "Stone wall", 1, 17);
			menuLine("64", "Small box hedge", 1, 18);
			menuLine("65", "Gazebo", 1, 19);
			menuLine("66", "Sunflower", 1, 20);
			menuLine("66", "Rosemary", 1, 21);
			menuLine("67", "Iron railings", 1, 22);
			menuLine("68", "Topiary hedge", 1, 23);
			menuLine("70", "Dungeon entrance", 1, 24);
			menuLine("71", "Marigolds", 1, 25);
			menuLine("71", "Daffodils", 1, 26);
			menuLine("71", "Picket fence", 1, 27);
			menuLine("71", "Small fountain", 1, 28);
			menuLine("72", "Fancy hedge", 1, 29);
			menuLine("75", "Magic tree", 1, 30);
			menuLine("75", "Large fountain", 1, 31);
			menuLine("75", "Garden fence", 1, 32);
			menuLine("76", "Tall fancy hedge", 1, 33);
			menuLine("76", "Roses", 1, 34);
			menuLine("76", "Bluebells", 1, 35);
			menuLine("79", "Marble wall", 1, 36);
			menuLine("80", "Tall box hedge", 1, 37);
			menuLine("81", "Posh fountain", 1, 38);
			optionTab("Construction", "Garden", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 9) {
			clearMenu();
			menuLine("61", "Throne room floor decoration", 1, 0);
			menuLine("65", "Oak cage", 1, 1);
			menuLine("65", "Oubiette spikes", 1, 2);
			menuLine("68", "Steel cage", 1, 3);
			menuLine("70", "Oak and steel cage", 1, 4);
			menuLine("70", "Skeleton guard", 1, 5);
			menuLine("71", "Tentacle pool", 1, 6);
			menuLine("72", "Spike trap", 1, 7);
			menuLine("74", "Large trapdoor", 1, 8);
			menuLine("74", "Oak dungeon door", 1, 9);
			menuLine("74", "Guard dog", 1, 10);
			menuLine("75", "Wooden dungeon treasure crate", 1, 11);
			menuLine("75", "Demon", 1, 12);
			menuLine("75", "Steel cage", 1, 13);
			menuLine("76", "Man trap", 1, 14);
			menuLine("77", "Oubliette flame pit", 1, 15);
			menuLine("78", "Hobgoblin guard", 1, 16);
			menuLine("79", "Oak dungeon treasure chest", 1, 17);
			menuLine("80", "Spiked cage", 1, 18);
			menuLine("80", "Tangle vine", 1, 19);
			menuLine("80", "Kalphite soldier", 1, 20);
			menuLine("82", "Lesser magic cage", 1, 21);
			menuLine("82", "Baby red dragon", 1, 22);
			menuLine("83", "Teak dungeon treasure chest", 1, 23);
			menuLine("83", "Rocnar", 1, 24);
			menuLine("84", "Steel-plated oak door", 1, 25);
			menuLine("84", "Marble trap", 1, 26);
			menuLine("85", "Tok-Xil", 1, 27);
			menuLine("85", "Bone cage", 1, 28);
			menuLine("86", "Huge spider", 1, 29);
			menuLine("87", "Mahogany dungeon treasure chest", 1, 30);
			menuLine("88", "Teleport trap", 1, 31);
			menuLine("89", "Greater magic cage", 1, 32);
			menuLine("90", "Troll guard", 1, 33);
			menuLine("90", "Dagannoth", 1, 34);
			menuLine("91", "Magic dungeon treasure chest", 1, 35);
			menuLine("94", "Marble dungeon door", 1, 36);
			menuLine("94", "Hellhound", 1, 37);
			menuLine("95", "Steel dragon", 1, 38);
			optionTab("Construction", "Dungeon", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 10) {
			clearMenu();
			menuLine("45", "Oak altar", 1, 0);
			menuLine("45", "Steel torches (chapel)", 1, 1);
			menuLine("48", "Symbol of Saradomin", 1, 2);
			menuLine("48", "Symbol of Guthix", 1, 3);
			menuLine("48", "Symbol of Zamorak", 1, 4);
			menuLine("49", "Wooden torches (chapel)", 1, 5);
			menuLine("49", "Chapel windchimes", 1, 6);
			menuLine("49", "Small chapel statue", 1, 7);
			menuLine("49", "Shuttered chapel window", 1, 8);
			menuLine("50", "Teak altar", 1, 9);
			menuLine("53", "Steel candlesticks", 1, 10);
			menuLine("56", "Cloth-covered teak altar", 1, 11);
			menuLine("57", "Gold candlesticks", 1, 12);
			menuLine("58", "Chapel bells", 1, 13);
			menuLine("59", "Icon of Saradomin", 1, 14);
			menuLine("59", "Icon of Guthix", 1, 15);
			menuLine("59", "Icon of Zamorak", 1, 16);
			menuLine("60", "Cloth-covered mahogany altar", 1, 17);
			menuLine("61", "Oak incense burners", 1, 18);
			menuLine("64", "Limestone altar", 1, 19);
			menuLine("65", "Mahogany incense burners", 1, 20);
			menuLine("69", "Medium chapel statue", 1, 21);
			menuLine("69", "Chapel organ", 1, 22);
			menuLine("69", "Decorative chapel window", 1, 23);
			menuLine("69", "Marble incense burner", 1, 24);
			menuLine("70", "Marble altar", 1, 25);
			menuLine("71", "Icon of Bob the Cat", 1, 26);
			menuLine("75", "Gilded marble altar", 1, 27);
			menuLine("89", "Large chapel statue", 1, 28);
			menuLine("89", "Stained-glass chapel window", 1, 29);
			optionTab("Construction", "Chapel", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 11) {
			clearMenu();
			menuLine("5", "Cat blanket", 1, 0);
			menuLine("19", "Cat basket", 1, 1);
			menuLine("26", "Rope bell pull", 1, 2);
			menuLine("27", "Oak staircase", 1, 3);
			menuLine("33", "Cushioned cat basket", 1, 4);
			menuLine("37", "Teak bell pull", 1, 5);
			menuLine("42", "Crystal ball", 1, 6);
			menuLine("48", "Teak staircase", 1, 7);
			menuLine("50", "Teak portal frame", 1, 8);
			menuLine("50", "Teleport focus", 1, 9);
			menuLine("54", "Elemental sphere", 1, 10);
			menuLine("60", "Greater teleport focus", 1, 11);
			menuLine("65", "Mahogany portal frame", 1, 12);
			menuLine("66", "Crystal of power", 1, 13);
			menuLine("67", "Limestone spiral staircase", 1, 14);
			menuLine("68", "Oak oubliette ladder", 1, 15);
			menuLine("68", "Oak lever", 1, 16);
			menuLine("78", "Teak oubliette ladder", 1, 17);
			menuLine("78", "Teak lever", 1, 18);
			menuLine("80", "Marble portal frame", 1, 19);
			menuLine("80", "Scrying pool", 1, 20);
			menuLine("82", "Marble staircase", 1, 21);
			menuLine("88", "Mahogany oubliette ladder", 1, 22);
			menuLine("88", "Mahogany lever", 1, 23);
			menuLine("97", "Marble spiral", 1, 24);
			optionTab("Construction", "Other", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Cost. Room",
			          "");
		} else if (screen == 12) {
			clearMenu();
			menuLine("42", "Oak wardrobe (costume room)", 1, 0);
			menuLine("44", "Oak fancy dress box", 1, 1);
			menuLine("46", "Oak armour case", 1, 2);
			menuLine("48", "Oak treasure chest", 1, 3);
			menuLine("50", "Oak toy box", 1, 4);
			menuLine("51", "Carved oak wardrome (costume room)", 1, 5);
			menuLine("54", "Oak cape rack", 1, 6);
			menuLine("60", "Teak wardrobe (costume room)", 1, 7);
			menuLine("62", "Teak fancy dress box", 1, 8);
			menuLine("63", "Teak cape rack", 1, 9);
			menuLine("64", "Teak armour case", 1, 10);
			menuLine("66", "Teak treasure chest", 1, 11);
			menuLine("68", "Teak toy box", 1, 12);
			menuLine("69", "Carved teak wardobe (costume room)", 1, 13);
			menuLine("72", "Mahogany cape rack", 1, 14);
			menuLine("78", "Mahogany wardobe (costume room)", 1, 15);
			menuLine("80", "Mahogany fancy dress box", 1, 16);
			menuLine("81", "Gilded mahogany cape rack", 1, 17);
			menuLine("82", "Mahogany armour case", 1, 18);
			menuLine("84", "Mahogany treasure chest", 1, 19);
			menuLine("86", "Mahogany toy box", 1, 20);
			menuLine("87", "Gilded mahogany wardobe (costume room)", 1, 21);
			menuLine("90", "Marble cape rack", 1, 22);
			menuLine("96", "Marble wardobe (costume room)", 1, 23);
			menuLine("99", "Magic stone cape rack", 1, 24);
			optionTab("Construction", "Costume Room", "Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other",
			          "Cost. Room", "");
		}
	}
}
