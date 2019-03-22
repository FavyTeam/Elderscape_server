package game.bot;

import core.ServerConfiguration;
import network.login.RS2LoginProtocolDecoder;

/**
 * Bot manager class.
 *
 * @author MGT Madness, created on 20-04-2016.
 */
public class BotManager {

	/**
	 * Names of bots, my friends that i used to play with in the past.
	 */
	public final static String[] botNames =
			{
					// @formatter:off.
					"Sketchy", "Miamii", "Nope not", "Rahm65", "Omfg Xbow",
					"Im Farmer", "G 0 L D Dick", "Tubek", "Th E Roy Alz", "Remy E",
					"Str8 Ballin", "5susan5", "Maxipads", "Morgana",
					"Sabrex828", "Xupa05", "Kannaduh", "Dejwis", "I Swagger I", "Lolo Pendew", "Not A Banana", "High     Def",
					"Soldjr", "Nottingham", "Inequality", "Eliohu", "Mihaimihai", "Smoke", "Dunowathitem", "Salocuk", "Leon5262149", "Purplelionn", "Soulofwar999",
					// @formatter:on.
			};

	/**
	 * Amount of bots to log in. If number is more than 50, all bots will log in.
	 */
	public final static int BOTS_AMOUNT = ServerConfiguration.STABILITY_TEST ? 200 : 10;

	public static int currentBotNumber;

	/**
	 * Log in the bots on server start-up.
	 */
	public static void logInBots() {
		if (!ServerConfiguration.ENABLE_BOTS) {
			return;
		}
		if (ServerConfiguration.STABILITY_TEST) {
			for (int index = 0; index < BOTS_AMOUNT; index++) {
				RS2LoginProtocolDecoder.loadBot("bot" + index, "dattebayod3d", true);
			}
			return;
		}
		for (int index = 0; index < BOTS_AMOUNT; index++) {
			String name = index < botNames.length ? botNames[index] : String.format("Bot%s", index);

			RS2LoginProtocolDecoder.loadBot(name, "dattebayod3d", true);
		}
	}

}
