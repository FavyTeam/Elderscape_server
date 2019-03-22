package game.content.packet;

import game.content.combat.Combat;
import game.content.dialogue.DialogueChain;
import game.entity.EntityType;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.pet.PlayerPet;
import network.packet.PacketType;
import utility.Misc;

public class ItemOnPlayerPacket implements PacketType {
	@Override
	public void processPacket(Player c, int packetType, int packetSize, boolean trackPlayer) {
		@SuppressWarnings("unused")
		int interfaceId = c.getInStream().readUnsignedWordBigEndianA();
		int playerIndex = c.getInStream().readUnsignedWord();
		int itemId = c.getInStream().readUnsignedWord();
		int slotId = c.getInStream().readUnsignedWordBigEndian();
		c.setItemOnPlayer(null);
		if (playerIndex > PlayerHandler.players.length) {
			return;
		}
		if (slotId > c.playerItems.length) {
			return;
		}
		if (PlayerHandler.players[playerIndex] == null) {
			return;
		}
		if (!ItemAssistant.playerHasItem(c, itemId, 1, slotId)) {
			return;
		}
		Player other = PlayerHandler.players[playerIndex];

		if (other == null) {
			return;
		}
		if (other.getType() == EntityType.PLAYER_BOT) {
			return;
		}
		if (other.getType() == EntityType.PLAYER_PET && other instanceof PlayerPet) {
			if (Area.inDangerousPvpArea(c) || Area.inAreaWhereItemsGetDeletedUponExit(c))
			{
				return;
			}
			PlayerPet otherPlayerPet = (PlayerPet) other;

			PlayerPet myPet = c.getPlayerPet();

			if (myPet == null || otherPlayerPet.getOwner() != c || myPet != otherPlayerPet) {
				c.getPA().sendMessage("This pet does not belong to you.");
				return;
			}
			if (PlayerPet.DELETE_ITEM_ON_EQUIP) {
                ItemDefinition definition = ItemDefinition.getDefinitions()[itemId];

				c.setDialogueChain(new DialogueChain().
						item(definition == null ? "Unknown" : definition.name, itemId, 250, 0, 0,
                                "Upon successfully equipping this item to the player-pet,",
                                "the item will be deleted from your inventory.",
                                "@red@This item cannot be re-obtained."))
                        .option((player, option) -> {
                            if (option == 1) {
                                player.getPA().closeInterfaces(true);
                                if (!ItemAssistant.playerHasItem(c, itemId, 1, slotId)) {
                                    player.setDialogueChainAndStart(new DialogueChain().statement("You do not have this item anymore, weird huh?"));
                                    return;
                                }
                                myPet.equip(itemId, 1, slotId);
                            } else {
                                player.getPA().closeInterfaces(true);
                            }
                        }, "Are you sure you want to equip this item?", "Yes", "No").start(c);
			} else {
				otherPlayerPet.equip(itemId, 1, slotId);
			}
			return;
		}
	}
}
