package be.ucll.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import be.ucll.exception.ServiceException;

@Service
public class TriggerService {

    private final WebClient webClient;

    public TriggerService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void SendCallback(String url, String id){

        try {

            String statusMessage = id.isBlank()?"completed":"pending";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("StatusMessage", statusMessage);
            requestBody.put("FlowId", id);

            String response = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        } catch (WebClientException e) {
            throw new ServiceException("Service unavailable at " + url + ": " + e.getMessage());
        } catch (ServiceException e) {
            throw new ServiceException("Unexpected Error: " + e.getMessage());
        }
    }
}
