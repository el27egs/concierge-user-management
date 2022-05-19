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

import java.util.Collection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloackRoleConverter extends BaseJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	public Collection<GrantedAuthority> convert(Jwt jwt) {

		Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
		/*
		 * We add here all authorities,from roles, scopes etc. for Spring all of them
		 * are GrantedAuthorities we just need to take of add the prefixes ROLE_ or
		 * SCOPE_ and how can we use them to verify at method security level hasRole()
		 * does not require adding ROLE_ prefix, hasAuthority() requires the full name
		 * as they were defined, hasAuthority() will not append any prefix, it will look
		 * for exact match
		 */
		return authorities;
	}
}
