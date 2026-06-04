package com.nowak.budget_manager.common.exception;

public class AccountHasTransactionsException extends RuntimeException{
    public AccountHasTransactionsException(String message){
        super(message);
    }
}
