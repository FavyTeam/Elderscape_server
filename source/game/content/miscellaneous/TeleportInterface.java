package game.content.miscellaneous;

import java.util.ArrayList;

import core.GameType;
import core.ServerConfiguration;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.EdgeAndWestsRule;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

/**
 * Teleport interface.
 *
 * @author MGT Madness, created on 05-09-2016.
 */
public class TeleportInterface {
	private static ArrayList<String> titles = new ArrayList<String>();

	private static ArrayList<String> monster = new ArrayList<String>();

	private static ArrayList<String> skilling = new ArrayList<String>();

	private static ArrayList<String> wilderness = new ArrayList<String>();

	private static ArrayList<String> boss = new ArrayList<String>();

	private static ArrayList<String> minigame = new ArrayList<String>();

	private static ArrayList<String> city = new ArrayList<String>();

	private static ArrayList<String> donator = new ArrayList<String>();


	public static  void add(ArrayList<String> list, String entry, GameType type) {
		switch (type) {
			case ANY:
				list.add(entry);
				break;
			case OSRS:
				if(GameType.isOsrs())
				list.add(entry);
				break;
			case PRE_EOC:
				if(GameType.isPreEoc())
				list.add(entry);
				break;
			case OSRS_PVP:
				if(GameType.isOsrsPvp())
				list.add(entry);
				break;
			case OSRS_ECO:
				if(GameType.isOsrsEco())
				list.add(entry);
				break;
		}
	}

