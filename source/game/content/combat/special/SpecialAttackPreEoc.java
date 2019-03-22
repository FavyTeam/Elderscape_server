package game.content.combat.special;

import core.GameType;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.AbyssalVineWhipSpecialEffect;
import game.content.combat.effect.KorasiSwordSpecialEffect;
import game.content.combat.effect.MorriganThrowingAxeSpecialEffect;
import game.content.combat.effect.MorrigansJavelinSpecialEffect;
import game.content.combat.effect.StatiusWarhammerSpecialEffect;
import game.content.combat.effect.VestaSpearSpecialEffect;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.range.RangedAmmoUsed;
import game.content.music.SoundSystem;
import game.entity.Entity;
import game.npc.Npc;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

public class SpecialAttackPreEoc {

	public static void specialAttackPreEoc(Player attacker, Player victim, int weapon, boolean victimExists, int delay, Npc targetNpc, String itemName) {
		if (!GameType.isPreEoc()) {
			return;
		}
		switch (weapon) {
			case 13_899 : // vls
				attacker.startAnimation(10502);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.45;
				attacker.setSpecialAttackAccuracyMultiplier(1.7);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 13_905 : // statius warhammer
				if (victimExists) {
					SpecialAttackBase.effect.addEffect(new StatiusWarhammerSpecialEffect());
				}
				attacker.startAnimation(10499);
				attacker.gfx0(1835);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.specDamage = 1.25;
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 13_902 : // vesta spear
				if (victimExists) {
					SpecialAttackBase.effect.addEffect(new VestaSpearSpecialEffect());
				}
				attacker.startAnimation(10505);
				attacker.gfx0(1840);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				attacker.specDamage = 1.25;
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 13_879 : // morrigan javelin
				if (attacker.getNpcIdAttacking() > 0) {
					attacker.getPA().sendMessage("This special attack can only be used on players.");
					break;
				}
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.startAnimation(10_501);
				attacker.gfx0(1836);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.setHitDelay(delay);
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					SpecialAttackBase.damageType = EntityDamageType.RANGED;
					SpecialAttackBase.effect.addEffect(new MorrigansJavelinSpecialEffect());
				}
				SoundSystem.sendSound(attacker, victim, 386, 180);
				break;

			case 13_883 : // morrigan axe
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.startAnimation(10_504);
				attacker.gfx0(1838);
				attacker.setSpecialAttackAccuracyMultiplier(1.2);
				attacker.setHitDelay(delay);
				if (victimExists) {
					Combat.fireProjectilePlayer(attacker, victim);
					SpecialAttackBase.damageType = EntityDamageType.RANGED;
					SpecialAttackBase.effect.addEffect(new MorriganThrowingAxeSpecialEffect());
				}
				attacker.specDamage = 1.20;
				SoundSystem.sendSound(attacker, victim, 386, 180);
				break;

			case 19_780 : // Korasi
				final boolean playerVictim = victimExists;
				attacker.getEventHandler().addEvent(attacker, new CycleEvent<Entity>() {
					@Override
					public void execute(CycleEventContainer<Entity> container) {
						if (playerVictim) {
							SpecialAttackBase.effect.addEffect(new KorasiSwordSpecialEffect());
						} else if (targetNpc != null && attacker.getNpcIdAttacking() > 0) {
							KorasiSwordSpecialEffect.onNpc(attacker, targetNpc);
						}
						container.stop();
					}

					@Override
					public void stop() {

					}
				}, 1);
				attacker.startAnimation(14788);
				attacker.gfx0(1729);
				attacker.setHitDelay(delay);
				attacker.specDamage = 1.55;
				attacker.setSpecialAttackAccuracyMultiplier(2.0);
				SpecialAttackBase.damageType = EntityDamageType.MAGIC;
				break;

			case 21_371 : // Abyssal vine whip
				if (victimExists) {
					SpecialAttackBase.effect.addEffect(new AbyssalVineWhipSpecialEffect());
				}
				attacker.startAnimation(1658);
				attacker.setHitDelay(delay);
				attacker.setSpecialAttackAccuracyMultiplier(1.25);
				SoundSystem.sendSound(attacker, victim, 1081, 300);
				SpecialAttackBase.damageType = EntityDamageType.MELEE;
				break;

			case 15241 : // hand cannon
				attacker.startAnimation(12175);
				attacker.setHitDelay(7);
				attacker.setAttackTimer(9);
				RangedAmmoUsed.deleteAmmo(attacker);
				final boolean foundVictim = victimExists;
				final Player player = victim;
				attacker.getEventHandler().addEvent(attacker, new CycleEvent<Entity>() {

					@Override
					public void execute(CycleEventContainer<Entity> container) {
						container.stop();
					}

					@Override
					public void stop() {
						attacker.gfx0(2138);
						if (foundVictim) {
							Combat.fireProjectilePlayer(attacker, player);
						} else if (attacker.getNpcIdAttacking() > 0) {
							CombatNpc.fireProjectileNpc(attacker);
						}
					}
				}, 3);
				break;
		}
	}

}
