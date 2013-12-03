package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl;
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature;

import java.util.Collection;

public class DeferredGlobalFunctionCallSignatureConverter {


    public DeferredGlobalFunctionCallSignatureConverter() {
    }


    public CustomMethodCallSignature convertSignatureToClassSignature( CustomMethodCallSignature signature, Project project ) {
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        String cleanedVariableSignature = signature.getClassName().substring( 2 );
        Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature( cleanedVariableSignature );
        if ( bySignature.size() == 0 ) {
            return signature;
        }

        FunctionImpl firstSignatureMatch = ( FunctionImpl ) bySignature.iterator().next();
        return new CustomMethodCallSignature(
                "#M#C" + firstSignatureMatch.getType(),
                signature.getMethod(),
                signature.getParameter()
        );
    }
}
