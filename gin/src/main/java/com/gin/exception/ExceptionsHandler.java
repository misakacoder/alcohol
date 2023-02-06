package com.gin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class ExceptionsHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionsHandler.class);

    @ExceptionHandler(Exception.class)
    public void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("", e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        try (PrintWriter writer = response.getWriter()) {
            e.printStackTrace(writer);
        } catch (IOException ioe) {
            log.error("", ioe);
        }
    }
}
