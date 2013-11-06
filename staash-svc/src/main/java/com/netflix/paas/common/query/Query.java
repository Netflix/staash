package com.netflix.paas.common.query;

import java.io.IOException;
import java.io.OutputStream;

    public interface Query {
        int length() throws QueryException;

        void writeTo(OutputStream os) throws IOException, QueryException;

        String getQuery();

        QueryType getQueryType();

        void writeTo(OutputStream ostream, int offset, int packLength) throws IOException, QueryException;
    }
