package game.content.worldevent;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.Skull;
import game.content.packet.PickUpItemPacket;
import game.content.starter.GameMode;
import game.item.GroundItem;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.npc.NpcHandler;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

import java.util.ArrayList;

/**
 * Blood key world event.
 *
 * @author MGT Madness, created on 28-02-2017.
 */
public class BloodKey {

	public static int getBloodKeyCurrencyLootAmount() {
		return GameType.isOsrsPvp() ? 50000 : 38000000;
	}

	public static int getMiniBloodKeyCurrencyLootAmount() {
		return GameType.isOsrsPvp() ? 25000 : 19000000;
	}

	public static boolean isAnyBloodKey(int itemId) {
		return itemId == 20526 || itemId == 13302;
	}

	public static boolean isBloodKey(int itemId) {
		return itemId == 20526;
	}

	public static boolean isMiniBloodKey(int itemId) {
		return itemId == 13302;
	}

	private final static int SECONDS_TO_PICK_UP_KEY = 50;

	private static final int LEAST_DAMAGE_TO_BOX_OUT = 8;

	public static ArrayList<String> keyCoordsData = new ArrayList<String>();

	public static ArrayList<String> bossCoordsData = new ArrayList<String>();

	public static void bloodKeyInitialize() {
		keyCoordsData.add("3259 3779 = south east of Chaos dwarfs");
		keyCoordsData.add("3366 3936 = at the lava bridge");
		keyCoordsData.add("3285 3945 = at Rogue's castle");
		keyCoordsData.add("3038 3704 = at the Bandit camp");
		keyCoordsData.add("3241 3614 = at the Chaos temple");


		bossCoordsData.add("3060 3828 = north of Lava dragons");
		bossCoordsData.add("3043 3938 = north of Mage bank stairs");
		bossCoordsData.add("3106 3907 = south of Mage arena");
		bossCoordsData.add("2979 3781 = north of Revenants");
	}

	public static String location = "";

	public static long timeBloodKeySpawned;

	public static long timeBossSpawned;

	/**
	 * Which key is currently active, KEY or BOSS. Key is for Blood key, BOSS is for Skotizo.
	 */
	public static String keyType = "";


	public static int tick = -1;

	public static long spawnKeyAnnounced;

	@SuppressWarnings("unchecked")
	public static void spawnBloodKey(String type) {
		keyType = type;
		spawnKeyAnnounced = System.currentTimeMillis();
		String string = "";
		if (type.equals("KEY")) {
			string = "Pickup the blood key for " + Misc.formatRunescapeStyle(getBloodKeyCurrencyLootAmount()) + " loot!";
		} else {
			string = "Kill Skotizo for 25-85k loot!";
		}
		final String finalString = string;
		Announcement.announce(finalString, ServerConstants.DARK_BLUE);
		Announcement.announce("Location will be announced in 3 minutes.", ServerConstants.DARK_BLUE);
		tick = 0;
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				tick++;
				if (tick == 3) {
					container.stop();
				} else if (tick == 2) {
					Announcement.announce(finalString, ServerConstants.DARK_BLUE);
					Announcement.announce("Location will be announced in 1 minute.", ServerConstants.DARK_BLUE);
				} else if (tick == 1) {
					Announcement.announce(finalString, ServerConstants.DARK_BLUE);
					Announcement.announce("Location will be announced in 2 minutes.", ServerConstants.DARK_BLUE);
				}
			}

