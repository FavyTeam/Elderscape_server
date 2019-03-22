package game.npc.impl.tekton;

import core.ServerConstants;
import game.entity.Entity;
import game.npc.Npc;
import game.npc.combat.Damage;
import game.npc.combat.DamageQueue;
import game.player.Player;
import game.player.event.CycleEventContainer;
import game.player.event.FailSafeCycleEvent;
import game.position.Position;
import utility.Misc;

import java.util.*;

/**
 * Created by Jason MK on 2018-09-28 at 10:07 AM
 */
public class TektonFireAttackEvent extends FailSafeCycleEvent<Entity> {

    public TektonFireAttackEvent(int maximumExecutionsInclusive) {
        super(maximumExecutionsInclusive);
        System.out.println("Created fire event");
    }

    @Override
    public void onSafe(CycleEventContainer<Entity> container) {
        final Npc tekton = (Npc) container.getOwner();

        if (tekton.isDead() || tekton.getCurrentHitPoints() <= 0) {
            container.stop();
            return;
        }
        for (Player next : tekton.getLocalPlayers()) {
            Position start = Misc.random(Tekton.FIRE_STARTING_POSITIONS);

            if (start == null) {
                continue;
            }
            Position target = new Position(next);

            next.getPA().createPlayersProjectile(start.translate(0, 0, next.getHeight()), target,
                    50, 65, 130, 35, 15, 0, 0, 30);

            DamageQueue.add(new Damage(next, tekton, ServerConstants.MAGIC, 4, 30, -1, p ->
                    target.matches(p.getX(), p.getY(), p.getHeight()), null, (value, p) -> {
                next.getPA().createPlayersStillGfx(131, target.getX(), target.getY(), 25, 0);
            }));
        }
    }
}
