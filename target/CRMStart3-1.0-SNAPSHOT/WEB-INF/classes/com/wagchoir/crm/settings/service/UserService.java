package com.wagchoir.crm.settings.service;

import com.wagchoir.crm.exception.LoginException;
import com.wagchoir.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String account, String pwd, String ip) throws LoginException;

    List<User> getUserList();
}
