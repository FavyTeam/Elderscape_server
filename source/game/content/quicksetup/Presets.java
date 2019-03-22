package game.content.quicksetup;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RunePouch;
import game.item.ItemAssistant;
import game.item.ItemRequirement;
import game.player.Player;
import java.util.ArrayList;

/**
 * Custom preset system.
 *
 * @author MGT Madness, created on 30-01-2017.
 */
public class Presets {

	static ArrayList<String> saveCurrentPresetTemp(Player player, boolean hasName) {
		ArrayList<String> temp = new ArrayList<String>();

		temp.add("Spellbook:" + player.spellBook);
		temp.add("Wolpertinger:" + (player.getPetId() == 6869 ? "true" : "false"));
		for (int index = 0; index < 7; index++) {
			temp.add("Skill:" + player.baseSkillLevel[index]);
		}

		for (int index = 0; index < player.playerItems.length; index++) {
			temp.add("Inventory:" + (player.playerItems[index] - 1) + ":" + player.playerItemsN[index]);
		}

		for (int index = 0; index < player.playerEquipment.length; index++) {
			temp.add("Equipment:" + player.playerEquipment[index] + ":" + player.playerEquipmentN[index]);
		}

		for (int index = 0; index < player.runePouchItemId.length; index++) {
			temp.add("Pouch:" + player.runePouchItemId[index] + ":" + player.runePouchItemAmount[index]);
		}
		if (hasName) {
			temp.add("Name:" + getCurrentPresetName(player));
		}
		return temp;
	}

	static void saveCurrentPresetFinal(Player player, int presetNumber, ArrayList<String> presetTemp) {
		if (presetNumber == 1) {
			player.preset1 = presetTemp;
		} else if (presetNumber == 2) {
			player.preset2 = presetTemp;
		} else if (presetNumber == 3) {
			player.preset3 = presetTemp;
		} else if (presetNumber == 4) {
			player.preset4 = presetTemp;
		} else if (presetNumber == 5) {
			player.preset5 = presetTemp;
		} else if (presetNumber == 6) {
			player.preset6 = presetTemp;
		} else if (presetNumber == 7) {
			player.preset7 = presetTemp;
		} else if (presetNumber == 8) {
			player.preset8 = presetTemp;
		} else if (presetNumber == 9) {
			player.preset9 = presetTemp;
		}

	}

	static void presetKit(Player player, int presetNumber) {
		ArrayList<String> presetTemp = new ArrayList<String>();
		presetTemp = getCurrentTempArray(player, presetNumber);

		player.presetIndex = presetNumber;
		if (presetTemp.isEmpty()) {
			presetTemp = saveCurrentPresetTemp(player, false);
			saveCurrentPresetFinal(player, presetNumber, presetTemp);
			player.getPA().sendMessage(":packet:namepreset");
		} else {
			player.getDH().sendDialogues(262);
		}

	}

	private static ArrayList<String> getCurrentTempArray(Player player, int presetNumber) {
		if (presetNumber == 1) {
			return player.preset1;
		} else if (presetNumber == 2) {
			return player.preset2;
		} else if (presetNumber == 3) {
			return player.preset3;
		} else if (presetNumber == 4) {
			return player.preset4;
		} else if (presetNumber == 5) {
			return player.preset5;
		} else if (presetNumber == 6) {
			return player.preset6;
		} else if (presetNumber == 7) {
			return player.preset7;
		} else if (presetNumber == 8) {
			return player.preset8;
		} else if (presetNumber == 9) {
			return player.preset9;
		}
		return null;
	}

