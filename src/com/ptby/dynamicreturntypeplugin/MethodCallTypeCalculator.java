package com.ptby.dynamicreturntypeplugin;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.Collection;

public class MethodCallTypeCalculator {
    public final MethodCallValidator methodCallValidator = new MethodCallValidator();


    public MethodCallTypeCalculator() {
    }


    public PhpType calculateFromMethodCall( ClassMethodConfig classMethodConfig, MethodReferenceImpl methodReference ) {
        if ( methodCallValidator
                .isValidMethodCall( methodReference, classMethodConfig ) ) {
            return calculateTypeFromParameter( methodReference, classMethodConfig.getParameterIndex() );
        }

        return null;
    }


    public PhpType calculateTypeFromParameter( MethodReferenceImpl methodReference, int parameterIndex ) {
        PsiElement[] parameters = methodReference.getParameters();
        if ( parameters.length <=  parameterIndex  ) {
            return null;
        }

        PsiElement element = parameters[ parameterIndex ];
        if ( element instanceof PhpTypedElement ) {
            PhpType type = ( ( PhpTypedElement ) element ).getType();
            if ( !type.toString().equals( "void" ) ) {
                if ( type.toString().equals( "string" ) ) {
                    return castStringToPhpType( methodReference, element );
                }else if( type.toString().matches( "#K#C(.*)\\.(.*)\\|\\?" )){
                    return castClassConstantToPhpType( methodReference, element, type.toString() );
                }

                return type;
            }
        }

        return null;
    }


    private PhpType castClassConstantToPhpType( MethodReferenceImpl methodReference, PsiElement element, String classConstant ) {
        String[] constantParts = classConstant.split( "(#K#C|\\.|\\|\\?)" );
        if ( constantParts.length < 3 ) {
            return null;
        }

        String className = constantParts[ 1 ];
        String constantName = constantParts[ 2 ];

        PhpIndex phpIndex = PhpIndex.getInstance( methodReference.getProject() );
        Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN( className );
        for ( PhpClass phpClass : classesByFQN ) {
            Collection<Field> fields = phpClass.getFields();
            for ( Field field : fields ) {
                if( field.isConstant() && field.getName().equals( constantName )  ){
                    PsiElement defaultValue = field.getDefaultValue();
                    if ( defaultValue == null ) {
                        return null;
                    }
                    String constantText =  defaultValue.getText();
                    if ( constantText.equals( "__CLASS__" ) ){
                        PhpType phpType = new PhpType();
                        phpType.add( className );
                        return phpType;
                    }
                }
            }
        }

        return null;
    }


    private PhpType castStringToPhpType( MethodReferenceImpl methodReference, PsiElement element ) {
        String potentialClassName = element.getText().trim();
        if ( potentialClassName.equals( "" )) {
            return null;
        }

        String classWithoutQuotes = potentialClassName.replaceAll( "(\"|')", "" );
        PhpIndex phpIndex = PhpIndex.getInstance( methodReference.getProject() );
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN( classWithoutQuotes );

        if ( phpClasses.size() == 0  ) {
            return null;
        }

        PhpType phpType = new PhpType();
        phpType.add( classWithoutQuotes );
        return phpType;
    }
}