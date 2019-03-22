package network.sql.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Parameter<T> {

	private final int parameterId;

	private final T value;

	public Parameter(int parameterId, T value) {
		this.parameterId = parameterId;
		this.value = value;
	}

	public abstract void set(PreparedStatement statement) throws SQLException;

	public int getParameterId() {
		return parameterId;
	}

	public T getValue() {
		return value;
	}
}