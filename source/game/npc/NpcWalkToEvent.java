package game.npc;

import game.entity.Entity;
import game.player.event.FailSafeCycleEvent;
import game.position.Position;
import game.player.event.CycleEventContainer;
import game.position.PositionUtils;
import game.position.distance.DistanceAlgorithms;

/**
 * Created by Jason MK on 2018-09-26 at 10:28 AM
 */
public class NpcWalkToEvent extends FailSafeCycleEvent<Entity> {

    private final Position destination;

    private final int distance;

    private boolean destinationReached;

    public NpcWalkToEvent(int maximumExecutionsInclusive, Position destination, int distance) {
        super(maximumExecutionsInclusive);
        this.destination = destination;
        this.distance = distance;
    }

    @Override
    public void onSafe(CycleEventContainer<Entity> container) {
        Npc npc = (Npc) container.getOwner();

        if (npc == null || npc.isDead() || npc.getCurrentHitPoints() <= 0) {
            container.stop();
            return;
        }
        if (PositionUtils.withinDistance(npc, destination, distance, DistanceAlgorithms.EUCLIDEAN)) {
            container.stop();
            destinationReached = true;
        } else {
            npc.walkTileInDirection(destination.getX(), destination.getY());
        }
    }

    public boolean isDestinationReached() {
        return destinationReached;
    }
}
