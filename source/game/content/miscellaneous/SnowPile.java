package game.content.miscellaneous;

import core.ServerConstants;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.object.custom.Object;
import game.object.custom.ObjectManagerServer;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

import java.util.ArrayList;
import java.util.List;

public class SnowPile {
	/**
	 * The amount of snow balls that can be removed from the snowpile. Once it reaches 0, the next snowpile smaller version will spawn.
	 */
	private final static int SNOW_PILE_HEALTH_PER_STAGE = 1500;

	/**
	 * Minimum amount of digs to receive a scarf/hat
	 */
	private final static int MINIMUM_DIGS_FOR_A_SCARF = 120;

	/**
	 * For every 20 digs, the player will receive a bauble.
	 */
	private final static int DIGS_PER_BAUBLE_REWARD = 40;

	private final static int[] SNOW_PILE_OBJECT_STAGES =
			{19030, 19031, 19033, 19034, 19035};

	private final static int[] BAUBLES =
			{6822, 6823, 6824, 6825, 6826, 6827,

			};

	private final static int[] HAT_SCARVES =
			{6856, 6857, 6858, 6859, 6860, 6861, 6862, 6863,};

	private final static int[] RARE_REWARDS =
			{
					13344, // Inverted santa
					21859, // Santa partyhat thing
					//21865, // Xmas net, used to catch snow sprite npcs
			};

	private String playerName = "";

	private int digs;

	private SnowPile(String playerName, int digs) {
		this.playerName = playerName;
		this.digs = digs;
	}

	public static List<SnowPile> snowPileList = new ArrayList<SnowPile>();

	private static int snowPileCurrentHealth;

	private static int snowPileStage;

	public static ArrayList<String> snowpileLocations = new ArrayList<String>();

	public static void readLocations() {
		snowpileLocations.add("2976 3400 = north of Falador");
		snowpileLocations.add("3093 3226 = south of Draynor");
		snowpileLocations.add("3252 3285 = in the Lumbridge cow field");
		snowpileLocations.add("3194 3435 = in central Varrock");
		snowpileLocations.add("3050 3489 = at the Monastery");
		snowpileLocations.add("2746 3469 = in Camelot");
		snowpileLocations.add("2627 3122 = north east of Yanille");
		snowpileLocations.add("2882 3462 = west of Taverly");
		snowpileLocations.add("2812 3358 = west of Entrana");
	}

	private static String location = "";

	public static void spawnSnowpile() {
		snowPileCurrentHealth = SNOW_PILE_HEALTH_PER_STAGE;
		snowPileStage = 0;
		snowPileList.clear();
		spawnSnowPileObject();
		Object object = null;
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				// Final stage.
				if (snowPileStage == SNOW_PILE_OBJECT_STAGES.length - 1) {
					container.stop();
					return;
				}
				announceLocation();
			}

