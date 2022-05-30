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
package com.ngineapps.concierge.user.management.config.health;

import java.net.Socket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("concierge-user-management-gateway")
@Slf4j
public class GatewayHealthContributor implements HealthIndicator, HealthContributor {

  private final RestTemplate restTemplate;

  @Value("${app.urls.api-gateway}")
  private String apiGatewayUrl;

  public GatewayHealthContributor(@Qualifier("internalWebClient") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public Health health() {
    try (Socket socket = new Socket(new java.net.URL(apiGatewayUrl).getHost(), 8008)) {
    } catch (Exception e) {
      log.warn("Failed to connect to: {}", apiGatewayUrl);
      return Health.down().withDetail("error", e.getMessage()).build();
    }
    return Health.up().build();
  }
}
