package com.ptby.dynamicreturntypeplugin.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;

import java.util.ArrayList;
import java.util.List;

public class JsonToDynamicReturnTypeConfigConverter {

    public DynamicReturnTypeConfig convertJson( String json ) {
        JsonElement jsonElement = createJsonElementFromJson( json );
        if ( jsonElement == null || !jsonElement.isJsonObject() ) {
            return new DynamicReturnTypeConfig();
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray methodCalls = jsonObject.getAsJsonArray( "methodCalls" );
        List<ClassMethodConfig> classMethodConfigs = castJsonMethodCallConfigToClassMethodConfigs( methodCalls );

        JsonElement functionCalls = jsonObject.get( "functionCalls" );
        List<FunctionCallConfig> functionCallConfigs = castJsonMethodCallConfigToFunctionCallConfigs( functionCalls );

        return new DynamicReturnTypeConfig( classMethodConfigs, functionCallConfigs );
    }


    private JsonElement createJsonElementFromJson( String json ) {
        Gson gson = new Gson();
        try {
            return gson.fromJson( json, JsonElement.class );
        } catch ( JsonSyntaxException e ) {
            return null;
        }
    }


    private List<ClassMethodConfig> castJsonMethodCallConfigToClassMethodConfigs( JsonElement methodCalls ) {
        ArrayList<ClassMethodConfig> classMethodConfigs = new ArrayList<ClassMethodConfig>();
        if ( methodCalls == null ) {
            return classMethodConfigs;
        }

        JsonArray jsonMethodConfigList = methodCalls.getAsJsonArray();
        for ( JsonElement jsonElement : jsonMethodConfigList ) {
            if ( !jsonElement.isJsonNull() ) {
                JsonObject jsonMethodCall = jsonElement.getAsJsonObject();
                ClassMethodConfig classMethodConfig = new ClassMethodConfig(
                        getJsonString( jsonMethodCall, "class" ),
                        getJsonString( jsonMethodCall, "method" ),
                        getJsonInt( jsonMethodCall, "position" ),
                        getJsonString( jsonMethodCall, "mask" )
                );

                if( classMethodConfig.isValid() ){
                    classMethodConfigs.add( classMethodConfig );
                }

            }
        }

        return classMethodConfigs;
    }


    private String getJsonString( JsonObject object, String name ) {
        if ( !object.has( name ) ) {
            return "";
        }

        return object.get( name ).getAsString();
    }


    private int getJsonInt( JsonObject object, String name ) {
        if ( !object.has( name ) ) {
            return -1;
        }

        return object.get( name ).getAsInt();
    }


    private List<FunctionCallConfig> castJsonMethodCallConfigToFunctionCallConfigs( JsonElement functionCalls ) {
        ArrayList<FunctionCallConfig> functionCallConfigs = new ArrayList<FunctionCallConfig>();
        if ( functionCalls == null ) {
            return functionCallConfigs;
        }

        JsonArray jsonFunctionCalConfigList = functionCalls.getAsJsonArray();
        for ( JsonElement jsonElement : jsonFunctionCalConfigList ) {
            if ( !jsonElement.isJsonNull() ) {
                JsonObject jsonFunctionCall = jsonElement.getAsJsonObject();
                FunctionCallConfig functionCallConfig = new FunctionCallConfig(
                        getJsonString( jsonFunctionCall, "function" ),
                        getJsonInt( jsonFunctionCall, "position" ),
                        getJsonString( jsonFunctionCall, "mask" )
                );

                if( functionCallConfig.isValid() ){
                    functionCallConfigs.add( functionCallConfig );
                }
            }
        }

        return functionCallConfigs;
    }
}