			@Override
			public void stop() {

			}
		}, 100);
	}

	private static void announceLocation() {
		Announcement.announce(location, ServerConstants.DARK_BLUE);
		Announcement.announce("Dig through it for a chance to receive rare rewards!", ServerConstants.DARK_BLUE);
	}

	private static void spawnSnowPileObject() {
		int random = Misc.random(0, snowpileLocations.size() - 1);
		String parseCoords[] = snowpileLocations.get(random).split(" ");
		String parseDescription[] = snowpileLocations.get(random).split("= ");
		location = "A snow pile has appeared " + parseDescription[1] + "!";
		announceLocation();
		new Object(SNOW_PILE_OBJECT_STAGES[snowPileStage], Integer.parseInt(parseCoords[0]), Integer.parseInt(parseCoords[1]), 0, 0, 10, -1, -1);
	}

	private static Object deleteAllSnowPileObjects() {
		for (int index = 0; index < ObjectManagerServer.objects.size(); index++) {
			Object object = ObjectManagerServer.objects.get(index);
			for (int a = 0; a < SNOW_PILE_OBJECT_STAGES.length; a++) {
				if (object.objectId == SNOW_PILE_OBJECT_STAGES[a]) {
					ObjectManagerServer.deleteGlobalObjectPacket(object.objectX, object.objectY, object.height, object.type);
					ObjectManagerServer.objects.remove(index);
					return object;
				}
			}
		}
		return null;
	}

	public static boolean isSnowPileObject(Player player, int objectId) {
		for (int a = 0; a < SNOW_PILE_OBJECT_STAGES.length; a++) {
			if (objectId == SNOW_PILE_OBJECT_STAGES[a]) {
				if (snowPileStage == SNOW_PILE_OBJECT_STAGES.length - 1) {
					finalStageObjectClick(player);
					return true;
				}
				normalSnowpileObjectDig(player);
				return true;
			}
		}
		return false;
	}

	private static void normalSnowpileObjectDig(Player player) {
		if (System.currentTimeMillis() - player.actionDelay < 500) {
			return;
		}
		player.actionDelay = System.currentTimeMillis();

		if (player.questStages[2] != 7) {
			player.getDH().sendStatement("You have not completed the Christmas quest, talk to Santa at Edgeville.");
			return;
		}
		if (System.currentTimeMillis() - player.snowPileAnimationDelay > 2500) {
			player.startAnimation(5067);
			player.snowPileAnimationDelay = System.currentTimeMillis();
		}
		ItemAssistant.addItem(player, 10501, 1);
		SnowPile instance = getSnowPileListInstance(player.getPlayerName());
		if (instance == null) {
			snowPileList.add(new SnowPile(player.getPlayerName(), 1));
		} else {
			instance.digs = instance.digs + 1;
		}
		snowPileCurrentHealth--;
		if (snowPileCurrentHealth == 0) {
			snowPileCurrentHealth = SNOW_PILE_HEALTH_PER_STAGE;
			Object object = deleteAllSnowPileObjects();
			if (snowPileStage < SNOW_PILE_OBJECT_STAGES.length - 1) {
				snowPileStage++;
				new Object(SNOW_PILE_OBJECT_STAGES[snowPileStage], object.objectX, object.objectY, 0, 0, 10, -1, -1);
				informNearbyPlayersOfSnowPileChange(object.objectX, object.objectY);
				deleteFinalSnowPileObjectTimer();
			}
		}

	}

	private static void informNearbyPlayersOfSnowPileChange(int objectX, int objectY) {
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (Misc.distanceToPoint(loop.getX(), loop.getY(), objectX, objectY) <= 15) {
				loop.getPA().sendMessage("The snow pile is dissolving.");
			}
		}

	}

	private static void deleteFinalSnowPileObjectTimer() {
		if (snowPileStage == SNOW_PILE_OBJECT_STAGES.length - 1) {
			String firstName = "";
			int firstDigs = 0;
			String secondName = "";
			int secondDigs = 0;
			for (int index = 0; index < snowPileList.size(); index++) {
				SnowPile instance = snowPileList.get(index);
				if (instance.digs > firstDigs) {
					firstDigs = instance.digs;
					firstName = instance.playerName;
				}
			}
			for (int index = 0; index < snowPileList.size(); index++) {
				SnowPile instance = snowPileList.get(index);
				if (instance.digs > secondDigs && !instance.playerName.equals(firstName)) {
					secondDigs = instance.digs;
					secondName = instance.playerName;
				}
			}
			Announcement.announce("The top digger is " + firstName + " with " + firstDigs + " digs, followed by " + secondName + " with " + secondDigs + " digs!",
			                      ServerConstants.DARK_BLUE);
			Object object = null;
			CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					Announcement.announce("The Snowpile event has ended!", ServerConstants.DARK_BLUE);
					deleteAllSnowPileObjects();
				}
			}, 100);
		}

	}

	private static SnowPile getSnowPileListInstance(String playerName) {
		for (int index = 0; index < snowPileList.size(); index++) {
			SnowPile instance = snowPileList.get(index);
			if (instance.playerName.equals(playerName)) {
				return instance;
			}
		}
		return null;
	}

	private static void finalStageObjectClick(Player player) {
		for (int index = 0; index < snowPileList.size(); index++) {
			SnowPile instance = snowPileList.get(index);
			if (instance.playerName.equals(player.getPlayerName())) {
				giveReward(player, instance.digs);
				snowPileList.remove(index);
				return;
			}
		}
		player.getPA().sendMessage("There is no reward for you to collect.");
	}

	private static void giveReward(Player player, int digs) {
		if (digs < DIGS_PER_BAUBLE_REWARD) {
			player.getPA().sendMessage("You did not dig enough in the event, no rewards await you..");
			return;
		}
		player.getPA().sendMessage("You have found rewards based on your " + Misc.formatNumber(digs) + " digs!");
		int baublesAmount = digs / DIGS_PER_BAUBLE_REWARD;
		int rareRewardChance = 200;
		for (int index = 0; index < baublesAmount; index++) {
			ItemAssistant.addItemToInventoryOrDrop(player, BAUBLES[Misc.random(0, BAUBLES.length - 1)], 1);
		}
		if (digs > MINIMUM_DIGS_FOR_A_SCARF) {
			ItemAssistant.addItemToInventoryOrDrop(player, HAT_SCARVES[Misc.random(0, HAT_SCARVES.length - 1)], 1);
		}

		if (Misc.hasOneOutOf(rareRewardChance)) {
			int rareRewardItem = RARE_REWARDS[Misc.random(0, RARE_REWARDS.length - 1)];
			ItemAssistant.addItemToInventoryOrDrop(player, rareRewardItem, 1);
			Announcement.announce(
					ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " received one " + ItemAssistant.getItemName(rareRewardItem) + " from the Snow pile event.");
			RareDropLog.appendRareDrop(player, "Snow pile: " + ItemAssistant.getItemName(rareRewardItem));
		}
	}

}
