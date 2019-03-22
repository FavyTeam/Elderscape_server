package game.content.skilling.summoning.familiar.special.impl;

import core.ServerConstants;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.content.skilling.summoning.Summoning;
import game.content.skilling.summoning.familiar.SummoningFamiliarNpc;
import game.content.skilling.summoning.familiar.special.SummoningFamiliarSpecialAbility;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Handles the wolpertinger summoning familiar special ability
 * 
 * @author 2012
 *
 */
public class WolpertingerSummoningFamiliarSpecialAbility
		implements SummoningFamiliarSpecialAbility {

	/**
	 * The gfx
	 */
	private static final int WOLPERTINGER_GFX = 1_464;

	/**
	 * The reuse time
	 */
	private static final int RE_USE_TIME = 10_000 * 6;

	@Override
	public boolean sendScroll(Player player) {
		/*
		 * The npc
		 */
		SummoningFamiliarNpc npc = player.getSummoning().getFamiliar();
		/*
		 * Invalid npc
		 */
		if (npc == null) {
			return false;
		}
		/*
		 * Already used
		 */
		if (System.currentTimeMillis() - player.lastWolpertingerSpecialAttack < RE_USE_TIME) {
			player.playerAssistant.sendMessage("Your Wolpertinger is exhausted.");
			return false;
		}
		/*
		 * Start
		 */
		player.lastWolpertingerSpecialAttack = System.currentTimeMillis();
		/*
		 * Face player
		 */
		npc.facePlayer(player.getPlayerId());
		/*
		 * Perform gfx
		 */
		npc.gfx0(WOLPERTINGER_GFX);
		/*
		 * Add timed action
		 */
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Player performance
				 */
				player.gfx0(1303);
				player.startAnimation(Summoning.ACTIVATE_ANIMATION);
				/*
				 * Boost magic level
				 */
				player.currentCombatSkillLevel[ServerConstants.MAGIC] += 7;
				if (player.currentCombatSkillLevel[ServerConstants.MAGIC] > player.getBaseMagicLevel()
						+ 7) {
					player.currentCombatSkillLevel[ServerConstants.MAGIC] =
							player.getBaseMagicLevel() + 7;
				}
				RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
				container.stop();
			}

			@Override
			public void stop() {

			}
		}, 4);
		return true;
	}

	@Override
	public void cycle(Player player) {

	}

	@Override
	public double getSkillBonus(Player player, int skill) {
		return skill == ServerConstants.MAGIC ? 1.08 : 1.0;
	}

	@Override
	public int getPercentageRequired() {
		return 0;
	}
}
