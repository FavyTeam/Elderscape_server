package game.content.highscores;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.ServerConstants;
import utility.FileUtility;
import utility.Misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Trivia highscores.
 *
 * @author MGT Madness, created on23-02-2017.
 */
public class HighscoresTrivia {
	/**
	 * The class instance.
	 */
	private static final HighscoresTrivia instance = new HighscoresTrivia();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresTrivia getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/trivia.json";

	public HighscoresTrivia[] highscoresList;

	public String name;

	public int answers;


	public HighscoresTrivia() {
		this.name = "";
		this.answers = 0;
	}

	public HighscoresTrivia(String username, int answers) {
		this.name = username;
		this.answers = answers;
	}

	public void initiateHighscoresInstance() {
		highscoresList = new HighscoresTrivia[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresTrivia();
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
				int answers = 0;
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", answers")) {
					answers = reader.get("rank " + i + ", answers").getAsInt();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresTrivia(name, answers);
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
				object.addProperty("rank " + i + ", answers", highscoresList[i].answers);

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

	public void sortHighscores(String name) {
		name = Misc.capitalize(name);
		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (highscoresList[index].name.equalsIgnoreCase(name)) {
				highscoresList[index].answers++;
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(name)) {
				if (1 > highscoresList[0].answers) {
					highscoresList[0] = new HighscoresTrivia(name, 1);
					break;
				}
			}

		}
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].answers > highscoresList[index + 1].answers) {
					HighscoresTrivia temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}
	}
}