			@Override
			public void stop() {
				keyType = "";
				tick = -1;
				ArrayList<String> data = new ArrayList<String>();
				if (type.equals("KEY")) {
					data = keyCoordsData;
				} else {
					data = bossCoordsData;
				}
				int random = Misc.random(0, data.size() - 1);
				String parseCoords[] = data.get(random).split(" ");
				String parseDescription[] = data.get(random).split("= ");
				String text = "The blood key";
				if (type.equals("BOSS")) {
					text = "Skotizo";
				}
				String description = text + " has spawned " + parseDescription[1] + "!";
				location = description;
				if (type.equals("BOSS")) {
					Announcement.announce("The top 2 damage dealers will get the reward!", ServerConstants.DARK_BLUE);
					Announcement.announce(finalString, ServerConstants.DARK_BLUE);
					timeBossSpawned = System.currentTimeMillis();
				} else {
					timeBloodKeySpawned = System.currentTimeMillis();
					Announcement.announce("Pick it up before it dissapears!", ServerConstants.DARK_BLUE);
					Announcement.announce("Do not attack back in singles with key or server will 1 hit you.", ServerConstants.DARK_BLUE);
					Announcement.announce("Reward is " + Misc.formatRunescapeStyle(getBloodKeyCurrencyLootAmount()) + " loot.", ServerConstants.DARK_BLUE);
				}
				Announcement.announce(description, ServerConstants.DARK_BLUE);
				if (type.equals("BOSS")) {
					NpcHandler.spawnNpc(null, 7286, Integer.parseInt(parseCoords[0]), Integer.parseInt(parseCoords[1]), 0, false, false);
				} else {
					GroundItem item = new GroundItem(20526, Integer.parseInt(parseCoords[0]), Integer.parseInt(parseCoords[1]), 1, 2, "", "", 0, 0, "OTHER: Blood key spawn", "",
							"", "", "Blood key, " + type);
					Server.itemHandler.addItem(item);
				}
			}
		}, 100);

	}

	public static void pickUpBloodKey(Player player, int itemId) {
		if (player.getCombatLevel() != 126) {
			player.getPA().sendMessage("Sneaky sneaky, 126 or no can do.");
			return;
		}
		if (isBloodKey(itemId)) {
			if (System.currentTimeMillis() - BloodKey.timeBloodKeySpawned < SECONDS_TO_PICK_UP_KEY * 1000) {
				long time = SECONDS_TO_PICK_UP_KEY - ((System.currentTimeMillis() - BloodKey.timeBloodKeySpawned) / 1000);
				player.getPA().sendMessage("You can pickup the key in " + time + " second" + Misc.getPluralS((int) time) + ".");
				return;
			}
			player.getDH().sendDialogues(266);
		} else {
			player.getDH().sendDialogues(471);
		}
	}

	public static void confirmPickUpBloodKeyBoss(Player player) {

		player.getPA().closeInterfaces(true);
		if (player.getCombatLevel() != 126) {
			player.getPA().sendMessage("Sneaky sneaky, 126 or no can do.");
			return;
		}
		boolean hasKey = ItemAssistant.hasItemInInventory(player, 13302);
		if (!hasKey && player.getPA().withInDistance(player.itemPickedUpX, player.itemPickedUpY, player.getX(), player.getY(), 1)) {
			if (PickUpItemPacket.pickUpItem(player, 1)) {
				if (ItemAssistant.hasItemInInventory(player, 13302)) {
					Announcement.announce(player.getPlayerName() + " has picked up the blood key at level " + player.getWildernessLevel() + " wild!", ServerConstants.DARK_BLUE);
					Skull.goldenSkullBoss(player);
					player.teleBlockEndTime = System.currentTimeMillis() + 86400000;
					OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_TELEBLOCK, 86400);
				}
			}
		} else {
			player.getPA().sendMessage("You may only pick up one blood key at a time!");
		}
	}

	public static void confirmPickUpBloodKey(Player player) {
		player.getPA().closeInterfaces(true);
		if (player.getCombatLevel() != 126) {
			player.getPA().sendMessage("Sneaky sneaky, 126 or no can do.");
			return;
		}
		if (System.currentTimeMillis() - BloodKey.timeBloodKeySpawned < SECONDS_TO_PICK_UP_KEY * 1000) {
			long time = SECONDS_TO_PICK_UP_KEY - ((System.currentTimeMillis() - BloodKey.timeBloodKeySpawned) / 1000);
			player.getPA().sendMessage("You can pickup the key in " + time + " second" + Misc.getPluralS((int) time) + ".");
			return;
		}
		boolean hasKey = ItemAssistant.hasItemInInventory(player, 20526);
		if (!hasKey && player.getPA().withInDistance(player.itemPickedUpX, player.itemPickedUpY, player.getX(), player.getY(), 1)) {
			if (PickUpItemPacket.pickUpItem(player, 1)) {
				if (ItemAssistant.hasItemInInventory(player, 20526)) {
					Announcement.announce(player.getPlayerName() + " has picked up the blood key at level " + player.getWildernessLevel() + " wild!", ServerConstants.DARK_BLUE);
					Skull.goldenSkullBoss(player);
					player.teleBlockEndTime = System.currentTimeMillis() + 86400000;
					OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_TELEBLOCK, 86400);
				}
			}
		} else {
			player.getPA().sendMessage("You may only pick up one blood key at a time!");
		}
	}

	public static void leftWild(Player player) {
		// Golden skull.
		if (player.headIconPk == 2 && player.getHeight() != 20) {
			Announcement.announce(player.getPlayerName() + " has made it out alive!", ServerConstants.DARK_BLUE);
			Skull.clearSkull(player);
		}

	}

	public static void openChest(Player player, int itemId) {
		if (itemId != 20526 && itemId != 13302) {
			player.getPA().sendMessage("You do not have a blood key.");
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, itemId, 1);
		int amount = getBloodKeyCurrencyLootAmount();
		if (isMiniBloodKey(itemId)) {
			amount = getMiniBloodKeyCurrencyLootAmount();
		}
		ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), amount);
		if (!player.isAdministratorRank()) {
			CoinEconomyTracker.addIncomeList(player, "BLOOD-KEY " + amount);
		}
		player.startAnimation(6387);
		int reward = 0;
		boolean announce = false;
		if (isMiniBloodKey(itemId)) {
			if (Misc.hasOneOutOf(8)) {
				int random = Misc.random(1, 3);
				reward = 19481;
				if (random <= 2) {
					reward = 21003;
				} else if (random == 3) {
					reward = 11284;
				}
				announce = true;
			}
		} else {
			reward = Misc.hasPercentageChance(65) ? 12829 : 12831;
		}
		if (reward > 0) {
			ItemAssistant.addItemToInventoryOrDrop(player, reward, 1);
		}
		if (announce) {
			Announcement.announce(
					ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received one " + ItemAssistant.getItemName(reward) + " from the " + ItemAssistant
							                                                                                                                                         .getItemName(
									                                                                                                                                         itemId)
					+ "");
			player.getPA().sendScreenshot(ItemAssistant.getItemName(reward), 2);
		}
		if (reward > 0) {
			Announcement.announce(
					player.getPlayerName() + " has received " + Misc.formatRunescapeStyle(amount) + " bm and a " + ItemAssistant.getItemName(reward) + " from the " + ItemAssistant
							                                                                                                                                                  .getItemName(
									                                                                                                                                                  itemId)
					+ "", ServerConstants.DARK_BLUE);
		} else {
			Announcement.announce(
					player.getPlayerName() + " has received " + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " from the "
					+ ItemAssistant.getItemName(itemId) + "", ServerConstants.DARK_BLUE);
		}

	}

	/**
	 * Prevent boxing key out in singles.
	 */
	public static void goldenSkullPlayerDamaged(Player victim, Player attacker, int damage) {
		if (Area.inMulti(victim.getX(), victim.getY())) {
			return;
		}
		if (victim.headIconPk == 2 && victim.getHeight() != 20 && victim.lastPlayerAttackedName.equals(attacker.getPlayerName())
		    && System.currentTimeMillis() - victim.getTimeAttackedAnotherPlayer() <= 6000) {
			if (System.currentTimeMillis() - victim.timeGoldenSkullAttacked <= 2000) {
				return;
			}
			boolean completedSize = false;
			if (victim.antiBoxingData.size() == 15) {
				victim.antiBoxingData.remove(0);
				completedSize = true;
			}
			victim.timeGoldenSkullAttacked = System.currentTimeMillis();
			victim.antiBoxingData.add(damage + "");
			if (completedSize) {
				int totalDamage = 0;
				for (int index = 0; index < victim.antiBoxingData.size(); index++) {
					totalDamage += Integer.parseInt(victim.antiBoxingData.get(index));
				}
				if (totalDamage / victim.antiBoxingData.size() < LEAST_DAMAGE_TO_BOX_OUT) {
					Announcement.announce(victim.getPlayerName() + " has been 1 hit by " + ServerConstants.getServerName() + " for boxing his way out.", ServerConstants.RED_COL);
					Combat.createHitsplatOnPlayerNormal(victim, victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS), ServerConstants.POISON_HITSPLAT_COLOUR,
					                                    ServerConstants.NO_ICON);
				}
			}
		}

	}

	public static boolean hasAnyBloodKeyInInventory(Player player) {
		return ItemAssistant.hasItemInInventory(player, 20526) || ItemAssistant.hasItemInInventory(player, 13302);
	}

	public static void diedWithBloodKey(Player victim) {
		if (hasAnyBloodKeyInInventory(victim) && !victim.isAdministratorRank()) {
			Announcement.announce(victim.getPlayerName() + " has died with the blood key at level " + victim.getWildernessLevel() + " wild!", ServerConstants.DARK_BLUE);
		}

	}

}
