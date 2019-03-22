package network.sql.table.impl;

import network.sql.table.SQLTableEntry;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by Jason MK on 2018-08-17 at 9:08 AM
 */
public class OutputSQLTableEntry implements SQLTableEntry {

    private final long timeOfOutput;

    private final String output;

    private final Type type;

    public OutputSQLTableEntry(long timeOfOutput, String output, Type type) {
        this.timeOfOutput = timeOfOutput;
        this.output = output;
        this.type = type;
    }

    public OutputSQLTableEntry(long timeOfOutput, String output) {
        this(timeOfOutput, output, Type.OUTPUT);
    }

    public OutputSQLTableEntry(long timeOfOutput, Throwable throwable) {
        this(timeOfOutput, ExceptionUtils.getStackTrace(throwable), Type.ERROR);
    }

    public long getTimeOfOutput() {
        return timeOfOutput;
    }

    public String getOutput() {
        return output;
    }

    public String getTypeToString() {
        return type.name();
    }

    public enum Type {
        OUTPUT, ERROR
    }
}
