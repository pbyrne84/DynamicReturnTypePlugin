package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

public class PassthruValueReplacementStrategy implements ValueReplacementStrategy{
    @Override
    public String replaceCalculatedValue( String currentValue ) {
        if( currentValue == null ){
            return "";
        }

        return currentValue;
    }


    public boolean equals( Object object ){
        return object instanceof PassthruValueReplacementStrategy;

    }
}
