package game.content.item.impl;

import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.content.miscellaneous.PlayerGameTime;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;

/**
 * Handles the veteran cape
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {20_763, 20_764}),})
public class VeteranCape implements ItemInteraction {

	/**
	 * The days required
	 */
	private static final int DAYS_REQUIRED = 100;

	/**
	 * The hours required
	 */
	private static final int HOURS_REQUIRED = 2_000;

	/**
	 * Checking whether eligible for cape
	 * 
	 * @param player the player
	 * @return eligible
	 */
	public static boolean eligible(Player player) {
		/*
		 * The days
		 */
		int days = PlayerGameTime.getDaysSinceAccountCreated(player);
		/*
		 * Below days
		 */
		if (days < DAYS_REQUIRED) {
			player.getPA().sendMessage("You need to spend " + (DAYS_REQUIRED - days)
					+ " more days on Dawntained to equip this cape.");
			return false;
		}
		/*
		 * The hours
		 */
		int hours = PlayerGameTime.getHoursOnline(player);
		/*
		 * Below hours
		 */
		if (hours < HOURS_REQUIRED) {
			player.getPA().sendMessage("You need to spend " + (HOURS_REQUIRED - hours)
					+ " more hours on Dawntained to equip this cape.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		/*
		 * The cape
		 */
		if (id == 20763 || id == 20764) {
			return eligible(player);
		}
		return true;
	}

	@Override
	public void operate(Player player, int id) {
		player.gfx0(1446);
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
