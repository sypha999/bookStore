package org.findar.bookstore.exception;

public class NotFound extends RuntimeException{
    public NotFound(String message){
        super(message);
    }
}