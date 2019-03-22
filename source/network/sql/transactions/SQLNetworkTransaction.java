package network.sql.transactions;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Jason MacKeigan on 2018-06-15 at 10:19 AM
 */
public interface SQLNetworkTransaction {

	void call(Connection connection) throws SQLException;

}
