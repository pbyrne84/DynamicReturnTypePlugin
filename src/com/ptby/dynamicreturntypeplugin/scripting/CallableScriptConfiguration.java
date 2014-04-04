package com.ptby.dynamicreturntypeplugin.scripting;

public class CallableScriptConfiguration {

    private final String scriptFileLocation;
    private final String scriptCode;
    private final String scriptCall;


    public CallableScriptConfiguration( String scriptFileLocation, String scriptCode, String scriptCall ) {
        this.scriptFileLocation = scriptFileLocation;
        this.scriptCode = scriptCode;
        this.scriptCall = scriptCall;
    }


    public String getFileLocation() {
        return scriptFileLocation;
    }


    public String getCode() {
        return scriptCode;
    }


    public String getCall() {
        return scriptCall;
    }
}
