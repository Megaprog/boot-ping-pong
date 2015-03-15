package ppc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Client extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final int attempts;
    private final List<String> userIds;
    private final Map<String, AtomicLong> pongs;
    private final URL url;

    public Client(int number, int attempts, List<String> userIds, Map<String, AtomicLong> pongs, String url) {
        super("Client" + number);
        this.attempts = attempts;
        this.userIds = userIds;
        this.pongs = pongs;
        try {
            this.url = new URL(url);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL=" + url, e);
        }
    }

    @Override
    public void run() {
        for (int i = 1; i <= attempts; i++) {
            final String userId = userIds.get(ThreadLocalRandom.current().nextInt(userIds.size()));
            final Map<String, String> requestJson = new HashMap<>();
            requestJson.put("command", "PING");
            requestJson.put("userId", userId);
            final byte[] body;
            try {
                body = OBJECT_MAPPER.writeValueAsBytes(requestJson);
            }
            catch (JsonProcessingException e) {
                log.error("Can't parsing " + requestJson, e);
                return;
            }


            HttpURLConnection connection = null;
            try {
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length", String.valueOf(body.length));
                }
                catch (IOException e) {
                    log.error("Exception during opening connection", e);
                    return;
                }

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body);
                }
                catch (IOException e) {
                    log.error("Exception during request body writing", e);
                    return;
                }

                final int responseStatus;
                try {
                    responseStatus = connection.getResponseCode();
                }
                catch (IOException e) {
                    log.error("Exception during response status reading", e);
                    return;
                }

                if (responseStatus == 200) {
                    try (InputStream inputStream = connection.getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        int value;
                        while ((value = inputStream.read()) != -1) {
                            outputStream.write(value);
                        }

                        final Map<String, Object> responseJson = new ObjectMapper().readValue(outputStream.toByteArray(),
                                new TypeReference<HashMap<String, Object>>(){});

                        final AtomicLong pong = pongs.get(userId);
                        final long pongValue = pong.incrementAndGet();

                        log.debug("Pong on client {} on server {}", pongValue, responseJson.get("pong"));
                    }
                    catch (IOException e) {
                        log.error("Exception during response body reading", e);
                        return;
                    }
                }
                else {
                    log.warn("Server response status=" + responseStatus);
                }
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

}
