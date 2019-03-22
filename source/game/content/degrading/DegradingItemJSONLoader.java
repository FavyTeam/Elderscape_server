package game.content.degrading;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.ServerConstants;
import utility.JSONLoader;

/**
 * Loading degrading items
 * 
 * @author 2012
 *
 */
public class DegradingItemJSONLoader extends JSONLoader {

	/**
	 * The file location
	 */
	private static final String FILE_LOCATION =
			ServerConstants.getDataLocation() + "items/degradables.json";


	/**
	 * The degradable definitions
	 */
	private static HashMap<Integer, DegradingItem> degradables =
			new HashMap<Integer, DegradingItem>();

	/**
	 * Loads the definition
	 */
	public DegradingItemJSONLoader() {
		super(FILE_LOCATION);
		load();
	}

	@Override
	public void load(JsonObject reader, Gson builder) {
		int id = reader.get("id").getAsInt();
		int hitsRemaining = reader.get("hitsRemaining").getAsInt();
		int slot = reader.get("slot").getAsInt();
		int dropItem = reader.get("dropItem").getAsInt();
		int nextItem = reader.get("nextItem").getAsInt();
		boolean degradeOnCombat = true;
		if (reader.has("degradeOnCombat")) {
			degradeOnCombat = reader.get("degradeOnCombat").getAsBoolean();
		}
		DegradingItem item =
				new DegradingItem(id, hitsRemaining, slot, dropItem, nextItem, degradeOnCombat);
		getDegradables().put(id, item);
	}

	/**
	 * Gets the main id from counter parts
	 * 
	 * @param id the id
	 * @return the main id
	 */
	private static int getMainId(int id) {
		for (Map.Entry<Integer, DegradingItem> entry : degradables.entrySet()) {
			Integer key = entry.getKey();
			DegradingItem item = entry.getValue();
			if (item.getNextItem() == id) {
				return key;
			}
		}
		return id;
	}

	/**
	 * Gets the item
	 * 
	 * @param id the id
	 * @return the item
	 */
	public static DegradingItem getItem(int id) {
		return getDegradables().get(getMainId(id));
	}

	/**
	 * Gets the degradables
	 *
	 * @return the degradables
	 */
	public static HashMap<Integer, DegradingItem> getDegradables() {
		return degradables;
	}
}
