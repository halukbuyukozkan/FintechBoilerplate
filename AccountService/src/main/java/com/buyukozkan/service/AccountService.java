package com.buyukozkan.service;

import com.buyukozkan.dto.request.LoginRequest;
import com.buyukozkan.dto.request.RegisterRequest;
import com.buyukozkan.dto.response.LoginResponse;
import com.buyukozkan.dto.response.KeycloakPasswordGrantResponse;
import com.buyukozkan.model.Account;
import com.buyukozkan.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final KeycloakService keycloakService;

    public AccountService(AccountRepository accountRepository, KeycloakService keycloakService) {
        this.accountRepository = accountRepository;
        this.keycloakService = keycloakService;
    }

    public LoginResponse register(RegisterRequest request){

        if (accountRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        Account account = Account.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userName(request.getUsername())
                .address(request.getAddress())
                .createAt(System.currentTimeMillis())
                .updateAt(System.currentTimeMillis())
                .state(true)
                .build();

        Account savedAccount = accountRepository.save(account);

        return LoginResponse.builder()
                .userId(savedAccount.getId())
                .email(savedAccount.getEmail())
                .message("Account registered successfully")
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + request.getEmail()));

        if (!account.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String primary = (account.getUserName() != null && !account.getUserName().isBlank())
                ? account.getUserName()
                : account.getEmail();

        KeycloakPasswordGrantResponse token = obtainKeycloakToken(primary, account.getEmail(), request.getPassword());

        return LoginResponse.builder()
                .userId(account.getId())
                .email(account.getEmail())
                .message("Login successful")
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresIn(token.getExpiresIn())
                .tokenType(token.getTokenType() != null ? token.getTokenType() : "Bearer")
                .build();

    }

    private KeycloakPasswordGrantResponse obtainKeycloakToken(String primaryUsername, String email, String password) {
        boolean willRetryWithEmail = email != null && !email.equals(primaryUsername);

        try {
            return keycloakService.obtainPasswordGrantToken(primaryUsername, password, !willRetryWithEmail);
        } catch (Exception first) {
            if (willRetryWithEmail) {
                try {
                    return keycloakService.obtainPasswordGrantToken(email, password, true);
                } catch (Exception second) {
                    log.debug("Keycloak token retry with email also failed: {}", second.getMessage());
                }
            }
            log.error("Keycloak token after DB login failed for {}: {}", primaryUsername, first.getMessage());
            throw new RuntimeException("Login ok but SSO token failed: " + first.getMessage(), first);
        }
    }
}
