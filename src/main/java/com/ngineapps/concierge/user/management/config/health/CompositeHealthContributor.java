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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.stereotype.Component;

@Component("concierge-user-management-api")
public class CompositeHealthContributor
    implements org.springframework.boot.actuate.health.CompositeHealthContributor {

  private Map<String, HealthContributor> contributors = new LinkedHashMap<>();

  @Autowired
  public CompositeHealthContributor(
      @Qualifier("concierge-user-management-db") HealthIndicator databaseHealthContributor,
      @Qualifier("concierge-user-management-gateway") HealthIndicator gatewayHealthContributor) {

    contributors.put("concierge-user-management-gateway", gatewayHealthContributor);

    contributors.put("concierge-user-management-db", databaseHealthContributor);
  }

  /** return list of health contributors */
  @Override
  public Iterator<NamedContributor<HealthContributor>> iterator() {
    return contributors.entrySet().stream()
        .map((entry) -> NamedContributor.of(entry.getKey(), entry.getValue()))
        .iterator();
  }

  @Override
  public HealthContributor getContributor(String name) {
    return contributors.get(name);
  }
}
