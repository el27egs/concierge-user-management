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
package com.ngineapps.concierge.user.management.controllers;

import com.ngineapps.concierge.user.management.dto.UserAccountValidationDTO;
import com.ngineapps.concierge.user.management.dto.UserResponseDTO;
import com.ngineapps.concierge.user.management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

  private final UserService userService;

  public UsersController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String userId) {
    return ResponseEntity.ok().body(UserResponseDTO.builder().id(userId).build());
  }

  @GetMapping("/{userId}/accounts/{accountId}/validate")
  public ResponseEntity<UserAccountValidationDTO> validateUserIsAccountOwner(
      @PathVariable int userId, @PathVariable String accountId) {

    log.info("Validating if user with id: {} is owner of account: {}", userId, accountId);

    UserAccountValidationDTO responseDto =
        userService.validateUserIsAccountOwner(userId, accountId);

    log.info(
        "User with id: {} {} owner of the account: {}",
        userId,
        (responseDto.isOwnerOfAccount() ? "IS" : "IS NOT"),
        accountId);

    return ResponseEntity.ok().body(responseDto);
  }
}
