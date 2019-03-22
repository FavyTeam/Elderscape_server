package game.content.skilling.hunter.trap.impl;

import core.Server;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterEquipment;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.trap.HunterTrap;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Created by Jason MacKeigan on 2018-05-01 at 5:05 PM
 */
public class BoxTrap extends HunterTrap {

	//9380 = open
	//9381 = animation for trapping
	//9382 = shaking box
	//9385 = fail
	public BoxTrap(int x, int y, int height, int face, int type, int newId, int ticks, String owner, HunterStyle style) {
		super(9380, x, y, height, face, type, newId, ticks, owner, style);
	}

	@Override
	public void onCapture(Player hunter, HunterCreature creature) {
		transform(9381);
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer<Object> container) {
				container.stop();
				transform(9382);
			}

			@Override
			public void stop() {  }
		}, 2);
	}

	/**
	 * Referenced when the object is removed from the manager.
	 */
	@Override
	public void onRemove() {
		super.onRemove();

		if (!isItemReturned()) {
			setItemReturned(true);

			Player player = PlayerHandler.getPlayerForName(getOwner());

			if (player == null) {
				return;
			}
			Server.itemHandler.createGroundItem(player, 10008, objectX, objectY, height, 1, true, 0, false,
			                                    getOwner(), "", "", "", "BirdSnareTrap onRemove");
		}
	}

	@Override
	public void onFailCapture(Player hunter, HunterCreature creature) {
		transform(9385);
	}

	@Override
	public HunterEquipment equipment() {
		return HunterEquipment.BOX_TRAP;
	}

	@Override
	public HunterStyle style() {
		return HunterStyle.BOX_TRAPPING;
	}
}
