package com.ptby.dynamicreturntypeplugin.gettype;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner;

import java.io.IOException;

public class GetTypeResponseFactory {

    private final ConfigAnalyser configAnalyser;
    private final MethodCallReturnTypeScanner methodCallReturnTypeScanner;
    private final FunctionCallReturnTypeScanner functionCallReturnTypeScanner;


    public GetTypeResponseFactory( ConfigAnalyser configAnalyser,
                                   MethodCallReturnTypeScanner methodCallReturnTypeScanner,
                                   FunctionCallReturnTypeScanner functionCallReturnTypeScanner ) {
        this.configAnalyser = configAnalyser;
        this.methodCallReturnTypeScanner = methodCallReturnTypeScanner;
        this.functionCallReturnTypeScanner = functionCallReturnTypeScanner;
    }


    public GetTypeResponse createDynamicReturnType( PsiElement psiElement ) throws IOException {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            return createMethodResponse( ( MethodReferenceImpl ) psiElement );
        } else if ( PlatformPatterns.psiElement( PhpElementTypes.FUNCTION_CALL ).accepts( psiElement ) ) {
            return createFunctionResponse( ( FunctionReferenceImpl ) psiElement );
        }

        return new GetTypeResponse( null );
    }


    private GetTypeResponse createMethodResponse( MethodReferenceImpl classMethod ) {
        return methodCallReturnTypeScanner.getTypeFromMethodCall(
                configAnalyser.getCurrentClassMethodConfigs( classMethod.getProject() ), classMethod
        );
    }


    private GetTypeResponse createFunctionResponse( FunctionReferenceImpl functionReference ) {
        return functionCallReturnTypeScanner.getTypeFromFunctionCall(
                configAnalyser.getCurrentFunctionCallConfigs( functionReference.getProject() ), functionReference
        );
    }
}
