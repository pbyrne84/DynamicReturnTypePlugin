package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.script.ScriptEngineManager;

public class ValueReplacementStrategyFromConfigFactory {
    private static final String FILE_RETURN_TYPE_REPLACEMENT_CALL = "fileReturnTypeReplacementCall";

    private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    public ValueReplacementStrategyFromConfigFactory() {

    }


    public ValueReplacementStrategy createFromJson( String scriptParentDirectory, JsonObject configObject ) {
        if ( configObject.has( "mask" ) ) {
            String mask = configObject.get( "mask" ).getAsString().trim();
            if ( mask.contains( "%" ) ) {
                return new MaskValueReplacementStrategy( mask );
            }
        }

        if ( configObject.has( FILE_RETURN_TYPE_REPLACEMENT_CALL ) && configObject.get(
                FILE_RETURN_TYPE_REPLACEMENT_CALL
        ).isJsonArray() ) {
            JsonArray javascriptCallOptions = configObject.get( FILE_RETURN_TYPE_REPLACEMENT_CALL ).getAsJsonArray();
            if ( javascriptCallOptions.size() == 2 ) {
                String fileName = javascriptCallOptions.get( 0 ).getAsString();
                String javascriptFunctionCall = javascriptCallOptions.get( 1 ).getAsString();
                return createScriptFileCallbackReplacementStrategy(
                        scriptParentDirectory,
                        configObject,
                        fileName,
                        javascriptFunctionCall
                );
            }
        }

        return new PassthruValueReplacementStrategy();
    }


    private ValueReplacementStrategy createScriptFileCallbackReplacementStrategy( String scriptParentDirectory,
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
                scriptEngineManager,
                scriptParentDirectory,
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
