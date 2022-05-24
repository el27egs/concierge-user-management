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

import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/access")
public class AccessTokenController {

  /*
   * Method Security Level authorities represent a more granular permissions like
   * deleteUser or creteUSer with our converter we are passing the scope with
   * SCOPE_ prefix using "scope" claim from JWT token
   *
   * Here we also created a custom scope in our Authorization Server call
   * concierge-user, and we add it as a optional scope in the call.
   */
  // @PreAuthorize("hasAuthority('SCOPE_concierge-user')")

  /*
   * Method Security Level roles represent a high-level permissions like AdminUser
   * or EditorAccounts with our converter we are passing the roles with ROLE_
   * prefix using "realm_access.roles" claim from JWT token Remember hasRole
   * automatically add ROLE_ prefix
   */
  @GetMapping("/token")
  // @PreAuthorize("hasRole('concierge-user-token')")
  public ResponseEntity<Map<String, Object>> token(@AuthenticationPrincipal Jwt jwt) {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");

    return ResponseEntity.ok().headers(headers).body(Collections.singletonMap("principal", jwt));
  }

  /*
   * Method security at level method, no required checking roles or authorities on
   * WebSecurityConfig We can use @Secured in controller or service classes.
   */
  /*
   * PreAuthorize annotation support method expression but Secured annotation no.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('concierge-user-token') or #id == #jwt.subject")
  // @Secured("ROLE_concierge-user-token")
  public ResponseEntity<String> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");

    return ResponseEntity.ok()
        .headers(headers)
        .body("Id " + id + " and JWT subject = " + jwt.getSubject());
  }
}
