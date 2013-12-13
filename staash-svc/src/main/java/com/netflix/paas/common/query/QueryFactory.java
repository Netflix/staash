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
package com.netflix.paas.common.query;

import java.util.List;

import com.netflix.paas.model.StorageType;
import com.netflix.paas.rest.meta.entity.PaasTableEntity;
import com.netflix.paas.rest.util.Pair;

public class QueryFactory {
    public static final String INSERT_FORMAT = "INSERT INTO %s(%s) VALUES (%s)";
    public static final String CREATE_DB_FORMAT_MYSQL = "CREATE DATABASE %s";
    public static final String CREATE_DB_FORMAT_CASS = "CREATE KEYSPACE %s WITH replication = { 'class' : 'SimpleStrategy', 'replication_factor' : %d }";
    public static final String CREATE_TABLE_FORMAT = "CREATE TABLE %s(%s PRIMARY KEY(%s))";
    public static final String SWITCH_DB_FORMAT = "USE %s";
    public static final String SELECT_ALL = "SELECT * FROM %s WHERE %s='%s';";
    public static final String SELECT_EVENT = "SELECT * FROM %s WHERE %s='%s' AND %s=%s;";


    
    public static String  BuildQuery(QueryType qType,StorageType sType) {

        switch (sType) {
            case CASSANDRA:
                switch (qType) {
                case INSERT:
                    return INSERT_FORMAT;
                case CREATEDB:
                    return CREATE_DB_FORMAT_CASS;
                case CREATETABLE:
                    return CREATE_TABLE_FORMAT;
                case SWITCHDB:
                    return SWITCH_DB_FORMAT;
                case SELECTALL:
                    return SELECT_ALL;
                case SELECTEVENT:
                    return SELECT_EVENT;
                }
            case MYSQL:
                switch (qType) {
                case INSERT:
                    return INSERT_FORMAT;
                case CREATEDB:
                    return CREATE_DB_FORMAT_MYSQL;
                case CREATETABLE:
                    return CREATE_TABLE_FORMAT;
                case SWITCHDB:
                    return SWITCH_DB_FORMAT;
                case SELECTALL:
                    return SELECT_ALL;
                }
        }
        return null;
    }
//    //needs to be modified
//    public static  String BuildCreateTableQuery(PaasTableEntity tableEnt, StorageType type) {
//        // TODO Auto-generated method stub
//        String storage = tableEnt.getStorage();
//        if (type!=null && type.getCannonicalName().equals(StorageType.MYSQL.getCannonicalName())) {
//            String schema = tableEnt.getSchemaName();
//            String tableName = tableEnt.getName().split("\\.")[1];
//            List<Pair<String, String>> columns = tableEnt.getColumns();
//            String colStrs = "";
//            for (Pair<String, String> colPair : columns) {
//                colStrs = colStrs + colPair.getRight() + " " + colPair.getLeft()
//                        + ", ";
//            }
//            String primarykeys = tableEnt.getPrimarykey();
//            String PRIMARYSTR = "PRIMARY KEY(" + primarykeys + ")";
//            return "CREATE TABLE " +  tableName + " (" + colStrs
//                    + " " + PRIMARYSTR + ");";
//        } else {
//        String schema = tableEnt.getSchemaName();
//        String tableName = tableEnt.getName().split("\\.")[1];
//        List<Pair<String, String>> columns = tableEnt.getColumns();
//        String colStrs = "";
//        for (Pair<String, String> colPair : columns) {
//            colStrs = colStrs + colPair.getRight() + " " + colPair.getLeft()
//                    + ", ";
//        }
//        String primarykeys = tableEnt.getPrimarykey();
//        String PRIMARYSTR = "PRIMARY KEY(" + primarykeys + ")";
//        return "CREATE TABLE " + schema + "." + tableName + " (" + colStrs
//                + " " + PRIMARYSTR + ");";
//        }
//    }
    
}
