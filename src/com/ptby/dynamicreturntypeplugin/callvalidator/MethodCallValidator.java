package com.ptby.dynamicreturntypeplugin.callvalidator;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;

import java.util.Collection;

public class MethodCallValidator {
    private final ConfigAnalyser configAnalyser;


    public MethodCallValidator( ConfigAnalyser configAnalyser ) {
        this.configAnalyser = configAnalyser;
    }


    public boolean validateCallMatchesConfig( PhpIndex phpIndex,
                                              String calledMethod,
                                              String cleanedVariableSignature,
                                              Collection<? extends PhpNamedElement> fieldElements ) {
        for ( PhpNamedElement fieldElement : fieldElements ) {
            DynamicReturnTypeConfig currentConfig = configAnalyser.getCurrentConfig();
            PhpType type = fieldElement.getType();
            for ( ClassMethodConfig classMethodConfig : currentConfig.getClassMethodConfigs() ) {
                if ( classMethodConfig.methodCallMatches( type.toString(), calledMethod ) ) {
                    return true;
                }

                if ( classMethodConfig.getMethodName().equals( calledMethod ) ) {
                    String varialeFqnClassName = cleanedVariableSignature.substring( 2 );
                    boolean hasSuperClass = fieldElement.getType()
                                                        .findSuper( classMethodConfig
                                                                .getFqnClassName(), varialeFqnClassName, phpIndex
                                                        );
                    if ( hasSuperClass ) {
                        return true;
                    }
                }
            }


        }
        return false;
    }
}