package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.intellij.openapi.project.Project;

public class PassthruValueReplacementStrategy implements ValueReplacementStrategy{
    @Override
    public String replaceCalculatedValue( Project project, String currentValue ) {
        if( currentValue == null ){
            return "";
        }

        return currentValue;
    }


    public boolean equals( Object object ){
        return object instanceof PassthruValueReplacementStrategy;

    }
}
