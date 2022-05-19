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

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Scopes are added at request token level Scopes can be added in Keycloak as
		 * optional scope, it needs to be created beforehand
		 *
		 * Roles are added at user level For users, in Role Mapping it must be assigned
		 * to it, it needs to be created beforehand The roles are added in real_access
		 * by default, TODO check how to add to resources_access and what is the
		 * difference. real_access role can be assigned to any user in same realm and it
		 * can be read for any application client registered in this realm.
		 */

		/*
		 *
		 * Option 1a using training example, here the jwt() call generate the scopes
		 * automatically, without CustomConverter Using hasAuthority
		 */

		/*
		 * http.authorizeRequests(authorize ->
		 * authorize.mvcMatchers("/api/v1/locations").permitAll()
		 * .mvcMatchers(HttpMethod.GET,"/api/v1/access/token").hasAuthority(
		 * "SCOPE_profile")).sessionManagement()
		 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and().
		 * csrf().disable() .oauth2ResourceServer().jwt();
		 *
		 */

		/*
		 * Option 1b using training example, here the jwt() call generate the scopes
		 * automatically, without CustomConverter Using role, this configuration is not
		 * enough, we need to convert these into roles using a custom converter due each
		 * Authorization Server can create the access token in different way, so we will
		 * need the converter to create GrantedAuthority instances, as is it will reply
		 * with 403 Forbidden
		 */

		/*
		 * http.authorizeRequests(authorize ->
		 * authorize.mvcMatchers("/api/v1/locations").permitAll()
		 * .mvcMatchers(HttpMethod.GET,"/api/v1/access/token").hasRole(
		 * "concierge-user-token")).sessionManagement()
		 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and().
		 * csrf().disable() .oauth2ResourceServer().jwt();
		 */

		/*
		 * Option 1c, same as 1b but using a converter to avoid the Forbidden 403 error
		 * here we are using setJwtGrantedAuthoritiesConverter instead of
		 * jwtAuthenticationConverter like in the other training
		 *
		 */

		/*
		 * JwtAuthenticationConverter jwtAuthenticationConverter = new
		 * JwtAuthenticationConverter();
		 * jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new
		 * KeycloackRoleConverter());
		 *
		 * http.authorizeRequests(authorize ->
		 * authorize.mvcMatchers("/api/v1/locations").permitAll()
		 * .mvcMatchers(HttpMethod.GET,
		 * "/api/v1/access/token").hasRole("concierge-user-token"))
		 * .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
		 * and().cors().and().csrf()
		 * .disable().oauth2ResourceServer().jwt().jwtAuthenticationConverter(
		 * jwtAuthenticationConverter);
		 */

		/*
		 * Option 1d, same as 1c but using hasAuthority insteed of hasRole
		 *
		 */

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloackRoleConverter());

		http.authorizeRequests(authorize -> authorize.mvcMatchers("/api/v1/locations").permitAll()
				.mvcMatchers(HttpMethod.GET, "/api/v1/access/token").hasAuthority("ROLE_concierge-user-token")
				.anyRequest().authenticated()).sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and().csrf().disable()
				.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter);

		/*
		 * Option 2 using custom converter without PreAuthorize, using Web Security
		 * level
		 */
		/*
		 * http.authorizeRequests(authorize ->
		 * authorize.mvcMatchers("/api/v1/locations").permitAll()
		 * .mvcMatchers(HttpMethod.GET,"/api/v1/access/token").hasAuthority(
		 * "SCOPE_profile")).sessionManagement()
		 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and().
		 * csrf().disable() .oauth2ResourceServer(oauth2 -> oauth2 .jwt(jwt ->
		 * jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter())))
		 * .headers().xssProtection().and().contentSecurityPolicy("script-src 'self'");
		 */

		/*
		 * Option 3 using custom converter with preAuthorize, using Method Security
		 * level
		 */
		/*
		 * http.authorizeRequests(authorize ->
		 * authorize.mvcMatchers("/api/v1/locations").permitAll()
		 * .mvcMatchers("/api/v1/access/token").authenticated()).sessionManagement()
		 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and().
		 * csrf().disable() .oauth2ResourceServer(oauth2 -> oauth2 .jwt(jwt ->
		 * jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter())))
		 * .headers().xssProtection().and().contentSecurityPolicy("script-src 'self'");
		 *
		 */
	}
}
