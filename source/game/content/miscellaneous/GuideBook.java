package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Guide book interface.
 *
 * @author MGT Madness, created on 08-11-2016.
 */
public class GuideBook {

	/**
	 * Store the titles of the guides.
	 */
	public static ArrayList<String> titles = new ArrayList<String>();

	/**
	 * Store the content of the guides.
	 */
	public static ArrayList<String> content = new ArrayList<String>();

	public static void loadGuideDataFile() {
		try {
			content.clear();
			titles.clear();
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "content/guide.txt"));
			String line;
			String contentStrings = "";
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					if (line.contains("#")) {
						if (!contentStrings.isEmpty()) {
							content.add(contentStrings);
						}
						contentStrings = "";
						titles.add(line.substring(1));
					} else {
						line = line.replace("--", " ");
						contentStrings = contentStrings + line + ";";
					}
				}
			}
			content.add(contentStrings); // Has to be here so the last guide can be saved properly.
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayGuideInterface(Player player) {
		for (int index = 0; index < titles.size(); index++) {
			player.getPA().sendFrame126(titles.get(index), 22609 + index);
		}
		InterfaceAssistant.setFixedScrollMax(player, 22608, (int) (titles.size() * 15.2));
		player.getPA().displayInterface(22550);
	}

	public static boolean isGuideInterfaceButton(Player player, int buttonId) {
		if (buttonId >= 88081 && buttonId <= 88140) {
			// Pvm Rare Drops.
			if (buttonId == 88081) {
				NpcDropTableInterface.displayInterface(player, true);
				return true;
			}
			int indexButton = (buttonId - 88081);
			if (indexButton > titles.size() - 1) {
				return true;
			}
			if (indexButton > content.size() - 1) {
				return false;
			}
			String[] parseContent = content.get(indexButton).split(";");
			int lastIndexUsed = 0;
			player.getPA().sendFrame126(titles.get(indexButton), 22556);
			for (int index = 0; index < parseContent.length; index++) {
				String string = parseContent[index];
				if (string.isEmpty()) {
					break;
				}
				player.getPA().sendFrame126(string, 22558 + index);
				lastIndexUsed = 22558 + index;
			}
			lastIndexUsed++;
			InterfaceAssistant.clearFrames(player, lastIndexUsed, 22607);
			player.getPA().setTextClicked(22609 + indexButton, true);
			InterfaceAssistant.setFixedScrollMax(player, 22557, (int) (parseContent.length * 18.5));
		}

		return false;
	}
}
