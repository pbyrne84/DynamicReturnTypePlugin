package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.vfs.VirtualFile;

public class ValueReplacementStrategyFromConfigFactory {
    public static final String FILE_FUNCTION_CALL_KEY = "fileFunctionCall";

    public ValueReplacementStrategyFromConfigFactory() {
    }


    public ValueReplacementStrategy createFromJson( VirtualFile configFile, JsonObject configObject ) {
        if ( configObject.has( "mask" ) ) {
            String mask = configObject.get( "mask" ).getAsString().trim();
            if ( mask.contains( "%" ) ) {
                return new MaskValueReplacementStrategy( mask );
            }
        }

        if ( configObject.has( FILE_FUNCTION_CALL_KEY ) && configObject.get( FILE_FUNCTION_CALL_KEY ).isJsonArray() ) {
            JsonArray javascriptCallOptions = configObject.get( FILE_FUNCTION_CALL_KEY ).getAsJsonArray();
            if ( javascriptCallOptions.size() == 2 ) {
                String fileName = javascriptCallOptions.get( 0 ).getAsString();
                String javascriptFunctionCall = javascriptCallOptions.get( 1 ).getAsString();

                return createScriptFileCallbackReplacementStrategy( configFile, configObject, fileName, javascriptFunctionCall );
            }
        }

        return new PassthruValueReplacementStrategy();
    }


    private ValueReplacementStrategy createScriptFileCallbackReplacementStrategy( VirtualFile configFile,
                                                                                  JsonObject configObject,
                                                                                  String fileName,
                                                                                  String javascriptFunctionCall ) {
        String className = getJsonString( configObject, "class" );

        String method;
        if ( className.equals( "" ) ) {
            method = getJsonString( configObject, "function" );
        } else {
            method = getJsonString( configObject, "method" );
        }

        return new ScriptFileCallbackReplacementStrategy(
                configFile,
                className,
                method,
                fileName,
                javascriptFunctionCall
        );
    }


    private String getJsonString( JsonObject object, String name ) {
        if ( !object.has( name ) ) {
            return "";
        }

        return object.get( name ).getAsString().trim();
    }
}
