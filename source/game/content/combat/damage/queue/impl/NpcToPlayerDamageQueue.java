package game.content.combat.damage.queue.impl;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.queue.EntityDamageQueue;
import game.npc.Npc;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 2:36 PM
 */
public class NpcToPlayerDamageQueue extends EntityDamageQueue<Player, Npc> {

	@Override
	public void apply(EntityDamage<Player, Npc> damage) {

	}
	
}
