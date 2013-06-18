package com.ptby.dynamicreturntypeplugin;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class DynamicReturnTypeProvider implements PhpTypeProvider2 {

    private final MethodCallTypeCalculator methodCallTypeCalculator;
    private final CallReturnTypeCalculator callReturnTypeCalculator = new CallReturnTypeCalculator();
    private final ConfigAnalyser configAnalyser;
    private final FunctionCallReturnTypeScanner functionCallReturnTypeScanner;
    private final MethodCallReturnTypeScanner methodCallReturnTypeScanner;
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );


    public DynamicReturnTypeProvider() {
        MethodCallValidator methodCallValidator = new MethodCallValidator();
        configAnalyser = new ConfigAnalyser( methodCallValidator );

        functionCallReturnTypeScanner = new FunctionCallReturnTypeScanner( callReturnTypeCalculator );

        methodCallTypeCalculator = new MethodCallTypeCalculator( methodCallValidator, callReturnTypeCalculator );
        methodCallReturnTypeScanner = new MethodCallReturnTypeScanner( methodCallTypeCalculator );

        classConstantAnalyzer = new ClassConstantAnalyzer();
    }


    @Override
    public char getKey() {
        return 0;
    }


    public String getType( PsiElement psiElement ) {
        try {
            try {
                try {
                    String dynamicReturnType = createDynamicReturnType( psiElement );
                    if ( dynamicReturnType == null ) {
                        return null;
                    }

                    return dynamicReturnType;
                } catch ( MalformedJsonException e ) {
                    logger.warn( "MalformedJsonException", e );
                } catch ( JsonSyntaxException e ) {
                    logger.warn( "JsonSyntaxException", e );
                } catch ( IOException e ) {
                    logger.error( "IOException", e );
                }

            } catch ( IndexNotReadyException e ) {
                logger.error( "IndexNotReadyException", e );
            }
        } catch ( Exception e ) {
            logger.error( "Exception", e );

            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Collection<? extends PhpNamedElement> getBySignature( String type, Project project ) {
        if( classConstantAnalyzer.verifySignatureIsClassConstant( type ) ){
            return PhpIndex.getInstance( project ).getAnyByFQN(
                    classConstantAnalyzer.getClassNameFromConstantLookup( type, project )
            );
        }

        return PhpIndex.getInstance( project ).getAnyByFQN( type );
    }


    private DynamicReturnTypeConfig getDynamicReturnTypeConfig( PsiElement psiElement ) throws IOException {
        DynamicReturnTypeConfig dynamicReturnTypeConfig = configAnalyser.analyseConfig( psiElement.getProject() );
        if ( dynamicReturnTypeConfig == null ) {
            return new DynamicReturnTypeConfig();
        }

        return dynamicReturnTypeConfig;
    }


    private String createDynamicReturnType( PsiElement psiElement ) throws IOException {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;

            List<ClassMethodConfig> classMethodConfigs
                    = getDynamicReturnTypeConfig( psiElement ).getClassMethodConfigs();

            return methodCallReturnTypeScanner.getTypeFromMethodCall( classMethodConfigs, classMethod );
        } else if ( PlatformPatterns.psiElement( PhpElementTypes.FUNCTION_CALL ).accepts( psiElement ) ) {
            FunctionReferenceImpl functionReference = ( FunctionReferenceImpl ) psiElement;

            List<FunctionCallConfig> functionCallConfigs
                    = getDynamicReturnTypeConfig( psiElement ).getFunctionCallConfigs();

            return functionCallReturnTypeScanner.getTypeFromFunctionCall( functionCallConfigs, functionReference
            );
        }

        return null;
    }
}
