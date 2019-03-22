package game.content.highscores;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.ServerConstants;
import game.content.profile.NpcKillTracker;
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
 * Pvm highscores.
 *
 * @author MGT Madness, created on 30-01-2016.
 */
public class HighscoresPvm {
	/**
	 * The class instance.
	 */
	private static final HighscoresPvm instance = new HighscoresPvm();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresPvm getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/pvm.json";

	public HighscoresPvm[] highscoresList;

	public String name;

	public int score;

	public int bossKills;

	public String mostKilledBoss;

	public String gameMode;

	public HighscoresPvm() {
		this.name = "";
		this.score = 0;
		this.bossKills = 0;
		this.mostKilledBoss = "";
		this.gameMode = "";
	}

	public HighscoresPvm(String username, int score, int bossKills, String mostKilledBoss, String gameMode) {
		this.name = username;
		this.score = score;
		this.bossKills = bossKills;
		this.mostKilledBoss = mostKilledBoss;
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
		highscoresList = new HighscoresPvm[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresPvm();
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
				int score = 0;
				int bossKills = 0;
				String mostKilledBoss = "";
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", score")) {
					score = reader.get("rank " + i + ", score").getAsInt();
				}
				if (reader.has("rank " + i + ", bossKills")) {
					bossKills = reader.get("rank " + i + ", bossKills").getAsInt();
				}
				if (reader.has("rank " + i + ", mostKilledBoss")) {
					mostKilledBoss = reader.get("rank " + i + ", mostKilledBoss").getAsString();
				}
				if (reader.has("rank " + i + ", gameMode")) {
					gameMode = reader.get("rank " + i + ", gameMode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresPvm(name, score, bossKills, mostKilledBoss, gameMode);
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
				object.addProperty("rank " + i + ", score", highscoresList[i].score);
				object.addProperty("rank " + i + ", bossKills", highscoresList[i].bossKills);
				object.addProperty("rank " + i + ", mostKilledBoss", highscoresList[i].mostKilledBoss);
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

	public void sortHighscores(Player player) {

		if (player.isCombatBot()) {
			return;
		}

		if (player.isAdministratorRank()) {
			return;
		}
		int bossKillScore = NpcKillTracker.getBossKillsScore(player.npcKills, false);
		int totalBossKills = NpcKillTracker.getNpcAmount(player.npcKills, "BOSS");
		String highestBossKilled = NpcKillTracker.getHighestBossKilled(player);
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (bossKillScore >= highscoresList[index].score && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || totalBossKills > highscoresList[index].bossKills && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].score = bossKillScore;
				highscoresList[index].bossKills = totalBossKills;
				highscoresList[index].mostKilledBoss = highestBossKilled;
				highscoresList[index].gameMode = player.getGameMode();
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (score > highscoresList[0].score || totalBossKills > highscoresList[0].bossKills) {
					highscoresList[0] = new HighscoresPvm(player.getPlayerName(), bossKillScore, totalBossKills, highestBossKilled, player.getGameMode());
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].score > highscoresList[index + 1].score) {
					HighscoresPvm temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
		for (int index = 0; index < highscoresList.length - 1; index++) {
			if (highscoresList[index].score == highscoresList[index + 1].score && highscoresList[index].bossKills < highscoresList[index + 1].bossKills) {
				HighscoresPvm temp = highscoresList[index];
				highscoresList[index] = highscoresList[index + 1];
				highscoresList[index + 1] = temp;
			}
		}
	}
}
