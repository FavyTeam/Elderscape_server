package game.content.combat.damage;

import game.entity.Entity;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 1:47 PM
 */
public interface EntityDamageEffect<T extends Entity, S extends Entity> {

	EntityDamage<T, S> onCalculation(EntityDamage<T, S> damage);

	void onApply(EntityDamage<T, S> damage);

}