	public static void teleportStartUp() {
		add(titles, "Wilderness", GameType.ANY);
		add(titles, "Skilling", GameType.ANY);
		add(titles, "Locations", GameType.ANY);
		add(titles, "Monsters", GameType.ANY);
		if(GameType.isOsrsEco())  titles.add("bosses"); else titles.add("Safe bosses");
		add(titles, "Minigames", GameType.ANY);
		add(titles, "Donator", GameType.ANY);

		add(monster, "Lumbridge cows ; 3259 3267 0", GameType.ANY);
		add(monster, "Yaks ; 2321 3793 0", GameType.ANY);
		add(monster, "", GameType.ANY);
		if (GameType.isOsrsEco()) monster.add("Ammonite crabs ; 3726 3892 0"); else monster.add("Rock crabs ; 2676 3715 0");

		add(monster,"Sand crabs ; 1866 3552 0", GameType.OSRS);
		add(monster,"Slayer tower ; 3428 3537 0", GameType.ANY);
		add(monster,"Brimhaven dungeon ; 2713 9564 0", GameType.ANY);
		add(monster,"Bandit camp ; 3169 2990 0", GameType.ANY);
		add(monster,"Taverley dungeon ; 2884 9798 0", GameType.ANY);
		add(monster,"Edgeville dungeon ; 3096 9867 0", GameType.ANY);
		add(monster,"Fremennik dungeon ; 2808 10002 0", GameType.ANY);
		add(monster,"Tzhaar ; 2452 5167 0", GameType.ANY);
		add(monster,"Wyvern cave ; 3056 9564 0", GameType.OSRS);

		/*
		 // Future expansion when Entrana gets too crowded
		if (Config.ECO) {
			skilling.add("Entrana skilling zone ; 2866 3337 0");
			skilling.add("Mining ; 3023 9739 0");
			skilling.add("Smithing ; 2469 3436 0");
			skilling.add("Fishing ; 2552 3563 0");
			skilling.add("Cooking ; 2998 3906 0");
			skilling.add("Woodcutting ; 2998 3906 0");
			skilling.add("Agility ; 2998 3906 0");
			skilling.add("Farming ; 2816 3462 0");
			skilling.add("Thieving ; 2998 3906 0");
			skilling.add("Runecrafting ; 3039 4835 0");
			skilling.add("Hunter ; 2334 3578 0");
		}
		*/

		add(skilling,"Entrana Skilling Zone ; 2866 3337 0", GameType.ANY);
		add(skilling,"Dwarven Mine ; 3023 9739 0", GameType.ANY);
		add(skilling,"Agility ; 2998 3906 0", GameType.ANY);
		add(skilling,"Hunter ; 2340 3584 0", GameType.ANY);

		add(wilderness,"Edge PvP @gr3@(Bank area) ; 3094 3496 4", GameType.ANY);
		add(wilderness,"West Dragons @red@(Wilderness 10) ; 2979 3594 0", GameType.ANY);
		add(wilderness,"East Dragons @red@(Wilderness 17) ; 3348 3647 0", GameType.ANY);
		add(wilderness,"Elder Chaos Druids @red@(Wilderness 15) ; 3235 3635 0", GameType.OSRS);
		add(wilderness,"Graveyard @red@(Wilderness 19) ; 3146 3671 0", GameType.ANY);
		add(wilderness,"Demonic Gorillas @red@(Wilderness 20) ; 3108 3674 0", GameType.OSRS);
		add(wilderness,"Tormented Demons @red@(Wilderness 24) ; 3260 3705 0", GameType.OSRS);
		add(wilderness,"Crazy Archaeologist @red@(Wilderness 25) ; 2980 3713 0", GameType.OSRS);
		add(wilderness,"Revenants @red@(Wilderness 27) ; 2978 3735 0", GameType.OSRS);
		add(wilderness,"Venenatis @red@(Wilderness 28) ; 3308 3737 0", GameType.OSRS);
		add(wilderness,"Lava Dragons @red@(Wilderness 31) ; 3070 3760 0", GameType.OSRS);
		add(wilderness,"Chinchompa Hill @red@(Wilderness 34) ; 3138 3785 0", GameType.ANY);
		add(wilderness,"Vet'ion @red@(Wilderness 34) ; 3220 3789 0", GameType.OSRS);
		add(wilderness,"Revenant Caves @red@(Wilderness 40) ; 3127 3833 0", GameType.ANY);//TODO - replace with pre-eoc rev caves
		add(wilderness,"Chaos Fanatic @red@(Wilderness 42) ; 2979 3848 0", GameType.OSRS);
		add(wilderness,"Callisto @red@(Wilderness 44) ; 3202 3865 0", GameType.OSRS);
		add(wilderness,"Ice Strykewyrms @red@(Wilderness 45) ; 2977 3873 0", GameType.ANY);
		add(wilderness,"Demonic Ruins @red@(Wilderness 46) ; 3288 3886 0", GameType.ANY);
		add(wilderness,"Chaos Elemental @red@(Wilderness 50) ; 3307 3916 0", GameType.ANY);
		add(wilderness,"Mage Arena @red@(Wilderness 52) ; 3105 3934 0", GameType.ANY);
		add(wilderness,"Scorpia @red@(Wilderness 54) ; 3231 3944 0", GameType.OSRS);
		add(wilderness,"Magebank @gr3@(Bank area) ; 2537 4714 0", GameType.ANY);

		if (ServerConfiguration.DEBUG_MODE) {
			add(boss,"Zulrah ; 2200 3056 0", GameType.OSRS);
			add(boss,"Abyssal Sire ; 3105 4836 0", GameType.OSRS);
			add(boss,"Deranged Archaeologist ; 3682 3715 0", GameType.OSRS);
		}
		add(boss, "Vorkath ; 2272 4045 0", GameType.OSRS);
		add(boss, "Kraken ; 5 5 5", GameType.OSRS);
		add(boss, "Lizard Shaman ; 1481 3697 0", GameType.OSRS);
		add(boss, "Kalphite Queen ; 3507 9494 0", GameType.ANY);
		add(boss, "Giant Mole ; 1760 5194 0", GameType.ANY);
		add(boss, "Corporeal Beast ; 2966 4382 2", GameType.ANY);
		add(boss, "Cerberus ; 1240 1226 0", GameType.OSRS);
		add(boss, "K'ril Tsutsaroth ; 2925 5331 2",  GameType.ANY);
		add(boss, "Commander Zilyana ; 2907 5265 0", GameType.ANY);
		add(boss, "Kree'arra ; 2839 5296 2", GameType.ANY);
		add(boss, "General Graardor ; 2864 5354 2", GameType.ANY);
		add(boss, "Dagannoth Kings ; 1904 4366 0", GameType.ANY);
		add(boss, "King Black Dragon ; 2271 4680 0", GameType.ANY);
		add(boss, "TzTok-Jad ; 2452 5167 0", GameType.ANY);

		add(minigame,"Duel Arena ; 3366 3266 0", GameType.ANY);
		add(minigame,"Npc Item Doubler ; 3091 3505 0", GameType.OSRS);
		add(minigame,"Clan Wars ; 3327 4758 0", GameType.ANY);
		add(minigame,"Dicing ; 1690 4250 0", GameType.ANY);
		add(minigame,"Barrows ; 3565 3315 0", GameType.ANY);
			//minigame.add("Pest control ; 2658 2659 0");
			add(minigame,"Warrior's Guild ; 2851 3548 0", GameType.OSRS_ECO);
			add(minigame,"Recipe For Disaster ; 1900 5346 0", GameType.OSRS_ECO);
			add(minigame,"Fight Pits ; 2401 5180 0", GameType.OSRS_ECO);

		/*
		minigame.add("Zombie survival ; 3659 3516 0");
		*/

		if (ServerConfiguration.DEBUG_MODE) {
			donator.add("Donator Zone ; 2530 2724 0");
		}
		else
		{
			donator.add("Donator Zone ; 2192 3251 0");
		}
		add(donator,"Donator Dungeon ; 1748 5325 0", GameType.OSRS);

			add(city,"Fossil Island (east) ; 3817 3808 0", GameType.OSRS_ECO);
			add(city,"Fossil Island (west) ; 3735 3803 0", GameType.OSRS_ECO);
			add(city,"Land's End ; 1504 3423 0", GameType.OSRS_ECO);
		add(city,"Varrock ; 3213 3424 0", GameType.ANY);
		add(city,"Falador ; 2965 3378 0", GameType.ANY);
		add(city,"Lumbridge ; 3222 3218 0", GameType.ANY);
		add(city,"Al-kharid ; 3276 3167 0", GameType.ANY);
		add(city,"Karamja ; 2947 3147 0", GameType.ANY);
		if (ServerConfiguration.DEBUG_MODE) {
			add(city,"Zul-andra ; 2199 3056 0", GameType.OSRS);
		}
		add(city,"Draynor ; 3093 3248 0", GameType.ANY);
		add(city,"Ardougne ; 2661 3306 0", GameType.ANY);
		add(city,"Taverly ; 2934 3450 0", GameType.ANY);
		add(city,"Yanille ; 2606 3092 0", GameType.ANY);
		add(city,"Catherby ; 2827 3437 0", GameType.ANY);
		add(city,"Nardah ; 3420 2916 0", GameType.ANY);
		add(city,"Pollvineach ; 3357 2967 0", GameType.ANY);
		add(city,"Canifis ; 3494 3483 0", GameType.ANY);
		add(city,"Camelot ; 2756 3477 0", GameType.ANY);
		if (ServerConfiguration.DEBUG_MODE) {
			city.add("Port Phasmatys ; 3684 3473 0");
		}
	}

