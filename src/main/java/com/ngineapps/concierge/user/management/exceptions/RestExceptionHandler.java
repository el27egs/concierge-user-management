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
package com.ngineapps.concierge.user.management.exceptions;

import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private ResponseEntity<Object> buildResponseEntity(final ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(ClientBusinessLogicException.class)
  protected ResponseEntity<Object> handleClientBusinessLogicException(
      final ClientBusinessLogicException ex) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage);
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exceptionMessage);
    return this.buildResponseEntity(apiError);
  }

  @ExceptionHandler(ServerBusinessLogicException.class)
  protected ResponseEntity<Object> handleServerBusinessLogicException(
      final ServerBusinessLogicException ex) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage);
    final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exceptionMessage);
    return this.buildResponseEntity(apiError);
  }

  @ExceptionHandler(DataNotFoundException.class)
  protected ResponseEntity<Object> handleDataNotFoundException(final DataNotFoundException ex) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage);
    String message =
        "El recurso solicitado no está disponible en este momento. Valide los datos de la solicitud de entrada.";
    final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(message);
    return this.buildResponseEntity(apiError);
  }

  @ExceptionHandler(AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationException(final AuthenticationException ex) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage, ex);
    log.error("Stack Trace: ", ex);
    String message =
        "Por favor, consulte con su administrador para obtener el acceso a este recurso.";
    final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
    apiError.setMessage(message);
    return this.buildResponseEntity(apiError);
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException ex) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage, ex);
    log.error("Stack Trace: ", ex);
    String message =
        "Por favor, consulte con su administrador para obtener los permisos necesarios para poder acceder a este recurso.";
    final ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
    apiError.setMessage(message);
    return this.buildResponseEntity(apiError);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleException(final Exception ex) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage, ex);
    log.error("Stack Trace: ", ex);
    String message =
        "El servicio no está disponible en este momento. Por favor, póngase en contacto con el administrador del servicio..";
    final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(message);
    return this.buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage);
    final String message =
        "Datos de solicitud de entrada no legibles. Por favor, verifique nuevamente los datos ingresados.";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(message);
    return this.buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    final Map<String, String> errors = new HashMap<>();
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage);
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              final String fieldName = ((FieldError) error).getField();
              final String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    final String message =
        "Datos de solicitud de entrada incorrectos. Por favor, verifique nuevamente los datos ingresados.";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(message);
    apiError.setErrors(errors);
    return this.buildResponseEntity(apiError);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    String exceptionMessage = ex.getMessage();
    log.error("Exception message: {} ", exceptionMessage);
    final String message =
        "Datos de solicitud de entrada incorrectos. Por favor, verifique nuevamente los datos ingresados.";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(message);
    return this.buildResponseEntity(apiError);
  }
}
