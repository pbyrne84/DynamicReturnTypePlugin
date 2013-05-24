package com.ptby.dynamicreturntypeplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiElementBase;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.Collection;

public class CallReturnTypeCaster {
    public CallReturnTypeCaster() {
    }


    public PhpType calculateTypeFromMethodParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        PsiElement[] parameters = methodReference.getParameters();
        return calculateTypeFromParameter( methodReference, parameterIndex, parameters );
    }


    public PhpType calculateTypeFromFunctionParameter( FunctionReferenceImpl functionReference, int parameterIndex ) {
        PsiElement[] parameters = functionReference.getParameters();
        return calculateTypeFromParameter( functionReference, parameterIndex, parameters );
    }


    private PhpType calculateTypeFromParameter( PsiElementBase elementBase, int parameterIndex, PsiElement[] parameters ) {
        if ( parameters.length <= parameterIndex ) {
            return null;
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                if ( type.toString().equals( "string" ) ) {
                    return castStringToPhpType( elementBase, element );
                } else if ( type.toString().matches( "#K#C(.*)\\.(.*)\\|\\?" ) ) {
                    return castClassConstantToPhpType( elementBase, element, type.toString() );
                }

                return type;
            }
        }

        return null;
    }


    private PhpType castClassConstantToPhpType( PsiElementBase elementBase, PsiElement element, String classConstant ) {
        String[] constantParts = classConstant.split( "(#K#C|\\.|\\|\\?)" );
        if ( constantParts.length < 3 ) {
            return null;
        }

        String className = constantParts[ 1 ];
        String constantName = constantParts[ 2 ];

        PhpIndex phpIndex = PhpIndex.getInstance( elementBase.getProject() );
        Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN( className );
        for ( PhpClass phpClass : classesByFQN ) {
            Collection<Field> fields = phpClass.getFields();
            for ( Field field : fields ) {
                if ( field.isConstant() && field.getName().equals( constantName ) ) {
                    PsiElement defaultValue = field.getDefaultValue();
                    if ( defaultValue == null ) {
                        return null;
                    }
                    String constantText = defaultValue.getText();
                    if ( constantText.equals( "__CLASS__" ) ) {
                        PhpType phpType = new PhpType();
                        phpType.add( className );
                        return phpType;
                    }
                }
            }
        }

        return null;
    }


   private  PhpType castStringToPhpType( PsiElementBase elementBase, PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" ) ) {
            return null;
        }

        String classWithoutQuotes = potentialClassName.replaceAll( "(\"|')", "" );
        PhpIndex phpIndex = PhpIndex.getInstance( elementBase.getProject() );
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN( classWithoutQuotes );

        if ( phpClasses.size() == 0 ) {
            return null;
        }

        PhpType phpType = new PhpType();
        phpType.add( classWithoutQuotes );
        return phpType;
    }
}