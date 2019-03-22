package network.sql.batch.impl;

import network.sql.SQLConstants;
import network.sql.batch.SQLTableBatchUpdateEvent;
import network.sql.table.impl.LongStringSQLTableEntry;
import network.sql.transactions.SQLNetworkTransaction;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason MK on 2018-08-13 at 10:54 AM
 */
public class LongStringBatchUpdateEvent extends SQLTableBatchUpdateEvent<LongStringSQLTableEntry> {

    private static final LongStringBatchUpdateEvent INSTANCE = new LongStringBatchUpdateEvent();

    public LongStringBatchUpdateEvent() {
        super(1);
    }

    @Override
    protected SQLNetworkTransaction createTransaction(List<LongStringSQLTableEntry> pending) {
        return connection -> {
            try (PreparedStatement statement = connection.prepareStatement(String.format("INSERT INTO %s (length, content) VALUES(?, ?);",
                    SQLConstants.LOGS_LONG_STRING.toTableName()))) {
                for (LongStringSQLTableEntry entry : pending) {
                    statement.setInt(1, entry.getLength());
                    statement.setString(2, entry.getContent());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        };
    }

    @Override
    protected List<LongStringSQLTableEntry> createBackingList() {
        return new ArrayList<>();
    }

    public static LongStringBatchUpdateEvent getInstance() {
        return INSTANCE;
    }
}
