package com.github.deenr.contribu.service.impl;

import com.github.deenr.contribu.enums.GitPlatform;
import com.github.deenr.contribu.enums.TokenStatus;
import com.github.deenr.contribu.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class GithubOAuthServiceImpl implements OAuthService {
    @Value("${client.url}")
    private String clientUrl;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    @Value("${github.token-url}")
    private String accessTokenUrl;

    @Value("${github.auth-url}")
    private String authUrl;

    private final UserServiceImpl userService;
    private final GitProviderServiceImpl gitProviderService;

    @Autowired
    GithubOAuthServiceImpl(UserServiceImpl userService, GitProviderServiceImpl gitProviderService) {
        this.userService = userService;
        this.gitProviderService = gitProviderService;
    }

    @Override
    public String getAuthorizationUrl() {
        return String.format(
                "%s?client_id=%s&redirect_uri=%s",
                authUrl,
                clientId,
                String.format("%s?provider=%s", redirectUri, "github")
        );
    }

    @Override
    public HttpStatusCode fetchAndSetToken(String code, String email) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("client_id", clientId);
            body.put("client_secret", clientSecret);
            body.put("redirect_uri", String.format("%s?provider=%s", redirectUri, "github"));
            body.put("code", code);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUrl, request, Map.class);

            System.err.println(response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String accessToken = response.getBody().get("access_token").toString();

                gitProviderService.save(email, GitPlatform.GITHUB, accessToken);

                return response.getStatusCode();
            }


            throw new RuntimeException("Unexpected response from GitHub: " + response.getStatusCode());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to retrieve access token: " + ex.getMessage(), ex);
        }
    }

    @Override
    public ResponseEntity<Object> handleCallback(HttpStatusCode statusCode) {
        return ResponseEntity.status(statusCode)
                .header("Location", "https://google.com")
                .build();
    }

    @Override
    public void removeToken(String email) {
        gitProviderService.delete(email, GitPlatform.GITHUB);
    }

    @Override
    public TokenStatus getTokenStatus(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            String validateTokenUrl = "https://api.github.com/user";

            ResponseEntity<Map> response = restTemplate.exchange(
                    validateTokenUrl,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return TokenStatus.VALID;
            }
        } catch (Exception ex) {
            return TokenStatus.NOT_AUTHORIZED;
        }

        return TokenStatus.EXPIRED;
    }
}
