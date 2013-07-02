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


    public ParameterType calculateTypeFromParameter( int parameterIndex, PsiElement[] parameters ) {
        if ( parameters.length <= parameterIndex ) {
            return new ParameterType( null );
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                if ( type.toString().equals( "string" ) ) {
                    return new ParameterType( cleanClassText( element ) );
                } else if ( classConstantAnalyzer.verifySignatureIsClassConstant( type.toString() ) ) {
                    return new ParameterType( type.toString() );
                }

                for ( String singleType : type.getTypes() ) {
                    if ( singleType.substring( 0, 1 ).equals( "\\" ) ) {
                        return new ParameterType( "#C" + singleType );
                    }
                    return new ParameterType( singleType.substring( 3 ) );
                }
            }
        }

        return new ParameterType( null );
    }


    private String cleanClassText( PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" ) ) {
            return null;
        }

        return potentialClassName.replaceAll( "(\"|')", "" );
    }
}
