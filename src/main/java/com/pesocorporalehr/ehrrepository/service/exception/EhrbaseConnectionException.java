package com.pesocorporalehr.ehrrepository.service.exception;


public class EhrbaseConnectionException extends RuntimeException {

    public EhrbaseConnectionException(String message) {
        super(message);
    }

    public EhrbaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}