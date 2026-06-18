package com.estudios.demosafkaspring.exceptions;

public class InsufficientCapacityException extends RuntimeException {
    public InsufficientCapacityException(String errorMessage) {
        super(errorMessage);
    }
}
