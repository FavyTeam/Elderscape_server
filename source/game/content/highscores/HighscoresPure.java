package game.content.highscores;


import com.google.gson.*;
import core.ServerConstants;
import game.content.achievement.AchievementStatistics;
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
 * Pure highscores.
 *
 * @author MGT Madness, created on 30-01-2016.
 */
public class HighscoresPure {
	/**
	 * The class instance.
	 */
	private static final HighscoresPure instance = new HighscoresPure();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresPure getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/pure kills.json";

	public HighscoresPure[] highscoresList;

	public String name;

	public int kills;

	public int deaths;

	public double kdr;

	public String gameMode;

	public HighscoresPure() {
		this.name = "";
		this.kills = 0;
		this.deaths = 0;
		this.kdr = 0;
		this.gameMode = "";
	}

	public HighscoresPure(String username, int playerKills, int playerDeaths, double kdr, String gameMode) {
		this.name = username;
		this.kills = playerKills;
		this.deaths = playerDeaths;
		this.kdr = kdr;
		this.gameMode = gameMode;
	}

	public void initiateHighscoresInstance() {
		highscoresList = new HighscoresPure[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresPure();
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

			JsonElement result = fileParser.parse(fileReader);

			if (result instanceof JsonNull) {
				return;
			}
			JsonObject reader = (JsonObject) result;

			for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
				String name = "";
				int kills = 0;
				int deaths = 0;
				double kdr = 0;
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", kills")) {
					kills = reader.get("rank " + i + ", kills").getAsInt();
				}
				if (reader.has("rank " + i + ", deaths")) {
					deaths = reader.get("rank " + i + ", deaths").getAsInt();
				}
				if (reader.has("rank " + i + ", kdr")) {
					kdr = reader.get("rank " + i + ", kdr").getAsDouble();
				}
				if (reader.has("rank " + i + ", gameMode")) {
					gameMode = reader.get("rank " + i + ", gameMode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresPure(name, kills, deaths, kdr, gameMode);
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
				object.addProperty("rank " + i + ", kills", highscoresList[i].kills);
				object.addProperty("rank " + i + ", deaths", highscoresList[i].deaths);
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

	public void sortHighscores(Player player) {
		if (player.isAdministratorRank()) {
			return;
		}

		if (player.isCombatBot()) {
			return;
		}
		if (player.getPureKills() == 0) {
			return;
		}
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (player.getPureKills() > highscoresList[index].kills && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || player.deathTypes[AchievementStatistics.PURE] > highscoresList[index].deaths && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].kills = player.getPureKills();
				highscoresList[index].deaths = player.deathTypes[AchievementStatistics.PURE];
				highscoresList[index].kdr = Misc.getKDR(highscoresList[index].kills, highscoresList[index].deaths);
				highscoresList[index].gameMode = player.getGameMode();
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (player.getPureKills() > highscoresList[0].kills || player.deathTypes[AchievementStatistics.PURE] > highscoresList[0].deaths) {
					highscoresList[0] = new HighscoresPure(player.getPlayerName(), player.getPureKills(), player.deathTypes[AchievementStatistics.PURE],
					                                       Misc.getKDR(player.getPureKills(), player.deathTypes[AchievementStatistics.PURE]), player.getGameMode());
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].kills > highscoresList[index + 1].kills) {
					HighscoresPure temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
		for (int index = 0; index < highscoresList.length - 1; index++) {
			if (highscoresList[index].kills == highscoresList[index + 1].kills && highscoresList[index].deaths < highscoresList[index + 1].deaths) {
				HighscoresPure temp = highscoresList[index];
				highscoresList[index] = highscoresList[index + 1];
				highscoresList[index + 1] = temp;
			}
		}
	}
}
