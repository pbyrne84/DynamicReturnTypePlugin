package com.ptby.dynamicreturntypeplugin.typecalculation;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;

public class CallReturnTypeCalculator {


    private final ClassConstantAnalyzer classConstantAnalyzer;


    public CallReturnTypeCalculator() {
        classConstantAnalyzer = new ClassConstantAnalyzer();
    }


    public String calculateTypeFromMethodParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        if( methodReference.getClassReference() instanceof FieldReferenceImpl ){
            return packageFieldReference( methodReference,  parameterIndex  );
        }

        return calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
    }


    private String packageFieldReference( MethodReferenceImpl methodReference, int parameterIndex ) {

        FieldReferenceImpl classReference = ( FieldReferenceImpl )methodReference.getClassReference();

        String returnType           = calculateTypeFromParameter( parameterIndex, methodReference.getParameters() );
        String typeAsString         = classReference.getType().toString();
        String intellijReference    = typeAsString.substring( 0, typeAsString.length() -2 );
        String packagedFieldReference = FieldReferenceAnalyzer.packageForGetTypeResponse(
                intellijReference, methodReference.getName(), returnType
        );
        return packagedFieldReference;


    }


    public String calculateTypeFromFunctionParameter( FunctionReferenceImpl functionReference, int parameterIndex ) {
        return calculateTypeFromParameter(  parameterIndex, functionReference.getParameters() );
    }


    private String calculateTypeFromParameter(  int parameterIndex, PsiElement[] parameters ) {
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
                    return singleType.substring( 3 );
                }
            }
        }

        return null;
    }



   private  String cleanClassText( PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" ) ) {
            return null;
        }

       return potentialClassName.replaceAll( "(\"|')", "" );
    }
}