package com.ptby.dynamicreturntypeplugin.json;

import com.google.gson.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategyFromConfigFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonToDynamicReturnTypeConfigConverter {
    ValueReplacementStrategyFromConfigFactory valueReplacementStrategyFromConfigFactory = new ValueReplacementStrategyFromConfigFactory();


    public DynamicReturnTypeConfig convertJson( final VirtualFile configFile ) throws IOException {
        String parentFolder = configFile.getParent().getCanonicalPath();
        JsonElement jsonElement = createJsonElementFromJson( new String( configFile.contentsToByteArray() ) );
        if ( jsonElement == null || !jsonElement.isJsonObject() ) {
            return new DynamicReturnTypeConfig();
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray methodCalls = jsonObject.getAsJsonArray( "methodCalls" );
        List<ClassMethodConfig> classMethodConfigs = castJsonMethodCallConfigToClassMethodConfigs(
                parentFolder,
                methodCalls
        );

        JsonElement functionCalls = jsonObject.get( "functionCalls" );
        List<FunctionCallConfig> functionCallConfigs = castJsonMethodCallConfigToFunctionCallConfigs(
                parentFolder,
                functionCalls
        );

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


    private List<ClassMethodConfig> castJsonMethodCallConfigToClassMethodConfigs( String parentFolder,
                                                                                  JsonElement methodCalls ) {
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
                        valueReplacementStrategyFromConfigFactory.createFromJson( parentFolder, jsonMethodCall )
                );

                if ( classMethodConfig.isValid() ) {
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


    private List<FunctionCallConfig> castJsonMethodCallConfigToFunctionCallConfigs( String parentFolder,
                                                                                    JsonElement functionCalls ) {
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
                        valueReplacementStrategyFromConfigFactory.createFromJson( parentFolder, jsonFunctionCall )
                );

                if ( functionCallConfig.isValid() ) {
                    functionCallConfigs.add( functionCallConfig );
                }
            }
        }

        return functionCallConfigs;
    }
}
