package game.content.minigame.zombie;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Zombie instance, where data used by bother players is stored here.
 *
 * @author MGT Madness, created on 05-10-2016.
 */
public class ZombieGameInstance {
	/**
	 * Store all active minigame instances.
	 */
	public static List<ZombieGameInstance> instance = new ArrayList<ZombieGameInstance>();

	private String playerOneName;

	private String playerTwoName;

	public int wave;

	public String chestCoordinates;

	public String zombieList = "";

	public int chestId;

	private int height;

	public int spawnedZombiesLeft;

	public int totalWaveZombiesLeft;

	public int playerOneDamage;

	public int playerTwoDamage;

	public String zombieSpawns;

	public int playerOnePoints;

	public int playerTwoPoints;

	public int bossSpawnOrder = -1;

	public boolean playerOneAvailable;

	public boolean playerTwoAvailable;

	public String waveItems;

	/**
	 * Zombie data instance, store data that is shared between both players.
	 */
	public ZombieGameInstance(String playerOneName, String playerTwoName, int wave, String chestCoordinates, String zombieList, int chestId, int height, int totalWaveZombiesLeft,
	                          String zombieSpawns, boolean playerOneAvailable, boolean playerTwoAvailable) {
		this.playerOneName = playerOneName;
		this.playerTwoName = playerTwoName;
		this.wave = wave;
		this.chestCoordinates = chestCoordinates;
		this.zombieList = zombieList;
		this.chestId = chestId;
		this.height = height;
		this.totalWaveZombiesLeft = totalWaveZombiesLeft;
		this.zombieSpawns = zombieSpawns;
		this.playerOneAvailable = playerOneAvailable;
		this.playerTwoAvailable = playerTwoAvailable;
	}

	public String getPlayerOneName() {
		return playerOneName;
	}

	public String getPlayerTwoName() {
		return playerTwoName;
	}

	public int getChestId() {
		return chestId;
	}

	public int getWave() {
		return wave;
	}

	public int getHeight() {
		return height;
	}

	public String getChestCoordinates() {
		return chestCoordinates;
	}

	public int getChestXCoordinate() {
		String data[] = chestCoordinates.split(" ");
		return Integer.parseInt(data[0]);
	}

	public int getChestYCoordinate() {
		String data[] = chestCoordinates.split(" ");
		return Integer.parseInt(data[1]);
	}

	public int getChestFace() {
		String data[] = chestCoordinates.split(" ");
		return Integer.parseInt(data[2]);
	}

	/**
	 * Get current zombie minigame instance that i am in.
	 */
	public static ZombieGameInstance getCurrentInstance(String playerName) {
		for (int index = 0; index < instance.size(); index++) {
			if (instance.get(index).playerOneName.equals(playerName) || instance.get(index).playerTwoName.equals(playerName)) {
				return instance.get(index);
			}
		}
		return null;
	}

	/**
	 * Find player instance of my partner.
	 *
	 * @param playerOne My name.
	 */
	public static Player getPartnerInstance(Player player) {
		String partner = "";
		for (int index = 0; index < instance.size(); index++) {
			if (!instance.get(index).playerOneName.equals(player.getPlayerName())) {
				partner = instance.get(index).playerOneName;
				break;
			}
			if (!instance.get(index).playerTwoName.equals(player.getPlayerName())) {
				partner = instance.get(index).playerTwoName;
				break;
			}
		}
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getPlayerName().equals(partner)) {
				return loop;
			}
		}
		return null;
	}

	/**
	 * @return The name of your partner.
	 */
	public static String getPartnerName(Player player) {
		for (int index = 0; index < instance.size(); index++) {
			if (!instance.get(index).playerOneName.equals(player.getPlayerName())) {
				return instance.get(index).playerOneName;
			}
			if (!instance.get(index).playerTwoName.equals(player.getPlayerName())) {
				return instance.get(index).playerTwoName;
			}
		}
		return "?";
	}

	/**
	 * Get index of the minigame instance that i belong to.
	 */
	public static int getMinigameInstanceIndex(String playerName) {
		for (int index = 0; index < instance.size(); index++) {
			if (instance.get(index).playerOneName.equals(playerName) || instance.get(index).playerTwoName.equals(playerName)) {
				return index;
			}
		}
		return -1;
	}
}
