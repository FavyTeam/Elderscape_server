package game.content.cannon;

import core.Server;
import game.position.Position;
import game.entity.Entity;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEventContainer;
import game.player.event.FailSafeCycleEvent;

/**
 * Created by Jason MK on 2018-07-20 at 12:44 PM
 */
public class CannonSetupEvent extends FailSafeCycleEvent<Entity> {

    private final Position position;

    private CannonSetupStage stage = CannonSetupStage.BASE;

    private final Object cannon;

    public CannonSetupEvent(Position position) {
        super(15);
        this.position = position;
        this.cannon = new Object(stage.getObjectId(), position.getX(), position.getY(), position.getZ(), 0, 10, -1, -1, false);
    }

    @Override
    public void onSafe(CycleEventContainer<Entity> container) {
        Player player = (Player) container.getOwner();

        if (!Server.objectManager.exists(cannon.objectId, cannon.objectX, cannon.objectY, cannon.height)) {
            Server.objectManager.addObject(cannon);
            player.startAnimation(827);
            return;
        }
        try {
            stage = stage.next();
        } catch (IllegalStateException ise) {
            player.sendDebugMessage("Finished setting up cannon.");
            container.stop();
            return;
        }
        cannon.transform(stage.getObjectId());
        player.turnPlayerTo(cannon.objectX, cannon.objectY);
        player.startAnimation(827);
    }

}
