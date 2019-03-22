package game.player.punishment;

import java.util.ArrayList;
import java.util.List;

/**
 * Store wilderness ips to prevent multilog in wild.
 *
 * @author MGT Madness, created on
 */
public class WildernessData {


	public static List<WildernessData> wildernessData = new ArrayList<WildernessData>();


	public String ip = "";

	public String uid = "";

	public WildernessData(String ip, String uid) {
		this.ip = ip;
		this.uid = uid;
	}
}
