package game.content.combat.vsplayer.range;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.skilling.summoning.Summoning;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Player Range formulas.
 *
 * @author MGT Madness, created on 17-11-2013.
 */

public class RangedFormula {

	/**
	 * Generate the range damage for normal attacks that only deal 1 hitsplat at a time.
	 * <p>
	 * This method is called during the weapon animation and the damage is used when the hitsplat appears. Remember to update calculateSecondDamage same as this but change the rangeSecondDamage. For calculateSecondDamage, remove the Bolts Effects part.
	 *
	 * @param attacker The player that is attacking.
	 * @param theTarget The player being attacked.
	 */
	public static int calculateRangedDamage(Player attacker, Player victim, boolean secondDamage) {
		attacker.maximumDamageRanged = getRangedMaximumDamage(attacker);

		int damage;
		int arrow = attacker.playerEquipment[ServerConstants.ARROW_SLOT];
		boolean damageWillNotBe0 = false;

		/* Player activate Diamond bolts (e) special attack */
		if ((arrow == 9243 || arrow == 21946) && Misc.hasPercentageChance(10)) // Diamond bolt (e)
		{
			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					damageWillNotBe0 = true;
					attacker.showDiamondBoltGFX = true;
				}
			}
		}
		if (isRangedDamage0(attacker, victim) && !damageWillNotBe0) {
			damage = 0;
		} else {
			damage = Misc.random(1, attacker.maximumDamageRanged);
		}
		damage = Effects.victimWearingElysianSpiritShield(victim, damage, false);
		if (GameType.isPreEoc()) {
			if (victim.getEquippedShield(18361) && damage > 20) {
				damage *= 0.93;
			} else if (victim.getEquippedShield(18359) && damage > 20) {
				damage *= 0.86;
			}
		}
		if (victim.prayerActive[ServerConstants.PROTECT_FROM_RANGED]) {
			damage *= 0.6;
		}

		if (attacker.hit1) {
			damage = attacker.maximumDamageRanged;
		}

		/* Player Dark bow special attack is active. */
		if (!attacker.dragonThrownaxeSpecialUsed) {
			if (attacker.isUsingDarkBowSpecialAttack() && damage < 8) {
				damage = 8;
			}
		}

		/* Player activate Dragon bolts (e) special attack */
		if ((arrow == 9244 || arrow == 21948) & Combat.antiFire(victim, true, false) <= 0 && Misc.hasPercentageChance(6)) {
			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					attacker.showDragonBoltGFX = true;
					damage *= 1.45;
				}
			}
		}

		if (GameType.isPreEoc()) {
			if (attacker.playerEquipment[ServerConstants.AMULET_SLOT] == 15_126) {
				damage *= 1.20;
			}
			if (Combat.fullTrickster(attacker) || Combat.fullBattleMage(attacker)) {
				damage *= 1.15;
			}
		}

		/* Player activate Opal bolts (e) special attack */
		if ((arrow == 9236 || arrow == 21932) && Misc.hasPercentageChance(5)) {
			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					attacker.showOpalBoltGFX = true;
					int level = attacker.getCurrentCombatSkillLevel(ServerConstants.RANGED);
					damage += level / 10;
				}
			}
		}

		// Onyx bolt (e)
		else if ((arrow == 9245 || arrow == 21950) && Misc.hasPercentageChance(10)) {
			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					damage *= 1.2;
					attacker.showOnyxBoltGfx = true;
					int heal = damage / 4;
					attacker.addToHitPoints(heal);
				}
			}
		}
		/* Player activate the Ruby bolts (e) special attack */
		else if ((arrow == 9242 || arrow == 21944) && damage > 0 && Misc.hasPercentageChance(11)) {

			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (arrow <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					attacker.showRubyBoltGFX = true;
				}
			}
		}

		if (!attacker.dragonThrownaxeSpecialUsed) {
			if (attacker.blowpipeSpecialAttack) {
				attacker.addToHitPoints(damage / 2);
			}
		}
		if (!attacker.dragonThrownaxeSpecialUsed) {
			if (secondDamage) {
				attacker.rangedSecondDamage = damage;
			} else {
				attacker.rangedFirstDamage = damage;
			}
		} else {
			attacker.dragonThrownAxeSpecialDamage = damage;
		}
		return damage;
	}

	/**
	 * @param player The player
	 * @return The attack multiplier.
	 */
	public static double getRangedAttackMultiplier(Player player) {
		double multiplier = getPrayerRangedMultiplier(player);
		if (RangedData.wearingFullVoidRanged(player)) {
			multiplier += 0.10;
		}
		multiplier *= player.getSpecialAttackAccuracyMultiplier();
		return multiplier;
	}

	private static double getPrayerRangedMultiplier(Player player) {
		if (player.prayerActive[ServerConstants.SHARP_EYE]) {
			return 1.05;
		} else if (player.prayerActive[ServerConstants.HAWK_EYE]) {
			return 1.10;
		} else if (player.prayerActive[ServerConstants.EAGLE_EYE]) {
			return 1.15;
		} else if (player.prayerActive[ServerConstants.RIGOUR]) {
			return 1.20;
		}
		return 1.0;
	}

	/**
	 * @param player The player.
	 * @return The defence multiplier.
	 */
	public static double getRangedDefenceMultiplier(Player player) {
		double multiplier = MeleeFormula.getMeleePrayerDefenceMultiplier(player);
		return multiplier;
	}

	/**
	 * @param player The associated player.
	 * @return The player's invisible ranged attack advantage.
	 */
	public static int getInvisibleRangedAttackAdvantage(Player player) {
		double skillLevel = player.getCurrentCombatSkillLevel(ServerConstants.RANGED);
		double highestItemBonus = player.playerBonus[ServerConstants.RANGED_ATTACK_BONUS];
		double itemBonusMultiplier = 1.0;
		double baseAdvantage = 10;
		double otherMultiplier = getRangedAttackMultiplier(player);
		double finalMultiplier = 1.65;
		if (player.getWieldedWeapon() == 12926) {
			highestItemBonus += player.blowpipeDartItemId == 11230 ? 18 : player.blowpipeDartItemId == 811 ? 15 : 0;
		}
		double finalAttackAdvantage = 0;
		double accurateCombatStyleModifier = player.getCombatStyle(ServerConstants.ACCURATE) ? 1.01 : 1.0;
		finalAttackAdvantage += highestItemBonus * itemBonusMultiplier;
		finalAttackAdvantage += baseAdvantage;
		finalAttackAdvantage += skillLevel;
		finalAttackAdvantage *= otherMultiplier;
		finalAttackAdvantage *= accurateCombatStyleModifier;
		finalAttackAdvantage *= finalMultiplier;
		finalAttackAdvantage *= 2.27;
		if (GameType.isPreEoc()) {
			double attack = Summoning.getSkillBonus(player, ServerConstants.RANGED);
			if (attack > 1.0) {
				finalAttackAdvantage *= attack;
			}
		}
		return (int) finalAttackAdvantage;
	}

	//143 attack bonus, 92 ranged level vs 224 defence bonus, 93 defence level
	//143 attack bonus, 104 ranged level vs 224 defence bonus, 76 defence level
	//143 attack bonus, 92 ranged level vs 132 defence bonus, 76 defence level
	//143 attack bonus, 92 ranged level vs 0 defence bonus, 76 defence level
	//143 attack bonus, 92 ranged level vs 159 defence bonus, 40 defence level
	public static int getInvisibleRangedDefenceAdvantage(Player player) {
		double skillLevel = player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE);
		double highestItemBonus = player.playerBonus[ServerConstants.RANGED_DEFENCE_BONUS];
		double itemBonusMultiplier = 1;
		double baseAdvantage = 10;
		double otherMultiplier = getRangedDefenceMultiplier(player);
		double finalMultiplier = 1.73;
		double advantage = 0;
		double accurateCombatStyleModifier = player.getCombatStyle(ServerConstants.DEFENSIVE) ? 1.01 : player.getCombatStyle(ServerConstants.LONG_RANGED) ? 1.01 : 1.0;
		double effectiveDefenceBonus = 0.5;
		effectiveDefenceBonus += 0.004237 * skillLevel;
		highestItemBonus *= effectiveDefenceBonus;
		advantage += highestItemBonus * itemBonusMultiplier;
		advantage += baseAdvantage;
		advantage *= otherMultiplier;
		advantage *= accurateCombatStyleModifier;
		advantage += skillLevel;
		advantage *= finalMultiplier;
		advantage *= 2.27;
		if (GameType.isPreEoc()) {
			double def = Summoning.getSkillBonus(player, ServerConstants.RANGED);
			if (def > 1.0) {
				advantage *= def;
			}
		}
		return (int) advantage;
	}

	/**
	 * Calculate weather the 0 should appear.
	 *
	 * @param attacker
	 * @param victim
	 */
	public static boolean isRangedDamage0(Player attacker, Player victim) {
		int Difference;
		Difference = Misc.random(getInvisibleRangedAttackAdvantage(attacker)) - Misc.random(getInvisibleRangedDefenceAdvantage(victim));
		if (Difference <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Calculate the maximum range damage depending on weapon etc..
	 *
	 * @param player The player
	 * @return The max damage of range.
	 */
	public static int getRangedMaximumDamage(Player player) {
		int rangeLevel = player.getCurrentCombatSkillLevel(ServerConstants.RANGED);
		double modifier = 1.0;
		double specialAttackDamageMultiplier = player.specDamage;

		if (player.dragonThrownaxeSpecialUsed) {
			specialAttackDamageMultiplier = 1.0;
		}
		if (player.prayerActive[ServerConstants.SHARP_EYE]) {
			modifier += 0.05;
		} else if (player.prayerActive[ServerConstants.HAWK_EYE]) {
			modifier += 0.10;
		} else if (player.prayerActive[ServerConstants.EAGLE_EYE]) {
			modifier += 0.15;
		} else if (player.prayerActive[ServerConstants.RIGOUR]) {
			modifier += 0.23;
		}
		if (RangedData.wearingFullVoidRanged(player)) {
			modifier += 0.10;
		}
		if (RangedData.wearingFullVoidRangedElite(player)) {
			modifier += 0.025;
		}

		double c = modifier * rangeLevel;
		int rangedStrength = getRangedStrength(player);
		double maxDamage = (c + 8) * (rangedStrength + 70) / 640;
		maxDamage *= specialAttackDamageMultiplier;
		if (maxDamage < 1) {
			maxDamage = 1;
		}
		if (!player.dragonThrownaxeSpecialUsed) {
			if (maxDamage > 48 && player.isUsingDarkBowSpecialAttack()) {
				maxDamage = 48;
			}
		}
		return (int) maxDamage;
	}

	public static int getRangedStrength(Player player) {
		int arrow = 0;
		arrow = player.playerEquipment[ServerConstants.ARROW_SLOT];
		int strength = 0;

		// Amulet of anguish
		if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 19547 || player.playerEquipment[ServerConstants.AMULET_SLOT] == 22249) {
			strength += 5;
		}

		// Ava's assembler
		if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 22109) {
			strength += 2;
		}

		// Assembler Max cape
		if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 21898) {
			strength += 2;
		}
		String weaponName = ItemAssistant.getItemName(player.getWieldedWeapon());
		if (GameType.isOsrs()) {
			// Heavy ballista, this is only exception other than twisted bow that uses break instead of return, as it uses Dragon javelins as ammo.
			if (weaponName.equals("heavy ballista")) {
				strength += 15;
			}
		}
		// Wielded items come first before arrows strength bonuses, or else it will stack with arrows and that is a bug and is overpowered.
		switch (player.getWieldedWeapon()) {
			case 13_883:
			case 13_879:
				return strength += 80;

			case 22634: // Morrigan's throwing axe
				return strength += 117;
			case 22636: // Morrigan's javelin
				return strength += 145;

			// Crystal bow, this stops arrows and bolts giving extra damage.
			case 4214:
				return strength += 70;
			case 22550: // Craw's bow
			case 22547: // Craw's bow (u)
				return strength += 60;

			// Toxic blowpipe
			case 12926:
				return strength += 40 + (player.blowpipeDartItemId == 11230 ? 20 : player.blowpipeDartItemId == 811 ? 14 : 0);

			// Bronze knife
			case 864:
				return strength += 3;

			// Iron knife
			case 863:
				return strength += 4;

			// Steel knife
			case 865:
				return strength += 7;

			// Black knife
			case 869:
				return strength += 8;

			// Mithril knife
			case 866:
				return strength += 10;

			// Adamant knife
			case 867:
				return strength += 14;

			// Rune knife
			case 868:
				return strength += 24;

			// Dragon thrownaxe
			case 20849:
				return strength += 47;


			case 806: // Bronze dart.
				return strength += 1;

			case 807: // Iron dart.
				return strength += 3;

			case 808: // Steel dart.
				return strength += 4;

			case 809: // Mithril dart.
				return strength += 7;

			case 810: // Adamant dart.
				return strength += 10;

			case 811: // Rune dart.
				return strength += 14;

			case 11230: // Dragon dart.
				return strength += 20;

			//Toktz-xil-ul.
			case 6522:
				return strength += 49;

			// Twisted bow, the weapon itself has a strength bonus that stacks ontop of the arrow.
			case 20997:
			case 16052:
			case 16056:
			case 16282:
				strength += 20;
				break;

		}
		switch (arrow) {
			// Bronze bolts
			case 877:
				strength += 10;
				break;

			// Iron bolts
			case 9140:
				strength += 46;
				break;

			// Steel bolts
			case 9141:
				strength += 64;
				break;

			// Dragon javelin
			case 19484:
				strength += 150;
				break;

			case 9142: // Mithril bolts
			case 9241: // Emerald bolts (e)
			case 9240: // Sapphire bolts (e)
			case 9338: // Emerald bolts
			case 9337: // Sapphire bolts
				strength += 82;
				break;
			case 9143: // Adamant bolts
			case 9243: // Diamond bolts (e)
			case 9242: // Ruby bolts (e)
			case 9339: // Ruby bolts
			case 9340: // Diamond bolts
				strength += 100;
				break;

			case 9144: // Runite bolts
			case 9244: // Dragon bolts e
			case 9245: // Onxy bolts e
			case 9341: // Dragon bolts
			case 9342: // Onyx bolts
				strength += 115;
				break;

			case 21905: // Dragon bolts
			case 21924: // Dragon bolts (p)
			case 21926: // Dragon bolts (p+)
			case 21928: // Dragon bolts (p++)
			case 21932: // Opal dragon bolts (e)
			case 21934: // Jade dragon bolts (e)
			case 21936: // Pearl dragon bolts (e)
			case 21938: // Topaz dragon bolts (e)
			case 21940: // Sapphire dragon bolts (e)
			case 21942: // Emerald dragon bolts (e)
			case 21944: // Ruby dragon bolts (e)
			case 21946: // Diamond dragon bolts (e)
			case 21948: // Dragonstone dragon bolts (e)
			case 21950: // Onyx dragon bolts (e)
				strength += 122;
				break;

			// Bronze arrow
			case 882:
				strength += 7;
				break;

			// Iron arrow
			case 884:
				strength += 10;
				break;

			// Steel arrow
			case 886:
				strength += 16;
				break;

			// Mithril arrow
			case 888:
				strength += 22;
				break;

			// Adamant arrow
			case 890:
				strength += 31;
				break;

			// Long kebbit bolts
			case 10159:
				strength += 38;
				break;

			// Rune arrow
			case 892:
				strength += 49;
				break;

			// Amethyst arrows
			case 21326:
				strength += 55;
				break;

			// Bolt rack
			case 4740:
				strength += 59;
				break;

			// Dragon arrows
			case 11212:
				strength += 60;
				break;

		}
		return strength;

	}


	public static boolean applyDragonThrownAxeDamage(Player attacker, Player victim) {
		if (attacker.getWieldedWeapon() == 20849 && attacker.isUsingSpecial()) {
			if (Combat.checkSpecAmount(attacker, attacker.getWieldedWeapon())) {
				attacker.setUsingSpecialAttack(false);
				CombatInterface.addSpecialBar(attacker, attacker.getWieldedWeapon());
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.startAnimation(7521);
				attacker.dragonThrownaxeSpecialUsed = true;
				Combat.fireProjectilePlayer(attacker, victim);
				attacker.dragonThrownaxeSpecialUsed = false;
				Combat.attackApplied(attacker, victim, "RANGED", true);
				attacker.getIncomingDamageOnVictim().add(new EntityDamage<>(victim, attacker,
						RangedFormula.calculateRangedDamage(attacker, victim, false), 4, EntityDamageType.RANGED, attacker.maximumDamageRanged, true, false));
			}
			else {
				Combat.notEnoughSpecialLeft(attacker);
			}
			return true;
		}
		return false;
	}


}
