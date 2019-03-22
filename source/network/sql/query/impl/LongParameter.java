package network.sql.query.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import network.sql.query.Parameter;

/**
 * Created by MGT Madness on 13-07-2018.
 */
public class LongParameter extends Parameter<Long> {

	public LongParameter(int parameterId, long value) {
		super(parameterId, value);
	}

	@Override
	public void set(PreparedStatement statement) throws SQLException {
		statement.setLong(getParameterId(), super.getValue());
	}
}
