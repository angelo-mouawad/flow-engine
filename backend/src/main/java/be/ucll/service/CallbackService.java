package be.ucll.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CallbackService {

    public static final String PENDING = "pending";
    public static final String COMPLETED = "completed";
    public static final String FAILED = "failed";

    private static final Logger log = LoggerFactory.getLogger(CallbackService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Two constructors, so Spring needs to be told which one to use. The package-private one is for tests.
    @Autowired
    public CallbackService(ObjectMapper objectMapper) {
        this(HttpClient.newBuilder().connectTimeout(TIMEOUT).build(), objectMapper);
    }

    CallbackService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public void send(String callbackUrl, String flowId, String status) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            return;
        }

        try {
            URI uri = URI.create(callbackUrl.trim());
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                log.warn("Skipping callback for flow {}: {} is not an http(s) URL", flowId, callbackUrl);
                return;
            }

            String body = objectMapper.writeValueAsString(Map.of("flowId", flowId, "status", status));
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .whenComplete((response, error) -> {
                        if (error != null) {
                            log.warn("Callback to {} for flow {} failed: {}", callbackUrl, flowId, error.getMessage());
                        } else if (response.statusCode() >= 400) {
                            log.warn("Callback to {} for flow {} returned {}", callbackUrl, flowId, response.statusCode());
                        }
                    });
        } catch (Exception e) {
            log.warn("Could not send callback to {} for flow {}: {}", callbackUrl, flowId, e.getMessage());
        }
    }
}
