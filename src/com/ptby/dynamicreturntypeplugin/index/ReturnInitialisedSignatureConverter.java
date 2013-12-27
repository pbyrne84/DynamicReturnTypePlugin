package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl;

import java.util.Collection;

public class ReturnInitialisedSignatureConverter {


    public ReturnInitialisedSignatureConverter() {
    }


    public String convertSignatureToClassSignature( String signature, Project project ) {
        String[] split = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        if( split.length < 3 ) {
            return signature;
        }

        String variableSignature = split[ 0 ];
        String calledMethod = split[ 1 ];
        String passedType = split[ split.length - 1 ];

        String cleanedVariableSignature = variableSignature.substring( 2 );
        Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature( cleanedVariableSignature );
        if ( bySignature.size() == 0 ) {
            return signature;
        }

        FunctionImpl firstSignatureMatch = (FunctionImpl)bySignature.iterator().next();
        return "#M#C" + firstSignatureMatch.getType() +  ":" + calledMethod + ":" + passedType ;
    }
}
