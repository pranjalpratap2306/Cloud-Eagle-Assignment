# Zoom API Integration

This Java application demonstrates integration with Zoom APIs for the CloudEagle Assessment. It implements OAuth2 authentication and showcases fetching account information, user lists, plan details, and activity reports.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Zoom account with admin privileges
- Zoom OAuth app (created in Zoom App Marketplace)

## Setup Instructions

### 1. Create Zoom OAuth App

1. Go to [Zoom App Marketplace](https://marketplace.zoom.us/)
2. Sign in with your Zoom account
3. Click "Develop" → "Build App"
4. Select "OAuth" app type
5. Fill in app details and create the app
6. In app settings, note down:
   - **Client ID**
   - **Client Secret**
7. Add redirect URI: `https://oauth.pstmn.io/v1/callback`
8. In the "Scopes" section, add these permissions:
   - `account:read:admin` - Access account information
   - `user:read:admin` - Access user information


### 2. Set Environment Variables

Set the following environment variables:

```bash
export ZOOM_CLIENT_ID=
export ZOOM_CLIENT_SECRET=

```

### 3. Build and Run

```bash
# Navigate to the project directory
cd zoom-implementation

# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="com.cloudeagle.zoom.ZoomApiDemo"
```


## API Endpoints Demonstrated

1. **OAuth2 Token Exchange**: `POST https://zoom.us/oauth/token`
2**Users List**: `GET https://api.zoom.us/v2/users`

For Testing screenshots please refer to CloudEagle_Zoom_Assessment.md

## Error Handling
The application includes comprehensive error handling for:
- Missing environment variables
- OAuth authentication failures
- API request failures
- JSON parsing errors
- Network connectivity issues
- Rate limiting responses