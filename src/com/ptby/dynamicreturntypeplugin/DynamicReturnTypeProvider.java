package com.ptby.dynamicreturntypeplugin;

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
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ReturnInitialisedSignatureConverter;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature;
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor;
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class DynamicReturnTypeProvider implements PhpTypeProvider2 {

    public static final char PLUGIN_IDENTIFIER_KEY = 'Ђ';
    public static final String PLUGIN_IDENTIFIER_KEY_STRING = new String( new char[] {PLUGIN_IDENTIFIER_KEY } );
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final GetTypeResponseFactory getTypeResponseFactory;
    private final ReturnInitialisedSignatureConverter returnInitialisedSignatureConverter;
    private final com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );
    private final FieldReferenceAnalyzer fieldReferenceAnalyzer;
    private VariableAnalyser variableAnalyser;


    public DynamicReturnTypeProvider() {
        ConfigState configState = ConfigStateContainer.getConfigState();

        ConfigAnalyser configAnalyser = configState.getConfigAnalyser();
        fieldReferenceAnalyzer = new FieldReferenceAnalyzer( configAnalyser );
        classConstantAnalyzer = new ClassConstantAnalyzer();
        variableAnalyser = new VariableAnalyser( configAnalyser, classConstantAnalyzer );
        returnInitialisedSignatureConverter = new ReturnInitialisedSignatureConverter();
        variableAnalyser = new VariableAnalyser( configAnalyser, classConstantAnalyzer );
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
            GetTypeResponse dynamicReturnType = getTypeResponseFactory.createDynamicReturnType( psiElement );
            if ( dynamicReturnType.isNull() ) {
                return null;
            }

            return dynamicReturnType.toString();
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

        Collection<? extends PhpNamedElement> bySignature = null;
        String lastFqnName = "";
        for ( String chainedSignature : createChainedSignatureList( signature ) ) {
            String newSignature = lastFqnName + chainedSignature;
            bySignature = processSingleSignature( newSignature, project );

            if ( bySignature.iterator().hasNext()  ) {
                lastFqnName = "#M#C" + bySignature.iterator().next().getFQN();
            }
        }

        return bySignature;
    }


    public List<String> createChainedSignatureList( String signature ) {
        int chainedSignatureCount = StringUtils.countMatches( signature, PLUGIN_IDENTIFIER_KEY_STRING ) + 1 ;

        List<String> chainedSignatureList = new ArrayList<String>();
        if ( chainedSignatureCount < 2 ) {
            chainedSignatureList.add( signature );
            return chainedSignatureList;
        }

        int beginIndex = signature.lastIndexOf( PLUGIN_IDENTIFIER_KEY_STRING );
        String cleanedSignature = signature.substring( beginIndex + 1 );

        int currentStringPos = 0;
        int currentOrdinalIncrement = 3;
        for ( int i = 0; i < chainedSignatureCount; i++ ) {
            int nextSignatureStart = StringUtils.ordinalIndexOf( cleanedSignature, ":", currentOrdinalIncrement );
            String subSignature;
            if ( nextSignatureStart != -1  ) {
                subSignature = cleanedSignature.substring( currentStringPos, nextSignatureStart );
            }else{
                subSignature = cleanedSignature.substring( currentStringPos );
            }

            chainedSignatureList.add( subSignature );
            currentStringPos = nextSignatureStart;
            currentOrdinalIncrement = currentOrdinalIncrement + 2;
            if ( nextSignatureStart == -1  ) {
                return chainedSignatureList;
            }


        }

        return chainedSignatureList;
    }


    private Collection<? extends PhpNamedElement> processSingleSignature( String signature, Project project ) {
        Collection<? extends PhpNamedElement> bySignature;CustomSignatureProcessor customSignatureProcessor = new CustomSignatureProcessor(
                returnInitialisedSignatureConverter,
                classConstantAnalyzer,
                fieldReferenceAnalyzer,
                variableAnalyser
        );

        bySignature = customSignatureProcessor
                .getBySignature( signature, project );


        return bySignature;
    }


}