	public static void receivePresetNameChange(Player player, String string) {
		String name = string.substring(10);
		int frameId = 22867 + ((player.presetIndex - 1) * 4);
		boolean nameExistsBefore = true;
		if (!presetNameExists(player)) {
			nameExistsBefore = false;
		}
		switch (player.presetIndex) {
			case 1:
				if (nameExistsBefore) {
					player.preset1.remove(54);
					player.preset1.add("Name:" + name);
				} else {
					player.preset1.add("Name:" + name);
				}
				break;
			case 2:
				if (nameExistsBefore) {
					player.preset2.remove(54);
					player.preset2.add("Name:" + name);
				} else {
					player.preset2.add("Name:" + name);
				}
				break;
			case 3:
				if (nameExistsBefore) {
					player.preset3.remove(54);
					player.preset3.add("Name:" + name);
				} else {
					player.preset3.add("Name:" + name);
				}
				break;
			case 4:
				if (nameExistsBefore) {
					player.preset4.remove(54);
					player.preset4.add("Name:" + name);
				} else {
					player.preset4.add("Name:" + name);
				}
				break;
			case 5:
				if (nameExistsBefore) {
					player.preset5.remove(54);
					player.preset5.add("Name:" + name);
				} else {
					player.preset5.add("Name:" + name);
				}
				break;
			case 6:
				if (nameExistsBefore) {
					player.preset6.remove(54);
					player.preset6.add("Name:" + name);
				} else {
					player.preset6.add("Name:" + name);
				}
				break;
			case 7:
				if (nameExistsBefore) {
					player.preset7.remove(54);
					player.preset7.add("Name:" + name);
				} else {
					player.preset7.add("Name:" + name);
				}
				break;
			case 8:
				if (nameExistsBefore) {
					player.preset8.remove(54);
					player.preset8.add("Name:" + name);
				} else {
					player.preset8.add("Name:" + name);
				}
				break;
			case 9:
				if (nameExistsBefore) {
					player.preset9.remove(54);
					player.preset9.add("Name:" + name);
				} else {
					player.preset9.add("Name:" + name);
				}
				break;
		}
		if (!nameExistsBefore) {
			player.getPA().sendMessage("You have created and saved your " + getCurrentPresetName(player) + " preset.");
		}
		InterfaceAssistant.closeDialogueOnly(player);
		player.getPA().sendFrame126(name, frameId);
	}

	public static void equipPreset(Player player) {

		if (!Bank.hasBankingRequirements(player, true)) {
			return;
		}
		if (player.presetIndex == 0) {
			return;
		}
		if (System.currentTimeMillis() - player.timeUsedRiskCommand < 45000) {
			player.getPA().quickChat("I have used a preset.");
		}
		ArrayList<String> presetTemp = new ArrayList<String>();
		presetTemp = getCurrentTempArray(player, player.presetIndex);
		player.timeUsedPreset = System.currentTimeMillis();
		String spellbook = presetTemp.get(0).substring(10);
		QuickSetUp.bankInventoryAndEquipment(player);
		boolean cannotBankItem = player.bankIsFullWhileUsingPreset;
		int[] skills = new int[7];
		for (int index = 2; index < 9; index++) {
			String parse[] = presetTemp.get(index).split(":");
			int level = Integer.parseInt(parse[1]);
			skills[index - 2] = level;
		}
		for (int index = 9; index < 37; index++) {
			String parse[] = presetTemp.get(index).split(":");
			int itemId = Integer.parseInt(parse[1]);
			int amount = Integer.parseInt(parse[2]);
			if (itemId <= 0) {
				continue;
			}

			if (player.playerItems[index - 9] <= 0) {
				if (Bank.hasItemInBankAndDelete(player, itemId, amount)) {
					player.playerItems[index - 9] = itemId + 1;
					player.playerItemsN[index - 9] = amount;
					player.setInventoryUpdate(true);

					if (itemId == 12791) {

						for (int b = 51; b < 54; b++) {
							String parse1[] = presetTemp.get(b).split(":");
							int itemId1 = Integer.parseInt(parse1[1]);
							int amount1 = Integer.parseInt(parse1[2]);
							player.runePouchItemId[b - 51] = itemId1;
							player.runePouchItemAmount[b - 51] = amount1;
						}
						RunePouch.updateRunePouchMainStorage(player, false);
					}
				} else {
					player.getPA().sendMessage(ServerConstants.RED_COL + ItemAssistant.getItemName(itemId) + " x" + amount + " is missing from your bank!");
				}
			} else {
				player.getPA().sendMessage(ServerConstants.RED_COL + "No space to bank your item: " + ItemAssistant.getItemName(player.playerItems[index - 9] - 1) + ".");
				cannotBankItem = true;
			}

		}


		QuickSetUp.setCombatSkills(player, "", true, skills);
		for (int index = 37; index < 51; index++) {
			String parse[] = presetTemp.get(index).split(":");
			int itemId = Integer.parseInt(parse[1]);
			int amount = Integer.parseInt(parse[2]);
			if (itemId <= 0) {
				continue;
			}
			int equipmentSlot = index - 37;
			if (player.playerEquipment[equipmentSlot] <= 0) {
				if (Bank.hasItemInBankAndDelete(player, itemId, amount)) {
					// Some players have Barrows gloves in their arrow slot because of a bug caused on 08-01-2018 for 2 hours.
					if (equipmentSlot == ServerConstants.ARROW_SLOT && itemId == 7462) {
					} else if (equipmentSlot == ServerConstants.HAND_SLOT && itemId == 7459 && skills[ServerConstants.DEFENCE] < 13) {
					} else {
						ItemRequirement.setItemRequirements(player, ItemAssistant.getItemName(itemId).toLowerCase(), itemId);
						if (!ItemRequirement.hasItemRequirement(player, equipmentSlot)) {
							if (!Bank.addItemToBank(player, itemId, amount, false)) {
								ItemAssistant.addItemReward(null, player.getPlayerName(), itemId, amount, false, -1);
							}
							continue;
						}
						ItemAssistant.replaceEquipmentSlot(player, equipmentSlot, itemId, amount, false);
					}
				} else {
					player.getPA().sendMessage(ServerConstants.RED_COL + ItemAssistant.getItemName(itemId) + " x" + amount + " is missing from your bank!");
				}
			} else {
				player.getPA().sendMessage(ServerConstants.RED_COL + "No space to bank your item: " + ItemAssistant.getItemName(player.playerEquipment[equipmentSlot]) + ".");
				cannotBankItem = true;
			}
		}
		if (ItemAssistant.is2handed(player.playerEquipment[ServerConstants.WEAPON_SLOT]) && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.SHIELD_SLOT)) {
			ItemAssistant
					.addItemReward(null, player.getPlayerName(), player.playerEquipment[ServerConstants.SHIELD_SLOT], player.playerEquipmentN[ServerConstants.SHIELD_SLOT], false,
					               -1);
			ItemAssistant.deleteEquipment(player, player.playerEquipment[ServerConstants.SHIELD_SLOT], ServerConstants.SHIELD_SLOT);
			player.getPA().sendMessage(ServerConstants.RED_COL + "Your shield has been removed for abuse.");
		}
		QuickSetUp.updateEquipment(player);
		QuickSetUp.heal(player, true, true);
		Presets.isPresetFlagged(player, cannotBankItem);
		if (!spellbook.equals(player.spellBook)) {
			QuickSetUp.setPrayerAndMagicBook(player, spellbook);
		}

