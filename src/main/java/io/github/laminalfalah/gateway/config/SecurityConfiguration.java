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

import java.time.Duration;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.CrossOriginEmbedderPolicyServerHttpHeadersWriter.CrossOriginEmbedderPolicy;
import org.springframework.security.web.server.header.CrossOriginOpenerPolicyServerHttpHeadersWriter.CrossOriginOpenerPolicy;
import org.springframework.security.web.server.header.CrossOriginResourcePolicyServerHttpHeadersWriter.CrossOriginResourcePolicy;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;

/**
 * @author laminalfalah <laminalfalah08@gmail.com> on 08/11/23
 */

@Configuration
@EnableWebFlux
@EnableWebSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration implements WebFluxConfigurer {

  @Value("${management.endpoints.web.base-path:/actuator}")
  private String pathActuator;

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    http.httpBasic(HttpBasicSpec::disable)
        .formLogin(FormLoginSpec::disable)
        .csrf(CsrfSpec::disable)
//        .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                          .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
//        )
        .exceptionHandling(
            exceptionHandler ->
                exceptionHandler.accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
        )
        .exceptionHandling(
            exceptionHandler ->
                exceptionHandler.authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))))
        .headers(
            headerSpec ->
                headerSpec.frameOptions(frameOptionsSpec -> frameOptionsSpec.mode(Mode.SAMEORIGIN))
                    .contentSecurityPolicy(c -> c.reportOnly(true))
                    .contentSecurityPolicy(c -> c.policyDirectives("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:;"))
                    .frameOptions(f -> f.mode(Mode.DENY))
                    .hsts(h -> h.includeSubdomains(true).preload(true).maxAge(Duration.ofHours(1)))
                    .referrerPolicy(r -> r.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .crossOriginEmbedderPolicy(c -> c.policy(CrossOriginEmbedderPolicy.REQUIRE_CORP))
                    .crossOriginOpenerPolicy(c -> c.policy(CrossOriginOpenerPolicy.SAME_ORIGIN_ALLOW_POPUPS))
                    .crossOriginResourcePolicy(c -> c.policy(CrossOriginResourcePolicy.CROSS_ORIGIN))
                    .permissionsPolicy(p -> p.policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"))
        )
        .authorizeExchange(
            authorizeExchangeSpec ->
                authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/livez", "/readyz").permitAll()
                    .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                    .pathMatchers(HttpMethod.GET, pathActuator + "/info").permitAll()
                    .pathMatchers(HttpMethod.GET, pathActuator + "/health").permitAll()
                    .pathMatchers(HttpMethod.GET, pathActuator + "/health/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/version").permitAll()
                    .pathMatchers(pathActuator + "/**").permitAll()
                    .pathMatchers("/**").permitAll()
                    .anyExchange().authenticated()
        );

    return http.build();
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration());
    return new CorsWebFilter(source);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").combine(corsConfiguration());
  }

  private CorsConfiguration corsConfiguration() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(false);
    corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
    corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
    //corsConfiguration.setAllowedOrigins(List.of("http://localhost:8080"));
    corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
    //corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:*"));
    corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
    corsConfiguration.setExposedHeaders(Collections.singletonList("*"));
    corsConfiguration.setMaxAge(Duration.ofHours(1));
    return corsConfiguration;
  }

}
