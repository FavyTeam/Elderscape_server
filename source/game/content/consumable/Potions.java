package game.content.consumable;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Poison;
import game.content.combat.Venom;
import game.content.consumable.Food.FoodToEat;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.TradeAndDuel;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.skilling.summoning.Summoning;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * @author Sanity
 */

public class Potions {

	public static void handlePotion(Player player, int itemId, int slot) {
		if (player.duelRule[TradeAndDuel.NO_DRINK]) {
			player.playerAssistant.sendMessage("You may not drink potions in this duel.");
			return;
		}
		if (player.getDead()) {
			player.playerAssistant.sendMessage("You are unable to eat whilst dead.");
			return;
		}
		if (player.getTransformed() != 0) {
			return;
		}
		if (player.getHeight() == 20 && player.tournamentTarget == -1) {
			player.getPA().sendMessage("Use the box of health instead.");
			return;
		}
		long delay = 1700;
		if (System.currentTimeMillis() - player.potDelay < delay) {
			return;
		}
		if (System.currentTimeMillis() - player.karambwanDelay < 1700) {
			return;
		}
		if (System.currentTimeMillis() - player.pizzaDelayOther < 1700) {
			return;
		}
		player.showPotionMessage = true;
		String item = ItemAssistant.getItemName(itemId);
		if (GameType.isOsrs()) {
			switch (itemId) {
				// Battlemage potion
				case 22449:
				case 22452:
				case 22455:
					drinkStatPotion(player, itemId, itemId + 3, slot, ServerConstants.DEFENCE, true);
					drinkMagicPotion(player, itemId, -1, slot, ServerConstants.MAGIC, false);
					break;
				case 22458:
					drinkStatPotion(player, itemId, 229, slot, ServerConstants.DEFENCE, true);
					drinkMagicPotion(player, itemId, -1, slot, ServerConstants.MAGIC, false);
					break;

				// Bastion potion
				case 22461:
				case 22464:
				case 22467:
					drinkStatPotion(player, itemId, itemId + 3, slot, ServerConstants.DEFENCE, true);
					drinkStatPotion(player, itemId, -1, slot, ServerConstants.RANGED, false);
					break;
				case 22470:
					drinkStatPotion(player, itemId, 229, slot, ServerConstants.DEFENCE, true);
					drinkStatPotion(player, itemId, -1, slot, ServerConstants.RANGED, false);
					break;
			}
		}
		switch (itemId) {

			case 4417:
			case 4419:
			case 4421:
				drinkGuthixRest(player, itemId, itemId + 2, slot);
				break;
			case 4423:
				drinkGuthixRest(player, itemId, player.smashVials ? -1 : 1980, slot);
				break;
			// Zamorak brew.
			case 2450:
			case 189:
			case 191:
			case 193:
				return;
			case 12905:
				drinkAntiVenom(player, itemId, itemId + 2, slot); // no immunity,
				// just cures
				// current venom
				break;
			case 12140:
				drinkSummoningPotion(player, itemId, itemId + 2, slot);
				break;
			case 12142:
				drinkSummoningPotion(player, itemId, itemId + 2, slot);
				break;
			case 12144:
				drinkSummoningPotion(player, itemId, itemId + 2, slot);
				break;
			case 12146:
				drinkSummoningPotion(player, itemId, 229, slot);
				break;

			case 12907:
				drinkAntiVenom(player, itemId, itemId + 2, slot);
				break;
			case 12909:
				drinkAntiVenom(player, itemId, itemId + 2, slot);
				break;
			case 12911:
				drinkAntiVenom(player, itemId, 229, slot);
				break;
			case 12913: // anti-vemom + start here
				drinkAntiVenomPlus(player, itemId, itemId + 2, slot, 3); // 3 mins
				// immunity
				break;
			case 12915:
				drinkAntiVenomPlus(player, itemId, itemId + 2, slot, 3);
				break;
			case 12917:
				drinkAntiVenomPlus(player, itemId, itemId + 2, slot, 3);
				break;
			case 12919:
				drinkAntiVenomPlus(player, itemId, 229, slot, 3);
				break;

			case 12695:
				drinkStatPotion(player, itemId, 12697, slot, 0, true);
				drinkStatPotion(player, itemId, 0, slot, 1, true);
				drinkStatPotion(player, itemId, 0, slot, 2, true);
				break;

			case 12697:
				drinkStatPotion(player, itemId, 12699, slot, 0, true);
				drinkStatPotion(player, itemId, 0, slot, 1, true);
				drinkStatPotion(player, itemId, 0, slot, 2, true);
				break;

			case 12699:
				drinkStatPotion(player, itemId, 12701, slot, 0, true);
				drinkStatPotion(player, itemId, 0, slot, 1, true);
				drinkStatPotion(player, itemId, 0, slot, 2, true);
				break;

			case 12701:
				drinkStatPotion(player, itemId, 229, slot, 0, true);
				drinkStatPotion(player, itemId, 0, slot, 1, true);
				drinkStatPotion(player, itemId, 0, slot, 2, true);
				break;

			case 6685:
				// brews
				doTheBrew(player, itemId, 6687, slot);
				break;
			case 6687:
				doTheBrew(player, itemId, 6689, slot);
				break;
			case 6689:
				doTheBrew(player, itemId, 6691, slot);
				break;
			case 6691:
				doTheBrew(player, itemId, 229, slot);
				break;

			case 3008:
				energyPotion(player, itemId, 3010, slot, 10);
				break;
			case 3010:
				energyPotion(player, itemId, 3012, slot, 10);
				break;
			case 3012:
				energyPotion(player, itemId, 3014, slot, 10);
				break;
			case 3014:
				energyPotion(player, itemId, 229, slot, 10);
				break;

			case 12625:
				drinkStaminaPotion(player, itemId + 2, slot, 2);
				break;
			case 12627:
				drinkStaminaPotion(player, itemId + 2, slot, 2);
				break;
			case 12629:
				drinkStaminaPotion(player, itemId + 2, slot, 2);
				break;
			case 12631:
				drinkStaminaPotion(player, 229, slot, 2);
				break;

			case 2436:
				drinkStatPotion(player, itemId, 145, slot, 0, true); // Super
				// attack.
				break;
			case 145:
				drinkStatPotion(player, itemId, 147, slot, 0, true);
				break;
			case 147:
				drinkStatPotion(player, itemId, 149, slot, 0, true);
				break;
			case 149:
				drinkStatPotion(player, itemId, 229, slot, 0, true);
				break;
			case 2440:
				drinkStatPotion(player, itemId, 157, slot, 2, true); // sup str
				break;
			case 157:
				drinkStatPotion(player, itemId, 159, slot, 2, true);
				break;
			case 159:
				drinkStatPotion(player, itemId, 161, slot, 2, true);
				break;
			case 161:
				drinkStatPotion(player, itemId, 229, slot, 2, true);
				break;
			case 2444:
				drinkStatPotion(player, itemId, 169, slot, 4, false); // range pot
				break;
			case 169:
				drinkStatPotion(player, itemId, 171, slot, 4, false);
				break;
			case 171:
				drinkStatPotion(player, itemId, 173, slot, 4, false);
				break;
			case 173:
				drinkStatPotion(player, itemId, 229, slot, 4, false);
				break;
			case 2432:
				drinkStatPotion(player, itemId, 133, slot, 1, false); // def pot
				break;
			case 133:
				drinkStatPotion(player, itemId, 135, slot, 1, false);
				break;
			case 135:
				drinkStatPotion(player, itemId, 137, slot, 1, false);
				break;
			case 137:
				drinkStatPotion(player, itemId, 229, slot, 1, false);
				break;
			case 113:
				drinkStatPotion(player, itemId, 115, slot, 2, false); // str pot
				break;
			case 115:
				drinkStatPotion(player, itemId, 117, slot, 2, false);
				break;
			case 117:
				drinkStatPotion(player, itemId, 119, slot, 2, false);
				break;
			case 119:
				drinkStatPotion(player, itemId, 229, slot, 2, false);
				break;
			case 2428:
				drinkStatPotion(player, itemId, 121, slot, 0, false); // attack pot
				break;
			case 121:
				drinkStatPotion(player, itemId, 123, slot, 0, false);
				break;
			case 123:
				drinkStatPotion(player, itemId, 125, slot, 0, false);
				break;
			case 125:
				drinkStatPotion(player, itemId, 229, slot, 0, false);
				break;
			case 2442:
				drinkStatPotion(player, itemId, 163, slot, 1, true); // super def
				// pot
				break;
			case 163:
				drinkStatPotion(player, itemId, 165, slot, 1, true);
				break;
			case 165:
				drinkStatPotion(player, itemId, 167, slot, 1, true);
				break;
			case 167:
				drinkStatPotion(player, itemId, 229, slot, 1, true);
				break;
			case 10925:
				drinkSanfew(player, itemId, itemId + 2, slot);
				break;
			case 10927:
				drinkSanfew(player, itemId, itemId + 2, slot);
				break;
			case 10929:
				drinkSanfew(player, itemId, itemId + 2, slot);
				break;
			case 10931:
				drinkSanfew(player, itemId, 229, slot);
				break;
			case 3024:
				drinkSuperRestore(player, itemId, 3026, slot);
				break;
			case 3026:
				drinkSuperRestore(player, itemId, 3028, slot);
				break;
			case 3028:
				drinkSuperRestore(player, itemId, 3030, slot);
				break;
			case 3030:
				drinkSuperRestore(player, itemId, 229, slot);
				break;
			case 2434:
				drinkPrayerPotion(player, itemId, 139, slot, false);
				break;
			case 139:
				drinkPrayerPotion(player, itemId, 141, slot, false);
				break;
			case 141:
				drinkPrayerPotion(player, itemId, 143, slot, false);
				break;
			case 143:
				drinkPrayerPotion(player, itemId, 229, slot, false);
				break;
			case 2446:
				drinkAntiPoison(player, itemId, 175, slot, Misc.getSecondsToMilliseconds(90)); // anti
																															// poisons
				break;
			case 175:
				drinkAntiPoison(player, itemId, 177, slot, Misc.getSecondsToMilliseconds(90));
				break;
			case 177:
				drinkAntiPoison(player, itemId, 179, slot, Misc.getSecondsToMilliseconds(90));
				break;
			case 179:
				drinkAntiPoison(player, itemId, 229, slot, Misc.getSecondsToMilliseconds(90));
				break;
			case 2448:
				drinkAntiPoison(player, itemId, 181, slot, Misc.getMinutesToMilliseconds(6)); // anti
																														// poisons
				break;
			case 181:
				drinkAntiPoison(player, itemId, 183, slot, Misc.getMinutesToMilliseconds(6));
				break;
			case 183:
				drinkAntiPoison(player, itemId, 185, slot, Misc.getMinutesToMilliseconds(6));
				break;
			case 185:
				drinkAntiPoison(player, itemId, 229, slot, Misc.getMinutesToMilliseconds(6));
				break;
			case 2452:
			case 2454:
			case 2456:
				drinkAntiFirePotion(player, itemId + 2, slot, 6);
				break;
			case 2458:
				drinkAntiFirePotion(player, 229, slot, 6);
				break;

			case 11951:
			case 11953:
			case 11955:
				drinkAntiFirePotion(player, itemId + 2, slot, 12);
				break;
			case 11957:
				drinkAntiFirePotion(player, 229, slot, 12);
				break;
			case 15320:
			case 15321:
			case 15322:
				player.extremeMagic = true;
				drinkExtremePotion(player, itemId, itemId + 1, slot, ServerConstants.MAGIC, false);
				break;
			case 15323:
				player.extremeMagic = true;
				drinkExtremePotion(player, itemId, 229, slot, ServerConstants.MAGIC, false);
				break;
			case 15312:
				// Extreme Strength
				drinkExtremePotion(player, itemId, 15313, slot, ServerConstants.STRENGTH, false);
				break;
			case 15313:
				// Extreme Strength
				drinkExtremePotion(player, itemId, 15314, slot, ServerConstants.STRENGTH, false);
				break;
			case 15314:
				// Extreme Strength
				drinkExtremePotion(player, itemId, 15315, slot, ServerConstants.STRENGTH, false);
				break;
			case 15315:
				// Extreme Strength
				drinkExtremePotion(player, itemId, 229, slot, ServerConstants.STRENGTH, false);
				break;
			case 15308:
				// Extreme Attack
				drinkExtremePotion(player, itemId, 15309, slot, ServerConstants.ATTACK, false);
				break;
			case 15309:
				// Extreme Attack
				drinkExtremePotion(player, itemId, 15310, slot, ServerConstants.ATTACK, false);
				break;
			case 15310:
				// Extreme Attack
				drinkExtremePotion(player, itemId, 15311, slot, ServerConstants.ATTACK, false);
				break;
			case 15311:
				// Extreme Attack
				drinkExtremePotion(player, itemId, 229, slot, ServerConstants.ATTACK, false);
				break;
			case 15316:
				// Extreme Defence
				drinkExtremePotion(player, itemId, 15317, slot, ServerConstants.DEFENCE, false);
				break;
			case 15317:
				// Extreme Defence
				drinkExtremePotion(player, itemId, 15318, slot, ServerConstants.DEFENCE, false);
				break;
			case 15318:
				// Extreme Defence
				drinkExtremePotion(player, itemId, 15319, slot, ServerConstants.DEFENCE, false);
				break;
			case 15319:
				// Extreme Defence
				drinkExtremePotion(player, itemId, 229, slot, ServerConstants.DEFENCE, false);
				break;
			case 15324:
				// Extreme Ranging
				drinkExtremePotion(player, itemId, 15325, slot, ServerConstants.RANGED, false);
				break;
			case 15325:
				// Extreme Ranging
				drinkExtremePotion(player, itemId, 15326, slot, ServerConstants.RANGED, false);
				break;
			case 15326:
				// Extreme Ranging
				drinkExtremePotion(player, itemId, 15327, slot, ServerConstants.RANGED, false);
				break;
			case 15327:
				// Extreme Ranging
				drinkExtremePotion(player, itemId, 229, slot, ServerConstants.RANGED, false);
				break;
			// Magic pots
			case 3040:
				drinkMagicPotion(player, itemId, 3042, slot, ServerConstants.MAGIC, false);
				break;
			case 3042:
				drinkMagicPotion(player, itemId, 3044, slot, ServerConstants.MAGIC, false);
				break;
			case 3044:
				drinkMagicPotion(player, itemId, 3046, slot, ServerConstants.MAGIC, false);
				break;
			case 3046:
				drinkMagicPotion(player, itemId, 229, slot, ServerConstants.MAGIC, false);
				break;
			case 15332:
				doOverload(player, itemId, 15333, slot);
				break;
			case 15333:
				doOverload(player, itemId, 15334, slot);
				break;
			case 15334:
				doOverload(player, itemId, 15335, slot);
				break;
			case 15335:
				doOverload(player, itemId, 229, slot);
				break;
			case 11732:
			case 11731:
			case 11730:
				drinkOverload(player, itemId, itemId + 1, slot);
				break;
			case 11733:
				drinkOverload(player, itemId, 229, slot);
				break;

			case 15300:
			case 15301:
			case 15302:
				recoverSpecial(player, itemId, itemId + 1, slot);
				break;
			case 15303:
				recoverSpecial(player, itemId, 229, slot);
				break;

			// Super prayer.
			case 15328:
				drinkPrayerPotion(player, itemId, 15329, slot, true);
				break;
			case 15329:
				drinkPrayerPotion(player, itemId, 15330, slot, true);
				break;
			case 15330:
				drinkPrayerPotion(player, itemId, 15331, slot, true);
				break;
			case 15331:
				drinkPrayerPotion(player, itemId, 229, slot, true);
				break;

		}
		PotionFlask.drink(player, itemId, slot);
		if (player.showPotionMessage) {
			player.soundToSend = 334;
			player.soundDelayToSend = 400;
			player.potDelay = System.currentTimeMillis();
			player.cannotEatDelay = System.currentTimeMillis();
			Combat.resetPlayerAttack(player);
			player.playerAssistant.stopAllActions();
			if (System.currentTimeMillis() - player.lastPotionSip <= 1300) {
				player.setAttackTimer(player.getAttackTimer() + 1);
				player.cannotEatDelay = System.currentTimeMillis();
			}
			player.lastPotionSip = System.currentTimeMillis();
			player.potionDrank++;
			String filteredName = ItemAssistant.getItemName(itemId);
			int index = filteredName.indexOf("(");
			if (index == -1) {
				return;
			}
			player.setInventoryUpdate(true);
			if (ItemAssistant.getItemName(itemId).contains("Guthix rest")) {
				player.queuedPacketMessage.add("You drink the herbal tea.");
				player.queuedPacketMessage.add("The tea heals some health.");
			} else {
				filteredName = filteredName.substring(0, index).toLowerCase();
				boolean flask = GameType.isPreEoc() && filteredName.contains("flask");
				String potion = flask ? "flask" : "potion";
				if (filteredName.contains("potion") || flask) {
					player.queuedPacketMessage.add("You drink some of your " + filteredName + ".");
				} else {
					player.queuedPacketMessage
							.add("You drink some of your " + filteredName + " potion.");
				}
				if (item.endsWith("(6)")) {
					player.queuedPacketMessage.add("You have 5 doses of " + potion + " left.");
				} else if (item.endsWith("(5)")) {
					player.queuedPacketMessage.add("You have 4 doses of " + potion + " left.");
				} else if (item.endsWith("(4)")) {
					player.queuedPacketMessage.add("You have 3 doses of " + potion + " left.");
				} else if (item.endsWith("(3)")) {
					player.queuedPacketMessage.add("You have 2 doses of " + potion + " left.");
				} else if (item.endsWith("(2)")) {
					player.queuedPacketMessage.add("You have 1 dose of " + potion + " left.");
				} else if (item.endsWith("(1)")) {
					if (player.smashVials == true) {
						ItemAssistant.deleteItemFromInventory(player, 229, slot, 1);
						player.queuedPacketMessage.add("You quickly smash the empty vial.");
					} else {
						player.queuedPacketMessage.add("You have finished your " + potion + ".");
					}
				}
			}
		}
	}

