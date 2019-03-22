package game.content.miscellaneous;

import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Gnome glider feature.
 *
 * @author Liberty/Roboyoto
 */
public class GnomeGlider {

	private static enum GnomeGliderData {
		SINDARPOS("Sindarpos", 3058, 2848, 3497, 0, 1),
		TA_QUIR_PRIW("Ta Quir Priw", 3057, 2465, 3501, 3, 2),
		LEMANTO_ANDRA("Lemanto Andra", 3059, 3321, 3427, 0, 3),
		KAR_HEWO("Kar-Hewo", 3060, 3278, 3212, 0, 4),
		GANDIUS("Gandius", 3056, 2972, 2969, 0, 8),
		LEMANTOLLY_UNDRI("Lemantolly Undri", 48054, 2544, 2970, 0, 10);

		private String destinationName;

		private int button;

		private int destinationX;

		private int destinationY;

		private int destinationHeight;

		private int interfaceFrameActionType;

		public String getDestinationName() {
			return destinationName;
		}

		public int getButton() {
			return button;
		}

		public int getDestinationX() {
			return destinationX;
		}

		public int getDestinationY() {
			return destinationY;
		}

		public int getDestinationHeight() {
			return destinationHeight;
		}

		public int getInterfaceFrameActionType() {
			return interfaceFrameActionType;
		}

		private GnomeGliderData(String destinationName, int button, int destinationX, int desinationY, int destinationHeight, int interfaceFrameActionType) {
			this.destinationName = destinationName;
			this.button = button;
			this.destinationX = destinationX;
			this.destinationY = desinationY;
			this.destinationHeight = destinationHeight;
			this.interfaceFrameActionType = interfaceFrameActionType;
		}
	}


	;

	/**
	 * True if the button matches a Gnome glider button.
	 *
	 * @param player The associated player.
	 * @param button The button used by the player.
	 * @return True, if the button matches the list of Gnome glider buttons.
	 */
	public static boolean isGnomeGliderButton(Player player, int button) {
		for (GnomeGliderData data : GnomeGliderData.values()) {
			if (data.getButton() == button) {
				handleGnomeGliderTravel(player, data);
				return true;
			}
		}
		return false;
	}

	/**
	 * Fly the player to the destination.
	 *
	 * @param player The associated player.
	 * @param data The Gnome Glider data.
	 */
	public static void handleGnomeGliderTravel(final Player player, final GnomeGliderData data) {
		if (player.gnomeGliderEvent) {
			return;
		}
		if (player.getActionIdUsed() != 2650) {
			return;
		}
		player.getPA().displayInterface(802);
		player.getPA().sendFrame36(153, data.getInterfaceFrameActionType(), false);
		player.playerAssistant.sendMessage("The gnome takes you to " + data.getDestinationName() + ".");
		player.gnomeGliderEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.getPA().movePlayer(data.getDestinationX(), data.getDestinationY(), data.getDestinationHeight());
				player.getPA().closeInterfaces(true);
				player.gnomeGliderEvent = false;
				player.resetActionIdUsed();
			}
		}, 4);
	}
}
