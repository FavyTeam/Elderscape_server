package network.connection;

import java.util.ArrayList;
import java.util.List;

public class VoteTracker {

	public static List<VoteTracker> voteList = new ArrayList<VoteTracker>();

	public String addressIp = "";

	public int siteId;

	public long timeClaimed;

	public String uid = "";

	public String name = "";

	public VoteTracker(String name, String uid, String address, int siteId, long timeClaimed) {
		this.name = name;
		this.uid = uid;
		this.addressIp = address;
		this.siteId = siteId;
		this.timeClaimed = timeClaimed;
	}

}
