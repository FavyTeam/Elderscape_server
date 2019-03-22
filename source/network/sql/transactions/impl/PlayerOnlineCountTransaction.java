package network.sql.transactions.impl;

import network.sql.transactions.SQLNetworkTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Jason MK on 2018-07-12 at 2:28 PM
 */
public class PlayerOnlineCountTransaction implements SQLNetworkTransaction {

    private final String date;

    private final int count;

    public PlayerOnlineCountTransaction(String date, int count) {
        this.date = date;
        this.count = count;
    }

    @Override
    public void call(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO server.stats_player_online (time, online_count) VALUES(?, ?)")) {
            statement.setString(1, date);
            statement.setInt(2, count);
            statement.executeUpdate();
        }
    }
}
