package com.ptby.dynamicreturntypeplugin;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.ptby.dynamicreturntypeplugin.config.ConfigState;
import com.ptby.dynamicreturntypeplugin.config.ConfigStateContainer;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponseFactory;
import com.ptby.dynamicreturntypeplugin.index.ClassAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class DynamicReturnTypeProvider implements PhpTypeProvider2 {

    public static final char PLUGIN_IDENTIFIER_KEY = 'Ђ';
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final ClassAnalyzer classAnalyzer;
    private final GetTypeResponseFactory getTypeResponseFactory;
    private com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );
    private FieldReferenceAnalyzer fieldReferenceAnalyzer;
    private VariableAnalyser variableAnalyser;


    public DynamicReturnTypeProvider() {
        ConfigState configState = ConfigStateContainer.getConfigState();

        ConfigAnalyser configAnalyser = configState.getConfigAnalyser();
        fieldReferenceAnalyzer = new FieldReferenceAnalyzer( configAnalyser );
        classConstantAnalyzer  = new ClassConstantAnalyzer();
        variableAnalyser       = new VariableAnalyser( configAnalyser, classConstantAnalyzer );
        classAnalyzer          = new ClassAnalyzer( configAnalyser );

        getTypeResponseFactory = createGetTypeResponseFactory( configAnalyser );
    }


    private GetTypeResponseFactory createGetTypeResponseFactory( ConfigAnalyser configAnalyser ) {
        CallReturnTypeCalculator callReturnTypeCalculator = new CallReturnTypeCalculator();
        FunctionCallReturnTypeScanner functionCallReturnTypeScanner = new FunctionCallReturnTypeScanner( callReturnTypeCalculator );
        MethodCallReturnTypeScanner methodCallReturnTypeScanner = new MethodCallReturnTypeScanner( callReturnTypeCalculator );

        return new GetTypeResponseFactory(
                configAnalyser, methodCallReturnTypeScanner, functionCallReturnTypeScanner
        );
    }


    @Override
    public char getKey() {
        return PLUGIN_IDENTIFIER_KEY;
    }


    public String getType( PsiElement psiElement ) {
        try {
            try {
                GetTypeResponse dynamicReturnType = getTypeResponseFactory.createDynamicReturnType( psiElement );
                if ( dynamicReturnType.isNull() ) {
                    return null;
                }

                return dynamicReturnType.toString();
            } catch ( MalformedJsonException e ) {
                logger.warn( "MalformedJsonException", e );
            } catch ( JsonSyntaxException e ) {
                logger.warn( "JsonSyntaxException", e );
            } catch ( IOException e ) {
                logger.warn( "IOException", e );
            }
        } catch ( Exception e ) {
            if ( !( e instanceof ProcessCanceledException ) ) {
                logger.error( "Exception", e );
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    public Collection<? extends PhpNamedElement> getBySignature( String signature, Project project ) {
        SignatureMatcher signatureMatcher = new SignatureMatcher();

        PhpIndex phpIndex = PhpIndex.getInstance( project );
        if ( signatureMatcher.verifySignatureIsClassConstantFunctionCall( signature ) ) {
            return phpIndex.getAnyByFQN(
                    classConstantAnalyzer.getClassNameFromConstantLookup( signature, project )
            );
        } else if ( signatureMatcher.verifySignatureIsFieldCall( signature ) ) {
            return fieldReferenceAnalyzer.getClassNameFromFieldLookup( signature, project );
        } else if ( signatureMatcher.verifySignatureIsMethodCall( signature ) ) {
            return variableAnalyser.getClassNameFromVariableLookup( signature, project );
        }

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
