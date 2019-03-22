package tools.discord.content;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.highscores.HighscoresTrivia;
import sx.blah.discord.handle.obj.IMessage;
import tools.discord.api.DiscordBot;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Trivia system for discord.
 *
 * @author MGT Madness, created on 23-02-2017.
 */
public class DiscordTrivia {
	/**
	 * Store questions.
	 */
	public static ArrayList<String> questions = new ArrayList<String>();

	/**
	 * Store the multipler answers for each question.
	 */
	public static ArrayList<String> answers = new ArrayList<String>();

	/**
	 * Store current question index.
	 */
	public static int currentQuestionIndex = -1;

	/**
	 * Load trivia.txt questions and answers.
	 */
	public static void loadTriviaFile() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("data/content/trivia.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					if (line.contains("?")) {
						questions.add(line);
					} else {
						answers.add(line);
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ask a trivia question to discord.
	 */
	public static void askQuestion() {
		if (!ServerConfiguration.DISCORD) {
			return;
		}
		currentQuestionIndex = Misc.random(questions.size() - 1);
		DiscordBot.announce("[Trivia]" + questions.get(currentQuestionIndex), true);
		DiscordBot.announce("Answer for 2,000 blood money on ::discord using !trivia youranswer", true);
	}

	public static void answerQuestion(IMessage message, String playerAnswer) {
		String playerName = message.getAuthor().getName();
		playerAnswer = playerAnswer.toLowerCase();
		playerAnswer = playerAnswer.replace("trivia ", "");
		if (currentQuestionIndex == -1) {
			DiscordBot.announce("No question available.", false);
			return;
		}
		String parseAnswers[] = answers.get(currentQuestionIndex).split("/");
		for (int index = 0; index < parseAnswers.length; index++) {
			if (parseAnswers[index].toLowerCase().equals(playerAnswer)) {
				DiscordBot.announce("[Trivia]" + playerName + " has won the prize!", true);
				DiscordBot.announce("::claimevent in-game to receive your reward.", false);
				//CommunityEvent.eventNames.add(playerName.toLowerCase() + "-2000");
				HighscoresTrivia.getInstance().sortHighscores(message.getAuthor().getName());
				HighscoresTrivia.getInstance().saveFile();
				shoutHighscores();
				currentQuestionIndex = -1;
				return;
			}
		}
		DiscordBot.announce("Wrong answer.", false);
	}

	static void shoutHighscores() {
		String text = "[TRIVIA] Leaderboard: \n";
		for (int i = HighscoresTrivia.getInstance().highscoresList.length - 1; i > -1; i--) {
			if (HighscoresTrivia.getInstance().highscoresList[i].name.isEmpty()) {
				continue;
			}
			text = text + "#" + (ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + " " + HighscoresTrivia.getInstance().highscoresList[i].name + " has " + HighscoresTrivia
					                                                                                                                                              .getInstance().highscoresList[i].answers
			       + " answers! \n";
		}
		DiscordBot.announce(text, false);

	}


}
