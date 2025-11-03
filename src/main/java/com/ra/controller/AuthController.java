package com.ra.controller;

import com.ra.model.dto.request.AccountLoginDTO;
import com.ra.model.dto.request.AccountRegisterDTO;
import com.ra.model.dto.response.DataResponse;
import com.ra.model.entiry.Account;
import com.ra.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AuthController {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<DataResponse<Account>> register(@Valid @RequestBody AccountRegisterDTO accountRegisterDTO){
        Account newAccount = accountService.registerAccount(accountRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<Account>builder()
                        .status(201)
                        .message("Register account successfully")
                        .data(newAccount)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<String>> login(@Valid @RequestBody AccountLoginDTO accountLoginDTO){
        String token = accountService.loginAccount(accountLoginDTO);
        return ResponseEntity.ok(
                DataResponse.<String>builder()
                        .status(200)
                        .message("login successfully")
                        .data(token)
                        .build()
        );
    }
}
