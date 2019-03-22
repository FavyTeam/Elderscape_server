package utility;

import core.ServerConfiguration;
import core.ServerConstants;
import java.util.ArrayList;

/**
 * Store all connection details to website and ability to update them without restarting server.
 *
 * @author MGT Madness, created on 27-08-2017.
 */
public class WebsiteLogInDetails {

	public static String IP_ADDRESS = "";

	public static String SQL_USERNAME = "";

	public static String SQL_PASSWORD = "";

	public static String VOTE_DATABASE_NAME = "";

	public static String DONATE_DATABASE_NAME = "";

	public static String WEBSITE_DEDICATED_SERVER_USERNAME = "";

	public static String WEBSITE_DEDICATED_SERVER_PASSWORD = "";

	public static int WEBSITE_DEDICATED_SERVER_PORT = 0;

	public static String WEBSITE_URL = "";

	public static String GITHUB_PASSWORD = "";

	public static int OSRS_DONATION_LIMIT = 100;

	public static String ADMIN_MOBILE_NUMBER = "";

	public static String OSRS_BOT_NAME = "";

	public static String OSRS_WORLD = "";

	public static double PLAYER_COUNT_MODIFIER = 1.0;

	public static boolean playerCountRead;

	public static boolean SPAWN_VERSION = false;

	/**
	 * Read the latest passwords.
	 */
	public static void readLatestWebsiteLogInDetails() {
		ArrayList<String> data = FileUtility.readFile(ServerConstants.getDedicatedServerConfigLocation() + "website_login_details.txt");

		for (int index = 0; index < data.size(); index++) {
			String string = data.get(index);
			String type = string.substring(0, string.indexOf("="));
			String password = string.replace(type + "=", "");
			switch (type) {
				case "IP_ADDRESS":
					IP_ADDRESS = password;
					break;
				case "VOTE_DATABASE_NAME":
					VOTE_DATABASE_NAME = password;
					break;
				case "DONATE_DATABASE_NAME":
					DONATE_DATABASE_NAME = password;
					break;
				case "SQL_USERNAME":
					SQL_USERNAME = password;
					break;
				case "SQL_PASSWORD":
					SQL_PASSWORD = password;
					break;
				case "WEBSITE_URL":
					WEBSITE_URL = password;
					break;
				case "WEBSITE_DEDICATED_SERVER_PORT":
					WEBSITE_DEDICATED_SERVER_PORT = Integer.parseInt(password);
					break;
				case "WEBSITE_DEDICATED_SERVER_USERNAME":
					WEBSITE_DEDICATED_SERVER_USERNAME = password;
					break;
				case "WEBSITE_DEDICATED_SERVER_PASSWORD":
					WEBSITE_DEDICATED_SERVER_PASSWORD = password;
					break;
				case "GITHUB_PASSWORD":
					GITHUB_PASSWORD = password;
					break;
				case "OSRS_DONATION_LIMIT":
					OSRS_DONATION_LIMIT = Integer.parseInt(password);
					break;
				case "ADMIN_MOBILE_NUMBER":
					ADMIN_MOBILE_NUMBER = password;
					break;
				case "OSRS_BOT_NAME" :
					OSRS_BOT_NAME = password;
					break;
				case "OSRS_WORLD" :
					OSRS_WORLD = password;
					break;
				case "SPAWN_VERSION" :
					SPAWN_VERSION = Boolean.parseBoolean(password);
					break;
				case "PLAYER_COUNT_MODIFIER" :
					if (!playerCountRead) {
						PLAYER_COUNT_MODIFIER = Double.parseDouble(password);
						playerCountRead = true;
					}
					break;
			}

			if (ServerConfiguration.DEBUG_MODE) {
				switch (type) {
					case "DEBUG_USERNAME":
						SQL_USERNAME = password;
						break;
					case "DEBUG_PASSWORD":
						SQL_PASSWORD = password;
						break;
				}
			}

			if (WebsiteLogInDetails.SPAWN_VERSION) {
				IP_ADDRESS = "";
			}
		}
	}

}
