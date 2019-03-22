package game.content.combat.vsplayer.magic;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.HolidayItem;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.effect.AkrisaeRobeEffect;
import game.content.combat.effect.DominionGloveEffect;
import game.content.combat.effect.IceBarrageBloodBarrageEffect;
import game.content.combat.effect.MagicSpellEffects;
import game.content.combat.effect.VenomEffect;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.Effects;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.TradeAndDuel;
import game.content.quicksetup.QuickSetUp;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Handle the magic attack.
 *
 * @author MGT Madness, created on 28-03-2015.
 */
public class MagicAttack {

	/**
	 * Initiate the magic attack.
	 *
	 * @param attacker The player initiating the attack.
	 * @param victim The player receiving the attack.
	 */
	public static void landMagicAttack(Player attacker, Player victim) {
		if (!attacker.hasLastCastedMagic()) {
			return;
		}

		if (HolidayItem.isHolidayItem(attacker, victim) || attacker.getWieldedWeapon() == 7671 || attacker.getWieldedWeapon() == 7673) {
			return;
		}
		if (attacker.getSpellId() == -1) {
			return;
		}
		Combat.attackApplied(attacker, victim, "MAGIC", false);
		attacker.setLastUsedMagic(System.currentTimeMillis());
		attacker.setUsingMagic(true);
		MagicData.requiredRunes(attacker, attacker.getSpellId(), "DELETE RUNES");
		attacker.startAnimation(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][2]);
		Combat.addBarragesCasted(attacker);
		attacker.setLastCastedMagic(true);
		attacker.setMagicSplash(MagicFormula.isSplash(attacker, victim));
		if (attacker.isMagicSplash()) {
			victim.botLastDamageTakenType = ServerConstants.PROTECT_FROM_MAGIC;
		}
		boolean showXpDrop = true;
		// Teleblock spell.
		if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12445) {
			showXpDrop = false;
		}
		int damageAmount = MagicFormula.calculateMagicDamage(attacker, victim);
		int hitDelay = Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase());
		EntityDamage<Player, Player> damage = new EntityDamage<Player, Player>(victim, attacker, damageAmount, hitDelay, EntityDamageType.MAGIC, attacker.getMaximumDamageMagic(), showXpDrop, attacker.isMagicSplash());
		if (Combat.usingMultiSpell(attacker)) {
			damage.addEffect(new IceBarrageBloodBarrageEffect());
		} // Toxic staff of the dead.
		if ((attacker.getWieldedWeapon() == 12904 || attacker.getWieldedWeapon() == 16209 || attacker.getWieldedWeapon() == 16272) && Misc.hasPercentageChance(25)) {
			damage.addEffect(new VenomEffect());
		}

		// Delete gfx
		victim.gfx0(65535);

		// Apply the gfx on the v	ictim depending on distance.
		int delay = 43;
		int targetDistance = attacker.getPA().distanceToPoint(victim.getX(), victim.getY());
		delay += targetDistance * 11;

		if (victim.barrageOrb == 1 && CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12891
				&& GameType.isPreEoc()) {
			victim.gfx(1677, 0);
		} else
		if (Combat.getEndGfxHeight(attacker, CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0]) == 100 && !attacker.isMagicSplash()) {
			victim.gfxDelay(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][5], delay, 100);
		} else if (!attacker.isMagicSplash()) {
			victim.gfxDelay(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][5], delay, 0);
		} else if (attacker.isMagicSplash()) {
			victim.gfxDelay(85, delay, 120);
		}

		if (GameType.isPreEoc()) {
			if (DominionGloveEffect.hasSpellcasterGloves(attacker)) {
				damage.addEffect(new DominionGloveEffect());
			}
			if (AkrisaeRobeEffect.fullAkrisae(attacker)) {
				damage.addEffect(new AkrisaeRobeEffect());
			}
		}

		damage.addEffect(new MagicSpellEffects(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0]));

		if (Combat.wearingFullAhrim(attacker) && Misc.hasPercentageChance(25)) {
			if (Combat.hasAmuletOfTheDamned(attacker)) {
			}
			Effects.ahrimsEffect(attacker, victim);

		}
		int pX = attacker.getX();
		int pY = attacker.getY();
		int nX = victim.getX();
		int nY = victim.getY();
		int offX = (pY - nY) * -1;
		int offY = (pX - nX) * -1;
		attacker.setProjectileStage(2);
		if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3] > 0) {
			if (Combat.getStartGfxHeight(attacker) == 100) {
				attacker.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3]);
			} else {
				attacker.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3]);
			}
		}
		if (victim.isFrozen()) {
			victim.barrageOrb = 1;
		} else {
			victim.barrageOrb = 0;
		}
		if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4] > 0) {
			int projectileId = CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4];
			// Teleblock spell
			if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12445 && attacker.isMagicSplash()) {
				projectileId = 1300;
			}
			int speed = 60;
			int distanceFromTarget = attacker.getPA().distanceToPoint(victim.getX(), victim.getY());
			if (distanceFromTarget >= 2) {
				speed += 20;
			}
			if (distanceFromTarget >= 5) {
				speed += 25;
			}
			if (distanceFromTarget >= 8) {
				speed += 30;
			}
			attacker.getPA()
					.createPlayersProjectile(pX, pY, offX, offY, 50, speed, projectileId, Combat.getStartHeight(attacker), Combat.getEndHeight(attacker), -victim.getPlayerId() - 1,
			                                 Combat.getStartDelay(attacker), Combat.getProjectileSlope(attacker));
		}
		attacker.setOldSpellId(attacker.getSpellId());
		if (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0] == 12891 && victim.isMoving()) {
			attacker.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, -victim.getPlayerId() - 1, Combat.getStartDelay(attacker),
			                                         Combat.getProjectileSlope(attacker));
		}
		long freezeDelay = Combat.getFreezeTime(attacker); // freeze time
		if (freezeDelay > 0 && victim.canBeFrozen() && !attacker.isMagicSplash()) {
			if (!victim.bot || victim.getPlayerName().equals("Remy E")) {
				Movement.stopMovement(victim);
				victim.setFrozenLength(freezeDelay);
				OverlayTimer.sendOverlayTimer(victim, Combat.getFreezeSpriteId(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0]), (int) freezeDelay / 1000);
				victim.frozenBy = attacker.getPlayerId();
				victim.getPA().sendMessage("<col=ff0000>You have been frozen!");
			}
		}
		attacker.getIncomingDamageOnVictim().add(damage);
		if (attacker.lastUsedManualSpell) {
			attacker.resetPlayerIdAttacking();
		}
		attacker.setSpellId(-1);
	}

	/**
	 * Handle the content of magic spell attack on player packet.
	 *
	 * @param attacker The player using the spell.
	 * @param victimID The player receiving the spell.
	 * @param castingSpellId The spell used by the attacker.
	 */
	public static void magicOnPlayerPacket(Player attacker, int victimID, int castingSpellId) {
		attacker.setUsingRanged(false);
		attacker.setMeleeFollow(false);
		attacker.playerAssistant.stopAllActions();
		Player victim = PlayerHandler.players[victimID];
		if (victim == null) {
			return;
		}
		if (!attacker.getPA().withinDistance(victim)) {
			return;
		}

		MagicData.setCombatSpell(attacker, castingSpellId);
		if (Combat.spellbookPacketAbuse(attacker, attacker.getSpellId())) {
			Combat.resetPlayerAttack(attacker);
			return;
		}
		attacker.faceUpdate(victim.getPlayerId() + 32768);
		attacker.setPlayerIdAttacking(victim.getPlayerId());
		attacker.setLastCastedMagic(true);
		attacker.setPlayerIdToFollow(victim.getPlayerId());
		Combat.stopMovement(attacker, victim, false);

		// Has to be kept here so the player doesn't run 2 extra tiles before realising he cannot even attack.
		if (!AttackPlayer.hasSubAttackRequirements(attacker, victim, true, true)) {
			attacker.turnPlayerTo(victim.getX(), victim.getY());
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
		}
	}

	/**
	 * Requirements to attack with magic.
	 *
	 * @param attacker The player attacking.
	 * @param victim The player receiving the attack.
	 * @return True, if the player has the magic requirements.
	 */
	public static boolean hasMagicRequirements(Player attacker, Player victim) {
		int castedSpell = attacker.getSpellId();
		if (castedSpell == -1) {
			return false;
		}
		if (attacker.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < CombatConstants.MAGIC_SPELLS[castedSpell][1]) {
			attacker.playerAssistant.sendMessage("You need to have a magic level of " + CombatConstants.MAGIC_SPELLS[castedSpell][1] + " to cast this spell.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}

		if (attacker.getSpellId() == 31) {
			if (EdgeAndWestsRule.isEdgeOrWestRule(attacker, victim, "TELEBLOCK")) {
				Combat.resetPlayerAttack(attacker);
				return false;
			}
		}

		if (!MagicData.requiredRunes(attacker, castedSpell, "CHECK REQUIREMENT")) {
			attacker.playerAssistant.sendMessage("You don't have the required runes to cast this spell.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}

		int staffRequired = MagicData.getStaffNeeded(attacker, castedSpell);
		if (staffRequired > 0) {
			if (attacker.getWieldedWeapon() != staffRequired) {
				attacker.playerAssistant.sendMessage("You need a " + ItemAssistant.getItemName(staffRequired).toLowerCase() + " to cast this spell.");
				Combat.resetPlayerAttack(attacker);
				Movement.stopMovement(attacker);
				return false;
			}
		}

		if (attacker.getDuelStatus() == ServerConstants.DUELING) {
			if (attacker.duelRule[TradeAndDuel.NO_MAGIC]) {
				attacker.playerAssistant.sendMessage("Magic has been disabled in this duel!");
				Combat.resetPlayerAttack(attacker);
				Movement.stopMovement(attacker);
				return false;
			}
		}

		for (int value = 0; value < CombatConstants.REDUCE_SPELLS.length; value++) {
			if (CombatConstants.REDUCE_SPELLS[value] == CombatConstants.MAGIC_SPELLS[castedSpell][0]) {
				if ((System.currentTimeMillis() - victim.reduceSpellDelay[value]) < CombatConstants.REDUCE_SPELL_TIME[value]) {
					attacker.playerAssistant.sendMessage("That player is currently immune to this spell.");
					Combat.resetPlayerAttack(attacker);
					Movement.stopMovement(attacker);
					return false;
				}
				break;
			}
		}

		if (!victim.canBeFrozen() && Combat.isModernSpellbookBindSpell(attacker, castedSpell)) {
			attacker.playerAssistant.sendMessage("That player is already affected by this spell.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}

		if (System.currentTimeMillis() < victim.teleBlockEndTime && CombatConstants.MAGIC_SPELLS[castedSpell][0] == CombatConstants.TELE_BLOCK) {
			attacker.playerAssistant.sendMessage("That player is already affected by this spell.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}

		if (System.currentTimeMillis() < (victim.teleBlockEndTime + 30000) && CombatConstants.MAGIC_SPELLS[castedSpell][0] == CombatConstants.TELE_BLOCK) {
			attacker.playerAssistant.sendMessage("That player is currently immune to this spell.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}

		for (int r = 0; r < CombatConstants.REDUCE_SPELLS.length; r++) {
			if (CombatConstants.REDUCE_SPELLS[r] == CombatConstants.MAGIC_SPELLS[castedSpell][0]) {
				attacker.reduceSpellId = r;
				if ((System.currentTimeMillis() - victim.reduceSpellDelay[attacker.reduceSpellId]) > CombatConstants.REDUCE_SPELL_TIME[attacker.reduceSpellId]) {
					victim.canUseReducingSpell[attacker.reduceSpellId] = true;
				} else {
					victim.canUseReducingSpell[attacker.reduceSpellId] = false;
				}
				break;
			}
		}
		if (!victim.canUseReducingSpell[attacker.reduceSpellId]) {
			attacker.playerAssistant.sendMessage("That player is currently immune to this spell.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}
		// Dinh's bulwark
		if ((attacker.getWieldedWeapon() == 21015 || attacker.getWieldedWeapon() == 16259)) {
			attacker.getPA().sendMessage("Your bulwark gets in the way.");
			return false;
		}
		if (!Region.isStraightPathUnblockedProjectiles(attacker.getX(), attacker.getY(), victim.getX(), victim.getY(), attacker.getHeight(), 1, 1, true)) {
			if (attacker.isFrozen()) {
				Combat.resetPlayerAttack(attacker);
			}
			return false;
		}
		//boolean attackerHasF2p = QuickSetUp.isUsingF2pOnly(attacker, false, true);
		boolean victimHasF2p = QuickSetUp.isUsingF2pOnly(victim, false, true);
		// if victim is f2p and i am using a spell that is more than level 59 required magic level or using  lvl 50 snare spell.
		if (victimHasF2p && (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][1] > 59 || attacker.getSpellId() == 23)) {
			attacker.getPA().sendMessage("You cannot use P2p spells on a F2p player.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			return false;
		}
		return true;
	}

	/**
	 * Re-engage the player with magic or start autocasting.
	 *
	 * @param attacker The player attacking.
	 */
	public static void reEngageWithMagic(Player attacker) {

		/*
		 * This will activate when the player uses the magic packet before the hitsplat lands. Because when the hitsplat lands, it will setUsingMagic(false);
		 */
		if (attacker.getSpellId() > 0 && !attacker.getAutoCasting()) {
			attacker.setLastCastedMagic(true);
			attacker.lastUsedManualSpell = true;
		}

		// If player is using melee/ranged packet and is autocasting.
		if (attacker.getAutoCasting() && attacker.getSpellId() == -1) // Spell id is 0 when autocasting and changes when the player has manually casted a spell.
		{
			// If Trident spell
			if (attacker.getAutocastId() == 52) {
				return;
			}
			if (Combat.spellbookPacketAbuse(attacker, attacker.getAutocastId())) {
				Combat.resetPlayerAttack(attacker);
				return;
			}
			attacker.lastUsedManualSpell = false;
			attacker.setSpellId(attacker.getAutocastId());
			attacker.setLastCastedMagic(true);
		}
	}

	/**
	 * Send the barrage/blitz projectile.
	 *
	 * @param attacker The player sending the projectile.
	 * @param victim The player receiving the projectile.
	 */
	public static void createMagicProjectile(Player attacker, Player victim) {
		if (attacker.getSpellId() != 47 && attacker.getSpellId() != 43) {
			return;
		}
		int pX = attacker.getX();
		int pY = attacker.getY();
		int oX = victim.getX();
		int oY = victim.getY();
		int offX = (pY - oY) * -1;
		int offY = (pX - oX) * -1;
		int differenceY = attacker.getY() - victim.getY();
		int differenceX = attacker.getX() - victim.getX();
		pY -= differenceY / 1.5;
		pX -= differenceX / 1.5;
		int distance = 0;
		if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 4)) {
			distance++;
		}
		if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 6)) {
			distance++;
		}
		attacker.getPA()
		        .createPlayersProjectile2(pX, pY, offX, offY, 50, 90 + (distance * 25), MagicAttack.PROJECTILE_ID, 60, 40, -victim.getPlayerId() - 1, 53, 0, attacker.getHeight());
	}

	final static int PROJECTILE_ID = 368;

	public static void chargeSpell(Player player) {
		if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 80) {
			player.getPA().sendMessage("You need 80 magic to cast this spell.");
			return;
		}
		if (System.currentTimeMillis() - player.chargeSpellTime < Misc.getMinutesToMilliseconds(1)) {
			player.getPA().sendMessage("You have recently used the charge spell already.");
			return;
		}
		if (MagicData.requiredRunes(player, 48, "CHECK REQUIREMENT")) {
			MagicData.requiredRunes(player, 48, "DELETE RUNES");
			player.startAnimation(811);
			player.gfx(308, 140);
			player.chargeSpellTime = System.currentTimeMillis();
			OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_CHARGE, 360);
		} else {
			player.getPA().sendMessage("You do not have the required runes to cast this spell.");
		}

	}

}
