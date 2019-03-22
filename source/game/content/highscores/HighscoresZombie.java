package game.content.highscores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
 * Melee Highscores.
 *
 * @author MGT Madness, created on 16-10-2016
 */
public class HighscoresZombie {
	/**
	 * The class instance.
	 */
	private static final HighscoresZombie instance = new HighscoresZombie();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresZombie getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/zombie.json";

	public HighscoresZombie[] highscoresList;

	public String name;

	public String partnerName;

	public int wave;

	public long timePutOnHighscores;

	public HighscoresZombie() {
		this.name = "";
		this.partnerName = "";
		this.wave = 0;
		this.timePutOnHighscores = 0;
	}

	public HighscoresZombie(String username, String partnerName, int wave, long timePutOnHighscores) {
		this.name = username;
		this.partnerName = partnerName;
		this.wave = wave;
		this.timePutOnHighscores = timePutOnHighscores;
	}

	public void initiateHighscoresInstance() {
		highscoresList = new HighscoresZombie[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresZombie();
		}
		loadFile();
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
				int wave = 0;
				String partnerName = "";
				long timePutOnHighscores = 0;
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", partnerName")) {
					partnerName = reader.get("rank " + i + ", partnerName").getAsString();
				}
				if (reader.has("rank " + i + ", wave")) {
					wave = reader.get("rank " + i + ", wave").getAsInt();
				}
				if (reader.has("rank " + i + ", timePutOnHighscores")) {
					timePutOnHighscores = reader.get("rank " + i + ", timePutOnHighscores").getAsLong();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresZombie(name, partnerName, wave, timePutOnHighscores);
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
				object.addProperty("rank " + i + ", partnerName", highscoresList[i].partnerName);
				object.addProperty("rank " + i + ", wave", highscoresList[i].wave);
				object.addProperty("rank " + i + ", timePutOnHighscores", highscoresList[i].timePutOnHighscores);

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

	public void sortHighscores(Player player, String partnerName) {
		if (player.isAdministratorRank()) {
			return;
		}

		if (player.isCombatBot()) {
			return;
		}
		String rankOne = highscoresList[29].name + highscoresList[29].partnerName;
		boolean foundMatch = false;
		for (int index = 0; index < highscoresList.length; index++) {
			// If i am more than my current self on Highscores, then overwrite my own self that is own Highscores.
			if (highscoresList[index].name.equals(player.getPlayerName()) && highscoresList[index].partnerName.equals(partnerName)) {
				if (player.highestZombieWave > highscoresList[index].wave) {
					highscoresList[index].wave = player.highestZombieWave;
					highscoresList[index].timePutOnHighscores = System.currentTimeMillis();
				}
				foundMatch = true;
				break;
			} else if (highscoresList[index].name.equals(partnerName) && highscoresList[index].partnerName.equals(player.getPlayerName())) {
				if (player.highestZombieWave > highscoresList[index].wave) {
					highscoresList[index].wave = player.highestZombieWave;
					highscoresList[index].timePutOnHighscores = System.currentTimeMillis();
				}
				foundMatch = true;
				break;
			}

		}
		if (!foundMatch) {
			if (player.highestZombieWave > highscoresList[0].wave) {
				highscoresList[0] = new HighscoresZombie(player.getPlayerName(), partnerName, player.highestZombieWave, System.currentTimeMillis());
			}
		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].wave > highscoresList[index + 1].wave) {
					HighscoresZombie temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}


		if (!rankOne.equals(highscoresList[29].name + highscoresList[29].partnerName)) {
			player.getPA()
			      .announce(highscoresList[29].name + " and " + highscoresList[29].partnerName + " have broken the record and reached wave " + highscoresList[29].wave + "!");
			player.getPA().sendScreenshot("zombies " + highscoresList[29].wave, 2);
		}
	}
}
