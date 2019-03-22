package game.content.combat;


import java.util.stream.IntStream;

import core.GameType;
import core.ServerConstants;
import game.bot.BotCommunication;
import game.bot.BotContent;
import game.content.achievement.AchievementStatistics;
import game.content.achievement.Achievements;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicData;
import game.content.combat.vsplayer.melee.MeleeData;
import game.content.combat.vsplayer.range.RangedData;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.miscellaneous.TradeAndDuel;
import game.content.music.SoundSystem;
import game.content.prayer.Prayer;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.prayer.combat.impl.Wrath;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.worldevent.BloodKey;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import utility.Misc;

public class Combat {

	public static boolean wearingFullJusticiar(Player attacker) {
		if (!GameType.isOsrs()) {
			return false;
		}

		// Justiciar set
		if (ItemAssistant.getItemName(attacker.playerEquipment[ServerConstants.HEAD_SLOT]).equals("Justiciar faceguard") && ItemAssistant.getItemName(attacker.playerEquipment[ServerConstants.BODY_SLOT]).equals("Justiciar chestguard") && ItemAssistant.getItemName(attacker.playerEquipment[ServerConstants.LEG_SLOT]).equals("Justiciar legguards")) {
			return true;
		}
		return false;
	}

	public static void attackApplied(Player attacker, Player victim, String type, boolean specialAttack) {
		victim.setUnderAttackBy(attacker.getPlayerId());
		victim.setLastAttackedBy(attacker.getPlayerId());
		attacker.setPlayerIdAttacking(victim.getPlayerId());
		AchievementStatistics.startNewFight(attacker, victim, type);
		attacker.stakeAttacks++;
		if (specialAttack) {
			attacker.stakeSpecialAttacks++;
		}
	}

