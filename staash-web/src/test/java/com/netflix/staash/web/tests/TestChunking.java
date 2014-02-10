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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.Assert;

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
	private static final String KS = "myks";
	private static final String CF = "chunks";
	private static final String ENC1 = "SHA-1";
	private static final String ENC2 = "MD5";// optional, less strength
	private static final String OBJASC = "testascii";
	private static final String OBJBIN = "testbinary";
	private static final String FILEASC = "chunktest.html";
	private static final String FILEBIN = "test.exe";

	@Before
	public void setup() {
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
				.forCluster("Test Cluster")
				.forKeyspace(KS)
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
	public void chunktestbinary() throws IOException {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, CF);
		InputStream fis = null;
		InputStream bis = null;
		try {
			fis = this.getClass().getClassLoader().getResource(FILEBIN)
					.openStream();
			ObjectMetadata meta = ChunkedStorage
					.newWriter(provider, OBJBIN, fis).withChunkSize(0x1000)
					.withConcurrencyLevel(8).withTtl(60) // Optional TTL for the
															// entire object
					.call();
			Long writesize = meta.getObjectSize();
			// Long readsize = readChunked("myks","chunks","test1");
			byte[] written = new byte[writesize.intValue()];
			 bis =
			 this.getClass().getClassLoader().getResource(FILEBIN).openStream();
			 int i1 = ((BufferedInputStream)bis).read(written, 0,
			 writesize.intValue());
			 System.out.println("length read = "+i1);
			byte[] read = readChunked(KS, CF, OBJBIN);
			boolean cmp = compareMD5(written, read);
			Assert.assertTrue(cmp == true);
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			if (fis != null)
				fis.close();
			if (bis!=null) 
				bis.close();
		}
	}

	@Test
	public void chunktestascii() throws IOException {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, CF);
		InputStream fis = null;
		InputStream bis = null;
		try {
			fis = this.getClass().getClassLoader().getResource(FILEASC)
					.openStream();
			ObjectMetadata meta = ChunkedStorage
					.newWriter(provider, OBJASC, fis).withChunkSize(0x1000)
					.withConcurrencyLevel(8).withTtl(60) // Optional TTL for the
															// entire object
					.call();
			Long writesize = meta.getObjectSize();
			// Long readsize = readChunked("myks","chunks","test1");
			byte[] written = new byte[writesize.intValue()];
			 bis =
			 this.getClass().getClassLoader().getResource("chunktest.html").openStream();
			 int i1 = ((BufferedInputStream)bis).read(written, 0,
			 writesize.intValue());
			 System.out.println("length read = "+i1);
			byte[] read = readChunked(KS, CF, OBJASC);
			boolean cmp = compareMD5(written, read);
			Assert.assertTrue(cmp == true);
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			if (fis != null)
				fis.close();
			if (bis!=null) 
				bis.close();
		} 
	}

	public boolean compareMD5(byte[] written, byte[] read) {
		try {
			MessageDigest md = MessageDigest.getInstance(ENC1);
			byte[] wdigest = md.digest(written);
			byte[] rdigest = md.digest(read);
			return Arrays.equals(wdigest, rdigest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getCause());
		}
	}

	public byte[] readChunked(String db, String table, String objName)
			throws Exception {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, table);
		ObjectMetadata meta = ChunkedStorage.newInfoReader(provider, objName)
				.call();
		ByteArrayOutputStream os = new ByteArrayOutputStream(meta
				.getObjectSize().intValue());
		meta = ChunkedStorage.newReader(provider, objName, os)
				.withBatchSize(10).call();
		return (os != null) ? os.toByteArray() : new byte[0];
	}
}
