package game.content.highscores;

import game.player.Player;

/**
 * Load all highscore systems here.
 *
 * @author m g t
 */
public class Highscores {

	/**
	 * Initiate all highscores instances.
	 */
	public static void initiateHighscoresInstance() {
		HighscoresPure.getInstance().initiateHighscoresInstance();
		HighscoresTotalLevel.getInstance().initiateHighscoresInstance();
		HighscoresTotalLevelGladiator.getInstance().initiateHighscoresInstance();
		HighscoresBerserker.getInstance().initiateHighscoresInstance();
		HighscoresMelee.getInstance().initiateHighscoresInstance();
		HighscoresHybrid.getInstance().initiateHighscoresInstance();
		HighscoresRangedTank.getInstance().initiateHighscoresInstance();
		HighscoresPker.getInstance().initiateHighscoresInstance();
		HighscoresPvm.getInstance().initiateHighscoresInstance();
		HighscoresBarrowsRun.getInstance().initiateHighscoresInstance();
		HighscoresTournament.getInstance().initiateHighscoresInstance();
		HighscoresDaily.getInstance().initiateHighscoresInstance();
		HighscoresF2p.getInstance().initiateHighscoresInstance();
	}

	/**
	 * Save all highscores files.
	 */
	public static void saveHighscoresFiles() {
		HighscoresPure.getInstance().saveFile();
		HighscoresTotalLevel.getInstance().saveFile();
		HighscoresTotalLevelGladiator.getInstance().saveFile();
		HighscoresBerserker.getInstance().saveFile();
		HighscoresMelee.getInstance().saveFile();
		HighscoresHybrid.getInstance().saveFile();
		HighscoresRangedTank.getInstance().saveFile();
		HighscoresPker.getInstance().saveFile();
		HighscoresPvm.getInstance().saveFile();
		HighscoresBarrowsRun.getInstance().saveFile();
		HighscoresTournament.getInstance().saveFile();
		HighscoresDaily.getInstance().saveFile();
		HighscoresF2p.getInstance().saveFile();
	}

	/**
	 * Sort all highscores depending on variable changed.
	 *
	 * @param player The associated player.
	 */
	public static void sortHighscoresOnLogOut(Player player) {
		HighscoresTotalLevel.getInstance().sortHighscores(player);
		HighscoresTotalLevelGladiator.getInstance().sortHighscores(player);
		HighscoresPvm.getInstance().sortHighscores(player);
	}

	public static void nameChangeUpdateHighscores(String oldName, String newName1) {
		HighscoresPure.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresTotalLevel.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresTotalLevelGladiator.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresBerserker.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresMelee.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresHybrid.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresRangedTank.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresPker.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresPvm.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresBarrowsRun.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresTournament.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresDaily.getInstance().changeNameOnHighscores(oldName, newName1);
		HighscoresF2p.getInstance().changeNameOnHighscores(oldName, newName1);
	}

}
