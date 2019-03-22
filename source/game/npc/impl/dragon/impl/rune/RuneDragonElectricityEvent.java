package game.npc.impl.dragon.impl.rune;

import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.entity.Entity;
import game.npc.Npc;
import game.player.Player;
import game.player.event.CycleEventAdapter;
import game.player.event.CycleEventContainer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MK on 2018-06-28 at 4:07 PM
 */
public class RuneDragonElectricityEvent extends CycleEventAdapter<Entity> {

    private final Npc npc;

    private final Player player;

    private Position target;

    public RuneDragonElectricityEvent(Npc npc, Player player, Position target) {
        this.npc = npc;
        this.player = player;
        this.target = target;
    }

    @Override
    public void execute(CycleEventContainer<Entity> container) {
        if (npc == null || player == null) {
            container.stop();
            return;
        }
        if (npc.isDead() || player.dead) {
            container.stop();
            return;
        }
        int executions = container.getExecutions();

        if (executions >= 15) {
            container.stop();
            return;
        }
        if (executions % 5 == 0) {
            Position last = new Position(target);

            Set<Position> positions = target.surroundingUnblocked(1);
            if (!positions.isEmpty()) {
                List<Position> list = new ArrayList<>(positions);

                Collections.shuffle(list);

                Position random = list.stream().findAny().orElse(null);

                if (random != null) {
                    target = random;
                    player.getPA().createPlayersProjectile(last, target, 50, 100, 1488, 0, 0, 0, 0, 0);
                    player.getPA().createPlayersStillGfx(1488, target.getX(), target.getY(), 0, 50);
                }
            }
        }

        if (target.distanceTo(player.getX(), player.getY()) <= 1) {
            Combat.createHitsplatOnPlayerNormal(player, ThreadLocalRandom.current().nextInt(6, 8),
                    ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON);
        }
    }
}
