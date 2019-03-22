package utility;

import core.ServerConstants;

import java.util.ArrayList;

/**
 * Load the text width of the text used in sendMessage method. The data is dumped from the client.
 *
 * @author MGT Madness, created on 29-08-2017.
 */
public class LoadTextWidth {

	private final static int CROWN_AVERAGE_WIDTH = 14;

	public static int[] characterWidths = new int[256];

	/**
	 * Load the text widths.
	 */
	public static void LoadTextWidthFile() {
		ArrayList<String> data = FileUtility.readFile(ServerConstants.getOsrsGlobalDataLocation() + "misc/client_send_message_text_width.txt");
		for (int index = 0; index < data.size(); index++) {
			String[] parse = data.get(index).split(": ");
			characterWidths[index] = Integer.parseInt(parse[1]);
		}
	}


	private final static String startEffect = "lt";

	private final static String endEffect = "gt";

	private final static String startImage = "img=";

	private final static String aRSString_4135 = "nbsp";

	private final static String aRSString_4169 = "reg";

	private final static String aRSString_4165 = "times";

	private final static String aRSString_4162 = "shy";

	private final static String aRSString_4163 = "copy";

	private final static String aRSString_4147 = "euro";

	public static String shortenText(String string) {
		int startIndex = -1;
		int finalWidth = 0;
		for (int currentCharacter = 0; currentCharacter < string.length(); currentCharacter++) {
			int character = string.charAt(currentCharacter);
			if (character > 255) {
				character = 32;
			}
			if (character == 60) {
				startIndex = currentCharacter;
			} else {
				if (character == 62 && startIndex != -1) {
					String effectString = string.substring(startIndex + 1, currentCharacter);
					startIndex = -1;
					if (effectString.equals(startEffect)) {
						character = 60;
					} else if (effectString.equals(endEffect)) {
						character = 62;
					} else if (effectString.equals(aRSString_4135)) {
						character = 160;
					} else if (effectString.equals(aRSString_4162)) {
						character = 173;
					} else if (effectString.equals(aRSString_4165)) {
						character = 215;
					} else if (effectString.equals(aRSString_4147)) {
						character = 128;
					} else if (effectString.equals(aRSString_4163)) {
						character = 169;
					} else if (effectString.equals(aRSString_4169)) {
						character = 174;
					} else {
						if (effectString.startsWith(startImage)) {
							try {
								finalWidth += CROWN_AVERAGE_WIDTH; // on client it is += chatImages[iconId].maxWidth;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						continue;
					}
				}
				if (startIndex == -1) {
					if (finalWidth + characterWidths[character] > 470) {
						return string.substring(0, currentCharacter + 1);
					}
					finalWidth += characterWidths[character];
				}
			}
		}
		return string;
	}

}
