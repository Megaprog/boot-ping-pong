package pp;

import com.datastax.driver.core.Cluster;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.jmmo.sc.Cassandra;
import org.jmmo.sc.EntityInfo;
import org.jmmo.sc.EntityPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pp.model.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Autowired
    public Cassandra cassandra(@Value("${cassandra.embedded:true}") boolean embedded,
                               @Value("#{'${cassandra.hosts:localhost}'.split(',')}") String[] hosts,
                               @Value("${cassandra.keyspace:ping_pong}")String keyspace,
                               @Value("${cassandra.truncate:false}") boolean truncate) throws IOException {

        if (embedded) {
            new EmbeddedCassandraService().start();
        }

        final EntityPool entityPool = new EntityPool();
        final Cassandra cassandra = new Cassandra(Cluster.builder().addContactPoints(hosts).build().connect(), entityPool);

        cassandra.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace + " WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");
        cassandra.execute("USE " + keyspace);

        final EntityInfo<User> entityInfo = entityPool.entityInfo(User.class);
        cassandra.execute("CREATE TABLE IF NOT EXISTS " + entityInfo.table() + " (\n" +
                "  id text PRIMARY KEY,\n" +
                "  pings counter,\n" +
                ")");

        if (truncate) {
            cassandra.execute("truncate " + entityInfo.table());
        }

        return cassandra;
    }
}
