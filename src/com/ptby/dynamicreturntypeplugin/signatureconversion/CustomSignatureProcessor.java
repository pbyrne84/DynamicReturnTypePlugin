package com.ptby.dynamicreturntypeplugin.signatureconversion;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ReturnInitialisedSignatureConverter;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;

import java.util.Collection;
import java.util.Collections;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class CustomSignatureProcessor {
    private ReturnInitialisedSignatureConverter returnInitialisedSignatureConverter;
    private ClassConstantAnalyzer classConstantAnalyzer;
    private FieldReferenceAnalyzer fieldReferenceAnalyzer;
    private VariableAnalyser variableAnalyser;
    private final com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );


    public CustomSignatureProcessor( ReturnInitialisedSignatureConverter returnInitialisedSignatureConverter,
                                     ClassConstantAnalyzer              classConstantAnalyzer,
                                     FieldReferenceAnalyzer             fieldReferenceAnalyzer,
                                     VariableAnalyser                   variableAnalyser                                 ) {
        this.returnInitialisedSignatureConverter = returnInitialisedSignatureConverter;
        this.classConstantAnalyzer = classConstantAnalyzer;
        this.fieldReferenceAnalyzer = fieldReferenceAnalyzer;
        this.variableAnalyser = variableAnalyser;
    }


    public Collection<? extends PhpNamedElement> getBySignature( String signature, Project project ) {
        PhpIndex phpIndex = PhpIndex.getInstance( project );
        CustomMethodCallSignature customMethodCallSignature = CustomMethodCallSignature.createFromString( signature );
        if ( customMethodCallSignature == null ) {
            return tryFunctionCall( signature, phpIndex, project );
        }

        SignatureMatcher signatureMatcher = new SignatureMatcher();
        if ( signatureMatcher.verifySignatureIsDeferredGlobalFunctionCall( customMethodCallSignature ) ||
                signatureMatcher.verifySignatureIsFromReturnInitialiasedLocalObject( customMethodCallSignature ) ) {
            customMethodCallSignature = returnInitialisedSignatureConverter.convertSignatureToClassSignature(
                    customMethodCallSignature, project
            );
        }

        if ( signatureMatcher.verifySignatureIsClassConstantFunctionCall( customMethodCallSignature ) ) {
            return phpIndex.getAnyByFQN(
                    classConstantAnalyzer
                            .getClassNameFromConstantLookup( customMethodCallSignature.getRawStringSignature(), project )
            );
        } else if ( signatureMatcher.verifySignatureIsFieldCall( customMethodCallSignature ) ) {
            return fieldReferenceAnalyzer.getClassNameFromFieldLookup( customMethodCallSignature, project );
        } else if ( signatureMatcher.verifySignatureIsMethodCall( customMethodCallSignature ) ) {
            return variableAnalyser.getClassNameFromVariableLookup( customMethodCallSignature, project );
        }

        return tryToDeferToDefaultType( signature, phpIndex );
    }


    private Collection<? extends PhpNamedElement> tryFunctionCall( String signature, PhpIndex phpIndex, Project project ) {
        SignatureMatcher signatureMatcher = new SignatureMatcher();
        if ( signatureMatcher.verifySignatureIsClassConstantFunctionCall( signature ) ) {
            return phpIndex.getAnyByFQN(
                    classConstantAnalyzer.getClassNameFromConstantLookup( signature, project )
            );
        }

        return tryToDeferToDefaultType( signature, phpIndex );
    }


    private Collection<? extends PhpNamedElement> tryToDeferToDefaultType( String signature, PhpIndex phpIndex ) {
        if ( signature.indexOf( "#" ) != 0 ) {
            if ( signature.indexOf( "\\" ) != 0 ) {
                signature = "\\" + signature;
            }
            return phpIndex.getAnyByFQN( signature );
        }

        try {
            return phpIndex.getBySignature( signature, null, 0 );
        } catch ( RuntimeException e ) {
            logger.warn( "Cannot decode " + signature );
            return Collections.emptySet();
        }
    }
}
