package game.content.skilling.hunter.trap.impl;

import core.Server;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterEquipment;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.trap.HunterTrap;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 10:24 AM
 */
public class BirdSnareTrap extends HunterTrap {

	public BirdSnareTrap(int x, int y, int height, int face, int type, String owner) {
		super(9345, x, y, height, face, type, -1, 180, owner, HunterStyle.BIRD_SNARING);
	}

	@Override
	public void onCapture(Player hunter, HunterCreature creature) {
		transform(creature.objectTransformedOnCapture());
	}

	@Override
	public void onFailCapture(Player hunter, HunterCreature creature) {
		transform(9344);
	}

	/**
	 * Referenced when the object is removed from the manager.
	 */
	@Override
	public void onRemove() {
		super.onRemove();

		if (!super.isItemReturned()) {
			super.setItemReturned(true);

			Player player = PlayerHandler.getPlayerForName(getOwner());

			if (player == null) {
				return;
			}
			Server.itemHandler.createGroundItem(player, 10006, objectX, objectY, height, 1, true, 0, false,
					getOwner(), "", "", "", "BirdSnareTrap onRemove");
		}
	}

	@Override
	public HunterEquipment equipment() {
		return HunterEquipment.BIRD_SNARE;
	}

	@Override
	public HunterStyle style() {
		return HunterStyle.BIRD_SNARING;
	}
}
