package game.content.skilling.summoning.familiar.special.impl;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.skilling.summoning.Summoning;
import game.content.skilling.summoning.familiar.SummoningFamiliarNpc;
import game.content.skilling.summoning.familiar.special.SummoningFamiliarSpecialAbility;
import game.entity.Entity;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Handles the steel titan summong familiar special ability
 * 
 * @author 2012
 *
 */
public class SteelTitanSummoningFamiliarSpecialAbility implements SummoningFamiliarSpecialAbility {

	/**
	 * The special gfx
	 */
	private static final int SPECIAL_GFX = 1_449;

	/**
	 * The special animation
	 */
	private static final int SPECIAL_ANIMATION = 8196;

	/**
	 * Whether can attack npc
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @return checking whether can attack
	 */
	private boolean canAttack(Player player, Npc npc) {
		/*
		 * Not in multi
		 */
		if (!Area.npcInMulti(npc, player.getX(), player.getY())
				|| !Area.inMulti(player.getX(), player.getY())) {
			player.getPA().sendMessage("You need to be in a multi area to use this.");
			return false;
		}
		/*
		 * Already in combat
		 */
		if (npc.underAttackBy > 0 && npc.underAttackBy != player.getPlayerId()) {
			player.playerAssistant.sendMessage("This monster is already in combat.");
			return false;
		} /*
			 * Already in combat
			 */
		if (!Area.npcInMulti(npc, player.getX(), player.getY())
				&& Combat.wasUnderAttackByAnotherPlayer(player, 5000)) {
			player.playerAssistant.sendMessage("I am already under attack.");
			return false;
		}
		/*
		 * Already in combat
		 */
		if ((player.getUnderAttackBy() > 0
				|| player.getNpcIndexAttackingPlayer() > 0 && Combat.wasAttackedByNpc(player))
				&& player.getNpcIndexAttackingPlayer() != npc.npcIndex) {
			player.playerAssistant.sendMessage("I am already under attack.");
			return false;
		}
		/*
		 * Too far
		 */
		if (!player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(),
				npc.getY(), 4)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean sendScroll(Player player) {
		/*
		 * Not in combat
		 */
		if (player.getPlayerIdAttacking() < 1 && player.getNpcIdAttacking() < 1) {
			player.getPA().sendMessage("You need to be in combat to use this special.");
			return false;
		}
		/*
		 * The titan
		 */
		SummoningFamiliarNpc titan = player.getSummoning().getFamiliar();
		/*
		 * Invalid titan
		 */
		if (titan == null) {
			return false;
		}
		/*
		 * Cooldown
		 */
		if (System.currentTimeMillis() - player.lastWolpertingerSpecialAttack < 5000) {
			player.getPA().sendMessage("Your titan needs to cooldown before using the special again.");
			return false;
		}
		/*
		 * Set time
		 */
		player.lastWolpertingerSpecialAttack = System.currentTimeMillis();
		/*
		 * Titan perform
		 */
		titan.gfx0(SPECIAL_GFX);
		NpcHandler.startAnimation(titan, SPECIAL_ANIMATION);
		/*
		 * Player perform
		 */
		player.startAnimation(Summoning.ACTIVATE_ANIMATION);
		/*
		 * Attacking player
		 */
		if (player.getPlayerIdAttacking() > 0) {
			/*
			 * The victim
			 */
			Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
			/*
			 * Invalid victim
			 */
			if (victim == null) {
				return false;
			}
			/*
			 * Can't attack
			 */
			if (!AttackPlayer.hasAttackRequirements(player, victim, true, true)) {
				return false;
			}
			/*
			 * Face player
			 */
			titan.facePlayer(victim.getPlayerId());
			/*
			 * Perform the special damage
			 */
			player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
				/*
				 * The cycle
				 */
				int cycle = 0;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					/*
					 * The hits
					 */
					if (cycle == 1 || cycle == 2) {
						Combat.createHitsplatOnPlayerPvp(player, victim, 1 + Misc.random(23),
								ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "",
								-1);
						Combat.createHitsplatOnPlayerPvp(player, victim, 1 + Misc.random(23),
								ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "",
								-1);
					}
					cycle++;
					if (cycle == 4) {
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 1);
		} else
		/*
		 * Attacking npc
		 */
		if (player.getNpcIdAttacking() > 0 || player.getNpcIndexAttackingPlayer() > 0) {
			/*
			 * The npc
			 */
			Npc npc = NpcHandler.npcs[player.getNpcIdAttacking() > 0 ? player.getNpcIdAttacking()
					: player.getNpcIndexAttackingPlayer()];
			/*
			 * Invalid npc
			 */
			if (npc == null) {
				return false;
			}
			/*
			 * Can't attack
			 */
			if (!canAttack(player, npc)) {
				return false;
			}
			/*
			 * Titan face the npc
			 */
			titan.faceNpc(npc.npcIndex);
			/*
			 * Perform the special damage
			 */
			player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
				/*
				 * The cycle
				 */
				int cycle = 0;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					/*
					 * The hits
					 */
					if (cycle == 1 || cycle == 2) {
						CombatNpc.applyHitSplatOnNpc(player, npc, 1 + Misc.random(23),
								ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						CombatNpc.applyHitSplatOnNpc(player, npc, 1 + Misc.random(23),
								ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 2);
					}
					cycle++;
					if (cycle == 4) {
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 1);
		}
		return false;
	}

	@Override
	public void cycle(Player player) {

	}

	@Override
	public double getSkillBonus(Player player, int skill) {
		/*
		 * Defence skill bonus
		 */
		if (skill == ServerConstants.DEFENCE) {
			return 1.15;
		}
		return 0;
	}

	@Override
	public int getPercentageRequired() {
		return 0;
	}
}
