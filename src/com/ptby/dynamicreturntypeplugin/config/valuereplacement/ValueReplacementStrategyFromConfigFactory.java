package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.google.gson.JsonObject;

public class ValueReplacementStrategyFromConfigFactory {

    public ValueReplacementStrategyFromConfigFactory() {
    }


    public ValueReplacementStrategy createFromJson( JsonObject configObject ) {
        if ( configObject.has( "mask" ) ) {
            String mask = configObject.get( "mask" ).getAsString().trim();
            if ( mask.contains( "%" ) ) {
                return new MaskValueReplacementStrategy( mask );
            }
        }

        return new PassthruValueReplacementStrategy();
    }
}
