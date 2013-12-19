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
package com.netflix.staash.rest.util;

public interface MetaConstants {
    public static final String CASSANDRA_KEYSPACE_ENTITY_TYPE = "com.test.entity.type.cassandra.keyspace";
    public static final String PAAS_TABLE_ENTITY_TYPE = "com.test.entity.type.paas.table";
    public static final String PAAS_STORAGE_TYPE_ENTITY = "com.test.entity.type.paas.storage";
    public static final String PAAS_DB_ENTITY_TYPE = "com.test.entity.type.paas.db";
    public static final String PAAS_TS_ENTITY_TYPE = "com.test.entity.type.paas.timeseries";
    public static final String CASSANDRA_CF_TYPE = "com.test.entity.type.cassandra.columnfamily";
    public static final String CASSANDRA_TIMESERIES_TYPE = "com.test.entity.type.cassandra.timeseries";
    public static final String PAAS_CLUSTER_ENTITY_TYPE = "com.test.entity.type.paas.table";
    public static final String STORAGE_TYPE = "com.test.trait.type.storagetype";
    public static final String RESOLUTION_TYPE = "com.test.trait.type.resolutionstring";
    public static final String NAME_TYPE = "com.test.trait.type.name";
    public static final String RF_TYPE = "com.test.trait.type.replicationfactor";
    public static final String STRATEGY_TYPE = "com.test.trait.type.strategy";
    public static final String COMPARATOR_TYPE = "com.test.trait.type.comparator";
    public static final String KEY_VALIDATION_CLASS_TYPE = "com.test.trait.type.key_validation_class";
    public static final String COLUMN_VALIDATION_CLASS_TYPE = "com.test.trait.type.validation_class";
    public static final String DEFAULT_VALIDATION_CLASS_TYPE = "com.test.trait.type.default_validation_class";
    public static final String COLUMN_NAME_TYPE = "com.test.trait.type.colum_name";
    public static final String CONTAINS_TYPE = "com.test.relation.type.contains";
    public static final String PERIOD_TIME_SERIES = "period";
    public static final String PREFIX_TIME_SERIES = "prefix";
    public static final String META_KEY_SPACE = "paasmetaks";
    public static final String META_COLUMN_FAMILY = "metacf";
}
