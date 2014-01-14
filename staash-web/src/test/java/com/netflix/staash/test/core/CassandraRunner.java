
package com.netflix.staash.test.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.db.ColumnFamilyType;
import org.apache.cassandra.db.marshal.CounterColumnType;
import org.apache.cassandra.db.marshal.TypeParser;
import org.apache.cassandra.exceptions.AlreadyExistsException;
import org.apache.cassandra.service.MigrationManager;
import org.apache.cassandra.service.StorageProxy;
import org.apache.cassandra.service.StorageService;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraRunner extends BlockJUnit4ClassRunner {

  private static final Logger logger = LoggerFactory.getLogger(CassandraRunner.class);
  
  static StaashDeamon staashDaemon;

  static ExecutorService executor = Executors.newSingleThreadExecutor();

  public CassandraRunner(Class<?> klass) throws InitializationError {
    super(klass);
    logger.debug("CassandraRunner constructed with class {}", klass.getName());
  }

  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    logger.debug("runChild invoked on method: " + method.getName());
    RequiresKeyspace rk = method.getAnnotation(RequiresKeyspace.class);
    RequiresColumnFamily rcf = method.getAnnotation(RequiresColumnFamily.class);
    if ( rk != null ) {
      maybeCreateKeyspace(rk, rcf);
    } else if ( rcf != null ) {
      maybeCreateColumnFamily(rcf);
    }

    super.runChild(method, notifier);
  }

  
  @Override
  public void run(RunNotifier notifier) {
    startCassandra();
    RequiresKeyspace rk = null;
    RequiresColumnFamily rcf = null;
    for (Annotation ann : getTestClass().getAnnotations() ) {
      if ( ann instanceof RequiresKeyspace ) {
        rk = (RequiresKeyspace)ann;
      } else if ( ann instanceof RequiresColumnFamily ) {
        rcf = (RequiresColumnFamily)ann;
      }
    }
    if ( rk != null ) {
      maybeCreateKeyspace(rk, rcf);
    } else if ( rcf != null ) {
      maybeCreateColumnFamily(rcf);
    }

    super.run(notifier);
  }

  
  private void maybeCreateKeyspace(RequiresKeyspace rk, RequiresColumnFamily rcf) {
    logger.debug("RequiresKeyspace annotation has keyspace name: {}", rk.ksName());
    List<CFMetaData> cfs = extractColumnFamily(rcf);
    try {
      MigrationManager
              .announceNewKeyspace(KSMetaData.newKeyspace(rk.ksName(),
                      rk.strategy(), KSMetaData.optsWithRF(rk.replication()), false, cfs));
    } catch (AlreadyExistsException aee) {
      logger.info("using existing Keyspace for " + rk.ksName());
      if ( cfs.size() > 0 ) {
        maybeTruncateSafely(rcf);
      }
    } catch (Exception ex) {
      throw new RuntimeException("Failure creating keyspace for " + rk.ksName(),ex);
    }
  }

  private List<CFMetaData> extractColumnFamily(RequiresColumnFamily rcf) {
    logger.debug("RequiresColumnFamily  has name: {} for ks: {}", rcf.cfName(), rcf.ksName());
    List<CFMetaData> cfms = new ArrayList<CFMetaData>();
    if ( rcf != null ) {
      try {
        cfms.add(new CFMetaData(rcf.ksName(), rcf.cfName(),
                ColumnFamilyType.Standard, TypeParser.parse(rcf.comparator()), null));

      } catch (Exception ex) {
        throw new RuntimeException("unable to create column family for: " + rcf.cfName(), ex);
      }
    }
    return cfms;
  }

  private void maybeCreateColumnFamily(RequiresColumnFamily rcf) {
    try {
      CFMetaData cfMetaData;
      if ( rcf.isCounter() ) {
        cfMetaData = new CFMetaData(rcf.ksName(), rcf.cfName(),
                      ColumnFamilyType.Standard, TypeParser.parse(rcf.comparator()), null)
                .replicateOnWrite(false).defaultValidator(CounterColumnType.instance);
      } else {
        cfMetaData = new CFMetaData(rcf.ksName(), rcf.cfName(),
                      ColumnFamilyType.Standard, TypeParser.parse(rcf.comparator()), null);
      }
      MigrationManager.announceNewColumnFamily(cfMetaData);
    } catch(AlreadyExistsException aee) {
      logger.info("CF already exists for " + rcf.cfName());
      maybeTruncateSafely(rcf);
    } catch (Exception ex) {
      throw new RuntimeException("Could not create CF for: " + rcf.cfName(), ex);
    }
  }

  private void maybeTruncateSafely(RequiresColumnFamily rcf) {
    if ( rcf != null && rcf.truncateExisting() ) {
      try {
        StorageProxy.truncateBlocking(rcf.ksName(), rcf.cfName());
      } catch (Exception ex) {
        throw new RuntimeException("Could not truncate column family: " + rcf.cfName(),ex);
      }
    }
  }

  private void startCassandra() {
    if ( staashDaemon != null ) {
      return;
    }
    deleteRecursive(new File("/tmp/staash_cache"));
    deleteRecursive(new File ("/tmp/staash_data"));
    deleteRecursive(new File ("/tmp/staash_log"));
    System.setProperty("cassandra-foreground", "true");
    System.setProperty("log4j.defaultInitOverride","true");
    System.setProperty("log4j.configuration", "log4j.properties");
    System.setProperty("cassandra.ring_delay_ms","1000");
    System.setProperty("cassandra.start_rpc","true"); 
    System.setProperty("cassandra.start_native_transport","true"); 

    executor.execute(new Runnable() {
      public void run() {
          staashDaemon = new StaashDeamon();
          staashDaemon.activate();
      }
    });
    try {
      TimeUnit.SECONDS.sleep(3);
    }
    catch (InterruptedException e) {
      throw new AssertionError(e);
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          logger.error("In shutdownHook");
          stopCassandra();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

  private void stopCassandra() throws Exception {
    if (staashDaemon != null) {
        staashDaemon.deactivate();
      StorageService.instance.stopClient();

    }
    executor.shutdown();
    executor.shutdownNow();
  }

  private static boolean deleteRecursive(File path) {
    if (!path.exists())
      return false;
    boolean ret = true;
    if (path.isDirectory()){
      for (File f : path.listFiles()){
        ret = ret && deleteRecursive(f);
      }
    }
    return ret && path.delete();
  }
}
