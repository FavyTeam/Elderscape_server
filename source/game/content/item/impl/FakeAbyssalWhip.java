package game.content.item.impl;

import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;

/**
 * Handles the fake abyssal whip
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {14661}),})
public class FakeAbyssalWhip implements ItemInteraction {

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		if (id == 14661) {
			ItemAssistant.deleteItemFromInventory(player, id, 1);
			player.getPA().sendMessage("The abyssal whip disappears into nothing in your hands.");;
		}
		return false;
	}

	@Override
	public void operate(Player player, int id) {

	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		return false;
	}

	@Override
	public boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	@Override
	public boolean useItemOnObject(Player player, int id, int object) {
		return false;
	}

	@Override
	public boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}