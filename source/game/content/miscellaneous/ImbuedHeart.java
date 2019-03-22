package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.consumable.RegenerateSkill;
import game.player.Area;
import game.player.Player;
import utility.Misc;

public class ImbuedHeart {

	public static void invigorate(Player player) {
		if (!GameType.isOsrs()) {
			return;
		}
		if (player.duelRule[TradeAndDuel.NO_DRINK]) {
			player.getPA().sendMessage("You may not use the heart's effect in this duel.");
			return;
		}
		if (player.imbuedHeartEndTime > System.currentTimeMillis()) {
			long time = (player.imbuedHeartEndTime - System.currentTimeMillis()) / 1000;
			player.getPA().sendMessage("The heart is still drained of its power.");
			player.getPA().sendMessage("Judging by how it feels, it will be ready in " + Misc.getTimeLeft((int) time) + ".");
			return;
		}
		if (player.getCurrentCombatSkillLevel(ServerConstants.MAGIC) > player.getBaseMagicLevel()) {
			player.getPA().sendMessage("You cannot use this while your magic is already boosted.");
			return;
		}
		player.gfx0(1316);
		player.imbuedHeartEndTime = System.currentTimeMillis() + Misc.getSecondsToMilliseconds(7);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);

		boostMagicLevel(player);
		if (Area.inDangerousPvpAreaOrClanWars(player) || Area.inDuelArenaRing(player)) {
			Combat.lowerBoostedCombatLevels(player);
		}
		OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_IMBUED_HEART, 420);

	}

	private static void boostMagicLevel(Player player) {
		int level = player.getBaseMagicLevel();
		int boost = 1 + level / 10;
		player.currentCombatSkillLevel[ServerConstants.MAGIC] += boost;
		if (player.currentCombatSkillLevel[ServerConstants.MAGIC] > level + boost) {
			player.currentCombatSkillLevel[ServerConstants.MAGIC] = level + boost;
		}
		player.skillTabMainToUpdate.add(ServerConstants.MAGIC);
	}

}
