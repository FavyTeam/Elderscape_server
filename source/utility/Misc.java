package utility;


import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Blacklist;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import network.sql.batch.impl.OutputBatchUpdateEvent;
import network.sql.table.impl.OutputSQLTableEntry;
import org.apache.commons.io.IOUtils;

public final class Misc {

	private Misc() {
		throw new RuntimeException();
	}

	public static boolean timeElapsed(long lastTimeApplied, long millisecondsPassed) {
		return System.currentTimeMillis() - lastTimeApplied >= millisecondsPassed;
	}

	public static int lockon(Player player) {
		return -player.getPlayerId() - 1;
	}

	public static int toLocal(int value, int regionValue) {
		return value - 8 * regionValue;
	}

	public static ArrayList<String> readWebsiteContent(String websiteUrl)
	{
		ArrayList<String> text = new ArrayList<String>();
		try {
			URL url = new URL(websiteUrl);
		URLConnection con = url.openConnection();
			con.addRequestProperty("User-Agent", "Mozilla/4.76");
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding(); // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
		encoding = encoding == null ? "UTF-8" : encoding;
		String body;
			body = IOUtils.toString(in, encoding);
			text = new ArrayList<String>(Arrays.asList(body.split("\\r?\\n")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * @return 5.5 for example.
	 */
	public static double roundDoubleToNearestOneDecimalPlace(double number) {
		DecimalFormat df = new DecimalFormat("#.#");
		return Double.parseDouble(df.format(number));
	}

	/**
	 * Shorted to 5.66 for example.
	 */
	public static double roundDoubleToNearestTwoDecimalPlaces(double number) {
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.parseDouble(df.format(number));
	}

	public static double getDoubleRoundedUp(double doubleNumber) {
		return Math.ceil(doubleNumber);
	}

	public static double getDoubleRoundedDown(double doubleNumber) {
		return (double) ((int) doubleNumber);
	}

	/**
	 * Retrieves an element from the list at random.
	 *
	 * @param elements the elements we're selecting from.
	 * @param <T> the type of list of elements.
	 * @return the random element from the list.
	 */
	public static <T> T random(List<T> elements) {
		if (elements.isEmpty()) {
			return null;
		}
		return elements.get(ThreadLocalRandom.current().nextInt(0, elements.size()));
	}

	/**
	 * Retrieves an element from the list at random without the provided elements.
	 *
	 * @param elements the elements we're selecting from.
	 * @param <T> the type of list of elements.
	 * @return the random element from the list.
	 */
	public static <T> T randomWithout(List<T> elements, T... without) {
		if (elements.isEmpty()) {
			return null;
		}
		List<T> copy = new ArrayList<>(elements);

		copy.removeAll(Arrays.asList(without));

		return copy.get(ThreadLocalRandom.current().nextInt(0, copy.size()));
	}

	/**
	 * Retrieves an element from the array at random.
	 *
	 * @param elements the array of elements we're selecting from.
	 * @param <T> the type of element.
	 * @return a single random element from the array.
	 */
	public static <T> T random(T[] elements) {
		return elements[ThreadLocalRandom.current().nextInt(0, elements.length)];
	}

	/**
	 * True if the server has been online for less than 2 hours.
	 */
	public static boolean serverRecentlyLaunched() {
		int serverUptimeMinutes = (int) ((System.currentTimeMillis() - Server.timeServerOnline) / ServerConstants.MILLISECONDS_MINUTE);

		if (serverUptimeMinutes < 120) {
			return true;
		}
		return false;
	}

	/**
	 * Determines whether or not the entity is within the local viewport of the entity.
	 *
	 * @param viewportX the viewport origin x.
	 * @param viewportY the viewport origin y.
	 * @param targetX the target x.
	 * @param targetY the target y.
	 * @return {@code true} if the result of subtracting each x and each y from each other is <= 15 && >= -16.
	 */
	public static boolean withinLocalViewport(int viewportX, int viewportY, int targetX, int targetY) {
		int deltaX = targetX - viewportX;

		int deltaY = targetY - viewportY;

		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	/**
	 * @return Return the <img=x> tag depending on the gameMode given is Ironman related.
	 */
	public static String getIronManCrown(String gameMode) {
		switch (gameMode) {
			case "STANDARD IRON MAN":
				return "<img=9> ";
			case "HARDCORE IRON MAN":
				return "<img=26> ";
			case "ULTIMATE IRON MAN":
				return "<img=25> ";
		}
		return "";
	}

	private static final char[] validChars =
			{'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
					'6', '7', '8', '9'
			};

	public static boolean isSamePlayer(Player one, Player two) {
		if (Misc.uidMatches(one.addressUid, two.addressUid) || one.addressIp.equals(two.addressIp) && !one.addressIp.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean containsPassword(String password, String text) {
		password = password.toLowerCase();
		text = text.toLowerCase();

		// Incase a players password is something common, so it only triggers if they say their password clearly.
		if (text.equals(password) || text.startsWith(password + " ") || text.contains(" " + password + " ") || text.endsWith(" " + password)) {
			return true;
		}
		return false;
	}

	public static void printWarning(String string) {
		print("[WARNING]: " + string);
	}



	public static int distanceToPoint(int firstX, int firstY, int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(firstX - pointX, 2) + Math.pow(firstY - pointY, 2));
	}

	public static long stringToLong(String s) {
		long l = 0L;
		for (int i = 0; i < s.length() && i < 12; i++) {
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}

		for (; l % 37L == 0L && l != 0L; l /= 37L)
			;
		return l;
	}

	public static int countOccurrences(String string, char needle) {
		int count = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == needle) {
				count++;
			}
		}
		return count;
	}

	public static String getTimeLeft(int seconds) {
		int hours = 0;
		int minutes = 0;
		int secondsLeft = 0;

		String time = "";
		if (seconds > 3600) {
			hours = seconds / 3600;
			time = hours + " hour" + Misc.getPluralS(hours);
		}
		int value1 = 0;
		value1 = seconds - (hours * 3600);
		if (value1 > 0) {
			minutes = value1 / 60;
			if (minutes > 0) {
				if (!time.isEmpty()) {
					time = time + " ";
				}
				time = time + minutes + " minute" + Misc.getPluralS(minutes);
			}
		}
		int value2 = value1 - (minutes * 60);
		if (value2 > 0) {
			secondsLeft = value2;


			if (!time.isEmpty()) {
				time = time + " ";
			}
			time = time + secondsLeft + " second" + Misc.getPluralS(secondsLeft);
		}

		return time;
	}

	public static int readPropertyInteger(Properties property, String string) {
		return Integer.parseInt(property.getProperty(string));
	}

	public static double readPropertyDouble(Properties property, String string) {
		String value = property.getProperty(string);

		if (value == null || value.isEmpty()) {
			return 0.0;
		}
		return Double.parseDouble(property.getProperty(string));
	}

	public static boolean readPropertyBoolean(Properties property, String string) {
		return Boolean.parseBoolean(property.getProperty(string));
	}


	public static String readPropertyString(Properties property, String string) {
		return property.getProperty(string);
	}

	/**
	 * @return -1 if the array does not contain the given number. 0 or more will return the matching index with the number.
	 */
	public static int getArrayIndex(int[] array, int number) {
		for (int index = 0; index < array.length; index++) {
			if (number == array[index]) {
				return index;
			}
		}
		return -1;
	}

	public static boolean arrayHasNumber(int[] array, int number) {
		for (int index = 0; index < array.length; index++) {
			if (number == array[index]) {
				return true;
			}
		}
		return false;
	}

	public static boolean arrayHasNumber(int[][] array, int number, int indexMatch) {
		for (int index = 0; index < array.length; index++) {
			if (number == array[index][indexMatch]) {
				return true;
			}
		}
		return false;
	}

	public static int arrayHasNumber(int[][] array, int number, int indexMatch, int indexReturn) {
		for (int index = 0; index < array.length; index++) {
			if (number == array[index][indexMatch]) {
				return array[index][indexReturn];
			}
		}
		return -1;
	}

	public static boolean isFlaggedOffensiveName(String name) {
		name = name.toLowerCase();
		for (int index = 0; index < ServerConstants.FLAGGED_NAMES.length; index++) {
			if (name.toLowerCase().contains(ServerConstants.FLAGGED_NAMES[index])) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public static int convertToPositive(int number) {
		return Math.abs(number);
	}

	/**
	 * Make sure the uid given is a parsed one!
	 */
	private static boolean isInvalidParsedUid(String uid) {
		if (uid.equals("invalid")) {
			return true;
		}
		return false;
	}

	public static boolean uidMatches(String currentUid, String matchingWithUid) {
		if (!currentUid.contains(ServerConstants.UUID_SEPERATOR) && !matchingWithUid.contains(ServerConstants.UUID_SEPERATOR)) {
			if (!Blacklist.useAbleData(matchingWithUid)) {
				return false;
			}
			if (!Blacklist.useAbleData(currentUid)) {
				return false;
			}
			if (isInvalidParsedUid(currentUid)) {
				return false;
			}
			if (isInvalidParsedUid(matchingWithUid)) {
				return false;
			}
			currentUid = formatUid(currentUid);
			matchingWithUid = formatUid(matchingWithUid);
			if (matchingWithUid.equals(currentUid)) {
				return true;
			}
			return false;
		}
		else {
			ArrayList<String> currentUidList = new ArrayList<String>();

			if (!currentUid.contains(ServerConstants.UUID_SEPERATOR)) {
				currentUidList.add(currentUid);
			} else {
				String[] parseCurrentUid = currentUid.split(ServerConstants.UUID_SEPERATOR);
				for (int index = 0; index < parseCurrentUid.length; index++) {

					if (!Blacklist.useAbleData(parseCurrentUid[index])) {
						continue;
					}
					if (isInvalidParsedUid(parseCurrentUid[index])) {
						continue;
					}
					parseCurrentUid[index] = formatUid(parseCurrentUid[index]);
					currentUidList.add(parseCurrentUid[index]);
				}
			}

			if (!matchingWithUid.contains(ServerConstants.UUID_SEPERATOR)) {
				for (int a = 0; a < currentUidList.size(); a++) {
					if (matchingWithUid.equals(currentUidList.get(a))) {
						return true;
					}
				}
			} else {
				String[] parseMatchingUid = matchingWithUid.split(ServerConstants.UUID_SEPERATOR);
				for (int index = 0; index < parseMatchingUid.length; index++) {
					if (!Blacklist.useAbleData(parseMatchingUid[index])) {
						continue;
					}
					if (isInvalidParsedUid(parseMatchingUid[index])) {
						continue;
					}
					parseMatchingUid[index] = formatUid(parseMatchingUid[index]);
					for (int a = 0; a < currentUidList.size(); a++) {
						if (parseMatchingUid[index].equals(currentUidList.get(a))) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	public static boolean isUidSaveableIntoCharacterFile(String uid) {
		if (uid.equals("invalid")) {
			return false;
		}
		return true;
	}
	public static String formatUid(String uid) {
		uid = uid.toLowerCase();
		uid = uid.trim();
		if (uid.length() > 60) {
			uid = uid.substring(0, 60);
		}
		if (containsNewLineExploit(uid)) {
			return "invalid";
		}
		if (uid.isEmpty()) {
			return "invalid";
		}
		return uid;
	}

	public static boolean macMatches(String currentMac, String matchingWithMac) {
		currentMac = currentMac.toLowerCase();
		matchingWithMac = matchingWithMac.toLowerCase();
		if (Blacklist.useAbleData(currentMac) && Blacklist.useAbleData(matchingWithMac)) {
			if (currentMac.equals(matchingWithMac)) {
				return true;
			}
		}
		return false;
	}

	public static String getPluralS(int amount) {
		if (amount > 1) {
			return "s";
		}
		return "";
	}

	public static String getPluralS(long amount) {
		if (amount > 1) {
			return "s";
		}
		return "";
	}

	public static String getPluralWordWithKey(String key, long amount) {
		if (amount > 1) {
			return key + "s";
		}
		return key;
	}

	public static long ticksToSeconds(long ticks) {
		return (long) ((double) ticks * .6D);
	}

	public static long hoursToTicks(long hours) {
		return hours * 6000;
	}

	public static long minutesToTicks(long minutes) {
		return minutes * 100;
	}

	public static long getTimeMilliseconds() {
		return System.currentTimeMillis();
	}

	/**
	 * Return the amount of milliseconds. Depending on the hours given.
	 */
	public static long getHoursToMilliseconds(int hours) {
		return (long) hours * 3600000;
	}

	/**
	 * Return the amount of milliseconds. Depending on the minutes given.
	 */
	public static long getMinutesToMilliseconds(int minutes) {
		return (long) minutes * 60000;
	}

	/**
	 * Turn seconds into milliseconds. So 60 becomes 60,000
	 */
	public static long getSecondsToMilliseconds(double seconds) {
		return (long) (seconds * 1000);
	}

	public static long getMillisecondsToSeconds(long milliseconds) {
		return milliseconds / 1_000;
	}

	public static long getMillisecondsToMinutes(long milliseconds) {
		return milliseconds / 60_000;
	}

	/**
	 * Get the player instance by search for the specific name.
	 */
	public static Player getPlayerByName(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player loop = PlayerHandler.players[i];
			if (loop == null) {
				continue;
			}
			if (loop.getPlayerName().equalsIgnoreCase(name)) {
				return loop;
			}
		}
		return null;
	}

	/**
	 * Creates a new file if one does not already exist for the given path. If one does, this
	 * does nothing.
	 *
	 * @param path
	 * 			  the path to the given file.
	 * @param lines
	 * 			  the lines to add to the file if absent.
	 * @throws IOException
	 * 			  thrown if we cannot write for some reason.
	 */
	public static void createFileIfAbsent(Path path, String... lines) throws IOException {
		if (Files.notExists(path)) {
			Files.write(path, Arrays.asList(lines), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}
	}

	/**
	 * Get the player instance by search for the specific name.
	 */
	public static Player getPlayerByIndex(int index) {
		return PlayerHandler.players[index];
	}

	/**
	 * @return a or an
	 */
	public static String getAorAnWithOutKey(String targetWord) {
		String anA = "a";
		targetWord = targetWord.toLowerCase();
		if (targetWord.startsWith("a") || targetWord.startsWith("e") || targetWord.startsWith("i") || targetWord.startsWith("o") || targetWord.startsWith("u")) {
			anA = "an";
		}
		return anA;
	}

	/**
	 * @return a targetWord/an targetWord
	 */
	public static String getAorAn(String targetWord) {
		String anA = "a";
		String before = targetWord;
		targetWord = targetWord.toLowerCase();
		if (targetWord.startsWith("a") || targetWord.startsWith("e") || targetWord.startsWith("i") || targetWord.startsWith("o") || targetWord.startsWith("u")) {
			anA = "an";
		}
		return anA + " " + before;
	}

	public static String nameForLong(long l) {
		try {
			if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L)
				return "invalid_name";
			if (l % 37L == 0L)
				return "invalid_name";
			int i = 0;
			char ac[] = new char[12];
			while (l != 0L) {
				long l1 = l;
				l /= 37L;
				ac[11 - i++] = validChars[(int) (l1 - l * 37L)];
			}
			return new String(ac, 12 - i, i);
		} catch (RuntimeException e) {
			return "";
		}
	}

	/**
	 * 05-06-2018, 05:34: PM
	 */
	public static String getDateAndTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm: a");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * Return a date and or time with a specific format.
	 * Such as: dd/MM/yyyy, hh:mm:ss a
	 */
	public static String getDateOrTimeWithFormat(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * 05-06-2018, 05:34:59 PM
	 */
	public static String getDateAndTimeAndSeconds() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * Example return: [08:55 PM]
	 */
	public static String getDateAndTimeLog() {
		return "[" + getDateAndTime() + "] ";
	}

	/**
	 * 01-12-2006
	 */
	public static String getDateOnlyDashes() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * 16/07/2009
	 */
	public static String getDateOnlySlashes() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * Get the name of week for today.
	 */
	public static String getDayOfTheWeek() {
		Date now = new Date();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
		return simpleDateformat.format(now);
	}

	/**
	 * Get the previous day name depending on todays day name.
	 */
	public static String getPreviousDayName() {
		String todayName = getDayOfTheWeek();
		for (int index = 0; index < DAYS_OF_WEEK.length; index++) {
			if (todayName.equals("Sunday")) {
				return "Saturday";
			}
			if (todayName.equals(DAYS_OF_WEEK[index])) {
				return DAYS_OF_WEEK[index - 1];
			}
		}
		return "Unfound day";
	}

	/**
	 * Days of the week in order.
	 */
	private final static String[] DAYS_OF_WEEK =
			{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	/**
	 * Format number into for example: 357,555
	 */
	public static String formatNumber(long number) {
		// Do not use return NumberFormat.getIntegerInstance().format(number);. It is 9 times slower.
		String string = Long.toString(number);
		if (number < 1000) {
			return string;
		}
		if (number >= 1000 && number < 10000) {
			return string.substring(0, 1) + "," + string.substring(1);
		}
		if (number < 100000) {
			return string.substring(0, 2) + "," + string.substring(2);
		}
		if (number < 1000000) {
			return string.substring(0, 3) + "," + string.substring(3);
		}
		if (number < 10000000) {
			return string.substring(0, 1) + "," + string.substring(1, 4) + "," + string.substring(4, 7);
		}
		if (number < 100000000) {
			return string.substring(0, 2) + "," + string.substring(2, 5) + "," + string.substring(5, 8);
		}
		if (number < 1000000000) {
			return string.substring(0, 3) + "," + string.substring(3, 6) + "," + string.substring(6, 9);
		}
		if (number <= Integer.MAX_VALUE) {
			return string.substring(0, 1) + "," + string.substring(1, 4) + "," + string.substring(4, 7) + "," + string.substring(7, 10);
		}
		return string;
	}

	public static String formatToK(int coins) {
		if (coins >= 10_000 && coins < 10_000_000) {
			return formatNumber(coins / 1000) + "K";
		}
		if (coins >= 10_000_000) {
			return formatNumber(coins / 1_000_000) + "M";
		}
		return formatNumber(coins);
	}
	/**
	 * % chance of this boolean turning true.
	 *
	 * @param chance The % amount
	 * @return chance is true
	 */
	public static boolean hasPercentageChance(int chance) {
		if (chance > 100) {
			chance = 100;
		}
		if (chance <= 0) {
			return false;
		}
		if (random(99) < chance) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the String contains any offensive content.
	 *
	 * @param chat The chat to check for offensive content.
	 * @return True, if the chat contained offensive content.
	 */
	public static boolean checkForOffensive(String chat) {
		for (int i = 0; i < ServerConstants.offensiveLanguage.length; i++) {
			if (chat.toLowerCase().contains(ServerConstants.offensiveLanguage[i])) {
				return true;
			}
		}
		return false;
	}

	public static int random(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	/**
	 * 1 out of the given integer to turn true.
	 * So if i put 150, then it means, there is a 1 in 150 chance to turn true.
	 */
	public static boolean hasOneOutOf(double chance) {
		if (chance < 1) {
			chance = 0;
		}
		chance *= 100.0;
		return random(1, (int) chance) <= 100;
	}

	/**
	 * Capitalize the first letter and other letters that before it is a space.
	 *
	 * @param s
	 * @return
	 */
	public static String capitalize(String s) {
		s = s.toLowerCase();
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
				}
			}
		}
		return s;
	}

	/**
	 * Capitalize first letter.
	 */
	public static String optimize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String longToPlayerName(long l) {
		try {
			int i = 0;
			char ac[] = new char[99];
			while (l != 0L) {
				long l1 = l;
				l /= 37L;
				ac[11 - i++] = playerNameXlateTable[(int) (l1 - l * 37L)];
			}
			return new String(ac, 12 - i, i).replaceAll("_", " ");
		} catch (Exception e) {
			return "";
		}
	}

	public static final char playerNameXlateTable[] =
			{'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
					'6', '7', '8', '9', '[', ']', '/', '-', ' '
			};

	/**
	 * Such as 500k or 50.6m 
	 */
	public static String formatRunescapeStyle(int num) {
		return formatRunescapeStyle((long) num);
	}

	/**
	 * Such as 500k or 50.6m 
	 */
	public static String formatRunescapeStyle(long num) {
		boolean negative = false;
		if (num < 0) {
			num = -num;
			negative = true;
		}
		int length = String.valueOf(num).length();
		String number = Long.toString(num);
		String numberString = number;
		String end = "";
		if (length == 4) {
			numberString = number.substring(0, 1) + "k";
			//6400
			double doubleVersion = 0.0;
			doubleVersion = num / 1000.0;
			if (doubleVersion != getDoubleRoundedUp(doubleVersion)) {
				if (num - (1000 * getDoubleRoundedDown(doubleVersion)) > 100) {
					numberString = number.substring(0, 1) + "." + number.substring(1, 2) + "k";
				}
			}
		} else if (length == 5) {
			numberString = number.substring(0, 2) + "k";
		} else if (length == 6) {
			numberString = number.substring(0, 3) + "k";
		} else if (length == 7) {
			String sub = number.substring(1, 2);
			if (sub.equals("0")) {
				numberString = number.substring(0, 1) + "m";
			} else {
				numberString = number.substring(0, 1) + "." + number.substring(1, 2) + "m";
			}
		} else if (length == 8) {
			end = "." + number.substring(2, 3);
			if (end.equals(".0")) {
				end = "";
			}
			numberString = number.substring(0, 2) + end + "m";
		} else if (length == 9) {
			end = "." + number.substring(3, 4);
			if (end.equals(".0")) {
				end = "";
			}
			numberString = number.substring(0, 3) + end + "m";
		} else if (length == 10) {
			numberString = number.substring(0, 4) + "m";
		} else if (length == 11) {
			numberString = number.substring(0, 2) + "." + number.substring(2, 5) + "b";
		} else if (length == 12) {
			numberString = number.substring(0, 3) + "." + number.substring(3, 6) + "b";
		} else if (length == 13) {
			numberString = number.substring(0, 4) + "." + number.substring(4, 7) + "b";
		}
		if (negative) {
			numberString = "-" + numberString;
		}
		return numberString;
	}

	public static String ucFirst(String str) {
		str = str.toLowerCase();
		if (str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			return str.toUpperCase();
		}
		return str;
	}

	public static int hexToInt(byte data[], int offset, int len) {
		int temp = 0;
		int i = 1000;
		for (int cntr = 0; cntr < len; cntr++) {
			int num = (data[offset + cntr] & 0xFF) * i;
			temp += num;
			if (i > 1) {
				i = i / 1000;
			}
		}
		return temp;
	}

	/**
	 * If range is 5, then it will give a random value of 1 to 5.
	 */
	public static int random2(int range) {
		return (int) ((java.lang.Math.random() * range) + 1);
	}

	/**
	 * If range is 5, then it will give a random value of 0 to 5.
	 */
	public static int random(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	public static long playerNameToInt64(String s) {
		long l = 0L;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}


	private static char decodeBuf[] = new char[4096];

	public static String textUnpack(byte packedData[], int size) {
		try {
			int idx = 0;
			for (int i = 0; i < size; i++) {
				int val = packedData[i];
				decodeBuf[idx++] = xlateTable[val];
			}

			return new String(decodeBuf, 0, idx);
		} catch (Exception e) {
			return "";
		}
	}

	public static String optimizeText(String text) {
		char buf[] = text.toCharArray();
		boolean endMarker = true;
		for (int i = 0; i < buf.length; i++) {
			char c = buf[i];
			if (endMarker && c >= 'a' && c <= 'z') {
				buf[i] -= 0x20;
				endMarker = false;
			}
			if (c == '.' || c == '!' || c == '?')
				endMarker = true;
		}
		return new String(buf, 0, buf.length);
	}

	public static void textPack(byte packedData[], java.lang.String text) {
		if (text.length() > 80)
			text = text.substring(0, 80);
		text = text.toLowerCase();

		int carryOverNibble = -1;
		int ofs = 0;
		for (int idx = 0; idx < text.length(); idx++) {
			char c = text.charAt(idx);
			int tableIdx = 0;
			for (int i = 0; i < xlateTable.length; i++) {
				if (c == xlateTable[i]) {
					tableIdx = i;
					break;
				}
			}
			if (tableIdx > 12)
				tableIdx += 195;
			if (carryOverNibble == -1) {
				if (tableIdx < 13)
					carryOverNibble = tableIdx;
				else
					packedData[ofs++] = (byte) (tableIdx);
			} else if (tableIdx < 13) {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + tableIdx);
				carryOverNibble = -1;
			} else {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + (tableIdx >> 4));
				carryOverNibble = tableIdx & 0xf;
			}
		}

		if (carryOverNibble != -1)
			packedData[ofs++] = (byte) (carryOverNibble << 4);
	}

	public static char xlateTable[] =
			{
					' ',
					'e',
					't',
					'a',
					'o',
					'i',
					'h',
					'n',
					's',
					'r',
					'd',
					'l',
					'u',
					'm',
					'w',
					'c',
					'y',
					'f',
					'g',
					'p',
					'b',
					'v',
					'k',
					'x',
					'j',
					'q',
					'z',
					'0',
					'1',
					'2',
					'3',
					'4',
					'5',
					'6',
					'7',
					'8',
					'9',
					' ',
					'!',
					'?',
					'.',
					',',
					':',
					';',
					'(',
					')',
					'-',
					'&',
					'*',
					'\\',
					'\'',
					'@',
					'#',
					'+',
					'=',
					'\243',
					'$',
					'%',
					'"',
					'[',
					']',
					'>',
					'<',
					'^',
					'/'
			};


	public static int direction(int startX, int startY, int destinationX, int destinationY) {
		final int directionX = destinationX - startX, directionY = destinationY - startY;
		if (directionX < 0) {
			if (directionY < 0) {
				if (directionX < directionY)
					return 11;
				return directionX <= directionY ? 10 : 9;
			}
			if (directionY > 0) {
				if (-directionX < directionY)
					return 15;
				return -directionX <= directionY ? 14 : 13;
			} else
				return 12;
		}
		if (directionX > 0) {
			if (directionY < 0) {
				if (directionX < -directionY)
					return 7;
				return directionX <= -directionY ? 6 : 5;
			}
			if (directionY > 0) {
				if (directionX < directionY)
					return 1;
				return directionX <= directionY ? 2 : 3;
			} else
				return 4;
		}
		if (directionY < 0) {
			return 8;
		}

		return directionY <= 0 ? -1 : 0;
	}

	public static byte directionDeltaX[] = new byte[]
			                                       {0, 1, 1, 1, 0, -1, -1, -1};

	public static byte directionDeltaY[] = new byte[]
			                                       {1, 1, 0, -1, -1, -1, 0, 1};

	public static byte xlateDirectionToClient[] = new byte[]
			                                              {1, 2, 4, 7, 6, 5, 3, 0};

	public static int getIndex(int id, int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (id == array[i]) {
				return i;
			}
		}
		return -1;
	}

	public static ArrayList<String> consolePrint = new ArrayList<String>();

	public static ArrayList<String> consolePrintPermSave = new ArrayList<String>();

	public static void print(Throwable throwable) {
		throwable.printStackTrace();

		OutputBatchUpdateEvent.getInstance().submit(new OutputSQLTableEntry(System.currentTimeMillis(), throwable));
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		consolePrint.add(throwable.getMessage());
		for (StackTraceElement element : throwable.getStackTrace()) {
			consolePrint.add(String.format("[%s] %s", getDateAndTime(), element.toString()));
		}
	}

	public static void print(String string) {
		System.out.println(string);
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		consolePrint.add("[" + Misc.getDateAndTime() + "] " + string);
	}

	public static void printDebugOnly(String string) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}
		System.out.println(string);
	}

	public static void print(int string) {
		System.out.println(string);
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		consolePrint.add("[" + Misc.getDateAndTime() + "] " + string);
	}

	public static void print(double string) {
		System.out.println(string);
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		consolePrint.add("[" + Misc.getDateAndTime() + "] " + string);
	}

	public static void printDontSave(String string) {
		System.out.println(string);
	}

	public static String getTime(long timeInSeconds) {
		int seconds = (int) timeInSeconds;
		if (seconds < 60) {
			return seconds + " secs";
		} else if (seconds < 3600) {
			String secondsText = (seconds / 60) > 1 ? "mins" : "min";
			return (seconds / 60) + " " + secondsText;
		} else {
			int hour = (seconds / 3600);
			String hourText = hour > 1 ? "hours" : "hour";
			return hour + " " + hourText;
		}
	}

	public static boolean containsNewLineExploit(String string) {
		// The \r and /n creates a new line in the string, so the player can do it then authority = 2 then [EOF] to force admin.
		// < and > is so they do not use <col=55> or <img=11>
		if (string.contains("\r") || string.contains("\n") || string.contains("<") && string.contains(">")) {
			return true;
		}
		return false;
	}

	/***
	 * Convert current time in milliseconds to a proper readable date such as 06/08/2009, 08:23: PM
	 */
	public static String millisecondsToDateAndTime(long milliseconds) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm: a");
		String dateFormatted = formatter.format(milliseconds);
		return dateFormatted;
	}

	/***
	 * Convert current time in milliseconds to a proper readable date such as 05-06-2018
	 */
	public static String millisecondsToDateOnly(long milliseconds) {
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String dateFormatted = formatter.format(milliseconds);
		return dateFormatted;
	}

	/**
	 * @return The player's ip address from their character file.
	 */
	public static String getOfflineIpAddress(String name) {
		return Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
	}

	/**
	 * @return The player's uid from their character file.
	 */
	public static String getOfflineUid(String name) {
		return Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
	}

	/**
	 * @return The player's mac from their character file.
	 */
	public static String getOfflineMac(String name) {
		return Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressMac", 3);
	}

	/**
	 * @return True if invalid username.
	 */
	public static boolean invalidUsername(String name) {
		if (!name.matches("[A-Za-z0-9 ]+")) {
			return true;
		}
		return false;
	}

	/**
	 * Get the KDR.
	 *
	 * @param kills The player's kills.
	 * @param deaths The player's death.s
	 * @return The KDR.
	 */
	public static double getKDR(int kills, int deaths) {
		double kdr = (double) kills / (double) deaths;
		if (kdr != kdr) {
			kdr = 0;
		}
		if (deaths == 0) {
			kdr = kills;
		}
		kdr = roundDoubleToNearestTwoDecimalPlaces(kdr);
		return kdr;
	}

	public static boolean validMacAddress(String macAddress) {

		// Client will give this if null.
		if (macAddress.toLowerCase().equals("invalid")) {
			return true;
		}
		if (countOccurrences(macAddress, "-".charAt(0)) != 5) {
			return false;
		}
		if (macAddress.length() != 17) {
			return false;
		}

		//00-ff-7b-0e-75-bc
		for (int index = 0; index < macAddress.length(); index++) {
			String letter = Character.toString(macAddress.charAt(index));
			if (index == 2 || index == 5 || index == 8 || index == 11 || index == 14) {
				if (!letter.equals("-")) {
					return false;
				}
			} else {
				if (!letter.matches("[a-z0-9]+")) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 15-02-1996, 07:59: PM
	 * dd/MM/yyyy, hh:mm: a
	 */
	public static long dateToMilliseconds(String string, String dateFormat) {
		String myDate = string;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sdf.parse(myDate);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public static void printStackTrace() {
		for (int index = 0; index < Thread.currentThread().getStackTrace().length; index++) {
			print(Thread.currentThread().getStackTrace()[index].getClassName() + "#" + Thread.currentThread().getStackTrace()[index].getMethodName());
		}
	}

	public static void printStackTraceArrayList() {
		ArrayList<String> string = new ArrayList<String>();
		for (int index = 0; index < Thread.currentThread().getStackTrace().length; index++) {
			string.add(Thread.currentThread().getStackTrace()[index].getClassName() + "#" + Thread.currentThread().getStackTrace()[index].getMethodName());
		}
	}



	public static ArrayList<Integer> removeArraylistIntegerMatch(ArrayList<Integer> list, int number, boolean removeAllMatches) {
		for (int index = 0; index < list.size(); index++) {
			if (list.get(index) == number) {
				list.remove(index);
				index--;
				if (!removeAllMatches) {
					return list;
				}
			}
		}
		return list;
	}
}
