/*
 *     Copyright 2022-Present Ngine Apps @ http://www.ngingeapps.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ngineapps.concierge.user.management.service;

import com.ngineapps.concierge.user.management.dto.UserAccountValidationDTO;
import com.ngineapps.concierge.user.management.exceptions.ClientBusinessLogicException;
import com.ngineapps.concierge.user.management.exceptions.DataNotFoundException;
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
  public UserAccountValidationDTO validateUserIsAccountOwner(int userId, String accountId) {

    User user =
        this.userRepository
            .findUserByUserIdAndAccountId(userId, accountId)
            .orElseThrow(
                () -> new DataNotFoundException(UserMessages.USER_IS_NOT_ACCOUNT_OWNER));

    UserAccountValidationDTO dto =
        UserAccountValidationDTO.builder()
            .userId(userId)
            .accountId(accountId)
            .isUserOwnerAccount(true)
            .build();

    return dto;
  }
}
