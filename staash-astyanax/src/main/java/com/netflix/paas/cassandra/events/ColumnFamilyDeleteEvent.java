package com.netflix.paas.cassandra.events;

import com.netflix.paas.cassandra.keys.ColumnFamilyKey;

public class ColumnFamilyDeleteEvent {
    private final ColumnFamilyKey columnFamilyKey;

    public ColumnFamilyDeleteEvent(ColumnFamilyKey columnFamilyKey) {
        super();
        this.columnFamilyKey = columnFamilyKey;
    }

    public ColumnFamilyKey getColumnFamilyKey() {
        return columnFamilyKey;
    }
}
