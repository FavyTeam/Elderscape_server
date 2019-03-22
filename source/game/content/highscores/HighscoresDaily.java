package game.content.highscores;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.GameType;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.ClaimPrize;
import game.player.Player;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import utility.FileUtility;
import utility.Misc;

/**
 * Daily highscores.
 *
 * @author MGT Madness, created on 21-03-2017.
 */
public class HighscoresDaily {


	public static int getDailyHighscoresPrizeAmount() {
		return GameType.isOsrsPvp() ? 25000 : 19000000;
	}

	/**
	 * The class instance.
	 */
	private static final HighscoresDaily instance = new HighscoresDaily();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresDaily getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/daily.json";

	public HighscoresDaily[] highscoresList;

	public String name;

	public int main;

	public int extra1;

	public String extra2;

	public String gameMode = "";

	public HighscoresDaily() {
		this.name = "";
		this.main = 0;
		this.extra1 = 0;
		this.extra2 = "";
		this.gameMode = "";
	}

	public HighscoresDaily(String username, int main, int extra1, String extra2, String gameMode) {
		this.name = username;
		this.main = main;
		this.extra1 = extra1;
		this.extra2 = extra2;
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
		highscoresList = new HighscoresDaily[200];
		for (int i = 0; i < 200; i++) {
			highscoresList[i] = new HighscoresDaily();
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

			for (int i = 0; i < 200; i++) {
				String name = "";
				int main = 0;
				int extra1 = 0;
				String extra2 = "";
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", main")) {
					main = reader.get("rank " + i + ", main").getAsInt();
				}
				if (reader.has("rank " + i + ", extra1")) {
					extra1 = reader.get("rank " + i + ", extra1").getAsInt();
				}
				if (reader.has("rank " + i + ", extra2")) {
					extra2 = reader.get("rank " + i + ", extra2").getAsString();
				}
				if (reader.has("rank " + i + ", gamemode")) {
					gameMode = reader.get("rank " + i + ", gamemode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresDaily(name, main, extra1, extra2, gameMode);
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
				object.addProperty("rank " + i + ", main", highscoresList[i].main);
				object.addProperty("rank " + i + ", extra1", highscoresList[i].extra1);
				object.addProperty("rank " + i + ", extra2", highscoresList[i].extra2);
				object.addProperty("rank " + i + ", gamemode", highscoresList[i].gameMode);
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

	public void sortHighscores(Player player, int mainVariable, int secondVariable, String extra, String gameMode) {
		if (player.isAdministratorRank()) {
			return;
		}

		if (player.isCombatBot()) {
			return;
		}
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].main += mainVariable;
				highscoresList[index].extra1 = secondVariable;
				highscoresList[index].extra2 = extra;
				highscoresList[index].gameMode = gameMode;
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (mainVariable > highscoresList[0].main) {
					highscoresList[0] = new HighscoresDaily(player.getPlayerName(), mainVariable, secondVariable, extra, gameMode);
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].main > highscoresList[index + 1].main) {
					HighscoresDaily temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
	}


	public final String[] DAILY_HIGHSCORES_LIST =
			{"Boss kills", "F2p kills", "Hybrid kills", "Target kills"};

	public String currentDailyHighscores = DAILY_HIGHSCORES_LIST[0];

	public final String FILE_LOCATION = "backup/logs/highscores/daily type.txt";

	public void saveDailyHighscoresType() {
		FileUtility.deleteAllLines(FILE_LOCATION);
		try {
			BufferedWriter bw = null;
			bw = new BufferedWriter(new FileWriter(FILE_LOCATION, true));
			bw.write(currentDailyHighscores);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void readDailyHighscoresType() {
		try (BufferedReader file = new BufferedReader(new FileReader(FILE_LOCATION))) {
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					currentDailyHighscores = line;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void informHighscores(Player player) {
		player.getPA().sendMessage(
				ServerConstants.DARK_BLUE + "Daily highscores: " + currentDailyHighscores + ". #1 spot receives " + Misc.formatRunescapeStyle(getDailyHighscoresPrizeAmount()) + " "
				+ ServerConstants.getMainCurrencyName().toLowerCase() + ".");
	}

	/**
	 * @param targetHourTime Target hour time (event for example)
	 * @param nightorDayTarget AM or PM of the target hour time
	 * @param result TECHNICAL if i want the result to print out minutes left.
	 * <p>
	 * HIGHSCORES if i want the result to print out 1h 30m instead of 90
	 * @return
	 */
	public String getTimeLeft(String eventTimeString, String result) {
		String currentTimeString = Misc.getDateOrTimeWithFormat("hh:mm a");
		long currentTime = Misc.dateToMilliseconds(currentTimeString, "hh:mm a");
		long eventTime = Misc.dateToMilliseconds(eventTimeString, "hh:mm a");
		long resultLong = eventTime - currentTime;
		if (resultLong < 0L) {
			resultLong += Misc.getMinutesToMilliseconds(1440);
		}
		return result.equals("TECHNICAL") ? (resultLong + "") : Misc.getTimeLeft((int) Misc.getMillisecondsToSeconds(resultLong));
	}

	public boolean claimedReward;

	public void dateChanged() {
		if (highscoresList[199].name.isEmpty()) {
			return;
		}
		claimedReward = true;
		int prizeAmount = getDailyHighscoresPrizeAmount();
		if (highscoresList[199].gameMode.contains("IRON MAN")) {
			prizeAmount /= 10;
		}
		Announcement.announce(
				highscoresList[199].name + " has won the daily " + currentDailyHighscores + " highscores and is awarded " + Misc.formatRunescapeStyle(prizeAmount) + " "
				+ ServerConstants.getMainCurrencyName().toLowerCase() + "!", ServerConstants.DARK_BLUE);
		ClaimPrize.eventNames.add(highscoresList[199].name + "-" + prizeAmount);

		for (int index = 0; index < DAILY_HIGHSCORES_LIST.length; index++) {
			if (DAILY_HIGHSCORES_LIST[index].equals(currentDailyHighscores)) {
				int newIndex = index + 1;
				if (newIndex > DAILY_HIGHSCORES_LIST.length - 1) {
					newIndex = 0;
				}
				currentDailyHighscores = DAILY_HIGHSCORES_LIST[newIndex];
				break;
			}
		}
		Announcement.announce(
				"The new daily highscores is " + currentDailyHighscores + ", the #1 spot will claim " + Misc.formatRunescapeStyle(getDailyHighscoresPrizeAmount()) + " "
				+ ServerConstants.getMainCurrencyName().toLowerCase() + " in 24h!", ServerConstants.DARK_BLUE);

		highscoresList = new HighscoresDaily[200];
		for (int i = 0; i < 200; i++) {
			highscoresList[i] = new HighscoresDaily();
		}
	}
}
