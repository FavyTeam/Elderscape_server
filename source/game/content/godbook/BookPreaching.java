package game.content.godbook;

import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

import java.util.HashMap;
import java.util.Map;

public enum BookPreaching {

	HOLY_BOOK(3840, new String[][]
			                {
					                {"9178", "In the name of Saradomin,", "Protector of us all,", "I now join you in the eyes of Saradomin.", null},
					                {"9179", "Thy cause was false, thy skills did lack.", "See you in lumbridge when you get back", null},
					                {"9180", "Go in peace in the name of Saradomin,", "May his glory shine upon you like the sun.", null},
					                {"9181", "Walk proud, and show mercy,", "For you carry my name in your heart,", "This is Saradomin's wisdom.", null}
			                }),
	BOOK_OF_BALANCE(3844, new String[][]
			                      {
					                      {"9178", "Light and dark, day and night,", "Balance arises from contrast.", "I unify thee in the name of Guthix.", null},
					                      {"9179", "Thy death was not in vain,", "For it brought some balance to the world.", "May Guthix bring you rest.", null},
					                      {"9180", "May you walk the path and never fall,", "For Guthix walks beside thee on thy journey.", "May Guthix bring you peace.", null},
					                      {"9181", "A Journey of a single step,", "May take thee over a thousand miles.", "May Guthix bring you balance.", null}
			                      }),
	UNHOLY_BOOK(3842, new String[][]
			                  {
					                  {"9178", "Two great warriors, joined by hand,", "to spread destruction across the land.", "In Zamorak's name, now two are one.", null},
					                  {"9179", "The weak deserve to die,", "So that the strong may flourish.", "This is the creed of Zamorak.", null},
					                  {"9180", "May your bloodthirst be never sated,", "and may all your battles be glorious.", "Zamorak bring you strength", null},
					                  {"9181", "There is no opinion that cannot be proven true,", "by crushing those who choose to disagree with it.", "Zamorak give me strength!",
							                  null
					                  }
			                  });

	private int itemId;

	private String[][] preachData;

	private BookPreaching(int itemId, String[][] preachData) {
		this.itemId = itemId;
		this.preachData = preachData;
	}

	private static Map<Integer, BookPreaching> godBooks = new HashMap<Integer, BookPreaching>();

	static {
		for (final BookPreaching type : values()) {
			godBooks.put(type.itemId, type);
		}
	}

	/**
	 * Sends the options dialogue with preach options
	 *
	 * @param player the player to send the options to
	 * @param itemId the item the player's interacting with
	 */
	public static boolean sendPreachOptions(Player player, int itemId) {
		switch (itemId) {
			case 3840:
				player.nextDialogue = 0;
				player.setDialogueAction(40);
				player.getDH().sendOption("Wedding Ceremony", "Blessing", "Last Rites", "Preach");
				return true;

			// Unholy.
			case 3842:
				player.nextDialogue = 0;
				player.setDialogueAction(41);
				player.getDH().sendOption("Wedding Ceremony", "Blessing", "Last Rites", "Preach");
				return true;
			case 3844:
				player.nextDialogue = 0;
				player.setDialogueAction(42);
				player.getDH().sendOption("Wedding Ceremony", "Blessing", "Last Rites", "Preach");
				return true;
		}
		return false;
	}

	/**
	 * Handles the preaching action
	 *
	 * @param player the player preaching
	 * @param itemId the item the player's interacting with
	 * @param actionButtonId the button id the player's interacting with
	 */
	public static void handlePreach(final Player player, int itemId, final int preachType) {
		player.getPA().closeInterfaces(true);
		final BookPreaching books = godBooks.get(itemId);
		if (itemId != books.itemId) {
			return;
		}
		if (player.usingPreachingEvent) {
			return;
		}
		player.usingPreachingEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

			private int currentQuote = 1;

			@Override
			public void execute(CycleEventContainer container) {
				player.startAnimation(1670);
				if (books.preachData[preachType][currentQuote] != null) {
					player.forcedChat(books.preachData[preachType][currentQuote], false, false);
					currentQuote++;
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.usingPreachingEvent = false;
			}

		}, 3);
	}

}
