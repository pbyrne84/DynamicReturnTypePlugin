package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;

import java.util.Collection;

/**
 * I cannot seem to be able to find the type from a field without looking at the index so final validation on whether to actually ovveride
 * has to be done later
 */
public class FieldReferenceAnalyzer {
    //#P#C\AdvertsOfDirectEmployersView.oTaskData|?
    public static final String FIELD_CALL_PATTERN = "(#P#C.*):(.*):(.*)";
    private final ConfigAnalyser configAnalyser;
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final OriginalCallAnalyzer originalCallAnalyzer;
    private final MethodCallValidator methodCallValidator;


    public FieldReferenceAnalyzer( ConfigAnalyser configAnalyser ) {
        this.configAnalyser = configAnalyser;
        classConstantAnalyzer = new ClassConstantAnalyzer();
        originalCallAnalyzer = new OriginalCallAnalyzer();
        methodCallValidator = new MethodCallValidator( configAnalyser );
    }


    static public String packageForGetTypeResponse( String intellijReference, String methodName, String returnType ) {
        return intellijReference + ":" + methodName + ":" + returnType;
    }


    public boolean verifySignatureIsFieldCall( String signature ) {
        boolean matches = signature.matches( FIELD_CALL_PATTERN );
        return matches;
    }


    public Collection<? extends PhpNamedElement> getClassNameFromFieldLookup( String signature, Project project ) {
        String[] split = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        String fieldSignature = split[ 0 ];
        String calledMethod = split[ 1 ];
        String passedType = split[ 2 ];

        String type = locateType( phpIndex, fieldSignature, calledMethod, passedType );
        if ( type == null ) {
            return originalCallAnalyzer
                    .getFieldInstanceOriginalReturnType( phpIndex, fieldSignature, calledMethod );
        }

        if ( type.indexOf( "#C" ) == 0 ) {
            return phpIndex.getBySignature( type, null, 0 );
        } else if ( classConstantAnalyzer.verifySignatureIsClassConstant( type ) ) {
            type = classConstantAnalyzer.getClassNameFromConstantLookup( type, project );
        }
        return phpIndex.getAnyByFQN( type );
    }


    private String locateType( PhpIndex phpIndex, String fieldSignature, String calledMethod, String passedType ) {
        Collection<? extends PhpNamedElement> fieldElements = phpIndex.getBySignature( fieldSignature, null, 0 );
        if( fieldElements.size() == 0 ){
            return null;
        }

        PhpNamedElement fieldElement = fieldElements.iterator().next();
        PhpType type = fieldElement.getType();
        if(!methodCallValidator.validateCallMatchesConfig(  phpIndex, calledMethod, "#C" + type.toString(), fieldElements ) ){
            return null;
        }

        return passedType;
    }
}
