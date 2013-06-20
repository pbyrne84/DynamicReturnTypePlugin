package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;

public class ParameterTypeCalculator {
    private final ClassConstantAnalyzer classConstantAnalyzer;


    public ParameterTypeCalculator( ClassConstantAnalyzer classConstantAnalyzer1 ) {
        this.classConstantAnalyzer = classConstantAnalyzer1;
    }


    public String calculateTypeFromParameter( int parameterIndex, PsiElement[] parameters ) {
        if ( parameters.length <= parameterIndex ) {
            return null;
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                if ( type.toString().equals( "string" ) ) {
                    return cleanClassText( element );
                } else if ( classConstantAnalyzer.verifySignatureIsClassConstant( type.toString() ) ) {
                    return type.toString();
                }

                for ( String singleType : type.getTypes() ) {
                    if ( singleType.substring( 0, 1 ).equals( "\\" ) ) {
                        return "#C" + singleType;
                    }
                    return singleType.substring( 3 );
                }
            }
        }

        return null;
    }


    private String cleanClassText( PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" ) ) {
            return null;
        }

        return potentialClassName.replaceAll( "(\"|')", "" );
    }
}
