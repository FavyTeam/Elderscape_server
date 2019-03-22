package network.sql.query.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import network.sql.query.Parameter;

/**
 * Created by MGT Madness on 13-07-2018.
 */
public class DoubleParameter extends Parameter<Double> {

	public DoubleParameter(int parameterId, double value) {
		super(parameterId, value);
	}

	@Override
	public void set(PreparedStatement statement) throws SQLException {
		statement.setDouble(getParameterId(), super.getValue());
	}
}
