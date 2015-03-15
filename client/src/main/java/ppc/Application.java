package ppc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@EnableAutoConfiguration
public class Application implements CommandLineRunner {

    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {
        SpringApplication.run(Application.class, args);
    }

    @Value("${url:http://localhost/handler}")
    String url;

    @Value("${attempts:10}")
    int attempts;

    @Value("${threads:1}")
    int threads;

    @Value("${users:1}")
    int users;

    @Override
    public void run(String... args) throws Exception {
        final List<String> userIds = new ArrayList<>(users);
        final Map<String, AtomicLong> pongs = new HashMap<>(users);
        for (int i = 1; i <= users; i++) {
            final String userId = UUID.randomUUID().toString();
            userIds.add(userId);
            pongs.put(userId, new AtomicLong());
        }

        final Instant begin = Instant.now();
        System.out.println();
        System.out.println("Connecting to url: " + url);
        System.out.printf("Started at %s: attempts %d, threads %d, users %d %n", begin, attempts, threads, users);
        System.out.println();

        final List<Client> clients = IntStream.range(0, threads).mapToObj(number -> new Client(number, attempts, userIds, pongs, url)).map(client -> {
            client.start();
            return client;
        }).collect(Collectors.toList());

        for (Client client : clients) {
            client.join();
        }

        final Instant end = Instant.now();
        final Duration duration = Duration.between(begin, end);
        System.out.println();
        System.out.printf("Ended at %s: duration %s %n", end, duration);
        System.out.println("Results:");
        System.out.println();

        pongs.forEach((s, atomicLong) -> System.out.printf("user: id=%s, pongs=%d %n", s, atomicLong.get()));
        System.out.println();
    }
}
