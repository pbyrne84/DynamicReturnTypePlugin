package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider;
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

                String singleType = getTypeSignature( type );
                if ( singleType == null ) {
                    return new ParameterType( null );
                }

                if ( singleType.substring( 0, 1 ).equals( "\\" ) ) {
                    return new ParameterType( "#C" + singleType );
                }

                if ( singleType.substring( 0, 5 ).equals( "#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY + "#C\\" ) ) {
                    return new ParameterType( singleType.substring( 2 ) );
                }

                if ( singleType.substring( 0, 7 ).equals( "#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY + "#P#C\\" ) ) {
                    return new ParameterType( singleType.substring( 2 ) );
                }

                if ( singleType.length() < 3 ) {
                    return new ParameterType( null );
                }

                String calculatedType = singleType.substring( 3 );
                return new ParameterType( calculatedType );
            }
        }

        return new ParameterType( null );
    }


    private String getTypeSignature(  PhpType type ){
        String typeSignature = null;
        for( String singleType : type.getTypes() ){
            typeSignature = singleType;
        }

        return typeSignature;
    }


    private String cleanClassText( PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" ) ) {
            return null;
        }

        return potentialClassName.replaceAll( "(\"|')", "" );
    }
}
