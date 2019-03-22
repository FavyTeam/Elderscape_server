package game.content.combat.special;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.AbyssalTentacleSpecialEffect;
import game.content.combat.effect.AbyssalWhipSpecial;
import game.content.combat.effect.DragonWarhammerSpecialEffect;
import game.content.combat.effect.MorriganThrowingAxeSpecialEffect;
import game.content.combat.effect.MorrigansJavelinSpecialEffect;
import game.content.combat.effect.SaveDamage;
import game.content.combat.effect.StatiusWarhammerSpecialEffect;
import game.content.combat.effect.VestaSpearSpecialEffect;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedAmmoUsed;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.music.SoundSystem;
import game.npc.Npc;
import game.player.Player;
import utility.Misc;

public class SpecialAttackOsrs {

	public static void specialAttackOsrs(Player attacker, Player victim, int weapon, boolean victimExists, int delay, Npc targetNpc, String itemName) {
		if (!GameType.isOsrs()) {
			return;
		}

		if (itemName.equals("Heavy ballista") || itemName.equals("Light ballista")) {
			attacker.setUsingMediumRangeRangedWeapon(true);
			attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
			attacker.startAnimation(7222);
			attacker.setSpecialAttackAccuracyMultiplier(1.65);
			attacker.specDamage = 1.25;
			attacker.setProjectileStage(1);
			attacker.setHitDelay(delay);
			if (victimExists) {
				Combat.fireProjectilePlayer(attacker, victim);
				SpecialAttackBase.damageType = EntityDamageType.RANGED;
				if (itemName.equals("Heavy ballista")) {
					SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
				}
			} else if (attacker.getNpcIdAttacking() > 0) {
				CombatNpc.fireProjectileNpc(attacker);
			}
			attacker.specialAttackWeaponUsed[32] = 1;
			attacker.setWeaponAmountUsed(32);
			attacker.rangedSpecialAttackOnNpc = true;
		}

		if (itemName.equals("Armadyl crossbow")) {
			attacker.armadylCrossbowSpecial = true;
			attacker.setUsingMediumRangeRangedWeapon(true);
			attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
			RangedAmmoUsed.deleteAmmo(attacker);
			attacker.setProjectileStage(1);
			attacker.startAnimation(4230);
			attacker.setHitDelay(delay);
			if (victimExists) {
				Combat.fireProjectilePlayer(attacker, victim);
				SpecialAttackBase.damageType = EntityDamageType.RANGED;
			} else if (attacker.getNpcIdAttacking() > 0) {
				CombatNpc.fireProjectileNpc(attacker);
			}
			attacker.setSpecialAttackAccuracyMultiplier(2.0);
			attacker.rangedSpecialAttackOnNpc = true;
		}

		// Abyssal tentacle
		if (Combat.hasAbyssalTentacle(attacker, weapon)) {
			if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
				targetNpc.gfx100(341);
				targetNpc.setFrozenLength(5000);
				if (Misc.hasPercentageChance(25)) {
					CombatNpc.applyPoisonOnNpc(attacker, targetNpc, 4);
				}
			}
			if (victimExists) {
				SpecialAttackBase.effect.addEffect(new AbyssalTentacleSpecialEffect());
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
			}
			attacker.startAnimation(1658);
			attacker.setHitDelay(delay);
			SoundSystem.sendSound(attacker, victim, 1081, 300);
		}
		switch (weapon) {

			// Vesta's longsword
			case 22613 :
				attacker.startAnimation(10999);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.2;
				attacker.setSpecialAttackAccuracyMultiplier(1.7);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 22622: // statius warhammer
				if (victimExists) {
					SpecialAttackBase.effect.addEffect(new StatiusWarhammerSpecialEffect());
				}
				attacker.startAnimation(1378);
				//attacker.gfx0(1292);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.specDamage = 1.25;
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 22610: // vesta spear
				if (victimExists) {
					SpecialAttackBase.effect.addEffect(new VestaSpearSpecialEffect());
				}
				attacker.startAnimation(8184);
				attacker.gfx0(1627);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.specDamage = 1.25;
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 22636: // morrigan javelin
				attacker.getAttributes().put(Player.MORRIGANS_JAVS_SPECIAL, true);
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.startAnimation(806);
				attacker.gfx100(1621);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.setHitDelay(delay);
				SpecialAttackBase.damageType = EntityDamageType.RANGED;
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					SpecialAttackBase.effect.addEffect(new MorrigansJavelinSpecialEffect());
				}
				else if (attacker.getNpcIdAttacking() > 0) {
					CombatNpc.fireProjectileNpc(attacker);
				}
				SoundSystem.sendSound(attacker, victim, 386, 180);
				break;

