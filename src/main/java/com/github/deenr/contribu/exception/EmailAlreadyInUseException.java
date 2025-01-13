package com.github.deenr.contribu.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    private String message;

    public EmailAlreadyInUseException() {}

    public EmailAlreadyInUseException(String msg) {
        super(msg);
        this.message = msg;
    }
}
