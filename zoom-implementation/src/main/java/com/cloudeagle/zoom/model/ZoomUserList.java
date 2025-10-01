package com.cloudeagle.zoom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Model class representing Zoom User List Response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoomUserList {
    
    @JsonProperty("page_count")
    private Integer pageCount;
    
    @JsonProperty("page_number")
    private Integer pageNumber;
    
    @JsonProperty("page_size")
    private Integer pageSize;
    
    @JsonProperty("total_records")
    private Integer totalRecords;
    
    @JsonProperty("users")
    private List<ZoomUser> users;
    
    // Default constructor
    public ZoomUserList() {}
    
    // Getters and Setters
    public Integer getPageCount() {
        return pageCount;
    }
    
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    
    public Integer getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Integer getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public List<ZoomUser> getUsers() {
        return users;
    }
    
    public void setUsers(List<ZoomUser> users) {
        this.users = users;
    }
    
    @Override
    public String toString() {
        return "ZoomUserList{" +
                "pageCount=" + pageCount +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalRecords=" + totalRecords +
                ", users=" + users +
                '}';
    }
    
    /**
     * Nested class representing individual Zoom User
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZoomUser {
        
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("first_name")
        private String firstName;
        
        @JsonProperty("last_name")
        private String lastName;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("type")
        private Integer type;
        
        @JsonProperty("role_name")
        private String roleName;
        
        @JsonProperty("pmi")
        private Long pmi;
        
        @JsonProperty("use_pmi")
        private Boolean usePmi;
        
        @JsonProperty("personal_meeting_url")
        private String personalMeetingUrl;
        
        @JsonProperty("timezone")
        private String timezone;
        
        @JsonProperty("verified")
        private Integer verified;
        
        @JsonProperty("dept")
        private String dept;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("last_login_time")
        private String lastLoginTime;
        
        @JsonProperty("last_client_version")
        private String lastClientVersion;
        
        @JsonProperty("language")
        private String language;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("role_id")
        private String roleId;
        
        // Default constructor
        public ZoomUser() {}
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public Integer getType() {
            return type;
        }
        
        public void setType(Integer type) {
            this.type = type;
        }
        
        public String getRoleName() {
            return roleName;
        }
        
        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
        
        public Long getPmi() {
            return pmi;
        }
        
        public void setPmi(Long pmi) {
            this.pmi = pmi;
        }
        
        public Boolean getUsePmi() {
            return usePmi;
        }
        
        public void setUsePmi(Boolean usePmi) {
            this.usePmi = usePmi;
        }
        
        public String getPersonalMeetingUrl() {
            return personalMeetingUrl;
        }
        
        public void setPersonalMeetingUrl(String personalMeetingUrl) {
            this.personalMeetingUrl = personalMeetingUrl;
        }
        
        public String getTimezone() {
            return timezone;
        }
        
        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
        
        public Integer getVerified() {
            return verified;
        }
        
        public void setVerified(Integer verified) {
            this.verified = verified;
        }
        
        public String getDept() {
            return dept;
        }
        
        public void setDept(String dept) {
            this.dept = dept;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
        
        public String getLastLoginTime() {
            return lastLoginTime;
        }
        
        public void setLastLoginTime(String lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }
        
        public String getLastClientVersion() {
            return lastClientVersion;
        }
        
        public void setLastClientVersion(String lastClientVersion) {
            this.lastClientVersion = lastClientVersion;
        }
        
        public String getLanguage() {
            return language;
        }
        
        public void setLanguage(String language) {
            this.language = language;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getRoleId() {
            return roleId;
        }
        
        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }
        
        public String getFullName() {
            return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        }
        
        public String getUserTypeDescription() {
            if (type == null) return "Unknown";
            switch (type) {
                case 1: return "Basic";
                case 2: return "Licensed";
                case 3: return "On-Prem";
                default: return "Unknown (" + type + ")";
            }
        }
        
        @Override
        public String toString() {
            return "ZoomUser{" +
                    "id='" + id + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", type=" + type +
                    ", roleName='" + roleName + '\'' +
                    ", dept='" + dept + '\'' +
                    ", status='" + status + '\'' +
                    ", lastLoginTime='" + lastLoginTime + '\'' +
                    '}';
        }
    }
}
