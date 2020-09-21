package com.thealer.telehealer.apilayer.manager.helper;

import java.io.IOException;

public class NoConnectivityException extends IOException{

    @Override
    public String getMessage() {
        return "No Internet Connection";
    }
}
