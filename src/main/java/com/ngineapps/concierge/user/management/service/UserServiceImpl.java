package com.ngineapps.concierge.user.management.service;

import com.ngineapps.concierge.user.management.dto.UserAccountValidationDTO;
import com.ngineapps.concierge.user.management.exceptions.ClientBusinessLogicException;
import com.ngineapps.concierge.user.management.model.User;
import com.ngineapps.concierge.user.management.repositories.UserRepository;
import com.ngineapps.concierge.user.management.util.UserMessages;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    /*
     * TODO probar esta relacion usando Fetch porque de lo contratio no funcionara cuando se tenga el DS readonly
     */
    public UserAccountValidationDTO validateUserIsAccountOwner(int userId, String accountId){

        User user =
                this.userRepository
                        .findUserByUserIdAndAccountId(userId, accountId)
                        .orElseThrow(
                                () -> new ClientBusinessLogicException(UserMessages.USER_IS_NOT_ACCOUNT_OWNER));

        UserAccountValidationDTO dto =
                UserAccountValidationDTO.builder().userId(userId).accountId(accountId).isUserOwnerAccount(true).build();

        return dto;
    }
}


