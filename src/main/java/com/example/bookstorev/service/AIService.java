package com.example.bookstorev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIService {

    @Value("${google.generativeai.api.key}") // Clé API Gemini
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public String getRecommendations(String bookTitle) {
        RestTemplate restTemplate = new RestTemplate();

        // Construire le corps de la requête
        String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"Recommande 3 livres similaires à '%s'. Justifie brièvement chaque recommandation.\"}]}]}",
                bookTitle
        );

        // Envoyer la requête à l'API Gemini
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = GEMINI_URL + "?key=" + apiKey;
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(url, request, String.class);

        // Extraire la réponse
        return extractRecommendations(response);
    }

    private String extractRecommendations(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'extraction des recommandations.";
        }
    }
}