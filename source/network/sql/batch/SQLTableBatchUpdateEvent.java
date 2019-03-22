package network.sql.batch;

import core.Server;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import network.sql.table.SQLTableEntry;
import network.sql.transactions.SQLNetworkTransaction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Jason MK on 2018-08-13 at 10:43 AM
 */
public abstract class SQLTableBatchUpdateEvent<T extends SQLTableEntry> extends CycleEvent<Object> {

    private final List<T> pending;

    private final int thresholdBeforeUpdate;

    private boolean forceUpdate;

    public SQLTableBatchUpdateEvent(int thresholdBeforeUpdate) {
        this.thresholdBeforeUpdate = thresholdBeforeUpdate;
        this.pending = createBackingList();
    }

    public void submit(T entry) {
        pending.add(entry);
    }

    @Override
    public void execute(CycleEventContainer<Object> container) {
        if (!forceUpdate && pending.size() < thresholdBeforeUpdate) {
            return;
        }
        forceUpdate = false;
        Server.getSqlNetwork().submit(createTransaction(new ArrayList<>(pending)));
        pending.clear();
    }

    public void forceUpdate() {
        forceUpdate = true;
    }

    protected abstract List<T> createBackingList();

    protected abstract SQLNetworkTransaction createTransaction(List<T> pending);

    @Override
    public void stop() {

    }
}
