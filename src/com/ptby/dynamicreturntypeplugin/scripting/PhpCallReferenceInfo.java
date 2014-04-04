package com.ptby.dynamicreturntypeplugin.scripting;

public class PhpCallReferenceInfo {

    private final String jsonConfiguredClass;
    private final String jsConfiguredMethod;


    public PhpCallReferenceInfo( String jsonConfiguredClass, String jsConfiguredMethod ) {
        this.jsonConfiguredClass = jsonConfiguredClass;
        this.jsConfiguredMethod = jsConfiguredMethod;
    }


    public String getJsonConfiguredClass() {
        return jsonConfiguredClass;
    }


    public String getJsConfiguredMethod() {
        return jsConfiguredMethod;
    }
}
