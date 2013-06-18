package com.ptby.dynamicreturntypeplugin.config;

import java.util.ArrayList;

public class FunctionCallConfigList extends ArrayList<FunctionCallConfig> {
    public FunctionCallConfigList( FunctionCallConfig... functionCallConfigs ) {
        for ( FunctionCallConfig functionCallConfig : functionCallConfigs ) {
            add( functionCallConfig );
        }
    }
}
