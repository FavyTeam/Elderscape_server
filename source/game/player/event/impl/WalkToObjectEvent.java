package game.player.event.impl;

import game.entity.Entity;
import game.object.ObjectEvent;
import game.object.click.FirstClickObject;
import game.object.click.FourthClickObject;
import game.object.click.SecondClickObject;
import game.object.click.ThirdClickObject;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MK on 2018-08-08 at 9:58 AM
 */
public class WalkToObjectEvent extends CycleEvent<Entity> {

    private static final int MAXIMUM_ATTEMPTS = 20;

    private final Object object;

    private int attempts;

    public WalkToObjectEvent(Object object) {
        this.object = object;
    }

    @Override
    public void execute(CycleEventContainer<Entity> container) {
        Entity owner = container.getOwner();

        if (owner == null) {
            container.stop();
            return;
        }
        Player player = (Player) owner;

        if (player.isDisconnected()) {
            container.stop();
            return;
        }

        if (player.getObjectId() != object.objectId || player.getObjectX() != object.objectX || player.getObjectY() != object.objectY) {
            container.stop();
            return;
        }

        if (player.distanceToPoint(object.objectX, object.objectY) > 15) {
            container.stop();
            return;
        }

        if (attempts++ > MAXIMUM_ATTEMPTS) {
            container.stop();
            return;
        }

        if (Region.inDistance(player.getInteractingObjectDefinition(), object.face, object.objectX, object.objectY, player.getX(), player.getY())) {
            container.stop();
            if (player.doingClickObjectType1Event) {
                FirstClickObject.firstClickObjectOsrs(player, object.objectId, object.objectX, object.objectY);
            } else if (player.doingClickObjectType2Event) {
                SecondClickObject.secondClickObjectOsrs(player, object.objectId, object.objectX, object.objectY);
            } else if (player.doingClickObjectType3Event) {
                ThirdClickObject.thirdClickObjectOsrs(player, object.objectId, object.objectX, object.objectY);
            } else if (player.doingClickObjectType4Event) {
                FourthClickObject.fourthClickObject(player, object.objectId, object.objectX, object.objectY);
            }
        } else {
            player.setWalkingPacketQueue(object.objectX, object.objectY);
        }
    }

    /**
     * Code which should be ran when the event stops
     */
    @Override
    public void stop() {

    }
}
