package com.netflix.paas.cassandra.events;

import com.netflix.paas.cassandra.keys.ColumnFamilyKey;

public class ColumnFamilyUpdateEvent {
    private final ColumnFamilyKey columnFamilyKey;

    public ColumnFamilyUpdateEvent(ColumnFamilyKey columnFamilyKey) {
        super();
        this.columnFamilyKey = columnFamilyKey;
    }

    public ColumnFamilyKey getColumnFamilyKey() {
        return columnFamilyKey;
    }
}
