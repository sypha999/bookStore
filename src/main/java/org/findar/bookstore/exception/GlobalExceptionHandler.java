package org.findar.bookstore.exception;


import jakarta.servlet.http.HttpServletResponse;
import org.findar.bookstore.dtos.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<GlobalResponse> handlerForFailedException(final NotFound e, HttpServletResponse response) {
        GlobalResponse failedResponse = new GlobalResponse();
        failedResponse.setStatus(HttpStatus.NOT_FOUND);
        failedResponse.setDateTime(LocalDateTime.now().toString());
        failedResponse.setDebugMessage(e.getMessage());
        response.setStatus(404);
        return new ResponseEntity<>(failedResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<GlobalResponse> handlerForInternalServerError(final HttpServerErrorException e, HttpServletResponse response) {
        GlobalResponse failedResponse = new GlobalResponse();
        failedResponse.setStatus(HttpStatus.BAD_GATEWAY);
        failedResponse.setDateTime(LocalDateTime.now().toString());
        response.setStatus(503);
        failedResponse.setDebugMessage(e.getMessage());
        return new ResponseEntity<>(failedResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<GlobalResponse> handlerForInternalServerError(final HttpClientErrorException e, HttpServletResponse response) {
        GlobalResponse failedResponse = new GlobalResponse();
        failedResponse.setStatus(HttpStatus.BAD_GATEWAY);
        failedResponse.setDateTime(LocalDateTime.now().toString());
        response.setStatus(500);
        failedResponse.setDebugMessage(e.getMessage());
        return new ResponseEntity<>(failedResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<GlobalResponse> handlerForBadRequest(final BadRequest e, HttpServletResponse response) {
        GlobalResponse failedResponse = new GlobalResponse();
        failedResponse.setStatus(HttpStatus.BAD_REQUEST);
        failedResponse.setDateTime(LocalDateTime.now().toString());
        failedResponse.setDebugMessage(e.getMessage());
        response.setStatus(400);
        return new ResponseEntity<>(failedResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<GlobalResponse> handleExceptionforRequestHeader(final MissingRequestHeaderException e, HttpServletResponse response) {
        GlobalResponse failedResponse = new GlobalResponse();
        failedResponse.setStatus(HttpStatus.UNAUTHORIZED);
        failedResponse.setDateTime(LocalDateTime.now().toString());
        failedResponse.setDebugMessage(e.getMessage());
        response.setStatus(400);
        return new ResponseEntity<>(failedResponse, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(UnathuorizedAccess.class)
    public ResponseEntity<GlobalResponse> handlerForUnauthorizedAccess(final UnathuorizedAccess e, HttpServletResponse response) {
        GlobalResponse failedResponse = new GlobalResponse();
        failedResponse.setStatus(HttpStatus.UNAUTHORIZED);
        failedResponse.setDateTime(LocalDateTime.now().toString());
        failedResponse.setDebugMessage(e.getMessage());
        response.setStatus(401);
        return new ResponseEntity<>(failedResponse, HttpStatus.UNAUTHORIZED);
    }
}