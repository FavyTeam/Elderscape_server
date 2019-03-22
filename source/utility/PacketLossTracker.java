package utility;

import game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PacketLossTracker {
	/**
	 * Store the packet loss data here.
	 */
	public static List<PacketLossTracker> packetLossList = new ArrayList<PacketLossTracker>();

	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int lightPacketLoss;

	public int getLightPacketLoss() {
		return lightPacketLoss;
	}

	public void setLightPacketLoss(int amount) {
		this.lightPacketLoss = amount;
	}

	private int heavyPacketLoss;

	public int getHeavyPacketLoss() {
		return heavyPacketLoss;
	}

	public void setHeavyPacketLoss(int amount) {
		this.heavyPacketLoss = amount;
	}

	public PacketLossTracker(String name, int lightPacketLoss, int heavyPacketLoss) {
		this.name = name;
		this.lightPacketLoss = lightPacketLoss;
		this.heavyPacketLoss = heavyPacketLoss;
	}

	public static PacketLossTracker getPlayerPacketLossInstance(Player player) {
		for (int index = 0; index < packetLossList.size(); index++) {
			PacketLossTracker instance = packetLossList.get(index);
			if (instance.getName().equals(player.getPlayerName())) {
				return instance;
			}
		}
		return null;
	}

}
