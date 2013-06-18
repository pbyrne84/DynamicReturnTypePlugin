package com.ptby.dynamicreturntypeplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiElementBase;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;

public class CallReturnTypeCalculator {


    private final ClassConstantAnalyzer classConstantAnalyzer;


    public CallReturnTypeCalculator() {
        classConstantAnalyzer = new ClassConstantAnalyzer();
    }


    public String calculateTypeFromMethodParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        PsiElement[] parameters = methodReference.getParameters();
        return calculateTypeFromParameter( methodReference, parameterIndex, parameters );
    }


    public String calculateTypeFromFunctionParameter( FunctionReferenceImpl functionReference, int parameterIndex ) {
        PsiElement[] parameters = functionReference.getParameters();
        return calculateTypeFromParameter( functionReference, parameterIndex, parameters );
    }


    private String calculateTypeFromParameter( PsiElementBase elementBase, int parameterIndex, PsiElement[] parameters ) {
        if ( parameters.length <= parameterIndex ) {
            return null;
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                if ( type.toString().equals( "string" ) ) {
                    return cleanClassText( elementBase, element );
                } else if ( type.toString().matches( "#K#C(.*)\\.(.*)\\|\\?" ) ) {
                    return classConstantAnalyzer.castClassConstantToPhpType( elementBase, element, type.toString() );
                }

                for ( String singleType : type.getTypes() ) {
                    return singleType.substring( 3 );
                }
            }
        }

        return null;
    }



   private  String cleanClassText( PsiElementBase elementBase, PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" ) ) {
            return null;
        }

        String classWithoutQuotes = potentialClassName.replaceAll( "(\"|')", "" );
        return classWithoutQuotes;
    }
}