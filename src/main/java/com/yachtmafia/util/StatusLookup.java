package com.yachtmafia.util;

/**
 * Created by xfant on 2018-03-21.
 */
public enum StatusLookup {
    FAILED(-1),
    SUCCESS(0),
    REQUEST_SENT_FROM_WEB(100),
    REQUEST_RECEIVED_BY_SERVER(101),
    WALLET_CREATED(102),
    SUBMITTING_TO_EXCHANGE(103),
    VERIFYING_EXCHANGE(104),
    ADDING_TO_WALLET(105),
    FINALISING(106),

    PAYMENT_NOT_RECEIVED(300),
    COULD_NOT_ADD_WALLET(301),
    COULD_NOT_SEND_EMAIL(302),
    COULD_NOT_ADD_PORTFOLIO_BALANCE(303);


    private int code;

    StatusLookup(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
