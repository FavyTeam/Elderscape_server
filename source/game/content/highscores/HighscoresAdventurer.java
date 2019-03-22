package game.content.highscores;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.ServerConstants;
import game.content.profile.NpcKillTracker;
import game.content.profile.ProfileRank;
import game.player.Player;
import utility.FileUtility;
import utility.Misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Adventurer highscores.
 *
 * @author MGT Madness, created on 06-01-2016.
 */
public class HighscoresAdventurer {
	/**
	 * The class instance.
	 */
	private static final HighscoresAdventurer instance = new HighscoresAdventurer();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresAdventurer getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/adventurer.json";

	public HighscoresAdventurer[] highscoresList;

	public String name;

	public int rankNumber;

	public int totalLevel;

	public int bossKill;

	public String gameMode;

	public HighscoresAdventurer() {
		this.name = "";
		this.rankNumber = 0;
		this.totalLevel = 0;
		this.bossKill = 0;
		this.gameMode = "";
	}

	public HighscoresAdventurer(String username, int rank, int totalLevel, int bossKill, String gameMode) {
		this.name = username;
		this.rankNumber = rank;
		this.totalLevel = totalLevel;
		this.bossKill = bossKill;
		this.gameMode = gameMode;
	}

	public void changeNameOnHighscores(String oldName, String newName) {
		if (highscoresList == null) {
			return;
		}
		for (int i = 0; i < highscoresList.length; i++) {

			if (highscoresList[i].name.equalsIgnoreCase(oldName)) {
				highscoresList[i].name = Misc.capitalize(newName);
				break;
			}
		}
	}

	public void initiateHighscoresInstance() {
		highscoresList = new HighscoresAdventurer[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresAdventurer();
		}
		loadFile();
	}

	public void loadFile() {
		if (!FileUtility.fileExists(HIGHSCORES_LOCATION)) {
			saveFile();
		}
		Path path = Paths.get(HIGHSCORES_LOCATION);
		File file = path.toFile();
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);

			for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
				String name = "";
				int rankNumber = 0;
				int totalLevel = 0;
				int bossKill = 0;
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", rankNumber")) {
					rankNumber = reader.get("rank " + i + ", rankNumber").getAsInt();
				}
				if (reader.has("rank " + i + ", totalLevel")) {
					totalLevel = reader.get("rank " + i + ", totalLevel").getAsInt();
				}
				if (reader.has("rank " + i + ", bossKill")) {
					bossKill = reader.get("rank " + i + ", bossKill").getAsInt();
				}
				if (reader.has("rank " + i + ", gameMode")) {
					gameMode = reader.get("rank " + i + ", gameMode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresAdventurer(name, rankNumber, totalLevel, bossKill, gameMode);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveFile() {
		Path path = Paths.get(HIGHSCORES_LOCATION);
		File file = path.toFile();
		file.getParentFile().setWritable(true);
		try (FileWriter writer = new FileWriter(file)) {

			Gson builder = new GsonBuilder().setPrettyPrinting().create();
			JsonObject object = new JsonObject();
			for (int i = 0; i < highscoresList.length; i++) {
				object.addProperty("rank " + i + ", name", highscoresList[i].name);
				object.addProperty("rank " + i + ", rankNumber", highscoresList[i].rankNumber);
				object.addProperty("rank " + i + ", totalLevel", highscoresList[i].totalLevel);
				object.addProperty("rank " + i + ", bossKill", highscoresList[i].bossKill);
				object.addProperty("rank " + i + ", gameMode", highscoresList[i].gameMode);

			}
			writer.write(builder.toJson(object));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isOnHighscores(String name) {
		for (int i = 0; i < highscoresList.length; i++) {

			if (highscoresList[i].name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}



	public void removePlayer(String name) {
		for (int index = 0; index < highscoresList.length; index++) {
			if (highscoresList[index].name.equalsIgnoreCase(name)) {
				highscoresList[index] = new HighscoresAdventurer("", 0, 0, 0, "");
			}
		}
	}

	public void sortHighscores(Player player) {

		if (player.isCombatBot()) {
			return;
		}

		if (player.isAdministratorRank()) {
			return;
		}
		int rank = ProfileRank.getAdventurerRankNumber(player.getTotalLevel(), player.bossScoreCapped, player.getBarrowsRunCompleted(), player.getClueScrollsCompleted(),
		                                               player.titleTotal, player.achievementTotal);
		int totalBossKills = NpcKillTracker.getNpcAmount(player.npcKills, "BOSS");
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (rank > highscoresList[index].rankNumber && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || player.getTotalLevel() > highscoresList[index].totalLevel && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || totalBossKills > highscoresList[index].bossKill && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].rankNumber = rank;
				highscoresList[index].totalLevel = player.getTotalLevel();
				highscoresList[index].bossKill = totalBossKills;
				highscoresList[index].gameMode = player.getGameMode();
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (rank > highscoresList[0].rankNumber) {
					highscoresList[0] = new HighscoresAdventurer(player.getPlayerName(), rank, player.getTotalLevel(), totalBossKills, player.getGameMode());
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].rankNumber > highscoresList[index + 1].rankNumber) {
					HighscoresAdventurer temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
		for (int index = 0; index < highscoresList.length - 1; index++) {
			if (highscoresList[index].rankNumber == highscoresList[index + 1].rankNumber && highscoresList[index].bossKill > highscoresList[index + 1].bossKill) {
				HighscoresAdventurer temp = highscoresList[index];
				highscoresList[index] = highscoresList[index + 1];
				highscoresList[index + 1] = temp;
			}
		}
	}
}
