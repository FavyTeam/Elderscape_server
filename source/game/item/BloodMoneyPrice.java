package game.item;

import core.ServerConstants;

import java.io.BufferedReader;
import java.io.FileReader;

public class BloodMoneyPrice {
	public static final BloodMoneyPrice[] DEFINITIONS = new BloodMoneyPrice[ServerConstants.MAX_ITEM_ID];


	public BloodMoneyPrice(int itemId, int bloodMoneyPrice, int harvestedPrice, boolean spawnFree) {
		this.itemId = itemId;
		this.bloodMoneyPrice = bloodMoneyPrice;
		this.harvestedPrice = harvestedPrice;
		this.spawnFree = spawnFree;
	}

	public static BloodMoneyPrice[] getDefinitions() {
		return DEFINITIONS;
	}

	public final int itemId;

	public final int bloodMoneyPrice;

	public final int harvestedPrice;

	/**
	 * If true, do not withdraw from bank.
	 */
	public final boolean spawnFree;

	public static int getBloodMoneyPrice(int itemId) {
		itemId = ItemAssistant.getUnNotedItem(itemId);
		if (getDefinitions()[itemId] == null) {
			return 0;
		}
		return getDefinitions()[itemId].bloodMoneyPrice;
	}

	public static void loadBloodMoneyPrice() {

		int customprice = 50000;
		for (int index = 0; index < ServerConstants.getImmortalDonatorItems().length; index++) {
			int itemId = ServerConstants.getImmortalDonatorItems()[index];
			BloodMoneyPrice.DEFINITIONS[itemId] = new BloodMoneyPrice(itemId, customprice, 0, false);
			ItemDefinition.getDefinitions()[itemId].price = customprice * 750;
		}

		try {


			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "items/free items.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty() && !line.startsWith("//")) {
					String[] parse = line.split(" ");
					int itemId = Integer.parseInt(parse[0]);
					int harvestPrice = 0;
					if (BloodMoneyPrice.getDefinitions()[itemId] != null) {
						harvestPrice = BloodMoneyPrice.getDefinitions()[itemId].harvestedPrice;
					}
					BloodMoneyPrice.DEFINITIONS[itemId] = new BloodMoneyPrice(itemId, BloodMoneyPrice.getBloodMoneyPrice(itemId), harvestPrice, true);
				}
			}

			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
