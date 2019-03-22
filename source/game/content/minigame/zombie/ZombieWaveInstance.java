package game.content.minigame.zombie;

import core.ServerConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Load Zombie map data from text files and store data into object instance.
 *
 * @author MGT Madness, created on 05-10-2016.
 */
public class ZombieWaveInstance {

	/**
	 * Store all waves data.
	 */
	public static List<ZombieWaveInstance> instance = new ArrayList<ZombieWaveInstance>();

	private int wave;

	private int chestId;

	private String teleportCoordinates;

	private String chestCoordinates;

	public String anvilCoordinates;

	private String shopList = "";

	private int totalZombies;

	public String zombieSpawns = "";

	public int points;

	public int bossId;

	public String bossDrops;

	/**
	 * Used for if player goes too far away from teleport point, cannot use other one because the other one is only saved for the wave that teleports the player.
	 */
	public String teleportCoordinatesSaved;


	/**
	 * Zombie wave data.
	 */
	public ZombieWaveInstance(int wave, String teleportCoordinates, String chestCoordinates, String shopList, int chestId, int totalZombies, String zombieSpawns, int points,
	                          int bossId, String bossDrops, String anvilCoordinates, String teleportCoordinatesSaved) {
		this.wave = wave;
		this.teleportCoordinates = teleportCoordinates;
		this.chestCoordinates = chestCoordinates;
		this.shopList = shopList;
		this.chestId = chestId;
		this.totalZombies = totalZombies;
		this.zombieSpawns = zombieSpawns;
		this.points = points;
		this.bossId = bossId;
		this.bossDrops = bossDrops;
		this.anvilCoordinates = anvilCoordinates;
		this.teleportCoordinatesSaved = teleportCoordinatesSaved;
	}

	public int getWave() {
		return wave;
	}

	public int getChestId() {
		return chestId;
	}

	public String getTeleportCoordinates() {
		return teleportCoordinates;
	}

	public int getTeleportXCoordinate() {
		String data[] = teleportCoordinates.split(" ");
		return Integer.parseInt(data[0]);
	}

	public int getTotalZombies() {
		return totalZombies;
	}

	public int getTeleportYCoordinate() {
		String data[] = teleportCoordinates.split(" ");
		return Integer.parseInt(data[1]);
	}


	public String getChestCoordinates() {
		return chestCoordinates;
	}

	public String getShopList() {
		return shopList;
	}

	public static void loadZombieContent() {
		loadWaveData();
		loadItemDrops();
	}

	/**
	 * Get the instance of the given wave.
	 *
	 * @return
	 */
	public static ZombieWaveInstance getWaveData(int wave) {
		return instance.get(wave - 1);
	}

	/**
	 * Store the number of the final zombie wave.
	 */
	public static int finalZombieWave;

	/**
	 * List of chest ids, used for verifying if object is useable.
	 */
	public static ArrayList<String> verifiedObjects = new ArrayList<String>();


	/**
	 * Store all boss ids here.
	 */
	public static ArrayList<String> allBossIds = new ArrayList<String>();



