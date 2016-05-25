package com.excilys.cdb.servlets;

import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return "errors/500";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex) {
        return "errors/404";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handlePageNotFoundException(NoHandlerFoundException ex) {
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception ex) {
        return "errors/505";
    }
}
