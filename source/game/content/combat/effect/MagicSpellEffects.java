package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.miscellaneous.OverlayTimer;
import game.content.skilling.Skilling;
import game.player.Player;

public class MagicSpellEffects implements EntityDamageEffect {

	private int spellId;

	public MagicSpellEffects(int spellId) {
		this.spellId = spellId;
	}

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {


		Player victim = (Player) damage.getTarget();
		Player attacker = (Player) damage.getSender();
		switch (spellId) {
			// Blood spells.
			case 12901:
			case 12919:
			case 12911:
			case 12929:
				if (!attacker.isMagicSplash()) {
					int heal = attacker.getMagicDamage() / 4;
					attacker.addToHitPoints(heal);
				}
				break;

			// Teleblock spell
			case 12445:
				int teleBlockOutcome = 80;
				if ((System.currentTimeMillis() > victim.teleBlockEndTime + 30000) && !attacker.isMagicSplash()) {
					if (victim.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
						teleBlockOutcome = 82;
						victim.teleBlockEndTime = System.currentTimeMillis() + 150000;
						victim.getPA().sendMessage(ServerConstants.PURPLE_COL + "A teleblock spell has been cast on you. It will expire in 2 minutes and a half.");
						OverlayTimer.sendOverlayTimer(victim, ServerConstants.OVERLAY_TIMER_SPRITE_TELEBLOCK, 150);
					} else {
						teleBlockOutcome = 85;
						victim.teleBlockEndTime = System.currentTimeMillis() + 300000;
						victim.getPA().sendMessage(ServerConstants.PURPLE_COL + "A teleblock spell has been cast on you. It will expire in 5 minutes.");
						OverlayTimer.sendOverlayTimer(victim, ServerConstants.OVERLAY_TIMER_SPRITE_TELEBLOCK, 300);
					}
				}
				Skilling.addSkillExperience(attacker, teleBlockOutcome, ServerConstants.MAGIC, false);
				break;
		}
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player victim = (Player) damage.getTarget();
		Player attacker = (Player) damage.getSender();

		switch (spellId) {

			// Miasmic rush
			case 32600:
				if (!attacker.isMagicSplash() && victim.miasmicSpeedTime < 1) {
					victim.miasmicSpeedTimeElapsed = System.currentTimeMillis();
					victim.miasmicSpeedTime = 12;
				}
				break;
			// Miasmic burst
			case 32620:
				if (!attacker.isMagicSplash() && victim.miasmicSpeedTime < 1) {
					victim.miasmicSpeedTimeElapsed = System.currentTimeMillis();
					victim.miasmicSpeedTime = 24;
				}
				break;
			// Miasmic blitz
			case 32640:
				if (!attacker.isMagicSplash() && victim.miasmicSpeedTime < 1) {
					victim.miasmicSpeedTimeElapsed = System.currentTimeMillis();
					victim.miasmicSpeedTime = 36;
				}
				break;
			// Miasmic Barrage
			case 32660:
				if (!attacker.isMagicSplash() && victim.miasmicSpeedTime < 1) {
					victim.miasmicSpeedTimeElapsed = System.currentTimeMillis();
					victim.miasmicSpeedTime = 48;
				}
				break;
			// Claws of guthix
			case 1191:
				if (!attacker.isMagicSplash()) {
					victim.timeClawsOfGuthixAffected = System.currentTimeMillis();
				}
				break;

			// Zamorak flames
			case 1192:
				if (!attacker.isMagicSplash()) {
					victim.timeZamorakFlamesAffected = System.currentTimeMillis();
					int drain = (int) (victim.getBaseMagicLevel() * 0.05);
					int min = victim.getBaseMagicLevel() - drain;
					if (Skilling.decreaseCombatSkill(victim, ServerConstants.MAGIC, drain, min)) {
						victim.getPA().sendMessage("Your magic has been lowered!");
					}
				}
				break;

			// Saradomin strike
			case 1190:
				if (!attacker.isMagicSplash()) {
					Combat.applyPrayerReduction(attacker, victim, 1, false);
				}
				break;

			case 12987: // Shadow rush
			case 13011: // Shadow burst
			case 12999: // Shadaow blitz
			case 13023: // Shadow barrage
				if (System.currentTimeMillis() - victim.reduceStat > 35000) {
					victim.reduceStat = System.currentTimeMillis();
					victim.currentCombatSkillLevel[ServerConstants.ATTACK] -= ((double) victim.baseSkillLevel[ServerConstants.ATTACK] / 100.0) * CombatConstants.getShadowSpellAttackLevelReductionPercentage(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0]);
					Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.ATTACK);
				}
				break;

			// Confuse
			case 1153:
				victim.currentCombatSkillLevel[ServerConstants.ATTACK] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 5) / 100);
				victim.playerAssistant.sendMessage("Your attack level has been reduced!");
				victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.ATTACK);
				break;

			// Weaken
			case 1157:
				victim.currentCombatSkillLevel[ServerConstants.STRENGTH] -= ((victim.baseSkillLevel[ServerConstants.STRENGTH] * 5) / 100);
				victim.playerAssistant.sendMessage("Your strength level has been reduced!");
				victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.STRENGTH);
				break;

			// Curse
			case 1161:
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] -= ((victim.baseSkillLevel[ServerConstants.DEFENCE] * 5) / 100);
				victim.playerAssistant.sendMessage("Your defence level has been reduced!");
				victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
				break;

			// Vulnerability
			case 1542:
				victim.currentCombatSkillLevel[ServerConstants.DEFENCE] -= ((victim.baseSkillLevel[ServerConstants.DEFENCE] * 10) / 100);
				victim.playerAssistant.sendMessage("Your defence level has been reduced!");
				victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
				break;

			// Enfeeble
			case 1543:
				victim.currentCombatSkillLevel[ServerConstants.STRENGTH] -= ((victim.baseSkillLevel[ServerConstants.STRENGTH] * 10) / 100);
				victim.playerAssistant.sendMessage("Your strength level has been reduced!");
				victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.STRENGTH);
				break;

			// Stun
			case 1562:
				victim.currentCombatSkillLevel[ServerConstants.ATTACK] -= ((victim.baseSkillLevel[ServerConstants.ATTACK] * 10) / 100);
				victim.playerAssistant.sendMessage("Your attack level has been reduced!");
				victim.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
				Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.ATTACK);
				break;
		}
	}
}
