package com.ra.service;

import com.ra.model.dto.request.AccountLoginDTO;
import com.ra.model.dto.request.AccountRegisterDTO;
import com.ra.model.entiry.Account;

public interface AccountService {
    Account getAccountByUsername(String username);

 Account registerAccount(AccountRegisterDTO accountRegisterDTO);

String loginAccount(AccountLoginDTO accountLoginDTO);
}
