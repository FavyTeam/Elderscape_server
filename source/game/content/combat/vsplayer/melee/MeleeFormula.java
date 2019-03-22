package game.content.combat.vsplayer.melee;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.DominionGloveEffect;
import game.content.combat.effect.SaveDamage;
import game.content.combat.vsplayer.Effects;
import game.content.music.SoundSystem;
import game.content.skilling.summoning.Summoning;
import game.player.Player;
import utility.Misc;

/**
 * Melee Formulas.
 *
 * @author MGT Madness, created on 20-11-2013.
 */
public class MeleeFormula {

	/**
	 * @param player The associated player.
	 * @return The player's invisible attack advantage.
	 */
	public static int getInvisibleMeleeAttackAdvantage(Player player) {
		double advantage = 10; // Base
		double equipmentMultiplier = 1.0;
		double currentLevel = player.getCurrentCombatSkillLevel(ServerConstants.ATTACK);
		double attackBonus = player.playerBonus[getHighestMeleeAttackBonus(player)] * equipmentMultiplier;
		double prayerMultiplier = getMeleePrayerAttackMultiplier(player);
		advantage += currentLevel;
		advantage += attackBonus;
		advantage *= prayerMultiplier;
		if (!player.isGraniteMaulSpecial) {
			advantage *= player.getSpecialAttackAccuracyMultiplier();
			if (Combat.wearingFullObsidianArmour(player)) {
				int TOKTZ_XIL_AK = 6523; // Obsidian sword.
				int TOKTZ_XIL_EK = 6525; // Obsidian dagger.
				int TZHAAR_KET_OM = 6528; // Obby maul.

				if (player.getWieldedWeapon() == TZHAAR_KET_OM || player.getWieldedWeapon() == TOKTZ_XIL_EK || player.getWieldedWeapon() == TOKTZ_XIL_AK) {
					advantage *= 1.1;
				}
			}
		}
		if (Combat.wearingFullVoidMelee(player)) {
			advantage *= 1.1;
		}
		if (player.getCombatStyle(ServerConstants.ACCURATE)) {
			advantage *= 1.02;
		}
		if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
			advantage *= 1.01;
		}
		if (GameType.isPreEoc()) {
			double attack = Summoning.getSkillBonus(player, ServerConstants.ATTACK);
			if (attack > 1.0) {
				advantage *= attack;
			}
		}
		double finalMultiplier = 1.5;
		advantage *= finalMultiplier;
		advantage *= 2;
		return (int) advantage;

	}

	/**
	 * @param player The associated player.
	 * @return The player's invisible defence advantage.
	 */
	public static int getInvisibleMeleeDefenceAdvantage(Player player) {
		double advantage = 10; // Base
		double equipmentMultiplier = 1.0;
		double currentLevel = player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE);
		double defenceBonus = player.playerBonus[getHighestMeleeDefenceBonus(player)] * equipmentMultiplier;
		double prayerMultiplier = getMeleePrayerDefenceMultiplier(player);
		double effectiveDefenceBonus = 0.5;
		effectiveDefenceBonus += 0.004237 * currentLevel;
		advantage += (defenceBonus * effectiveDefenceBonus);
		advantage *= prayerMultiplier;
		double finalMultiplier = 1.65;
		advantage *= finalMultiplier;
		advantage += currentLevel;
		if (player.getCombatStyle(ServerConstants.LONG_RANGED) || player.getCombatStyle(ServerConstants.DEFENSIVE)) {
			advantage *= 1.02;
		}
		if (player.getCombatStyle(ServerConstants.CONTROLLED)) {
			advantage *= 1.01;
		}
		advantage *= 2;
		if (GameType.isPreEoc()) {
			double def = Summoning.getSkillBonus(player, ServerConstants.DEFENCE);
			if (def > 1.0) {
				advantage *= def;
			}
		}
		if (Combat.isClawsOfGuthixEffected(player)) {
			advantage *= 0.95;
		}
		return (int) advantage;
	}

	/**
	 * Get the prayer defence multiplier.
	 *
	 * @param player The associated player.
	 * @return The prayer defence multiplier.
	 */
	public static double getMeleePrayerDefenceMultiplier(Player player) {
		if (player.prayerActive[ServerConstants.THICK_SKIN]) {
			return 1.05;
		} else if (player.prayerActive[ServerConstants.ROCK_SKIN]) {
			return 1.1;
		} else if (player.prayerActive[ServerConstants.STEEL_SKIN]) {
			return 1.15;
		} else if (player.prayerActive[ServerConstants.CHIVALRY]) {
			return 1.2;
		} else if (player.prayerActive[ServerConstants.PIETY]) {
			return 1.25;
		} else if (player.prayerActive[ServerConstants.RIGOUR]) {
			return 1.25;
		} else if (player.prayerActive[ServerConstants.AUGURY]) {
			return 1.25;
		}
		return 1.0;
	}

	/**
	 * Get the prayer attack multiplier.
	 *
	 * @param player The associated player.
	 * @return The prayer attack multiplier.
	 */
	private static double getMeleePrayerAttackMultiplier(Player player) {
		if (player.prayerActive[ServerConstants.CLARITY_OF_THOUGHT]) {
			return 1.05;
		} else if (player.prayerActive[ServerConstants.IMPROVED_REFLEXES]) {
			return 1.10;
		} else if (player.prayerActive[ServerConstants.INCREDIBLE_REFLEXES]) {
			return 1.15;
		} else if (player.prayerActive[ServerConstants.CHIVALRY]) {
			return 1.15;
		} else if (player.prayerActive[ServerConstants.PIETY]) {
			return 1.2;
		}
		return 1.0;
	}


	/**
	 * Get the highest attack bonus.
	 *
	 * @param player The assocaited player.
	 * @return The highest attack bonus.
	 */
	private static int getHighestMeleeAttackBonus(Player player) {
		if (player.playerBonus[0] > player.playerBonus[1] && player.playerBonus[0] > player.playerBonus[2]) {
			return 0;
		}
		if (player.playerBonus[1] > player.playerBonus[0] && player.playerBonus[1] > player.playerBonus[2]) {
			return 1;
		}
		return player.playerBonus[2] <= player.playerBonus[1] || player.playerBonus[2] <= player.playerBonus[0] ? 0 : 2;
	}

	/**
	 * Get the highest defence bonus.
	 *
	 * @param player Player
	 * @return The highest defence bonus.
	 */
	public static int getHighestMeleeDefenceBonus(Player player) {
		int index7 = player.playerBonus[7];
		int index6 = player.playerBonus[6];
		int index5 = player.playerBonus[5];

		if (index5 > index6 && index5 > index7) {
			return 5;
		}
		if (index6 > index5 && index6 > index7) {
			return 6;
		}
		return index7 <= index5 || index7 <= index6 ? 5 : 7;
	}

	/**
	 * Get the maximum melee damage.
	 *
	 * @param player The associated player.
	 * @return The maximum melee damage.
	 */
	public static int getMaximumMeleeDamage(Player player) {
		double prayerBoost = 0;
		if (player.prayerActive[1]) {
			prayerBoost += 0.05;
		} else if (player.prayerActive[6]) {
			prayerBoost += 0.10;
		} else if (player.prayerActive[14]) {
			prayerBoost += 0.15;
		} else if (player.prayerActive[24]) {
			prayerBoost += 0.18;
		} else if (player.prayerActive[25]) {
			prayerBoost += 0.23;
		}
		double visible_strength = player.getCurrentCombatSkillLevel(ServerConstants.STRENGTH);
		double prayer_multiplier = (1.00 + prayerBoost);
		double style_bonus = player.getCombatStyle(ServerConstants.AGGRESSIVE) ? 3 : player.getCombatStyle(ServerConstants.CONTROLLED) ? 1 : 0;
		boolean meleeVoid = Combat.wearingFullVoidMelee(player);

		double effective_level = visible_strength;

		// Apply the prayer bonus.
		effective_level = Math.floor(effective_level * prayer_multiplier);

		// Apply the style bonus.
		effective_level += style_bonus;

		// Add 8 because the guide says to?
		effective_level += 8;

		// Apply Void bonus if applicable.
		if (meleeVoid) {
			effective_level = Math.floor(effective_level * 1.10);
		}
		// Effective level completed above.

		double equipment_bonus = player.playerBonus[10];
		double baseMaxHit = Math.floor(0.5 + effective_level * (equipment_bonus + 64) / 640);

		if (GameType.isPreEoc()) {
			if (Combat.fullTrickster(player) || Combat.fullBattleMage(player)) {
				baseMaxHit *= 1.15;
			}
		}

		if (!player.isGraniteMaulSpecial) {
			if (Combat.wearingFullDharok(player)) {
				double dharok_hp_max = player.getBaseHitPointsLevel();
				double dharok_hp_current = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				double dharoks_multiplier = 1 + (dharok_hp_max - dharok_hp_current) / 100 * dharok_hp_max / 100;
				double dharoks_max = Math.floor(baseMaxHit * dharoks_multiplier);
				return (int) dharoks_max;
			}
			int TOKTZ_XIL_AK = 6523; // Obsidian sword.
			int TOKTZ_XIL_EK = 6525; // Obsidian dagger.
			int TZHAAR_KET_OM = 6528; // Obby maul.
			int BERSERKER_NECKLACE = 11128;

			if (player.getWieldedWeapon() == TZHAAR_KET_OM || player.getWieldedWeapon() == TOKTZ_XIL_EK || player.getWieldedWeapon() == TOKTZ_XIL_AK) {
				if (Combat.wearingFullObsidianArmour(player)) {
					baseMaxHit = baseMaxHit * 1.10;
				}
				if (player.playerEquipment[ServerConstants.AMULET_SLOT] == BERSERKER_NECKLACE) {
					baseMaxHit = baseMaxHit * 1.20;
				}
				return (int) baseMaxHit;
			}
			return (int) (baseMaxHit * player.specDamage);

		}
		return (int) baseMaxHit;
	}


	/**
	 * Reduce the damage if the victim has melee protection prayer active.
	 *
	 * @param attacker The player attacking.
	 * @param victim The player under attack.
	 * @param damage The attacker's damage.
	 */
	public static int getVictimMeleePrayerActive(Player attacker, Player victim, int damage) {
		if (Combat.wearingFullVerac(attacker)) {
			return damage;
		}
		if (victim.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
			damage *= 0.6;
		}
		return damage;
	}

	/**
	 * Calculate the damage that the player will deal.
	 *
	 * @param attacker The player attacking.
	 * @param victim The player being attacked.
	 * @param damageType 1 is for a single hitsplat, 2 is for the second hitsplat and so on..
	 */
	public static int calculateMeleeDamage(Player attacker, Player victim, int damageType) {
		int damage = 0;

		MeleeAttack.saveCriticalDamage(attacker);
		if (MeleeFormula.getMeleeDamage0(attacker, victim)) {
			damage = 0;
		} else {
			int maximumDamage = getMaximumMeleeDamage(attacker);
			int lowest = 1;
			damage = Misc.random(lowest, maximumDamage);
		}
		damage = getMeleeDamageReduction(attacker, victim, damage);
		if (attacker.hit1) {
			damage = getMaximumMeleeDamage(attacker);
		}
		return damage;
	}


	/**
	 * Reduce the melee damage through Elysian spiritt shield, prayer & Toxic staff of the dead special
	 * attack.
	 */
	public static int getMeleeDamageReduction(Player attacker, Player victim, int damage) {
		damage = Effects.victimWearingElysianSpiritShield(victim, damage, false);

		if (GameType.isPreEoc()) {
			if (victim.getEquippedShield(18363) && damage > 20) {
				damage *= 0.86;
			} else if (victim.getEquippedShield(18359) && damage > 20) {
				damage *= 0.93;
			}
		}

		if (!attacker.dragonSwordSpecial) {
			damage = getVictimMeleePrayerActive(attacker, victim, damage);
		}
		if (System.currentTimeMillis() - victim.timeStaffOfTheDeadSpecialUsed <= 60000 && Combat.wearingStaffOfTheDead(victim)) {
			damage /= 2;
		}
		if (DominionGloveEffect.hasGoliathGloves(victim)) {
			damage *= 1.0 - (Misc.random(5) / 100);
			if (Misc.hasPercentageChance(5)) {
				int bonus = (25 + Misc.random(25)) / 100;
				damage *= bonus;
			}
		}
		return damage;
	}

	/**
	 * True, if the damage will 0.
	 *
	 * @param c Player
	 * @param i Other player
	 */
	public static boolean getMeleeDamage0(Player attacker, Player victim) {
		int difference;
		if (Combat.wearingFullVerac(attacker) && Misc.hasPercentageChance(25)) {
			difference = 1;
		} else {
			int attackerMaxRoll = getInvisibleMeleeAttackAdvantage(attacker);
			int victimMaxRoll = getInvisibleMeleeDefenceAdvantage(victim);
			difference = Misc.random(attackerMaxRoll) - Misc.random(victimMaxRoll);
		}
		if (difference <= 0) {
			return true;
		}
		return false;
	}



	public static void applyNewDragonClawsSpecialAttack(Player attacker, Player victim) {
		int damage1 = MeleeFormula.calculateMeleeDamage(attacker, victim, 0);
		int damage2 = 0;
		int damage3 = 0;
		int damage4 = 0;

		/*
		First, a normal attack, which usually deals a high amount of damage, then half of that first hit, rounded down, and then the third and fourth
		hits are half of the second hit, rounded down. For example, 35-17-8-9.
		If the first hit misses and the second one hits, the 3rd and 4th hits will each deal half of the damage of the second hit. For example, 0-34-17-18.
		If the first two attacks hit 0-0, the 3rd and 4th attacks will each only deal 75% of your max hit. For example, 0-0-23-22.
		If the claws' first 3 hits are zeros, the last hit (if it is a non-zero) will have a 50% damage boost; e.g.: 0-0-0-46.
		If all hits miss, the 3rd and/or 4th hits will each be 1, e.g. 0-0-1-1.
		Also, each sequence may sometimes add one extra damage to the fourth hit, so an expected hit such as 32-16-8-8 might actually be 32-16-8-9.[1]
		 */

		/* Start of First result. */
		if (damage1 > 0) {
			damage2 = damage1 / 2;
			damage3 = damage2 / 2;
			damage4 = damage3 + Misc.random(1);
		} else {
			damage2 = MeleeFormula.calculateMeleeDamage(attacker, victim, 0);
			if (damage2 > 0) {
				damage3 = damage2 / 2;
				damage4 = damage3 + Misc.random(1);
			} else {
				damage1 = MeleeFormula.calculateMeleeDamage(attacker, victim, 0);
				if (damage1 > 0) {
					damage1 = 0;
					damage3 = (int) (MeleeFormula.getMaximumMeleeDamage(attacker) * 0.75);
					damage3 = MeleeFormula.getMeleeDamageReduction(attacker, victim, damage3);
					damage4 = damage3 + Misc.random(1);
				} else {
					damage1 = MeleeFormula.calculateMeleeDamage(attacker, victim, 0);
					if (damage1 > 0) {
						damage2 = 0;
						damage3 = 0;
						damage4 = (int) (damage1 * 1.5);
						damage1 = 0;
					} else {
						damage3 = 1;
						damage4 = 1;
					}
				}
			}
		}
		attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, damage1, 3, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false).addEffect(new SaveDamage("DRAGON CLAWS FIRST")));
		attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, damage2, 3, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false).addEffect(new SaveDamage("DRAGON CLAWS SECOND")));
		attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, damage3, 4, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false).addEffect(new SaveDamage("DRAGON CLAWS THIRD")));
		attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, damage4, 4, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false).addEffect(new SaveDamage("DRAGON CLAWS FOURTH")));

	}

	public static boolean applyGraniteMaulDamage(Player attacker, Player victim) {
		boolean used = false;
		if (attacker.graniteMaulSpecialAttackClicks > 0 && Combat.hasGraniteMaulEquipped(attacker)) {
			for (int index = 0; index < 2; index++) {
				if (attacker.graniteMaulSpecialAttackClicks == 0) {
					break;
				}
				if (Combat.checkSpecAmount(attacker, 4153)) {
					attacker.startAnimation(1667);
					attacker.gfx100(340);
					SoundSystem.sendSound(attacker, victim, 1079, 350);
					attacker.setUsingSpecialAttack(false);
					CombatInterface.addSpecialBar(attacker, attacker.getWieldedWeapon());
					Combat.attackApplied(attacker, victim, "MELEE", true);
					Combat.performBlockAnimation(victim, attacker);
					attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, MeleeFormula.calculateMeleeDamage(attacker, victim, 0), 3, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false));
					used = true;
				}
				else {
					Combat.notEnoughSpecialLeft(attacker);
				}
				attacker.graniteMaulSpecialAttackClicks--;
			}
			attacker.graniteMaulSpecialAttackClicks = 0;
		}
		return used;
	}

}
