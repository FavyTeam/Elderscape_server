package game.content.packet;

import game.content.combat.Combat;
import game.item.ItemAssistant;
import game.item.ItemOnNpc;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import network.packet.PacketHandler;
import network.packet.PacketType;


public class ItemOnNpcPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int itemId = player.getInStream().readSignedWordA();
		int npcId = player.getInStream().readSignedWordA();
		int itemSlot = player.getInStream().readSignedWordBigEndian();
		if (npcId <= 0) {
			return;
		}
		if (npcId > NpcHandler.NPC_INDEX_OPEN_MAXIMUM) {
			return;
		}

		if (ItemAssistant.isNulledSlot(itemSlot)) {
			return;
		}
		Npc npc = NpcHandler.npcs[npcId];
		if (npc == null) {
			return;
		}
		int npcType = npc.npcType;
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
			PacketHandler.saveData(player.getPlayerName(), "i: " + npcId);
			PacketHandler.saveData(player.getPlayerName(), "slot: " + itemSlot);
			PacketHandler.saveData(player.getPlayerName(), "npcId: " + npcType);
		}

		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}

		if (!ItemAssistant.playerHasItem(player, itemId, 1, itemSlot)) {
			return;
		}


		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		player.getPA().stopAllActions();
		Combat.resetPlayerAttack(player);
		player.setNpcIdToFollow(npcId);
		player.itemOnNpcEvent = true;

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@SuppressWarnings("unused")
			@Override
			public void execute(CycleEventContainer container) {
				if (!player.itemOnNpcEvent) {
					container.stop();
					return;
				}
				if (npc == null) {
					container.stop();
					return;
				}
				if (player.getPA().withinDistanceOfTargetNpc(npc, 1)) {
					ItemOnNpc.itemOnNpc(player, itemId, npcId, itemSlot);
					container.stop();
					return;
				}
			}

			@Override
			public void stop() {
				player.itemOnNpcEvent = false;
				player.resetNpcIdToFollow();


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
			}
		}, 1);
	}
}
