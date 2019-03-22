package game.content.packet;

import core.Server;
import game.content.minigame.zombie.Zombie;
import game.content.music.Music;
import game.content.skilling.Farming;
import game.item.GlobalItemSpawn;
import game.npc.NpcAggression;
import game.player.Player;
import network.packet.PacketType;

/**
 * Change Regions
 */
public class ChangeRegionPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		Server.itemHandler.showGroundItemsToSpecificPlayerOnRegionChange(player);
		Server.objectManager.changeRegionPacketClientObjectUpdate(player, false);
		player.saveFile = true;
		Music.playRegionMusic(player);
		GlobalItemSpawn.loadGlobalDrops(player);
		Zombie.loadChest(player);
		Farming.updateOnRegionChange(player);
		if (player.getRedSkull() || player.getWhiteSkull()) {
			player.getPA().requestUpdates();
		}
		NpcAggression.clearNpcAggressionForSpecificPlayer(player);
		player.onRegionChange();
	}

}
