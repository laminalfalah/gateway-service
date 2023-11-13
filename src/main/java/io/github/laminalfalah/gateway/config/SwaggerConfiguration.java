package io.github.laminalfalah.gateway.config;

/*
 * Copyright (C) 2023 the original author laminalfalah All Right Reserved.
 *
 * io.github.laminalfalah.gateway.config
 *
 * This is part of the gateway-service.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
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

import io.github.laminalfalah.gateway.exception.GatewayException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author laminalfalah <laminalfalah08@gmail.com> on 09/11/23
 */

@Slf4j
@Configuration
@RefreshScope
public class SwaggerConfiguration {

  @Value("${spring.application.name}")
  private String applicationName;

  @Bean
  @Primary
  public SwaggerUiConfigProperties swaggerUiConfig(SpringDocConfigProperties doc, SwaggerUiConfigProperties config, RouteDefinitionLocator locator) {
    List<RouteDefinition> routeDefinitions = locator.getRouteDefinitions().collectList().block();

    if (routeDefinitions == null) {
      throw new GatewayException("route is null");
    }

    Set<SwaggerUrl> swaggerUrls = new HashSet<>();

    routeDefinitions.forEach(routeDefinition -> {
      SwaggerUrl swaggerUrl = new SwaggerUrl();

      String name = routeDefinition.getId().contains("_")
                        ? routeDefinition.getId().toLowerCase().split("_")[1]
                        : routeDefinition.getId().toLowerCase();

      if (!Objects.equals(name, applicationName)) {
        swaggerUrl.setName(name);
        swaggerUrl.setDisplayName(StringUtils.capitalize(name.replace("-", " ")));
        swaggerUrl.setUrl(doc.getApiDocs().getPath() + (name.equals("openapi") ? "" : "/" + name));
        swaggerUrls.add(swaggerUrl);
      }
    });

    config.setUrls(swaggerUrls);

    return config;
  }

}
