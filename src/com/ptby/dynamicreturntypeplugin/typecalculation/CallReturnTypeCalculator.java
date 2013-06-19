package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;

public class CallReturnTypeCalculator {


    private final ClassConstantAnalyzer classConstantAnalyzer;


    public CallReturnTypeCalculator() {
        classConstantAnalyzer = new ClassConstantAnalyzer();
    }


    public String calculateTypeFromMethodParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof FieldReferenceImpl ) {
            return packageFieldReference( methodReference, parameterIndex );
        }

        if ( classReference instanceof VariableImpl ) {
            return packageVariableReference( methodReference, parameterIndex );

        }

        return calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
    }


    private String packageFieldReference( MethodReferenceImpl methodReference, int parameterIndex ) {

        FieldReferenceImpl fieldReference = ( FieldReferenceImpl ) methodReference.getClassReference();

        String returnType = calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
        String typeAsString = fieldReference.getType().toString();
        String intellijReference = typeAsString.substring( 0, typeAsString.length() - 2 );
        String packagedFieldReference = VariableAnalyser.packageForGetTypeResponse(
                intellijReference, methodReference.getName(), returnType
        );

        return packagedFieldReference;
    }


    private String packageVariableReference( MethodReferenceImpl methodReference, int parameterIndex ) {
        String returnType = calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
        String name = methodReference.getName();
        String[] methodCallParts = methodReference.getSignature().split( "\\." );

        String packagedVariableReference = FieldReferenceAnalyzer.packageForGetTypeResponse(
                methodCallParts[ 0 ], name, returnType
        );

        return packagedVariableReference;
    }


    public String calculateTypeFromFunctionParameter( FunctionReferenceImpl functionReference, int parameterIndex ) {
        return calculateTypeFromParameter( parameterIndex, functionReference.getParameters() );
    }


    private String calculateTypeFromParameter( int parameterIndex, PsiElement[] parameters ) {
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
                    if( singleType.substring( 0,1 ).equals( "\\" ) ){
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