	private static void drinkOverload(final Player player, int itemID, int result, int slot) {
		if (Area.inDangerousPvpArea(player)) {
			player.showPotionMessage = false;
			player.playerAssistant.sendMessage("You may not use this in the Wilderness.");
			return;
		}
		if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < 51) {
			player.playerAssistant.sendMessage("Your hitpoints are too low.");
			player.showPotionMessage = false;
			return;
		}
		if (player.overloadEvent) {
			player.showPotionMessage = false;
			return;
		}
		player.overloadEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getDead()) {
					container.stop();
					return;
				}
				if (player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
					container.stop();
					return;
				}
				if (Area.inDangerousPvpAreaOrClanWars(player) || Area.inDuelArenaRing(player)) {
					container.stop();
					return;
				}
				player.overloadTicks++;
				overloadBoost(player);
				player.startAnimation(3170);
				Combat.createHitsplatOnPlayerNormal(player, 10, ServerConstants.NORMAL_HITSPLAT_COLOUR,
						ServerConstants.NO_ICON);
				if (player.overloadTicks >= 5) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.overloadEvent = false;
				player.overloadTicks = 0;
			}
		}, 3);

		player.overloadReboostTicks = 1;
		overloadReBoostEvent(player);
		player.startAnimation(829);
		player.playerItems[slot] = result + 1;
	}

	public static void overloadReBoostEvent(final Player player) {
		if (player.overloadReboostTicks == 0) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (player.overloadReboostTicks == 0) {
					container.stop();
					return;
				}
				if (Zombie.inZombieMiniGameArea(player, player.getX(), player.getY())) {
					container.stop();
					return;
				}

				if (Area.inDangerousPvpAreaOrClanWars(player) || Area.inDuelArenaRing(player)) {
					player.overloadReboostTicks++;
					return;
				}

				if (player.getDead()) {
					container.stop();
					return;
				}
				if (player.duelRule[TradeAndDuel.NO_DRINK]) {
					container.stop();
					return;
				}
				if (player.overloadReboostTicks >= 21) {
					for (int index = 0; index < 7; index++) {
						if (index == ServerConstants.HITPOINTS || index == ServerConstants.PRAYER) {
							continue;
						}
						player.currentCombatSkillLevel[index] = 99;
						Skilling.updateSkillTabFrontTextMain(player, index);
					}
					player.addToHitPoints(50);
					player.getPA().sendMessage("Your overload effects have run out.");
					container.stop();
					return;
				} else {
					overloadBoost(player);
				}
				player.overloadReboostTicks++;
			}

			@Override
			public void stop() {
				player.overloadReboostTicks = 0;
			}
		}, 25);

	}

	private static void overloadBoost(Player player) {
		player.currentCombatSkillLevel[ServerConstants.STRENGTH] =
				(int) (player.getBaseStrengthLevel() * 1.27);
		player.currentCombatSkillLevel[ServerConstants.ATTACK] =
				(int) (player.getBaseAttackLevel() * 1.27);
		player.currentCombatSkillLevel[ServerConstants.DEFENCE] =
				(int) (player.getBaseDefenceLevel() * 1.27);
		player.currentCombatSkillLevel[ServerConstants.RANGED] =
				(int) (player.getBaseRangedLevel() * 1.27);
		player.currentCombatSkillLevel[ServerConstants.MAGIC] =
				(int) (player.getBaseMagicLevel() * 1.07) + 1;
		RegenerateSkill.storeBoostedTime(player, ServerConstants.STRENGTH);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.ATTACK);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.DEFENCE);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.RANGED);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);

		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.STRENGTH);
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.ATTACK);
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.DEFENCE);
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.RANGED);
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
	}

	public static void energyPotion(Player player, int itemId, int i, int slot, int percentage) {
		player.startAnimation(829);
		player.playerItems[slot] = i + 1;
		player.runEnergy += percentage;
		if (player.runEnergy > 100) {
			player.runEnergy = 100.0;
		}
		AgilityAssistant.updateRunEnergyInterface(player);

	}

	public static void drinkMagicPotion(Player player, int itemId, int replaceItem, int slot,
			int stat, boolean sup) {
		player.startAnimation(829);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);
		if (replaceItem > 0) {
			player.playerItems[slot] = replaceItem + 1;
		}
		enchanceMagic(player, stat, sup);

	}

	public static void enchanceMagic(Player player, int skillID, boolean sup) {
		player.currentCombatSkillLevel[skillID] += 4;
		if (player.currentCombatSkillLevel[skillID] > player.getBaseMagicLevel() + 4) {
			player.currentCombatSkillLevel[skillID] = player.getBaseMagicLevel() + 4;
		}
		player.skillTabMainToUpdate.add(skillID);
	}

	public static void recoverSpecial(Player player, int itemId, int replaceItem, int slot) {
		if (!Area.isWithInArea(player, 3072, 3108, 3481, 3519)) {
			player.playerAssistant.sendMessage("You may only use this in Edgeville.");
			player.showPotionMessage = false;
			return;
		}
		if (player.getSpecialAttackAmount() == 10) {
			player.playerAssistant.sendMessage("Your special attack is already full.");
			player.showPotionMessage = false;
			return;
		}
		if ((player.getSpecialAttackAmount() + 2.5) > 10) {
			player.setSpecialAttackAmount(10, false);
		} else {
			player.setSpecialAttackAmount(player.getSpecialAttackAmount() + 2.5, false);
		}
		player.startAnimation(829);
		player.queuedPacketMessage.add(
				"As you drink drink the potion, you feel your special attack slightly regenerate.");
		player.playerItems[slot] = replaceItem + 1;
		CombatInterface.updateSpecialBar(player);
	}

	public static void enchanceStat2(Player player, int skillID, boolean sup) {
		RegenerateSkill.storeBoostedTime(player, skillID);
		player.currentCombatSkillLevel[skillID] += getExtremeStat(player, skillID, sup);
		player.skillTabMainToUpdate.add(skillID);
	}

	public static int getExtremeStat(Player player, int skill, boolean sup) {
		int increaseBy = 0;
		increaseBy = (int) (player.baseSkillLevel[skill] * (player.extremeMagic ? .07 : .26)) + 1;
		if (player.currentCombatSkillLevel[skill] + increaseBy > player.baseSkillLevel[skill]
				+ increaseBy + 1) {
			return player.baseSkillLevel[skill] + increaseBy - player.currentCombatSkillLevel[skill];
		}
		return increaseBy;
	}

	public static void drinkAntiPoison(Player player, int itemId, int replaceItem, int slot,
			long immuneLength) {
		player.startAnimation(829);
		if (slot >= 0) {
			player.playerItems[slot] = replaceItem + 1;
		}
		curePoison(player, immuneLength);
	}

	public static void curePoison(Player player, long immuneLength) {
		player.poisonImmune = immuneLength;
		player.lastPoisonSip = System.currentTimeMillis();
		Poison.removePoison(player);
	}

	public static void drinkAntiVenom(Player player, int itemId, int replaceItem, int slot) {
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		Venom.removeVenom(player);
	}

	public static void drinkAntiVenomPlus(Player player, int itemId, int replaceItem, int slot,
			int immuneLength) {
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		venomImmunity(player, immuneLength);
		curePoison(player, Misc.getMinutesToMilliseconds(12));
		OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ANTI_VENOM,
				immuneLength * 60);
	}

	public static void venomImmunity(Player player, int immuneLength) {
		player.venomImmunityExpireTime = System.currentTimeMillis() + immuneLength * 60000;// 3
		// minutes
	}

	public static void drinkStatPotion(Player player, int itemId, int replaceItem, int slot,
			int stat, boolean sup) {
		player.startAnimation(829);
		RegenerateSkill.storeBoostedTime(player, stat);
		if (replaceItem > 0) {
			player.playerItems[slot] = replaceItem + 1;
		}
		player.hasOverloadBoost = false;
		enchanceStat(player, stat, sup);
	}

	private static void ringOfTheGodsImbuedEffect(Player player) {
		if (player.getBasePrayerLevel() < 86) {
			player.currentCombatSkillLevel[ServerConstants.PRAYER] += 1;
		} else {
			player.currentCombatSkillLevel[ServerConstants.PRAYER] += 2;
		}
	}

	/**
	 * Drinking the summoning potion
	 *
	 * @param player the player
	 * @param itemId the item id
	 * @param replaceItem the replacement
	 * @param slot the slot
	 */
	private static void drinkSummoningPotion(Player player, int itemId, int replaceItem, int slot) {
		/*
		 * Only available for pre eoc
		 */
		if (GameType.isPreEoc()) {
			player.startAnimation(829);
			player.playerItems[slot] = replaceItem + 1;
			/*
			 * The max level
			 */
			int lvl =
					Skilling.getLevelForExperience(player.skillExperience[ServerConstants.SUMMONING]);
			/*
			 * The percentage to add
			 */
			int toAdd = (int) (7 + (lvl * .25));
			/*
			 * Adds to skill
			 */
			player.baseSkillLevel[ServerConstants.SUMMONING] += toAdd;
			/*
			 * Fixes max
			 */
			if (player.baseSkillLevel[ServerConstants.SUMMONING] > lvl) {
				player.baseSkillLevel[ServerConstants.SUMMONING] = lvl;
			}
			/*
			 * Restores special
			 */
			player.getSummoning().setSpecial(player.getSummoning().getSpecial() + 15);
			/*
			 * Fixes special
			 */
			if (player.getSummoning().getSpecial() > 100) {
				player.getSummoning().setSpecial(100);
			}
			/*
			 * Sends display
			 */
			Summoning.sendFamiliarInterfaceText(player);
			/*
			 * Update skill
			 */
			player.skillTabMainToUpdate.add(ServerConstants.SUMMONING);
		}
	}

	public static void drinkSuperRestore(Player player, int itemId, int replaceItem, int slot) {
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		player.currentCombatSkillLevel[ServerConstants.PRAYER] += (player.getBasePrayerLevel() * .33);
		ringOfTheGodsImbuedEffect(player);
		if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > player.getBasePrayerLevel()) {
			player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
		}
		player.skillTabMainToUpdate.add(ServerConstants.PRAYER);

		if (GameType.isPreEoc()) {
			int lvl =
					Skilling.getLevelForExperience(player.skillExperience[ServerConstants.SUMMONING]);
			player.baseSkillLevel[ServerConstants.SUMMONING] += (lvl * .33);
			if (player.baseSkillLevel[ServerConstants.SUMMONING] > lvl) {
				player.baseSkillLevel[ServerConstants.SUMMONING] = lvl;
			}
			player.skillTabMainToUpdate.add(ServerConstants.SUMMONING);
		}
		restoreStats(player);
	}

	public static void drinkSanfew(Player player, int itemId, int replaceItem, int slot) {
		drinkSuperRestore(player, itemId, replaceItem, slot);
		Potions.drinkAntiPoison(player, itemId, -1, -1, Misc.getMinutesToMilliseconds(6));
		if (Venom.isReduceVenomToPoison(player)) {

		}
	}

	public static void drinkPrayerPotion(Player player, int itemId, int replaceItem, int slot,
			boolean superPrayer) {
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		player.currentCombatSkillLevel[ServerConstants.PRAYER] +=
				(player.getBasePrayerLevel() * (superPrayer ? .40 : .30));
		ringOfTheGodsImbuedEffect(player);
		if (player.currentCombatSkillLevel[ServerConstants.PRAYER] > player.getBasePrayerLevel()) {
			player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
		}
		player.skillTabMainToUpdate.add(ServerConstants.PRAYER);
	}

	public static void restoreStats(Player player) {
		for (int j = 0; j <= 6; j++) {
			if (j == 5 || j == 3) {
				continue;
			}
			if (player.currentCombatSkillLevel[j] < player.baseSkillLevel[j]) {
				double boost = 8.0 + (player.baseSkillLevel[j] * .25);
				boost = Math.ceil(boost);
				player.currentCombatSkillLevel[j] += (int) boost;
				if (player.currentCombatSkillLevel[j] > player.baseSkillLevel[j]) {
					player.currentCombatSkillLevel[j] = player.baseSkillLevel[j];
				}
				player.skillTabMainToUpdate.add(j);
			}
		}
		player.queuedSetSkillLevel[0] = ServerConstants.MAGIC;
		player.queuedSetSkillLevel[1] = player.getCurrentCombatSkillLevel(ServerConstants.MAGIC);
		player.queuedSetSkillLevel[2] = player.skillExperience[ServerConstants.MAGIC];
	}

	public static void consumePizza(Player player, int itemId, int slot) {
		if (player.getDead()) {
			player.getPA().sendMessage("You are unable to eat whilst dead.");
			return;
		}
		int delay = 800;
		if (ItemAssistant.getItemName(itemId).toLowerCase().contains("half")
				|| ItemAssistant.getItemName(itemId).toLowerCase().contains("1/2")) {
			delay = 500;
		}
		if (System.currentTimeMillis() - player.pizzaDelay < delay) {
			return;
		}
		if (System.currentTimeMillis() - player.foodDelay < delay) {
			return;
		}
		if (player.duelRule[6]) {
			player.getPA().sendMessage("You may not eat in this duel.");
			player.showPotionMessage = false;
			return;
		}
		if (System.currentTimeMillis() - player.cannotEatDelay < 1700) {
			return;
		}

		RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
		player.startAnimation(829);
		player.getPA().stopAllActions();
		Combat.resetPlayerAttack(player);
		FoodToEat food = FoodToEat.food.get(itemId);
		ItemAssistant.deleteItemFromInventory(player, itemId, slot, 1);
		ItemAssistant.addItemToInventory(player, food.replaceWith(), 1, slot, false);
		if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < player
				.getBaseHitPointsLevel()) {
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += 9;
			if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] > player
					.getBaseHitPointsLevel()) {
				player.currentCombatSkillLevel[ServerConstants.HITPOINTS] =
						player.getBaseHitPointsLevel();
			}
			player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
			RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
		}
		player.queuedPacketMessage.add("You eat the " + ItemAssistant.getItemName(itemId) + ".");
		player.soundToSend = 317;
		player.soundDelayToSend = 400;
		player.foodAte++;
		player.setInventoryUpdate(true);
		player.pizzaDelay = System.currentTimeMillis();
		player.foodDelay = System.currentTimeMillis();
		if (System.currentTimeMillis() - player.lastPotionSip <= 1300) {
			player.setAttackTimer(player.getAttackTimer() + 1);
		}
		player.lastPotionSip = System.currentTimeMillis();

	}

	public static void doTheBrew(Player player, int itemId, int replaceItem, int slot) {
		if (player.duelRule[6]) {
			player.playerAssistant.sendMessage("You may not drink in this duel.");
			player.showPotionMessage = false;
			return;
		}

		RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.DEFENCE);
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		int[] toDecrease = {ServerConstants.ATTACK, ServerConstants.STRENGTH, ServerConstants.RANGED,
				ServerConstants.MAGIC};
		for (int skillsToDecrease : toDecrease) {
			player.currentCombatSkillLevel[skillsToDecrease] -=
					getBrewStat(player, skillsToDecrease, .10);
			if (player.currentCombatSkillLevel[skillsToDecrease] <= 0) {
				player.currentCombatSkillLevel[skillsToDecrease] = 1;
			}
			if (player.currentCombatSkillLevel[skillsToDecrease] < player.baseSkillLevel[skillsToDecrease]) {
				RegenerateSkill.storeBoostedTime(player, skillsToDecrease);
			}
			player.skillTabMainToUpdate.add(skillsToDecrease);
		}
		player.currentCombatSkillLevel[ServerConstants.DEFENCE] += getBrewStat(player, 1, .20);
		if (player.getCurrentCombatSkillLevel(
				ServerConstants.DEFENCE) > (player.getBaseDefenceLevel() * 1.2 + 1)) {
			player.currentCombatSkillLevel[ServerConstants.DEFENCE] =
					(int) (player.getBaseDefenceLevel() * 1.2);
		}
		player.skillTabMainToUpdate.add(ServerConstants.DEFENCE);
		if (player.getCurrentCombatSkillLevel(
				ServerConstants.HITPOINTS) < player.getBaseHitPointsLevel() * 1.17 + 1) {
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += getBrewStat(player, 3, .15);

			if (player.getCurrentCombatSkillLevel(
					ServerConstants.HITPOINTS) > (player.getBaseHitPointsLevel() * 1.17)) {
				player.setHitPoints((int) (player.getBaseHitPointsLevel() * 1.17));
			}
		}

		player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
		player.queuedSetSkillLevel[0] = ServerConstants.MAGIC;
		player.queuedSetSkillLevel[1] = player.getCurrentCombatSkillLevel(ServerConstants.MAGIC);
		player.queuedSetSkillLevel[2] = player.skillExperience[ServerConstants.MAGIC];
	}

	public static void drinkGuthixRest(Player player, int itemId, int replaceItem, int slot) {
		if (player.duelRule[6]) {
			player.playerAssistant.sendMessage("You may not drink in this duel.");
			player.showPotionMessage = false;
			return;
		}

		RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		if (player.getCurrentCombatSkillLevel(
				ServerConstants.HITPOINTS) < player.getBaseHitPointsLevel() + 5) {
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += 5;

			if (player.getCurrentCombatSkillLevel(
					ServerConstants.HITPOINTS) > player.getBaseHitPointsLevel() + 5) {
				player.setHitPoints(player.getBaseHitPointsLevel() + 5);
			}
		}
		player.setInventoryUpdate(true);
		player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
		player.runEnergy += 10;
		if (player.runEnergy > 100) {
			player.runEnergy = 100.0;
		}
		AgilityAssistant.updateRunEnergyInterface(player);
		if (!Venom.isReduceVenomToPoison(player)) {
			if (player.poisonEvent) {
				player.poisonDamage--;
				if (player.poisonDamage == 0) {
					Potions.drinkAntiPoison(player, itemId, -1, -1, 0);
				}
			}
		}

	}

	public static void enchanceStat(Player player, int skillID, boolean sup) {
		player.currentCombatSkillLevel[skillID] += getBoostedStat(player, skillID, sup);
		if (player.currentCombatSkillLevel[skillID] > 118) {
			player.currentCombatSkillLevel[skillID] = 118;
		}
		player.skillTabMainToUpdate.add(skillID);
	}

	public static int getBrewStat(Player player, int skill, double amount) {
		return (int) (player.baseSkillLevel[skill] * amount) + 2;
	}

	public static int getBoostedStat(Player player, int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) ((skill == ServerConstants.HITPOINTS ? player.getBaseHitPointsLevel()
					: player.baseSkillLevel[skill]) * .20);
		else
			increaseBy = (int) ((skill == ServerConstants.HITPOINTS ? player.getBaseHitPointsLevel()
					: player.baseSkillLevel[skill]) * .13) + 1;
		if (player.currentCombatSkillLevel[skill] + increaseBy > (skill == ServerConstants.HITPOINTS
				? player.getBaseHitPointsLevel() : player.baseSkillLevel[skill]) + increaseBy + 1) {
			return (skill == 3 ? player.getBaseHitPointsLevel() : player.baseSkillLevel[skill])
					+ increaseBy - player.currentCombatSkillLevel[skill];
		}
		return increaseBy;
	}

	public static boolean isPotion(Player player, int itemId) {
		String name = ItemAssistant.getItemName(itemId);
		if (name.contains("glory")) {
			return false;
		}
		if (name.contains("amulet")) {
			return false;
		}
		if (GameType.isPreEoc()) {
			if (name.contains("(5)") || name.contains("(6)") || name.contains("flask")) {
				return true;
			}
		}
		return name.contains("(4)") || name.contains("(3)") || name.contains("(2)")
				|| name.contains("(1)");
	}

	public static void drinkExtremePotion(Player player, int itemId, int replaceItem, int slot,
			int stat, boolean sup) {
		if (!GameType.isPreEoc()) {
			return;
		}
		if (Area.inDangerousPvpArea(player)) {
			player.showPotionMessage = false;
			player.playerAssistant.sendMessage("You may not use this in the Wilderness.");
			return;
		}
		player.startAnimation(829);
		player.playerItems[slot] = replaceItem + 1;
		enchanceStat2(player, stat, sup);
		player.hasOverloadBoost = false;
		player.extremeMagic = false;
	}

	public static void drinkStaminaPotion(Player player, int itemId, int slot, int minutes) {
		player.setStaminaPotionTimer(minutes * 6);
		player.playerItems[slot] = itemId + 1;
		player.runEnergy += 20;
		if (player.runEnergy > 100) {
			player.runEnergy = 100.0;
		}
		AgilityAssistant.updateRunEnergyInterface(player);
		staminaPotionEvent(player);
		player.startAnimation(829);

	}

	/**
	 * The stamina potion event.
	 *
	 * @param player The associated player.
	 */
	public static void staminaPotionEvent(final Player player) {
		if (player.getStaminaPotionTimer() == 0) {
			return;
		}
		AgilityAssistant.staminaEffectOn(player);// Sends sprite to orb
		if (player.staminaEvent) {
			return;
		}
		player.staminaEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getStaminaPotionTimer() == 0) {
					container.stop();
					return;
				}
				player.setStaminaPotionTimer(player.getStaminaPotionTimer() - 1);
				if (player.getStaminaPotionTimer() == 1) {
					player.getPA().sendMessage(
							ServerConstants.GOLD_COL + "Your stamina enhancement is about to expire.");
				} else if (player.getStaminaPotionTimer() == 0) {
					container.stop();
				}

			}

			@Override
			public void stop() {
				player.staminaEvent = false;
				AgilityAssistant.staminaEffectOff(player);// Sends sprite to orb
				player.getPA()
						.sendMessage(ServerConstants.GOLD_COL + "Your stamina enhancement has expired.");
			}
		}, 17);// 10 second loop
	}

	/**
	 * Drink the anti-fire potion.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity used.
	 * @param slot The slot of the item identity used.
	 */
	public static void drinkAntiFirePotion(Player player, int itemId, int slot, int minutes) {
		player.antiFirePotion = true;
		player.setAntiFirePotionTimer(minutes * 2);
		player.playerItems[slot] = itemId + 1;
		antiFirePotionEvent(player);
		player.startAnimation(829);
		OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ANTI_FIRE,
				minutes * 60);

	}

	/**
	 * The anti-fire potion event.
	 *
	 * @param player The associated player.
	 */
	public static void antiFirePotionEvent(final Player player) {
		if (player.getAntiFirePotionTimer() == 0) {
			return;
		}
		if (player.antiFireEvent) {
			return;
		}
		if (player.getAntiFirePotionTimer() == 1) {
			player.playerAssistant.sendMessage(
					ServerConstants.PURPLE_COL + "Your anti-fire potion is about to expire.");
		}
		player.antiFireEvent = true;
		player.antiFirePotion = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.setAntiFirePotionTimer(player.getAntiFirePotionTimer() - 1);
				if (player.getAntiFirePotionTimer() == 1) {
					player.playerAssistant.sendMessage(
							ServerConstants.PURPLE_COL + "Your anti-fire potion is about to expire.");
				}
				if (player.getAntiFirePotionTimer() == 0) {
					container.stop();
				}

			}

			@Override
			public void stop() {
				player.antiFirePotion = false;
				player.antiFireEvent = false;
				player.playerAssistant
						.sendMessage(ServerConstants.PURPLE_COL + "Your anti-fire potion has expired.");
			}
		}, 50);
	}

	public static void eatKarambwan(Player player, int itemId, int slot) {
		if (player.getDead()) {
			player.playerAssistant.sendMessage("You are unable to eat whilst dead.");
			return;
		}
		if (System.currentTimeMillis() - player.karambwanDelay < 1700) {
			return;
		}
		if (System.currentTimeMillis() - player.pizzaDelayOther < 1700) {
			return;
		}
		if (player.duelRule[6]) {
			player.playerAssistant.sendMessage("You may not eat in this duel.");
			player.showPotionMessage = false;
			return;
		}
		RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
		player.startAnimation(829);
		player.playerItems[slot] = 0;
		player.playerAssistant.stopAllActions();
		Combat.resetPlayerAttack(player);
		if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < player
				.getBaseHitPointsLevel()) {
			int healAmount = 18;
			if ((player.currentCombatSkillLevel[ServerConstants.HITPOINTS] + healAmount) > player
					.getBaseHitPointsLevel()) {
				healAmount = player.getBaseHitPointsLevel()
						- player.currentCombatSkillLevel[ServerConstants.HITPOINTS];
			}
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += healAmount;
			player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
			RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);
		}
		player.queuedPacketMessage.add("You eat the " + ItemAssistant.getItemName(itemId) + ".");
		player.soundToSend = 317;
		player.soundDelayToSend = 400;
		player.foodAte++;
		player.setInventoryUpdate(true);
		player.karambwanDelay = System.currentTimeMillis();
		player.cannotEatDelay = System.currentTimeMillis();
		if (System.currentTimeMillis() - player.lastPotionSip <= 1300
				|| System.currentTimeMillis() - player.foodDelay < 1700) {
		} else {
			player.setAttackTimer(player.getAttackTimer() + 2);
		}

	}

	public static void refillVial(Player player, int itemId) {
		if (!DonatorContent.canUseFeature(player,
				DonatorTokenUse.DonatorRankSpentData.ULTIMATE_DONATOR)) {
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 229)) {
			player.getPA().sendMessage("You need an empty vial to decant some potion into.");
			return;
		}
		if (player.baseSkillLevel[ServerConstants.HERBLORE] < 75) {
			player.getDH().sendStatement("You need a Herblore level of at least 75 to do this.");
			return;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		if (ItemAssistant.hasItemAmountInInventory(player, 229, 1)) {
			startFillingCycle(player, itemId);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (Skilling.forceStopSkillingEvent(player)) {
						container.stop();
						return;
					}
					if (ItemAssistant.hasItemInInventory(player, 229)) {
						startFillingCycle(player, itemId);
					} else {
						container.stop();
					}
				}

				@Override
				public void stop() {
					Skilling.endSkillingEvent(player);
				}
			}, 5);
			return;
		}
	}

	private static void startFillingCycle(Player player, int itemId) {
		if (ItemAssistant.hasItemAmountInInventory(player, 229, 1)) {
			player.turnPlayerTo(player.getObjectX(), player.getObjectY());
			ItemAssistant.deleteItemFromInventory(player, 229, 1);
			ItemAssistant.addItem(player, itemId, 1);
			player.startAnimation(881);
			player.getPA().sendFilterableMessage("You fill the vial with some potion.");
		}
	}

	public static void doOverload(Player c, int itemId, int replaceItem, int slot) {
		int health = c.getBaseHitPointsLevel();
		int herbLevel = c.baseSkillLevel[15];
		if (health < 50) {
			c.getPA().sendMessage("I should get some more lifepoints before using this!");
			return;
		}
		if (herbLevel < 96) {
			c.getPA().sendMessage("You need 96 herblore to drink an overload");
			return;
		}
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.hasOverloadBoost = true;
		doOverloadBoost(c);
		handleOverloadTimers(c);
	}

	private static void handleOverloadTimers(Player c) {
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer b) {
				c.hasOverloadBoost = false;
			}

			@Override
			public void stop() {}
		}, 500); // 15 minutes
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer b) {
				if (c.hasOverloadBoost) {
					doOverloadBoost(c);
				} else {
					b.stop();
					int[] toNormalise = {0, 1, 2, 4, 6};
					for (int i = 0; i < toNormalise.length; i++) {
						c.currentCombatSkillLevel[toNormalise[i]] = (c.baseSkillLevel[toNormalise[i]]);
					}
				}
			}

			@Override
			public void stop() {}
		}, 25); // 15 seconds
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			int counter2 = 0;

			@Override
			public void execute(CycleEventContainer b) {
				if (counter2 < 5) {
					c.startAnimation(2383);// if loading 602 (3170)
					c.dealDamage(10);
					Combat.createHitsplatOnPlayerNormal(c, 10, ServerConstants.NORMAL_HITSPLAT_COLOUR,
							ServerConstants.NO_ICON);
					c.skillTabMainToUpdate.add(3);
					counter2++;
				} else {
					b.stop();
				}
			}

			@Override
			public void stop() {}
		}, 1); // 1 tick (600ms)
	}

	private static void doOverloadBoost(Player c) {
		int[] toIncrease = {0, 1, 2, 4, 6};
		int boost;
		for (int i = 0; i < toIncrease.length; i++) {
			boost = (int) (getOverloadBoost(c, toIncrease[i]));
			c.currentCombatSkillLevel[toIncrease[i]] += boost;
			if (c.currentCombatSkillLevel[toIncrease[i]] > (c.baseSkillLevel[toIncrease[i]]) + boost) {
				c.currentCombatSkillLevel[toIncrease[i]] = (c.baseSkillLevel[toIncrease[i]]) + boost;
			}
			c.skillTabMainToUpdate.add(toIncrease[i]);
		}
	}

	private static double getOverloadBoost(Player c, int skill) {
		double boost = 1;
		switch (skill) {
			case 0:
			case 1:
			case 2:
				boost = 5 + (c.baseSkillLevel[skill]) * .22;
				break;
			case 4:
				boost = 3 + (c.baseSkillLevel[skill]) * .22;
				break;
			case 6:
				boost = 7;
				break;
		}
		return boost;
	}
}
