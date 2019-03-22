package network.sql.transactions.impl;

public class OsrsBotTransaction {

	public final int id;
	public final String time;
	public final String playerName;
	public final String playerIp;
	public final String playerUid;
	public final String rsn;
	public final double millionsReceived;
	public final int failedCollection;

	public OsrsBotTransaction(int id, String time, String playerName, String playerIp, String playerUid, String rsn, double millionsReceived, int failedCollection) {
		this.id = id;
		this.time = time;
		this.playerName = playerName;
		this.playerIp = playerIp;
		this.playerUid = playerUid;
		this.rsn = rsn;
		this.millionsReceived = millionsReceived;
		this.failedCollection = failedCollection;
	}

}
