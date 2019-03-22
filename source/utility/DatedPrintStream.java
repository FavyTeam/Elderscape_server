package utility;

import network.sql.batch.impl.OutputBatchUpdateEvent;
import network.sql.table.impl.OutputSQLTableEntry;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatedPrintStream extends PrintStream {

	private DateFormat dateFormat = new SimpleDateFormat();

	private Date cachedDate = new Date();

	private SimpleTimer refreshTimer = new SimpleTimer();

	private String getPrefix() {
		if (refreshTimer.elapsed() > 1000) {
			refreshTimer.reset();
			cachedDate = new Date();
		}
		return dateFormat.format(cachedDate);
	}

	public DatedPrintStream(PrintStream out) {
		super(out);
	}

	public void printToSql(String str) {
		OutputBatchUpdateEvent.getInstance().submit(new OutputSQLTableEntry(System.currentTimeMillis(), str));
	}

	@Override
	public void print(String str) {
		printToSql(str);
		String string = "[" + getPrefix() + "]: " + str;
		super.print(string);
	}
}
