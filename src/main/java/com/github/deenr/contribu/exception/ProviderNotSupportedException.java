package com.github.deenr.contribu.exception;

public class ProviderNotSupportedException extends RuntimeException {
    public ProviderNotSupportedException(String provider) {
        super("Provider not supported: " + provider);
    }
}
