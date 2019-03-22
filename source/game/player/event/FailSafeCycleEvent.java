package game.player.event;

/**
 * Created by Jason MK on 2018-09-14 at 11:16 AM
 */
public abstract class FailSafeCycleEvent<T> extends CycleEventAdapter<T> {

    private final int maximumExecutionsInclusive;

    private boolean failed;

    public FailSafeCycleEvent(int maximumExecutionsInclusive) {
        this.maximumExecutionsInclusive = maximumExecutionsInclusive;
    }

    @Override
    public final void execute(CycleEventContainer<T> container) {
        if (container.getExecutions() >= maximumExecutionsInclusive) {
            failed = true;
            container.stop();
            onFail(container);
            return;
        }
        onSafe(container);
    }

    public abstract void onSafe(CycleEventContainer<T> container);

    public void onFail(CycleEventContainer<T> container) {

    }

    public boolean isFailed() {
        return failed;
    }
}
