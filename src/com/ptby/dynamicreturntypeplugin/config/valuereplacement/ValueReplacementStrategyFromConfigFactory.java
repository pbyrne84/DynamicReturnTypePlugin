package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.google.gson.JsonObject;
import com.intellij.openapi.vfs.VirtualFile;

public class ValueReplacementStrategyFromConfigFactory {

    public ValueReplacementStrategyFromConfigFactory() {
    }


    public ValueReplacementStrategy createFromJson( VirtualFile configFile, JsonObject configObject ) {
        if ( configObject.has( "mask" ) ) {
            String mask = configObject.get( "mask" ).getAsString().trim();
            if ( mask.contains( "%" ) ) {
                return new MaskValueReplacementStrategy( mask );
            }
        }

        return new PassthruValueReplacementStrategy();
    }
}
