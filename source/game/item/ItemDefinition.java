package game.item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.GameType;
import core.ServerConstants;

public class ItemDefinition {

	public static final ItemDefinition[] DEFINITIONS = new ItemDefinition[ServerConstants.MAX_ITEM_ID];


	public ItemDefinition(int itemId, String itemName, int[] Bonuses, int rangedStrengthBonus, int magicStrengthBonus, boolean note, int unNotedId, int notedId, boolean stackable, int price, boolean f2p, boolean random,
			boolean mask, boolean helm, boolean fullBody, boolean untradeableOsrsPvp, boolean untradeableOsrsEco, boolean untradeablePreEoc, boolean destroyOnDrop, boolean toInventoryOnDeathOsrsPvp, boolean toInventoryOnDeathPreEoc) {
		this.itemId = itemId;
		this.name = itemName;
		this.bonuses = Bonuses;
		this.rangedStrengthBonus = rangedStrengthBonus;
		this.magicStrengthBonus = magicStrengthBonus;
		this.note = note;
		this.unNotedId = unNotedId;
		this.notedId = notedId;
		this.stackable = stackable;
		this.price = price;
		this.f2p = f2p;
		this.random = random;
		this.mask = mask;
		this.helm = helm;
		this.fullBody = fullBody;
		this.untradeableOsrsPvp = untradeableOsrsPvp;
		this.untradeableOsrsEco = untradeableOsrsEco;
		this.untradeablePreEoc = untradeablePreEoc;
		this.destroyOnDrop = destroyOnDrop;
		this.toInventoryOnDeathOsrsPvp = toInventoryOnDeathOsrsPvp;
		this.toInventoryOnDeathPreEoc = toInventoryOnDeathPreEoc;
	}

	public static ItemDefinition[] getDefinitions() {
		return DEFINITIONS;
	}

	public final int itemId;

	public String name;

	public int[] bonuses = new int[12];

	public int rangedStrengthBonus;

	public int magicStrengthBonus;

	public final boolean note;

	public void setUnNotedId(int id) {
		unNotedId = id;
	}

	public int unNotedId;

	public int notedId;

	public final boolean stackable;

	public final boolean f2p;

	public final boolean mask;

	public final boolean helm;

	public final boolean fullBody;

	public final boolean untradeableOsrsPvp;

	public final boolean untradeableOsrsEco;

	public final boolean untradeablePreEoc;

	public final boolean destroyOnDrop;

	public final boolean toInventoryOnDeathOsrsPvp;

	public final boolean toInventoryOnDeathPreEoc;

	/**
	 * True, if it is an item that is randomized when using quick setup. Such as a randomized Mystic top item.
	 * Random items are not banked when using quick setup button.
	 */
	public final boolean random;

	public int price;

	public static void loadItemDefinitionsAll() {
		System.out.println("Loading All Item Definitions.");
		loadItemDefinitions(ServerConstants.getOsrsGlobalDataLocation() + "items/item definition.txt");
		if (GameType.isOsrsEco()) {
			loadItemDefinitions(ServerConstants.getEcoDataLocation() + "items/item definition.txt");
		}
	}


	private static String nameTemp = "";

	private static int itemIdTemp = 0;

	private static int unNotedIdTemp = 0;

	private static int notedIdTemp = 0;

	private static int bloodMoneyPriceTemp = 0;

	private static int harvestedPriceTemp = 0;

	private static int[] bonusesTemp = new int[12];

	private static int rangedStrengthBonusTemp = 0;

	private static int magicStrengthBonusTemp = 0;

	private static boolean noteTemp = false;

	private static boolean f2pTemp = false;

	private static boolean randomTemp = false;

	private static boolean stackableTemp = false;

	private static int priceTemp = 0;

	private static boolean helmTemp = false;

	private static boolean maskTemp = false;

	private static boolean fullBodyTemp = false;

	private static boolean untradeableOsrsPvpTemp;

	private static boolean untradeableOsrsEcoTemp;

	private static boolean untradeablePreEocTemp;

	private static boolean destroyOnDropTemp;

	private static boolean toInventoryOnDeathOsrsPvpTemp;

	private static boolean toInventoryOnDeathPreEocTemp;

	public static boolean isNoteableItem(int itemId) {
		ItemDefinition instance = ItemDefinition.getDefinitions()[itemId];
		ItemDefinition secondInstance = ItemDefinition.getDefinitions()[itemId + 1];
		if (secondInstance != null) {
			if (instance.notedId == 0 && secondInstance.note) {
				return true;
			}
		}

		if (instance.notedId > 0) {
			return true;
		}

		return false;
	}

	public static void loadItemDefinitions(String fileLocation) {
		try {

			BufferedReader file = new BufferedReader(new FileReader(fileLocation));
			String line;

			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					readItemDefinitionsLine(line);
				}
			}

			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


