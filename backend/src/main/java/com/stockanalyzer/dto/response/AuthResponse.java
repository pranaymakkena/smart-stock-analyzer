package com.stockanalyzer.dto.response;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UserResponse user;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String tokenType, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse instance = new AuthResponse();
        public Builder accessToken(String v) { instance.accessToken = v; return this; }
        public Builder refreshToken(String v) { instance.refreshToken = v; return this; }
        public Builder tokenType(String v) { instance.tokenType = v; return this; }
        public Builder user(UserResponse v) { instance.user = v; return this; }
        public AuthResponse build() { return instance; }
    }
}
