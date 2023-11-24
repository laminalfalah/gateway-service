package io.github.laminalfalah.gateway.controller;

/*
 * Copyright (C) 2023 the original author laminalfalah All Right Reserved.
 *
 * io.github.laminalfalah.gateway.controller
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

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author laminalfalah <laminalfalah08@gmail.com> on 24/11/23
 */

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Hello Controller", description = "Test endpoint gateway")
public class HelloController {

  @GetMapping
  public Mono<ResponseEntity<?>> index() {
    return Mono.just(ResponseEntity.ok().body(Collections.singletonMap("message", "It's Gateway")));
  }

}
