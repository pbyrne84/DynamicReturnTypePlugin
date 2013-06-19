package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
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


    public FieldReferenceAnalyzer( ConfigAnalyser configAnalyser) {
        this.configAnalyser = configAnalyser;
    }


    static public String packageForGetTypeResponse( String intellijReference, String methodName, String returnType ){
        return intellijReference + ":" + methodName + ":" + returnType;
    }


    public boolean verifySignatureIsFieldCall( String signature ) {
        System.out.println( signature );
        boolean matches = signature.matches( FIELD_CALL_PATTERN );
        System.out.println( matches );
        return matches;
    }


    public String getClassNameFromFieldLookup( String signature, Project project){
        String[] split   = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        String fieldSignature = split[ 0 ];
        String calledMethod   = split[ 1 ];
        String passedType     = split[ 2 ];

        Collection<? extends PhpNamedElement> fieldElements = phpIndex.getBySignature( fieldSignature, null, 0 );
        for ( PhpNamedElement fieldElement : fieldElements ) {
            DynamicReturnTypeConfig currentConfig = this.configAnalyser.getCurrentConfig();
            PhpType type = fieldElement.getType();
            for ( ClassMethodConfig classMethodConfig : currentConfig.getClassMethodConfigs() ) {
                if( classMethodConfig.methodCallMatches( type.toString(), calledMethod ) ){
                    return passedType;
                }
            }
        }

        return "";
    }
}
