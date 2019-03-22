package game.log;

import game.player.Player;
import network.packet.PacketHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * These data are flagged upon log in and added to packetLogAdd command so all their packets are tracked.
 *
 * @author MGT Madness, created on 10-06-2017.
 */
public class FlaggedData {

	public static ArrayList<String> flaggedIp = new ArrayList<String>();

	public static ArrayList<String> flaggedUid = new ArrayList<String>();

	public static void isFlaggedPlayerForPacketTracking(Player player) {
		boolean flagged = false;
		for (int index = 0; index < flaggedIp.size(); index++) {
			if (player.addressIp.contains(flaggedIp.get(index))) {
				flagged = true;
				break;
			}
		}
		for (int index = 0; index < flaggedUid.size(); index++) {
			if (player.addressUid.toLowerCase().contains(flaggedUid.get(index))) {
				flagged = true;
				break;
			}
		}
		if (!flagged) {
			return;
		}
		String name = player.getPlayerName().toLowerCase();
		if (!PacketHandler.packetLogPlayerList.contains(name)) {
			PacketHandler.packetLogPlayerList.add(name);
		}
	}

	public static void loadFlaggedData() {
		flaggedIp.clear();
		flaggedUid.clear();
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/packet abuse/flagged data.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.startsWith("//") && !line.isEmpty()) {
					line = line.toLowerCase();
					if (line.startsWith("ip: ")) {
						flaggedIp.add(line.toLowerCase().substring(4));
					} else if (line.startsWith("uid: ")) {
						flaggedUid.add(line.toLowerCase().substring(5));
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
