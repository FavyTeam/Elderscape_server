package network.sql.table.impl;

import network.sql.table.SQLTableEntry;

/**
 * Created by Jason MK on 2018-08-13 at 10:41 AM
 */
public class LongStringSQLTableEntry implements SQLTableEntry {

    private final int length;

    private final String content;

    public LongStringSQLTableEntry(int length, String content) {
        this.length = length;
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public String getContent() {
        return content;
    }
}