			case 22634: // morrigan axe
				attacker.getAttributes().put(Player.MORRIGANS_AXE_SPECIAL, true);
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.startAnimation(929);
				attacker.gfx100(1626);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.specDamage = Misc.random(120, 220) / 100.0; // 20 - 120%
				attacker.setHitDelay(delay);
				SpecialAttackBase.damageType = EntityDamageType.RANGED;
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					SpecialAttackBase.effect.addEffect(new MorriganThrowingAxeSpecialEffect());
				}
				else if (attacker.getNpcIdAttacking() > 0) {
					CombatNpc.fireProjectileNpc(attacker);
				}
				SoundSystem.sendSound(attacker, victim, 386, 180);
				break;
			case 13271 : // Abyssal dagger.
				attacker.startAnimation(3300);
				attacker.gfx0(1283);
				attacker.specDamage = 0.85;
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.setHitDelay(delay);
				attacker.specialAttackWeaponUsed[33] = 2;
				attacker.setWeaponAmountUsed(33);
				SoundSystem.sendSound(attacker, victim, 385, 0);
				attacker.setMultipleDamageSpecialAttack(true);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new SaveDamage("SECOND"));
				if (victimExists) {
					attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, MeleeFormula.calculateMeleeDamage(attacker, victim, 0), 3, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false).addEffect(new SaveDamage("FIRST")));
				}
				break;

			// Dragon warhammer
			case 13576 :
				attacker.startAnimation(1378);
				attacker.gfx0(1292);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.50;
				attacker.specialAttackWeaponUsed[2] = 1;
				attacker.setWeaponAmountUsed(2);
				attacker.setSpecEffect(6);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				SpecialAttackBase.effect.addEffect(new DragonWarhammerSpecialEffect());
				SpecialAttackBase.effect.addEffect(new SaveDamage("FIRST"));
				break;

			case 21009 : // Dragon sword
				attacker.dragonSwordSpecial = true;
				attacker.startAnimation(7515);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.25;
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				SoundSystem.sendSound(attacker, victim, 390, 0);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 12808 : // Sara's blessed sword.
				if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
					targetNpc.gfx100(1196);
				}
				if (victimExists) {
					victim.gfx(1196, 0);
				}
				attacker.startAnimation(1133);
				attacker.gfx100(1213);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.5);
				attacker.specDamage = 1.25;
				attacker.specialAttackWeaponUsed[13] = 2;
				attacker.setWeaponAmountUsed(13);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 4151 : // Abyssal whip
			case 12773 : // Volcanic abyssal whip
			case 12774 : // Frozen abyssal whip
			case 15_441 :
			case 15_442 :
			case 15_443 :
			case 15_444 :
				if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
					targetNpc.gfx100(341);
				}
				if (victimExists) {
					SpecialAttackBase.effect.addEffect(new AbyssalWhipSpecial());
				}
				attacker.startAnimation(1658);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				SoundSystem.sendSound(attacker, victim, 1081, 300);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			// Dragon thrownaxe
			case 20849 :
				if (attacker.getNpcIdAttacking() > 0) {
					attacker.startAnimation(7521);
					attacker.gfx100(1317);
					attacker.setSpecialAttackAccuracyMultiplier(1.25);
					attacker.setHitDelay(delay);
					attacker.setWeaponAmountUsed(0);
					SpecialAttackBase.damageType = EntityDamageType.RANGED;
					CombatNpc.fireProjectileNpc(attacker);
					attacker.rangedSpecialAttackOnNpc = true;
				}
				break;

			// Toxic blowpipe.
			case 12926 :
				attacker.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(true);
				attacker.startAnimation(5061);
				attacker.specDamage = 1.50;
				attacker.setProjectileStage(1);
				attacker.blowpipeSpecialAttack = true;
				attacker.setHitDelay(delay);
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					SpecialAttackBase.damageType = EntityDamageType.RANGED;
				} else if (attacker.getNpcIdAttacking() > 0) {
					CombatNpc.fireProjectileNpc(attacker);
				}
				attacker.rangedSpecialAttackOnNpc = true;
				break;

			case 861 : // Magic shortbow.
			case 12788 : // Magic shortbow (i).
				attacker.setBowSpecShot(1);
				attacker.setUsingMediumRangeRangedWeapon(true);
				attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
				RangedAmmoUsed.deleteAmmo(attacker);
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.startAnimation(1074);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.setProjectileStage(1);
				attacker.setHitDelay(delay);
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					attacker.setMagicBowSpecialAttack(true);
					Combat.fireProjectilePlayer(attacker, victim);
					SpecialAttackBase.damageType = EntityDamageType.RANGED;
					SpecialAttackBase.effect.addEffect(new SaveDamage("SECOND"));
					attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, RangedFormula.calculateRangedDamage(attacker, victim, false), 4, EntityDamageType.RANGED, attacker.maximumDamageRanged, true, false).addEffect(new SaveDamage("FIRST")));
				} else if (attacker.getNpcIdAttacking() > 0) {
					CombatNpc.fireProjectileNpc(attacker);
					attacker.setMagicBowSpecialAttack(true);
					CombatNpc.fireProjectileNpc(attacker);
				}
				attacker.specialAttackWeaponUsed[25] = 2;
				attacker.setWeaponAmountUsed(25);
				attacker.rangedSpecialAttackOnNpc = true;
				SoundSystem.sendSound(attacker, victim, 386, 180);
				break;

			case 11235 :
			case 12765 :
			case 12766 :
			case 12767 :
			case 12768 :
				// Dark bow
				attacker.setUsingMediumRangeRangedWeapon(true);
				attacker.setUsingDarkBowSpecialAttack(true);
				attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
				RangedAmmoUsed.deleteAmmo(attacker);
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.startAnimation(426);
				attacker.setProjectileStage(1);
				attacker.gfx100(Combat.getRangeStartGFX(attacker));
				attacker.setHitDelay(delay);
				if (attacker.getCombatStyle(ServerConstants.RAPID)) {
					attacker.setAttackTimer(attacker.getAttackTimer() - 1);
				}
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					int delayOther = 60;
					if (victimExists) {
						int distanceFromTarget = attacker.getPA().distanceToPoint(victim.getX(), victim.getY());
						if (distanceFromTarget >= 3) {
							delayOther += 20;
						}
						victim.gfxDelay(1100, delayOther, 100);
						SpecialAttackBase.damageType = EntityDamageType.RANGED;
						SpecialAttackBase.effect.addEffect(new SaveDamage("SECOND"));
						attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, RangedFormula.calculateRangedDamage(attacker, victim, false), distanceFromTarget >= 3 ? 6 : 5, EntityDamageType.RANGED, attacker.maximumDamageRanged, true, false).addEffect(new SaveDamage("FIRST")));

					}
				} else if (attacker.getNpcIdAttacking() > 0) {
					CombatNpc.fireProjectileNpc(attacker);
				}
				attacker.setSpecialAttackAccuracyMultiplier(1.20);
				attacker.specDamage = 1.5;
				attacker.specialAttackWeaponUsed[27] = 2;
				attacker.setWeaponAmountUsed(27);
				attacker.rangedSpecialAttackOnNpc = true;
				break;

			case 21902 : // Dragon crossbow.
				if (ServerConfiguration.DEBUG_MODE) {
					attacker.dragonCrossbowSpecial = true;
					attacker.setUsingMediumRangeRangedWeapon(true);
					attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
					RangedAmmoUsed.deleteAmmo(attacker);
					attacker.setProjectileStage(1);
					attacker.startAnimation(4230);
					attacker.setHitDelay(delay);
					if (victimExists) {
						Combat.fireProjectilePlayer(attacker, victim);
						SpecialAttackBase.damageType = EntityDamageType.RANGED;
					} else if (attacker.getNpcIdAttacking() > 0) {
						CombatNpc.fireProjectileNpc(attacker);
					}
					attacker.specDamage = 1.2;
					attacker.rangedSpecialAttackOnNpc = true;
				}
				break;
		}
	}

}
