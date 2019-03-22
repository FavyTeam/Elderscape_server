package network.sql.batch.impl;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import network.sql.SQLConstants;
import network.sql.batch.SQLTableBatchUpdateEvent;
import network.sql.table.impl.OutputSQLTableEntry;
import network.sql.transactions.SQLNetworkTransaction;
import utility.Misc;

/**
 * Created by Jason MK on 2018-08-17 at 9:08 AM
 */
public class OutputBatchUpdateEvent extends SQLTableBatchUpdateEvent<OutputSQLTableEntry> {

    private static final OutputBatchUpdateEvent INSTANCE = new OutputBatchUpdateEvent();

    private OutputBatchUpdateEvent() {
        super(50);
    }

    @Override
    public void submit(OutputSQLTableEntry entry) {
        super.submit(entry);

        if (entry.getTypeToString().equals(OutputSQLTableEntry.Type.ERROR.name())) {
            forceUpdate();
        }
    }

    @Override
    protected SQLNetworkTransaction createTransaction(List<OutputSQLTableEntry> pending) {
        return connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.OUTPUT_LOG) + " (time_of_output, date_and_time, content, content_type) VALUES (?, ?, ?, ?);")) {
                for (OutputSQLTableEntry entry : pending) {
                    statement.setLong(1, entry.getTimeOfOutput());
                    statement.setString(2, Misc.getDateAndTimeAndSeconds());
                    statement.setString(3, entry.getOutput());
                    statement.setString(4, entry.getTypeToString());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        };
    }

    @Override
    protected List<OutputSQLTableEntry> createBackingList() {
        return new CopyOnWriteArrayList<>();
    }

    public static OutputBatchUpdateEvent getInstance() {
        return INSTANCE;
    }
}
