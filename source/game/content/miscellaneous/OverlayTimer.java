package game.content.miscellaneous;

import core.ServerConstants;
import game.player.Player;
import utility.Misc;

public class OverlayTimer {

	public static void updateOverlayTimersOnLogIn(Player player) {
		long secondsLeft = (player.venomImmunityExpireTime - System.currentTimeMillis()) / 1000;
		sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ANTI_VENOM, (int) secondsLeft);
	
		secondsLeft = player.getAntiFirePotionTimer() * 30;
		sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ANTI_FIRE, (int) secondsLeft);
	
		secondsLeft = player.getStaminaPotionTimer() * 10;
		sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_STAMINA, (int) secondsLeft);
	
		secondsLeft = (player.teleBlockEndTime - System.currentTimeMillis()) / 1000;
		sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_TELEBLOCK, (int) secondsLeft);
	
		secondsLeft = (Misc.getMinutesToMilliseconds(6) - (System.currentTimeMillis() - player.chargeSpellTime)) / 1000;
		sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_CHARGE, (int) secondsLeft);

		secondsLeft = (player.imbuedHeartEndTime - System.currentTimeMillis()) / 1000;
		sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_IMBUED_HEART, (int) secondsLeft);
	}

	public static void sendOverlayTimer(Player player, int overlaySpriteId, int secondsDurationLeft) {
		if (secondsDurationLeft > 0) {
			player.getPA().sendMessage(":packet:startoverlaytimer " + overlaySpriteId + " " + secondsDurationLeft);
		}
	}

	public static void stopAllOverlayTimers(Player player) {
		player.getPA().sendMessage(":packet:stopalloverlaytimers");
	}

	public static void stopOverlayTimer(Player player, int overlaySpriteId) {
		player.getPA().sendMessage(":packet:stopoverlaytimer " + overlaySpriteId);
	}

}
