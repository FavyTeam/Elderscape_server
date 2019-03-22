package game.content.highscores;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.GameType;
import core.ServerConstants;
import game.content.profile.ProfileSearch;
import game.content.starter.GameMode;
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
 * Total level highscores.
 *
 * @author MGT Madness, created on 30-01-2016.
 */
public class HighscoresTotalLevel {
	/**
	 * The class instance.
	 */
	private static final HighscoresTotalLevel instance = new HighscoresTotalLevel();

	/**
	 * Returns a visible encapsulation of the class instance.
	 *
	 * @return The returned encapsulated instance.
	 */
	public final static HighscoresTotalLevel getInstance() {
		return instance;
	}

	private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/total level.json";

	public HighscoresTotalLevel[] highscoresList;

	public String name;

	public int totalLevel;

	public int xp;

	public String highestSkill;

	public String gameMode;

	public HighscoresTotalLevel() {
		this.name = "";
		this.totalLevel = 0;
		this.xp = 0;
		this.highestSkill = "";
		this.gameMode = "";
	}

	public HighscoresTotalLevel(String username, int totalLevel, int xpInMillion, String highestSkill, String gameMode) {
		this.name = username;
		this.totalLevel = totalLevel;
		this.xp = xpInMillion;
		this.highestSkill = highestSkill;
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
		highscoresList = new HighscoresTotalLevel[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
		for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++) {
			highscoresList[i] = new HighscoresTotalLevel();
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
				int totalLevel = 0;
				int xp = 0;
				String highestSkill = "";
				String gameMode = "";
				if (reader.has("rank " + i + ", name")) {
					name = reader.get("rank " + i + ", name").getAsString();
				}
				if (reader.has("rank " + i + ", total level")) {
					totalLevel = reader.get("rank " + i + ", total level").getAsInt();
				}
				if (reader.has("rank " + i + ", xp")) {
					xp = reader.get("rank " + i + ", xp").getAsInt();
				}
				if (reader.has("rank " + i + ", highestSkill")) {
					highestSkill = reader.get("rank " + i + ", highestSkill").getAsString();
				}
				if (reader.has("rank " + i + ", gameMode")) {
					gameMode = reader.get("rank " + i + ", gameMode").getAsString();
				}
				if (!name.isEmpty()) {
					highscoresList[i] = new HighscoresTotalLevel(name, totalLevel, xp, highestSkill, gameMode);
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
				object.addProperty("rank " + i + ", total level", highscoresList[i].totalLevel);
				object.addProperty("rank " + i + ", xp", highscoresList[i].xp);
				object.addProperty("rank " + i + ", highestSkill", highscoresList[i].highestSkill);
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
				highscoresList[index] = new HighscoresTotalLevel("", 0, 0, "", "");
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
		if (GameType.isOsrsEco()) {
			if (GameMode.getDifficulty(player, "GLADIATOR") || GameMode.getGameMode(player, "PKER")) {
				return;
			}
		}

		for (int index = 0; index < highscoresList.length; index++) {

			// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
			if (player.getTotalLevel() > highscoresList[index].totalLevel && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())
			    || (player.getXpTotal() / 1000000) > highscoresList[index].xp && highscoresList[index].name.equalsIgnoreCase(player.getPlayerName())) {
				highscoresList[index].totalLevel = player.getTotalLevel();
				highscoresList[index].xp = (int) (player.getXpTotal() / 1000000);
				highscoresList[index].highestSkill = ProfileSearch.getHighestSkillXp(player.getAbleToEditCombat(), player.skillExperience, player.combatExperienceGainedAfterMaxed);
				highscoresList[index].gameMode = player.getGameMode();
				break;
			}

			// If i am not on highscores, index 0 is the last spot on the highscores.
			else if (!isOnHighscores(player.getPlayerName())) {
				if (player.getTotalLevel() > highscoresList[0].totalLevel || (int) (player.getXpTotal() / 1000000) > highscoresList[0].xp) {
					highscoresList[0] = new HighscoresTotalLevel(player.getPlayerName(), player.getTotalLevel(), (int) (player.getXpTotal() / 1000000), ProfileSearch
							                                                                                                                                    .getHighestSkillXp(
									                                                                                                                                    player.getAbleToEditCombat(),
									                                                                                                                                    player.skillExperience,
									                                                                                                                                    player.combatExperienceGainedAfterMaxed),
					                                             player.getGameMode());
					break;
				}
			}

		}

		// If total level higher than above me, go up.
		for (int counter = 0; counter < highscoresList.length - 1; counter++) {
			for (int index = 0; index < highscoresList.length - 1 - counter; index++) {
				if (highscoresList[index].totalLevel > highscoresList[index + 1].totalLevel) {
					HighscoresTotalLevel temp = highscoresList[index];
					highscoresList[index] = highscoresList[index + 1];
					highscoresList[index + 1] = temp;
				}
			}
		}

		// If total level is same and i have more xp, go up.
		for (int index = 0; index < highscoresList.length - 1; index++) {
			if (highscoresList[index].totalLevel == highscoresList[index + 1].totalLevel && highscoresList[index].xp > highscoresList[index + 1].xp) {
				HighscoresTotalLevel temp = highscoresList[index];
				highscoresList[index] = highscoresList[index + 1];
				highscoresList[index + 1] = temp;
			}
		}
	}
}
