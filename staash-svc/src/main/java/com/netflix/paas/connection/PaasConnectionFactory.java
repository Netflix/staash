package com.netflix.paas.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.paas.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.model.CassandraStorage;
import com.netflix.paas.model.MySqlStorage;
import com.netflix.paas.service.CLIENTTYPE;

public class PaasConnectionFactory implements ConnectionFactory{
    private String clientType = "astyanax";
    private EurekaAstyanaxHostSupplier hs;
    public PaasConnectionFactory(String clientType, EurekaAstyanaxHostSupplier hs) {
        this.clientType = clientType;
        this.hs = hs;
    }
    public PaasConnection createConnection(JsonObject storageConf, String db)  {
        String type = storageConf.getString("type");
        if (type.equals("mysql")) {
            MySqlStorage mysqlStorageConf = new MySqlStorage(storageConf);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager
                        .getConnection(mysqlStorageConf.getJdbcurl(),mysqlStorageConf.getUser(), mysqlStorageConf.getPassword());
                return new MySqlConnection(connection);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            CassandraStorage cassStorage = new CassandraStorage(storageConf);
            if (clientType.equals(CLIENTTYPE.ASTYANAX.getType())) {
                return new AstyanaxCassandraConnection(cassStorage.getCluster(), db, hs);
            } else {
            Cluster cluster = Cluster.builder().withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy())).addContactPoint(cassStorage.getCluster()).build();
            return new CassandraConnection(cluster);
            }
        }
    }
}
