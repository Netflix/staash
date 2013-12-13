/*******************************************************************************
 * /***
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 ******************************************************************************/
package com.netflix.paas.json;

public class PaasJson {
    public static final String CASS_STORAGE = new JsonObject().putString("name", "name")
                                                                .putString("type", "cassandra")
                                                                .putString("cluster", "cluster")
                                                                .putString("port", "port")
                                                                .putString("replicationfactor", "3")
                                                                .putString("strategy", "NetworkTopologyStrategy")
                                                                .putString("asyncreplicate", "")
                                                                .putString("poolsize", "3")
                                                                .toString();
    public static final String MYSQL_STORAGE = new JsonObject().putString("name", "name")
                                                                .putString("type", "mysql")
                                                                .putString("host", "host")
                                                                .putString("jdbcurl", "jdbc:mysql://localhost:3306/")
                                                                .putString("user", "user")
                                                                .putString("port", "port")
                                                                .putString("asyncreplicate", "")
                                                                .toString();
    public static final String PASS_DB =  new JsonObject().putString("name", "name").toString();
    public static final String PASS_TABLE =     new JsonObject().putString("name","name")
                                                                .putString("columns", "column1,column2")
                                                                .putString("storage", "storagename")
                                                                .putString("indexrowkeys", "true")
                                                                .toString();
    public static final String PASS_TIMESERIES = new JsonObject().putString("name", "name")
                                                                 .putString("periodicity", "milliseconds")
                                                                 .putString("prefix", "yesorno")
                                                                 .toString();
    public static final String TABLE_INSERT_ROW = new JsonObject().putString("columns", "col1,col2,col3")
                                                                    .putString("values", "value1,value2,value3")
                                                                    .toString();
    public static final String TS_INSERT_EVENT = new JsonObject().putString("time", "milliseconds")
                                                                 .putString("event", "event payload")
                                                                 .putString("prefix", "prefix")
                                                                 .toString();
    public static final String READ_EVENT_URL = "http://hostname:8080/paas/v1/data/time/100000";
    public static final String READ_ROW_URL = "http://hostname:8080/paas/v1/data";
    public static final String LIST_SCHEMAS_URL = "http://hostname:8080/paas/v1/admin";
    public static final String LIST_STORAGE_URL = "http://hostname:8080/paas/v1/admin/storage";
    public static final String LIST_TABLES_URL = "http://hostname:8080/paas/v1/admin/db";
    public static final String CREATE_DB_URL = "http://hostname:8080/paas/v1/admin";
    public static final String CREATE_TABLE_URL = "http://hostname:8080/paas/v1/admin/db";
    public static final String CREATE_TS_URL = "http://hostname:8080/paas/v1/admin/db/timeseries";
    public static final String CREATE_STORAGE_URL = "http://hostname:8080/paas/v1/admin/storage";
    public static final String LIST_TS_URL = "http://hostname:8080/paas/v1/admin/db/timeseries";
    public static final String INSERT_EVENT_URL = "http://hostname:8080/paas/v1/data/db/timeseries";
    public static final String WRITE_ROW_URL = "http://hostname:8080/paas/v1/data/db/table";
}
