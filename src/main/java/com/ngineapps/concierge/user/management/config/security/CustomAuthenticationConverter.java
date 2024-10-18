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
package com.ngineapps.concierge.user.management.config.security;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  @Override
  public AbstractAuthenticationToken convert(@Nullable Jwt jwt) {

    log.info("CustomAuthenticationConverter getting roles");

    if (Objects.isNull(jwt)) {
      log.error("JWT argument is NULL");
      throw new IllegalArgumentException("JWT cannot be NULL");
    }

    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

    return new JwtAuthenticationToken(jwt, authorities);
  }

  public Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    grantedAuthorities.addAll(getRealmAccessRoles(jwt));
    grantedAuthorities.addAll(getResourceAccessRoles(jwt));
    grantedAuthorities.addAll(getScopes(jwt));

    log.info("\nGrantedAuthorities {}\n", grantedAuthorities);
    return grantedAuthorities;
  }

  public List<SimpleGrantedAuthority> getResourceAccessRoles(Jwt jwt) {

    Map<String, Object> resourceAccess =
        (Map<String, Object>) jwt.getClaims().get("resource_access");

    if (resourceAccess == null || resourceAccess.isEmpty()) {
      log.error("JWT does not contains resource_access claim");
      return new ArrayList<>();
    }

    Map<String, Object> conciergeGrants =
        (Map<String, Object>) resourceAccess.get("concierge-grants");

    if (conciergeGrants == null || conciergeGrants.isEmpty()) {
      log.error("JWT does not contains concierge-grants claim");
      return new ArrayList<>();
    }

    return ((List<String>) conciergeGrants.get("roles"))
        .stream()
            .map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }

  public List<SimpleGrantedAuthority> getRealmAccessRoles(Jwt jwt) {

    Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

    if (realmAccess == null || realmAccess.isEmpty()) {
      log.error("JWT does not contains realm_access claim");
      return new ArrayList<>();
    }

    return ((List<String>) realmAccess.get("roles"))
        .stream()
            .map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }

  public List<SimpleGrantedAuthority> getScopes(Jwt jwt) {

    String scopes = (String) jwt.getClaims().get("scope");

    if (scopes == null || scopes.isEmpty()) {
      log.error("JWT does not contains scope claim");
      return Collections.emptyList();
    }

    return Arrays.stream(scopes.split("\\s+"))
        .map(roleName -> "SCOPE_" + roleName)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
