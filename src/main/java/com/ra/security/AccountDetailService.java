package com.ra.security;

import com.ra.model.entiry.Account;
import com.ra.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountDetailService implements UserDetailsService {
    @Autowired
    private  AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("user not found with username" + username);
        }else{
            return AccountPrincipal.builder()
                    .account(account)
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().toString())))
                    .build();
        }
    }
}
