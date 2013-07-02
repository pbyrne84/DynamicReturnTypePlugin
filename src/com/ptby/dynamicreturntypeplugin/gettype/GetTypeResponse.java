package com.ptby.dynamicreturntypeplugin.gettype;

public class GetTypeResponse {
    private final String response;


    public GetTypeResponse( String response ) {
        this.response = response;
    }


    @Override
    public String toString() {
        return response;
    }
}
