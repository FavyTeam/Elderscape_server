package game.content.packet;

import core.Plugin;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.music.SoundSystem;
import game.content.shop.ShopAssistant;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.content.skilling.crafting.EnchantJewelry;
import game.content.skilling.smithing.Smithing;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;


/**
 * Magic on items
 **/
public class MagicOnItemsPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int slot = player.getInStream().readSignedWord();
		int itemId = player.getInStream().readSignedWordA();
		int junk = player.getInStream().readSignedWord();
		int spellId = player.getInStream().readSignedWordA();
		if (ItemAssistant.isNulledSlot(slot)) {
			return;
		}
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "slot: " + slot);
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
			PacketHandler.saveData(player.getPlayerName(), "junk: " + junk);
			PacketHandler.saveData(player.getPlayerName(), "spellId: " + spellId);
		}
		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}
		if (!ItemAssistant.playerHasItem(player, itemId, 1, slot)) {
			return;
		}
		if (player.doingAnAction() || player.getDoingAgility() || player.isTeleporting() || player.getDead() || player.isAnEgg || player.getTransformed() != 0) {
			return;
		}


		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}

		Combat.resetPlayerAttack(player);

		if (!player.spellBook.equals("MODERN")) {
			PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
			PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", tried to use modern book spell id: " + spellId);
			return;
		}

		player.getPA().stopAllActions();

		switch (spellId) {

			// Bones to peaches.
			case 15877:
				if (itemId != 526 && itemId != 532) {
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, 526) && !ItemAssistant.hasItemInInventory(player, 532)) {
					return;
				}

				if ((ItemAssistant.hasItemAmountInInventory(player, 555, 4) || ItemAssistant.hasItemEquipped(player, 1383)) && (
						ItemAssistant.hasItemAmountInInventory(player, 557, 4) || ItemAssistant.hasItemEquipped(player, 1385)) && ItemAssistant
								                                                                                                          .hasItemAmountInInventory(player, 561,
								                                                                                                                                    2)) {
					player.startAnimation(722);
					player.gfx0(311);
					player.getPA().sendFrame106(6);
					ItemAssistant.deleteItemFromInventory(player, 561, 2);
					if (!ItemAssistant.hasItemEquipped(player, 1383)) {
						ItemAssistant.deleteItemFromInventory(player, 555, 4);
					}
					if (!ItemAssistant.hasItemEquipped(player, 1385)) {
						ItemAssistant.deleteItemFromInventory(player, 557, 4);
					}

					for (int index = 0; index < player.playerItems.length; index++) {
						int item = player.playerItems[index] - 1;
						if (item == 526 || item == 532) {
							player.playerItems[index] = 6883 + 1;
						}
					}
					player.setInventoryUpdate(true);
				}
				break;
			case 1155: //Lvl-1 enchant sapphire
			case 1165: //Lvl-2 enchant emerald
			case 1176: //Lvl-3 enchant ruby
			case 1180: //Lvl-4 enchant diamond
			case 1187: //Lvl-5 enchant dragonstone
			case 6003: //Lvl-6 enchant onyx
				EnchantJewelry.enchantBolt(player, spellId, itemId);
				EnchantJewelry.enchantItem(player, itemId, spellId, slot);
				break;
			case 1162:
				// low alch
				if (System.currentTimeMillis() - player.alchDelay > 1000) {
					if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 21) {
						player.playerAssistant.sendMessage("You need 21 magic to cast this spell.");
						return;
					}
					if (itemId == ServerConstants.getMainCurrencyId()) {
						player.playerAssistant.sendMessage("You can't alch this.");
						break;
					}
					if (!ItemAssistant.hasItemAmountInInventory(player, 561, 1)) {
						player.playerAssistant.sendMessage("You need a nature rune to do this.");
						return;
					}
					if (!ItemAssistant.hasItemAmountInInventory(player, 554, 3) && !ItemAssistant.hasItemEquipped(player, 1387)) {
						player.playerAssistant.sendMessage("You need 3 fire runes.");
						return;
					}
					if (ItemAssistant.hasSingularUntradeableItem(player, itemId)) {
						player.singularUntradeableItemsOwned.remove(Integer.toString(itemId));
					}
					ItemAssistant.deleteItemFromInventory(player, 561, 1);
					if (!ItemAssistant.hasItemEquipped(player, 1387)) {
						ItemAssistant.deleteItemFromInventory(player, 554, 3);
					}
					int price = (ShopAssistant.getSellToShopPrice(player, itemId, false) / 3) * 2;
					ItemAssistant.addItemToInventoryOrDrop(player, 995, price);
					ItemAssistant.deleteItemFromInventory(player, itemId, slot, 1);
					player.startAnimation(712);
					player.gfx100(112);
					player.alchDelay = System.currentTimeMillis();
					player.getPA().sendFrame106(6);
					SoundSystem.sendSound(player, 224, 0);
					Skilling.addSkillExperience(player, CombatConstants.MAGIC_SPELLS[CombatConstants.LOW_ALCH_INDEX][1], 6, false);
					Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
				}
				break;

			case 1178:
				// high alch
				if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 55) {
					player.playerAssistant.sendMessage("You need 55 magic to cast this spell.");
					return;
				}
				if (System.currentTimeMillis() - player.alchDelay > 2000) {
					if (itemId == ServerConstants.getMainCurrencyId()) {
						player.playerAssistant.sendMessage("You can't alch this.");
						break;
					}
					if (!ItemAssistant.hasItemAmountInInventory(player, 561, 1)) {
						player.playerAssistant.sendMessage("You need a nature rune to do this.");
						return;
					}
					if (!ItemAssistant.hasItemAmountInInventory(player, 554, 5) && !ItemAssistant.hasItemEquipped(player, 1387)) {
						player.playerAssistant.sendMessage("You need 5 fire runes.");
						return;
					}


					if (ItemAssistant.hasSingularUntradeableItem(player, itemId)) {
						player.singularUntradeableItemsOwned.remove(Integer.toString(itemId));
					}
					ItemAssistant.deleteItemFromInventory(player, 561, 1);
					if (!ItemAssistant.hasItemEquipped(player, 1387)) {
						ItemAssistant.deleteItemFromInventory(player, 554, 5);
					}
					ItemAssistant.deleteItemFromInventory(player, itemId, slot, 1);
					ItemAssistant.addItemToInventoryOrDrop(player, 995, ShopAssistant.getSellToShopPrice(player, itemId, false));
					if (itemId == 4151) {
						Achievements.checkCompletionMultiple(player, "1126");
					}
					player.skillingStatistics[SkillingStatistics.HIGH_ALCHEMY]++;
					SoundSystem.sendSound(player, 223, 0);
					player.startAnimation(713);
					player.gfx100(113);
					player.alchDelay = System.currentTimeMillis();
					player.getPA().sendFrame106(6);
					Skilling.addSkillExperience(player, CombatConstants.MAGIC_SPELLS[CombatConstants.HIGH_ALCH_INDEX][1], 6, false);
					Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
				}
				break;
			// Superheat.
			case 1173:
				if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 43) {
					player.playerAssistant.sendMessage("You need 43 magic to cast this spell.");
					return;
				}
				if (System.currentTimeMillis() - player.alchDelay > 2000) {
					// add 438 to copper ore which is 436
					if (!ItemAssistant.hasItemAmountInInventory(player, 561, 1)) {
						player.playerAssistant.sendMessage("You need a nature rune to do this.");
						return;
					}
					if (!ItemAssistant.hasItemAmountInInventory(player, 554, 4) && !ItemAssistant.hasItemEquipped(player, 1387)) {
						player.playerAssistant.sendMessage("You need 4 fire runes.");
						return;
					}
					boolean hasOre = false;
					String name = "";
					for (int index = 0; index < Smithing.ORES_SMELT.length; index++) {
						if (itemId == Smithing.ORES_SMELT[index]) {
							hasOre = true;
							name = Smithing.ORES_SMELT_NAME[index];
							break;
						}
					}
					if (!hasOre) {
						player.getPA().sendMessage("You do not have the required ore to superheat.");
						return;
					}
					player.setActionIdUsed(5);
					Smithing.smithingButtons(player, 0, name);
					player.startAnimation(723);
					player.gfx100(148);
					player.alchDelay = System.currentTimeMillis();
					player.getPA().sendFrame106(6);
				}
				break;
			default:
				if (Plugin.execute("cast_spell_" + spellId + "_on_item_" + itemId, player, slot)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
		}

	}

}
