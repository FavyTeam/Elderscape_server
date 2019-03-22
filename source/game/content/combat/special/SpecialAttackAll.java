package game.content.combat.special;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.AncientMaceSpecialEffect;
import game.content.combat.effect.BandosGodswordSpecialEffect;
import game.content.combat.effect.BarrelchestAnchorSpecialEffect;
import game.content.combat.effect.DragonScimitarSpecialEffect;
import game.content.combat.effect.PoisonEffect;
import game.content.combat.effect.SaradominGodswordSpecialEffect;
import game.content.combat.effect.SaveDamage;
import game.content.combat.effect.ZamorakGodswordSpecialEffect;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.npc.Npc;
import game.player.Player;
import utility.Misc;

public class SpecialAttackAll {

	public static void specialAttackAll(Player attacker, Player victim, int weapon, boolean victimExists, int delay, Npc targetNpc, String itemName) {
		// Armadyl godsword
		if (GameType.isOsrs() && Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, weapon, 0) || GameType.isPreEoc() && weapon == 11694) {
			attacker.startAnimation(GameType.isOsrs() ? Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, weapon, 0, 1) : 11989);
			attacker.gfx0(GameType.isOsrs() ? 1211 : 2113);
			attacker.specDamage = 1.38;
			attacker.setSpecialAttackAccuracyMultiplier(2.70);
			attacker.setHitDelay(delay);
			attacker.specialAttackWeaponUsed[3] = 1;
			attacker.setWeaponAmountUsed(3);
			SpecialAttackBase.damageType = EntityDamageType.MELEE;
			SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
		}

		// Bandos godsword
		if (GameType.isPreEoc() && weapon == 11696 || GameType.isOsrs() && (weapon == 11804 || weapon == 20370)) {
			attacker.setSpecEffect(3);
			attacker.startAnimation(GameType.isOsrs() ? weapon == 20370 ? 7643 : 7642 : 11991);
			attacker.gfx0(GameType.isOsrs() ? 1212 : 2114);
			attacker.specDamage = 1.21;
			attacker.setSpecialAttackAccuracyMultiplier(1.35);
			attacker.setHitDelay(delay);
			attacker.specialAttackWeaponUsed[0] = 1;
			attacker.setWeaponAmountUsed(0);
			SpecialAttackBase.damageType = EntityDamageType.MELEE;
			SpecialAttackBase.effect.addEffect(new BandosGodswordSpecialEffect());
			SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
		}

		// Saradomin godsword
		if (GameType.isPreEoc() && weapon == 11698 || GameType.isOsrs() && (weapon == 11806 || weapon == 20372)) {
			attacker.setSpecEffect(4);
			attacker.startAnimation(GameType.isOsrs() ? weapon == 20372 ? 7641 : 7640 : 12019);
			attacker.gfx0(GameType.isOsrs() ? 1209 : 2109);
			attacker.specDamage = 1.10;
			attacker.setHitDelay(delay);
			attacker.specialAttackWeaponUsed[1] = 1;
			attacker.setWeaponAmountUsed(1);
			attacker.setSpecialAttackAccuracyMultiplier(2.0);
			SpecialAttackBase.damageType = EntityDamageType.MELEE;
			SpecialAttackBase.effect.addEffect(new SaradominGodswordSpecialEffect());
			SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
		}

		// Zamorak godsword
		if (GameType.isPreEoc() && weapon == 11700 || GameType.isOsrs() && (weapon == 11808 || weapon == 20374)) {
			attacker.setSpecEffect(2);
			attacker.startAnimation(GameType.isOsrs() ? weapon == 20374 ? 7639 : 7638 : 7070);
			attacker.gfx0(GameType.isOsrs() ? 1210 : 1221);
			attacker.setSpecialAttackAccuracyMultiplier(1.35);
			attacker.setHitDelay(delay);
			attacker.specDamage = 1.15;
			attacker.specialAttackWeaponUsed[4] = 1;
			attacker.setWeaponAmountUsed(4);
			if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
				targetNpc.gfx0(GameType.isOsrs() ? 369 : 2104);
			}
			SpecialAttackBase.damageType = EntityDamageType.MELEE;
			SpecialAttackBase.effect.addEffect(new ZamorakGodswordSpecialEffect());
			SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
		}

		// Dragon claws
		if (itemName.equals("Dragon claws")) {
			attacker.startAnimation(7514);
			attacker.gfx0(1171);
			attacker.setHitDelay(delay);
			attacker.setDragonClawsSpecialAttack(true);
			attacker.setMultipleDamageSpecialAttack(true);
			attacker.setSpecialAttackAccuracyMultiplier(1.25);
			attacker.setWeaponAmountUsed(15);
			if (victimExists) {
				MeleeFormula.applyNewDragonClawsSpecialAttack(attacker, victim);
				Combat.performBlockAnimation(victim, attacker);
				Combat.attackApplied(attacker, victim, "MELEE", true);
			}
			return;
		}

		switch (weapon) {

			// Dragon longsword
			case 1305 :
				attacker.gfx100(248);
				attacker.startAnimation(1058);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.3;
				attacker.specialAttackWeaponUsed[5] = 1;
				attacker.setWeaponAmountUsed(5);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				SoundSystem.sendSound(attacker, victim, 390, 0);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
				break;

			// Dragon spear
			// Zamorakian spear
			case 1249 :
			case 11824 :
			case 11889 : // Zamorakian hasta
				attacker.startAnimation(1064);
				attacker.gfx100(253);
				if (victimExists) {
					victim.getPA().getSpeared(attacker.getX(), attacker.getY());
					victim.gfx100(254);
					if (!victim.soundSent) {
						SoundSystem.sendSound(attacker, victim, 511, 450);
					}
					Combat.resetPlayerAttack(victim);
					attacker.turnPlayerTo(victim.getX(), victim.getY());
					Combat.performBlockAnimation(victim, attacker);
					Combat.attackApplied(attacker, victim, "MELEE", true);
				}
				attacker.setHitDelay(0);
				Combat.resetPlayerAttack(attacker);
				return;

			// Dragon Halberd
			case 3204 :
				attacker.gfx100(1172);
				attacker.startAnimation(1203);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.specDamage = 1.1;
				if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
					if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), targetNpc.getVisualX(), targetNpc.getVisualY(), 1)) {
						attacker.setMultipleDamageSpecialAttack(true);
					}
				}
				if (victimExists) {
					SpecialAttackBase.damageType = EntityDamageType.MELEE;
				}
				attacker.specialAttackWeaponUsed[7] = 1;
				attacker.setWeaponAmountUsed(7);
				break;

			// Dragon scimitar
			case 4587 :
			case 20000 :
				attacker.setSpecEffect(1);
				attacker.gfx100(347);
				attacker.startAnimation(1872);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.setHitDelay(delay);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new DragonScimitarSpecialEffect());
				break;

			// Dragon mace
			case 1434 :
				attacker.startAnimation(1060);
				attacker.gfx100(251);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.5;
				attacker.specialAttackWeaponUsed[9] = 1;
				attacker.setWeaponAmountUsed(9);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
				break;

			// Barrelchest Anchor
			case 10887 :
				attacker.gfx100(1027);
				attacker.startAnimation(5870);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.setHitDelay(delay);
				attacker.specialAttackWeaponUsed[10] = 1;
				attacker.setWeaponAmountUsed(10);
				attacker.setSpecEffect(5);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new BarrelchestAnchorSpecialEffect());
				SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
				break;

			// Dragon dagger
			case 1215 :
			case 1231 :
			case 5680 :
			case 5698 :
				attacker.gfx100(252);
				attacker.startAnimation(1062);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.35);
				attacker.specDamage = 1.15;
				attacker.setMultipleDamageSpecialAttack(true);
				attacker.specialAttackWeaponUsed[11] = 2;
				attacker.setWeaponAmountUsed(11);
				SoundSystem.sendSound(attacker, victim, 385, 0);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new SaveDamage("SECOND"));
				if (victimExists) {
					attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, MeleeFormula.calculateMeleeDamage(attacker, victim, 0), 3, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false).addEffect(new SaveDamage("FIRST")));
					if (itemName.contains("p+")) {
						SpecialAttackBase.effect.addEffect(new PoisonEffect(6));
					}
				}
				break;

			// Saradomin sword
			case 11838 :
				if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
					targetNpc.gfx100(1196);
				}
				attacker.startAnimation(1132);
				attacker.gfx100(1213);
				attacker.setHitDelay(delay);
				attacker.saradominSwordSpecialAttack = true;
				attacker.setMultipleDamageSpecialAttack(true);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.specDamage = 1.1;
				attacker.specialAttackWeaponUsed[13] = 1;
				attacker.setWeaponAmountUsed(13);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
				if (victimExists) {
					victim.gfx(1196, 0);
					int magicDamage = Misc.random(0, 16);
					if (Misc.random(attacker.playerBonus[ServerConstants.SLASH_ATTACK_BONUS]) < Misc.random(victim.playerBonus[ServerConstants.MAGIC_DEFENCE_BONUS])) {
						magicDamage = 0;
					}
					attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, magicDamage, 3, EntityDamageType.MAGIC, 16, false, false).addEffect(new SaveDamage("FIRST")));
					Skilling.addSkillExperience(attacker, magicDamage * 2, ServerConstants.MAGIC, false);
					Combat.performBlockAnimation(victim, attacker);
					Combat.attackApplied(attacker, victim, "MELEE", true);
				}
				return;

			// Ancient mace
			case 11061 :
				attacker.startAnimation(6147);
				attacker.gfx0(1052);
				attacker.setHitDelay(delay);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new AncientMaceSpecialEffect());
				break;
		}
	}

}
