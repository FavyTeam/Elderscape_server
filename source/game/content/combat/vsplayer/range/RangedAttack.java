package game.content.combat.vsplayer.range;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.HolidayItem;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.AkrisaeRobeEffect;
import game.content.combat.effect.ChinchompaEffect;
import game.content.combat.effect.DominionGloveEffect;
import game.content.combat.effect.DropAmmoEffect;
import game.content.combat.effect.GfxEndEffect;
import game.content.combat.effect.KarilSetEffect;
import game.content.combat.effect.RubyBoltEffect;
import game.content.combat.effect.SaveDamage;
import game.content.combat.effect.VenomEffect;
import game.content.combat.vsplayer.AttackPlayer;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Handle the ranged attack.
 *
 * @author MGT Madness, created on 28-03-2015.
 */
public class RangedAttack {

	/**
	 * Initiate the ranged attack.
	 *
	 * @param attacker The player initiating the attack.
	 * @param victim The player receiving the attack.
	 */
	public static boolean landRangedAttack(Player attacker, Player victim, boolean specialWeapon) {

		if (!attacker.getUsingRanged()) {
			return false;
		}

		if (HolidayItem.isHolidayItem(attacker, victim) || attacker.getWieldedWeapon() == 7671 || attacker.getWieldedWeapon() == 7673) {
			Combat.setAttackTimer(attacker);
			return true;
		}

		if (RangedFormula.applyDragonThrownAxeDamage(attacker, victim)) {
			return true;
		}
		if (specialWeapon) {
			return true;
		}
		Combat.setAttackTimer(attacker);
		if (AttackPlayer.landSpecialAttack(attacker, victim)) {
			return true;
		}
		Combat.attackApplied(attacker, victim, "RANGED", false);
		attacker.startAnimation(RangedData.getRangedAttackEmote(attacker));
		int damageAmount = RangedFormula.calculateRangedDamage(attacker, victim, false);
		int delay = Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase());
		EntityDamage<Player, Player> damage = new EntityDamage<Player, Player>(victim, attacker, damageAmount, delay, EntityDamageType.RANGED, attacker.maximumDamageRanged, true, false);
		if (attacker.showDiamondBoltGFX) {
			damage.addEffect(new GfxEndEffect(758, 0));
		} else if (attacker.showOnyxBoltGfx) {
			damage.addEffect(new GfxEndEffect(753, 0));
		}
		else if (attacker.showOpalBoltGFX) {
			damage.addEffect(new GfxEndEffect(749, 0));
		} else if (attacker.showDragonBoltGFX) {
			damage.addEffect(new GfxEndEffect(756, 0));
			attacker.specialAttackWeaponUsed[29] = 1;
			attacker.setWeaponAmountUsed(29);
			damage.addEffect(new SaveDamage("FIRST"));
		} else if (attacker.showRubyBoltGFX) {
			damage.addEffect(new GfxEndEffect(754, 0));
			damage.addEffect(new RubyBoltEffect());
		}
		
		if (GameType.isPreEoc()) {
			if (DominionGloveEffect.hasSwiftGloves(attacker)) {
				damage.addEffect(new DominionGloveEffect());
			}
			if (AkrisaeRobeEffect.fullAkrisae(attacker)) {
				damage.addEffect(new AkrisaeRobeEffect());
			}
		}

		if (attacker.getWieldedWeapon() == 10033 || attacker.getWieldedWeapon() == 10034 || attacker.getWieldedWeapon() == 11959) {
			damage.addEffect(new ChinchompaEffect());
			damage.addEffect(new GfxEndEffect(157, 100));
		}

		// Toxic blowpipe
		if (attacker.getWieldedWeapon() == 12926 && Misc.hasPercentageChance(25)) {
			damage.addEffect(new VenomEffect());
		}
		if (Combat.wearingFullKaril(attacker) && Misc.hasPercentageChance(25)) {
			damage.addEffect(new KarilSetEffect());
		}
		RangedAmmoUsed.deleteAmmo(attacker);
		damage.addEffect(new DropAmmoEffect());
		attacker.getIncomingDamageOnVictim().add(damage);

		if (CombatConstants.isDarkBow(attacker.getWieldedWeapon())) {
			attacker.setUsingDarkBowNormalAttack(true);
			damageAmount = RangedFormula.calculateRangedDamage(attacker, victim, false);
			delay = Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase());
			damage = new EntityDamage<Player, Player>(victim, attacker, damageAmount, delay, EntityDamageType.RANGED, attacker.maximumDamageRanged, true, false);
			RangedAmmoUsed.deleteAmmo(attacker);
			damage.addEffect(new DropAmmoEffect());
			attacker.getIncomingDamageOnVictim().add(damage);
		}
		if (Combat.getRangeStartGFX(attacker) > 0) {
			attacker.gfx100(Combat.getRangeStartGFX(attacker));
		}
		Combat.fireProjectilePlayer(attacker, victim);
		return true;
	}

	/**
	 * @param attacker
	 * @param victim
	 * @return True, if the attacker has the Ranged requirements to attack the victim.
	 */
	public static boolean hasRangedRequirements(Player attacker, Player victim) {
		boolean usingMediumRangeRangedWeapon = RangedData.isWieldingMediumRangeRangedWeapon(attacker);
		boolean isWieldingRangedWeaponWithNoArrowSlotRequirement = RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(attacker);
		boolean hasArrowEquipped = attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= 0 ? false : true;

		if (attacker.getDuelStatus() == ServerConstants.DUELING && attacker.duelRule[2]) {
			attacker.playerAssistant.sendMessage("Range has been disabled in this duel!");
			Movement.stopMovement(attacker);
			Combat.resetPlayerAttack(attacker);
			return false;
		}

		if (usingMediumRangeRangedWeapon || isWieldingRangedWeaponWithNoArrowSlotRequirement) {
			if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, CombatConstants.getRangedWeaponDistance(attacker))) {
				return false;
			}
		}

		if (!RangedData.hasRequiredAmmo(attacker, hasArrowEquipped)) {
			Movement.stopMovement(attacker);
			Combat.resetPlayerAttack(attacker);
			return false;
		}

		if (Area.inMageArena(victim.getX(), victim.getY(), victim.getHeight())) {
			Movement.stopMovement(attacker);
			Combat.resetPlayerAttack(attacker);
			attacker.getPA().sendMessage("You cannot use ranged inside the mage arena.");
			return false;
		}
		if (!Region.isStraightPathUnblockedProjectiles(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), victim.getHeight(), 1, 1, true)) {
			return false;
		}


		return true;
	}

	public static void reEngageWithRanged(Player attacker) {
		if (!attacker.hasLastCastedMagic()) {
			if (RangedData.isWieldingMediumRangeRangedWeapon(attacker) || RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(attacker)) {
				attacker.setUsingRanged(true);
			}
		}

	}

}
