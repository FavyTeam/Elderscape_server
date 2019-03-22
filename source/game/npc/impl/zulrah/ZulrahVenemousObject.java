package game.npc.impl.zulrah;

import core.ServerConstants;
import game.content.combat.Combat;
import game.npc.Npc;
import game.object.custom.Object;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-18 at 10:17 AM
 */
public class ZulrahVenemousObject extends Object {

	private final Player target;

	private final Npc zulrah;

	private int lastDamageTick;

	public ZulrahVenemousObject(int id, int x, int y, int height, int face, int type, int newId, int ticks, boolean add, Player target, Npc zulrah) {
		super(id, x, y, height, face, type, newId, ticks, add);
		this.target = target;
		this.zulrah = zulrah;
	}

	@Override
	public void onTick() {
		super.onTick();

		if (zulrah == null || zulrah.isDead()) {
			tick = 0;
			return;
		}
		if (Math.abs(tick - lastDamageTick) >= 2 && target.distanceToPoint(objectX + 1, objectY + 1) <= 1) {
			Combat.createHitsplatOnPlayerNormal(target, 2, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
			lastDamageTick = tick;
		}
	}
}
