package network.sql.transactions;

import game.player.event.CycleEventAdapter;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MacKeigan on 2018-06-15 at 12:04 PM
 */
public class SQLNetworkTransactionFutureCycleEvent<T> extends CycleEventAdapter<Object> {

	private final SQLNetworkTransactionFuture<T> transactionFuture;

	private final int executionsBeforeForcefullyStop;

	private T result;

	private boolean completedWithErrors;

	public SQLNetworkTransactionFutureCycleEvent(SQLNetworkTransactionFuture<T> transactionFuture, int executionsBeforeForcefullyStop) {
		this.transactionFuture = transactionFuture;
		this.executionsBeforeForcefullyStop = executionsBeforeForcefullyStop;
	}

	public SQLNetworkTransactionFutureCycleEvent(SQLNetworkTransactionFuture<T> transactionFuture) {
		this(transactionFuture, 100);
	}

	@Override
	public void execute(CycleEventContainer<Object> container) {
		if (container.getExecutions() >= executionsBeforeForcefullyStop) {
			container.stop();
			return;
		}
		if (transactionFuture.isFinished()) {
			if (transactionFuture.isCompletedWithErrors()) {
				completedWithErrors = true;
			} else {
				result = transactionFuture.getResult();
			}
			container.stop();
		}
	}

	public T getResult() {
		return result;
	}

	public boolean isCompletedWithErrors() {
		return completedWithErrors;
	}

}
