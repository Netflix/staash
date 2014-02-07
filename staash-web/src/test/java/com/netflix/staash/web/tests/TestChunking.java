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
package com.netflix.staash.web.tests;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.recipes.storage.CassandraChunkedStorageProvider;
import com.netflix.astyanax.recipes.storage.ChunkedStorage;
import com.netflix.astyanax.recipes.storage.ChunkedStorageProvider;
import com.netflix.astyanax.recipes.storage.ObjectMetadata;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.staash.test.core.RequiresColumnFamily;
import com.netflix.staash.test.core.RequiresKeyspace;
import com.netflix.staash.test.core.CassandraRunner;

@RunWith(CassandraRunner.class)
@RequiresKeyspace(ksName = "myks")
@RequiresColumnFamily(ksName = "myks", cfName = "chunks", comparator = "org.apache.cassandra.db.marshal.UTF8Type", keyValidator = "org.apache.cassandra.db.marshal.UTF8Type")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestChunking {
	Keyspace keyspace;
	@Before
	@Ignore
	public void setup() {
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
				.forCluster("Test Cluster")
				.forKeyspace("myks")
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
								.setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE))
				.withConnectionPoolConfiguration(
						new ConnectionPoolConfigurationImpl("MyConnectionPool")
								.setPort(9160).setMaxConnsPerHost(1)
								.setSeeds("127.0.0.1:9160"))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		keyspace = context.getClient();
	}

	@Test
	@Ignore
	public void chunktest() {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, "chunks");
		try {
			InputStream fis = this.getClass().getClassLoader().getResource("chunktest.html").openStream();
			ObjectMetadata meta = ChunkedStorage
					.newWriter(provider, "test1", fis).withChunkSize(0x1000) 
					.withConcurrencyLevel(8) 
					.withTtl(60) // Optional TTL for the entire object
					.call();
			Long writesize = meta.getObjectSize();
			Long readsize = readChunked("myks","chunks","test1");
			assert(writesize == readsize);
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Long readChunked(String db, String table, String objName)
			throws Exception {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, table);
		ObjectMetadata meta = ChunkedStorage.newInfoReader(provider, objName)
				.call();
		ByteArrayOutputStream os = new ByteArrayOutputStream(meta
				.getObjectSize().intValue());
		meta = ChunkedStorage.newReader(provider, objName, os)
				.withBatchSize(10).call();
		return meta.getObjectSize();
	}
}
