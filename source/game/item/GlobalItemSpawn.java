package game.item;

import core.Server;
import core.ServerConstants;
import core.maintick.Task;
import game.content.music.SoundSystem;
import game.player.Player;
import game.player.PlayerHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles global drops which respawn after set amount of time when taken
 *
 * @author Stuart <RogueX>
 */
public class GlobalItemSpawn {

	/**
	 * time in seconds it takes for the item to respawn
	 */
	private static final int TIME_TO_RESPAWN = 60;

	/**
	 * holds all the objects
	 */
	public static List<GlobalDrop> globalDrops = new ArrayList<GlobalDrop>();

	/**
	 * loads the items
	 */
	public static void initialize() {
		String string;
		BufferedReader Checker = null;
		try {
			Checker = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "items/global item spawns.txt"));
			while ((string = Checker.readLine()) != null) {
				if (string.startsWith("#"))
					continue;

				if (string.contains("//")) {
					string = string.substring(0, string.indexOf("//") - 1);
				}
				String[] args = string.split(":");
				globalDrops.add(new GlobalDrop(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
			}
			Checker.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Server.scheduler.schedule(new Task() {
			@Override
			protected void execute() {
				for (GlobalDrop drop : globalDrops) {
					if (drop.isTaken()) {
						if ((System.currentTimeMillis() - drop.getTakenAt()) >= (TIME_TO_RESPAWN * 1000)) {
							drop.setTaken(false);
							for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
								Player player = PlayerHandler.players[i];
								if (player != null) {
									if (player.playerAssistant.distanceToPoint(drop.getX(), drop.getY()) <= 60) {
										ItemAssistant.createGroundItem(player, drop.getId(), drop.getX(), drop.getY(), drop.getAmount());
									}
								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * See if a drop exists at the given place
	 *
	 * @param a item id
	 * @param b x cord
	 * @param c y cord
	 * @return
	 */
	private static GlobalDrop itemExists(int a, int b, int c, int amount) {
		for (GlobalDrop drop : globalDrops) {
			if (drop.getId() == a && drop.getX() == b && drop.getY() == c && drop.getAmount() == amount) {
				return drop;
			}
		}
		return null;
	}

	/**
	 * Pick up an item at the given location
	 *
	 * @param itemId item id
	 * @param itemX cord x
	 * @param itemY cord y
	 */
	public static void pickup(Player player, int itemId, int itemX, int itemY, int amount) {
		GlobalDrop drop = itemExists(itemId, itemX, itemY, amount);

		if (drop == null) {
			return;
		}
		if (drop.isTaken()) {
			return;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0 && ItemDefinition.getDefinitions()[player.itemPickedUpId].stackable && ItemAssistant.hasItemInInventory(player,
		                                                                                                                                                             player.itemPickedUpId)) {
			if (player.lastItemIdPickedUp == itemId) {
				if (System.currentTimeMillis() - player.timePickedUpItem < 500) {
					return;
				}
			}
			player.lastItemIdPickedUp = itemId;
			ItemAssistant.addItem(player, itemId, drop.getAmount());
			SoundSystem.sendSound(player, 356, 0);
		} else if (ItemAssistant.getFreeInventorySlots(player) > 0) {
			if (player.lastItemIdPickedUp == itemId) {
				if (System.currentTimeMillis() - player.timePickedUpItem < 500) {
					return;
				}
			}
			player.timePickedUpItem = System.currentTimeMillis();
			player.lastItemIdPickedUp = itemId;
			ItemAssistant.addItem(player, itemId, drop.getAmount());
			SoundSystem.sendSound(player, 356, 0);
		} else {
			player.playerAssistant.sendMessage("You do not have enough inventory space.");
			return;
		}
		drop.setTakenAt(System.currentTimeMillis());
		drop.setTaken(true);
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player loopedPlayer = PlayerHandler.players[i];
			if (loopedPlayer != null) {
				if (loopedPlayer.playerAssistant.distanceToPoint(drop.getX(), drop.getY()) <= 60) {
					ItemAssistant.removeGroundItem(loopedPlayer, drop.getId(), drop.getX(), drop.getY(), drop.getAmount());
				}
			}
		}
	}

	/**
	 * Loads all the items when a player changes region
	 */
	public static void loadGlobalDrops(Player player) {
		for (GlobalDrop drop : globalDrops) {
			if (!drop.isTaken()) {
				if (player.playerAssistant.distanceToPoint(drop.getX(), drop.getY()) <= 60) {
					ItemAssistant.createGroundItem(player, drop.getId(), drop.getX(), drop.getY(), drop.getAmount());
				}
			}
		}
	}

	/**
	 * Holds each drops data
	 *
	 * @author Stuart
	 */
	public static class GlobalDrop {
		/**
		 * cord x
		 */
		int x;

		/**
		 * cord y
		 */
		int y;

		/**
		 * item id
		 */
		int id;

		/**
		 * item amount
		 */
		int amount;

		/**
		 * has the item been taken
		 */
		boolean taken = false;

		/**
		 * Time it was taken at
		 */
		long takenAt;

		/**
		 * Sets the drop arguments
		 *
		 * @param id item id
		 * @param amount item amount
		 * @param x cord x
		 * @param y cord y
		 */
		public GlobalDrop(int id, int amount, int x, int y) {
			this.id = id;
			this.amount = amount;
			this.x = x;
			this.y = y;
		}

		/**
		 * get cord x
		 *
		 * @return
		 */
		public int getX() {
			return this.x;
		}

		/**
		 * get cord x
		 *
		 * @return
		 */
		public int getY() {
			return this.y;
		}

		/**
		 * get the item id
		 *
		 * @return
		 */
		public int getId() {
			return this.id;
		}

		/**
		 * get the item amount
		 *
		 * @return
		 */
		public int getAmount() {
			return this.amount;
		}

		/**
		 * has the drop already been taken?
		 *
		 * @return
		 */
		public boolean isTaken() {
			return this.taken;
		}

		/**
		 * set if or not the drop has been taken
		 *
		 * @param a true yes false no
		 */
		public void setTaken(boolean a) {
			this.taken = a;
		}

		/**
		 * set the time it was picked up
		 *
		 * @param a
		 */
		public void setTakenAt(long a) {
			this.takenAt = a;
		}

		/**
		 * get the time it was taken at
		 *
		 * @return
		 */
		public long getTakenAt() {
			return this.takenAt;
		}

	}

}
