package com.sgt.sgt_api.entity;

import java.time.LocalDateTime;

public class Login {

    private String email;
    private String token;
    private LocalDateTime dataHoraLogin;

    public Login(String email, String token) {
        this.email = email;
        this.token = token;
        this.dataHoraLogin = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public String getToken() { return token; }
    public LocalDateTime getDataHoraLogin() { return dataHoraLogin; }
}