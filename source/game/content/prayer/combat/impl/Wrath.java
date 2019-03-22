package game.content.prayer.combat.impl;

import java.util.ArrayList;
import java.util.List;
import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.player.Area;
import game.player.Player;

/**
 * Handles wrath
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 * 
 */
public class Wrath {

	/**
	 * Handles wrath
	 * 
	 * @param player
	 *            the player
	 */
	public static void handleWrath(Player player) {
		/*
		 * Checks if in wild
		 */
		if (!Area.inDangerousPvpArea(player)) {
			return;
		}
		/*
		 * Player pos
		 */
		List<Position> pos = new ArrayList<>(player.getPosition().surrounding(5));
		/*
		 * The damage
		 */
		int damage = player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) * 3;
		/*
		 * Sends the gfx
		 */
		player.gfx0(2259);
		/*
		 * Send gfx
		 */
		for (Position position : pos) {
			/*
			 * Send gfx
			 */
			player.getPA().createPlayersStillGfx(2260, position.getX(), position.getY(),
					position.getZ(), 0);
		}
		/*
		 * Loops through locals
		 */
		for (Player victim : player.getLocalPlayers()) {
			/*
			 * Invalid victim
			 */
			if (victim == null) {
				continue;
			}
			/*
			 * Loop positions
			 */
			for (Position position : pos) {
				/*
				 * Position matches
				 */
				if (victim.getPosition().matches(position)) {
					/*
					 * Fixes damage
					 */
					if (damage > victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS)) {
						damage = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
					}
					/*
					 * Hit splat
					 */
					Combat.createHitsplatOnPlayerPvp(victim, player, damage,
							ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
				}
			}
		}
	}
}
