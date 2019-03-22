package game.content.skilling;

import core.GameType;
import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Resource wilderness additions.
 * @author MGT Madness, created on 21-07-2018.
 */
public class ResourceWilderness {

	public final static double EXPERIENCE_MULTIPLIER = 1.20;

	public final static int MINUTES_TO_RECEIVE_REWARD = 10;

	public static int getCurrencyAmountReward() {
		return GameType.isOsrsPvp() ? 1_000 : 700_000;
	}

	public static void peakIntoResourceWildArea(Player player) {
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (Area.inResourceWildernessOsrs(loop)) {
				player.getPA().sendMessage("Someone is in there..");
				return;
			}
		}
		player.getPA().sendMessage("No one is in here.");
	}

	public static void skilledInside(Player player) {

		if (!Misc.timeElapsed(player.getTimeEarnedBloodMoneyInResourceWild(), Misc.getMinutesToMilliseconds(MINUTES_TO_RECEIVE_REWARD))) {
			return;
		}
		player.setTimeEarnedBloodMoneyInResourceWild(System.currentTimeMillis());
		ItemAssistant.addItemToInventoryOrDrop(player, ServerConstants.getMainCurrencyId(), getCurrencyAmountReward());
		player.getPA().sendMessage("The cleaner hands over " + Misc.formatNumber(getCurrencyAmountReward()) + " " + ServerConstants.getMainCurrencyName() + " to you for being brave.");

	}

}
