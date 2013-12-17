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
package com.netflix.paas.cassandra.discovery;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.AmazonInfo.MetaDataKey;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Application;

public class EurekaAstyanaxHostSupplier implements HostSupplierProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaAstyanaxHostSupplier.class);
    
    private final DiscoveryClient eurekaClient;

    public EurekaAstyanaxHostSupplier() {
        this.eurekaClient = DiscoveryManager.getInstance().getDiscoveryClient();
        Preconditions.checkNotNull(this.eurekaClient);
    }

    public Supplier<List<Host>> getSupplier(final String clusterName) {
        return new Supplier<List<Host>>() {

            public List<Host> get() {
                Application app = eurekaClient.getApplication(clusterName.toUpperCase());
                List<Host> hosts = Lists.newArrayList();
                if (app == null) {
                    LOG.warn("Cluster '{}' not found in eureka", new Object[]{clusterName});
                }
                else {
                    List<InstanceInfo> ins = app.getInstances();
                    if (ins != null && !ins.isEmpty()) {
                        hosts = Lists.newArrayList(Collections2.transform(
                                        Collections2.filter(ins, new Predicate<InstanceInfo>() {
                                            public boolean apply(InstanceInfo input) {
                                                return input.getStatus() == InstanceInfo.InstanceStatus.UP;
                                            }
                                        }), new Function<InstanceInfo, Host>() {
                                            public Host apply(InstanceInfo info) {
                                                String[] parts = StringUtils.split(
                                                        StringUtils.split(info.getHostName(), ".")[0], '-');
        
                                                Host host = new Host(info.getHostName(), info.getPort())
                                                        .addAlternateIpAddress(
                                                                StringUtils.join(new String[] { parts[1], parts[2], parts[3],
                                                                        parts[4] }, "."))
                                                        .addAlternateIpAddress(info.getIPAddr())
                                                        .setId(info.getId());
                                                
                                                try {
                                                    if (info.getDataCenterInfo() instanceof AmazonInfo) {
                                                        AmazonInfo amazonInfo = (AmazonInfo)info.getDataCenterInfo();
                                                        host.setRack(amazonInfo.get(MetaDataKey.availabilityZone));
                                                    }
                                                }
                                                catch (Throwable t) {
                                                    LOG.error("Error getting rack for host " + host.getName(), t);
                                                }
        
                                                return host;
                                            }
                                        }));
                    }
                    else {
                        LOG.warn("Cluster '{}' found in eureka but has no instances", new Object[]{clusterName});
                    }
                }
                return hosts;
            }
        };
    }
}
