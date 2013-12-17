/*******************************************************************************
 * /*
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
 *  *
 ******************************************************************************/
package com.netflix.paas.common.query;

public enum QueryType {
    REPLACE, INSERT, SELECT, UPDATE, DELETE, ALTER, UNCLASSIFIABLE,CREATEDB,CREATETABLE,CREATESERIES,SWITCHDB,SELECTALL,SELECTEVENT;

    public static QueryType classifyQuery(final String query) {
        final String lowerCaseQuery = query.toLowerCase();
        if (lowerCaseQuery.startsWith("select")) {
            return QueryType.SELECT;
        } else if (lowerCaseQuery.startsWith("update")) {
            return QueryType.UPDATE;
        } else if (lowerCaseQuery.startsWith("insert")) {
            return QueryType.INSERT;
        } else if (lowerCaseQuery.startsWith("alter")) {
            return QueryType.ALTER;
        } else if (lowerCaseQuery.startsWith("delete")) {
            return QueryType.DELETE;
        } else if (lowerCaseQuery.startsWith("replace")) {
            return QueryType.REPLACE;
        } else if (lowerCaseQuery.startsWith("createdb")) {
            return QueryType.CREATEDB;
        } else if (lowerCaseQuery.startsWith("createtable")) {
            return QueryType.CREATETABLE;
        } else if (lowerCaseQuery.startsWith("switchdb")) {
            return QueryType.SWITCHDB;
        } else if (lowerCaseQuery.startsWith("selectall")) {
            return QueryType.SELECTALL;
        } else if (lowerCaseQuery.startsWith("createseries")) {
            return QueryType.CREATESERIES;
        } else if (lowerCaseQuery.startsWith("selectevent")) {
            return QueryType.SELECTEVENT;
        } else {
            return QueryType.UNCLASSIFIABLE;
        }
    }
}