	/**
	 * Load the wave data from the external text file.
	 */
	private static void loadWaveData() {
		boolean shopSupplies = false;
		int wave = 0;
		int chestId = 0;
		int totalZombies = 0;
		String teleportCoordinates = "";
		String chestCoordinates = "";
		String anvilCoordinates = "";
		String shopList = "";
		boolean zombieCoordsCheck = false;
		String zombieCoords = "";
		int bossId = 0;
		String bossDrops = "";
		boolean bossDropsCheck = false;
		int points = 0;
		String teleportCoordinatesSaved = "";
		try {
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "content/zombie/wave.txt"));
			String line;
			while ((line = file.readLine()) != null) {

				if (line.contains("Wave level:")) {

					int waveBefore = wave + 1;
					wave = Integer.parseInt(line.substring(line.indexOf(":") + 2));

					// Add in the waves that are missing from the text file.
					if (wave - waveBefore > 0) {
						int waveToAdd = waveBefore;
						for (int index = 0; index < wave - waveBefore; index++) {
							shopSupplies = false;
							zombieCoordsCheck = false;
							finalZombieWave = waveToAdd;
							ZombieWaveInstance data = new ZombieWaveInstance(waveToAdd, teleportCoordinates, chestCoordinates, shopList, chestId, totalZombies, zombieCoords,
							                                                 points, bossId, bossDrops, anvilCoordinates, teleportCoordinatesSaved);
							instance.add(data);
							bossId = 0;
							bossDrops = "";
							teleportCoordinates = "";
							totalZombies = 0;
							waveToAdd++;
						}
					}
				} else if (line.contains("Points:")) {
					points = Integer.parseInt(line.substring(line.indexOf(":") + 2));
				} else if (line.contains("Coordinates of teleport:")) {
					teleportCoordinates = line.substring(line.indexOf(":") + 2);
					teleportCoordinatesSaved = teleportCoordinates;
				} else if (line.contains("Chest id:")) {
					chestId = Integer.parseInt(line.substring(line.indexOf(":") + 2));
				} else if (line.contains("Coordinates of chest:")) {
					chestCoordinates = line.substring(line.indexOf(":") + 2);
					String[] parse = chestCoordinates.split(" ");
					verifiedObjects.add(chestId + ", " + parse[0] + ", " + parse[1]);
				} else if (line.contains("Anvil coordinates:")) {
					anvilCoordinates = line.substring(line.indexOf(":") + 2);
					String[] parse = anvilCoordinates.split(" ");
					verifiedObjects.add(2031 + ", " + parse[0] + ", " + parse[1]);
				} else if (line.contains("Boss id:")) {
					bossId = Integer.parseInt(line.substring(line.indexOf(":") + 2));
					allBossIds.add(bossId + "");
				} else if (line.contains("Boss drops:")) {
					bossDropsCheck = true;
					zombieCoordsCheck = false;
					shopSupplies = false;
				} else if (line.contains("Zombie coordinates:")) {
					bossDropsCheck = false;
					zombieCoordsCheck = true;
					shopSupplies = false;
					zombieCoords = "";
				} else if (line.contains("Shop supplies:")) {
					bossDropsCheck = false;
					zombieCoordsCheck = false;
					shopSupplies = true;
					shopList = "";
				} else if (line.contains("Zombies:")) {
					bossDropsCheck = false;
					zombieCoordsCheck = false;
					shopSupplies = false;
				} else if (line.isEmpty()) {
					shopSupplies = false;
					zombieCoordsCheck = false;
					finalZombieWave = wave;
					ZombieWaveInstance data = new ZombieWaveInstance(wave, teleportCoordinates, chestCoordinates, shopList, chestId, totalZombies, zombieCoords, points, bossId,
					                                                 bossDrops, anvilCoordinates, teleportCoordinatesSaved);
					instance.add(data);
					bossId = 0;
					bossDrops = "";
					teleportCoordinates = "";
					totalZombies = 0;
				} else {
					if (shopSupplies) {
						String data[] = line.split(" ");
						shopList = shopList + data[0] + " ";
					} else if (bossDropsCheck) {
						String data[] = line.split(" ");
						bossDrops = bossDrops + data[0] + "-" + data[1] + " ";
					} else if (zombieCoordsCheck) {
						String data[] = line.split(" ");
						zombieCoords = zombieCoords + data[0] + " " + data[1] + " ";
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get index of the wave.
	 */
	public static int getWaveDataIndex(int wave) {
		return wave - 1;
	}

	public static String wave5Items = "";

	public static String wave10Items = "";

	public static String wave15Items = "";

	public static String wave20Items = "";

	public static String wave30Items = "";

	public static String wave40Items = "";

	/**
	 * Items dropped at wave 10, 30, 40, 50.
	 */
	private static void loadItemDrops() {
		boolean wave10 = false;
		boolean wave5 = false;
		boolean wave15 = false;
		boolean wave20 = false;
		boolean wave30 = false;
		boolean wave40 = false;
		try {
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "content/zombie/wave drops.txt"));
			String line;
			while ((line = file.readLine()) != null) {

				if (line.contains("Wave level: 10")) {
					wave5 = false;
					wave10 = true;
					wave15 = false;
					wave20 = false;
					wave30 = false;
					wave40 = false;
				} else if (line.contains("Wave level: 5")) {
					wave5 = true;
					wave10 = false;
					wave15 = false;
					wave20 = false;
					wave30 = false;
					wave40 = false;
				} else if (line.contains("Wave level: 15")) {
					wave5 = false;
					wave10 = false;
					wave15 = true;
					wave20 = false;
					wave30 = false;
					wave40 = false;
				} else if (line.contains("Wave level: 20")) {
					wave5 = false;
					wave10 = false;
					wave15 = false;
					wave20 = true;
					wave30 = false;
					wave40 = false;
				} else if (line.contains("Wave level: 30")) {
					wave5 = false;
					wave10 = false;
					wave15 = false;
					wave20 = false;
					wave30 = true;
					wave40 = false;
				} else if (line.contains("Wave level: 40")) {
					wave5 = false;
					wave10 = false;
					wave15 = false;
					wave20 = false;
					wave30 = false;
					wave40 = true;
				} else if (!line.isEmpty()) {
					if (wave10) {
						String data[] = line.split(" ");
						int itemId = Integer.parseInt(data[0]);
						wave10Items = wave10Items + itemId + " ";
					} else if (wave30) {
						String data[] = line.split(" ");
						int itemId = Integer.parseInt(data[0]);
						wave30Items = wave30Items + itemId + " ";
					} else if (wave20) {
						String data[] = line.split(" ");
						int itemId = Integer.parseInt(data[0]);
						wave20Items = wave20Items + itemId + " ";
					} else if (wave15) {
						String data[] = line.split(" ");
						int itemId = Integer.parseInt(data[0]);
						wave15Items = wave15Items + itemId + " ";
					} else if (wave5) {
						String data[] = line.split(" ");
						int itemId = Integer.parseInt(data[0]);
						wave5Items = wave5Items + itemId + " ";
					} else if (wave40) {
						String data[] = line.split(" ");
						int itemId = Integer.parseInt(data[0]);
						wave40Items = wave40Items + itemId + " ";
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
