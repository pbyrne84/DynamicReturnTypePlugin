package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.EmptyIcon;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * As I haven't found a way to validate whether calls involving inheritance are valid with using the php index
 * calls with duplicate method names can get overridden by the loose getType call so this puts them back into their original state.
 */
public class OriginalCallAnalyzer {

    public Collection<? extends PhpNamedElement> getFieldInstanceOriginalReturnType( PhpIndex phpIndex,
                                                                                     CustomMethodCallSignature customMethodCallSignature,
                                                                                     Project project) {
        Collection<? extends PhpNamedElement> methodSignatures = phpIndex
                .getBySignature( customMethodCallSignature.getClassName(), null, 0 );

        if ( methodSignatures.size() == 0 ) {
            return Collections.emptySet();
        }

        FieldImpl field = ( FieldImpl ) methodSignatures.iterator().next();
        String classToFindOrigalTurnTypeOf = field.getType().toString();
        Collection<? extends PhpNamedElement> type;
        if ( null != ( type = getMethodCallReturnType( phpIndex, classToFindOrigalTurnTypeOf, customMethodCallSignature.getMethod(), project ) ) ) {
            return type;
        }


        return methodSignatures;
    }


    public Collection<? extends PhpNamedElement> getMethodCallReturnType( PhpIndex phpIndex,
                                                                          String className,
                                                                          String calledMethod,
                                                                          Project project ) {

        Method method;
        Collection<PhpClass> anyByFQN = phpIndex.getAnyByFQN( className );
        for ( PhpClass phpClass : anyByFQN ) {
            if ( null != ( method = findClassMethodByName( phpClass, calledMethod ) ) ) {
                String returnType = method.getType().toString();
                Collection<PhpClass> registeredTypes = phpIndex.getAnyByFQN( returnType );

                if( registeredTypes.size() > 0 ){
                    return registeredTypes;
                }

                Collection<PhpNamedElement> primitiveList = new ArrayList<PhpNamedElement>( );
                primitiveList.add( new LocalClassImpl( method.getType(), project ) );
                return primitiveList;
            }
        }

        return null;
    }


    private Method findClassMethodByName( PhpClass phpClass, String methodName ) {
        for ( Method method : phpClass.getMethods() ) {
            if ( method.getName().equals( methodName ) ) {
                return method;
            }
        }

        return null;
    }


  }


