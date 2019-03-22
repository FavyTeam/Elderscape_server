package game.content.miscellaneous;


import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * @author DerekH, created on 20-12-2013.
 * @author MGT Madness.
 */
public class CompletionistCape {

	public static enum ColoursData {
		RED_1(86059, 685),
		RED_2(86062, 702),
		RED_3(86065, 405),
		RED_4(86068, 548),
		BLUE(86071, 42945),
		LIGHT_BLUE(86074, 39871),
		RED(86077, 949),
		BLACK(86080, 129),
		WHITE(86083, 127),
		ORANGE(86086, 6077),
		YELLOW(86089, 11199),
		PURPLE(86092, -4186),
		PINK(86095, -8257),
		DARK_GREEN(86098, 18210),
		GREEN(86101, 21439),
		LIGHT_GREEN(86104, 347770);

		private int buttonId;

		private int runescapeColourValue;

		private ColoursData(int buttonId, int runescapeColourValue) {
			this.buttonId = buttonId;
			this.runescapeColourValue = runescapeColourValue;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getRunescapeColourValue() {
			return runescapeColourValue;
		}

	}

	/**
	 * Send the current Completionist cape player colours to the client.
	 */
	private static void sendCurrentCapeColours(Player player) {
		for (ColoursData data : ColoursData.values()) {
			if (player.compColor1 == data.getRunescapeColourValue()) {
				setMainColours(player, "TOP DETAIL", data.name());
			}
			if (player.compColor2 == data.getRunescapeColourValue()) {
				setMainColours(player, "TOP", data.name());
			}
			if (player.compColor3 == data.getRunescapeColourValue()) {
				setMainColours(player, "BOTTOM DETAIL", data.name());
			}
			if (player.compColor4 == data.getRunescapeColourValue()) {
				setMainColours(player, "BOTTOM", data.name());
			}
		}
	}

	/**
	 * Get the Runescape colour value depending on the given colour String.
	 */
	private static int getRunescapeColourValue(String colour) {
		for (ColoursData data : ColoursData.values()) {
			if (data.name().equals(colour)) {
				return data.getRunescapeColourValue();
			}
		}
		return 0;
	}

	/**
	 * Display the completionist cape interface.
	 */
	public static void displayInterface(Player player) {
		setPartEdited(player, "TOP");
		sendCurrentCapeColours(player);
		player.getPA().displayInterface(22060);
	}

	/**
	 * @return True, if the given buttonId is a Completionist cape interface button.
	 */
	public static boolean isCompletionistCapeButton(Player player, int buttonId) {
		for (ColoursData data : ColoursData.values()) {
			if (data.getButtonId() == buttonId) {
				if (!ItemAssistant.hasItemEquipped(player, 14011)) {
					return true;
				}
				setColourClicked(player, data.name());
				return true;
			}
		}
		switch (buttonId) {
			case 86047:
				setPartEdited(player, "TOP");
				return true;
			case 86050:
				setPartEdited(player, "TOP DETAIL");
				return true;
			case 86053:
				setPartEdited(player, "BOTTOM");
				return true;
			case 86056:
				setPartEdited(player, "BOTTOM DETAIL");
				return true;
		}
		return false;
	}

	/**
	 * Inform the client for the colours to display on the main parts.
	 */
	private static void setMainColours(Player player, String part, String colour) {
		player.playerAssistant.sendMessage(":capemainpart" + part + colour);
	}

	/**
	 * Inform the client on which main part is being edited.
	 */
	private static void setPartEdited(Player player, String partEdited) {
		if (!ItemAssistant.hasItemEquipped(player, 14011)) {
			return;
		}
		player.completionistCapePartEdited = partEdited;
		player.playerAssistant.sendMessage(":capepart" + partEdited);
		sendCurrentCapeClickedColour(player, partEdited);
	}

	/**
	 * Depending on the player's current cape colour and the part edited, send the appropriate colour button to be clicked.
	 *
	 * @param part The completionist cape part being edited.
	 */
	private static void sendCurrentCapeClickedColour(Player player, String part) {
		int colour = 0;
		switch (part) {
			case "TOP DETAIL":
				colour = player.compColor1;
				break;
			case "TOP":
				colour = player.compColor2;
				break;
			case "BOTTOM DETAIL":
				colour = player.compColor3;
				break;
			case "BOTTOM":
				colour = player.compColor4;
				break;
		}
		for (ColoursData data : ColoursData.values()) {
			if (data.getRunescapeColourValue() == colour) {
				player.playerAssistant.sendMessage(":capecolour" + data.name());
			}
		}
	}

