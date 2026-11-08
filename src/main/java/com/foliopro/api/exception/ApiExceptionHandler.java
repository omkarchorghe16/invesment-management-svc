package com.foliopro.api.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
 public record ApiError(Instant timestamp,int status,String error,String message,Map<String,String> details) {}
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<ApiError> notFound(NoSuchElementException e){return response(HttpStatus.NOT_FOUND,e.getMessage(),Map.of());}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiError> badRequest(IllegalArgumentException e){return response(HttpStatus.BAD_REQUEST,e.getMessage(),Map.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e){Map<String,String> details=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->details.put(x.getField(),x.getDefaultMessage()));return response(HttpStatus.BAD_REQUEST,"Request validation failed",details);}
 @ExceptionHandler({MethodArgumentTypeMismatchException.class,org.springframework.http.converter.HttpMessageNotReadableException.class}) ResponseEntity<ApiError> malformed(Exception e){return response(HttpStatus.BAD_REQUEST,"Invalid request parameter or JSON body",Map.of());}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiError> unexpected(Exception e){return response(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error",Map.of());}
 private ResponseEntity<ApiError> response(HttpStatus s,String m,Map<String,String>d){return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),s.getReasonPhrase(),m,d));}
}
