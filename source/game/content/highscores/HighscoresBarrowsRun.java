package game.content.highscores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.ServerConfiguration;
import core.ServerConstants;
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
 * Barrows run Highscores.
 *
 * @author MGT Madness, created on 10-08-2016.
 */
public class HighscoresBarrowsRun {
	/**
	 * The class instance.
	 */
	private static final HighscoresBarrowsRun instance = new HighscoresBarrowsRun();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresBarrowsRun getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/Highscores/barrows run.json";

	public HighscoresBarrowsRun[] highscoresList;

	public String name;

	public int barrowsTime;

	public int barrowsRunAmount;

	public long time;

	public String gameMode;

	public HighscoresBarrowsRun() {
		this.name = "";
		this.barrowsTime = Integer.MAX_VALUE;
		this.barrowsRunAmount = 0;
		this.time = 0;
		this.gameMode = "";
	}

	public HighscoresBarrowsRun(String username, int barrowsTime, int barrowsRunAmount, long time, String gameMode) {
		this.name = username;
		this.barrowsTime = barrowsTime;
		this.barrowsRunAmount = barrowsRunAmount;
		this.time = time;
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
		highscoresList = new HighscoresBarrowsRun[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresBarrowsRun();
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
				int barrowsTime = 0;
				int barrowsRunAmount = 0;
				long time = 0;
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", barrows time")) {
					barrowsTime = reader.get("rank " + i + ", barrows time").getAsInt();
				}
				if (reader.has("rank " + i + ", barrows run amount")) {
					barrowsRunAmount = reader.get("rank " + i + ", barrows run amount").getAsInt();
				}
				if (reader.has("rank " + i + ", time")) {
					time = reader.get("rank " + i + ", time").getAsLong();
				}
				if (reader.has("rank " + i + ", game mode")) {
					gameMode = reader.get("rank " + i + ", game mode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresBarrowsRun(name, barrowsTime, barrowsRunAmount, time, gameMode);
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
				object.addProperty("rank " + i + ", barrows time", highscoresList[i].barrowsTime);
				object.addProperty("rank " + i + ", barrows run amount", highscoresList[i].barrowsRunAmount);
				object.addProperty("rank " + i + ", time", highscoresList[i].time);
				object.addProperty("rank " + i + ", game mode", highscoresList[i].gameMode);
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
		if (player.isAdministratorRank() && !ServerConfiguration.DEBUG_MODE) {
			return;
		}
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on Highscores, then overwrite my own self that is own Highscores.
			if (player.barrowsPersonalRecord < (highscoresList[index].barrowsTime == 0 ? Integer.MAX_VALUE : highscoresList[index].barrowsTime) && highscoresList[index].name
					                                                                                                                                       .equalsIgnoreCase(
							                                                                                                                                       player.getPlayerName())
			    || player.getBarrowsRunCompleted() > highscoresList[index].barrowsRunAmount && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].barrowsTime = player.barrowsPersonalRecord;
				highscoresList[index].barrowsRunAmount = player.getBarrowsRunCompleted();
				highscoresList[index].time = System.currentTimeMillis();
				break;
			}

			// If i am not on Highscores, index 0 is the last spot on the Highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (player.barrowsPersonalRecord < (highscoresList[0].barrowsTime == 0 ? Integer.MAX_VALUE : highscoresList[0].barrowsTime)) {
					highscoresList[0] = new HighscoresBarrowsRun(player.getPlayerName(), player.barrowsPersonalRecord, player.getBarrowsRunCompleted(), System.currentTimeMillis(),
					                                             player.getGameMode());
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if ((highscoresList[index].barrowsTime == 0 ? Integer.MAX_VALUE : highscoresList[index].barrowsTime) < (highscoresList[index + 1].barrowsTime == 0 ?
						                                                                                                        Integer.MAX_VALUE :
						                                                                                                        highscoresList[index + 1].barrowsTime)) {
					HighscoresBarrowsRun temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
	}
}