		Iterator<?> it = specialNotedItemData.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>) it.next();
			int unNotedId = pair.getKey();
			int notedId = pair.getValue();
			ItemDefinition.getDefinitions()[notedId].setUnNotedId(unNotedId);
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	/**
	 * Unnoted id, noted id.
	 */
	public static Map<Integer, Integer> specialNotedItemData = new HashMap<Integer, Integer>();

	private static void readItemDefinitionsLine(String line) {
		String[] parse = line.split(" ");
		if (line.startsWith("Id:")) {
			itemIdTemp = Integer.parseInt(parse[1]);
		} else if (line.startsWith("Name:")) {
			nameTemp = line.replace("Name: ", "");
		} else if (line.startsWith("Price:")) {
			priceTemp = Integer.parseInt(parse[1]);
		} else if (line.startsWith("Blood money price:")) {
			bloodMoneyPriceTemp = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Harvested price:")) {
			harvestedPriceTemp = Integer.parseInt(parse[2]);
		}
		else if (line.startsWith("Ranged strength bonus:")) {
			rangedStrengthBonusTemp = Integer.parseInt(parse[3]);
		}
		else if (line.startsWith("Magic strength bonus:")) {
			magicStrengthBonusTemp = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Noted:")) {
			noteTemp = parse[1].equals("true");
		} else if (line.startsWith("Noted id:")) {
			notedIdTemp = Integer.parseInt(parse[2]);
			unNotedIdTemp = itemIdTemp;
			specialNotedItemData.put(unNotedIdTemp, notedIdTemp);
		} else if (line.startsWith("Stackable:")) {
			stackableTemp = parse[1].equals("true");
		} else if (line.startsWith("Mask:")) {
			maskTemp = parse[1].equals("true");
		} else if (line.startsWith("Helm:")) {
			helmTemp = parse[1].equals("true");
		} else if (line.startsWith("Full body:")) {
			fullBodyTemp = parse[2].equals("true");
		} else if (line.startsWith("F2p:")) {
			f2pTemp = parse[1].equals("true");
		} else if (line.startsWith("Random:")) {
			randomTemp = parse[1].equals("true");
		} else if (line.startsWith("Untradeable osrs pvp:")) {
			untradeableOsrsPvpTemp = parse[3].equals("true");
		} else if (line.startsWith("Untradeable osrs eco:")) {
			untradeableOsrsEcoTemp = parse[3].equals("true");
		} else if (line.startsWith("Untradeable pre eoc:")) {
			untradeablePreEocTemp = parse[3].equals("true");
		} else if (line.startsWith("Inventory death osrs pvp:")) {
			toInventoryOnDeathOsrsPvpTemp = parse[4].equals("true");
		} else if (line.startsWith("Inventory death pre-eoc:")) {
			toInventoryOnDeathPreEocTemp = parse[3].equals("true");
		} else if (line.startsWith("Drop destroy:")) {
			destroyOnDropTemp = parse[2].equals("true");
		} else if (line.startsWith("Stab attack bonus:")) {
			bonusesTemp[0] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Slash attack bonus:")) {
			bonusesTemp[1] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Crush attack bonus:")) {
			bonusesTemp[2] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Magic attack bonus:")) {
			bonusesTemp[3] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Ranged attack bonus:")) {
			bonusesTemp[4] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Stab defence bonus:")) {
			bonusesTemp[5] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Slash defence bonus:")) {
			bonusesTemp[6] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Crush defence bonus:")) {
			bonusesTemp[7] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Magic defence bonus:")) {
			bonusesTemp[8] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Ranged defence bonus:")) {
			bonusesTemp[9] = Integer.parseInt(parse[3]);
		} else if (line.startsWith("Strength bonus:")) {
			bonusesTemp[10] = Integer.parseInt(parse[2]);
		} else if (line.startsWith("Prayer bonus:")) {
			bonusesTemp[11] = Integer.parseInt(parse[2]);
			if (noteTemp) {
				stackableTemp = true;
			}
			if (!GameType.isOsrsPvp()) {
				toInventoryOnDeathOsrsPvpTemp = false;
			}
			ItemDefinition.DEFINITIONS[itemIdTemp] = new ItemDefinition(itemIdTemp, nameTemp, bonusesTemp, rangedStrengthBonusTemp, magicStrengthBonusTemp, noteTemp, unNotedIdTemp, notedIdTemp, stackableTemp, priceTemp, f2pTemp,
					randomTemp, maskTemp, helmTemp, fullBodyTemp, untradeableOsrsPvpTemp, untradeableOsrsEcoTemp, untradeablePreEocTemp, destroyOnDropTemp, toInventoryOnDeathOsrsPvpTemp, toInventoryOnDeathPreEocTemp);
			BloodMoneyPrice.DEFINITIONS[itemIdTemp] = new BloodMoneyPrice(itemIdTemp, bloodMoneyPriceTemp, harvestedPriceTemp, false);
			bonusesTemp = new int[12];
			rangedStrengthBonusTemp = 0;
			magicStrengthBonusTemp = 0;
			notedIdTemp = 0;
			unNotedIdTemp = 0;
			priceTemp = 0;
			bloodMoneyPriceTemp = 0;
			harvestedPriceTemp = 0;
			untradeableOsrsPvpTemp = false;
			untradeableOsrsEcoTemp = false;
			untradeablePreEocTemp = false;
			destroyOnDropTemp = false;
			toInventoryOnDeathOsrsPvpTemp = false;
			fullBodyTemp = false;
			helmTemp = false;
			maskTemp = false;
			randomTemp = false;
			f2pTemp = false;
			stackableTemp = false;
			toInventoryOnDeathPreEocTemp = false;

		}

	}
}
