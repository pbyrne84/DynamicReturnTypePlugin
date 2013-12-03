package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;

import java.util.Collection;
import java.util.Collections;

public class VariableAnalyser {
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final MethodCallValidator methodCallValidator;
    private final OriginalCallAnalyzer originalCallAnalyzer;


    public VariableAnalyser( ConfigAnalyser configAnalyser, ClassConstantAnalyzer classConstantAnalyzer ) {
        this.classConstantAnalyzer = classConstantAnalyzer;
        this.methodCallValidator = new MethodCallValidator( configAnalyser );
        originalCallAnalyzer = new OriginalCallAnalyzer();
    }



    static public String packageForGetTypeResponse( String intellijReference, String methodName, String returnType ) {
        return intellijReference + ":" + methodName + ":" + returnType;
    }


    public Collection<? extends PhpNamedElement> getClassNameFromVariableLookup( String signature, Project project ) {
        String[] split = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        String passedType = "";
        if( split.length == 3 ) {
            passedType = split[ split.length - 1 ];
        }

        String variableSignature = split[ 0 ];
        String calledMethod = split[ 1 ];

        ClassMethodConfig matchingMethodConfig = getMatchingMethodConfig( phpIndex, project, variableSignature, calledMethod );
        if ( matchingMethodConfig == null ) {
            return originalCallAnalyzer.getMethodCallReturnType(
                    phpIndex, variableSignature.substring( 4 ), calledMethod, project
            );
        }

        if ( classConstantAnalyzer.verifySignatureIsClassConstant( passedType ) ) {
            String classNameFromConstantLookup = classConstantAnalyzer
                    .getClassNameFromConstantLookup( passedType, project );

            return phpIndex.getAnyByFQN( classNameFromConstantLookup );

        }

        String createdType = "#C" + matchingMethodConfig.formatUsingStringMask( passedType );
        return phpIndex
                .getBySignature( createdType );
    }


    private ClassMethodConfig getMatchingMethodConfig( PhpIndex phpIndex,Project project, String variableSignature, String calledMethod ) {
        String cleanedVariableSignature = variableSignature.substring( 2 );
        Collection<? extends PhpNamedElement> fieldElements = phpIndex
                .getBySignature( cleanedVariableSignature, null, 0 );

        return methodCallValidator
                .getMatchingConfig( phpIndex, project, calledMethod, cleanedVariableSignature, fieldElements );
    }
}
