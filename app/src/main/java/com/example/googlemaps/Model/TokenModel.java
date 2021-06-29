package com.example.googlemaps.Model;

public class TokenModel {
    private String Token;

    public TokenModel(String token) {
        Token = token;
    }

    public TokenModel() {
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
