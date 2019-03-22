package network.sql.query.impl;

import network.sql.query.Parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Jason MK on 2018-07-12 at 3:03 PM
 */
public class StringParameter extends Parameter<String> {

    public StringParameter(int parameterId, String value) {
        super(parameterId, value);
    }

    @Override
    public void set(PreparedStatement statement) throws SQLException {
        statement.setString(getParameterId(), getValue());
    }
}
