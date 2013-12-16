package com.netflix.paas.common.query;

public class QueryException extends Exception {
    private final int errorCode;
    private final String sqlState;

    public QueryException(final String message) {
        super(message);
        this.errorCode = -1;
        this.sqlState = "HY0000";

    }

    public QueryException(final String message, final short errorCode,
            final String sqlState) {
        super(message);
        this.errorCode = errorCode;
        this.sqlState = sqlState;
    }

    public QueryException(final String message, final int errorCode,
            final String sqlState, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.sqlState = sqlState;
    }

    public final int getErrorCode() {
        return errorCode;
    }

    public final String getSqlState() {
        return sqlState;
    }
}
