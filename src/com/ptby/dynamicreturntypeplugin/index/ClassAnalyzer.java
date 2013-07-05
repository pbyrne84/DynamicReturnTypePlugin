package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;

import java.util.Collection;
import java.util.Collections;

public class ClassAnalyzer {
    private static final String CLASS_CALL_PATTERN = "(#C.*):(.*):(.*)";
    private final ConfigAnalyser configAnalyser;
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final MethodCallValidator methodCallValidator;
    private final OriginalCallAnalyzer originalCallAnalyzer;


    public ClassAnalyzer( ConfigAnalyser configAnalyser ) {
        this.configAnalyser = configAnalyser;
        classConstantAnalyzer = new ClassConstantAnalyzer();
        methodCallValidator = new MethodCallValidator( configAnalyser );
        originalCallAnalyzer = new OriginalCallAnalyzer();
    }


    static public String packageForGetTypeResponse( String intellijReference, String methodName, String returnType ) {
        return intellijReference + ":" + methodName + ":" + returnType;
    }


    public boolean verifySignatureIsFieldCall( String signature ) {
        return signature.matches( CLASS_CALL_PATTERN );
    }


    public Collection<? extends PhpNamedElement> getClassNameFromClassLookup( String signature, Project project ) {
        String[] split = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        if( split.length < 3 ) {
            return Collections.emptySet();
        }

        String classSignature = split[ 0 ];
        String calledMethod = split[ 1 ];
        String passedType = split[ split.length - 1 ];

        ClassMethodConfig matchingConfig = methodCallValidator.getMatchingConfig( phpIndex, calledMethod, classSignature );
        if ( matchingConfig == null ) {
            return originalCallAnalyzer
                    .getMethodCallReturnType( phpIndex, classSignature.substring( 2 ), calledMethod );
        }

        if ( classConstantAnalyzer.verifySignatureIsClassConstant( passedType ) ) {
            String classNameFromConstantLookup = classConstantAnalyzer
                    .getClassNameFromConstantLookup( passedType, project );

            return phpIndex.getClassesByFQN( classNameFromConstantLookup );
        }

        return phpIndex.getBySignature( matchingConfig.formatUsingStringMask( passedType ), null, 0 );
    }
}
