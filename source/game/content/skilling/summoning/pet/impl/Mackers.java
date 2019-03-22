package game.content.skilling.summoning.pet.impl;

import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.npc.Npc;
import game.npc.pet.Pet;
import game.player.Player;
import game.type.GameTypeIdentity;

@ItemInteractionComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC,
		identity = {22_443}),})
/**
 * Mackers pet
 * 
 * @author 2012
 *
 */
public class Mackers implements ItemInteraction {

	/**
	 * The item id
	 */
	private static final int MACKERS = 22_443;

	/**
	 * The mackers npc
	 */
	private static final int MACKERS_PET = 14_674;

	@Override
	public boolean canEquip(Player player, int id, int slot) {
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
		if (id == MACKERS) {
			Pet.summonNpcOnValidTile(player, MACKERS_PET, false);
			return true;
		}
		return false;
	}
}