package game.entity.attributes;

import org.omg.CORBA.TRANSIENT;

/**
 * Created by Jason MacKeigan on 2018-06-07 at 3:01 PM
 */
public enum AttributeSerializationType {
	// The attribute is never serialized to the account
	TRANSIENT,

	// The attribute is serialized to the account
	PERMANENT
}
