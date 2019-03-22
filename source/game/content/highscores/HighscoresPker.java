package game.content.highscores;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.ServerConstants;
import game.content.achievement.AchievementStatistics;
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
public class HighscoresPker {
	/**
	 * The class instance.
	 */
	private static final HighscoresPker instance = new HighscoresPker();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresPker getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/pker.json";

	public HighscoresPker[] highscoresList;

	public String name;

	public int rankNumber;

	public int kill;

	public double kdr;

	public String gameMode;

	public HighscoresPker() {
		this.name = "";
		this.rankNumber = 0;
		this.kill = 0;
		this.kdr = 0;
		this.gameMode = "";
	}

	public HighscoresPker(String username, int rank, int kill, double kdr, String gameMode) {
		this.name = username;
		this.rankNumber = rank;
		this.kill = kill;
		this.kdr = kdr;
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
		highscoresList = new HighscoresPker[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresPker();
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
				int kill = 0;
				double kdr = 0;
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", rankNumber")) {
					rankNumber = reader.get("rank " + i + ", rankNumber").getAsInt();
				}
				if (reader.has("rank " + i + ", kill")) {
					kill = reader.get("rank " + i + ", kill").getAsInt();
				}
				if (reader.has("rank " + i + ", kdr")) {
					kdr = reader.get("rank " + i + ", kdr").getAsDouble();
				}
				if (reader.has("rank " + i + ", gameMode")) {
					gameMode = reader.get("rank " + i + ", gameMode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresPker(name, rankNumber, kill, kdr, gameMode);
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
				object.addProperty("rank " + i + ", kill", highscoresList[i].kill);
				object.addProperty("rank " + i + ", kdr", highscoresList[i].kdr);
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
		if (player.isAdministratorRank()) {
			return;
		}

		if (player.isCombatBot()) {
			return;
		}
		int rank = ProfileRank.getPkingRankNumber(player.getMeleeMainKills(), player.getHybridKills(), player.getPureKills(), player.getBerserkerPureKills(),
		                                          player.getRangedTankKills());
		double kdr = Misc.getKDR(player.getWildernessKills(true), player.getWildernessDeaths(true));
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (rank > highscoresList[index].rankNumber && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || player.getWildernessKills(true) > highscoresList[index].kill && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || kdr != highscoresList[index].kdr && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].rankNumber = rank;
				highscoresList[index].kill = player.getWildernessKills(true);
				highscoresList[index].kdr = kdr;
				highscoresList[index].gameMode = player.getGameMode();
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (player.getMeleeMainKills() > highscoresList[0].rankNumber || player.deathTypes[AchievementStatistics.MELEE_MAIN] > highscoresList[0].kill) {
					highscoresList[0] = new HighscoresPker(player.getPlayerName(), rank, player.getWildernessKills(true), kdr, player.getGameMode());
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].rankNumber > highscoresList[index + 1].rankNumber) {
					HighscoresPker temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
		for (int index = 0; index < highscoresList.length - 1; index++) {
			if (highscoresList[index].rankNumber == highscoresList[index + 1].rankNumber && highscoresList[index].kill > highscoresList[index + 1].kill) {
				HighscoresPker temp = highscoresList[index];
				highscoresList[index] = highscoresList[index + 1];
				highscoresList[index + 1] = temp;
			}
		}
	}
}
