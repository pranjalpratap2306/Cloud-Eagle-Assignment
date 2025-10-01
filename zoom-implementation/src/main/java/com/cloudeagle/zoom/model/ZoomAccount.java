package com.cloudeagle.zoom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model class representing Zoom Account Information
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoomAccount {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("account_name")
    private String accountName;
    
    @JsonProperty("account_alias")
    private String accountAlias;
    
    @JsonProperty("account_support_name")
    private String accountSupportName;
    
    @JsonProperty("account_support_email")
    private String accountSupportEmail;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("options")
    private AccountOptions options;
    
    // Default constructor
    public ZoomAccount() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public String getAccountAlias() {
        return accountAlias;
    }
    
    public void setAccountAlias(String accountAlias) {
        this.accountAlias = accountAlias;
    }
    
    public String getAccountSupportName() {
        return accountSupportName;
    }
    
    public void setAccountSupportName(String accountSupportName) {
        this.accountSupportName = accountSupportName;
    }
    
    public String getAccountSupportEmail() {
        return accountSupportEmail;
    }
    
    public void setAccountSupportEmail(String accountSupportEmail) {
        this.accountSupportEmail = accountSupportEmail;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public AccountOptions getOptions() {
        return options;
    }
    
    public void setOptions(AccountOptions options) {
        this.options = options;
    }
    
    @Override
    public String toString() {
        return "ZoomAccount{" +
                "id='" + id + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountAlias='" + accountAlias + '\'' +
                ", accountSupportName='" + accountSupportName + '\'' +
                ", accountSupportEmail='" + accountSupportEmail + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", options=" + options +
                '}';
    }
    
    /**
     * Nested class for account options
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccountOptions {
        
        @JsonProperty("share_rc")
        private Boolean shareRc;
        
        @JsonProperty("room_connector_token")
        private String roomConnectorToken;
        
        @JsonProperty("share_mc")
        private Boolean shareMc;
        
        @JsonProperty("meeting_connector_token")
        private String meetingConnectorToken;
        
        @JsonProperty("pay_mode")
        private String payMode;

        public AccountOptions() {}

        public Boolean getShareRc() {
            return shareRc;
        }
        
        public void setShareRc(Boolean shareRc) {
            this.shareRc = shareRc;
        }
        
        public String getRoomConnectorToken() {
            return roomConnectorToken;
        }
        
        public void setRoomConnectorToken(String roomConnectorToken) {
            this.roomConnectorToken = roomConnectorToken;
        }
        
        public Boolean getShareMc() {
            return shareMc;
        }
        
        public void setShareMc(Boolean shareMc) {
            this.shareMc = shareMc;
        }
        
        public String getMeetingConnectorToken() {
            return meetingConnectorToken;
        }
        
        public void setMeetingConnectorToken(String meetingConnectorToken) {
            this.meetingConnectorToken = meetingConnectorToken;
        }
        
        public String getPayMode() {
            return payMode;
        }
        
        public void setPayMode(String payMode) {
            this.payMode = payMode;
        }
        
        @Override
        public String toString() {
            return "AccountOptions{" +
                    "shareRc=" + shareRc +
                    ", roomConnectorToken='" + roomConnectorToken + '\'' +
                    ", shareMc=" + shareMc +
                    ", meetingConnectorToken='" + meetingConnectorToken + '\'' +
                    ", payMode='" + payMode + '\'' +
                    '}';
        }
    }
}
