package game.player.event.impl;

import core.Server;
import core.benchmark.GameBenchmark;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import network.login.RS2LoginProtocolDecoder;
import network.login.RS2LoginProtocolDecoder.UniqueIdentifierSet;
import network.sql.SQLConstants;

/**
 * Created by Jason MK on 2018-08-10 at 9:47 AM
 */
public final class IdentifierSetAnalysisEvent extends CycleEvent<Object> {

    private static final IdentifierSetAnalysisEvent INSTANCE = new IdentifierSetAnalysisEvent();

    private final Collection<RS2LoginProtocolDecoder.UniqueIdentifierSet> pendingPush = new CopyOnWriteArrayList<>();

    private IdentifierSetAnalysisEvent() {

    }

    /**
     * Code which should be ran when the event is executed
     *
     * @param container
     */
    @Override
    public void execute(CycleEventContainer<Object> container) {
		if (pendingPush.isEmpty()) {
            return;
        }
		new GameBenchmark("identifier-analysis-flush", this::flush, 100, TimeUnit.MILLISECONDS).execute();
    }

    public void add(RS2LoginProtocolDecoder.UniqueIdentifierSet set) {
        pendingPush.add(set);
    }

    private void flush() {
        Collection<RS2LoginProtocolDecoder.UniqueIdentifierSet> pushed = new ArrayList<>(pendingPush);
        pendingPush.removeAll(pushed);
		writeToSql(pushed, SQLConstants.STATS_UNIQUE_IDENTIFIER_ANALYSIS.toTableName());
		writeToSql(pushed, SQLConstants.STATS_UNIQUE_IDENTIFIER_ANALYSIS_ALL.toTableName());
	}

	private void writeToSql(Collection<UniqueIdentifierSet> pushed, String sqlTable) {
		Server.getSqlNetwork().submit(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
					String.format("INSERT IGNORE INTO %s (date, username, ip_address, os, serial, windows_uid_basic, windows_sn_different, base_board_serial_id, hard_disk_serial, file_store_uuid, uuids) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
							sqlTable))) {
                for (RS2LoginProtocolDecoder.UniqueIdentifierSet set : pushed) {
					statement.setString(1, set.getDate());
					statement.setString(2, set.getUsername());
					statement.setString(3, set.getIpAddress());
					statement.setString(4, set.osName);
					statement.setString(5, set.getSerial());
					statement.setString(6, set.windowsUidBasic);
					statement.setString(7, set.windowsSnDifferent);
					statement.setString(8, set.baseBoardSerialId);
					String combined = "";
					for (int index = 0; index < set.hardDiskSerials.size(); index++) {
						String extra = index == set.hardDiskSerials.size() - 1 ? "" : "#!#";
						combined = combined + set.hardDiskSerials.get(index) + extra;
					}
					statement.setString(9, combined);
					combined = "";

					for (int index = 0; index < set.fileStoreUuids.size(); index++) {
						String extra = index == set.fileStoreUuids.size() - 1 ? "" : "#!#";
						combined = combined + set.fileStoreUuids.get(index) + extra;
					}
					statement.setString(10, combined);
					combined = "";

					for (int index = 0; index < set.uuids.size(); index++) {
						String extra = index == set.uuids.size() - 1 ? "" : "#!#";
						combined = combined + set.uuids.get(index) + extra;
					}
					statement.setString(11, combined);
					combined = "";

                    statement.addBatch();
                }
                statement.executeBatch();
            }
        });
	}

	@Override
    public void stop() {

    }

    public static IdentifierSetAnalysisEvent getInstance() {
        return INSTANCE;
    }
}