	/**
	 * Set the colour clicked.
	 */
	private static void setColourClicked(Player player, String colour) {
		player.playerAssistant.sendMessage(":capecolour" + colour);
		if (colour.isEmpty()) {
			return;
		}
		switch (player.completionistCapePartEdited) {
			case "TOP DETAIL":
				player.compColor1 = getRunescapeColourValue(colour);
				setMainColours(player, "TOP DETAIL", colour);
				break;
			case "TOP":
				player.compColor2 = getRunescapeColourValue(colour);
				setMainColours(player, "TOP", colour);
				break;
			case "BOTTOM DETAIL":
				player.compColor3 = getRunescapeColourValue(colour);
				setMainColours(player, "BOTTOM DETAIL", colour);
				break;
			case "BOTTOM":
				player.compColor4 = getRunescapeColourValue(colour);
				setMainColours(player, "BOTTOM", colour);
				break;
		}
		updatePlayers(player);
	}

	/**
	 * Updated the Completionist cape for all player.
	 */
	private static void updatePlayers(Player player) {
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);

		// Required to update for players currently in range of my character.
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].playerAssistant.withinDistance(player)) {
					PlayerHandler.players[i].playerAssistant.sendMessage(":compu:");
				}
			}
		}
	}

	public static void rgbToHSL(int color) {
		double r = (color >> 16 & 0xff) / 256D;
		double g = (color >> 8 & 0xff) / 256D;
		double b = (color & 0xff) / 256D;
		double red_val1 = r;
		if (g < red_val1) {
			red_val1 = g;
		}
		if (b < red_val1) {
			red_val1 = b;
		}
		double red_val2 = r;
		if (g > red_val2) {
			red_val2 = g;
		}
		if (b > red_val2) {
			red_val2 = b;
		}
		double hueCalc = 0.0D;
		double satCalc = 0.0D;
		double lightCalc = (red_val1 + red_val2) / 2D;
		if (red_val1 != red_val2) {
			if (lightCalc < 0.5D) {
				satCalc = (red_val2 - red_val1) / (red_val2 + red_val1);
			}
			if (lightCalc >= 0.5D) {
				satCalc = (red_val2 - red_val1) / (2D - red_val2 - red_val1);
			}
			if (r == red_val2) {
				hueCalc = (g - b) / (red_val2 - red_val1);
			} else if (g == red_val2) {
				hueCalc = 2D + (b - r) / (red_val2 - red_val1);
			} else if (b == red_val2) {
				hueCalc = 4D + (r - g) / (red_val2 - red_val1);
			}
		}
		hueCalc /= 6D;
		int hue = (int) (hueCalc * 256D);
		int saturation = (int) (satCalc * 256D);
		int lightness = (int) (lightCalc * 256D);
		if (saturation < 0) {
			saturation = 0;
		} else if (saturation > 255) {
			saturation = 255;
		}
		if (lightness < 0) {
			lightness = 0;
		} else if (lightness > 255) {
			lightness = 255;
		}
		int divisor = 1;
		if (lightCalc > 0.5D) {
			divisor = (int) ((1.0D - lightCalc) * satCalc * 512D);
		} else {
			divisor = (int) (lightCalc * satCalc * 512D);
		}
		if (divisor < 1) {
			divisor = 1;
		}
		hue = (int) (hueCalc * divisor);
		int hueOffset = hue;
		int saturationOffset = saturation;
		int lightnessOffset = lightness;
		getHSLValue(hueOffset, saturationOffset, lightnessOffset);
	}


	private static void getHSLValue(int hue, int saturation, int lightness) {
		if (lightness > 179) {
			saturation /= 2;
		}
		if (lightness > 192) {
			saturation /= 2;
		}
		if (lightness > 217) {
			saturation /= 2;
		}
		if (lightness > 243) {
			saturation /= 2;
		}
		Misc.print("HSL: " + ((hue / 4 << 10) + (saturation / 32 << 7) + lightness / 2));
	}
}
