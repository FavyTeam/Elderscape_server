package game.content.combat.vsplayer;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Special combat effects that affect all combat types such as Ring of recoil.
 *
 * @author MGT Madness, created on 31-01-2015.
 */
public class Effects {

	/**
	 * Phoenix necklace effect.
	 *
	 * @param player The associated player.
	 */
	public static void phoenixNecklace(Player player, int damage) {
		if (damage == 0) {
			return;
		}
		int PHOENIX_NECKLACE = 11090;
		if (!ItemAssistant.hasItemEquipped(player, PHOENIX_NECKLACE)) {
			return;
		}
		// If hp les than 20$
		if (((double) player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / (double) player.getBaseHitPointsLevel()) <= 0.20) {
			// Add 30% of base hp to my hitpoints.
			player.addToHitPoints((int) (player.getBaseHitPointsLevel() / 10.0) * 3);
			ItemAssistant.deleteEquipment(player, PHOENIX_NECKLACE, ServerConstants.AMULET_SLOT);
			player.playerAssistant.sendMessage("Your phoenix necklace heals you, but is destroyed in the process.");
		}
	}

	/**
	 * Ring of recoil effect.
	 *
	 * @param attacker The player who caused the damage.
	 * @param victim The player being damaged.
	 * @param damage The damage dealt by the attacker.
	 */
	public static void recoilEffect(Player attacker, Player victim, int damage) {
		//Attacker = the one attacking
		// Victim the one being damaged
		// When it comes to recoil, the attacker gets damaged.
		if (damage > 0 && victim.getRecoilCharges() > 0 && (ItemAssistant.hasItemEquipped(victim, 2550) || ItemAssistant.hasItemEquipped(victim, 20657)
					&& victim.getAttributes().getOrDefault(Player.RING_OF_SUFFERING_ENABLED))) {
			if (victim.getDead() && victim.getHeight() == 20) {
				return;
			}
			int recoilDamage = damage / 10;
			if (recoilDamage < 1) {
				recoilDamage = 1;
			}
			victim.setRecoilCharges(victim.getRecoilCharges() - recoilDamage);
			if (victim.getRecoilCharges() < 0) {
				recoilDamage += victim.getRecoilCharges();
			}
			if (recoilDamage > 0) {
				victim.ignoreInCombat = true;
				Combat.createHitsplatOnPlayerPvp(victim, attacker, recoilDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "RECOIL", -1);
				victim.ignoreInCombat = false;
			}
			if (victim.getRecoilCharges() <= 0) {
				victim.setRecoilCharges(40);
				if (!ItemAssistant.hasItemEquipped(victim, 20657)) {
					ItemAssistant.deleteEquipment(victim, 2550, ServerConstants.RING_SLOT);
					victim.playerAssistant.sendMessage("<col=ba05a8>Your ring of recoil has shattered.");
				}
			}
		}
	}

	/**
	 * Apply the retribution or wrath effect.
	 */
	public static void appendRetributionOrWrath(Player victim, Player attacker) {
		if (!victim.getDead()) {
			return;
		}
		if (victim.redemptionOrWrathActivated) {
			return;
		}
		if (!victim.prayerActive[ServerConstants.RETRIBUTION]) {
			return;
		}
		if (!victim.getPA().withinDistanceOfTargetPlayer(attacker, 1)) {
			return;
		}
		victim.redemptionOrWrathActivated = true;
		int revengeDamage = (int) (victim.baseSkillLevel[ServerConstants.PRAYER] * 0.25);
		revengeDamage = Misc.random2(revengeDamage);
		Combat.createHitsplatOnPlayerPvp(victim, attacker, revengeDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
		victim.gfx0(437);
	}

	public static int victimWearingElysianSpiritShield(Player victim, int damage, boolean npcCombat) {
		// Elysian spirit shield.
		if (Combat.hasElysianSpiritShield(victim) && Misc.hasPercentageChance(70)) {
			victim.gfx0(321);
			if (damage > 0) {
				return damage *= 0.75;
			}
		} 
		else if (Combat.wearingFullJusticiar(victim)) {
			double reduction = (double) victim.playerBonus[ServerConstants.SLASH_DEFENCE_BONUS] / 3000.0;
			return damage *= 1.0 - reduction;
		}
		return damage;
	}

	/**
	 * The Karil's set effect, 25% chance of lowering the victim's Agility level by 20%.
	 */

	/*public static void karilsEffect(Player attacker, Player victim, int damage) {
		if (damage > 0) {
			int agility = (int) (victim.baseSkillLevel[ServerConstants.AGILITY] * 0.7);
			if (agility < 1) {
				agility = 1;
			}
			victim.gfx100(401);
			victim.baseSkillLevel[ServerConstants.AGILITY] = agility;
			Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.AGILITY);
			//RegenerateSkill.storeBoostedTime(victim, ServerConstants.AGILITY);
			victim.getPA().sendMessage("Your opponent has drained your Agility level!");
		}
	}*///TODO couldn't finish as there isn't a way to have drained non combat skills right now

	/**
	 * The Ahrim's set effect, 25% chance of lowering the victim's Strength level by 5.
	 */

	public static void ahrimsEffect(Player attacker, Player victim) {
		int strength = (int) (victim.currentCombatSkillLevel[ServerConstants.STRENGTH] - 5);
		if (strength < 5) {
			strength = 5;
		}
		victim.gfx100(400);
		victim.currentCombatSkillLevel[ServerConstants.STRENGTH] = strength;
		Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.STRENGTH);
		RegenerateSkill.storeBoostedTime(victim, ServerConstants.STRENGTH);
		victim.getPA().sendMessage("Your opponent has drained your Strength level!");
	}

	/**
	 * The Amulet of the damned Dharok's set effect, 25% chance of recoiling 15% of the damage taken.
	 */

	public static void dharoksDamnedEffect(Player attacker, Player victim, int damage) {
		//victim = the player getting attacked
		if (Combat.wearingFullDharok(victim) && Combat.hasAmuletOfTheDamned(victim) && Misc.hasPercentageChance(25) && damage > 0) {
			if (victim.getDead() && victim.getHeight() == 20) {
				return;
			}
			int dharokDamage = (int) (damage * 0.15); // 15% of damage is deflected.
			if (dharokDamage == 0) {
				return;
			}
			victim.ignoreInCombat = true;
			Combat.createHitsplatOnPlayerPvp(victim, attacker, dharokDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
			victim.ignoreInCombat = false;
		}
	}
}
