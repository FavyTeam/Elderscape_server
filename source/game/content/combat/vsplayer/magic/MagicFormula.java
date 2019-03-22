package game.content.combat.vsplayer.magic;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.range.RangedData;
import game.content.skilling.summoning.Summoning;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Player vs player Magic formulas
 *
 * @author MGT Madness, created on 21-11-2013.
 */
public class MagicFormula {

	/**
	 * calculate the int magicDamage.
	 * <p>
	 * This is called when the animation starts.
	 *
	 * @param attacker The player who is attacking.
	 * @param theTarget The player being attacked.
	 */
	public static int calculateMagicDamage(Player attacker, Player victim) {
		attacker.setMaximumDamageMagic(getMagicMaximumDamage(attacker));
		int damage = Misc.random(0, attacker.getMaximumDamageMagic()); // The damage to the target.
		damage = Effects.victimWearingElysianSpiritShield(victim, damage, false);
		if (victim.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
			damage *= 0.6;
		}
		if (GameType.isPreEoc()) {
			if (victim.getEquippedShield(18361) && damage > 20) {
				damage *= 0.86;
			} else if (victim.getEquippedShield(18363) && damage > 20) {
				damage *= 0.93;
			}
		}
		return attacker.setMagicDamage(damage);
	}

	/**
	 * The maximum damage with magic.
	 *
	 * @param player The player.
	 * @return The maximum damage
	 */
	public static int getMagicMaximumDamage(Player player) {
		if (player.getSpellId() == -1) {
			return 0;
		}
		double damage = CombatConstants.MAGIC_SPELLS[player.getSpellId()][6];
		if (System.currentTimeMillis() - player.chargeSpellTime <= Misc.getMinutesToMilliseconds(6)) {
			// Saradomin strike & Saradomin god cape
			if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1190
					&& MagicData.hasSaradominGodCape(player)) {
				damage += 10;
			}

			// Guthix strike & Guthix god cape
			else if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1191
					&& MagicData.hasGuthixGodCape(player)) {
				damage += 10;
			}

