package game.content.combat.damage.queue.impl;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.queue.EntityDamageQueue;
import game.content.combat.vsnpc.CombatNpc;
import game.npc.Npc;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 2:35 PM
 */
public class PlayerToNpcDamageQueue extends EntityDamageQueue<Npc, Player> {

	@Override
	public void apply(EntityDamage<Npc, Player> damage) {
		CombatNpc.applyHitSplatOnNpc(damage.getSender(), damage.getTarget(), damage.getDamage(),
				damage.getDamageType().getPreEocHitsplatColour(), damage.getDamageType().getPreEocHitsplatIcon(), 1);
	}
}
