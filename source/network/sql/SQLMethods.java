package network.sql;

import core.Server;
import core.ServerConfiguration;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import network.sql.query.impl.LongParameter;
import network.sql.query.impl.StringParameter;
import network.sql.transactions.SQLNetworkTransactionFuture;
import network.sql.transactions.SQLNetworkTransactionFutureCycleEvent;
import utility.Misc;

public class SQLMethods {

	public static void insertStatsDoubleItemsNpcPlayer(String name, long wealthRaw) {
		SQLNetwork.insert("INSERT IGNORE INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_DOUBLE_ITEMS_NPC_PLAYER) + " (time, name, wealth, wealth_raw) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, name), new StringParameter(3, Misc.formatRunescapeStyle(wealthRaw)), new LongParameter(4, wealthRaw));

	}

	public static void updateStatsTimeTable() {
		if (ServerConfiguration.MOCK_SQL) {
			return;
		}
		SQLNetworkTransactionFuture<Integer> future = new SQLNetworkTransactionFuture<Integer>() {
			@Override
			public Integer request(Connection connection) throws SQLException {
				int id = -1;
				try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_TIME) + " limit 1")) {
					try (ResultSet results = statement.executeQuery()) {
						while (results.next()) {
							id = results.getInt("id");
						}
					}
				}
				return id;
			}
		};
		Server.getSqlNetwork().submit(future);

		SQLNetworkTransactionFutureCycleEvent<Integer> futureEvent = new SQLNetworkTransactionFutureCycleEvent<>(future, 20);
		CycleEventContainer<Object> futureEventContainer = CycleEventHandler.getSingleton().addEvent(new Object(), futureEvent, 1);
		futureEventContainer.addStopListener(() -> {
			if (futureEvent.isCompletedWithErrors()) {
				return;
			}
			if (futureEvent.getResult() == -1) {
				SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_TIME) + " (time) VALUES(?)", new StringParameter(1, Misc.getDateAndTime()));
			} else {
				Server.getSqlNetwork().submit(connection -> {
					try (PreparedStatement statement = connection.prepareStatement("UPDATE " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_TIME) + " SET time=? WHERE id=?")) {
						statement.setString(1, Misc.getDateAndTime());
						statement.setInt(2, futureEvent.getResult());
						statement.executeUpdate();
					}
				});
			}
		});
	}
}
