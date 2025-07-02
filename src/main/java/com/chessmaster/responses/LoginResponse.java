package com.chessmaster.responses;

import lombok.Getter;

@Getter
public class LoginResponse {

    private String token;

    private long expiresIn;

    private String fullName;

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public LoginResponse setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ",expiresIn=" + expiresIn +
                '}';
    }
}