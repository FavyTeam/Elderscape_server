package network.sql.transactions;

import utility.Misc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Jason MacKeigan on 2018-06-15 at 12:00 PM
 */
public abstract class SQLNetworkTransactionFuture<T> implements SQLNetworkTransaction {

	private T result;

	private boolean finished;

	private boolean completedWithErrors;

	@Override
	public void call(Connection connection) {
		try {
			result = request(connection);
			finished = true;
		} catch (SQLException exception) {
			finished = true;
			completedWithErrors = true;
			Misc.print(exception);
		}
	}

	public abstract T request(Connection connection) throws SQLException;

	public boolean isFinished() {
		return finished;
	}

	public boolean isCompletedWithErrors() {
		return completedWithErrors;
	}

	public T getResult() {
		return result;
	}

}
