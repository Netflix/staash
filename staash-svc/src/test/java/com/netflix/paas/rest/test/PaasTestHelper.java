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
package com.netflix.paas.rest.test;

public class PaasTestHelper {
    public static final String ServerUrl = "http://localhost:8080";
    public static final String CreateDBUrl = "/paas/v1/admin";
    public static final String CreateDBPayload = "{name: testdb}";
    public static final String ListDBUrl = "/paas/v1/admin";
    public static final String CreateStorageUrl = "http://localhost:8080/paas/v1/admin/storage";
    public static final String CreateStoragePayloadCassandra = "{name:testStorageCass, type: cassandra, cluster:local, replicateto:newcluster";
    public static final String ListStorage = "/paas/v1/admin/storage";
    public static final String CreateTableUrl = "/paas/v1/admin/testdb";
    public static final String CreateTablePayload = "{name:testtable, columns:user,friends,wall,status, primarykey:user, storage: teststoagecass}";
    public static final String ListTablesUrl = "/paas/v1/admin/testdb";
    public static final String InsertRowUrl = "/paas/v1/data/testdb/testtable";
    public static final String InserRowUrlPayload = "{columns:user,friends,wall,status,values:rogerfed,rafanad,blahblah,blahblahblah}";
    public static final String ReadRowUrl = "/paas/v1/data/testdb/testtable/username/rogerfed";
    public static final String CreateTimeSeriesUrl = "/paa/v1/admin/timeseries/testdb";
    public static final String CreateTimeSeriesPayload = "{\"name\":\"testseries\",\"msperiodicity\":10000,\"prefix\":\"rogerfed\"}";
    public static final String ListTimeSeriesUrl = "/paas/v1/admin/timeseries/testdb";
    public static final String CreateEventUrl = "/paas/v1/admin/timeseries/testdb/testseries";
    public static final String CreateEventPayload = "{\"time\":1000000,\"event\":\"{tweet: enjoying a cruise}}\""; 
    public static final String ReadEventUrl = "/paas/v1/data/timeseries/testdb/testseries/time/100000/prefix/rogerfed";
}
