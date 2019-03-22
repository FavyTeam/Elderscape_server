package utility;

import game.item.ItemAssistant;
import game.item.ItemDefinition;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RsWikiDropDumper {

	/**
	 * Name of the npc, the program will auto format your input, if it formatted it incorrectly, fill up UNFORMATTED_NAME below instead.
	 * <p>If you type in kree'arra on this, it will format it to Kree'Arra which is not a valid url, use the UNFORMATTED_NAME below and type in Kree'arra.
	 */
	public final static String NAME_TO_FORMAT = "vorkath";

	/**
	 * If this is not an empty string, it will force use this name to search up the npc without formatting the string.
	 */
	public final static String UNFORMATTED_NAME = "";

	public final static boolean DEBUG = false;

	public static void main(String args[]) {
		ItemDefinition.loadItemDefinitionsAll();

		// Format the name to append to the url.
		String name = Misc.capitalize(NAME_TO_FORMAT.toLowerCase().replaceAll(" ", "_"));
		if (!UNFORMATTED_NAME.isEmpty()) {
			name = UNFORMATTED_NAME;
		}
		try {

			ArrayList<String> errors = new ArrayList<>();
			ArrayList<String> drops = new ArrayList<>();
			Document page = Jsoup.connect("http://oldschoolrunescape.wikia.com/wiki/" + name).get();

			Misc.print("Dumped from: " + "http://oldschoolrunescape.wikia.com/wiki/" + name);

			// Get the item drop elements (tables) from 'page'
			Elements dropRows = page.select(".dropstable");

			if (dropRows.size() < 1) {
				Misc.print("No drop table for " + name);
				return;
			}

			// Begin looping all of the tables.
			for (Element row : dropRows) {

				// The table itself.
				Element table = row.children().get(0);

				// We are at the table rows now and can begin dissecting drops.
				for (Element tableRow : table.select("tr")) {
					if (tableRow.toString().contains("Show/hide rare drop table")) {
						break;
					}

					// Size of TD (Table Data) which are the row components http://i.imgur.com/RLc093a.png
					Elements tableData = tableRow.select("td");

					// Html which we use for the rarity and amount(s)
					String html = tableData.html();

					// The a(nchor) element has first, the image, second, the item name
					Elements links = tableData.select("a");

					String itemName = "";

					// for some reason links.get(1) throws an error...
					int accumulator = 0;
					for (Element link : links) {
						if (accumulator == 1) {
							itemName = link.text();
						}
						accumulator++;
					}

					// Skip if null
					if (itemName.isEmpty()) {
						continue;
					}

					int itemIdFound = ItemAssistant.getItemId(itemName, false);
					// Skip if we don't have a definiton for this item.
					if (itemIdFound < 0) {
						errors.add("Skipped item name: " + itemName);
						continue;
					}

					String[] lines = html.split("\n");

					// The amount of lines where we extract the amoutn and rarity from

					String amount = "";
					String rarity = "";

					int index = 0;

					// Looping because yet again calling lines[5] lines[6] is throwing errors..
					for (String line : lines) {

						if (index == 5) { // The line id where we get the amount(s)
							amount = line.replaceAll(",", "");
						} else if (index == 6) { // The line with rarity
							rarity = line;
						}

						index++;
					}

					// The determined drop chance
					int chance = getChanceDependingOnRarity(rarity);
					if (chance <= 0) {
						errors.add("Incorrect chance: " + chance + " for: " + itemName + ", " + ItemAssistant.getItemName(itemIdFound));
						continue;
					}

					// The item id and note id.
					int normalItemId = itemIdFound;
					int notedItemId = ItemAssistant.getNotedItem(itemIdFound);

					// Now we actually gather the possible drops.
					if (amount.contains(";")) {
						String[] split = amount.split(";");
						for (String s : split) {
							// The drop has a varying amount
							if (s.contains("ï¿½")) {
								String[] values = s.split("ï¿½");
								boolean note = values[0].contains("note") || values[1].contains("note");
								int min = Integer.parseInt(values[0].replaceAll("[^\\d.]", ""));
								int max = Integer.parseInt(values[1].replaceAll("[^\\d.]", ""));
								int finalItemId = note ? notedItemId : normalItemId;
								if (DEBUG) {
									Misc.print("Here7: " + finalItemId);
								}
								drops.add(chance + " " + finalItemId + " " + min + "-" + max + " // " + ItemAssistant.getItemName(finalItemId));
								// Handle the noted amounts
							} else if (amount.contains("noted")) {
								String amt = amount.replaceAll("[^\\d.]", "");
								int a = Integer.parseInt(amt.replaceAll(" ", ""));
								if (DEBUG) {
									Misc.print("Here6: " + notedItemId);
								}
								drops.add(chance + " " + notedItemId + " " + a + " // " + ItemAssistant.getItemName(notedItemId));
								// An unkown amount so we just set it to 1
							} else if (amount.equalsIgnoreCase("Unknown")) {
								if (DEBUG) {
									Misc.print("Here5: " + normalItemId);
								}
								drops.add(chance + " " + normalItemId + " " + 1 + " // " + ItemAssistant.getItemName(normalItemId));
								// The drop has multiple set amounts
							} else {
								String amt = s.replaceAll("[^\\d.]", "");
								if (amt.equals("")) {
									continue;
								}
								int a = Integer.parseInt(amt);
								if (DEBUG) {
									Misc.print("Here4: " + normalItemId);
								}
								drops.add(chance + " " + normalItemId + " " + a + " // " + ItemAssistant.getItemName(normalItemId));
							}
						}
						// The dropped item is noted so we drop the noted id
					} else if (amount.contains("noted")) {
						String amt = amount.replaceAll("[^\\d.]", " ");
						amt = amt.replaceFirst(" ", "-");
						amt = amt.trim();
						if (amt.endsWith("-")) {
							amt = amt.substring(0, amt.length() - 1);
						}
						if (DEBUG) {
							Misc.print("Here3: " + notedItemId);
						}
						drops.add(chance + " " + notedItemId + " " + amt + " // " + ItemAssistant.getItemName(notedItemId));
						// A single drop with inclusive values
					} else if (amount.contains("ï¿½")) {
						String[] values = amount.split("ï¿½");
						boolean note = values[0].contains("note") || values[1].contains("note");
						int min = Integer.parseInt(values[0].replaceAll("[^\\d.]", ""));
						int max = Integer.parseInt(values[1].replaceAll("[^\\d.]", ""));
						int finalItemId = note ? notedItemId : normalItemId;
						if (DEBUG) {
							Misc.print("Here2: " + finalItemId);
						}
						drops.add(chance + " " + finalItemId + " " + min + "-" + max + " // " + ItemAssistant.getItemName(finalItemId));
						// A static drop
					} else if (amount.equalsIgnoreCase("Unknown")) {
						drops.add(chance + " " + normalItemId + " " + 1 + " // " + ItemAssistant.getItemName(normalItemId));
					} else {
						if (amount.isEmpty()) {
							errors.add("Empty amount: " + itemName);
							continue;
						}
						String itemAmount = amount.replaceAll("[^\\d.]", "");
						if (amount.contains("–")) { // this dash is not the same as the normal dash -
							itemAmount = amount.replace("–", "-");
						}
						if (DEBUG) {
							Misc.print("Here1: " + normalItemId + ", " + itemAmount + ", " + amount);
						}
						drops.add(chance + " " + normalItemId + " " + itemAmount + " // " + ItemAssistant.getItemName(normalItemId));
					}
				}
			}
			if (drops.isEmpty()) {
				Misc.print("No drops found for: " + name);
			} else {
				for (int counter = 0; counter < drops.size() - 1; counter++) {
					for (int index = 0; index < drops.size() - 1 - counter; index++) {
						String line1 = drops.get(index);
						String[] parse1 = line1.split(" ");
						int rarity1 = Integer.parseInt(parse1[0]);
						String line2 = drops.get(index + 1);
						String[] parse2 = line2.split(" ");
						int rarity2 = Integer.parseInt(parse2[0]);
						if (rarity1 > rarity2) {
							String temp = line1;
							drops.remove(index);
							drops.add(index, line2);
							drops.remove(index + 1);
							drops.add(index + 1, temp);
						}
					}
				}
				for (int index = drops.size() - 1; index > -1; index--) {
					Misc.print(drops.get(index));
				}
			}
			if (!errors.isEmpty()) {
				Misc.print("Errors----");
			}
			for (int index = 0; index < errors.size(); index++) {
				Misc.print(errors.get(index));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public final static int ALWAYS = 1;

	public final static int COMMON = 8;

	public final static int UNCOMMON = 32;

	public final static int RARE = 128;

	public final static int VERY_RARE = 256;

	private static int getChanceDependingOnRarity(String rarity) {
		switch (rarity) {
			case "Always":
				return ALWAYS;
			case "Common":
				return COMMON;
			case "Uncommon":
				return UNCOMMON;
			case "Rare":
				return RARE;
			case "Very rare":
				return VERY_RARE;
		}
		if (rarity.contains("/")) {
			String shortVersion = rarity.substring(0, 23);
			if (!shortVersion.contains("<small>")) {
				if (rarity.contains("Always")) {
					return ALWAYS;
				}
				if (rarity.contains("Common")) {
					return COMMON;
				}
				if (rarity.contains("Uncommon")) {
					return UNCOMMON;
				}
				if (rarity.contains("Rare")) {
					return RARE;
				}
				if (rarity.contains("Very rare")) {
					return VERY_RARE;
				}
			}
			//Uncommon (3/128)
			rarity = rarity.replace("(", "");
			rarity = rarity.replace(")", "");
			rarity = rarity.substring(rarity.indexOf(" "));
			int start = rarity.indexOf(">") + 1;
			int end = 0;
			int chopped = rarity.indexOf("/");
			String temp = rarity.substring(chopped);
			end = temp.indexOf("<") + chopped;
			rarity = rarity.substring(start, end);
			rarity = rarity.replaceAll("~", "");
			rarity = rarity.replaceAll(",", "");
			String[] parse = rarity.split("/");
			double firstNumber = Double.parseDouble(parse[0]);
			double secondNumber = Double.parseDouble(parse[1]);
			return (int) (secondNumber / firstNumber);
		}
		return -1;
	}
}
