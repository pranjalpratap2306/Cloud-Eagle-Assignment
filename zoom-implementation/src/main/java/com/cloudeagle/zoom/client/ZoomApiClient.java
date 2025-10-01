package com.cloudeagle.zoom.client;

import com.cloudeagle.zoom.model.OAuth2TokenResponse;
import com.cloudeagle.zoom.model.ZoomAccount;
import com.cloudeagle.zoom.model.ZoomUserList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Client for interacting with Zoom APIs
 */
public class ZoomApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ZoomApiClient.class);
    
    // Zoom API endpoints
    private static final String OAUTH_AUTHORIZE_URL = "https://zoom.us/oauth/authorize";
    private static final String OAUTH_TOKEN_URL = "https://zoom.us/oauth/token";
    private static final String API_BASE_URL = "https://api.zoom.us/v2";
    private static final String ACCOUNT_INFO_URL = API_BASE_URL + "/accounts/me";
    private static final String USERS_LIST_URL = API_BASE_URL + "/users";
    private static final String USER_ME_URL = API_BASE_URL + "/users/me";
    private static final String ACCOUNT_PLANS_URL = API_BASE_URL + "/accounts/me/plans";
    private static final String ACTIVITIES_URL = API_BASE_URL + "/report/activities";
    private static final String MEETINGS_URL = API_BASE_URL + "/users/me/meetings";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    
    public ZoomApiClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate the authorization URL for OAuth2 flow
     * 
     * @param redirectUri The redirect URI registered with your Zoom app
     * @param state Optional state parameter for security
     * @return Authorization URL
     */
    public String getAuthorizationUrl(String redirectUri, String state) {
        StringBuilder url = new StringBuilder(OAUTH_AUTHORIZE_URL);
        url.append("?response_type=code");
        url.append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8));
        url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
        
        if (state != null && !state.isEmpty()) {
            url.append("&state=").append(URLEncoder.encode(state, StandardCharsets.UTF_8));
        }
        
        return url.toString();
    }
    
    /**
     * Exchange authorization code for access token
     * 
     * @param authorizationCode The authorization code received from the callback
     * @param redirectUri The same redirect URI used in authorization
     * @return OAuth2TokenResponse containing access token and other details
     * @throws IOException If the HTTP request fails
     * @throws InterruptedException If the request is interrupted
     */
    public OAuth2TokenResponse exchangeCodeForToken(String authorizationCode, String redirectUri) 
            throws IOException, InterruptedException {
        
        String requestBody = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(authorizationCode, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        
        // Create Basic Auth header
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OAUTH_TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        logger.info("Exchanging authorization code for access token");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            OAuth2TokenResponse tokenResponse = objectMapper.readValue(response.body(), OAuth2TokenResponse.class);
            this.accessToken = tokenResponse.getAccessToken();
            logger.info("Successfully obtained access token");
            return tokenResponse;
        } else {
            logger.error("Failed to obtain access token. Status: {}, Response: {}", 
                    response.statusCode(), response.body());
            throw new IOException("Failed to obtain access token: " + response.body());
        }
    }
    
    /**
     * Set the access token directly (useful when you already have a valid token)
     * 
     * @param accessToken The access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    /**
     * Get account information including account name and details
     * 
     * @return ZoomAccount object containing account details
     * @throws IOException If the HTTP request fails
     * @throws InterruptedException If the request is interrupted
     * @throws IllegalStateException If access token is not set
     */
    public ZoomAccount getAccountInfo() throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token not set. Please authenticate first.");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ACCOUNT_INFO_URL))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        
        logger.info("Fetching account information");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            ZoomAccount account = objectMapper.readValue(response.body(), ZoomAccount.class);
            logger.info("Successfully retrieved account information for: {}", account.getAccountName());
            return account;
        } else {
            logger.error("Failed to fetch account info. Status: {}, Response: {}", 
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch account info: " + response.body());
        }
    }
    
    /**
     * Get list of users in the organization
     * 
     * @param status Filter by user status (active, inactive, pending)
     * @param pageSize Number of records per page (max 300)
     * @return ZoomUserList object containing user details
     * @throws IOException If the HTTP request fails
     * @throws InterruptedException If the request is interrupted
     * @throws IllegalStateException If access token is not set
     */
    public ZoomUserList getUsers(String status, Integer pageSize) throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token not set. Please authenticate first.");
        }
        
        StringBuilder url = new StringBuilder(USERS_LIST_URL);
        url.append("?status=").append(status != null ? status : "active");
        if (pageSize != null) {
            url.append("&page_size=").append(pageSize);
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        
        logger.info("Fetching users list");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            ZoomUserList userList = objectMapper.readValue(response.body(), ZoomUserList.class);
            logger.info("Successfully retrieved {} users", userList.getTotalRecords());
            return userList;
        } else {
            logger.error("Failed to fetch users list. Status: {}, Response: {}", 
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch users list: " + response.body());
        }
    }
    
    /**
     * Get account plans information
     * 
     * @return Raw JSON response containing plan details
     * @throws IOException If the HTTP request fails
     * @throws InterruptedException If the request is interrupted
     * @throws IllegalStateException If access token is not set
     */
    public String getAccountPlans() throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token not set. Please authenticate first.");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ACCOUNT_PLANS_URL))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        
        logger.info("Fetching account plans");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            logger.info("Successfully retrieved account plans");
            return response.body();
        } else {
            logger.error("Failed to fetch account plans. Status: {}, Response: {}", 
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch account plans: " + response.body());
        }
    }
    
    /**
     * Get activity reports (sign-in events)
     * 
     * @param fromDate Start date (YYYY-MM-DD format)
     * @param toDate End date (YYYY-MM-DD format)
     * @return Raw JSON response containing activity data
     * @throws IOException If the HTTP request fails
     * @throws InterruptedException If the request is interrupted
     * @throws IllegalStateException If access token is not set
     */
    public String getActivityReports(String fromDate, String toDate) throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token not set. Please authenticate first.");
        }
        
        StringBuilder url = new StringBuilder(ACTIVITIES_URL);
        url.append("?from=").append(fromDate);
        url.append("&to=").append(toDate);
        url.append("&page_size=100");
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        
        logger.info("Fetching activity reports from {} to {}", fromDate, toDate);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            logger.info("Successfully retrieved activity reports");
            return response.body();
        } else {
            logger.error("Failed to fetch activity reports. Status: {}, Response: {}", 
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch activity reports: " + response.body());
        }
    }
    
    /**
     * Get the current access token
     * 
     * @return The access token or null if not set
     */
    public String getAccessToken() {
        return accessToken;
    }
    
    /**
     * Get current user information (requires basic user scope)
     * 
     * @return Raw JSON response containing current user details
     * @throws IOException If the HTTP request fails
     * @throws InterruptedException If the request is interrupted
     * @throws IllegalStateException If access token is not set
     */
    public String getCurrentUser() throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token not set. Please authenticate first.");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_ME_URL))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        
        logger.info("Fetching current user information");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            logger.info("Successfully retrieved current user information");
            return response.body();
        } else {
            logger.error("Failed to fetch current user info. Status: {}, Response: {}", 
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch current user info: " + response.body());
        }
    }
    
    /**
     * Check if the client has a valid access token
     * 
     * @return true if access token is set, false otherwise
     */
    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isEmpty();
    }
}
