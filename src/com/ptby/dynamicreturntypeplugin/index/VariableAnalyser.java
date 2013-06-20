package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;

import java.util.Collection;

public class VariableAnalyser {
    //#M#C\JE\Match\Solr\Advert\SolrAdvertRetrieverTest:getFullMock:#K#C\JE\Match\Solr\Advert\AdvertBoardLocationTermsCalculator.CLASS_NAME|?
    public static final String VARIABLE_PATTERN = "(#M#C.*):(.*):(.*)";
    private final ConfigAnalyser configAnalyser;
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final MethodCallValidator methodCallValidator;


    public VariableAnalyser( ConfigAnalyser configAnalyser, ClassConstantAnalyzer classConstantAnalyzer ) {
        this.configAnalyser = configAnalyser;
        this.classConstantAnalyzer = classConstantAnalyzer;
        this.methodCallValidator = new MethodCallValidator( configAnalyser );
    }



    static public String packageForGetTypeResponse( String intellijReference, String methodName, String returnType ) {
        return intellijReference + ":" + methodName + ":" + returnType;
    }


    public boolean verifySignatureIsVariableCall( String signature ) {
        boolean matches = signature.matches( VARIABLE_PATTERN );
        return matches;
    }


    public String getClassNameFromFieldLookup( String signature, Project project ) {
        String[] split = signature.split( ":" );
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        String variableSignature = split[ 0 ];
        String calledMethod = split[ 1 ];
        String passedType = split[ split.length - 1 ];

        if ( !validateCall( phpIndex, variableSignature, calledMethod ) ) {
            return null;
        }

        if ( classConstantAnalyzer.verifySignatureIsClassConstant( passedType ) ) {
            String classNameFromConstantLookup = classConstantAnalyzer
                    .getClassNameFromConstantLookup( passedType, project );

            return classNameFromConstantLookup;
        }

        return locateType(   passedType );
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

    private String locateType( String passedType ) {
        return passedType.substring( 2  );


    }

}
