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
    private final com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );
    private ReturnInitialisedSignatureConverter returnInitialisedSignatureConverter;
    private ClassConstantAnalyzer classConstantAnalyzer;
    private FieldReferenceAnalyzer fieldReferenceAnalyzer;
    private VariableAnalyser variableAnalyser;


    public CustomSignatureProcessor( ReturnInitialisedSignatureConverter returnInitialisedSignatureConverter,
                                     ClassConstantAnalyzer classConstantAnalyzer,
                                     FieldReferenceAnalyzer fieldReferenceAnalyzer,
                                     VariableAnalyser variableAnalyser ) {
        this.returnInitialisedSignatureConverter = returnInitialisedSignatureConverter;
        this.classConstantAnalyzer = classConstantAnalyzer;
        this.fieldReferenceAnalyzer = fieldReferenceAnalyzer;
        this.variableAnalyser = variableAnalyser;
    }


    public Collection<? extends PhpNamedElement> getBySignature( String signature, Project project ) {
        PhpIndex phpIndex = PhpIndex.getInstance( project );
        CustomMethodCallSignature customMethodCallSignature = CustomMethodCallSignature.OBJECT$.createFromString( signature );
        if ( customMethodCallSignature == null ) {
            return tryFunctionCall( null, signature, phpIndex, project );
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
                            .getClassNameFromConstantLookup(
                                    customMethodCallSignature
                                            .getRawStringSignature(), project
                            )
            );
        } else if ( signatureMatcher.verifySignatureIsFieldCall( customMethodCallSignature ) ) {
            return fieldReferenceAnalyzer.getClassNameFromFieldLookup( customMethodCallSignature, project );
        } else if ( signatureMatcher.verifySignatureIsMethodCall( customMethodCallSignature ) ) {
            return variableAnalyser.getClassNameFromVariableLookup( customMethodCallSignature, project );
        }

        return tryToDeferToDefaultType( customMethodCallSignature, signature, phpIndex );
    }


    private Collection<? extends PhpNamedElement> tryFunctionCall( CustomMethodCallSignature customMethodCallSignature,
                                                                   String signature,
                                                                   PhpIndex phpIndex,
                                                                   Project project ) {
        SignatureMatcher signatureMatcher = new SignatureMatcher();
        if ( signatureMatcher.verifySignatureIsClassConstantFunctionCall( signature ) ) {
            return phpIndex.getAnyByFQN(
                    classConstantAnalyzer.getClassNameFromConstantLookup( signature, project )
            );
        }

        return tryToDeferToDefaultType( customMethodCallSignature, signature, phpIndex );
    }


    private Collection<? extends PhpNamedElement> tryToDeferToDefaultType( CustomMethodCallSignature customMethodCallSignature,
                                                                           String signature,
                                                                           PhpIndex phpIndex ) {
        if ( signature.indexOf( "#" ) != 0 ) {
            if ( signature.indexOf( "\\" ) != 0 ) {
                signature = "\\" + signature;
            }
            return phpIndex.getAnyByFQN( signature );
        }

        signature = cleanConstant( signature );

        try {
            return phpIndex.getBySignature( signature, null, 0 );
        } catch ( RuntimeException e ) {
            String signatureMessage = "function call";
            if ( customMethodCallSignature != null ) {
                signatureMessage = customMethodCallSignature.toString();
            }

            logger.warn(
                    "CustomSignatureProcessor.tryToDeferToDefaultType cannot decode " + signature + "\n" +
                            signatureMessage
            );

            return Collections.emptySet();
        }
    }


    /**
     * Indicates something is wrong from one of the tests in the test environment but no inspections are raised.
     * Just a warning in idea. Will need further looking into.
     *
     * @param signature
     * @return
     */
    private String cleanConstant( String signature ) {
        if ( signature.indexOf( "#K#C" ) == 0 && !signature.contains( "|?" ) ) {
            signature = signature + ".|?";
        }
        return signature;
    }
}
