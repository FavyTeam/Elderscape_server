package game.log;

import game.content.miscellaneous.ItemTransferLog;
import game.content.packet.PrivateMessagingPacket;
import game.player.Player;
import java.util.ArrayList;

/**
 * Used to take down RWT.
 *
 * @author MGT Madness, created on 08-05-2017
 */
public class StakingLog {
	public static ArrayList<String> data = new ArrayList<String>();

	public static void saveRiggedStakingSession(Player stakeWinner, Player stakeLoser, int stakeWinnerLoot, int stakeLoserLoot, String tokensWinner, String tokensLoser) {
		if (PrivateMessagingPacket.getMinutesFriendsFor(stakeWinner, stakeLoser.getPlayerName()) < ItemTransferLog.MINIMUM_MINUTES_FRIENDS && PrivateMessagingPacket.getMinutesFriendsFor(stakeLoser, stakeWinner.getPlayerName()) < ItemTransferLog.MINIMUM_MINUTES_FRIENDS) {
			//stakeWinner.setFlaggedForRwt(true);
			//stakeLoser.setFlaggedForRwt(true);
			//ItemTransferLog.rwtAlert.add(Misc.getDateAndTime() + " STAKE: [" + BannedData.getSourceName(stakeWinner) + "] won vs [" + BannedData.getSourceName(stakeLoser) + "], " + Misc.formatRunescapeStyle(stakeWinnerLoot) + " " + tokensWinner + " vs " + Misc.formatRunescapeStyle(stakeLoserLoot) + " " + tokensLoser);
			//ItemTransferLog.rwtAlert.add(stakeWinner.stakeAttacks + ", " + stakeWinner.stakeSpecialAttacks + " vs " + stakeLoser.stakeAttacks + ", " + stakeLoser.stakeSpecialAttacks);
		}
	}
}
