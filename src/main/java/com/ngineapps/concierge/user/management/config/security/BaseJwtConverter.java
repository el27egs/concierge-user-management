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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public abstract class BaseJwtConverter {

	public Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		grantedAuthorities.addAll(getRoles(jwt));
		grantedAuthorities.addAll(getScopes(jwt));

		return grantedAuthorities;
	}

	public List<SimpleGrantedAuthority> getRoles(Jwt jwt) {
		return ((List<String>) jwt.getClaimAsMap("realm_access").getOrDefault("roles", new ArrayList<>())).stream()
				.map(roleName -> "ROLE_" + roleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public List<SimpleGrantedAuthority> getScopes(Jwt jwt) {
		return Arrays.asList(jwt.getClaimAsString("scope").split("\\s+")).stream().map(roleName -> "SCOPE_" + roleName)
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
}
