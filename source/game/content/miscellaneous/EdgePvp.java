package game.content.miscellaneous;

import game.content.quicksetup.QuickSetUp;
import game.player.Player;

/**
 * Edgeville pvp.
 *
 * @author MGT Madness, created on 02-06-2017
 */
public class EdgePvp {
	public final static int MINUTES_AWARENESS_DELAY = 20;

	/**
	 * The time Edgeville pvp activity was announced.
	 */
	public static long timeAnnounced;

	public static boolean announceEdgePvpActivity() {
		if (System.currentTimeMillis() - timeAnnounced <= (60000 * MINUTES_AWARENESS_DELAY)) {
			return false;
		}
		timeAnnounced = System.currentTimeMillis();

		Announcement.announce("<img=17><col=b100e2>Hybrids are requesting fights right now at ::edgepvp");
		return true;
	}

	public static void edgePvpTeleportedTo(Player player) {
		player.getPA().sendMessage("You have teleported to Edgeville pvp. Where Hybrids battle it out.");
		EdgePvp.announceEdgePvpActivity();
		if (!QuickSetUp.isUsingF2pOnly(player, false, true)) {
			SpellBook.ancientMagicksSpellBook(player, true);
		}
	}
}
