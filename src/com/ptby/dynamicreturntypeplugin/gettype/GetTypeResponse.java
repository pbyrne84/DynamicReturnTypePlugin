package com.ptby.dynamicreturntypeplugin.gettype;

public class GetTypeResponse {
    private final String response;


    public GetTypeResponse( String response ) {
        if( response != null && response.equals( "null" ) ) {
            throw new RuntimeException("cannot be string null");

        }

        this.response = response;
    }


    public boolean isNull() {
        return response == null;
    }


    @Override
    public String toString() {
        return response;
    }
}
