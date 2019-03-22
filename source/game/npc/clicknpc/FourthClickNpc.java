package game.npc.clicknpc;

import core.GameType;
import core.Plugin;
import game.content.miscellaneous.EdgePvp;
import game.content.miscellaneous.PvpTask;
import game.content.packet.preeoc.ClickNpcPreEoc;
import game.content.skilling.Slayer;
import game.content.starter.GameMode;
import game.item.PotionCombining;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Fourth option click on NPC interactions.
 *
 * @author MGT Madness, created on  25-11-2016.
 */
public class FourthClickNpc {

	/**
	 * Fourth option click on NPC.
	 *
	 * @param player The associated player.
	 * @param npcType The NPC identity.
	 */
	public static void fourthClickNpc(Player player, int npcType) {
		player.resetNpcIdToFollow();
		player.setClickNpcType(0);

		Npc npc = NpcHandler.npcs[player.getNpcClickIndex()];
		NpcHandler.facePlayer(player, npc);
		player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());

		// Has to be on a delayed tick or else it will so awkward rotations when talking to an npc when running to it.
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.resetFaceUpdate();
			}
		}, 2);
		if (fourthClickNpcOsrs(player, npc, npcType)) {
			player.setNpcClickIndex(0);
			return;
		}
		if (ClickNpcPreEoc.fourthClickNpcPreEoc(player, npc, npcType)) {
			player.setNpcClickIndex(0);
			return;
		}
		player.setNpcClickIndex(0);
	}

	private static boolean fourthClickNpcOsrs(Player player, Npc npc, int npcType) {
		if (!GameType.isOsrs()) {
			return false;
		}

		switch (npcType) {

			// Nurse Tafani
			case 3343:
				if (!Area.inEdgevilleBankPvpInstance(player.getX(), player.getY(), player.getHeight())) {
					player.getPA().sendMessage("You cannot use this outside Edgeville pvp.");
					return true;
				}
				if (!EdgePvp.announceEdgePvpActivity()) {
					long minutesLeft = (60000 * EdgePvp.MINUTES_AWARENESS_DELAY) - (System.currentTimeMillis() - EdgePvp.timeAnnounced);
					minutesLeft /= 60000;
					player.getPA().sendMessage("This is on cooldown for " + minutesLeft + " more minutes.");
				}
				return true;
			// Pvp task master.
			case 315:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot speak to the Pvp task master.");
					return true;
				}
				PvpTask.killsLeft(player);
				return true;
			// Void Knight, Buy-back untradeables.
			case 1758:
				player.getShops().openShop(11);
				return true;
			// Horvik, magic.
			case 535:
				player.getShops().openShop(19);
				return true;

			// Vannaka, reset-task.
			case 6797:
			case 403:
				Slayer.resetTask(player);
				return true;

			// Shop keeper at Edgeville.
			case 512:
				PotionCombining.decantAllPotions(player);
				return true;
			default:
				if (Plugin.execute("fourth_click_npc_" + npcType, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;

		}
		return false;
	}

}
