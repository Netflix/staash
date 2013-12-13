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
package com.netflix.paas.rest.meta.entity;

public  class Entity {
    protected String rowKey;    
    protected String name;    
    protected String payLoad;
        
    public String getRowKey() {
        return rowKey;
    }

    public String getName() {
        return name;
    }
    protected void setRowKey(String rowkey) {
        this.rowKey = rowkey;
    }
    protected void setName(String name) {
        this.name = name;
    }

    public String getPayLoad() {
        return payLoad;
    }

    protected void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }
}
