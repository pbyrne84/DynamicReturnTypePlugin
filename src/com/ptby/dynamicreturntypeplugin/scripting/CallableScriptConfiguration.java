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


    public String getScriptFileLocation() {
        return scriptFileLocation;
    }


    public String getScriptCode() {
        return scriptCode;
    }


    public String getScriptCall() {
        return scriptCall;
    }
}
