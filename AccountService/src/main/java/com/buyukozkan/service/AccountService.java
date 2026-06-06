package com.buyukozkan.service;

import com.buyukozkan.dto.request.RegisterRequest;
import com.buyukozkan.dto.response.LoginResponse;
import com.buyukozkan.model.Account;
import com.buyukozkan.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
}
