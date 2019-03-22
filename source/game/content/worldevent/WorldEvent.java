package game.content.worldevent;

import core.GameType;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

import java.util.ArrayList;

/**
 * Automatic world event system.
 *
 * @author MGT Madness, created on 28-02-2018.
 */
public class WorldEvent {

	public static ArrayList<String> debug = new ArrayList<String>();

	public final static double SKILLING_EVENT_EXPERIENCE_MULTIPLIER = 3.0;

	/**
	 * Event length in minutes.
	 */
	private static int EVENT_LENGTH = 20;

	/**
	 * Time the event has ended or is scheduled to end.
	 */
	public static long timeEventEnd;

	/**
	 * Current active event.
	 */
	private static String currentEvent = "";

	/**
	 * Store the upcoming event.
	 */
	public static String nextEvent = "";

	public static boolean eventStartedAnnounced;

	/**
	 * World event game tick.
	 */
	public static void worldEventTick() {
		Tournament.tournamenTick();
	}

	public static void start20MinutesLastingEvent(String eventType) {
		timeEventEnd = System.currentTimeMillis() + (EVENT_LENGTH * 60000);
		getRandomEvent(eventType);
		debug.add(Misc.getDateAndTime() + ": Here9: " + System.currentTimeMillis() + ", " + timeEventEnd + ", " + getCurrentEvent() + ", " + nextEvent);
		eventStartedAnnounced = true;
		Announcement.announce("World event has started! It will last for " + EVENT_LENGTH + " minutes.", ServerConstants.DARK_BLUE);
		Announcement.announce(nextEvent, ServerConstants.DARK_BLUE);
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				eventEnded();
			}
		}, 100 * EVENT_LENGTH);
	}

	public static void logInUpdate(Player player) {
		if (!eventStartedAnnounced) {
			return;
		}
		long minutesLeft = ((timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
		player.getPA().sendMessage(ServerConstants.BLUE_COL + "Event active: " + nextEvent + ", " + minutesLeft + " minutes left.");
	}

	/**
	 * Set a random event.
	 */
	public static void getRandomEvent(String eventType) {
		if (eventType.equals("WILDERNESS EVENT")) {
			int random = Misc.random(1, 3);
			if (random <= 2) {
				String[] pkTypes =
						{"berserker", "ranged tank", "f2p", "hybrid at ::edgepvp"};
				random = Misc.random(0, pkTypes.length - 1);
				String finalBuild = pkTypes[random];
				nextEvent = "Pk as a " + finalBuild + " for 4x artefacts!";
				setCurrentEvent(finalBuild.toUpperCase() + " PK");
			} else if (random == 3) {
				String[] wildTypes =
						{"Ice Strykewyrm", "Venenatis", "Callisto", "Lava dragons", "Chaos elemental", "Revenants", "Tormented demons"};
				int amountOfNpcsToKill = 3;
				int currentNpcsAdded = 0;
				int maximumCancels = wildTypes.length - amountOfNpcsToKill;
				int currentCancels = 0;
				String npcsToKill = "";
				for (int index = 0; index < wildTypes.length; index++) {
					if (currentNpcsAdded == amountOfNpcsToKill) {
						break;
					}
					if (currentCancels == maximumCancels) {
						if (npcsToKill.isEmpty()) {
							npcsToKill = wildTypes[index];
						} else {
							npcsToKill = npcsToKill + ", " + wildTypes[index];
						}
						currentNpcsAdded++;
						continue;
					}
					double chance = (double) wildTypes.length / (double) amountOfNpcsToKill;
					chance *= 10;
					if (Misc.random(1, (int) chance) > 10) {
						currentCancels++;
						continue;
					}
					if (npcsToKill.isEmpty()) {
						npcsToKill = wildTypes[index];
					} else {
						npcsToKill = npcsToKill + ", " + wildTypes[index];
					}
					currentNpcsAdded++;
				}
				nextEvent = "Kill " + npcsToKill + " for 3x rare drop chance!";
				setCurrentEvent(npcsToKill);
			}
		} else if (eventType.equals("SKILLING")) {
			int skillMax = 20;
			int skillLowest = 7;
			int skillsAmount = (skillMax + 1) - skillLowest;
			int skillIndexEvent = Misc.random(7, 20);

			// Get a balanced chance for prayer event.
			if (Misc.hasOneOutOf(skillsAmount + 1) && GameType.isOsrsEco()) {
				skillIndexEvent = ServerConstants.PRAYER;
			}
			nextEvent = ServerConstants.SKILL_NAME[skillIndexEvent] + " skill now gives " + Misc.roundDoubleToNearestTwoDecimalPlaces(SKILLING_EVENT_EXPERIENCE_MULTIPLIER) + "x bonus experience!";
			setCurrentEvent(ServerConstants.SKILL_NAME[skillIndexEvent] + " skill");
		}
	}

	/**
	 * Event has ended.
	 */
	public static void eventEnded() {
		Announcement.announce("World event has ended!", ServerConstants.DARK_BLUE);
		debug.add(Misc.getDateAndTime() + ": Here10: " + System.currentTimeMillis() + ", " + timeEventEnd + ", " + getCurrentEvent() + ", " + nextEvent + ", "
		          + eventStartedAnnounced);
		setCurrentEvent("");
		eventStartedAnnounced = false;
	}

	/**
	 * @return True if the eventName matches the current active event.
	 */
	public static boolean getActiveEvent(String eventName) {
		return getCurrentEvent().equals(eventName);
	}

	public static String getCurrentEvent() {
		return currentEvent;
	}

	public static void setCurrentEvent(String currentEvent) {
		WorldEvent.currentEvent = currentEvent;
	}

}
