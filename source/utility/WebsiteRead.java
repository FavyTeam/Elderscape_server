package utility;

import core.GameType;
import core.Server;
import game.item.ItemDefinition;
import network.connection.VoteManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class WebsiteRead implements Runnable {

	public static boolean forceUpdate = false;

	@Override
	public void run() {
		ArrayList<String> websiteLines = new ArrayList<String>();
		try {
			URL tmp = new URL("http://www.dawntained.com/game/data.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
			String inputLine;

			boolean firstLineRead = false;
			while ((inputLine = br.readLine()) != null) {
				if (!inputLine.startsWith("websiteClientLatestVersion") && !firstLineRead) {
					break;
				}
				websiteLines.add(inputLine);
				firstLineRead = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int index = 0; index < websiteLines.size(); index++) {
			String string = websiteLines.get(index);
			String type = string.substring(0, string.indexOf("="));
			String text = string.replace(type + "=", "");
			switch (type) {
				case "websiteClientLatestVersion":
					Server.clientVersion = Integer.parseInt(text);
					break;
				case "extraRunelocusTickets":
					VoteManager.extraRunelocusTickets = Integer.parseInt(text);
					break;
			}
			if (GameType.isOsrsPvp()) {
				switch (type) {
					case "voteReward":
						VoteManager.voteReward = Integer.parseInt(text);
						ItemDefinition.DEFINITIONS[16000].name = "Blood money " + Misc.formatRunescapeStyle(VoteManager.voteReward);
						break;
				}
			} else {
				switch (type) {
					case "voteRewardEco":
						VoteManager.voteReward = Integer.parseInt(text);
						ItemDefinition.DEFINITIONS[16244].name = "Coins " + Misc.formatRunescapeStyle(VoteManager.voteReward);
						break;
				}
			}
		}
		if (forceUpdate) {
			Misc.print("Client version: " + Server.clientVersion);
			Misc.print("Vote reward: " + VoteManager.voteReward);
			Misc.print("Runelocus tickets: " + VoteManager.extraRunelocusTickets);
			forceUpdate = false;
		}
	}
}
