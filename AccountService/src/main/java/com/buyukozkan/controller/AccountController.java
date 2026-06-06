package com.buyukozkan.controller;

import com.buyukozkan.dto.request.LoginRequest;
import com.buyukozkan.dto.request.RegisterRequest;
import com.buyukozkan.dto.response.LoginResponse;
import com.buyukozkan.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/ping")
    public String ping() {
        return "Account Service is UP and running!";
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = accountService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = accountService.login(request);
        return ResponseEntity.ok(response);
    }
}
