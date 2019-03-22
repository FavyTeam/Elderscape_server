package game.content.miscellaneous;



import java.util.ArrayList;

import core.ServerConstants;
import game.content.combat.vsplayer.melee.MeleeData;
import game.item.ItemAssistant;
import game.object.clip.DynamicClipping;
import game.object.clip.ObjectDefinitionServer;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Slashing web.
 *
 * @author MGT Madness, created on 20-03-2015.
 */
public class Web {

	/**
	 * Web list, x, y, z, direction, state.
	 * State is either CUT or UNCUT.
	 */
	public static ArrayList<String> webList = new ArrayList<String>();

	public final static int WEB_OBJECT_ID = 733;

	private final static int WEB_RESPAWN_TICKS = 60;

	private final static int KNIFE_ITEM_ID = 946;

	/**
	 * Items used for slicing through webs at Magebank.
	 *
	 * @return True, if the player has a sharp weapon wielded.
	 */
	private static boolean wieldingSharpWeapon(Player player) {
		String s = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();
		if (s.contains("staff of the dead") || s.contains("2h") || s.contains("sword") || s.contains("dagger") || s.contains("rapier") || s.contains("scimitar") || s.contains(
				"halberd") || s.contains("spear") || s.contains("axe") || s.contains("claws") || s.contains("whip") || s.contains("abyssal tentacle") || s.contains(
				"toxic staff of the dead")) {
			return true;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player has the requirements to slash the web.
	 */
	private static boolean hasRequirements(Player player, int itemId) {
		if (itemId != 946) {
			return false;
		}
		if (System.currentTimeMillis() - player.lastWebCut < 1100) {
			return false;
		}
		player.lastWebCut = System.currentTimeMillis();
		if (!wieldingSharpWeapon(player) && !ItemAssistant.hasItemInInventory(player, KNIFE_ITEM_ID)) {
			player.playerAssistant.sendMessage("You need a sharp blade to cut the web.");
			return false;
		}
		return true;
	}

	/**
	 * Slash web feature.
	 *
	 * @param player
	 */
	public static void slash(Player player, int itemId) {
		if (!hasRequirements(player, itemId)) {
			return;
		}

		// Web closest to Mage bank lever
		if (player.getObjectX() == 3092 && player.getObjectY() == 3957) {
			if (Area.isWithInArea(player, 3090, 3092, 3956, 3958) || Area.isWithInArea(player, 3090, 3091, 3955, 3955)) {
				player.turnPlayerTo(3093, 3957);
			}
		}

		// Web second closest to Mage bank lever
		else if (player.getObjectX() == 3095 && player.getObjectY() == 3957) {
			boolean firstCompartment = Area.isWithInArea(player, 3090, 3092, 3956, 3958) || Area.isWithInArea(player, 3090, 3091, 3955, 3955);
			if (!firstCompartment && !Area.isWithInArea(player, 3093, 3094, 3956, 3958)) {
				player.turnPlayerTo(3094, 3957);
			}
		} else if (player.getObjectX() == 3105 && player.getObjectY() == 3958) {
			boolean inside = Area.isWithInArea(player, 3105, 3107, 3955, 3957);
			if (!inside) {
				player.turnPlayerTo(3105, 3957);
			}
		} else if (player.getObjectX() == 3106 && player.getObjectY() == 3958) {
			boolean inside = Area.isWithInArea(player, 3105, 3107, 3955, 3957);
			if (!inside) {
				player.turnPlayerTo(3106, 3957);
			}
		}
		player.startAnimation(MeleeData.getWeaponAnimation(player, ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase()));
		slashWebEvent(player);
	}

	/**
	 * Delete the web 1 game tick later.
	 *
	 * @param player The associated player.
	 */
	private static void slashWebEvent(final Player player) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {

				// Loop through web list to grab the face.
				for (int index = 0; index < webList.size(); index++) {
					String parse[] = webList.get(index).split(" ");
					String old = webList.get(index);
					int x = Integer.parseInt(parse[0]);
					int y = Integer.parseInt(parse[1]);
					int height = Integer.parseInt(parse[2]);
					int face = Integer.parseInt(parse[3]);
					String state = parse[4];
					if (state.equals("CUT")) {
						continue;
					}
					// Direction string is where the web coordinate is visually beside the web.
					// So if it says WEST, it means the web coordinate is west of the web.
					if (x == player.getObjectX() && y == player.getObjectY() && height == player.getHeight()) {

						if (state.equals("CUT")) {
							return;
						}
						if (Misc.hasPercentageChance(35)) {
							player.getPA().sendMessage("You fail to slash the web.");
							return;
						}
						int type = 0;

						int direction = Region.EAST;
						if (face == 1) {
							direction = Region.SOUTH;
						} else if (face == 3) {
							direction = Region.NORTH;
						} else if (face == 2) {
							direction = Region.WEST;
						}
						webList.remove(index);
						webList.add(old.replace("UNCUT", "CUT"));
						new Object(WEB_OBJECT_ID + 1, player.getObjectX(), player.getObjectY(), player.getHeight(), face, type, WEB_OBJECT_ID, WEB_RESPAWN_TICKS);
						player.playerAssistant.sendMessage("You slash the web.");
						if (direction == Region.WEST) {
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.EAST, true, ""));
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x + 1, y, height, Region.WEST, true, ""));
						} else if (direction == Region.EAST) {
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.WEST, true, ""));
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x - 1, y, height, Region.EAST, true, ""));
						} else if (direction == Region.NORTH) {
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.SOUTH, true, ""));
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y - 1, height, Region.NORTH, true, ""));
						} else if (direction == Region.SOUTH) {
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y, height, Region.NORTH, true, ""));
							DynamicClipping.dynamicClipping.add(new DynamicClipping(x, y + 1, height, Region.SOUTH, true, ""));
						}
						break;
					}

				}
			}
		}, 1);
	}

	/**
	 * When the web respawns.
	 *
	 * @param object
	 */
	public static void webRespawning(Object object) {
		if (object.objectId != 734) {
			return;
		}
		for (int index = 0; index < Web.webList.size(); index++) {
			String parse[] = Web.webList.get(index).split(" ");
			String old = Web.webList.get(index);
			int x = Integer.parseInt(parse[0]);
			int y = Integer.parseInt(parse[1]);
			int height = Integer.parseInt(parse[2]);
			int face = Integer.parseInt(parse[3]);
			// Direction string is where the web coordinate is visually beside the web.
			// So if it says WEST, it means the web coordinate is west of the web.
			if (x == object.objectX && y == object.objectY && height == object.height) {
				Web.webList.remove(index);
				Web.webList.add(old.replace("CUT", "UNCUT"));
				int direction = Region.EAST;
				if (face == 1) {
					direction = Region.SOUTH;
				} else if (face == 3) {
					direction = Region.NORTH;
				} else if (face == 2) {
					direction = Region.WEST;
				}
				if (direction == Region.WEST) {
					DynamicClipping.removeFromDynamicTileClipping(x, y, height, Region.EAST, true);
					DynamicClipping.removeFromDynamicTileClipping((x + 1), y, height, Region.WEST, true);
				} else if (direction == Region.EAST) {
					DynamicClipping.removeFromDynamicTileClipping(x, y, height, Region.WEST, true);
					DynamicClipping.removeFromDynamicTileClipping((x - 1), y, height, Region.EAST, true);
				} else if (direction == Region.NORTH) {
					DynamicClipping.removeFromDynamicTileClipping(x, y, height, Region.SOUTH, true);
					DynamicClipping.removeFromDynamicTileClipping(x, (y - 1), height, Region.NORTH, true);
				} else if (direction == Region.SOUTH) {
					DynamicClipping.removeFromDynamicTileClipping(x, y, height, Region.NORTH, true);
					DynamicClipping.removeFromDynamicTileClipping(x, (y + 1), height, Region.SOUTH, true);
				}
				break;
			}
		}

	}

	/**
	 * Read the web object data from a text file.
	 */
	public static void readWebData() {
		ArrayList<String> data = FileUtility.readFile(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/object/web_data.txt");

		// Web object id definition
		ObjectDefinitionServer definition = ObjectDefinitionServer.getObjectDef(734);
		for (int index = 0; index < data.size(); index++) {
			String parse[] = data.get(index).split(" ");
			int x = Integer.parseInt(parse[0]);
			int y = Integer.parseInt(parse[1]);
			int height = Integer.parseInt(parse[2]);
			int face = Integer.parseInt(parse[3]);
			webList.add(data.get(index));
			Region.removeClipping(x, y, 0);
			Region.addClippingForVariableObject(x, y, height, 0, face, definition.solid());
		}
	}

}
