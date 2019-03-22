package game.npc.clicknpc;

import core.GameType;
import core.Plugin;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.PvpTask;
import game.content.packet.preeoc.ClickNpcPreEoc;
import game.content.skilling.Slayer;
import game.content.starter.GameMode;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Third option click on NPC interactions.
 *
 * @author MGT Madness, created on 18-01-2013.
 */
public class ThirdClickNpc {

	/**
	 * Third option click on NPC.
	 *
	 * @param player The associated player.
	 * @param npcType The NPC identity.
	 */
	public static void thirdClickNpc(Player player, int npcType) {
		player.resetNpcIdToFollow();
		player.setClickNpcType(0);

		Npc npc = NpcHandler.npcs[player.getNpcClickIndex()];
		NpcHandler.facePlayer(player, npc);
		player.turnPlayerTo(npc.getX(), npc.getY());

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

		if (thirdClickNpcOsrs(player, npc, npcType)) {
			player.setNpcClickIndex(0);
			return;
		}
		if (ClickNpcPreEoc.thirdClickNpcPreEoc(player, npc, npcType)) {
			player.setNpcClickIndex(0);
			return;
		}
		player.setNpcClickIndex(0);
	}

	private static boolean thirdClickNpcOsrs(Player player, Npc npc, int npcType) {
		if (!GameType.isOsrs()) {
			return false;
		}
		if (Pet.petMetamorphosis(player, npc, 3)) {
			return true;
		}
		switch (npcType) {

			// Lottery/Durial321
			case 11057:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot participate in the lottery.");
					return true;
				}
				Lottery.currentPotAtDialogueOption(player);
				return true;
			case 5792:
				player.getShops().openShop(84);
				return true;
			case 1306: //make-over mage
				player.getDH().sendDialogues(290);
				return true;
			// Nurse Tafani
			case 3343:
				player.getShops().openShop(11);
				return true;

			// Pvp task master.
			case 315:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot speak to the Pvp task master.");
					return true;
				}
				PvpTask.claimReward(player);
				return true;

			// Void Knight, Untradeables.
			case 1758:
				player.getShops().openShop(46);
				return true;
			// Horvik, ranged.
			case 535:
				player.getShops().openShop(20);
				return true;

			// Vannaka, boss-task.
			case 6797:
			case 403:
				Slayer.giveBossTask(player);
				return true;

			// Shop keeper at Edgeville.
			case 512:
				player.getShops().openShop(7);
				return true;

			default:
				if (Plugin.execute("third_click_npc_" + npcType, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}
		return false;
	}

}
