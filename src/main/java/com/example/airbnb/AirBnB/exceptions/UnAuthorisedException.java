package com.example.airbnb.AirBnB.exceptions;

public class UnAuthorisedException extends RuntimeException {
    public UnAuthorisedException(String message) {
        super(message);
    }
}
