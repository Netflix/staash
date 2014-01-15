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
package com.netflix.staash.connection;

import java.io.InputStream;
import java.io.OutputStream;
import com.netflix.staash.json.JsonObject;

public interface PaasConnection {
    public String insert(String db, String table, JsonObject payload);
    public String read(String db, String table, String keycol, String key, String... keyvals);
    public String createDB(String dbname);
    public String createTable(JsonObject payload);
    public void closeConnection();
    public OutputStream readChunked(String db, String table, String objectName) throws Exception;
    public String writeChunked(String db, String table, InputStream is) throws Exception;
}
