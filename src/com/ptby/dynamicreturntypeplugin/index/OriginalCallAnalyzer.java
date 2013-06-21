package com.ptby.dynamicreturntypeplugin.index;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl;

import java.util.Collection;
import java.util.Collections;


/**
 * As I haven't found a way to validate whether calls involving inheritance are valid with using the php index
 * calls with duplicate method names can get overridden by the loose getType call so this puts them back into their original state.
 */
public class OriginalCallAnalyzer {

    public Collection<? extends PhpNamedElement> getFieldInstanceOriginalReturnType( PhpIndex phpIndex,
                                                                                     String originalCallSignature,
                                                                                     String calledMethod ) {
        Collection<? extends PhpNamedElement> methodSignatures = phpIndex
                .getBySignature( originalCallSignature, null, 0 );

        if ( methodSignatures.size() == 0 ) {
            return Collections.emptySet();
        }

        FieldImpl field = ( FieldImpl ) methodSignatures.iterator().next();
        String classToFindOrigalTurnTypeOf = field.getType().toString();
        Collection<? extends PhpNamedElement> type;
        if ( null != ( type = getMethodCallReturnType( phpIndex, classToFindOrigalTurnTypeOf, calledMethod ) ) ) {
            return type;
        }


        return methodSignatures;
    }


    private Method findClassMethodByName( PhpClass phpClass, String methodName ) {
        for ( Method method : phpClass.getMethods() ) {
            if ( method.getName().equals( methodName ) ) {
                return method;
            }
        }

        return null;
    }


    public Collection<? extends PhpNamedElement> getMethodCallReturnType( PhpIndex phpIndex,
                                                                          String className,
                                                                          String calledMethod ) {

        Method method;
        Collection<PhpClass> anyByFQN = phpIndex.getAnyByFQN( className );
        for ( PhpClass phpClass : anyByFQN ) {
            if ( null != ( method = findClassMethodByName( phpClass, calledMethod ) ) ) {
                return phpIndex.getAnyByFQN( method.getType().toString() );
            }
        }

        return null;
    }

}


