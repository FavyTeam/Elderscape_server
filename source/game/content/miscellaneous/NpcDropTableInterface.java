package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.item.ItemAssistant;
import game.npc.NpcDrops;
import game.npc.NpcDrops.NpcIdList;
import game.npc.data.NpcDefinition;
import game.player.Player;
import java.util.ArrayList;
import utility.Misc;

/**
 * Npc drop table interface, viewable by examining an npc or manually opening it.
 *
 * @author MGT Madness, created on 03-11-2016.
 */
public class NpcDropTableInterface {

	public static void popUpSearchTermReceived(Player player, int interfaceButtonId, String search) {
		if (player.getInterfaceIdOpened() != 29050) {
			return;
		}
		player.npcDropTableSearchList.clear();
		player.popUpSearchInterfaceButtonId = interfaceButtonId;
		player.popUpSearchTerm = search;
		switch (interfaceButtonId) {
			case 30420:
				for (int index = 0; index < NpcDrops.npcIdOrder.size(); index++) {
					int npcId = NpcDrops.npcIdOrder.get(index);
					if (NpcDefinition.getDefinitions()[npcId].name.toLowerCase().contains(search)) {
						player.npcDropTableSearchList.add(npcId);
					}
				}
				clearDropTableInterface(player);
				displayInterface(player, false);
				break;
			case 30424:
				for (int index = 0; index < NpcIdList.npcIdStoredList.size(); index++) {
					int npcId = NpcDrops.npcIdOrder.get(index);
					for (int a = 0; a < NpcDrops.NPC_DROPS[npcId].itemId.size(); a++) {
						int itemId = NpcDrops.NPC_DROPS[npcId].itemId.get(a);
						if (ItemAssistant.getItemName(itemId).toLowerCase().contains(search)) {
							player.npcDropTableSearchList.add(npcId);
							break;
						}
					}
				}
				clearDropTableInterface(player);
				displayInterface(player, false);
				break;
		}
	}

	public static void clearDropTableInterface(Player player) {
		player.getPA().setTextClicked(player.textClickedInterfaceId, false);
		int interfaceId = 33301;
		for (int index = 0; index < 100; index++) {
			player.getPA().sendFrame126("", interfaceId);
			interfaceId++;
			player.getPA().sendFrame126("", interfaceId);
			interfaceId++;
			player.getPA().sendFrame126("", interfaceId);
			interfaceId++;
			player.getPA().sendFrame126("", interfaceId);
			interfaceId++;
			player.getPA().sendFrame34(interfaceId, 0, 0, 0);
			interfaceId++;
			interfaceId++;
		}
	}


	public static void displayInterface(Player player, boolean openInterface) {
		ArrayList<Integer> npcLocalListIds = new ArrayList<Integer>();
		if (player.npcDropTableSearchList.isEmpty() && openInterface) {
			npcLocalListIds = NpcDrops.npcIdOrder;
		} else {
			npcLocalListIds = player.npcDropTableSearchList;
		}
		int offset = 0;
		for (int index = 0; index < NpcDrops.npcIdOrder.size(); index++) {
			String name = "";
			if (index <= npcLocalListIds.size() - 1) {
				int npcId = npcLocalListIds.get(index);
				name = NpcDefinition.getDefinitions()[npcId].name;
				int maximumNameLength = 18;
				if (name.length() > maximumNameLength) {
					name = name.substring(0, maximumNameLength);
				}
			} else {

			}
			player.getPA().sendFrame126(name, 27702 + index + offset);
			offset++;
		}
		int scrollLength = (int) Misc.getDoubleRoundedUp((double) npcLocalListIds.size() * 18);
		int minimumScrollLength = 232;
		if (scrollLength < minimumScrollLength) {
			scrollLength = minimumScrollLength;
		}
		InterfaceAssistant.setFixedScrollMax(player, 27700, scrollLength);
		if (openInterface) {
			player.popUpSearchTerm = "";
			clearDropTableInterface(player);
			player.getPA().displayInterface(29050);
		}

	}

	public static boolean isNpcDropTableButton(Player player, int buttonId) {

		int buttonStart = 108054;
		int buttonEnd = 109094;
		if (buttonId >= buttonStart && buttonId <= buttonEnd) {
			int indexButton = (buttonId - buttonStart) / 2;

			int npcId = 0;
			if (player.npcDropTableSearchList.isEmpty()) {
				if (indexButton > NpcIdList.npcIdStoredList.size() - 1) {
					return true;
				}
				npcId = NpcIdList.npcIdStoredList.get(indexButton).npcId.get(0);
			} else {
				if (indexButton > player.npcDropTableSearchList.size() - 1) {
					return true;
				}
				npcId = player.npcDropTableSearchList.get(indexButton);
			}
			int interfaceId = 33301;
			int dropsLength = NpcDrops.NPC_DROPS[npcId].itemId.size();
			player.getPA().setTextClicked(indexButton * 2 + 27702, true);
			for (int index = 0; index < 100; index++) {
				String name = "";
				int itemId = 0;
				int minimumAmount = 0;
				String chance = "";
				int maximumAmount = 0;
				String minimumPrice = "";
				String maximumPrice = "";
				if (index < dropsLength) {
					chance = "1/" + NpcDrops.NPC_DROPS[npcId].chance.get(index) + "";
					if (chance.equals("1/1")) {
						chance = "always";
					}

					itemId = NpcDrops.NPC_DROPS[npcId].itemId.get(index);
					minimumAmount = NpcDrops.NPC_DROPS[npcId].minimumAmount.get(index);
					maximumAmount = NpcDrops.NPC_DROPS[npcId].maximumAmount.get(index);
					minimumPrice = Misc.formatRunescapeStyle(ServerConstants.getItemValue(itemId) * minimumAmount);
					if (maximumAmount != minimumAmount) {
						maximumPrice = "-" + Misc.formatRunescapeStyle(ServerConstants.getItemValue(itemId) * maximumAmount);
					}
					name = ItemAssistant.getItemName(itemId);
					int maximumNameLength = 24;
					if (name.length() > maximumNameLength) {
						name = name.substring(0, maximumNameLength);
					}
				}
				player.getPA().sendFrame126(name, interfaceId);
				interfaceId++;
				String quantityString = minimumAmount + "";
				if (minimumAmount != maximumAmount) {
					quantityString = Misc.formatRunescapeStyle(minimumAmount) + "-" + Misc.formatRunescapeStyle(maximumAmount);
				}
				if (itemId == 0) {
					quantityString = "";
				}
				player.getPA().sendFrame126(quantityString, interfaceId);
				interfaceId++;
				String priceString = minimumPrice + maximumPrice;
				String tempMaximumPrice = maximumPrice.replace("-", "");
				if (minimumPrice.equals(tempMaximumPrice)) {
					priceString = minimumPrice;
				}
				player.getPA().sendFrame126(priceString, interfaceId);
				interfaceId++;
				player.getPA().sendFrame126(chance, interfaceId);
				interfaceId++;
				player.getPA().sendFrame34(interfaceId, itemId, 0, maximumAmount);
				interfaceId++;
				interfaceId++;
			}
			int scrollLength = (int) Misc.getDoubleRoundedUp((double) dropsLength * 32);
			int minimumScrollLength = 265;
			if (scrollLength < minimumScrollLength) {
				scrollLength = minimumScrollLength;
			}
			InterfaceAssistant.setFixedScrollMax(player, 30500, scrollLength);
			InterfaceAssistant.scrollUp(player);
			return true;
		}
		return false;
	}
}
