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
package com.ngineapps.concierge.user.management.config.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MetricsConfig {

  private CustomMetricsProperties properties;

  @Autowired
  public MetricsConfig(CustomMetricsProperties properties) {
    this.properties = properties;
  }

  /** @return Adds prefix to metrics. */
  @Bean
  public MeterRegistryCustomizer<MeterRegistry> commonTags() {

    String applicationPrefixKey = "application";
    String environmentPrefixKey = "env";
    String hostPrefixKey = "host";

    String hostName;
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      log.error("Error", e);
      hostName = "localhost";
    }
    String finalHostName = hostName;
    String applicationPrefix = properties.getAppName();
    return r ->
        r.config()
            .commonTags(
                applicationPrefixKey,
                applicationPrefix,
                environmentPrefixKey,
                properties.getEnvironment(),
                hostPrefixKey,
                finalHostName);
  }
}
