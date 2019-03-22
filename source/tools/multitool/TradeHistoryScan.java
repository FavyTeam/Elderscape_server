package tools.multitool;

import game.content.commands.NormalCommand;
import utility.FileUtility;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Scan the history of trades if a list of flagged players.
 *
 * @author MGT Madness, created on 28-09-2017
 */
public class TradeHistoryScan {

	public static void tradeHistoryScan() {
		final File folder = new File("backup/logs/trade");
		ArrayList<String> sort = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().contains("rigged stake")) {
				continue;
			}
			sort.add(fileEntry.getPath() + "#" + fileEntry.lastModified());
		}
		sort = NormalCommand.sort(sort, "#");
		DawntainedMultiTool.scanOngoing = false;

		for (int index = 0; index < sort.size(); index++) {
			String[] parse = sort.get(index).split("#");
			ArrayList<String> temp = FileUtility.readFile(parse[0]);
			ArrayList<String> data = new ArrayList<String>();
			for (int g = temp.size() - 1; g >= 0; g--) {
				data.add(temp.get(g));
			}

			for (int i = 0; i < data.size(); i++) {
				String line = data.get(i);
				for (int h = 0; h < DawntainedMultiTool.altNames.size(); h++) {
					if (!line.toLowerCase().contains("[" + DawntainedMultiTool.altNames.get(h) + "]")) {
						continue;
					}
					//26/09/2017, 03:14: AM PICKUP: [Amer] picked up an item while in combat 18 seconds ago that belongs to [I Am Groot] 10k
					String newLine = line;
					for (int k = 0; k < 20; k++) {
						String replacement = "";
						String format = "";
						boolean firstNameComplete = false;
						boolean secondNameComplete = false;
						format = newLine.substring(newLine.indexOf("[") + 1, newLine.indexOf("]"));
						replacement = format.replaceAll(" ", "_");
						if (!replacement.contains(" ")) {
							firstNameComplete = true;
						}
						newLine = newLine.replace(format, replacement);
						format = newLine.substring(newLine.lastIndexOf("[") + 1, newLine.lastIndexOf("]"));
						replacement = format.replaceAll(" ", "_");
						if (!replacement.contains(" ")) {
							secondNameComplete = true;
						}
						newLine = newLine.replace(format, replacement);
						if (firstNameComplete && secondNameComplete) {
							break;
						}
					}
					line = newLine;
					String split[] = line.split(" ");
					for (int z = 0; z < split.length; z++) {
						String text = split[z];
						text = text.replaceAll("_", " ").toLowerCase();
						boolean applied = false;
						for (int a = 0; a < DawntainedMultiTool.altNames.size(); a++) {
							String playerName = DawntainedMultiTool.altNames.get(a);
							playerName = "[" + playerName + "]";
							if (text.contains(playerName)) {
								DawntainedMultiTool.updateOutputText(text + " ", Color.RED, z == split.length - 1 ? true : false);
								applied = true;
								break;
							}
						}
						if (!applied) {
							DawntainedMultiTool.updateOutputText(text + " ", Color.BLACK, z == split.length - 1 ? true : false);
						}
					}
					break;
				}
			}


		}
		DawntainedMultiTool.scrollPane.getVerticalScrollBar().setValue(0);
	}

}
