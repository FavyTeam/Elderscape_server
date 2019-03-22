package game.bot;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedData;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.consumable.Food;
import game.content.consumable.Potions;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.quicksetup.MeleeMain;
import game.content.quicksetup.Pure;
import game.content.quicksetup.QuickSetUp;
import game.content.quicksetup.RangedTank;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Bot content.
 *
 * @author MGT Madness, created on 21-04-2016.
 */
public class BotContent {

	public static void adjustWeaponAttackStyle(Player bot) {
		if (!bot.isCombatBot()) {
			return;
		}
		if (RangedData.isWieldingMediumRangeRangedWeapon(bot) || RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(bot)) {
			bot.setCombatStyle(ServerConstants.RAPID);
		} else {
			if (bot.getWieldedWeapon() == 4151) // Abyssal whip.
			{
				bot.setCombatStyle(ServerConstants.ACCURATE);
			} else {
				bot.setCombatStyle(ServerConstants.AGGRESSIVE);
			}
		}
	}

	public static void retreatToBank(final Player bot, boolean instant) {
		bot.botDebug.add("Here1.");
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (bot.botEarlyRetreat) {
					return;
				}
				bot.botDebug.add("Here3.");
				walkToBankArea(bot);
				regearStage1Event(bot);
				switchOffPrayerEventWhenOutsideWild(bot);
			}
		}, instant ? 1 : Misc.random(20, 45));
	}

	/**
	 * Walk to the banker area.
	 */
	public static void walkToBankArea(Player bot) {
		int x = GameType.isOsrsPvp() ? 3093 : 3075;
		Movement.playerWalkBot(bot, x + Misc.random(8), 3515 + Misc.random(3));
	}

	public static void switchOffPrayerEventWhenOutsideWild(final Player bot) {
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!Area.inDangerousPvpArea(bot)) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				bot.botDebug.add("Here4.");
				turnOffPrayers(bot);
			}
		}, 2);
	}

	private static void applyEventWhenNextToBanker(final Player bot) {
		bot.botDebug.add("Here5.");
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				bot.botDebug.add("Here6.");
				bot.setBotStatus("");

				bot.turnPlayerTo(3096, 3514);
				deleteEquipmentEvent(bot);
				stage2Event(bot);
			}
		}, 3);
	}

	private static void stage2Event(final Player bot) {
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				regearStage2Event(bot);
			}
		}, Misc.random(20, 30));
	}

	private static void deleteEquipmentEvent(final Player bot) {
		bot.botDebug.add("Here7.");
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				bot.botDebug.add("Here8.");
				for (int index = 0; index < bot.playerEquipment.length; index++) {
					bot.playerEquipment[index] = 0;
					bot.playerEquipmentN[index] = 0;
				}
				Combat.updatePlayerStance(bot);
				bot.setUpdateRequired(true);
				bot.getPA().requestUpdates();
				bot.setAppearanceUpdateRequired(true);
			}
		}, Misc.random(3, 12));
	}

	private static void runToWilderness(final Player bot, int tickDelay) {

		bot.botEarlyRetreat = false;
		if (Misc.hasPercentageChance(10) && isVengeanceBot(bot.botPkType)) {
			Combat.castVengeance(bot);
		}

		adjustWeaponAttackStyle(bot);
		bot.botDebug.add("Here9.");
		roam(bot);
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				bot.botDebug.add("Here10.");
				if (!Area.inDangerousPvpArea(bot)) {
					int[] coordinates = getPkingCoordinates(bot.botPkType);
					Movement.playerWalkBot(bot, coordinates[0] + Misc.random(10), coordinates[1] + Misc.random(6));
				}
			}
		}, tickDelay);
	}

	private static int[] getPkingCoordinates(String botPkType) {
		int[] coordinates = new int[2];
		switch (botPkType) {
			case "PURE":
			case "INITIATE":
				coordinates[0] = 3072;
				coordinates[1] = 3520;
				break;

			case "MELEE":
				coordinates[0] = 3095;
				coordinates[1] = 3528;
				break;

			case "BERSERKER":
			case "RANGED TANK":
				coordinates[0] = 3092;
				coordinates[1] = 3524;
				break;
		}
		return coordinates;
	}

	private static void adjustSmite(Player bot) {
		if (System.currentTimeMillis() - bot.botTimePrayerToggled <= 4000) {
			return;
		}
		if (bot.getBotStatus().equals("LOOTING")) {
			return;
		}
		if (bot.prayerActive[bot.botLastDamageTakenType]) {
			return;
		}
		bot.botTimePrayerToggled = System.currentTimeMillis();
		Player attacker = PlayerHandler.players[bot.getLastAttackedBy()];
		if (attacker == null) {
			return;
		}
		if (attacker.prayerActive[ServerConstants.SMITE]) {
			if (!bot.prayerActive[ServerConstants.SMITE]) {
				togglePrayer(bot, ServerConstants.SMITE, true);
			}
		} else {
			if (bot.prayerActive[ServerConstants.SMITE]) {
				togglePrayer(bot, ServerConstants.SMITE, false);
			}
		}
	}

	public static void cannotAttackPlayer(Player bot) {
		if (!bot.isCombatBot()) {
			return;
		}
		turnOffPrayers(bot);
		bot.setBotStatus("LOOTING");
		BotContent.retreatToBank(bot, true);
		BotCommunication.sendBotMessage(bot, "?", false);
		bot.botDebug.add("Enemy not in wild.");
		Combat.resetPlayerAttack(bot);
	}

	public static void togglePrayer(final Player bot, final int prayerId, final boolean turnOn) {
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (turnOn && bot.prayerActive[prayerId]) {
					return;
				}
				if (!turnOn && !bot.prayerActive[prayerId]) {
					return;
				}
				RegularPrayer.activatePrayer(bot, prayerId);
			}
		}, Misc.random(2, 5));
	}

	static int getBotInvisibleSpecialAttackWeaponMax(int weapon, int baseDefenceLevel, boolean rangedVoid) {
		int damage = 0;
		switch (weapon) {
			// Armadyl godsword.
			case 11802:
				damage = 60;
				break;

			// Dragon dagger.
			case 1215:
				damage = 52;
				break;

			// Bandos godsword.
			case 11804:
				damage = 50;
				break;

			// Statius's warhammer
			case 13576:
				damage = 50;
				break;

			// Dragon mace.
			case 1434:
				damage = 45;
				break;

			// Dragon long.
			case 1305:
				damage = 42;
				break;

			// Rune crossbow.
			case 9185:
				damage = 45;
				break;

			// Dragon claws.
			case 20784:
				damage = 60;
				break;

			// Dark bow.
			case 11235:
			case 12765:
			case 12766:
			case 12767:
			case 12768:
			case 15241: // Hand cannon.
				damage = rangedVoid ? 60 : 50;
				break;
		}
		if (baseDefenceLevel <= 20) {
			damage *= 0.92;
		}
		return damage;
	}

	/**
	 * Used so the bot knows when to eat.
	 *
	 * @param player
	 * @return
	 */
	static int getPlayerMaxHitToEatAt(Player player) {
		if (player == null) {
			return 40;
		}
		boolean hasSpecial = player.getSpecialAttackAmount() >= getWeaponSpecialAttackRequiredAmount(player.getWieldedWeapon());
		int max = 0;
		if (RangedData.isWieldingMediumRangeRangedWeapon(player) || RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(player)) {
			max = RangedFormula.getRangedMaximumDamage(player);
		} else {
			max = MeleeFormula.getMaximumMeleeDamage(player);
		}
		if (hasSpecial) {
			switch (player.getWieldedWeapon()) {

				// Special attack multipliers are lower than usual.

				// Armadyl godsword.
				case 11802:
					max *= 1.25;
					break;

				// Dragon dagger.
				case 1215:
					max *= 1.7;
					break;

				// Bandos godsword.
				case 11804:
					max *= 1.05;
					break;

				// Statius's warhammer
				case 13576:
					max *= 1.04;
					break;

				// Dragon mace.
				case 1434:
					max *= 1.3;
					break;

				// Dragon long.
				case 1305:
					max *= 1.2;
					break;

				// Rune crossbow.
				case 9185:
					max *= RangedData.wearingFullVoidRanged(player) ? 1.3 : 1.2;
					break;

				// Dragon claws.
				case 20784:
					max *= 1.7;
					break;

				// Granite maul.
				case 4153:
					max *= 1.7;
					break;

				// Dark bow.
				case 11235:
				case 12765:
				case 12766:
				case 12767:
				case 12768:
				case 15241: // Hand cannon.
					max *= RangedData.wearingFullVoidRanged(player) ? 1.4 : 1.3;
					break;
			}
		}
		max *= 0.95;
		if (max < 39) {
			max = 39;
		}
		return max;
	}

	private static double getWeaponSpecialAttackRequiredAmount(int weapon) {
		if (weapon == 4153) {
			return 5.0;
		} else if (weapon == 11235) {
			return 5.5;
		} else if (weapon == 15241) {
			return 5.0;
		}
		for (int index = 0; index < MeleeMain.allSpecialAttackWeapons.length; index++) {
			if (weapon == MeleeMain.allSpecialAttackWeapons[index][0]) {
				return MeleeMain.allSpecialAttackWeapons[index][1] / 10.0;
			}
		}

		return 0.0;
	}

	private static boolean hasItemNameInInventory(Player bot, String name) {
		name = name.toLowerCase();
		for (int index = 0; index < bot.playerItems.length; index++) {
			int item = bot.playerItems[index] - 1;
			if (item > 0) {
				if (ItemDefinition.getDefinitions()[item].name.toLowerCase().contains(name)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int[] getItemIdAndSlot(Player bot, String name) {
		int[] result = new int[2];
		name = name.toLowerCase();
		for (int index = 0; index < bot.playerItems.length; index++) {
			int item = bot.playerItems[index] - 1;
			if (item > 0) {
				if (ItemDefinition.getDefinitions()[item] == null) {
					continue;
				}
				if (ItemDefinition.getDefinitions()[item].name.toLowerCase().contains(name)) {
					result[0] = item;
					result[1] = index;
					return result;
				}
			}
		}
		return result;
	}

	private static void potAndPray(Player bot) {
		int[] result;
		adjustSmite(bot);
		if (bot.getCurrentCombatSkillLevel(ServerConstants.STRENGTH) <= 115) {
			if (hasItemNameInInventory(bot, "STRENGTH")) {
				result = getItemIdAndSlot(bot, "STRENGTH");
				Potions.handlePotion(bot, result[0], result[1]);
			}
		}
		if (bot.getBaseAttackLevel() >= 75) {
			int attackToDrinkAt = 115;
			if (bot.getBaseAttackLevel() == 75) {
				attackToDrinkAt = 87;
			}
			if (bot.getCurrentCombatSkillLevel(ServerConstants.ATTACK) <= attackToDrinkAt) {
				if (hasItemNameInInventory(bot, "ATTACK")) {
					result = getItemIdAndSlot(bot, "ATTACK");
					Potions.handlePotion(bot, result[0], result[1]);
				}
			}
		}

		if (bot.getCurrentCombatSkillLevel(ServerConstants.RANGED) <= 109) {
			if (hasItemNameInInventory(bot, "RANGING")) {
				result = getItemIdAndSlot(bot, "RANGING");
				Potions.handlePotion(bot, result[0], result[1]);
			}
		}


		if (bot.getBaseDefenceLevel() >= 45) {
			int defenceToDrinkAt = 115;
			if (bot.getBaseDefenceLevel() == 45) {
				defenceToDrinkAt = 52;
			} else if (bot.getBaseDefenceLevel() == 85) {
				defenceToDrinkAt = 99;
			}
			if (bot.getCurrentCombatSkillLevel(ServerConstants.DEFENCE) <= defenceToDrinkAt) {
				if (hasItemNameInInventory(bot, "DEFENCE")) {
					result = getItemIdAndSlot(bot, "DEFENCE");
					Potions.handlePotion(bot, result[0], result[1]);
				}
			}
		}
		if (bot.getCurrentCombatSkillLevel(ServerConstants.PRAYER) <= 25 && !bot.isBotActionApplied()) {
			if (hasItemNameInInventory(bot, "PRAYER")) {
				result = getItemIdAndSlot(bot, "PRAYER");
				Potions.handlePotion(bot, result[0], result[1]);
			}
		}

		if (!bot.prayerActive[getMainPrayer(bot.botPkType, bot)]) {
			togglePrayer(bot, getMainPrayer(bot.botPkType, bot), true);
		}
	}

	private static int getMainPrayer(String botType, Player bot) {
		if (botType.equals("PURE")) {
			if (bot.botPureWeaponSet == Pure.weaponSetBot.length - 1) {
				return ServerConstants.EAGLE_EYE;
			} else {
				return ServerConstants.ULTIMATE_STRENGTH;
			}
		}
		switch (botType) {
			case "MELEE":
				return ServerConstants.PIETY;
			case "INITIATE":
			case "BERSERKER":
				return ServerConstants.ULTIMATE_STRENGTH;
			case "RANGED TANK":
				return ServerConstants.EAGLE_EYE;
		}
		return 0;
	}

	static void combatEvent(final Player bot) {
		bot.botDebug.add("Here12.");
		bot.botEatingEventTimer = 6;
		if (bot.botEatingEvent) {
			return;
		}
		bot.botDebug.add("Here13.");
		bot.botEatingEvent = true;
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				boolean attackerExists = false;
				Player attacker = PlayerHandler.players[bot.getLastAttackedBy()];
				if (attacker != null) {
					attackerExists = true;
				}
				bot.botDebug.add("Here13.5");
				if (bot.botEatingEventTimer == 0 || bot.getDead() || !bot.getBotStatus().equals("IN COMBAT") || !bot.botEatingEvent) {
					bot.botDebug.add("Here14.");
					container.stop();
					return;
				} else {
					bot.botEatingEventTimer--;
				}

				bot.botReAttack = true;
				if (System.currentTimeMillis() - bot.botTimeSwitchedItem < 1190) {
					eat(bot, attacker, attackerExists, false);
					return;
				}

				if (!bot.botUsedSpecialAttack) {
					bot.botDebug.add("Here15.");
					potAndPray(bot);
					specialAttack(bot, attacker, attackerExists);
					wearBackPrimaryWeapon(bot, bot.botWornItemThisTick);
					if (!bot.isBotActionApplied() && isVengeanceBot(bot.botPkType)) {
						bot.botDebug.add("Here34.");
						Combat.castVengeance(bot);
					}

				}
				eat(bot, attacker, attackerExists, false);
				beingLured(bot);
				setCombatFollowing(bot, attacker);
				if (attacker != null) {
					if (attacker.prayerActive[ServerConstants.PROTECT_FROM_MELEE] || attacker.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]
					    || attacker.prayerActive[ServerConstants.PROTECT_FROM_RANGED]) {
						if (!bot.prayerActive[bot.botLastDamageTakenType]) {
							togglePrayer(bot, bot.botLastDamageTakenType, true);
							bot.botEnemyPrayedTime = System.currentTimeMillis();
						}
						if (System.currentTimeMillis() - bot.botEnemyPrayedTime >= 8000) {
							bot.setBotStatus("LOOTING");
							retreatToBank(bot, false);

							BotCommunication.sendBotMessage(bot, "?", false);
							walkToBankArea(bot);
							bot.botDebug.add("8 seconds since enemy prayed.");
							container.stop();
							return;
						}
						// If bot has more inventory spaces than attacker, then run.
						if (ItemAssistant.getFreeInventorySlots(bot) > ItemAssistant.getFreeInventorySlots(attacker)) {
							bot.setBotStatus("LOOTING");
							retreatToBank(bot, false);

							BotCommunication.sendBotMessage(bot, "?", false);
							walkToBankArea(bot);
							bot.botDebug.add("Enemy prayed melee.");
							container.stop();
							return;
						}
					}
				}
			}

			@Override
			public void stop() {
				Combat.resetPlayerAttack(bot);
				bot.botDebug.add("Here37.");
				bot.setUsingSpecialAttack(false);
				bot.botUsedSpecialAttack = false;
				bot.botEatingEvent = false;
				if (!bot.getBotStatus().equals("LOOTING")) {
					bot.botDebug.add("Here38.");
					bot.setBotStatus("FIND FIGHT");
					runToWilderness(bot, Misc.random(1, 3));
				}
			}
		}, 2);

	}

	/**
	 * Prevent bot from being lured past 5 wilderness.
	 */
	private static void beingLured(Player bot) {
		if (bot.getY() < 3553) {
			return;
		}
		if (!bot.prayerActive[bot.botLastDamageTakenType]) {
			togglePrayer(bot, bot.botLastDamageTakenType, true);
		}
		bot.setBotStatus("LOOTING");
		BotContent.retreatToBank(bot, true);
		BotCommunication.sendBotMessage(bot, "?", false);
		bot.botDebug.add("Enemy not in wild.");
		Combat.resetPlayerAttack(bot);
	}

	public static void addBotDebug(Player bot, String debugMessage) {
		if (!bot.isCombatBot()) {
			return;
		}
		bot.botDebug.add(debugMessage);
	}

	private static void eat(Player bot, Player attacker, boolean attackerExists, boolean forceEat) {
		if (attacker == null) {
			return;
		}
		bot.botDebug.add("Here29.5: " + forceEat + ", " + bot.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS));
		if (!bot.botUsedSpecialAttack || bot.botUsedSpecialAttack && Misc.hasPercentageChance(50) || forceEat) {
			bot.botDebug.add("Here29.6");
			if (attackerExists) {
				int eatAt = (int) (getPlayerMaxHitToEatAt(attacker) * getEatPercentage(bot.getBaseDefenceLevel()));

				// If bot is wearing void knight top.
				if (ItemAssistant.hasItemEquippedSlot(bot, 8839, ServerConstants.BODY_SLOT)) {
					eatAt += 7;
				}
				if (attacker.getVengeance()) {
					eatAt += 15;
				}
				bot.botDebug.add("Here29: " + attacker.getPlayerName() + ", " + eatAt);
				if (!bot.isBotActionApplied() && bot.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) <= eatAt || forceEat) {
					bot.botDebug.add("Here30.");
					if (ItemAssistant.hasItemInInventory(bot, 385)) {
						bot.botDebug.add("Here31.");
						bot.setBotActionApplied(true);
						bot.botTimeSwitchedItem = System.currentTimeMillis();
						Food.eat(bot, 385, ItemAssistant.getItemSlot(bot, 385));
						if (attacker.getBaseDefenceLevel() >= 30 && bot.getBaseDefenceLevel() <= 20) {
							if (ItemAssistant.getItemAmount(bot, 385) <= 3) {
								if (!bot.prayerActive[bot.botLastDamageTakenType]) {
									togglePrayer(bot, bot.botLastDamageTakenType, true);
								}
								bot.setBotStatus("LOOTING");
								retreatToBank(bot, false);
								BotCommunication.sendBotMessage(bot, "DEFENCE NOOB", false);
								walkToBankArea(bot);
								bot.botDebug.add("2 food or less and enemy is defence noob.");
							}
						}
					}
				}
			}
		}

	}

	private static void setCombatFollowing(Player bot, Player attacker) {
		if (RangedData.isWieldingMediumRangeRangedWeapon(bot) || RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(bot)) {
			bot.setUsingRanged(true);
			bot.setMeleeFollow(false);
		} else {
			bot.setUsingRanged(false);
			bot.setMeleeFollow(true);
			bot.setUsingMediumRangeRangedWeapon(false);
			bot.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(false);
		}
		if (System.currentTimeMillis() - bot.botTimeSwitchedItem < 1190) {
			return;
		}
		if (attacker != null && bot.botReAttack && !bot.botWornItemThisTick) {
			bot.setPlayerIdAttacking(attacker.getPlayerId());
			bot.setPlayerIdToFollow(attacker.getPlayerId());
		}

	}

	private static void specialAttack(Player bot, Player attacker, boolean attackerExists) {
		bot.botDebug.add("Here16.");
		if (bot.botUsedSpecialAttack) {
			return;
		}
		bot.botDebug.add("Here16.5");
		if (bot.isBotActionApplied()) {
			return;
		}
		bot.botDebug.add("Here16.6");
		if (!attackerExists) {
			return;
		}
		bot.botDebug.add("Here16.7");
		if (bot.getSpecialAttackAmount() < getWeaponSpecialAttackRequiredAmount(bot.botSpecialAttackWeapon)) {
			return;
		}
		bot.botDebug.add("Here16.8");
		int botSpecAt = (int) (getBotInvisibleSpecialAttackWeaponMax(bot.botSpecialAttackWeapon, bot.getBaseDefenceLevel(), RangedData.wearingFullVoidRanged(bot) ? true : false)
		                       * getEatPercentage(attacker.getBaseDefenceLevel()));
		if (attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > botSpecAt) {
			return;
		}
		if (bot.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS)) {
			return;
		}

		bot.botDebug.add("Here17.");
		boolean activateSpecial = true;
		if (!bot.botWearingPrimaryWeapon) {
			bot.botWornItemThisTick = true;
			// So the bot does not wear primary weapon then back to special repeatedly.
		}
		if (!ItemAssistant.hasItemEquippedSlot(bot, bot.botSpecialAttackWeapon, ServerConstants.WEAPON_SLOT)) {
			bot.botDebug.add("Here18: " + bot.botShield + ", " + bot.botSpecialAttackWeapon);
			if (bot.botShield > 0 && ItemAssistant.is2handed(bot.botSpecialAttackWeapon) && ItemAssistant.getFreeInventorySlots(bot) >= 1 || !ItemAssistant.is2handed(
					bot.botSpecialAttackWeapon) || bot.botShield <= 0) {
				bot.botDebug.add("Here18.5");

				bot.botDebug.add("Here19.");
				if (bot.botSpecialAttackWeapon > 0) {
					bot.botDebug.add("Here19.5");
					int slot = ItemAssistant.getItemSlot(bot, bot.botSpecialAttackWeapon);
					if (slot >= 0) {
						bot.botTimeSwitchedItem = System.currentTimeMillis();
						bot.botDebug.add("Here20.");
						bot.botWornItemThisTick = true;
						ItemAssistant.wearItem(bot, bot.botSpecialAttackWeapon, slot);
						bot.faceUpdate(attacker.getPlayerId() + 32768);
						bot.botWearingPrimaryWeapon = false;
						bot.botDebug.add("Here20.5");
						bot.setBotActionApplied(true);
						bot.botUsedSpecialAttack = true;
						if (bot.botSpecialAttackWeaponShield > 0) {
							int slot1 = ItemAssistant.getItemSlot(bot, bot.botSpecialAttackWeaponShield);
							if (slot1 >= 0) {
								bot.botDebug.add("Here21.");
								ItemAssistant.wearItem(bot, bot.botSpecialAttackWeaponShield, slot1);
							}
						}
						if (bot.botArrowSpecial > 0) {
							int slot2 = ItemAssistant.getItemSlot(bot, bot.botArrowSpecial);
							if (slot2 >= 0) {
								bot.botDebug.add("Here20.6");
								ItemAssistant.wearItem(bot, bot.botArrowSpecial, slot2);
							}
						}
						bot.botDebug.add("Debug worn current items: " + bot.playerEquipment[ServerConstants.WEAPON_SLOT] + ", " + bot.playerEquipment[ServerConstants.SHIELD_SLOT]);
					} else {
						bot.botDebug.add("Here22.");
						bot.botDebug.add("WARNING: slot: " + slot + ", name: " + bot.getPlayerName() + ", " + bot.botSpecialAttackWeapon);
						activateSpecial = false;
					}
				}
			} else {
				bot.botDebug.add("Here23.");
				activateSpecial = false;
			}
		}
		if (activateSpecial) {
			bot.botDebug.add("Here24.");
			bot.setBotActionApplied(true);
			if (bot.getWieldedWeapon() != 9185) {
				bot.setUsingSpecialAttack(true);
			}
			bot.botUsedSpecialAttack = true;
			bot.botTimeSwitchedItem = System.currentTimeMillis();
		}

	}

	protected static boolean isVengeanceBot(String botPkType) {
		switch (botPkType) {
			case "MELEE":
			case "BERSERKER":
			case "RANGED TANK":
				return true;
		}
		return false;
	}

	private static double getEatPercentage(int baseDefenceLevel) {

		if (baseDefenceLevel <= 20) {
			return 1.1;
		} else if (baseDefenceLevel <= 60) {
			return 1.05;
		} else if (baseDefenceLevel <= 99) {
			return 1;
		}


		return 0.99;
	}

	private static void wearBackPrimaryWeapon(Player bot, boolean wornItemThisTick) {
		if (bot.botUsedSpecialAttack) {
			return;
		}
		bot.botDebug.add("Here26.");
		if (!bot.botWearingPrimaryWeapon) {
			bot.botDebug.add("Here26.5");
			if (wornItemThisTick) {
				return;
			}
			bot.botDebug.add("Here26.6");
			if (!ItemAssistant.hasItemEquippedSlot(bot, bot.botPrimaryWeapon, ServerConstants.WEAPON_SLOT)) {
				int slot = ItemAssistant.getItemSlot(bot, bot.botPrimaryWeapon);
				if (slot >= 0) {
					bot.botDebug.add("Here27: " + bot.botPrimaryWeapon + ", " + ItemAssistant.getItemSlot(bot, bot.botPrimaryWeapon) + ", " + ItemAssistant.getItemName(
							bot.playerItems[slot] - 1));
				}
				if (bot.botArrowSpecial > 0) {
					int slot2 = ItemAssistant.getItemSlot(bot, 9244);
					if (slot2 >= 0) {
						// Wear dragon bolts e.
						ItemAssistant.wearItem(bot, 9244, slot2);
					}
				}
				ItemAssistant.wearItem(bot, bot.botPrimaryWeapon, ItemAssistant.getItemSlot(bot, bot.botPrimaryWeapon));
				bot.setBotActionApplied(true);
				bot.botWearingPrimaryWeapon = true;
				if (bot.botArrowPrimary > 0) {
					bot.botDebug.add("Here27.57: " + bot.botArrowPrimary);
					slot = ItemAssistant.getItemSlot(bot, bot.botArrowPrimary);
					if (slot >= 0) {
						ItemAssistant.wearItem(bot, bot.botArrowPrimary, slot);
					} else {
						bot.botDebug.add("WARNING: slot: " + slot + ", name: " + bot.getPlayerName() + ", trying to wear arrow");
						bot.botDebug.add("START OF ITEMS ON DEATH IN INVENTORY:");
						for (int i = 0; i < bot.playerItems.length; i++) {
							if (bot.playerItems[i] > 0) {
								bot.botDebug.add(ItemDefinition.getDefinitions()[bot.playerItems[i] - 1].name);
							}
						}
						bot.botDebug.add("START OF ITEMS ON DEATH EQUIPMENT WORN:");
						for (int i = 0; i < bot.playerEquipment.length; i++) {
							if (bot.playerEquipment[i] > 0) {
								bot.botDebug.add(ItemDefinition.getDefinitions()[bot.playerEquipment[i]].name);
							}
						}
					}
				}
			}
			if (!ItemAssistant.is2handed(bot.botPrimaryWeapon)) {
				bot.botDebug.add("Here27.5");
				if (bot.botShield > 0 && !ItemAssistant.hasItemEquippedSlot(bot, bot.botShield, ServerConstants.SHIELD_SLOT)) {
					bot.botDebug.add("Here28.");
					ItemAssistant.wearItem(bot, bot.botShield, ItemAssistant.getItemSlot(bot, bot.botShield));
					bot.setBotActionApplied(true);
					bot.botWearingPrimaryWeapon = true;
				}
			}
		}

	}

	private static void turnOffPrayers(Player bot) {
		if (bot.prayerActive[getMainPrayer(bot.botPkType, bot)]) {
			togglePrayer(bot, getMainPrayer(bot.botPkType, bot), false);
		}
		if (bot.prayerActive[ServerConstants.SMITE]) {
			togglePrayer(bot, ServerConstants.SMITE, false);
		}
		if (bot.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
			togglePrayer(bot, ServerConstants.PROTECT_FROM_MAGIC, false);
		}
		if (bot.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
			togglePrayer(bot, ServerConstants.PROTECT_FROM_MELEE, false);
		}
		if (bot.prayerActive[ServerConstants.PROTECT_FROM_RANGED]) {
			togglePrayer(bot, ServerConstants.PROTECT_FROM_RANGED, false);
		}

	}

	private static void lootingEvent(final Player bot) {
		bot.botDebug.add("Here39.");
		if (!bot.prayerActive[bot.botLastDamageTakenType]) {
			togglePrayer(bot, bot.botLastDamageTakenType, true);
		}
		bot.botDebug.add("Here40.");
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!Area.inDangerousPvpArea(bot)) {
					container.stop();
					return;
				}
				if (bot.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < bot.getBaseHitPointsLevel()) {
					if (ItemAssistant.hasItemInInventory(bot, 385)) {
						Food.eat(bot, 385, ItemAssistant.getItemSlot(bot, 385));
					}
				}
			}

			@Override
			public void stop() {
			}
		}, 3);
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (bot.botEarlyRetreat) {
					return;
				}
				Movement.playerWalkBot(bot, bot.botEnemyDeathPosition[0], bot.botEnemyDeathPosition[1]);
				retreatToBank(bot, false);
			}
		}, Misc.random(5, 15));
	}

	public static void damaged(Player bot) {

		if (!bot.isCombatBot()) {
			return;
		}
		if (bot.getBotStatus().equals("LOOTING")) {
			Player attacker = PlayerHandler.players[bot.getLastAttackedBy()];
			eat(bot, attacker, true, true);
			bot.botDebug.add("Here41.");
			return;
		}
		if (bot.getBotStatus().equals("")) {
			bot.botDebug.add("Here41.5");
			return;
		}
		if (!bot.getDead()) {
			bot.setBotStatus("IN COMBAT");
		}
		combatEvent(bot);
	}

	public static void diedToBot(Player victim, Player bot) {
		if (bot == null) {
			return;
		}
		if (!bot.isCombatBot()) {
			return;
		}
		bot.setBotStatus("LOOTING");
		BotCommunication.sendBotMessage(bot, "GG KILL", false);
		bot.botDebug.add("Here36.");
		wearBackPrimaryWeapon(bot, false);
		turnOffPrayers(bot);
		bot.botEnemyDeathPosition[0] = victim.getX();
		bot.botEnemyDeathPosition[1] = victim.getY();
		lootingEvent(bot);
		victim.playerBotDeaths++;
	}

	public static void death(final Player bot) {
		if (!bot.isCombatBot()) {
			return;
		}
		if (bot.getPlayerName().equals("Remy E")) {
			BotCommunication.sendBotMessage(bot, "TANK DEAD", false);
			for (int a = 0; a < ServerConstants.MAXIMUM_PLAYERS; a++) {
				Player other = PlayerHandler.players[a];
				if (other == null) {
					continue;
				}
				if (other.isCombatBot()) {
					if (other.botPkType.isEmpty() && !other.getDead()) {
						BotCommunication.sendBotMessage(other, "BM KILL", false);
						other.resetFaceUpdate();
						CycleEventHandler.getSingleton().addEvent(other, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								container.stop();
							}

							@Override
							public void stop() {
								Movement.playerWalkBot(other, (other.getX() - 3) + Misc.random(6), (other.getY() - 3) + Misc.random(6));
							}
						}, Misc.random(1, 5));
					}
				}
			}
		} else {
			BotCommunication.sendBotMessage(bot, "GG DEATH", false);
		}
		bot.botUsedSpecialAttack = false;
		bot.botEnemySpecialAttack = 10;
		bot.botWearingPrimaryWeapon = true;
		bot.setUsingSpecialAttack(false);
		bot.botDiagonalTicks = 0;
		bot.botDebug.add("START OF ITEMS ON DEATH IN INVENTORY:");
		/*
		for (int i = 0; i < bot.playerItems.length; i++)
		{
				if (bot.playerItems[i] > 0)
				{
						bot.botDebug.add(ItemDefinition.getDefinitions()[bot.playerItems[i] - 1].name);
				}
		}
		bot.botDebug.add("START OF ITEMS ON DEATH EQUIPMENT WORN:");
		for (int i = 0; i < bot.playerEquipment.length; i++)
		{
				if (bot.playerEquipment[i] > 0)
				{
						bot.botDebug.add(ItemDefinition.getDefinitions()[bot.playerEquipment[i]].name);
				}
		}
		*/
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				regearStage1Event(bot);
			}
		}, Misc.random(7, 18));
	}

	public static void regearStage1Event(final Player bot) {
		if (bot.botRegearEvent) {
			bot.botDebug.add("Here50.");
			return;
		}
		bot.botRegearEvent = true;
		bot.botDebug.add("Here50.5");
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!Area.inDangerousPvpArea(bot) && !bot.getDead()) {
					walkToBankArea(bot); // Added here because an issue where the bot would stay 1 tile away from outside wild.
					container.stop();
				}
			}

			@Override
			public void stop() {
				bot.botDebug.add("Here50.6");
				interactWithBanker(bot);
			}
		}, 3);

	}


	private static void interactWithBanker(Player bot) {
		bot.botDebug.add("Here51.");
		bot.setBotStatus("");
		for (int index = 0; index < bot.playerItems.length; index++) {
			bot.playerItems[index] = 0;
			bot.playerItemsN[index] = 0;
		}
		Movement.playerWalkBot(bot, GameType.isOsrsPvp() ? 3096 : 3080, 3515);
		applyEventWhenNextToBanker(bot);

	}

	private static void regearStage2Event(final Player bot) {
		BotContent.gearUp(bot, false);

		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				wearItemEvent(bot);
			}
		}, Misc.random(10, 25));

	}

	private static void wearItemEvent(final Player bot) {
		for (int index = 0; index < bot.playerItems.length; index++) {
			int item = bot.playerItems[index] - 1;
			if (item <= 0) {
				continue;
			}
			bot.botItemsToWear.add(item + " " + index);
		}

		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				boolean wornItem = false;
				if (bot.botItemsToWear.isEmpty()) {
					container.stop();
					return;
				}
				for (int index = 0; index < bot.botItemsToWear.size(); index++) {
					String[] args = bot.botItemsToWear.get(index).split(" ");
					int item = Integer.parseInt(args[0]);
					int slot = Integer.parseInt(args[1]);

					if (item <= 0) {
						continue;
					}

					if (!wornItem) {
						ItemAssistant.wearItem(bot, item, slot);
						bot.botItemsToWear.remove(index);
						if (Misc.hasPercentageChance(80)) {
							wornItem = true;
							break;
						}
					}

				}
			}

			@Override
			public void stop() {
				spawnOther(bot, false);
			}
		}, 1);
	}

	public static void gearUp(Player bot, boolean instantGearUp) {

		if (!bot.isCombatBot()) {
			return;
		}
		if (bot.botPkType.isEmpty()) {
			bot.getPA().movePlayer(0, 0, 0);
			return;
		}
		bot.botArrowPrimary = -1;
		bot.botArrowSpecial = -1;
		bot.botEatingEvent = false;
		bot.botPrimaryWeapon = -1;
		bot.botPureWeaponSet = -1;
		bot.botShield = -1;
		bot.botSpecialAttackWeapon = -1;
		bot.botSpecialAttackWeaponShield = -1;
		bot.botUsedSpecialAttack = false;
		if (bot.botPkType.equals("PURE")) {
			bot.botPkType = "PURE";
			QuickSetUp.setCombatSkills(bot, "PURE", false, null);
		} else if (bot.botPkType.equals("INITIATE")) {
			bot.botPkType = "INITIATE";
			QuickSetUp.setCombatSkills(bot, "INITIATE", false, null);
		} else if (bot.botPkType.equals("BERSERKER")) {
			bot.botPkType = "BERSERKER";
			QuickSetUp.setCombatSkills(bot, "BERSERKER", false, null);
			QuickSetUp.setPrayerAndMagicBook(bot, "LUNAR");
		} else if (bot.botPkType.equals("RANGED TANK")) {
			bot.botPkType = "RANGED TANK";
			QuickSetUp.setCombatSkills(bot, "RANGED TANK", false, null);
			QuickSetUp.setPrayerAndMagicBook(bot, "LUNAR");
		} else if (bot.botPkType.equals("MELEE")) {
			bot.botPkType = "MELEE";
			QuickSetUp.setCombatSkills(bot, "MAIN", false, null);
			QuickSetUp.setPrayerAndMagicBook(bot, "LUNAR");
		}
		QuickSetUp.heal(bot, true, true);
		QuickSetUp.spawnEquipmentForBot(bot, spawnEquipmentSet(bot, bot.botPkType), instantGearUp ? false : true);
		if (instantGearUp) {
			spawnOther(bot, true);
			int[] coordinates = getPkingCoordinates(bot.botPkType);
			bot.getPA().movePlayer(coordinates[0] + Misc.random(10), coordinates[1] + Misc.random(6), 0);
		}

	}

	private static int[][] spawnEquipmentSet(Player bot, String type) {
		switch (type) {
			case "MELEE":
			case "BERSERKER":
				return MeleeMain.equipmentSetBot(type.equals("BERSERKER") ? true : false);
			case "PURE":
			case "INITIATE":
				return Pure.equipmentSetBot(bot);
			case "RANGED TANK":
				return RangedTank.equipmentSetBot(bot);
		}
		return null;
	}

	private static void spawnOther(Player bot, boolean deleteInventory) {
		if (bot.botPkType.isEmpty()) {
			return;
		}
		if (deleteInventory) {
			for (int index = 0; index < bot.playerItems.length; index++) {
				bot.playerItems[index] = 0;
				bot.playerItemsN[index] = 0;
			}
		} else {
			bot.botRegearEvent = false;
		}
		QuickSetUp.spawnInventory(bot, spawnInventorySet(bot, bot.botPkType));
		CombatInterface.showCombatInterface(bot, bot.getWieldedWeapon());
		Combat.updatePlayerStance(bot);
		bot.setBotStatus("FIND FIGHT");
		runToWilderness(bot, Misc.random(5, 15));
		bot.botDebug.add("FINISHED GEARING UP, RUNNING TO WILDERNESS");
		bot.botDebug.add("Inventory------------");
		for (int i = 0; i < bot.playerItems.length; i++) {
			if (bot.playerItems[i] > 0) {
				bot.botDebug.add(ItemDefinition.getDefinitions()[bot.playerItems[i] - 1].name + ", " + (bot.playerItems[i] - 1));
			}
		}
		bot.botDebug.add("Equipment------------");
		for (int i = 0; i < bot.playerEquipment.length; i++) {
			if (bot.playerEquipment[i] > 0) {
				bot.botDebug.add(ItemDefinition.getDefinitions()[bot.playerEquipment[i]].name + ", " + bot.playerEquipment[i]);
			}
		}
	}

	private static int[][] spawnInventorySet(Player bot, String type) {
		switch (type) {
			case "MELEE":
			case "BERSERKER":
				return MeleeMain.inventory(bot);
			case "PURE":
			case "INITIATE":
				return Pure.inventory(bot);
			case "RANGED TANK":
				return RangedTank.inventory(bot);
		}
		return null;
	}


	private static void roam(final Player bot) {
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!bot.runModeOn) {
					bot.runModeOn = true;
				}
				if (!Combat.inCombat(bot) && bot.getBotStatus().equals("FIND FIGHT")) {
					if (Misc.hasPercentageChance(45)) {
						int x = 0;
						int y = 0;
						if (Misc.hasPercentageChance(50)) {
							x = bot.getX() + Misc.random(0, 8);
						} else {
							x = bot.getX() - Misc.random(0, 8);
						}
						if (Misc.hasPercentageChance(50)) {
							y = bot.getY() + Misc.random(0, 8);
						} else {
							y = bot.getY() - Misc.random(0, 8);
						}

						int[] coordinates = getPkingCoordinates(bot.botPkType);
						if (!Area.isWithInArea(x, y, coordinates[0], coordinates[0] + 10, coordinates[1], coordinates[1] + 6)) {
							return;
						}
						Movement.playerWalkBot(bot, x, y);
					}
					//findBotToAttack(bot);
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
			}
		}, 3);
	}

	@SuppressWarnings("unused")
	private static void findBotToAttack(Player bot) {
		if (bot.isMoving()) {
			return;
		}
		if (Misc.random(10) == 1) {
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (!loop.isCombatBot()) {
					continue;
				}
				if (loop.getPlayerName().equals(bot.getPlayerName())) {
					continue;
				}
				if (!loop.getPA().withinDistance(bot)) {
					continue;
				}
				if (Combat.inCombat(loop)) {
					continue;
				}
				if (!Area.inDangerousPvpArea(loop)) {
					continue;
				}
				if (loop.prayerActive[bot.botLastDamageTakenType]) {
					continue;
				}
				int combatDif1 = Combat.getCombatDifference(bot.getCombatLevel(), loop.getCombatLevel());
				if (combatDif1 > bot.getWildernessLevel() || combatDif1 > loop.getWildernessLevel()) {
					continue;
				}
				bot.setPlayerIdAttacking(loop.getPlayerId());
				bot.setPlayerIdToFollow(loop.getPlayerId());
				if (Misc.hasPercentageChance(60) && isVengeanceBot(bot.botPkType)) {
					Combat.castVengeance(bot);
				}
				break;
			}
		}
	}

	public static void goodLuckMessage(Player attacker, Player victim) {
		if (attacker.isCombatBot() && System.currentTimeMillis() - attacker.botTimeInCombat > 15000) {
			if (!attacker.getBotStatus().equals("LOOTING")) {
				BotCommunication.sendBotMessage(attacker, "GOOD LUCK", true);
			}
		}
		if (victim.isCombatBot() && System.currentTimeMillis() - victim.botTimeInCombat > 15000) {
			if (!victim.getBotStatus().equals("LOOTING")) {
				BotCommunication.sendBotMessage(victim, "GOOD LUCK", true);
			}
		}
	}

	public static void handleSafing(final Player player) {
		final Player bot = PlayerHandler.players[player.getLastAttackedBy()];
		if (bot == null) {
			return;
		}
		if (!Combat.inCombat(bot)) {
			return;
		}
		if (player.prayerActive[bot.botLastDamageTakenType] || player.prayerActive[ServerConstants.PROTECT_FROM_RANGED]
		    || player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
			return;
		}
		if (bot.playerIdAttacking != player.getPlayerId()) {
			return;
		}
		if (bot.getDead()) {
			return;
		}
		if (!bot.isCombatBot()) {
			return;
		}
		if (bot.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > 75) {
			return;
		}
		int safeHp = getPlayerMaxHitToEatAt(bot) + 5;
		if (bot.getVengeance()) {
			safeHp += 15;
		}
		if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < safeHp) {
			return;
		}
		BotCommunication.sendBotMessage(bot, "SAFE", false);
		CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				eat(bot, player, true, true);
			}
		}, Misc.random(2, 5));
	}

}
