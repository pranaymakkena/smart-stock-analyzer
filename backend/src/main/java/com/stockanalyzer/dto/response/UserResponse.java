package com.stockanalyzer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private BigDecimal virtualBalance;
    private BigDecimal totalInvested;
    private BigDecimal totalProfit;
    private String profilePicture;
    private LocalDateTime createdAt;

    private UserResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getVirtualBalance() { return virtualBalance; }
    public void setVirtualBalance(BigDecimal virtualBalance) { this.virtualBalance = virtualBalance; }

    public BigDecimal getTotalInvested() { return totalInvested; }
    public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserResponse instance = new UserResponse();
        public Builder id(Long v) { instance.id = v; return this; }
        public Builder firstName(String v) { instance.firstName = v; return this; }
        public Builder lastName(String v) { instance.lastName = v; return this; }
        public Builder email(String v) { instance.email = v; return this; }
        public Builder role(String v) { instance.role = v; return this; }
        public Builder virtualBalance(BigDecimal v) { instance.virtualBalance = v; return this; }
        public Builder totalInvested(BigDecimal v) { instance.totalInvested = v; return this; }
        public Builder totalProfit(BigDecimal v) { instance.totalProfit = v; return this; }
        public Builder profilePicture(String v) { instance.profilePicture = v; return this; }
        public Builder createdAt(LocalDateTime v) { instance.createdAt = v; return this; }
        public UserResponse build() { return instance; }
    }
}
