package com.flipkart.android.proteus.exceptions;

/**
 * InvalidDataPathException
 *
 * @author Aditya Sharat
 */
public class InvalidDataPathException extends Exception {
    private String message;

    public InvalidDataPathException(String dataPath) {
        message = dataPath + " is invalid.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
