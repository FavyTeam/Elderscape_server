package game.object.lever;

import core.ServerConstants;
import game.object.custom.Object;
import game.player.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Lever object change and stalling.
 *
 * @author MGT Madness, created on 12-06-2017.
 */
public class Lever {
	private final static int STALL_DELAY = 3500;

	public static List<Lever> leverData = new ArrayList<Lever>();

	public int x;

	public int y;

	public long timeUsed;


	public Lever(int x, int y, long timeUsed) {
		this.x = x;
		this.y = y;
		this.timeUsed = timeUsed;
	}

	public static void readLeverObjectData() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/object/lever.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				String parse[] = line.split(" ");
				int id = Integer.parseInt(parse[0]);
				int x = Integer.parseInt(parse[1]);
				int y = Integer.parseInt(parse[2]);
				int face = Integer.parseInt(parse[3]);
				int type = Integer.parseInt(parse[4]);
				LeverObjectData.leverObjectData.add(new LeverObjectData(id, x, y, face, type));
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean canActivateLever(Player player, int x, int y) {
		boolean match = false;
		int matchIndex = -1;
		for (int index = 0; index < leverData.size(); index++) {
			if (leverData.get(index).x == x && leverData.get(index).y == y) {
				match = true;
				matchIndex = index;
				if (System.currentTimeMillis() - leverData.get(index).timeUsed <= STALL_DELAY) {
					return false;
				}
			}
		}
		if (match) {
			leverData.get(matchIndex).timeUsed = System.currentTimeMillis();
		} else {
			leverData.add(new Lever(x, y, System.currentTimeMillis()));
		}
		int type = 0;
		int face = 0;
		for (int i = 0; i < LeverObjectData.leverObjectData.size(); i++) {
			if (LeverObjectData.leverObjectData.get(i).id == player.getObjectId() && LeverObjectData.leverObjectData.get(i).x == player.getObjectX()
			    && LeverObjectData.leverObjectData.get(i).y == player.getObjectY()) {
				face = LeverObjectData.leverObjectData.get(i).face;
				type = LeverObjectData.leverObjectData.get(i).type;
			}
		}
		int pulledLeverId = 88;

		// These are the levers when exiting Mage arena in wild.
		if (player.getObjectId() == 9706 || player.getObjectId() == 9707) {
			pulledLeverId = player.getObjectId();
		}

		// Ardougne wild lever
		if (player.getX() == 3153 && player.getY() == 3923) {
			player.turnPlayerTo(3152, 3923);
		}
		new Object(pulledLeverId, player.getObjectX(), player.getObjectY(), player.getHeight(), face, type, player.getObjectId(), 4);
		return true;
	}

}