	public static boolean fullTrickster(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.HEAD_SLOT] == 21_467
				&& player.playerEquipment[ServerConstants.BODY_SLOT] == 21_468
				&& player.playerEquipment[ServerConstants.LEG_SLOT] == 21_469
				&& player.playerEquipment[ServerConstants.HAND_SLOT] == 21_470
				&& player.playerEquipment[ServerConstants.FEET_SLOT] == 21_471);
	}

	public static boolean fullBattleMage(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.HEAD_SLOT] == 21_467
				&& player.playerEquipment[ServerConstants.BODY_SLOT] == 21_468
				&& player.playerEquipment[ServerConstants.LEG_SLOT] == 21_469
				&& player.playerEquipment[ServerConstants.HAND_SLOT] == 21_470
				&& player.playerEquipment[ServerConstants.FEET_SLOT] == 21_471);
	}

	public static boolean hasElysianSpiritShield(Player player) {
		return GameType.isOsrs() && ItemAssistant.getItemName(player.playerEquipment[ServerConstants.SHIELD_SLOT]).equals("Elysian spirit shield");
	}

	public static boolean hasAbyssalTentacle(Player player, int itemId) {
		return GameType.isOsrs() && ItemAssistant.getItemName(itemId).equals("Abyssal tentacle");
	}

	public static boolean isZamorakFlamesEffected(Player player) {
		if (System.currentTimeMillis() - player.timeZamorakFlamesAffected <= 90000) {
			return true;
		}
		return false;
	}

	public static boolean isClawsOfGuthixEffected(Player player) {
		if (System.currentTimeMillis() - player.timeClawsOfGuthixAffected <= 90000) {
			return true;
		}
		return false;
	}

	public static boolean wearingFullObsidianArmour(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 21298 && player.playerEquipment[ServerConstants.BODY_SLOT] == 21301
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 21304;
	}

	public static boolean hasVoidTop(Player player) {
		return player.playerEquipment[ServerConstants.BODY_SLOT] == 8839 || player.playerEquipment[ServerConstants.BODY_SLOT] == 13072;
	}

	public static boolean hasVoidBottom(Player player) {
		return player.playerEquipment[ServerConstants.LEG_SLOT] == 8840 || player.playerEquipment[ServerConstants.LEG_SLOT] == 13073;
	}

	public static boolean hasSerpentineHelm(Player player) {
		// Serpentine helms and the coloured versions.
		if (ItemAssistant.hasItemEquippedSlot(player, 16137, ServerConstants.HEAD_SLOT) || ItemAssistant.hasItemEquippedSlot(player, 13197, ServerConstants.HEAD_SLOT)
		    || ItemAssistant.hasItemEquippedSlot(player, 13199, ServerConstants.HEAD_SLOT) || ItemAssistant.hasItemEquippedSlot(player, 12931, ServerConstants.HEAD_SLOT)) {
			return true;
		}
		return false;
	}

	public static void lowerBoostedCombatLevels(Player player) {
		if (player.currentCombatSkillLevel[ServerConstants.ATTACK] > 118) {
			player.currentCombatSkillLevel[ServerConstants.ATTACK] = 118;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.ATTACK);
		}
		if (player.currentCombatSkillLevel[ServerConstants.DEFENCE] > 118) {
			player.currentCombatSkillLevel[ServerConstants.DEFENCE] = 118;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.DEFENCE);
		}
		if (player.currentCombatSkillLevel[ServerConstants.STRENGTH] > 118) {
			player.currentCombatSkillLevel[ServerConstants.STRENGTH] = 118;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.STRENGTH);
		}
		if (player.currentCombatSkillLevel[ServerConstants.RANGED] > 118) {
			player.currentCombatSkillLevel[ServerConstants.RANGED] = 118;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.RANGED);
		}
		if (player.currentCombatSkillLevel[ServerConstants.MAGIC] > 105) {
			player.currentCombatSkillLevel[ServerConstants.MAGIC] = 105;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
		}
	}

	public static boolean spellbookPacketAbuse(Player player, int spellTarget) {
		if (player.spellBook.equals("MODERN") && spellTarget >= 32 && spellTarget <= 48) {
			PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
			PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", tried to use spell: " + spellTarget);
			return true;
		}
		if (player.spellBook.equals("ANCIENT")
				&& (spellTarget <= 31 || spellTarget >= (GameType.isPreEoc() ? 58 : 49))) {
			PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
			PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", tried to use spell: " + spellTarget);
			return true;
		}
		return false;
	}

	public static int antiFire(Player player, boolean dragonBolt, boolean npc) {
		int resistance = 0;

		if (player.antiFirePotion) {
			resistance++;
		}
		if (player.playerEquipment[ServerConstants.SHIELD_SLOT] == 1540 || // Anti-dragon shield
		    player.playerEquipment[ServerConstants.SHIELD_SLOT] == 11284 || // Dragonfire shield
		    player.playerEquipment[ServerConstants.SHIELD_SLOT] == 22002 || // Dragonfire ward
		    player.playerEquipment[ServerConstants.SHIELD_SLOT] == 21633 || // Ancient wyvern shield
		    !dragonBolt && player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
			resistance++;
		}
		return resistance;
	}

	public static int wyvernProtection(Player player, boolean npc) {
		int resistance = 0;
		if (player.playerEquipment[ServerConstants.SHIELD_SLOT] == 9731 // Mind shield
		    || player.playerEquipment[ServerConstants.SHIELD_SLOT] == 11284 // Dragonfire shield
		    || player.playerEquipment[ServerConstants.SHIELD_SLOT] == 21633 // Ancient wyvern shield
		    || player.playerEquipment[ServerConstants.SHIELD_SLOT] == 22002) // Dragonfire ward
		{
			resistance++;
		}
		return resistance;
	}

	public static boolean wearingFullVerac(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 4753 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4757
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 4759 && player.playerEquipment[ServerConstants.WEAPON_SLOT] == 4755;
	}

	public static boolean wearingFullDharok(Player player) {
		return player.getWieldedWeapon() == 4718 && player.playerEquipment[ServerConstants.HEAD_SLOT] == 4716 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4720
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 4722;
	}

	public static boolean wearingFullKaril(Player player) {
		return player.getWieldedWeapon() == 4734 && player.playerEquipment[ServerConstants.HEAD_SLOT] == 4732 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4736
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 4738;
	}

	public static boolean wearingFullGuthan(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 4724 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4728
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 4730 && player.playerEquipment[ServerConstants.WEAPON_SLOT] == 4726;
	}

	public static boolean wearingFullAhrim(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 4708 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4712
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 4714 && player.playerEquipment[ServerConstants.WEAPON_SLOT] == 4710;
	}

	public static boolean wearingFullTorag(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 4745 && player.playerEquipment[ServerConstants.BODY_SLOT] == 4749
		       && player.playerEquipment[ServerConstants.LEG_SLOT] == 4751 && player.playerEquipment[ServerConstants.WEAPON_SLOT] == 4747;
	}

	/**
	 * True if player has Salve amulet (e) equipped.
	 */
	public static boolean hasSalveAmuletE(Player player) {
		return (player.playerEquipment[ServerConstants.AMULET_SLOT] == 10588);
	}

	public static boolean hasAmuletOfTheDamned(Player player) {
		return (player.playerEquipment[ServerConstants.AMULET_SLOT] == 12851);
	}

	public static void castVengeance(Player player) {

		if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 94) {
			player.getPA().sendMessage("You need 94 magic to cast vengeance.");
			return;
		}
		if (player.getDead()) {
			return;
		}
		if (player.duelRule[4]) {
			player.playerAssistant.sendMessage("Magic has been disabled for this duel!");
			return;
		}
		if (player.getVengeance()) {
			player.getPA().sendMessage("You have already casted vengance.");
			return;
		}
		if (player.baseSkillLevel[ServerConstants.DEFENCE] < 40) {
			player.getPA().sendMessage("You need 40 defence to cast vengeance.");
			return;
		}
		if (System.currentTimeMillis() - player.lastVeng > 30000) {
			if (MagicData.requiredRunes(player, 53, "CHECK REQUIREMENT")) {
				MagicData.requiredRunes(player, 53, "DELETE RUNES");
				OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_VENGEANCE, 30);
				player.setVengeance(true);
				player.lastVeng = System.currentTimeMillis();
				player.startAnimation(4410);
				Skilling.addSkillExperience(player, 112, ServerConstants.MAGIC, false);
				player.gfx100(726);
			} else {
				player.getPA().sendMessage("You do not have the required runes to cast this spell.");
			}
		} else {
			long time = 30000 - (System.currentTimeMillis() - player.lastVeng);
			time /= 1000;
			if (time == 0) {
				time = 1;
			}
			String second = time > 1 ? "seconds" : "second";
			player.playerAssistant.sendMessage("You must wait " + time + " " + second + " before casting this again.");
		}
	}

	/**
	 * Refresh combat skills.
	 *
	 * @param player The associated player.
	 */
	public static void refreshCombatSkills(Player player) {
		for (int i = 0; i < 7; i++) {
			Skilling.updateSkillTabFrontTextMain(player, i);
		}
	}

	/**
	 * @return True, if the player is wearing full void melee.
	 */
	public static boolean wearingFullVoidMelee(Player player) {
		return player.playerEquipment[ServerConstants.HEAD_SLOT] == 11665 && Combat.hasVoidTop(player) && Combat.hasVoidBottom(player)
		       && player.playerEquipment[ServerConstants.HAND_SLOT] == 8842;
	}

	/**
	 * Apply the redemption prayer effect.
	 *
	 * @param player The associated player.
	 */
	public static void appendRedemption(Player player, int damage) {
		if (damage <= 0) {
			return;
		}
		if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > (player.getBaseHitPointsLevel() / 10)) {
			return;
		}
		if (player.prayerActive[ServerConstants.REDEMPTION]) {
			player.addToHitPoints((int) (player.getBaseHitPointsLevel() * .25));
			player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
			Skilling.updateSkillTabFrontTextMain(player, 3);
			Skilling.updateSkillTabFrontTextMain(player, 5);
			player.gfx0(436);
			Combat.resetPrayers(player);
		}
	}

	/**
	 * Reset combat variables.
	 */
	public static void resetSpecialAttackData(Player player) {
		player.dragonSwordSpecial = false;
		player.setSpecialAttackAccuracyMultiplier(1.0);
		player.specDamage = 1.0;
		player.setDragonClawsSpecialAttack(false);
		player.setMultipleDamageSpecialAttack(false);
		player.saradominSwordSpecialAttack = false;
		player.morrigansJavelinSpecialAttack = false;
		player.blowpipeSpecialAttack = false;
		player.showDragonBoltGFX = false;
		player.showRubyBoltGFX = false;
		player.showOpalBoltGFX = false;
		player.showDiamondBoltGFX = false;
		player.showOnyxBoltGfx = false;
		player.setMagicBowSpecialAttack(false);
		player.setUsingDarkBowSpecialAttack(false);
		player.setUsingDarkBowNormalAttack(false);
		player.armadylCrossbowSpecial = false;
		player.dragonCrossbowSpecial = false;
		player.getAttributes().put(Player.MORRIGANS_AXE_SPECIAL, false);
		player.getAttributes().put(Player.MORRIGANS_JAVS_SPECIAL, false);
	}

	/**
	 * Restore the special attack by 10% every 30 seconds, as to how osrs has it.
	 */
	public static void restoreSpecialAttackEvent(final Player player) {
		if (player.specialAttackEvent) {
			return;
		}
		if (player.getSpecialAttackAmount() >= 10) {
			return;
		}
		player.specialAttackEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getSpecialAttackAmount() < 10) {
					player.specialAttackRestoreTimer++;
					if (player.specialAttackRestoreTimer >= 50) {
						double amount = 1.0;
						if (player.getSpecialAttackAmount() + amount > 10.0) {
							amount = 10.0 - player.getSpecialAttackAmount();
						}
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() + amount, false);
						CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
						player.specialAttackRestoreTimer = 0;
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.specialAttackEvent = false;
				player.specialAttackRestoreTimer = 0;
			}
		}, 1);
	}

	/**
	 * Use to check if the player is in a player vs player area or in combat and also send alert messages to the player.
	 *
	 * @return True, if the player is in PVP area or in combat.
	 */
	public static boolean inPVPAreaOrCombat(Player player) {
		if (Area.inDangerousPvpAreaOrClanWars(player)) {
			player.playerAssistant.sendMessage("You cannot do this in a player vs player area.");
			return true;
		}
		if (inCombatAlert(player)) {
			return true;
		}
		return false;
	}

	/**
	 * Use to check if the player is in combat and also send alert message to the player.
	 *
	 * @return True, if player is in combat.
	 */
	public static boolean inCombatAlert(Player player) {
		if ((inCombat(player) || System.currentTimeMillis() - player.timeNpcAttackedPlayer < 9600) && !player.isAdministratorRank()) {
			int timer = secondsUntillOutOfCombat(player);
			String word = timer > 1 ? "seconds" : "second";
			if (timer <= 0) {
				return false;
			}
			player.playerAssistant.sendMessage("You need to wait " + timer + " " + word + " from being out of combat to use this.");
			return true;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return Amount of seconds untill completely out of combat, starting from 10.
	 */
	public static int secondsUntillOutOfCombat(Player player) {
		long timer = player.getTimeAttackedAnotherPlayer();
		if (timer < player.getTimeUnderAttackByAnotherPlayer()) {
			timer = player.getTimeUnderAttackByAnotherPlayer();
		} else if (timer < player.getTimeNpcAttackedPlayer()) {
			timer = player.getTimeNpcAttackedPlayer();
		} else if (timer < player.timeNpcAttackedPlayer) {
			timer = player.timeNpcAttackedPlayer;
		}
		if (System.currentTimeMillis() < player.getTimeCanDisconnectAtBecauseOfCombat()) {
			int left = (int) ((player.getTimeCanDisconnectAtBecauseOfCombat() - System.currentTimeMillis()) / 1000);
			if (left == 0) {
				left = 1;
			}
			return left;
		}

		if ((System.currentTimeMillis() - timer) <= 650) {
			timer = 10;
		} else {
			timer = (System.currentTimeMillis() - timer) / 1000;
		}
		timer = 10 - timer;
		if (timer == 0) {
			timer = 10;
		}
		return (int) timer;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player has attacked another player within the given time.
	 */
	public static boolean wasAttackingAnotherPlayer(Player player, long time) {
		if (System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= time) {
			return true;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player has been under attack by another player within the given time.
	 */
	public static boolean wasUnderAttackByAnotherPlayer(Player player, long time) {
		if (System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= time) {
			return true;
		}
		return false;
	}

	/**
	 * @return True, if the player has been in combat with an NPC in the last 5 seconds.
	 */
	public static boolean wasAttackedByNpc(Player player) {
		if (System.currentTimeMillis() - player.timeNpcAttackedPlayer < 4000) {
			return true;
		}
		return false;
	}

	/**
	 * @return True, if the player has been in combat in the last 10 seconds.
	 */
	public static boolean inCombat(Player player) {
		if (wasUnderAttackByAnotherPlayer(player, 9600) || wasAttackingAnotherPlayer(player, 9600) || wasAttackedByNpc(player) || player.getTimeCanDisconnectAtBecauseOfCombat() > System.currentTimeMillis()) {
			return true;
		}
		if (System.currentTimeMillis() - player.timeExitedWilderness < 10000) {
			return true;
		}
		return false;
	}

	public static boolean getUsingCrossBow(Player player) {
		if (GameType.isPreEoc()) {
			if (player.getWieldedWeapon() == 24_338) {
				return true;
			}
		}
		if (player.getWieldedWeapon() == 9185 || ItemAssistant.getItemName(player.getWieldedWeapon()).equals("Armadyl crossbow") || player.getWieldedWeapon() == 21012) {
			return true;
		}
		return false;
	}

	public static boolean usingCrystalBow(Player player) {
		return player.getWieldedWeapon() >= 4212 && player.getWieldedWeapon() <= 4223;
	}

	/**
	 * Create a hitsplat on the player that is not created by an enemy player. Such as npc damage, self inflicting damage like rock cake etc..
	 */
	public static void createHitsplatOnPlayerNormal(Player player, int damage, int hitSplatColour, int icon) {
		if (player.isTeleporting()) {
			return;
		}
		if (damage > player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS)) {
			damage = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
			if (damage == 0) {
				return;
			}
		}
		if (damage == 0 && player.getDead()) {
			return;
		}
		boolean maxHit = false;
		if (player.dragonSpearEvent) {
			player.dragonSpearEffectStack.add("Damage:" + damage + " " + hitSplatColour + " " + icon + " " + maxHit);
			return;
		}
		player.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
		player.dealDamage(damage);
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
	}

	/**
	 * @param attacker The player dealing the hitsplat.
	 * @param damage The damage dealt.
	 * @param hitSplatColour
	 * @param icon The icon type. -1 for no icon, 0 is Melee, 1 is Ranged, 2 is Magic, 3 for deflect, 4 for Dwarf cannon.
	 * @param damageType TODO
	 * @param damageType The type of damage that it is, used to calculate a player's total damage of a specific hitsplat type, such as MELEE, RANGED, MAGIC, VENGEANCE etc.
	 * @param forceMaxHit TODO
	 * @param theVictim The player receiving the hitsplat.
	 */
	public static void createHitsplatOnPlayerPvp(Player attacker, Player victim, int damage, int hitSplatColour, int icon, boolean isNotMainDamage,
	                                             String damageType, int forceMaxHit) {
		if (victim.isTeleporting()) {
			return;
		}
		if (damage == 0 && victim.getDead()) {
			return;
		}
		if (attacker.getDead() && attacker.getHeight() == 20) {
			return;
		}
		if (victim.getDead()) {
			return;
		}
		switch (damageType) {
			case "MELEE":
				attacker.totalDamage[0] += damage;
				break;
			case "RANGED":
				attacker.totalDamage[1] += damage;
				break;
			case "MAGIC":
				attacker.totalDamage[2] += damage;
				break;
			case "VENGEANCE":
				attacker.totalDamage[3] += damage;
				break;
			case "RECOIL":
				attacker.totalDamage[4] += damage;
				break;
			case "DRAGON_FIRE":
				attacker.totalDamage[5] += damage;
				break;
		}
		if (!attacker.ignoreInCombat) {
			victim.setTimeUnderAttackByAnotherPlayer(System.currentTimeMillis());
			attacker.setTimeAttackedAnotherPlayer(System.currentTimeMillis());
		}
		else {
			victim.timeUnderAttackByAnotherPlayerOther = System.currentTimeMillis();
		}

		if (victim.damageTakenName[attacker.getPlayerId()] != null) {
			if (!victim.damageTakenName[attacker.getPlayerId()].equals(attacker.getPlayerName())) {
				victim.damageTaken[attacker.getPlayerId()] = 0;
				victim.damageTakenName[attacker.getPlayerId()] = attacker.getPlayerName();
			}
		} else {
			victim.damageTakenName[attacker.getPlayerId()] = attacker.getPlayerName();
		}
		victim.damageTaken[attacker.getPlayerId()] += damage;

		boolean maxHit = false;
		if (!isNotMainDamage) {
			if (attacker.maximumDamageMelee < 4) {
				attacker.maximumDamageMelee = 4;
			}
			if (attacker.maximumDamageRanged < 4) {
				attacker.maximumDamageRanged = 4;
			}
			if (attacker.getMaximumDamageMagic() < 4) {
				attacker.setMaximumDamageMagic(4);
			}

			switch (icon) {
				case 0:
					int damageMelee = attacker.maximumDamageMelee;
					if (attacker.isGraniteMaulSpecial) {
						damageMelee = attacker.graniteMaulSpecialCriticalDamage;
					}
					maxHit = damage >= damageMelee * 0.96;
					break;
				case 1:
					maxHit = damage >= attacker.maximumDamageRanged * 0.96;
					break;
				case 2:
					maxHit = damage >= attacker.getMaximumDamageMagic() * 0.96;
					break;
			}
			if (icon <= 2 && forceMaxHit >= 1)
			{
				maxHit = damage >= forceMaxHit * 0.96;
			}
		}

		if (victim.isCombatBot()) {
			if (icon == ServerConstants.MAGIC_ICON) {
				victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_MAGIC;
			} else if (icon == ServerConstants.RANGED_ICON) {
				victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_RANGED;
			} else if (icon == ServerConstants.MELEE_ICON) {
				victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_MELEE;
			}
		}
		if (!isNotMainDamage) {
			Combat.applyPrayerReduction(attacker, victim, damage / 4, true);
		}
		if (victim.dragonSpearEvent) {
			victim.dragonSpearEffectStack.add("Damage:" + damage + " " + hitSplatColour + " " + icon + " " + maxHit);
			return;
		}
		victim.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
		victim.dealDamage(damage);
		if (!isNotMainDamage) {
			Effects.recoilEffect(attacker, victim, damage);
			Effects.dharoksDamnedEffect(attacker, victim, damage);
			/*
			 * Wrath
			 */
			if (GameType.isPreEoc()) {
				if (attacker.getPrayer().getActive().contains(Prayer.WRATH)) {
					Wrath.handleWrath(attacker);
				}
			}
			Effects.appendRetributionOrWrath(victim, attacker);
			BloodKey.goldenSkullPlayerDamaged(victim, attacker, damage);
		}
		Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.HITPOINTS);

	}

	public static void appendVengeanceVsPlayer(Player victimOfVengeance, Player vengeancePlayer, int damage) {
		if (damage <= 0) {
			return;
		}
		if (!vengeancePlayer.getVengeance()) {
			return;
		}
		damage = (int) (damage * 0.75);

		// If player hits 3 or less, it should break vengeance. Not confirmed with Osrs.

		if (vengeancePlayer.getDead() && victimOfVengeance.getHeight() == 20) {
			return;
		}
		victimOfVengeance.setTimeUnderAttackByAnotherPlayer(System.currentTimeMillis());
		vengeancePlayer.forcedChat("Taste vengeance!", false, false);
		vengeancePlayer.setVengeance(false);
		damage = Math.min(victimOfVengeance.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS), damage);

		vengeancePlayer.specialAttackWeaponUsed[31] = 1;
		vengeancePlayer.againstPlayer = true;
		vengeancePlayer.setWeaponAmountUsed(31);
		SpecialAttackTracker.saveMaximumDamage(vengeancePlayer, damage, "VENGEANCE", false);
		Combat.createHitsplatOnPlayerPvp(vengeancePlayer, victimOfVengeance, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "VENGEANCE", -1);
		Skilling.updateSkillTabFrontTextMain(victimOfVengeance, 3);
		victimOfVengeance.setUpdateRequired(true);
	}

	public static boolean usingMultiSpell(Player player) {
		switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0]) {
			case 12891:
			case 12881:
			case 13011:
			case 13023:
			case 12919:
				// blood spells
			case 12929:
			case 12963:
			case 12975:
				return true;
		}
		return false;
	}


	public static void multiSpellEffect(Player attacker, Player victim, int damage) {
		switch (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0]) {
			case 13011:
			case 13023:
				if (System.currentTimeMillis() - victim.reduceStat > 35000) {
					victim.reduceStat = System.currentTimeMillis();
					victim.currentCombatSkillLevel[ServerConstants.ATTACK] -= ((victim.getBaseAttackLevel() * 10) / 100);
				}
				break;
			case 12919:
				// blood spells
			case 12929:
				int heal = damage / 4;
				attacker.addToHitPoints(heal);
				break;
			case 12891:
			case 12881:
				if (victim.canBeFrozen()) {
					Movement.stopMovement(victim);
					Combat.resetPlayerAttack(victim);
					// Ice burst or Ice barrage.
					victim.setFrozenLength(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12881 ? 10000 : 20000);
					OverlayTimer
							.sendOverlayTimer(victim, Combat.getFreezeSpriteId(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0]), (int) victim.getFrozenLength() / 1000);
					victim.frozenBy = attacker.getPlayerId();
					victim.getPA().sendMessage("<col=ff0000>You have been frozen!");
				}
				break;
		}
	}

	public static void applyPrayerReduction(Player attacker, Player victim, int prayerReduceAmount, boolean attackerUsingSmite) {
		if (victim == null) {
			return;
		}
		if (attacker != null) {
			if (!attacker.prayerActive[ServerConstants.SMITE] && attackerUsingSmite) {
				return;
			}
		}
		if (prayerReduceAmount <= 0) {
			return;
		}
		if (victim.getTank()) {
			return;
		}
		if (victim.dragonSpearEvent) {
			victim.dragonSpearEffectStack.add("Prayer:" + prayerReduceAmount);
			return;
		}
		victim.currentCombatSkillLevel[ServerConstants.PRAYER] -= prayerReduceAmount;
		if (victim.getCurrentCombatSkillLevel(ServerConstants.PRAYER) <= 0) {
			victim.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
			resetPrayers(victim);
		}
		Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.PRAYER);
	}

	public static void applyPrayerReduction(Player victim, int prayerReduceAmount) {
		applyPrayerReduction(null, victim, prayerReduceAmount, false);
	}

	public static void fireProjectilePlayer(Player player, Player victim) {
		if (victim.getPlayerId() > 0) {
			if (victim != null) {
				player.setProjectileStage(2);
				final int pX = player.getX();
				final int pY = player.getY();
				int oX = victim.getX();
				int oY = victim.getY();
				final int offX = (pY - oY) * -1;
				final int offY = (pX - oX) * -1;
				if (player.getUsingRanged()) {
					int decrease = 0;
					int decreaseSecond = 0;
					if (player.isUsingDarkBowNormalAttack()) {
						decrease += 15;
						decreaseSecond -= 15;
					}
					if (GameType.isPreEoc()) {
						if (player.playerEquipment[3] == 15241) {
							player.gfx0(2138);
						}
					}
					player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, getProjectileSpeed(player) - decrease, getRangeProjectileGFX(player), getProjectileStartHeight(player),
							getProjectileEndHeight(player), -victim.getPlayerId() - 1, getStartDelay(player), getProjectileSlope(player),
					                                        player.getHeight());
					if (player.isMagicBowSpecialAttack()) {
						player.getPA()
						      .createPlayersProjectile2(pX, pY, offX, offY, 50, 95, getRangeProjectileGFX(player), getProjectileStartHeight(player), getProjectileEndHeight(player),
										-victim.getPlayerId() - 1, 65, 10, player.getHeight());
					} else if (usingDbow(player)) {
						player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, getProjectileSpeed(player) + 30 - decrease + decreaseSecond, getRangeProjectileGFX(player), 36, 31, -victim.getPlayerId() - 1, player.isUsingDarkBowNormalAttack() ? 54 : 51, player.isUsingDarkBowSpecialAttack() ? 30 : getProjectileSlope(player), player.getHeight());
					}
				}
			}
		}
	}

	public static int getProjectileSlope(Player player) {
		if (player.getSpellId() >= 0) {
			return 16;

		}
		if (RangedData.hasBowEquipped(player) || player.getEquippedWeapon(4214)) {
			return 16;
		} else if (RangedData.hasRangedWeapon(player)) {
			return 5;
		} else {
			return 0;
		}
	}

	/**
	 * @return The beginning height of the projectile.
	 */
	public static int getProjectileStartHeight(Player player) {
		if (player.armadylCrossbowSpecial || player.dragonCrossbowSpecial) {
			return 36;
		}
		int weapon = player.getWieldedWeapon();

		// Bronze to rune knife.
		if (weapon >= 863 && weapon <= 868) {
			return 39;
		}
		switch (player.getWieldedWeapon()) {
			case 15241:
				// Hand cannon.
				return 20;
			case 13_883:
				return 20;
			case 11959:
				// chin
				return 35;
			case 12153:
				return 20;
		}
		return 41;
	}

	/**
	 * @return The final height of the project as it ends.
	 */
	public static int getProjectileEndHeight(Player player) {
		if (player.armadylCrossbowSpecial || player.dragonCrossbowSpecial) {
			return 36;
		}
		switch (player.getWieldedWeapon()) {
			case 15241:
				// Hand cannon.
				return 20;

			case 11959:
				// chin
				return 20;
		}
		return 34;
	}


	public static boolean usingDbow(Player player) {
		return CombatConstants.isDarkBow(player.getWieldedWeapon());
	}

	public static boolean usingChins(Player player) {
		return CombatConstants.isChinchompa(player.getWieldedWeapon());
	}

	public static boolean checkSpecAmount(Player player, int weapon) {
		if (player.duelRule[10] && player.getDuelStatus() == 5) {
			player.playerAssistant.sendMessage("Special attacks have been disabled during this duel!");
			player.setUsingSpecialAttack(false);
			CombatInterface.updateSpecialBar(player);
			Combat.resetPlayerAttack(player);
			return false;
		}

		double vigour = player.getEquippedRing(19_669) && GameType.isPreEoc() ? 0.90 : 1.00;

		String itemName = ItemAssistant.getItemName(weapon);
		if (GameType.isOsrs() && Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, weapon, 0) || GameType.isPreEoc() && weapon == 11694) {
			if (player.getSpecialAttackAmount() >= 5 * vigour) {
				player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
				CombatInterface.addSpecialBar(player, weapon);
				return true;
			}
			return false;
		}
		if (Combat.hasAbyssalTentacle(player, weapon)) {
			if (player.getSpecialAttackAmount() >= 5 * vigour) {
				player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
				CombatInterface.addSpecialBar(player, weapon);
				return true;
			}
			return false;
		}
		if (ItemAssistant.getItemName(weapon).equals("Dragon claws")) {
			if (player.getSpecialAttackAmount() >= 5 * vigour) {
				player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
				CombatInterface.addSpecialBar(player, weapon);
				return true;
			}
		}
		if (ItemAssistant.getItemName(weapon).contains("Granite maul")) {
			if (player.getSpecialAttackAmount() >= 5 * vigour) {
				player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
				CombatInterface.addSpecialBar(player, weapon);
				return true;
			}
			return false;
		}
		if (GameType.isPreEoc()) {
			switch (weapon) {
				case 11696: // bgs
					if (player.getSpecialAttackAmount() >= 5 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 11698: // sgs
					if (player.getSpecialAttackAmount() >= 5 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 11700: //zgs
					if (player.getSpecialAttackAmount() >= 5 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 13_879: // morrigan javelin
					if (player.getNpcIdAttacking() > 0) {
						player.getPA().sendMessage("This special attack can only be used on players.");
						player.setUsingSpecialAttack(false);
						CombatInterface.updateSpecialBar(player);
						Combat.resetPlayerAttack(player);
						return false;
					}
					if (player.getSpecialAttackAmount() >= 5.0 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.0 * vigour,
								true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 19780: // korasi
					if (player.getSpecialAttackAmount() >= 5.5 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.5 * vigour,
								true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 13902: // statius hammer
					if (player.getSpecialAttackAmount() >= 3.5 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 3.5 * vigour,
								true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 13905: // vesta spear
					if (player.getSpecialAttackAmount() >= 5.0 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.0 * vigour,
								true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
				case 21371: // abyssal vine
					if (player.getSpecialAttackAmount() >= 6.0 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 6.0 * vigour,
								true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;

				case 15241: // hand cannon
					if (player.getSpecialAttackAmount() >= 5.0 * vigour) {
						player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.0 * vigour,
								true);
						CombatInterface.addSpecialBar(player, weapon);
						return true;
					}
					return false;
			}
		}
		else {
			if (itemName.equals("Heavy ballista") || itemName.equals("Light ballista")) {
				if (player.getSpecialAttackAmount() >= 6.5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 6.5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			}
			if (itemName.equals("Armadyl crossbow")) {
				if (player.getSpecialAttackAmount() >= 4 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 4.0 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			}
		}
		switch (weapon) {
			case 22636: // morrigan javelin
				if (player.getSpecialAttackAmount() >= 5.0 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.0 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 22622: // statius hammer
				if (player.getSpecialAttackAmount() >= 3.5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 3.5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 22610: // vesta spear
				if (player.getSpecialAttackAmount() >= 5.0 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.0 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;


			// Staff of the dead & Toxic staff of the dead
			case 11791:
			case 12904:
			case 16209:
			case 22296: // Staff of light
			case 16272: // Toxic staff of the dead
			case 15486:
				if (player.getSpecialAttackAmount() >= 10.0 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 10.0 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 12808: // Sara's blessed sword.
				if (player.getSpecialAttackAmount() >= 6.5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 6.5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;

			// Dragon halberd.
			case 3204:
				if (player.getSpecialAttackAmount() >= 3 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 3 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			// Hand cannon.
			case 15241:
			case 11804: // Bgs
			case 20370: // Bgs (or)
			case 13271: // Abyssal dagger.
				if (player.getSpecialAttackAmount() >= 5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 1249:
			case 11824:
				// Dragon spear
			case 1215:
			case 1231:
			case 5680:
			case 5698:
			case 13901:
			case 13899:
			case 1305:
			case 20849: // Dragon thrownaxe
			case 1434:
			case 11889: // Zamorakian hasta
			case 22613: // Vesta's longsword
				if (player.getSpecialAttackAmount() >= 2.5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 2.5 * vigour, true);
						CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 11802:
			case 20368: // Ags
			case 20374: // Zgs (orF)
			case 11808: // Zgs normal
			case 20372: // Sgs
			case 11806:
			case 13883:
			case 10887:
			case 12788: // Magic shortbow (i).
			case 12926: // Toxic blowpipe.
			case 4151: // Abyssal whip
			case 12773: // Volcanic abyssal whip
			case 12774: // Frozen abyssal whip
			case 15_441:
			case 15_442:
			case 15_443:
			case 15_444:
			case 22634: // Morrigan's throwing axe
				if (player.getSpecialAttackAmount() >= 5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 13576:
				if (player.getSpecialAttackAmount() >= 5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 1377:
			case 11838:
			case 13907:
			case 11061: // Ancient mace
				if (player.getSpecialAttackAmount() >= 10 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 10 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 4587:
			case 20000:
			case 859:
			case 861: // Magic shortbow.
			case 11235:
			case 12765:
			case 12766:
			case 12767:
			case 12768:
				if (player.getSpecialAttackAmount() >= 5.5 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 5.5 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;

			case 21009: // Dragon sword
				if (player.getSpecialAttackAmount() >= 4 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 4.0 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
			case 21902: // Dragon crossbow
				if (player.getSpecialAttackAmount() >= 6 * vigour) {
					player.setSpecialAttackAmount(player.getSpecialAttackAmount() - 6.0 * vigour, true);
					CombatInterface.addSpecialBar(player, weapon);
					return true;
				}
				return false;
		}
		return false;
	}

	/**
	 * Stop the player from attacking.
	 */
	public static void resetPlayerAttack(Player player) {
		player.setUsingRanged(false);
		player.setMeleeFollow(false);
		player.resetNpcIdentityAttacking();
		player.resetFaceUpdate();
		player.resetPlayerIdAttacking();
		Follow.resetFollow(player);
		player.setLastCastedMagic(false);
		player.setSpellId(-1);
	}

	public static int getCombatDifference(int combat1, int combat2) {
		if (combat1 > combat2) {
			return (combat1 - combat2);
		}
		if (combat2 > combat1) {
			return (combat2 - combat1);
		}
		return 0;
	}


	public final static double[] prayerDrainData =
			{
					0.05, // Thick Skin.
					0.05, // Burst of Strength.
					0.05, // Clarity of Thought.
					0.05, // Sharp Eye.
					0.05, // Mystic Will.
					0.10, // Rock Skin.
					0.10, // SuperHuman Strength.
					0.10, // Improved Reflexes.
					0.016, // Rapid restore.
					0.03, // Rapid Heal.
					0.03, // Protect Items.
					0.10, // Hawk eye.
					0.10, // Mystic Lore.
					0.20, // Steel Skin.
					0.20, // Ultimate Strength.
					0.20, // Incredible Reflexes.
					0.20, // Protect from Magic.
					0.20, // Protect from Missiles.
					0.20, // Protect from Melee.
					0.20, // Eagle Eye.
					0.20, // Mystic Might.
					0.05, // Retribution.
					0.10, // Redemption.
					0.3, // Smite.
					0.4, // Chivalry.
					0.4, // Piety.
					0.05, // Preserve.
					0.4, // Rigour.
					0.4, // Augury.
			};



	public static void reducePrayerLevel(Player player) {
		if (player.getTank()) {
			return;
		}
		if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) - 1 > 0) {
			player.currentCombatSkillLevel[ServerConstants.PRAYER] -= 1;
		} else {
			player.playerAssistant.sendMessage("You have run out of prayer points!");
			player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
			SoundSystem.sendSound(player, 437, 0);
			resetPrayers(player);
		}
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
	}

	public static void resetPrayers(Player player) {
		RegularPrayer.resetAllPrayerGlows(player);
		player.headIcon = -1;
		player.getPA().requestUpdates();
		InterfaceAssistant.quickPrayersOff(player);
		player.quickPray = false;
	}

	/**
	 * @param attackType The combat type, 0 is Melee, 1 is Range, 2 is Magic.
	 * @param damage The damage dealt to the target.
	 */
	public static void addCombatExperience(Player player, int attackType, int damage) {
		if (damage == 0 && !player.isUsingMagic()) {
			return;
		}
		if (player.isMagicSplash() && player.isUsingMagic()) {
			damage = 0;
			// Do not return because magic gives base experience.
		}
		switch (attackType) {

			case 0:
				// Melee
				switch (player.getCombatStyle()) {

					case 0:
						// Accurate
						Skilling.addSkillExperience(player, 4 * damage, ServerConstants.ATTACK, false);
						break;
					case 1:
						// Aggressive
						Skilling.addSkillExperience(player, 4 * damage, ServerConstants.STRENGTH, false);
						break;
					case 2:
						// Block
						Skilling.addSkillExperience(player, 4 * damage, ServerConstants.DEFENCE, false);
						break;
					case 3:
						// Controlled
						for (int i = 0; i < 3; i++) {
							Skilling.addSkillExperience(player, (4 * damage) / 3, i, false);
						}
						break;

				}

				break;

			case 1:
				// Ranged
				switch (player.getCombatStyle()) {

					case 0:
						// Accurate
					case 1:
						// Rapid
						Skilling.addSkillExperience(player, 4 * damage, ServerConstants.RANGED, false);
						break;
					case 3:
						// Block
						Skilling.addSkillExperience(player, 2 * damage, ServerConstants.RANGED, false);
						Skilling.addSkillExperience(player, 2 * damage, ServerConstants.DEFENCE, false);
						break;

				}

				break;

			case 2:
				// Magic
				int magicXP = damage * 2 + CombatConstants.MAGIC_SPELLS[player.getSpellId()][7];

					Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
					if (player.getSpellId() == 47) {
						SoundSystem.sendSound(player, victim, 1125, 300);
					} else if (player.getSpellId() == 43) {
						SoundSystem.sendSound(player, victim, 1110, 300);
					}
					Skilling.addSkillExperience(player, magicXP, ServerConstants.MAGIC, false);
				break;

		}

		// Teleblock spell
		if (player.getSpellId() >= 0) {
			if (damage > 0) {
				Skilling.addSkillExperience(player, (int) ((damage * 1.3)), ServerConstants.HITPOINTS, false); // Hitpoints experience.
			}
		} else {
			Skilling.addSkillExperience(player, (int) ((damage * 1.3)), ServerConstants.HITPOINTS, false); // Hitpoints experience.
		}
		player.usingMaxHitDummy = false;
	}

	/**
	 * Weapon stand, walk, run, etc emotes
	 **/
	public static void updatePlayerStance(Player player) {
		AgilityAssistant.stopResting(player);
		String weapon = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();

		/* Normal animations for without weapons. */
		player.playerStandIndex = 0x328;
		player.playerTurnIndex = 0x337;
		player.playerWalkIndex = 0x333;
		player.playerTurn180Index = 0x334;
		player.playerTurn90CWIndex = 0x335;
		player.playerTurn90CCWIndex = 0x336;
		player.playerRunIndex = 0x338;

		if (updatePlayerStancePreEoc(player)) {
			return;
		}
		// Zamorakian spear
		if (player.getWieldedWeapon() == 11824) {
			weaponRoamAnimations(player, 1662, 1663, 1664);
			return;
		}

		if (weapon.contains("ahrim") || weapon.contains("sceptre") || weapon.contains("battlestaff")
				|| weapon.contains("toktz-mej-tal") || weapon.contains("trident")) {
			weaponRoamAnimations(player, 809, 1146, 1210);
			return;
		}
		if (weapon.equals("dinh's bulwark")) {
			weaponRoamAnimations(player, 7508, 7510, 7509);
			return;
		}
		if (weapon.equals("elder maul")) {
			weaponRoamAnimations(player, 7518, 7520, 7519);
			return;
		}
		if (weapon.contains("boxing")) {
			weaponRoamAnimations(player, 3677, 3680, 3680);
			return;
		}
		if (weapon.contains("sled") && !player.idleEventUsed) {
			weaponRoamAnimations(player, 1461, 1468, 1467);
			return;
		}
		if (weapon.contains("giant present")) {
			weaponRoamAnimations(player, 4193, 4194, 4194);
			return;
		}
		if (weapon.contains("toy kite")) {
			weaponRoamAnimations(player, 8981, 8982, 8986);
		}
		if (weapon.contains("basket of eggs")) {
			weaponRoamAnimations(player, 1836, 1836, 1836);
		}
		if (weapon.contains("staff") || weapon.contains("halberd") || weapon.contains("spear") || weapon.contains("guthan") || weapon.contains("rapier") || weapon.contains("wand")
				|| weapon.contains("cane") || weapon.contains("vesta's longsword")) {
			weaponRoamAnimations(player, 813, 1146, 1210);
			return;
		}
		if (weapon.contains("dharok")) {
			weaponRoamAnimations(player, 2065, 2064, 824);
			return;
		}
		if (weapon.contains("verac")) {
			weaponRoamAnimations(player, 1832, 1830, 1831);
			return;
		}
		if (weapon.contains("karil")) {
			weaponRoamAnimations(player, 2074, 2076, 2077);
			return;
		}
		if (weapon.contains("2h sword")) {
			if (GameType.isOsrs()) {
				weaponRoamAnimations(player, 2561, 2064, 2563);
			} else {
				weaponRoamAnimations(player, 7047, 7046, 7039);
			}
			return;
		}
		if (weapon.contains("godsword") || weapon.contains("blessed sword") || weapon.contains("saradomin sword") || weapon.contains("blessed sword") || weapon.contains("spade")) {
			if (GameType.isOsrs()) {
				weaponRoamAnimations(player, 7053, 7052, 7043);
			}
			else {
				weaponRoamAnimations(player, 7047, 7046, 7039);
			}
			return;
		}
		if (weapon.contains("scimitar")) {
			weaponRoamAnimations(player, 808, 819, 824);
			return;
		}
		if (weapon.contains("crossbow")) {
			weaponRoamAnimations(player, 4591, 4226, 4228);
			return;
		}
		if (weapon.contains("bow")) {
			weaponRoamAnimations(player, 808, 819, 824);
			return;
		}
		if (weapon.contains("banner")) {
			weaponRoamAnimations(player, 1421, 1422, 1427);
			return;
		}
		if (weapon.contains("tentacle")) {
			weaponRoamAnimations(player, 808, 1660, 1661);
			return;
		}
		if (weapon.contains("granite maul")) {
			weaponRoamAnimations(player, 1662, 1663, 1664);
			return;
		}

		if (GameType.isOsrs()) {
			if (weapon.equals("heavy ballista") || weapon.equals("light ballista")) {
				weaponRoamAnimations(player, 7220, 7223, 7221);
				return;
			}
		}
		switch (player.getWieldedWeapon()) {
			// Clueless scroll
			case 20249:
				weaponRoamAnimations(player, 7271, 7272, 7273);
				break;

			// Scythe of vitur				
			case 22325:
				weaponRoamAnimations(player, 8057, 8011, 8016);
				break;

			// Viggora's chainmace				
			case 22545:
				weaponRoamAnimations(player, 244, 247, 248);
				break;

			// Heavy casket
			case 19941:
				weaponRoamAnimations(player, 4193, 4194, 4194);
				break;
			case 4151: // Abyssal whip
			case 12773: // Volcanic abyssal whip
			case 12774: // Frozen abyssal whip
			case 21371:
			case 15_441:
			case 15_442:
			case 15_443:
			case 15_444:
				weaponRoamAnimations(player, GameType.isOsrs() ? 808 : 11973, GameType.isOsrs() ? 1660 : 11975, 1661);
				break;

			case 13271: // Abyssal dagger (p++)
				player.playerStandIndex = 3296;
				break;

			case 12727: // Event rpg
				weaponRoamAnimations(player, 2316, 2317, 2322);
				break;

			case 20779: // Hunting knife
				weaponRoamAnimations(player, 3296, 7327, 2322);
				break;

			case 20056: // Ale of the gods
				weaponRoamAnimations(player, 3040, 3039, 3039);
				break;

			case 10010: // Butterfly net
			case 21865: // xmas net
				weaponRoamAnimations(player, 6604, 6607, 6603);
				break;

			case 10887:
				// Barrelchest Anchor
				weaponRoamAnimations(player, 5869, 5867, 5868);
				break;
			case 6528: // Tzhaar-ket-om
			case 20756: // Hill giant club
				weaponRoamAnimations(player, 0x811, 1663, 1664);
				break;
			case 1305:
				// Dragon longsword
				player.playerStandIndex = 809;
				break;

		}
	}

	private static boolean updatePlayerStancePreEoc(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}

		// Hand cannon
		if (player.getWieldedWeapon() == 15241) {
			weaponRoamAnimations(player, 12155, 12154, 12154);
			return true;
		}

		// Chaotic maul
		if (player.getWieldedWeapon() == 18353) {
			weaponRoamAnimations(player, 13217, 13218, 13220);
			return true;
		}
		return false;
	}

	/**
	 * The animations of the player when wielding a weapon.
	 *
	 * @param stand Animation when standing.
	 * @param walk Animation when walking.
	 * @param run Animation when running.
	 */
	public static void weaponRoamAnimations(Player player, int stand, int walk, int run) {
		player.playerStandIndex = stand;
		player.playerWalkIndex = walk;
		player.playerRunIndex = run;
		player.playerTurnIndex = walk;
		player.playerTurn180Index = walk;
		player.playerTurn90CWIndex = walk;
		player.playerTurn90CCWIndex = walk;
	}

	/**
	 * Block animations
	 */
	public static int getBlockAnimation(Player player) {
		if (player.getDead()) {
			return -1;
		}
		if (player.getDoingAgility() || player.getTransformed() != 0 || player.isTeleporting()) {
			return -1;
		}
		player.soundSent = false;
		String shield = ItemAssistant.getItemName(player.playerEquipment[ServerConstants.SHIELD_SLOT]).toLowerCase();
		String weapon = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();
		if (shield.contains("defender")) {
			SoundSystem.sendSound(player, 791, 0);
			player.soundSent = true;
			return 4177;
		}
		if (shield.contains("book") && (weapon.contains("wand"))) {
			return 404;
		}
		if (weapon.contains("godsword") || weapon.contains("saradomin sword") || weapon.contains("blessed sword")) {
			return GameType.isOsrs() ? 7056 : 7050;
		}
		if (weapon.contains("2h sword")) {
			return GameType.isOsrs() ? 410 : 7050;
		}
		if (shield.contains("shield") || shield.contains("buckler")) {
			SoundSystem.sendSound(player, 791, 0);
			player.soundSent = true;
			return 1156;
		}
		if (weapon.contains("staff") || weapon.contains("trident")) {
			return 420;
		}
		if (weapon.contains("boxing gloves")) {
			return 3679;
		}
		if (weapon.contains("scythe of vitur")) {
			return 8017;
		}
		if (weapon.contains("viggora's chainmace")) {
			return 7200;
		}
		if (GameType.isOsrs() && Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, player.getWieldedWeapon(), 0)) {
			return 7056;
		}

		if (weapon.contains("tentacle")) {
			SoundSystem.sendSound(player, 791, 0);
			player.soundSent = true;
			return 1659;
		}
		if (weapon.equals("elder maul")) {
			return 7517;
		}
		if (weapon.equals("dragon claws")) {
			return 397;
		}

		if (weapon.contains("granite maul")) {
			return 1666;
		}
		if (GameType.isOsrs()) {
			if (weapon.equals("heavy ballista") || weapon.equals("light ballista")) {
				return 7219;
			}
		}

		switch (player.getWieldedWeapon()) {
			case 18353:
				return 13054;
			case 15241:
				return 1666;

			// Zamorakian hasta
			case 11889:
				return 430;
			// Dinh's bulwark
			case 21015:
			case 16259:
				return 7512;

			case 12727: // Event rpg
				return 2324;

			case 20727: // Leaf-bladed battleaxe
				return 1156;

			case 20779: // Hunting blade
				return 378;

			// chinchompa
			case 10033:
			case 10034:
			case 11959:
				return 3176;
			case 1291:
			case 1293:
			case 1295:
			case 1297:
			case 1299:
			case 1301:
			case 1303:
			case 1305:
			case 6607:
			case 13474:
			case 13899:
			case 13901:
			case 13923:
			case 13925:
			case 13982:
			case 13984:
			case 16024:
			case 16025:
			case 16026:
			case 16027:
			case 16028:
			case 16029:
			case 16030:
			case 16031:
			case 16032:
			case 16033:
			case 16034:
			case 16383:
			case 16385:
			case 16387:
			case 16389:
			case 16391:
			case 16393:
			case 16395:
			case 16397:
			case 16399:
			case 16401:
			case 16403:
			case 16961:
			case 16963:
			case 18351:
			case 18352:
			case 18367:
			case 18368:
			case 1321:
			case 1323:
			case 1325:
			case 1327:
			case 1329:
			case 1331:
			case 1333:
			case 4587:
			case 20000:
			case 6611:
			case 13979:
			case 13981:
			case 14097:
			case 14287:
			case 14289:
			case 14291:
			case 14293:
			case 14295:
			case 746:
			case 747:
			case 1203:
			case 1205:
			case 1207:
			case 1209:
			case 1211:
			case 1213:
			case 1215:
			case 1217:
			case 1219:
			case 1221:
			case 1223:
			case 1225:
			case 1227:
			case 1229:
			case 1231:
			case 1233:
			case 1235:
			case 1813:
			case 5668:
			case 5670:
			case 5672:
			case 5674:
			case 5676:
			case 5678:
			case 5680:
			case 5682:
			case 5684:
			case 5686:
			case 5688:
			case 5690:
			case 5692:
			case 5694:
			case 5696:
			case 5698:
			case 5700:
			case 5702:
			case 6591:
			case 6593:
			case 6595:
			case 6597:
			case 8872:
			case 8873:
			case 8875:
			case 8877:
			case 8879:
			case 13976:
			case 13978:
			case 14297:
			case 14299:
			case 14301:
			case 14303:
			case 14305:
			case 15826:
			case 15848:
			case 15849:
			case 15850:
			case 15851:
			case 15853:
			case 15854:
			case 15855:
			case 15856:
			case 15857:
			case 15858:
			case 15859:
			case 15860:
			case 15861:
			case 15862:
			case 15863:
			case 15864:
			case 15865:
			case 15866:
			case 15867:
			case 15868:
			case 15869:
			case 15870:
			case 15871:
			case 15872:
			case 15873:
			case 15874:
			case 15875:
			case 15876:
			case 15877:
			case 15879:
			case 15880:
			case 15881:
			case 15882:
			case 15883:
			case 15884:
			case 15885:
			case 15886:
			case 15887:
			case 15888:
			case 15889:
			case 15890:
			case 15891:
			case 16757:
			case 16759:
			case 16761:
			case 16763:
			case 16765:
			case 16767:
			case 16769:
			case 16771:
			case 16773:
			case 16775:
			case 16777:
			case 16779:
			case 16781:
			case 16783:
			case 16785:
			case 16787:
			case 16789:
			case 16791:
			case 16793:
			case 16795:
			case 16797:
			case 16799:
			case 16801:
			case 16803:
			case 16805:
			case 16807:
			case 16809:
			case 16811:
			case 16813:
			case 16815:
			case 16817:
			case 16819:
			case 16821:
			case 16823:
			case 16825:
			case 16827:
			case 16829:
			case 16831:
			case 16833:
			case 16835:
			case 16837:
			case 16839:
			case 16841:
			case 16843:
			case 17275:
			case 17277:
			case 667:
			case 1277:
			case 1279:
			case 1281:
			case 1283:
			case 1285:
			case 1287:
			case 1289:
			case 19780:
			case 16935:
			case 16937:
			case 16939:
			case 16941:
			case 16943:
			case 16945:
			case 16947:
			case 16949:
			case 16951:
			case 16953:
			case 16955:
			case 16957:
			case 16959:
			case 18349:
			case 18350:
			case 18365:
			case 18366:
				return 404;


			case 13271: // Abyssal dagger.
				return 3295;

			case 1171:
			case 1173:
			case 1175:
			case 1177:
			case 1179:
			case 1181:
			case 1183:
			case 1185:
			case 1187:
			case 1189:
			case 1191:
			case 1193:
			case 1195:
			case 1197:
			case 1199:
			case 1201:
			case 1540:
			case 2589:
			case 2597:
			case 2603:
			case 2611:
			case 2621:
			case 2629:
			case 2659:
			case 2675:
			case 2890:
			case 3122:
			case 3488:
			case 3758:
			case 4156:
			case 4224:
			case 4226:
			case 4227:
			case 4228:
			case 4229:
			case 4230:
			case 4231:
			case 4232:
			case 4233:
			case 4234:
			case 4235:
			case 4507:
			case 4512:
			case 6215:
			case 6217:
			case 6219:
			case 6221:
			case 6223:
			case 6225:
			case 6227:
			case 6229:
			case 6231:
			case 6233:
			case 6235:
			case 6237:
			case 6239:
			case 6241:
			case 6243:
			case 6245:
			case 6247:
			case 6249:
			case 6251:
			case 6253:
			case 6255:
			case 6257:
			case 6259:
			case 6261:
			case 6263:
			case 6265:
			case 6267:
			case 6269:
			case 6271:
			case 6273:
			case 6275:
			case 6277:
			case 6279:
			case 6631:
			case 6633:
			case 6894:
			case 7332:
			case 7334:
			case 7336:
			case 7338:
			case 7340:
			case 7342:
			case 7344:
			case 7346:
			case 7348:
			case 7350:
			case 7352:
			case 7354:
			case 7356:
			case 7358:
			case 7360:
			case 7676:
			case 9731:
			case 10352:
			case 10665:
			case 10667:
			case 10669:
			case 10671:
			case 10673:
			case 10675:
			case 10677:
			case 10679:
			case 10827:
			case 11284:
			case 22002:
			case 12908:
			case 12910:
			case 12912:
			case 12914:
			case 12916:
			case 12918:
			case 12920:
			case 12922:
			case 12924:
			case 12926:
			case 12928:
			case 12930:
			case 12932:
			case 12934:
			case 13506:
			case 12829:
			case 12831:
			case 12825:
			case 16313:
			case 13740:
			case 12821:
			case 13964:
			case 13966:
			case 14578:
			case 14579:
			case 15808:
			case 15809:
			case 15810:
			case 15811:
			case 15812:
			case 15813:
			case 15814:
			case 15815:
			case 15816:
			case 15817:
			case 15818:
			case 16079:
			case 16933:
			case 16934:
			case 16971:
			case 16972:
			case 17341:
			case 17342:
			case 17343:
			case 17344:
			case 17345:
			case 17346:
			case 17347:
			case 17348:
			case 17349:
			case 17351:
			case 17353:
			case 17355:
			case 17357:
			case 17359:
			case 17361:
			case 17405:
			case 18359:
			case 18360:
			case 18361:
			case 18362:
			case 18363:
			case 18364:
			case 18582:
			case 18584:
			case 12335:
			case 12303:
			case 13231:
			case 19340:
			case 19345:
			case 19352:
			case 19410:
			case 19426:
			case 19427:
			case 19440:
			case 19441:
			case 19442:
			case 19749:
			case 4214: // Crystal bow full
			case 22550: // Craw's bow
			case 22547: // Craw's bow (u)
				SoundSystem.sendSound(player, 791, 0);
				player.soundSent = true;
				return 1156;
			case 13444:
			case 14661:
			case 21369:
			case 21372:
			case 21373:
			case 21374:
			case 21375:
			case 23691:
			case 4151: // Abyssal whip
			case 12773: // Volcanic abyssal whip
			case 12774: // Frozen abyssal whip
			case 21371:
			case 15_441:
			case 15_442:
			case 15_443:
			case 15_444:
				SoundSystem.sendSound(player, 791, 0);
				player.soundSent = true;
				return 1659;

			case 8844:
			case 8845:
			case 8846:
			case 8847:
			case 8848:
			case 8849:
			case 8850:
			case 15455:
			case 15456:
			case 15457:
			case 15458:
			case 15459:
			case 15825:
			case 17273:
			case 20072:
				SoundSystem.sendSound(player, 791, 0);
				player.soundSent = true;
				return 4177;

			case 3095:
			case 3096:
			case 3097:
			case 3098:
			case 3099:
			case 3100:
			case 3101:
			case 6587:
				return 397;

			case 1379:
			case 1381:
			case 1383:
			case 1385:
			case 1387:
			case 1389:
			case 1391:
			case 1393:
			case 1395:
			case 1397:
			case 1399:
			case 1401:
			case 1403:
			case 1405:
			case 1407:
			case 1409:
			case 2415:
			case 2416:
			case 2417:
			case 3053:
			case 3054:
			case 3055:
			case 3056:
			case 4170:
			case 4675:
			case 4710:
			case 4862:
			case 4863:
			case 4864:
			case 4865:
			case 4866:
			case 4867:
			case 6562:
			case 6603:
			case 6727:
			case 9084:
			case 9091:
			case 9092:
			case 9093:
			case 12795:
			case 12796:
			case 11739:
			case 11953:
			case 13406:
			case 13629:
			case 13630:
			case 13631:
			case 13632:
			case 13633:
			case 13634:
			case 13635:
			case 13636:
			case 13637:
			case 13638:
			case 13639:
			case 13640:
			case 13641:
			case 13642:
			case 6908:
			case 6910:
			case 6912:
			case 6914:
				return 415;
			// Boxing gloves
			case 7671:
			case 7673:
				return 3679;
			case 6528: // Tzhaar ket-om
			case 20756: // Hill giant club
				return 1666;
			case 1307:
			case 1309:
			case 1311:
			case 1313:
			case 1315:
			case 1317:
			case 1319:
			case 6609:
			case 7158:
			case 7407:
			case 16127:
			case 16128:
			case 16129:
			case 16130:
			case 16131:
			case 16132:
			case 16133:
			case 16134:
			case 16135:
			case 16889:
			case 16891:
			case 16893:
			case 16895:
			case 16897:
			case 16899:
			case 16901:
			case 16903:
			case 16905:
			case 16907:
			case 16909:
			case 16973:
			case 18369:
			case 20874:
			case 11802:
			case 20368: // Ags
			case 20374: // Zgs
			case 20372: // Sgs
			case 20370: // Bgs
			case 11804:
			case 11806:
			case 11808:
			case 11838:
			case 12808: // Sara's blessed sword.
				return 7056;

			case 3190:
			case 3192:
			case 3194:
			case 3196:
			case 3198:
			case 3200:
			case 3202:
			case 3204:
			case 6599:
				return 424;

			case 4718:
				return 424;

			case 10887:
				return 5866;

			case 4755:
				return 2063;

			// Zamorakian spear
			case 11824:
				return 1666;
			default:
				SoundSystem.sendSound(player, 816, 0);
				player.soundSent = true;
				return 424;
		}
	}

	/**
	 * Attack speed.
	 **/
	public static int getAttackTimerCount(Player player, String weaponName) {
		if (player.hasLastCastedMagic() && player.getSpellId() >= 0) {
			if (player.getSpellId() == 52) {
				return 4;
			}
			switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0]) {
				case 12871:
					// Ice blitz
				case 13023:
					// Shadow barrage
				case 12891:
					// Ice barrage
					return 5;
			}
			return 5;
		} else if (weaponName.contains("hunters' crossbow")) {
			if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				return 5;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 4;
			}
			if (player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
				return 3;
			}
		} else if (weaponName.contains("chinchompa")) {
			if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				return 5;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 4;
			}
			if (player.getCombatStyle(ServerConstants.AGGRESSIVE)) {
				return 3;
			}
		} else if (weaponName.contains("twisted bow")) {
			if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				return 6;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 6;
			}
			if (player.getCombatStyle(ServerConstants.RAPID)) {
				return 5;
			}
		} else if ((weaponName.contains("crossbow") || weaponName.contains("c'bow")) && !weaponName.contains("karil's crossbow")) {
			if (player.getCombatStyle(ServerConstants.LONG_RANGED)) {
				return 6;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 6;
			}
			if (player.getCombatStyle(ServerConstants.RAPID)) {
				return 5;
			}
		} else if (weaponName.contains("ballista")) {
			if (player.getCombatStyle(ServerConstants.LONG_RANGED)) {
				return 7;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 7;
			}
			if (player.getCombatStyle(ServerConstants.RAPID)) {
				return 6;
			}
		}
		else if (weaponName.contains("shortbow") && !weaponName.contains("dark bow")) {
			if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				return 4;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 4;
			}
			if (player.getCombatStyle(ServerConstants.RAPID)) {
				return 3;
			}
		}
		else if (weaponName.contains("longbow") || weaponName.contains("crystal bow")) {
			if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
				return 5;
			}
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 5;
			}
			if (player.getCombatStyle(ServerConstants.RAPID)) {
				return 4;
			}
		}

		if (WeaponSpeed.matching(WeaponSpeed.SPEED_2_TICKS, weaponName)) {
			if (player.getCombatStyle(ServerConstants.ACCURATE)) {
				return 3;
			} else if (player.getCombatStyle(ServerConstants.LONG_RANGED)) {
				return 3;
			} else {
				return 2;
			}
		}
		if (WeaponSpeed.matching(WeaponSpeed.SPEED_3_TICKS, weaponName)) {
			int speed = 3;
			if (player.getCombatStyle(ServerConstants.ACCURATE) || player.getCombatStyle(ServerConstants.LONG_RANGED)) {
				speed = 4;
			}
			if (player.getWieldedWeapon() == 12926) {
				if (player.getPlayerIdAttacking() > 0) {
				} else if (player.getNpcIdAttacking() > 0) {
					speed--;
				}
			}
			return speed;
		}
		if (WeaponSpeed.matching(WeaponSpeed.SPEED_7_TICKS, weaponName)) {
			return 7;
		}
		if (WeaponSpeed.matching(WeaponSpeed.SPEED_5_TICKS, weaponName)) {
			return 5;
		}
		if (WeaponSpeed.matching(WeaponSpeed.SPEED_6_TICKS, weaponName)) {
			return 6;
		}
		if (WeaponSpeed.matching(WeaponSpeed.SPEED_4_TICKS, weaponName)) {
			return 4;
		}
		if (WeaponSpeed.matching(WeaponSpeed.SPEED_8_TICKS, weaponName)) {
			return 8;
		}
		return 5;
	}

	/**
	 * How many game ticks it takes for the hitsplat to be applied after animation is applied.
	 **/
	public static int getHitDelay(Player player, String weaponName) {
		weaponName = weaponName.toLowerCase();
		if (player.hasLastCastedMagic() && player.getSpellId() >= 0) {
			int distance = 0;
			Player target = PlayerHandler.players[player.getPlayerIdAttacking()];
			if (target != null) {
				int distanceFromTarget = player.getPA().distanceToPoint(target.getX(), target.getY());
				if (distanceFromTarget >= 2) {
					distance += 2;
				}
				if (distanceFromTarget >= 5) {
					distance++;
				}
				if (distanceFromTarget >= 8) {
					distance++;
				}
			} else {
				Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];

				if (npc != null) {
					// Trident of the swamp.
					if (player.getWieldedWeapon() == 12899) {
						return 4;
					}

					int distanceFromTarget = player.getPA().distanceToPoint(npc.getX(), npc.getY());
					if (distanceFromTarget >= 2) {
						distance += 2;
					}
					if (distanceFromTarget >= 5) {
						distance++;
					}
					if (distanceFromTarget >= 8) {
						distance++;
					}
				}
			}
			distance += 3;

			// Blitz.
			if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12871) {
				distance++;
			}
			if (distance > 7) {
				distance = 7;
			}
			return distance;
		} else {
			if (RangedData.isRangedItem(player, player.getWieldedWeapon())) {
				int distance = 0;
				Player target = PlayerHandler.players[player.getPlayerIdAttacking()];
				if (target != null) {
					int distanceFromTarget = player.getPA().distanceToPoint(target.getX(), target.getY());
					if (distanceFromTarget >= 3) {
						distance++;
					}

				} else {
					Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];
					if (npc != null) {
						int distanceFromTarget = player.getPA().distanceToPoint(npc.getX(), npc.getY());
						if (distanceFromTarget >= 3) {
							distance++;
						}
					}
				}
				int distanceFinal = distance + 4;
				if (distanceFinal >= 5 && RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(player)) {
					distanceFinal = 4;
				}
				return distanceFinal;
			} else {
				return 3;
			}
		}
	}

	public static int getRequiredDistance(Player player) {
		return 1 + (MeleeData.usingHalberd(player) ? 1 : 0);
	}

	public static int getRangeStartGFX(Player player) {
		switch (player.getDroppedRangedWeaponUsed()) {
			case 863:
				return 220;
			case 864:
				return 219;
			case 865:
				return 221;
			case 866:
				// knives
				return 223;
			case 867:
				return 224;
			case 868:
				return 225;
			case 869:
				return 222;
			case 806:
				return 232;
			case 807:
				return 233;
			case 808:
				return 234;
			case 809:
				// darts
				return 235;

			case 11230:
				return 237;
			case 810:
				return 236;
			case 811:
				return 237;
			case 825:
				return 206;
			case 826:
				return 207;
			case 827:
				// javelin
				return 208;
			case 828:
				return 209;
			case 829:
				return 210;
			case 830:
				return 211;
			case 800:
				return 42;
			case 801:
				return 43;
			case 802:
				return 44; // axes
			case 803:
				return 45;
			case 804:
				return 46;
			case 805:
				return 48;
			case 882:
				return 19;
			case 884:
				return 18;
			case 886:
				return 20;
			case 888:
				return 21;
			case 890:
				return 22;
			case 892:
				return 24;
			case 21326:
				return 1385;
			case 11212:
				return 26;
			case 15241:
				return 2138;

			// Crystal bow full
			case 4214:
				return 250;

			case 22634: // Morrigan's throwing axe
				return 1624;

			case 22636: // Morrigain's javelin
				return 1619;

			case 22550: // Craw's bow
			case 22547: // Craw's bow (u)
				return 1611;

			case 20849: // Dragon thrownaxe
				return 1320;
		}
		return -1;
	}

	public static int getRangeProjectileGFX(Player player) {
		if (!player.dragonThrownaxeSpecialUsed) {
			if (player.blowpipeSpecialAttack) {
				return 1043;
			}
			if (player.isUsingDarkBowSpecialAttack()) {
				return 1099;
			}
		}
		if (player.isMagicBowSpecialAttack()) {
					return 249;
		}
		if (GameType.isPreEoc()) {
			switch (player.getWieldedWeapon()) {
				case 13_879:
					return 1837;
				case 13_883:
					return 1839;
			}
		}
		switch (player.getWieldedWeapon()) {

			// Dragon thrownaxe
			case 20849:
				if (player.dragonThrownaxeSpecialUsed) {
					return 1318;
				} else {
					return 1319;
				}
				// Crystal bow
			case 4214:
				return 249;
			case 22550: // Craw's bow
			case 22547: // Craw's bow (u)
				return 1574;
			case 22634: // Morrigan's throwing axe
				return player.getAttributes().getOrDefault(Player.MORRIGANS_AXE_SPECIAL) ? 1625 : 1623;
			case 22636: // Morrigain's javelin
				return player.getAttributes().getOrDefault(Player.MORRIGANS_JAVS_SPECIAL) ? 1622 : 1620;
		}

		// Armadyl crossbow.
		if (ItemAssistant.getItemName(player.getWieldedWeapon()).equals("Armadyl crossbow") && player.armadylCrossbowSpecial) {
			return 301;
		}
		// Dragon crossbow.
		if (player.getWieldedWeapon() == 21902 && player.dragonCrossbowSpecial) {
			return 301;
		}
		if (Combat.getUsingCrossBow(player)) {
			return 27;
		}
		if (player.getWieldedWeapon() == 10033) //grey chin
		{
			return 908;
		}
		if (player.getWieldedWeapon() == 10034) //red chin
		{
			return 909;
		}
		if (player.getWieldedWeapon() == 11959) //black chin
		{
			return 1272;
		}
		switch (player.getDroppedRangedWeaponUsed()) {
			case 15241:
				// Hand cannon.
			case 15243:
				// Hand cannon shot.
				return 2143;
			case 19484:// Dragon javelin
				return 1301;
			case 863:
				return 213;
			case 864:
				return 212;
			case 865:
				return 214;
			case 866:
				// knives
				return 216;
			case 867:
				return 217;
			case 868:
				return 218;
			case 869:
				return 215;
			case 806:
				return 226;
			case 807:
				return 227;
			case 808:
				return 228;
			case 809:
				// darts
				return 229;

			case 11230:
			case 12926:
				return 231;
			case 810:
				return 230;
			case 811:
				return 231;
			case 825:
				return 200;
			case 826:
				return 201;
			case 827:
				// javelin
				return 202;
			case 828:
				return 203;
			case 829:
				return 204;
			case 830:
				return 205;
			case 6522:
				// Toktz-xil-ul
				return 442;
			case 800:
				return 36;
			case 801:
				return 35;
			case 802:
				return 37; // axes
			case 803:
				return 38;
			case 804:
				return 39;
			case 805:
				return 40;
			case 882:
				return 10;
			case 884:
				return 9;
			case 886:
				return 11;
			case 888:
				return 12;
			case 890:
				return 13;
			case 892:
				return 15;
			case 21326:
				return 1384;
			case 11212:
				return 17;
			case 4740:
				// bolt rack
				return 27;
			//return 1622;
		}
		return -1;
	}

	public static int getProjectileSpeed(Player player) {
		int speed = 60;
		Player target = PlayerHandler.players[player.getPlayerIdAttacking()];
		if (target != null) {
			int distanceFromTarget = player.getPA().distanceToPoint(target.getX(), target.getY());
			if (distanceFromTarget >= 2) {
				speed += 7;
			}
			if (distanceFromTarget >= 5) {
				speed += 12;
			}
			if (distanceFromTarget >= 8) {
				speed += 17;
			}

		} else {
			Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];
			if (npc != null) {
				int distanceFromTarget = player.getPA().distanceToPoint(npc.getX(), npc.getY());
				if (distanceFromTarget >= 2) {
					speed += 7;
				}
				if (distanceFromTarget >= 5) {
					speed += 12;
				}
				if (distanceFromTarget >= 8) {
					speed += 17;
				}
			}
		}
		if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("Dark bow")) {
			speed += 10;
		}
		if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("Craw's bow")) {
			speed -= 10;
		}
		if (ItemAssistant.getItemName(player.getWieldedWeapon()).contains("throwing axe") || ItemAssistant.getItemName(player.getWieldedWeapon()).contains("thrown axe") || ItemAssistant.getItemName(player.getWieldedWeapon()).contains("knife") || ItemAssistant.getItemName(player.getWieldedWeapon()).contains("blowpipe") || ItemAssistant.getItemName(player.getWieldedWeapon()).contains("dart")) {
			speed -= 10;
		}
		if (player.armadylCrossbowSpecial || player.dragonCrossbowSpecial) {
			speed -= 5;
		}
		if (player.isMagicBowSpecialAttack()) {
			speed -= 5;
		}
		if (CombatConstants.isChinchompa(player.getWieldedWeapon())) {
			speed -= 10;
		}
		//lower = faster
		return speed;
	}

	public static boolean wearingStaff(Player player, int runeId) {
		int wep = player.getWieldedWeapon();
		switch (runeId) {
			case 554:
				if (wep == 1387)
					return true;
				break;
			case 555:
				if (wep == 1383)
					return true;
				break;
			case 556:
				if (wep == 1381)
					return true;
				break;
			case 557:
				if (wep == 1385)
					return true;
				break;
		}
		return false;
	}

	public static boolean hasTome(Player player, int runeId) {
		boolean hasTomeEquipped = player.playerEquipment[ServerConstants.SHIELD_SLOT] == 20714;
		switch (runeId) {
			case 554:
				if (hasTomeEquipped)
					return true;
		}
		return false;
	}

	public static void addBarragesCasted(Player player) {
		if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 12891) {
			player.barragesCasted++;
			Achievements.checkCompletionMultiple(player, "1076");
		}

		// Fire strike.
		else if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1158) {
			Achievements.checkCompletionSingle(player, 1019);
		}
	}

	public static boolean checkMagicRequirementsForNpcCombatAndMagicOnFloorItemPacket(Player player, int spell) {
		if (player.hasLastCastedMagic() && spell >= 0) { // check for runes
			if (!MagicData.requiredRunes(player, spell, "CHECK REQUIREMENT")) {
				player.playerAssistant.sendMessage("You don't have the required runes to cast this spell.");
				return false;
			}
		}
		if (player.hasLastCastedMagic() && player.getPlayerIdAttacking() > 0) {
			if (PlayerHandler.players[player.getPlayerIdAttacking()] != null) {
				for (int r = 0; r < CombatConstants.REDUCE_SPELLS.length; r++) { // reducing spells, confuse etc
					if (CombatConstants.REDUCE_SPELLS[r] == CombatConstants.MAGIC_SPELLS[spell][0]) {
						player.reduceSpellId = r;
						if ((System.currentTimeMillis() - PlayerHandler.players[player.getPlayerIdAttacking()].reduceSpellDelay[player.reduceSpellId])
						    > CombatConstants.REDUCE_SPELL_TIME[player.reduceSpellId]) {
							PlayerHandler.players[player.getPlayerIdAttacking()].canUseReducingSpell[player.reduceSpellId] = true;
						} else {
							PlayerHandler.players[player.getPlayerIdAttacking()].canUseReducingSpell[player.reduceSpellId] = false;
						}
						break;
					}
				}
				if (!PlayerHandler.players[player.getPlayerIdAttacking()].canUseReducingSpell[player.reduceSpellId]) {
					player.playerAssistant.sendMessage("That player is currently immune to this spell.");
					Movement.stopMovement(player);
					player.setLastCastedMagic(false);
					resetPlayerAttack(player);
					return false;
				}
			}
		}
		int staffRequired = MagicData.getStaffNeeded(player, spell);
		if (player.hasLastCastedMagic() && staffRequired > 0) {
			if (player.getWieldedWeapon() != staffRequired) {
				player.playerAssistant.sendMessage("You need a " + ItemAssistant.getItemName(staffRequired).toLowerCase() + " to cast this spell.");
				return false;
			}
		}
		if (spell >= 0) {
			if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < CombatConstants.MAGIC_SPELLS[spell][1]) {
				player.playerAssistant.sendMessage("You need to have a magic level of " + CombatConstants.MAGIC_SPELLS[spell][1] + " to cast this spell.");
				return false;
			}
		}
		if (player.hasLastCastedMagic()) {
			if (spell == 25) { // crumble undead
				if (IntStream.of(ServerConstants.UNDEAD_NPCS_OSRS).noneMatch(value -> value == NpcHandler.npcs[player.getNpcIdAttacking()].npcType)) {
					player.playerAssistant.sendMessage("You can only attack undead monsters with this spell.");
					Movement.stopMovement(player);
					AutoCast.resetAutocast(player, true);
					return false;
				}
			}
		}


		return true;
	}

	public static boolean isModernSpellbookBindSpell(Player player, int spellId) {
		switch (CombatConstants.MAGIC_SPELLS[spellId][0]) {
			case 1572: // Bind
			case 1582: // Snare
			case 1592: // Entangle
				return true;
		}
		return false;
	}

	public static long getFreezeTime(Player player) {
		switch (CombatConstants.MAGIC_SPELLS[player.getOldSpellId()][0]) {
			case 1572: // Bind
			case 12861:
				// ice rush
				return 5000;
			case 1582: // Snare
			case 12881:
				// ice burst
				return 10000;
			case 1592: // Entangle
			case 12871:
				// ice blitz
				return 15000;
			case 12891:
				// ice barrage
				return 20000;
			default:
				return 0;
		}
	}

	/**
	 * Used to send sprite id to the client to show the barrage/blitz/entangle timer.
	 */
	public static int getFreezeSpriteId(int spell) {
		switch (spell) {
			case 1572: // Bind
				return 738;
			case 12861:// ice rush

				return 735;
			case 1582: // Snare
				return 737;
			case 12881:// ice burst

				return 734;
			case 1592: // Entangle
				return 736;
			case 12871: // ice blitz

				return 733;
			case 12891: // ice barrage

				return 711;
			default:
				return 0;
		}
	}

	public static int getStartHeight(Player player) {

		if (player.getSpellId() == 52) {
			return 10;
		}
		switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0]) {
			case 1562:
				// stun
				return 25;
			case 12939:
				// smoke rush
				return 35;
			case 12987:
				// shadow rush
				return 38;
			case 12861:
				// ice rush
				return 15;
			case 12951:
				// smoke blitz
				return 38;
			case 12999:
				// shadow blitz
				return 25;
			case 12911:
				// blood blitz
				return 25;
			default:
				return 43;
		}
	}

	public static int getEndHeight(Player player) {


		if (player.getSpellId() == 52) {
			return 7;
		}
		switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0]) {
			case 1562:
				// stun
				return 10;
			case 12939:
				// smoke rush
				return 20;
			case 12987:
				// shadow rush
				return 28;
			case 12861:
				// ice rush
				return 10;
			case 12951:
				// smoke blitz
				return 28;
			case 12999:
				// shadow blitz
				return 15;
			case 12911:
				return 10; // blood blitz
			default:
				return 31;
		}
	}

	public static int getStartDelay(Player player) {
		if (player.getSpellId() >= 0) {
			switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0]) {
				case 1539:
					return 60;
				default:
					return 51;
			}
		}
		return 40;
	}

	public static int getEndGfxHeight(Player player, int spell) {
		switch (spell) {
			case 12987:
			case 12901:
			case 12861:
			case 12445:
			case 1192:
			case 13011:
			case 12919:
			case 12881:
			case 12999:
			case 12911:
			case 12871:
			case 13023:
			case 12929:
			case 12891:
			case 32600:
			case 32640:
				return 0;
			default:
				return 100;
		}
	}

	public static int getStartGfxHeight(Player player) {
		switch (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0]) {
			case 12871:
			case 12891:
			case 12445:
			case 32600:
			case 32620:
			case 32640:
				return 0;
			default:
				return 100;
		}
	}

	public static void handleDfs(final Player attacker) {
		final Player victim = PlayerHandler.players[attacker.getPlayerIdAttacking()];
		if (victim == null) {
			return;
		}
		if (victim.getDead()) {
			return;
		}
		if (attacker.getDead()) {
			return;
		}
		// The dragonfire attack can be used at range (up to 10 squares away) and over obstacles
		if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), victim.getX(),
				victim.getY(), 10)) {
			return;
		}
		if (System.currentTimeMillis() - attacker.dfsDelay > 120000) {
			int maximum = 25;
			int damage = Misc.random(maximum);
			int anti = Combat.antiFire(victim, false, false);
			if (anti >= 1) {
				damage = Misc.random(7);
			}
			final int damage1 = damage;
			attacker.startAnimation(6696);
			attacker.gfx0(1165);

			final int pX = attacker.getX();
			final int pY = attacker.getY();
			int oX = victim.getX();
			int oY = victim.getY();
			final int offX = (pY - oY) * -1;
			final int offY = (pX - oX) * -1;
			AttackPlayer.successfulAttackInitiated(attacker, victim);
			attacker.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 75, 1166, 37, 37, -victim.getPlayerId() - 1, 53, 16, attacker.getHeight());
			attacker.dfsDelay = System.currentTimeMillis();
			ItemAssistant.calculateEquipmentBonuses(attacker);
			OverlayTimer.sendOverlayTimer(attacker, ServerConstants.OVERLAY_TIMER_SPRITE_DRAGON_FIRE_SHIELD_SPECIAL, 120);
			attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, damage, 3, EntityDamageType.DRAGON_FIRE, maximum, false, false));
		} else {
			attacker.playerAssistant.sendMessage("My shield hasn't finished recharging yet.");
		}
	}

	public static void clickGraniteMaulSpecial(Player attacker) {
		if (attacker.dragonSpearEvent) {
			return;
		}

		if (!hasGraniteMaulEquipped(attacker)) {
			return;
		}

		if (attacker.duelRule[10] && attacker.getDuelStatus() == 5) {
			attacker.playerAssistant.sendMessage("Special attacks have been disabled during this duel!");
			attacker.setUsingSpecialAttack(false);
			CombatInterface.updateSpecialBar(attacker);
			Combat.resetPlayerAttack(attacker);
			return;
		}
		attacker.setUsingRanged(false);
		attacker.setLastCastedMagic(false);
		attacker.graniteMaulSpecialAttackClicks++;
		attacker.setUsingSpecialAttack(true);
		if (!attacker.lastPlayerAttackedName.isEmpty() && System.currentTimeMillis() - attacker.timeAttackedAnotherPlayer <= 4000 && attacker.hasLastAttackedAPlayer) {
			Player other = Misc.getPlayerByName(attacker.lastPlayerAttackedName);
			if (other != null) {
				if (attacker.getPA().withinDistanceOfTargetPlayer(other, 2)) {
					attacker.setPlayerIdAttacking(other.getPlayerId());
				}
			}
		}
	}

	/**
	 * @return true, if player is using a Chaotic weapon.
	 */
	public static boolean usingChaotic(Player player) {
		int ID = player.getWieldedWeapon();

		if (ID >= 18349 && ID <= 18363) {
			return true;
		}

		return false;


	}

	public static boolean stopMovement(Player player, Player target, boolean follow) {
		if (player.playerAssistant.isOnTopOfTarget(target)) {
			return false;
		}

		boolean unblocked = Region.isStraightPathUnblockedProjectiles(player.getX(), player.getY(), target.getX(), target.getY(), player.getHeight(), 1, 1, true);
		// Outside getPlayerIdAttack() > 0 because that field gets reset instantly after magic is casted.
		if (player.hasLastCastedMagic() && player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.MAGIC_FOLLOW_DISTANCE) && unblocked) {
			Movement.stopMovement(player);
			return true;
		}
		if (player.getPlayerIdAttacking() > 0 && unblocked) {
			if (player.isUsingMediumRangeRangedWeapon() || player.isWieldingRangedWeaponWithNoArrowSlotRequirement()) {
				if (player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.getRangedWeaponDistance(player))) {
					Movement.stopMovement(player);
					return true;
				}
			}
			if (MeleeData.usingHalberd(player) && player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.HALBERD_DISTANCE) && (player.getX() == target.getX()
			                                                                                                                                        || player.getY()
			                                                                                                                                           == target.getY())) {
				Movement.stopMovement(player);
				return true;
			}
		}

		// Snowball.
		if (player.getPlayerIdAttacking() > 0 && ItemAssistant.hasItemEquippedSlot(player, 10501, ServerConstants.WEAPON_SLOT)) {
			if (player.playerAssistant.withinDistanceOfTargetPlayer(target, 8)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * This is to fix the issue where you can stand on a player and attack, then walk on the player again and the
	 * player movement won't update.
	 *
	 * @param player The associated player.
	 */
	public static void preMovementCombatFix(Player player) {
		if ((player.getPlayerIdAttacking() > 0) && player.getAttackTimer() <= 1) {
			Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
			if (victim == null) {
				return;
			}
			if (player.playerAssistant.isOnTopOfTarget(victim)) {
				Movement.movePlayerFromUnderEntity(player);
			}
		}

	}

	public static boolean staffOfTheDeadSpecial(Player player) {
		if (!wearingStaffOfTheDead(player)) {
			return false;
		}

		if (player.duelRule[TradeAndDuel.NO_MAGIC] && player.getDuelStatus() == 5) {
			player.playerAssistant.sendMessage("Magic is not allowed in this duel!");
			player.setUsingSpecialAttack(false);
			CombatInterface.updateSpecialBar(player);
			Combat.resetPlayerAttack(player);
			return true;
		}
		if (player.duelRule[TradeAndDuel.NO_SPECIAL_ATTACK] && player.getDuelStatus() == 5) {
			player.playerAssistant.sendMessage("Special attacks have been disabled during this duel!");
			player.setUsingSpecialAttack(false);
			CombatInterface.updateSpecialBar(player);
			Combat.resetPlayerAttack(player);
			return true;
		}
		player.setUsingSpecialAttack(false);
		if (player.dragonSpearEvent) {
			return true;
		}
		if (!checkSpecAmount(player, player.getWieldedWeapon())) {
			Combat.notEnoughSpecialLeft(player);
			return true;
		}
		int animation = player.getWieldedWeapon() == 11791 ? 7083 : 1720;
		if (player.getWieldedWeapon() == 16209) {
			animation = 11052;
		}
		if (player.getWieldedWeapon() == 16272) {
			animation = 11055;
		}
		if (player.getWieldedWeapon() == 22296) {
			animation = 7967;
		}
		if (player.getWieldedWeapon() == 15486 && GameType.isPreEoc()) {
			animation = 12804;
		}
		player.startAnimation(animation);
		player.timeStaffOfTheDeadSpecialUsed = System.currentTimeMillis();
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (player.getWieldedWeapon() == 15486 && GameType.isPreEoc()) {
					player.gfx0(2319);
				} else {
					player.gfx(1228, 400);
				}
			}
		}, player.getWieldedWeapon() == 15486 && GameType.isPreEoc() ? 1 : 2);
		return true;
	}

	public static boolean wearingStaffOfTheDead(Player player) {
		if (player.getWieldedWeapon() == 11791 || player.getWieldedWeapon() == 12904 || player.getWieldedWeapon() == 16209 || player.getWieldedWeapon() == 16272
				|| player.getWieldedWeapon() == 22296
				|| (player.getWieldedWeapon() == 15486 && GameType.isPreEoc())) {
			return true;
		}
		return false;
	}

	public static void saradominGodswordSpecialAttack(Player attacker) {
		if (attacker.getWieldedWeapon() == (GameType.isOsrs() ? 11806 : 11698) || attacker.getWieldedWeapon() == 20372) {
			int damage = attacker.meleeFirstDamage;
			if (damage > 0) {
				int heal = damage / 2;
				if (heal < 10) {
					heal = 10;
				}
				if (attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < attacker.getBaseHitPointsLevel()) {
					if (heal + attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > attacker.getBaseHitPointsLevel()) {
						heal = attacker.getBaseHitPointsLevel() - attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
					}
					attacker.addToHitPoints(heal);
				}


				int prayer = damage / 4;
				if (prayer < 5) {
					prayer = 5;
				}
				if (attacker.getCurrentCombatSkillLevel(ServerConstants.PRAYER) < attacker.getBasePrayerLevel()) {
					if (prayer + attacker.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > attacker.getBasePrayerLevel()) {
						prayer = attacker.getBasePrayerLevel() - attacker.getCurrentCombatSkillLevel(ServerConstants.PRAYER);
					}
					attacker.currentCombatSkillLevel[ServerConstants.PRAYER] += prayer;
					Skilling.updateSkillTabFrontTextMain(attacker, ServerConstants.PRAYER);
				}
			}
		}

	}

	public static boolean hasGraniteMaulEquipped(Player attacker) {
		if (ItemAssistant.getItemName(attacker.getWieldedWeapon()).contains("Granite maul")) {
			return true;
		}
		return false;
	}

	public static void autoRetaliate(Player victim, Player attacker) {
		if (victim.getPlayerIdAttacking() <= 0 && victim.getNpcIdAttacking() <= 0) {
			if (victim.getAutoRetaliate() == 1 && !victim.isMoving() && !victim.doingAnAction()) {
				if (!victim.isCombatBot() && !victim.getBotStatus().equals("LOOTING")) {
					victim.setLastCastedMagic(false);
					victim.setPlayerIdAttacking(attacker.getPlayerId());
					victim.resetNpcIdToFollow();
					victim.setPlayerIdToFollow(attacker.getPlayerId());
					if (RangedData.isWieldingMediumRangeRangedWeapon(victim) || RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(victim)) {
						victim.setMeleeFollow(false);
					} else {
						victim.setMeleeFollow(true);
					}
				} else if (victim.isCombatBot() && victim.getBotStatus().equals("LOOTING")) {
					if (victim.botEarlyRetreat) {
						return;
					}
					victim.botEarlyRetreat = true;
					BotContent.walkToBankArea(victim);
					BotContent.regearStage1Event(victim);
					BotContent.switchOffPrayerEventWhenOutsideWild(victim);
					if (System.currentTimeMillis() - victim.timeAttackedAnotherPlayer >= 4000) {
						BotCommunication.sendBotMessage(victim, "?", false);
					}
				}
				if (victim.getBotStatus().equals("LOOTING")) {
					BotContent.addBotDebug(victim, "Is looting, cannot auto retaliate.");
				}
			}
		}
	}

	public static void performBlockAnimation(Player victim, Player attacker) {
		if ((victim.getAttackTimer() <= 3 || victim.getAttackTimer() == 0 && victim.getPlayerIdAttacking() == 0 && !attacker.getDoingAgility()) && System.currentTimeMillis() - victim.lastAttackAnimationTimer >= 1800) {
				victim.startAnimation(Combat.getBlockAnimation(victim));
				if (!victim.soundSent) {
					SoundSystem.sendSound(victim, attacker, 511, 450);
				}
			}

	}

	public static void notEnoughSpecialLeft(Player attacker) {
		if (attacker.getWieldedWeapon() == 13_879) {
			return;
		}
		attacker.playerAssistant.sendMessage("You don't have the required special energy to use this attack.");
		attacker.setUsingSpecialAttack(false);
		attacker.botUsedSpecialAttack = false;
		CombatInterface.updateSpecialBar(attacker);
		Combat.resetPlayerAttack(attacker);
	}

	public static void magicOnHitsplatReset(Player attacker) {
		attacker.setUsingMagic(false);
		attacker.setOldSpellId(0);
		if (attacker.getSpellId() == -1 && attacker.getPlayerIdAttacking() == 0) {
			attacker.resetFaceUpdate();
			if (attacker.hasLastCastedMagic()) {
				attacker.resetPlayerIdToFollow();
			}
		}
	}

	public static void setAttackTimer(Player attacker) {
		attacker.setAttackTimer(Combat.getAttackTimerCount(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
	}
}
