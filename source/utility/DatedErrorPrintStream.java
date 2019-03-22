package utility;

import network.sql.batch.impl.OutputBatchUpdateEvent;
import network.sql.table.impl.OutputSQLTableEntry;

import java.io.PrintStream;

/**
 * Created by Jason MK on 2018-08-17 at 11:43 AM
 */
public class DatedErrorPrintStream extends DatedPrintStream {

    public DatedErrorPrintStream(PrintStream out) {
        super(out);
    }

    @Override
    public void printToSql(String str) {
        OutputBatchUpdateEvent.getInstance().submit(new OutputSQLTableEntry(System.currentTimeMillis(), str, OutputSQLTableEntry.Type.ERROR));
    }
}
