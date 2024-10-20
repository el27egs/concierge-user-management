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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
// @EnableWebSecurity  // @Configuration is ok, this one throws an exception, do not use it
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .requestMatchers("/api/v1/users/**")
                    .authenticated()
                    .requestMatchers("/api/v1/locations/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwt -> jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter())))
        .headers(
            headers ->
                headers
                    .xssProtection(xss -> xss.disable())
                    .contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self'")))
        .build();
  }
}
