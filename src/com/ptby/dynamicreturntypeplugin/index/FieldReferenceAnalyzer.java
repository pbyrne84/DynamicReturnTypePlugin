package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.callvalidator.MethodCallValidator;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature;

import java.util.ArrayList;
import java.util.Collection;

/**
 * I cannot seem to be able to find the type from a field without looking at the index so final validation on whether to actually ovveride
 * has to be done later
 */
public class FieldReferenceAnalyzer {
    //#P#C\AdvertsOfDirectEmployersView.oTaskData|?
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



    public Collection<? extends PhpNamedElement> getClassNameFromFieldLookup( CustomMethodCallSignature customMethodCallSignature, Project project ) {
        PhpIndex phpIndex = PhpIndex.getInstance( project );

        String type = locateType( phpIndex, project, customMethodCallSignature );

        if ( type == null ) {
            return originalCallAnalyzer
                    .getFieldInstanceOriginalReturnType( phpIndex, customMethodCallSignature, project );
        }

        if ( type.contains( "[]" ) ) {
            Collection<PhpNamedElement> customList = new ArrayList<PhpNamedElement>( );
            customList.add( new LocalClassImpl( new PhpType().add( type ), project ) );
            return customList;
        }

        if ( type.indexOf( "#C" ) == 0 ) {
            return phpIndex.getBySignature( type, null, 0 );
        } else if ( classConstantAnalyzer.verifySignatureIsClassConstant( type ) ) {
            type = classConstantAnalyzer.getClassNameFromConstantLookup( type, project );
        }

        return phpIndex.getAnyByFQN( type );
    }


    private String locateType( PhpIndex phpIndex, Project project,  CustomMethodCallSignature customMethodCallSignature ) {
        Collection <? extends PhpNamedElement> fieldElements = phpIndex.getBySignature( customMethodCallSignature.getClassName(), null, 0 );
        if ( fieldElements.size() == 0 ) {
            return null;
        }

        PhpNamedElement fieldElement = fieldElements.iterator().next();
        PhpType type = fieldElement.getType();
        ClassMethodConfig matchingConfig = methodCallValidator
                .getMatchingConfig( phpIndex, project, customMethodCallSignature.getMethod(), "#C" + type.toString(), fieldElements );

        if ( matchingConfig == null ) {
            return null;
        }

        return matchingConfig.formatBeforeLookup( customMethodCallSignature.getParameter() );
    }
}
