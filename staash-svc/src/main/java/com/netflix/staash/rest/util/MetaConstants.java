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
    public static final String CASSANDRA_KEYSPACE_ENTITY_TYPE = "com.netflix.entity.type.cassandra.keyspace";
    public static final String STAASH_TABLE_ENTITY_TYPE = "com.netflix.entity.type.staash.table";
    public static final String STAASH_STORAGE_TYPE_ENTITY = "com.netflix.entity.type.staash.storage";
    public static final String STAASH_DB_ENTITY_TYPE = "com.netflix.entity.type.staash.db";
    public static final String STAASH_TS_ENTITY_TYPE = "com.netflix.entity.type.staash.timeseries";
    public static final String STAASH_KV_ENTITY_TYPE = "com.netflix.entity.type.staash.keyvaluestore";
    public static final String CASSANDRA_CF_TYPE = "com.netflix.entity.type.cassandra.columnfamily";
    public static final String CASSANDRA_TIMESERIES_TYPE = "com.netflix.entity.type.cassandra.timeseries";
    public static final String PAAS_CLUSTER_ENTITY_TYPE = "com.netflix.entity.type.staash.table";
    public static final String STORAGE_TYPE = "com.netflix.trait.type.storagetype";
    public static final String RESOLUTION_TYPE = "com.netflix.trait.type.resolutionstring";
    public static final String NAME_TYPE = "com.netflix.trait.type.name";
    public static final String RF_TYPE = "com.netflix.trait.type.replicationfactor";
    public static final String STRATEGY_TYPE = "com.netflix.trait.type.strategy";
    public static final String COMPARATOR_TYPE = "com.netflix.trait.type.comparator";
    public static final String KEY_VALIDATION_CLASS_TYPE = "com.netflix.trait.type.key_validation_class";
    public static final String COLUMN_VALIDATION_CLASS_TYPE = "com.netflix.trait.type.validation_class";
    public static final String DEFAULT_VALIDATION_CLASS_TYPE = "com.netflix.trait.type.default_validation_class";
    public static final String COLUMN_NAME_TYPE = "com.netflix.trait.type.colum_name";
    public static final String CONTAINS_TYPE = "com.netflix.relation.type.contains";
    public static final String PERIOD_TIME_SERIES = "period";
    public static final String PREFIX_TIME_SERIES = "prefix";
    public static final String META_KEY_SPACE = "staashmetaks_cde";
    public static final String META_COLUMN_FAMILY = "staashmetacf_cde";
}
