package game.npc;

import game.content.packet.ClickNpcPacket;
import game.npc.clicknpc.FirstClickNpc;
import game.npc.clicknpc.FourthClickNpc;
import game.npc.clicknpc.SecondClickNpc;
import game.npc.clicknpc.ThirdClickNpc;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Handle the Npc cycle events
 *
 * @author MGT Madness
 */

public class NpcEvent {

	/**
	 * Start the cycle event of left clicking an NPC, which executes the NPC action once the player is close enough to the NPC.
	 */
	@SuppressWarnings("unchecked")
	public static void clickNpcType1Event(final Player player) {

		if (player.usingClickNpcType1Event) {
			return;
		}

		player.usingClickNpcType1Event = true;

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				Npc npc = NpcHandler.npcs[player.getNpcClickIndex()];
				if (npc == null) {
					container.stop();
					return;
				}
				//@formatter:off
				if (!player.getPA().playerOnNpc(player, npc) && 
					player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getVisualX(), npc.getVisualY(), ClickNpcPacket.getNpcInteractionDistance(npc)) && 
					player.getClickNpcType() == 1 && player.getPA().canInteractWithNpc(npc)) {
					player.turnPlayerTo(npc.getX(), npc.getY());
					FirstClickNpc.firstClickNpc(player, player.getNpcType());
				}
				//@formatter:on

				if (player.getClickNpcType() != 1) {
					container.stop();
				}


			}

			@Override
			public void stop() {
				player.usingClickNpcType1Event = false;
			}
		}, 1);

	}

	/**
	 * Start the cycle event of second click on an NPC, which executes the NPC action once the player is close enough to the NPC.
	 */
	public static void clickNpcType2Event(final Player player) {

		if (player.usingClickNpcType2Event) {
			return;
		}

		player.usingClickNpcType2Event = true;

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && NpcHandler.npcs[player.getNpcClickIndex()] != null && player.playerAssistant
						                                                                                                                                             .withInDistance(
								                                                                                                                                             player.getX(),
								                                                                                                                                             player.getY(),
								                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]
										                                                                                                                                             .getX(),
								                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]
										                                                                                                                                             .getY(),
								                                                                                                                                             ClickNpcPacket
										                                                                                                                                             .getNpcInteractionDistance(
												                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]))
				    && player.getClickNpcType() == 2 && player.getPA().canInteractWithNpc(NpcHandler.npcs[player.getNpcClickIndex()])) {
					player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
					SecondClickNpc.secondClickNpc(player, player.getNpcType());
				}

				if (player.getClickNpcType() != 2) {
					container.stop();
				}


			}

			@Override
			public void stop() {
				player.usingClickNpcType2Event = false;
			}
		}, 1);

	}

	/**
	 * Start the cycle event of third click on an NPC, which executes the NPC action once the player is close enough to the NPC.
	 */
	public static void clickNpcType3Event(final Player player) {

		if (player.usingClickNpcType3Event) {
			return;
		}

		player.usingClickNpcType3Event = true;

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && NpcHandler.npcs[player.getNpcClickIndex()] != null && player.playerAssistant
						                                                                                                                                             .withInDistance(
								                                                                                                                                             player.getX(),
								                                                                                                                                             player.getY(),
								                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]
										                                                                                                                                             .getX(),
								                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]
										                                                                                                                                             .getY(),
								                                                                                                                                             ClickNpcPacket
										                                                                                                                                             .getNpcInteractionDistance(
												                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]))
				    && player.getClickNpcType() == 3 && player.getPA().canInteractWithNpc(NpcHandler.npcs[player.getNpcClickIndex()])) {
					player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
					ThirdClickNpc.thirdClickNpc(player, player.getNpcType());
				}

				if (player.getClickNpcType() != 3) {
					container.stop();
				}


			}

			@Override
			public void stop() {
				player.usingClickNpcType3Event = false;
			}
		}, 1);

	}

	public static void clickNpcType4Event(final Player player) {

		if (player.usingClickNpcType4Event) {
			return;
		}

		player.usingClickNpcType4Event = true;

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				if (!player.getPA().playerOnNpc(player, NpcHandler.npcs[player.getNpcClickIndex()]) && NpcHandler.npcs[player.getNpcClickIndex()] != null && player.playerAssistant
						                                                                                                                                             .withInDistance(
								                                                                                                                                             player.getX(),
								                                                                                                                                             player.getY(),
								                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]
										                                                                                                                                             .getX(),
								                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]
										                                                                                                                                             .getY(),
								                                                                                                                                             ClickNpcPacket
										                                                                                                                                             .getNpcInteractionDistance(
												                                                                                                                                             NpcHandler.npcs[player.getNpcClickIndex()]))
				    && player.getClickNpcType() == 4 && player.getPA().canInteractWithNpc(NpcHandler.npcs[player.getNpcClickIndex()])) {
					player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());
					FourthClickNpc.fourthClickNpc(player, player.getNpcType());
				}

				if (player.getClickNpcType() != 4) {
					container.stop();
				}


			}

			@Override
			public void stop() {
				player.usingClickNpcType4Event = false;
			}
		}, 1);

	}

}
