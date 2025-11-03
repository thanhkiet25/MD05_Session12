package com.ra.service.imp;

import com.ra.exception.CustomException;
import com.ra.model.contrain.Role;
import com.ra.model.dto.request.AccountLoginDTO;
import com.ra.model.dto.request.AccountRegisterDTO;
import com.ra.model.entiry.Account;
import com.ra.repository.AccountRepository;
import com.ra.security.jwt.JWTProvider;
import com.ra.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private  AccountRepository accountRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  JWTProvider jwtProvider;
    @Override
    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account registerAccount(AccountRegisterDTO accountRegisterDTO) {
        if(getAccountByUsername(accountRegisterDTO.getUsername())!=null){
            throw new CustomException("Username is already in use");
        }
        Account account = Account
                .builder()
                .username(accountRegisterDTO.getUsername())
                .password(passwordEncoder.encode(accountRegisterDTO.getPassword()))
                .fullName(accountRegisterDTO.getFullName())
                .phoneNumber(accountRegisterDTO.getPhoneNumber())
                .role(Role.EMPLOYEE)
                .build();
return accountRepository.save(account);
    }

    @Override
    public String loginAccount(AccountLoginDTO accountLoginDTO) {
        Account account = accountRepository.findByUsername(accountLoginDTO.getUsername());
        if(account == null || !passwordEncoder.matches(accountLoginDTO.getPassword(),account.getPassword())){
            throw new CustomException("Invalid username or password");
        }
        return jwtProvider.generateToken(account.getUsername());
    }


}
