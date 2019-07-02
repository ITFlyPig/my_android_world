package com.talk51.downloader.real.exception;

public class CancelException extends Exception {
    private String message;

    public CancelException(String message) {
        super(message);
        this.message = message;
    }
}