			// Zamorak strike & Zamorak god cape
			else if (CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1192
					&& MagicData.hasZamorakGodCape(player)) {
				damage += 10;
			}
		}

		if (GameType.isPreEoc()) {
			if (player.playerEquipment[ServerConstants.WEAPON_SLOT] == 13_867
					|| player.playerEquipment[ServerConstants.WEAPON_SLOT] == 22_494) {
				damage *= 1.10;
			} else if (player.playerEquipment[ServerConstants.WEAPON_SLOT] == 21_777) {
				damage *= 1.15;
			} else if (player.playerEquipment[ServerConstants.WEAPON_SLOT] == 18_355) {
				damage *= 1.20;
			}
			if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18_333) {
				damage *= 1.05;
			} else if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18_334) {
				damage *= 1.10;
			} else if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18_335) {
				damage *= 1.15;
			}
			if (Combat.fullTrickster(player) || Combat.fullBattleMage(player)) {
				damage *= 1.15;
			}
		}
		else {
			// Smoke battlestaff
			if (player.getWieldedWeapon() == 11998) {
				damage *= 1.1;
			}
		}
		/*
		 * Cheap hax to hit on lvl 1
		 */
		if (damage < 1 && Misc.random(2) == 1 && player.getBaseMagicLevel() < 5) {
			damage = 1;

		}
		double bonusDamageMultiplier = 1;

		bonusDamageMultiplier += getMagicPercentageDamageBonus(player);
		damage *= bonusDamageMultiplier;
		int roundedDamage = (int) Math.round(damage);
		return roundedDamage;
	}

	public static double getMagicPercentageDamageBonus(Player player) {
		double bonusDamageMultiplier = 0.0;
		bonusDamageMultiplier = getMagicBonusDamageMultiplierOsrs(player, bonusDamageMultiplier);
		bonusDamageMultiplier = getMagicBonusDamageMultiplierPreEoc(player, bonusDamageMultiplier);
		double value = (double) Math.round(bonusDamageMultiplier * 100) / 100;
		return value;
	}

	private static double getMagicBonusDamageMultiplierPreEoc(Player player, double bonusDamageMultiplier) {
		if (!GameType.isPreEoc()) {
			return bonusDamageMultiplier;
		}
		switch (player.getWieldedWeapon()) {
			case 16272 : // Toxic staff of the dead
			case 15486 : // Staff of light
				bonusDamageMultiplier += 0.15;
				break;

			case 4710 : // Ahrim's staff
				bonusDamageMultiplier += 0.05;
				break;
		}
		if (RangedData.wearingFullVoidMageElite(player)) {
			bonusDamageMultiplier += 0.025;
		}
		return bonusDamageMultiplier;
	}

	private static double getMagicBonusDamageMultiplierOsrs(Player player, Double bonusDamageMultiplier) {
		if (!GameType.isOsrs()) {
			return bonusDamageMultiplier;
		}
		switch (player.playerEquipment[ServerConstants.CAPE_SLOT]) {
			case 21791 : // Imbued sara cape, 2% bonus
			case 21776 : // Max cape version of Imbued sara cape
			case 21793 : // Imbued guthix cape, 2% bonus
			case 21784 : // Max cape version of Imbued guthix cape
			case 21795 : // Imbued zammy cape, 2% bonus
			case 21780 : // Max cape version of Imbued zammy cape
				bonusDamageMultiplier += 0.02;
				break;
		}
		switch (player.getWieldedWeapon()) {

			case 22296 : // Staff of light.
			case 11791 : // Staff of the dead.
			case 12904 : // Toxic staff of the dead.
			case 16209 : // Toxic staff of the dead
			case 16272 : // Toxic staff of the dead
				bonusDamageMultiplier += 0.15;
				break;

			case 20604 : // Elder wand
				bonusDamageMultiplier += 0.05;
				break;

			case 4710 : // Ahrim's staff
				bonusDamageMultiplier += 0.05;
				break;
		}
		if (ItemAssistant.getItemName(player.getWieldedWeapon()).equals("Kodai wand")) {
			bonusDamageMultiplier += 0.15;
		}

		// Occult necklace
		if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 12002 || player.playerEquipment[ServerConstants.AMULET_SLOT] == 19720) {
			bonusDamageMultiplier += 0.10;
		}

		// Tome of fire
		if (MagicFormula.isFireSpell(player) && player.playerEquipment[ServerConstants.SHIELD_SLOT] == 20714) {
			bonusDamageMultiplier += 0.50;
		}

		// Tormented bracelet
		if (player.playerEquipment[ServerConstants.HAND_SLOT] == 19544) {
			bonusDamageMultiplier += 0.05;
		}

		// Ancestral hat
		if (ItemAssistant.getItemName(player.playerEquipment[ServerConstants.HEAD_SLOT]).equals("Ancestral hat")) {
			bonusDamageMultiplier += 0.02;
		}
		// Ancestral top
		if (ItemAssistant.getItemName(player.playerEquipment[ServerConstants.BODY_SLOT]).equals("Ancestral robe top")) {
			bonusDamageMultiplier += 0.02;
		}
		// Ancestral bottom
		if (ItemAssistant.getItemName(player.playerEquipment[ServerConstants.LEG_SLOT]).equals("Ancestral robe bottom")) {
			bonusDamageMultiplier += 0.02;
		}

		// Zuriel set
		if (ItemAssistant.getItemName(player.playerEquipment[ServerConstants.HEAD_SLOT]).equals("Zuriel's hood")) {
			bonusDamageMultiplier += 0.02;
		}
		if (ItemAssistant.getItemName(player.playerEquipment[ServerConstants.BODY_SLOT]).equals("Zuriel's robe top")) {
			bonusDamageMultiplier += 0.02;
		}
		if (ItemAssistant.getItemName(player.playerEquipment[ServerConstants.LEG_SLOT]).equals("Zuriel's robe bottom")) {
			bonusDamageMultiplier += 0.02;
		}

		if (RangedData.wearingFullVoidMageElite(player)) {
			bonusDamageMultiplier += 0.025;
		}
		return bonusDamageMultiplier;
	}

	public static boolean isFireSpell(Player player) {
		if (player.getSpellId() < 0) {
			return false;
		}
		return CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1158
				|| CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1169
				|| CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1181
				|| CombatConstants.MAGIC_SPELLS[player.getSpellId()][0] == 1189;
	}

	/**
	 * True, if the random calculated difference between the player's magic attack and opponenet's
	 * magic defence is 0 or less.
	 * <p>
	 * Used to calculate weather a magic casted spell will be a splash.
	 *
	 * @param player The player that is attacking.
	 * @param theTarget The player being attacked.
	 * @return the splash result.
	 */
	public static boolean isSplash(Player player, Player target) {
		int Difference = Misc.random(getMagicAttackAdvantage(player))
				- Misc.random(getMagicDefenceAdvantage(target));
		if (Difference <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Calculate the Magic attack advantage.
	 *
	 * @param player The player.
	 * @return Magic attack advantage.
	 */
	public static int getMagicAttackAdvantage(Player player) {
		int baseAttack = 30;
		int magicAttack = baseAttack;
		double equipmentBonusMultiplier = 1; // Was 9.0 when players complained about splashing is a
															// bit more often.
		double equipmentBonus =
				(player.playerBonus[ServerConstants.MAGIC_ATTACK_BONUS] * equipmentBonusMultiplier);
		double magicMultiplier = 1.0;
		double levelMultiplier = player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) / 100.0;
		magicAttack += equipmentBonus;
		if (MagicData.wearingFullVoidMagic(player)) {
			magicMultiplier += 0.45;
		}
		magicMultiplier += getMagicPrayerBoost(player);
		magicAttack *= magicMultiplier;
		magicAttack *= levelMultiplier;
		double finalMultiplier = 1.11;
		magicAttack *= finalMultiplier;
		if (player.playerBonus[ServerConstants.MAGIC_ATTACK_BONUS] < 0) {
			magicAttack = 0;
		}
		magicAttack *= 6.4;
		if (GameType.isPreEoc()) {
			double attack = Summoning.getSkillBonus(player, ServerConstants.MAGIC);
			if (attack > 1.0) {
				magicAttack *= attack;
			}
			if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18_333) {
				magicAttack *= 1.05;
			} else if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18_334) {
				magicAttack *= 1.10;
			} else if (player.playerEquipment[ServerConstants.AMULET_SLOT] == 18_335) {
				magicAttack *= 1.15;
			}
		}
		else {
			// Smoke battlestaff
			if (player.getWieldedWeapon() == 11998) {
				magicAttack *= 1.1;
			}
		}
		if (Combat.isZamorakFlamesEffected(player)) {
			magicAttack *= 0.95;
		}
		return magicAttack;
	}

	// When it is 60 magic attack vs 42 magic defence, it should splash way more.

	public static int getMagicDefenceAdvantage(Player player) {
		double equipmentBonusMultiplier = 1;
		int magicDefence = (int) (player.playerBonus[ServerConstants.MAGIC_DEFENCE_BONUS]
				* equipmentBonusMultiplier);
		double magicMultiplier = 1.0 + getMagicPrayerBoost(player);
		int baseDefence = 22; // So splashing is more often when mage boxing.
		magicDefence += baseDefence;
		int levelsBelow =
				player.getBaseMagicLevel() - player.getCurrentCombatSkillLevel(ServerConstants.MAGIC);
		if (levelsBelow > 0) {
			levelsBelow *= 0.6;
			double value = 0.0;
			value = levelsBelow / 100.0;
			value = 1.0 - value;
			magicDefence *= value;
		}
		magicDefence *= magicMultiplier;
		double finalMultiplier = 1.0;
		magicDefence *= finalMultiplier;
		magicDefence *= 6.4;
		if (GameType.isPreEoc()) {
			double defence = Summoning.getSkillBonus(player, ServerConstants.DEFENCE);
			if (defence > 1.0) {
				magicDefence *= defence;
			}
		}
		if (Combat.isZamorakFlamesEffected(player)) {
			magicDefence *= 0.95;
		}
		return magicDefence;
	}

	private static double getMagicPrayerBoost(Player player) {
		if (player.prayerActive[ServerConstants.MYSTIC_WILL]) {
			return 0.05;
		} else if (player.prayerActive[ServerConstants.MYSTIC_LORE]) {
			return 0.10;
		} else if (player.prayerActive[ServerConstants.MYSTIC_MIGHT]) {
			return 0.15;
		} else if (player.prayerActive[ServerConstants.AUGURY]) {
			return 0.25;
		}
		return 0;
	}

}
