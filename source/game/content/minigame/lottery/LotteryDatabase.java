package game.content.minigame.lottery;

import java.util.ArrayList;
import java.util.List;

/**
 * Store the object instance of each player's lottery entry.
 *
 * @author MGT Madness, created on 23-09-2017
 */
public class LotteryDatabase {
	/**
	 * The player's username.
	 */
	private String playerName = "";

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String newPlayerName) {
		playerName = newPlayerName;
	}

	/**
	 * The amount of tickets purchased by this player.
	 */
	private int ticketsPurchased;

	public int getTicketsPurchased() {
		return ticketsPurchased;
	}

	public void setTickestPurchased(int newAmount) {
		ticketsPurchased = newAmount;
	}

	/**
	 * Store the Lottery object into this list to be able to loop through and extract variables from the Lottery object.
	 */
	public static List<LotteryDatabase> lotteryDatabase = new ArrayList<LotteryDatabase>();

	/**
	 * The object that stores everyones names along with how many tickest they purchased.
	 */
	public LotteryDatabase(String playerName, int ticketsPurchased) {
		this.playerName = playerName;
		this.ticketsPurchased = ticketsPurchased;
	}

	/**
	 * @return The lottery instance of the player.
	 */
	public static LotteryDatabase getPlayerLotteryInstance(String playerName) {
		for (int index = 0; index < LotteryDatabase.lotteryDatabase.size(); index++) {
			LotteryDatabase data = LotteryDatabase.lotteryDatabase.get(index);
			if (data.getPlayerName().equals(playerName)) {
				return data;
			}
		}
		return null;
	}


}
