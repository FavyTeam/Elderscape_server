package game.content.combat.vsplayer.melee;

import core.GameType;
import core.ServerConstants;
import game.bot.BotCommunication;
import game.bot.BotContent;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.AkrisaeRobeEffect;
import game.content.combat.effect.DominionGloveEffect;
import game.content.combat.effect.GuthanSetEffect;
import game.content.combat.effect.PoisonEffect;
import game.content.combat.effect.SaveDamage;
import game.content.combat.effect.ToragSetEffect;
import game.content.combat.effect.VenomEffect;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.miscellaneous.TradeAndDuel;
import game.content.prayer.PrayerManager;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.movement.Movement;
import utility.Misc;

/**
 * General melee related.
 *
 * @author MGT Madness, created on 08-02-2014.
 */
public class MeleeAttack {

	/**
	 * A normal single hitsplat damage melee attack.
	 *
	 * @param attacker The player attacking.
	 * @param theVictim The player being attacked.
	 * @param specialAttack True, if the attacker is using a special attack.
	 */
	public static boolean normalMeleeAttack(Player attacker, Player victim, boolean specialWeapon) {
		if (attacker.hasLastCastedMagic() || attacker.getUsingRanged()) {
			return false;
		}

		if (MeleeFormula.applyGraniteMaulDamage(attacker, victim)) {
			return true;
		}
		if (specialWeapon) {
			return true;
		}
		Combat.setAttackTimer(attacker);
		if (AttackPlayer.landSpecialAttack(attacker, victim)) {
			return true;
		}
		Combat.attackApplied(attacker, victim, "MELEE", false);
		attacker.setMeleeFollow(true);
		attacker.startAnimation(MeleeData.getWeaponAnimation(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
		int damageAmount = MeleeFormula.calculateMeleeDamage(attacker, victim, 1);
		int delay = Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase());

		EntityDamage<Player, Player> damage = new EntityDamage<Player, Player>(victim, attacker, damageAmount, delay, EntityDamageType.MELEE, attacker.maximumDamageMelee, true, false);

		PrayerManager.handleCombatPrayer(attacker, victim, damage.getDamage());

		// Dragon dagger p++ and Abyssal dagger p++.
		if ((attacker.getWieldedWeapon() == 5698 || attacker.getWieldedWeapon() == 13271) && Misc.hasPercentageChance(30)) {
			damage.addEffect(new PoisonEffect(6));
		}
			// Abyssal tentacle.
		if (Combat.hasAbyssalTentacle(attacker, attacker.getWieldedWeapon()) && Misc.hasPercentageChance(25)) {
				damage.addEffect(new PoisonEffect(4));
		}
		// Toxic staff of the dead
		if ((attacker.getWieldedWeapon() == 12904 || attacker.getWieldedWeapon() == 16209 || attacker.getWieldedWeapon() == 16272) && Misc.hasPercentageChance(25)) {
			damage.addEffect(new VenomEffect());
		}
		if (Combat.wearingFullGuthan(attacker) & Misc.hasPercentageChance(25) && damageAmount > 0) {
			damage.addEffect(new GuthanSetEffect());
		}
		if (Combat.wearingFullTorag(attacker) && Misc.hasPercentageChance(25) && damageAmount > 0) {
			damage.addEffect(new ToragSetEffect());
		}

		if (GameType.isPreEoc()) {
			if (DominionGloveEffect.hasGoliathGloves(attacker)) {
				damage.addEffect(new DominionGloveEffect());
			}
			if (AkrisaeRobeEffect.fullAkrisae(attacker)) {
				damage.addEffect(new AkrisaeRobeEffect());
			}
		}

		damage.addEffect(new SaveDamage("FIRST"));
		attacker.getIncomingDamageOnVictim().add(damage);
		Combat.performBlockAnimation(victim, attacker);
		return true;
	}

	/**
	 * Grab the maximum melee damage of the attacker, to use with the 634 hitsplats criticals.
	 *
	 * @param attacker The player attacking.
	 */
	public static void saveCriticalDamage(Player attacker) {
			attacker.maximumDamageMelee = MeleeFormula.getMaximumMeleeDamage(attacker);
	}

	public static boolean hasMeleeRequirements(Player attacker, Player victim) {
		if (attacker.duelRule[TradeAndDuel.NO_MELEE] && attacker.getDuelStatus() == ServerConstants.DUELING) {
			attacker.playerAssistant.sendMessage("Melee has been disabled in this duel!");
			return false;
		}

		if (GameType.isPreEoc()) {
			if (System.currentTimeMillis() - victim.immuneToMeleeAttacks < 5000) {
				attacker.getPA().sendMessage("This player is currently immune to melee attacks.");
				return false;
			}
		}

		if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, Combat.getRequiredDistance(attacker))) {
			if (attacker.isFrozen()) {
				Combat.resetPlayerAttack(attacker);
			} else {
				attacker.ignorePlayerTurn = true;
			}
			return false;
		}

		if (attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, Combat.getRequiredDistance(attacker))) {
			if (attacker.playerAssistant.isDiagonalFromTarget(victim)) {
				if (attacker.isFrozen()) {
					Combat.resetPlayerAttack(attacker);
				} else {
					attacker.ignorePlayerTurn = true;
				}

				// They kite the bot around a tree where the bot is diagonal from the player, so the bot just stays there forever.
				if (attacker.bot) {
					if (attacker.botDiagonalTicks >= 6) {
						if (!attacker.prayerActive[attacker.botLastDamageTakenType]) {
							BotContent.togglePrayer(attacker, attacker.botLastDamageTakenType, true);
						}
						attacker.setBotStatus("LOOTING");
						BotContent.retreatToBank(attacker, false);
						BotCommunication.sendBotMessage(attacker, "?", false);
						BotContent.walkToBankArea(attacker);
						attacker.botDiagonalTicks = 0;
					} else {
						attacker.botDiagonalTicks++;
					}
				}
				return false;
			}
		}

		if (Area.inMageArena(victim.getX(), victim.getY(), victim.getHeight())) {
			Movement.stopMovement(attacker);
			Combat.resetPlayerAttack(attacker);
			attacker.getPA().sendMessage("You cannot use melee inside the mage arena.");
			return false;
		}

		if (!Region.isStraightPathUnblocked(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), victim.getHeight(), 1, 1, false)) {
			return false;
		}

		return true;

	}
}
