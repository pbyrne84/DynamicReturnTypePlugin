package com.ptby.dynamicreturntypeplugin.callvalidator;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
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
            PhpType fieldElementType = fieldElement.getType();
            for ( ClassMethodConfig classMethodConfig : currentConfig.getClassMethodConfigs() ) {
                if ( classMethodConfig.methodCallMatches( fieldElementType.toString(), calledMethod ) ) {
                    return true;
                }

                if ( classMethodConfig.equalsMethodName( calledMethod ) ) {
                    String actualFqnClassName = cleanedVariableSignature.substring( 2 );
                    String expectedFqnClassName = classMethodConfig.getFqnClassName();

                    boolean hasSuperClass = PhpType.findSuper( expectedFqnClassName, actualFqnClassName, phpIndex );
                    if ( hasSuperClass ) {
                        return true;
                    }
                }
            }


        }
        return false;
    }


    public boolean validateCallMatchesConfig( PhpIndex phpIndex,String method ,String classSignature ) {
        String cleanedClassSignature = classSignature.substring( 2 );
        DynamicReturnTypeConfig currentConfig = configAnalyser.getCurrentConfig();

        for ( ClassMethodConfig classMethodConfig : currentConfig.getClassMethodConfigs() ) {
            if ( classMethodConfig.methodCallMatches( cleanedClassSignature, method ) ) {
                return true;
            }

            if ( classMethodConfig.equalsMethodName( method ) ) {
                String expectedFqnClassName = classMethodConfig.getFqnClassName();

                boolean hasSuperClass = PhpType.findSuper( expectedFqnClassName, cleanedClassSignature, phpIndex );
                if ( hasSuperClass ) {
                    return true;
                }
            }
        }

        return false;
    }
}