	public static boolean isTeleportInterfaceButton(Player player, int buttonId) {
		if (buttonId >= 77023 && buttonId <= 77107) {
			teleportAction(player, buttonId);
			return true;
		}
		switch (buttonId) {
			case 76247:
			case 76251:
			case 76255:
			case 77003:
			case 77007:
			case 77011:
			case 77015:
				if (!player.canUseTeleportInterface) {
					return true;
				}
				int index = buttonId - (buttonId > 77000 ? 76999 : 76243);
				if (buttonId > 77000) {
					index += 12;
				}
				InterfaceAssistant.scrollUp(player);
				player.teleportInterfaceIndex = index;
				player.getPA().setInterfaceClicked(19700, 19699 + index, true);
				displayTeleports(player, index);
				return true;
		}

		return false;

	}

	private static void teleportAction(Player player, int buttonId) {
		if (!player.canUseTeleportInterface) {
			return;
		}
		if (player.currentTeleports.isEmpty()) {
			return;
		}
		if (player.isInZombiesMinigame()) {
			return;
		}
		int index = buttonId - 77023;
		index /= 4;
		if (index > player.currentTeleports.size() - 1) {
			return;
		}
		String coordinate = player.currentTeleports.get(index).substring(player.currentTeleports.get(index).indexOf(";") + 2);
		String name = player.currentTeleports.get(index);
		String parse[] = coordinate.split(" ");
		int height = Integer.parseInt(parse[2]);
		int x = Integer.parseInt(parse[0]);
		int y = Integer.parseInt(parse[1]);

		// Recipe for disaster teleport
		if (x == 1900 && y == 5346) {
			height = 24 + (player.getPlayerId() * 4);
		}

		if (name.contains("Donator zone") && !player.isDonator()) {
			player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.DONATOR) + "The Donator zone is only for Donators. Type in ::donate");
			return;
		}
		if (name.contains("Donator dungeon") && !player.isSuperDonator()) {
			player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.SUPER_DONATOR) + "This Donator dungeon is only for Super Donators and above. Type in ::donate");
			player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.SUPER_DONATOR) + "Donators earn 20-35k an hour depending on their rank.");
			return;
		}
		if (name.contains("Kraken")) {
			player.getDH().sendDialogues(644);
			return;
		}
		if (name.contains("Dicing")) {
			if (!ClanChatHandler.inDiceCc(player, false, true)) {
				player.getPA().sendMessage("Join 'Dice' cc to start dicing.");
				return;
			}
		}
		if (GameType.isOsrsEco()) {
			if (name.contains("Mining")) {
				player.getPA().closeInterfaces(true);
				player.getDH().sendDialogues(550);
				return;
			}
			if (name.contains("Smithing")) {
				player.getPA().closeInterfaces(true);
				player.getDH().sendDialogues(551);
				return;
			}
			if (name.contains("Fishing")) {
				player.getPA().closeInterfaces(true);
				player.getDH().sendDialogues(552);
				return;
			}
			if (name.contains("Cooking")) {
				player.getPA().closeInterfaces(true);
				player.getDH().sendDialogues(553);
				return;
			}
			if (name.contains("Woodcutting")) {
				player.getPA().closeInterfaces(true);
				player.getDH().sendDialogues(554);
				return;
			}
			if (name.contains("Thieving")) {
				player.getPA().closeInterfaces(true);
				player.getDH().sendDialogues(556);
				return;
			}
		}
		if (name.contains("Agility")) {
			player.getPA().closeInterfaces(true);
			player.getDH().sendDialogues(555);
			return;
		}
		if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, x, y, height)) {
			return;
		}
		if (GameType.isOsrsEco()) {
			if (!Teleport.startTeleport(player, x, y, height, "ARCEUUS")) {
				return;
			}
		}
		if (!Teleport.spellTeleport(player, x, y, height, player.getActionIdUsed() == 4397 ? true : false)) {
			return;
		}
		player.lastTeleport = parse[0] + " " + parse[1] + " " + height;
		if (height == 4 && x == 3094 && y == 3496) {
			EdgePvp.edgePvpTeleportedTo(player);
		}
		player.canUseTeleportInterface = false;
		player.resetActionIdUsed();

	}

	private static void displayTeleports(Player player, int index) {
		if (!player.canUseTeleportInterface) {
			return;
		}
		ArrayList<String> list = null;
		if (index == 4) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (wilderness.size() * 25.2));
			list = wilderness;
		} else if (index == 24) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, 177);
			list = minigame;
		} else if (index == 8) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, 177);
			list = skilling;
		} else if (index == 12) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (city.size() * 25.2));
			list = city;
		} else if (index == 16) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (monster.size() * 25.2));
			list = monster;
		} else if (index == 20) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, (int) (boss.size() * 25.2));
			list = boss;
		} else if (index == 28) {
			InterfaceAssistant.setFixedScrollMax(player, 19734, 177);
			list = donator;
		}
		player.currentTeleports = list;
		for (int index1 = 0; index1 < 25; index1++) {
			String text = "";
			if (index1 <= list.size() - 1) {
				text = list.get(index1);
				int a = text.lastIndexOf(";") - 1;
				text = text.substring(0, a);
			}
			player.getPA().sendFrame126(text, 19738 + (index1 * 4));
		}
	}

	public static void displayInterface(Player player) {
		if (player.isInZombiesMinigame()) {
			return;
		}
		if (!player.canUseTeleportInterface) {
			return;
		}
		for (int index = 0; index < titles.size(); index++) {
			player.getPA().sendFrame126(titles.get(index), 19706 + (index * 4));
		}
		if (player.teleportInterfaceIndex == 0) {
			player.teleportInterfaceIndex = 4;
		}
		player.getPA().setInterfaceClicked(19700, 19699 + player.teleportInterfaceIndex, true);
		displayTeleports(player, player.teleportInterfaceIndex);
		player.getPA().displayInterface(19700);
	}

}
