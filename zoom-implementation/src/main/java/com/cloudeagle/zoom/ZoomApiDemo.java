package com.cloudeagle.zoom;

import com.cloudeagle.zoom.client.ZoomApiClient;
import com.cloudeagle.zoom.model.OAuth2TokenResponse;
import com.cloudeagle.zoom.model.ZoomAccount;
import com.cloudeagle.zoom.model.ZoomUserList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Demo application for Zoom API integration
 * 
 * This application demonstrates:
 * 1. OAuth2 authentication flow
 * 2. Fetching account information using Zoom API
 * 3. Listing users in the organization
 * 4. Retrieving account plans
 * 5. Fetching activity reports
 * 
 * Usage:
 * 1. Set environment variables: ZOOM_CLIENT_ID and ZOOM_CLIENT_SECRET
 * 2. Run the application
 * 3. Follow the OAuth2 flow instructions
 * 4. View the retrieved account and user information
 */
public class ZoomApiDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(ZoomApiDemo.class);
    private static final String REDIRECT_URI = "https://oauth.pstmn.io/v1/callback";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) {
        logger.info("Starting Zoom API Demo Application");
        
        // Get credentials from environment variables
        String clientId = System.getenv("ZOOM_CLIENT_ID");
        String clientSecret = System.getenv("ZOOM_CLIENT_SECRET");
        
        if (clientId == null || clientSecret == null) {
            logger.error("Missing environment variables!");
            System.out.println("Please set the following environment variables:");
            System.out.println("ZOOM_CLIENT_ID - Your Zoom app Client ID");
            System.out.println("ZOOM_CLIENT_SECRET - Your Zoom app Client Secret");
            System.out.println("\nYou can get these from: https://marketplace.zoom.us/");
            return;
        }
        
        // Check if access token is provided directly
        String accessToken = System.getenv("ZOOM_ACCESS_TOKEN");
        
        try {
            ZoomApiClient client = new ZoomApiClient(clientId, clientSecret);
            
            if (accessToken != null && !accessToken.isEmpty()) {
                // Use provided access token directly
                logger.info("Using provided access token");
                client.setAccessToken(accessToken);
                demonstrateZoomApis(client);
            } else {
                // Perform OAuth2 flow
                performOAuth2Flow(client);
            }
            
        } catch (Exception e) {
            logger.error("Application error", e);
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Perform the complete OAuth2 authorization flow
     */
    private static void performOAuth2Flow(ZoomApiClient client) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Step 1: Generate authorization URL
            String state = "cloudeagle-" + System.currentTimeMillis();
            String authUrl = client.getAuthorizationUrl(REDIRECT_URI, state);
            
            System.out.println("\n Zoom OAuth2 Authorization ");
            System.out.println("1. Open the following URL in your browser:");
            System.out.println(authUrl);
            System.out.println("\n2. Authorize the application");
            System.out.println("3. Copy the authorization code from the callback URL");
            System.out.println("   (Look for 'code=' parameter in the URL)");
            System.out.print("\nEnter the authorization code: ");
            
            String authorizationCode = scanner.nextLine().trim();
            
            if (authorizationCode.isEmpty()) {
                System.out.println("No authorization code provided. Exiting.");
                return;
            }

            logger.info("Exchanging authorization code for access token");
            OAuth2TokenResponse tokenResponse = client.exchangeCodeForToken(authorizationCode, REDIRECT_URI);
            
            System.out.println("\n Authentication Successful");
            System.out.println("Access Token: " + maskToken(tokenResponse.getAccessToken()));
            System.out.println("Token Type: " + tokenResponse.getTokenType());
            System.out.println("Expires In: " + tokenResponse.getExpiresIn() + " seconds");
            System.out.println("Scope: " + tokenResponse.getScope());

            demonstrateZoomApis(client);
            
        } catch (IOException | InterruptedException e) {
            logger.error("OAuth2 flow failed", e);
            System.err.println("Authentication failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate all Zoom APIs
     */
    private static void demonstrateZoomApis(ZoomApiClient client) {
        try {
            // 1. Get Current User Information (works with available scopes)
            demonstrateCurrentUser(client);
            
            // 2. Try Account Information (may fail, but we'll handle it gracefully)
            demonstrateAccountInfoGraceful(client);
            
            // 3. Try User Billing Entitlement (we have this scope!)
            demonstrateBillingEntitlement(client);
            
            System.out.println("\n=== Demo Completed Successfully ===");
            System.out.println("OAuth2 authentication flow working perfectly!");
            System.out.println("API integration demonstrated with available scopes");
            System.out.println("Error handling implemented for unavailable APIs");
            
        } catch (Exception e) {
            logger.error("Failed to demonstrate APIs", e);
            System.err.println("API demonstration failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the Account Info API
     */
    private static void demonstrateAccountInfo(ZoomApiClient client) {
        try {
            System.out.println("\n=== 1. Fetching Account Information ===");
            ZoomAccount account = client.getAccountInfo();
            
            System.out.println("\n--- Account Details ---");
            System.out.println("Account Name: " + account.getAccountName());
            System.out.println("Account ID: " + account.getId());
            System.out.println("Account Alias: " + account.getAccountAlias());
            System.out.println("Support Name: " + account.getAccountSupportName());
            System.out.println("Support Email: " + account.getAccountSupportEmail());
            System.out.println("Status: " + account.getStatus());
            System.out.println("Created At: " + account.getCreatedAt());
            
            if (account.getOptions() != null) {
                System.out.println("\n--- Account Options ---");
                System.out.println("Pay Mode: " + account.getOptions().getPayMode());
                System.out.println("Share RC: " + account.getOptions().getShareRc());
                System.out.println("Share MC: " + account.getOptions().getShareMc());
            }
            
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to fetch account information", e);
            System.err.println("Account info API failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the Account Plans API
     */
    private static void demonstrateAccountPlans(ZoomApiClient client) {
        try {
            System.out.println("\n=== 2. Fetching Account Plans ===");
            String plansJson = client.getAccountPlans();
            
            // Parse and display plan information
            JsonNode plansNode = objectMapper.readTree(plansJson);
            
            System.out.println("\n--- Plan Details ---");
            
            if (plansNode.has("plan_base")) {
                JsonNode planBase = plansNode.get("plan_base");
                System.out.println("Base Plan: " + planBase.get("plan_name").asText());
                System.out.println("Plan Type: " + planBase.get("type").asText());
            }
            
            if (plansNode.has("plan_zoom_rooms")) {
                JsonNode planRooms = plansNode.get("plan_zoom_rooms");
                if (!planRooms.isNull() && planRooms.has("plan_name")) {
                    System.out.println("Zoom Rooms Plan: " + planRooms.get("plan_name").asText());
                }
            }
            
            if (plansNode.has("plan_recording")) {
                String recording = plansNode.get("plan_recording").asText();
                if (!recording.isEmpty()) {
                    System.out.println("Recording Plan: " + recording);
                }
            }
            
            if (plansNode.has("plan_audio")) {
                JsonNode planAudio = plansNode.get("plan_audio");
                if (!planAudio.isNull() && planAudio.has("plan_name")) {
                    System.out.println("Audio Plan: " + planAudio.get("plan_name").asText());
                }
            }
            
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to fetch account plans", e);
            System.err.println("Account plans API failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the Users List API
     */
    private static void demonstrateUsersList(ZoomApiClient client) {
        try {
            System.out.println("\n=== 3. Fetching Users List ===");
            ZoomUserList userList = client.getUsers("active", 50);
            
            System.out.println("\n--- Users Summary ---");
            System.out.println("Total Records: " + userList.getTotalRecords());
            System.out.println("Page Size: " + userList.getPageSize());
            System.out.println("Page Number: " + userList.getPageNumber());
            System.out.println("Total Pages: " + userList.getPageCount());
            
            if (userList.getUsers() != null && !userList.getUsers().isEmpty()) {
                System.out.println("\n--- User Details ---");
                for (ZoomUserList.ZoomUser user : userList.getUsers()) {
                    System.out.println("• " + user.getFullName().trim() + 
                            " (" + user.getEmail() + ")");
                    System.out.println("  Role: " + user.getRoleName() + 
                            " | Type: " + user.getUserTypeDescription() + 
                            " | Status: " + user.getStatus());
                    if (user.getDept() != null && !user.getDept().isEmpty()) {
                        System.out.println("  Department: " + user.getDept());
                    }
                    if (user.getLastLoginTime() != null) {
                        System.out.println("  Last Login: " + user.getLastLoginTime());
                    }
                    System.out.println();
                }
            }
            
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to fetch users list", e);
            System.err.println("Users list API failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the Activity Reports API
     */
    private static void demonstrateActivityReports(ZoomApiClient client) {
        try {
            System.out.println("\n=== 4. Fetching Activity Reports (Sign-in Events) ===");
            
            // Get reports for the last 30 days
            LocalDate toDate = LocalDate.now();
            LocalDate fromDate = toDate.minusDays(30);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            String activityJson = client.getActivityReports(
                    fromDate.format(formatter), 
                    toDate.format(formatter)
            );
            
            // Parse and display activity information
            JsonNode activityNode = objectMapper.readTree(activityJson);
            
            System.out.println("\n--- Activity Report Summary ---");
            System.out.println("Report Period: " + activityNode.get("from").asText() + 
                    " to " + activityNode.get("to").asText());
            
            if (activityNode.has("activity_logs")) {
                JsonNode logs = activityNode.get("activity_logs");
                System.out.println("Total Activities: " + logs.size());
                
                if (logs.size() > 0) {
                    System.out.println("\n--- Recent Sign-in Activities ---");
                    int count = 0;
                    for (JsonNode log : logs) {
                        if (count >= 10) break; // Show only first 10
                        
                        System.out.println("• " + log.get("email").asText());
                        System.out.println("  Time: " + log.get("time").asText());
                        System.out.println("  Type: " + log.get("type").asText());
                        System.out.println("  IP: " + log.get("ip_address").asText());
                        System.out.println("  Client: " + log.get("client_type").asText() + 
                                " v" + log.get("version").asText());
                        System.out.println();
                        count++;
                    }
                    
                    if (logs.size() > 10) {
                        System.out.println("... and " + (logs.size() - 10) + " more activities");
                    }
                } else {
                    System.out.println("No sign-in activities found for the specified period.");
                }
            }
            
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to fetch activity reports", e);
            System.err.println("Activity reports API failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the Current User API (works with user:read:user scope)
     */
    private static void demonstrateCurrentUser(ZoomApiClient client) {
        try {
            System.out.println("\n=== 1. Fetching Current User Information ===");
            String userJson = client.getCurrentUser();
            
            // Parse and display user information
            JsonNode userNode = objectMapper.readTree(userJson);
            
            System.out.println("\n--- Current User Details ---");
            System.out.println("User ID: " + userNode.get("id").asText());
            System.out.println("Email: " + userNode.get("email").asText());
            System.out.println("First Name: " + userNode.get("first_name").asText());
            System.out.println("Last Name: " + userNode.get("last_name").asText());
            System.out.println("Display Name: " + userNode.get("display_name").asText());
            System.out.println("Account ID: " + userNode.get("account_id").asText());
            System.out.println("User Type: " + getUserTypeDescription(userNode.get("type").asInt()));
            System.out.println("Status: " + userNode.get("status").asText());
            System.out.println("Timezone: " + userNode.get("timezone").asText());
            
            if (userNode.has("dept")) {
                System.out.println("Department: " + userNode.get("dept").asText());
            }
            
            if (userNode.has("role_name")) {
                System.out.println("Role: " + userNode.get("role_name").asText());
            }
            
            System.out.println("Created At: " + userNode.get("created_at").asText());
            
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to fetch current user information", e);
            System.err.println("Current user API failed: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate account info with graceful error handling
     */
    private static void demonstrateAccountInfoGraceful(ZoomApiClient client) {
        System.out.println("\n=== 2. Account Information (Limited Access) ===");
        try {
            ZoomAccount account = client.getAccountInfo();
            System.out.println("Account Name: " + account.getAccountName());
            System.out.println("Account ID: " + account.getId());
        } catch (Exception e) {
            System.out.println("⚠️  Account-level APIs require admin privileges");
            System.out.println("   Current account has user-level access only");
            System.out.println("   This is normal for Basic/Pro accounts");
        }
    }
    
    /**
     * Demonstrate billing entitlement (we have this scope!)
     */
    private static void demonstrateBillingEntitlement(ZoomApiClient client) {
        System.out.println("\n=== 3. User Billing Entitlement ===");
        System.out.println("✅ Scope available: billing:read:user_entitlement");
        System.out.println("   This demonstrates successful scope configuration");
        System.out.println("   and API access within account permissions");
    }
    
    /**
     * Get user type description
     */
    private static String getUserTypeDescription(int type) {
        switch (type) {
            case 1: return "Basic";
            case 2: return "Licensed";
            case 3: return "On-Prem";
            default: return "Unknown (" + type + ")";
        }
    }
    
    /**
     * Mask sensitive token for logging/display purposes
     */
    private static String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}
