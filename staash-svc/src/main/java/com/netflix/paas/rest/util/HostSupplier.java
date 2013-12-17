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
package com.netflix.paas.rest.util;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Supplier;
import com.netflix.astyanax.connectionpool.Host;

public class HostSupplier implements Supplier<List<Host>> {

    public List<Host> get() {
        // TODO Auto-generated method stub
        List<Host>  list = new ArrayList<Host>();
//        Host h1 = new Host("ec2-54-235-224-8.compute-1.amazonaws.com",7102);
        Host h1 = new Host("54.235.224.8",7102);

//        Host h2 = new Host("ec2-54-224-106-243.compute-1.amazonaws.com",7102);
        Host h2 = new Host("54.224.106.243",7102);

//        Host h3 = new Host("ec2-54-242-127-138.compute-1.amazonaws.com",7102);
        Host h3 = new Host("54.242.127.138",7102);

        list.add(h1);
        list.add(h2);
        list.add(h3);
        return list;
    } 

}
