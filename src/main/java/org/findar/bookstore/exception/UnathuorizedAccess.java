package org.findar.bookstore.exception;

public class UnathuorizedAccess extends RuntimeException{
    public UnathuorizedAccess(String message){
        super(message);
    }
}