		InterfaceAssistant.closeDialogueOnly(player);
		if (presetTemp.size() == 54) {
			return;
		}
		String presetName = presetTemp.get(54).substring(5);
		player.getPA().sendMessage("You have loaded the " + presetName + " preset.");
	}

	public static boolean isPresetFlagged(Player player, boolean cannotBankItem) {
		if (cannotBankItem) {
			QuickSetUp.setCombatSkills(player, "MAIN", false, null);
			player.getPA().sendMessage(ServerConstants.RED_COL + "Your stats were set to max combat to prevent abuse.");
			player.getPA().sendMessage(ServerConstants.RED_COL + "Please make room in your bank and try again.");
			return true;
		}
		return false;
	}

	public static String getCurrentPresetName(Player player) {
		ArrayList<String> presetTemp = new ArrayList<String>();
		presetTemp = getCurrentTempArray(player, player.presetIndex);
		if (presetTemp == null) {
			return "";
		}
		if (presetTemp.size() == 54) {
			return "";
		}
		String presetName = presetTemp.get(54).substring(5);
		return presetName;

	}

	public static boolean presetNameExists(Player player) {
		ArrayList<String> presetTemp = new ArrayList<String>();
		presetTemp = getCurrentTempArray(player, player.presetIndex);
		if (presetTemp == null) {
			return false;
		}
		if (presetTemp.size() < 55) {
			return false;
		}
		return true;
	}

	public static void update(Player player, boolean warning) {
		if (warning) {
			player.getDH().sendDialogues(263);
		} else {

			ArrayList<String> presetTemp = new ArrayList<String>();
			presetTemp = saveCurrentPresetTemp(player, true);
			saveCurrentPresetFinal(player, player.presetIndex, presetTemp);
			player.getPA().sendMessage("You have successfully updated the " + getCurrentPresetName(player) + " preset.");
			InterfaceAssistant.closeDialogueOnly(player);
		}
	}

	public static void Rename(Player player) {
		player.getPA().sendMessage(":packet:namepreset");

	}

	public static void openPresetInterface(Player player) {
		for (int index = 1; index < 10; index++) {
			player.presetIndex = index;
			int frameId = 22867 + ((player.presetIndex - 1) * 4);
			if (!Presets.presetNameExists(player)) {
				player.getPA().sendFrame126("Empty preset", frameId);
				continue;
			}
			player.getPA().sendFrame126(Presets.getCurrentPresetName(player), frameId);
		}
		player.presetIndex = 0;
	}

}
