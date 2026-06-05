package com.nowak.budget_manager.common.exception;

public class NameConflictException extends RuntimeException{
    public NameConflictException(String message)
    {
        super(message);
    }
}
