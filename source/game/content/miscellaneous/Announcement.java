package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.highscores.HighscoresDaily;
import game.content.tradingpost.TradingPost;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.util.ArrayList;
import utility.Misc;

/**
 * Announcements.
 *
 * @author MGT Madness, created on 08-01-2015.
 */
public class Announcement {

	/**
	 * How often to announce game tips.
	 */
	private final static int GAME_ANNOUNCEMENT_TIP_INTERVAL = 10;

	/**
	 * How often to announce donate announcements.
	 */
	private final static int DONATE_ANNOUNCEMENTS_MINUTES_INTERVAL = 10;

	/**
	 * How often to execute a pending announcement. When a donate or game tip announcement has been executed, it will be added to a pending list where it is executed every x minutes.
	 */
	private final static int PENDING_ANNOUNCEMENT_INTERVAL = 5;

	private static String getBossesLootPerHour() {
		return GameType.isOsrsPvp() ? "25k" : "19m";
	}

	private static int value = Misc.random(40);

	public static void announce(String string, String colour) {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				PlayerHandler.players[i].playerAssistant.sendMessage(colour + string);
			}
		}
	}

	public static void announce(String string) {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				PlayerHandler.players[i].playerAssistant.sendMessage(string);
			}
		}
	}

	private static int donateIndex;

	private static int donateTypeValue = 0;

	public static ArrayList<String> announcementPendingList = new ArrayList<String>();

	public static void donateAnnouncementEvent() {
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				donateTypeValue++;
				if (donateTypeValue == 1) {
					TradingPost.tokensBeingPurchasedMessage();
				} else {
					donateAnnouncementAction();
				}
				if (donateTypeValue == 2) {
					donateTypeValue = 0;
				}
			}

			@Override
			public void stop() {
			}
		}, 100 * DONATE_ANNOUNCEMENTS_MINUTES_INTERVAL);

	}

	/**
	 * Announcements that are called every specified minutes.
	 */
	@SuppressWarnings("unchecked")
	public static void announcementGameTick() {
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				String[] message =
						{
								"Type in ::pkingloot to know the best way to make money through pking!",
								"Want to buy/sell an item? Go to ::market",
								"Being toxic and offensive will get you muted!",
								"Check the ::rules so you stay safe. Be friendly to everyone!",
								"Wilderness bosses is the fastest way to make money. Up to 25k/h.",
								"Never use a password from another website/rsps or you might get hacked!",
								"You can purchase skill capes from the Wise Old Man.",
								"Kill revenants for 20k+ an hour! One of the top money making methods.",
								"Check the guide button on your quest tab to learn alot.",
								"Chaos elemental gives 25k + an hour!",
								"Double your items right now at ::npcdoubler",
								"The higher your killstreak, the more bonus " + ServerConstants.getMainCurrencyName().toLowerCase() + " you will earn!",
								"Type in ::event to know what event is active and what will soon spawn!",
								"Ragging other players will get you ip banned.",
								"Pets are always kept upon death.",
								"Tell your friends and family about " + ServerConstants.getServerName() + "!",
								"Try your luck at Clue caskets from Party Pete to receive rares!",
								"Obtain Pvp tasks to receive up to " + Misc.formatRunescapeStyle(PvpTask.getPvpTaskRewardPerKill() * (PvpTask.getMaximumKillsPerTaskType() / 2) * 4)
								+ " " + ServerConstants.getMainCurrencyName().toLowerCase() + " after completion.",
								"Killing targets drop 4x artefacts.",
								"Become rich by doubling your items at ::npcdoubler",
								"Items harvested and created through skilling can be sold to shop for profit.",
								"" + ServerConstants.getServerName() + " is community driven, be sure to voice your opinion on the forums!",
								"Use the wilderness resource area earn 20% bonus experience!",
								"Talk to Hans at lumbridge to view account history.",
								"Magic hybrids and tribrids receive 2x artefacts from Pking!",
								"" + ServerConstants.getServerName() + " makes it rain.",
								"Mage arena can reward you with 35k+ an hour!",
								"Kill high tier skulled players for bonus 'shut down' blood money!",
								"Wild bosses reward up to " + getBossesLootPerHour() + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " per hour!",
								"::discord to communicate with fellow Dawntainers.",
								"Earn Youtube rank & " + Misc.formatRunescapeStyle(YoutubeRank.getRewardAmount()) + " " + ServerConstants.getMainCurrencyName()
								+ " if your video meets the requirements ::yt",
								"You may double or even triple your items at ::npcdoubler",
								"Max cape is only those worthy to wear it at maxed total.",
								"Open the shop of Sir Prysin to use achievement rewards that you've earned.",
								"Press F12 to save a screenshot.",
								"::risk to quick chat your risk with protect item forced on.",
								"Use blood keys on the chest at home for 50k bm.",
								"Report bugs on the forums in ::bugs",
								"You can purchase the Infernal cape at 1,000 kills!",
								"::blacklist players to avoid being attacked by raggers in Edgeville.",
								"The #1 daily highscores spot will receive " + Misc.formatRunescapeStyle(HighscoresDaily.getDailyHighscoresPrizeAmount()) + " " + ServerConstants
										                                                                                                                                  .getMainCurrencyName()
										                                                                                                                                  .toLowerCase()
								+ "!",
								"Ice strykewyrm is weaker to magic.",
								"All types of dragon bones and hides will be converted to " + ServerConstants.getMainCurrencyName().toLowerCase() + " on death.",
								"Join the '" + ServerConstants.getServerName() + "' clan chat to meet other players.",
								"Rune pouch is bought back from the void knight after death.",
								"Not in a clan yet? Join or create a clan and dominate the Wild at ::clans",
						};
				announcementPendingList.add(message[value]);
				value++;
				if (value > message.length - 1) {
					value = 0;
				}
			}

			@Override
			public void stop() {
			}
		}, 100 * GAME_ANNOUNCEMENT_TIP_INTERVAL);


	}

	private static long pendingAnnouncementSentTime;

	private static long staffAnnouncementSentTime;

	public static void announcementPendingEvent() {
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (System.currentTimeMillis() - pendingAnnouncementSentTime < (PENDING_ANNOUNCEMENT_INTERVAL * 60000)) {
					return;
				}
				if (announcementPendingList.isEmpty()) {
					return;
				}
				pendingAnnouncementSentTime = System.currentTimeMillis();
				int indexRandom = Misc.random(announcementPendingList.size() - 1);
				announce(announcementPendingList.get(indexRandom), "<img=20><col=0000ff>");
				announcementPendingList.remove(indexRandom);
			}

			@Override
			public void stop() {
			}
		}, 50);

		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (System.currentTimeMillis() - pendingAnnouncementSentTime < 150000) {
					return;
				}
				if (System.currentTimeMillis() - staffAnnouncementSentTime < Misc.getMinutesToMilliseconds(30)) {
					return;
				}
				boolean online = false;
				for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
					Player loop = PlayerHandler.players[index];
					if (loop == null) {
						continue;
					}
					if (!loop.isModeratorRank() && !loop.isSupportRank() && !loop.isAdministratorRank()) {
						continue;
					}
					if (loop.privateChat != ServerConstants.PRIVATE_ON) {
						continue;
					}
					online = true;
					break;
				}
				if (!online) {
					return;
				}
				staffAnnouncementSentTime = System.currentTimeMillis();
				announce("Need help or guidance? Pm a staff member on ::staff", "<img=20><col=0000ff>");
			}

			@Override
			public void stop() {
			}
		}, 50);
	}

	public static void donateAnnouncementAction() {
		String[] donateText =
				{
						"Try your luck and buy a M-box for only 2$, you may win a Party hat! ::donate",
						"Donate to help your favourite server grow ::donate",
						"Want to be a Donator with many special rewards & perks? ::donate today!",
						"Check out the Donator npc at ::shops for loads of powerful rewards!",
						"Help " + ServerConstants.getServerName() + " by donating to help the server grow! ::donate",
						"Want to own a dice bag to start hosting bets and fp? ::donate",
						"Need " + ServerConstants.getMainCurrencyName().toLowerCase() + "? You can buy it for very low prices at ::donate",
						"Mystery boxes contain Party hats, h'ween, 3rd age, elysians! Buy them at ::donate",
						"Want to get rich quick? Buy Mystery boxes which contain so many rares! ::donate",
						"Donators can yell, use an amazing Donator Zone & tonnes more! ::donate",
						"Donators can buy custom pets and custom items! Check it out at ::donate",
						"M-boxes give 15 % more loot on average than anything else in the Donator shop! ::donate",
						"Ancestral sets & rares can be purchased from the Donator shop! ::donate",
						"Donators receive " + Artefacts.donatorBloodMoneyAmount + "-" + Artefacts.supremeDonatorBloodMoneyAmount + " raw " + ServerConstants.getMainCurrencyName()
						                                                                                                                                    .toLowerCase()
						+ " on every kill! ::donate",
				};
		announcementPendingList.add(donateText[donateIndex]);
		donateIndex++;
		if (donateIndex > donateText.length - 1) {
			donateIndex = 0;
		}

	}

}
