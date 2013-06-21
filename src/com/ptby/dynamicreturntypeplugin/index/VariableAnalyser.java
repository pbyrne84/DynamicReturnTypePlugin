package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;

import java.util.Collection;
import java.util.Collections;

public class VariableAnalyser {
    public static final String VARIABLE_PATTERN = "(#M#C.*):(.*):(.*)";
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


    public boolean verifySignatureIsVariableCall( String signature ) {
        boolean matches = signature.matches( VARIABLE_PATTERN );
        return matches;
    }


    public Collection<? extends PhpNamedElement> getClassNameFromFieldLookup( String signature, Project project ) {
        String[] split = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        if( split.length < 3 ) {
            return Collections.emptySet();
        }

        String variableSignature = split[ 0 ];
        String calledMethod = split[ 1 ];
        String passedType = split[ split.length - 1 ];

        if ( !validateCall( phpIndex, variableSignature, calledMethod ) ) {
            return originalCallAnalyzer.getMethodCallReturnType( phpIndex, variableSignature.substring( 4 ), calledMethod );
        }

        if ( classConstantAnalyzer.verifySignatureIsClassConstant( passedType ) ) {
            String classNameFromConstantLookup = classConstantAnalyzer
                    .getClassNameFromConstantLookup( passedType, project );

            return phpIndex.getAnyByFQN( classNameFromConstantLookup );
        }

        return phpIndex.getAnyByFQN( passedType );
    }


    private boolean validateCall( PhpIndex phpIndex, String variableSignature, String calledMethod ) {
        String cleanedVariableSignature = variableSignature.substring( 2 );
        Collection<? extends PhpNamedElement> fieldElements = phpIndex
                .getBySignature( cleanedVariableSignature, null, 0 );

        if ( methodCallValidator
                .validateCallMatchesConfig( phpIndex, calledMethod, cleanedVariableSignature, fieldElements ) ) {
            return true;
        }

        return false;
    }
}
