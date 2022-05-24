package com.ngineapps.concierge.user.management.service;

import com.ngineapps.concierge.user.management.dto.UserAccountValidationDTO;

public interface UserService {

    UserAccountValidationDTO validateUserIsAccountOwner(int userId, String accountId);

}

