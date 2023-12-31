package io.github.laminalfalah.gateway.exception;

/*
 * Copyright (C) 2023 the original author laminalfalah All Right Reserved.
 *
 * io.github.laminalfalah.gateway.exception
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

/**
 * @author laminalfalah <laminalfalah08@gmail.com> on 14/11/23
 */

public class GatewayException extends RuntimeException {

  public GatewayException(String message) {
    super(message);
  }

  public GatewayException(String message, Throwable cause) {
    super(message, cause);
  }

}
