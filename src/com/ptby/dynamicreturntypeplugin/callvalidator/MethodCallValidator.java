package com.ptby.dynamicreturntypeplugin.callvalidator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.ClassReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.ptby.dynamicreturntypeplugin.json.JsonConfigurationChangeListener;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MethodCallValidator implements JsonConfigurationChangeListener {
    private Map<String,Boolean> validMethodCallCache = new HashMap<String, Boolean>();


    public MethodCallValidator() {
    }


    public boolean isValidMethodCall( MethodReferenceImpl methodReference, ClassMethodConfig classMethodConfig ) {
        boolean isCorrectMethodName = methodReference.getName().equals( classMethodConfig.getMethodName() );
        if ( !isCorrectMethodName ) {
            return false;
        }

        PhpExpression classReference = methodReference.getClassReference();
        if ( classReference instanceof VariableImpl ) {
            return validateAgainstVariableReference( methodReference, classMethodConfig, ( VariableImpl ) classReference );
        } else if ( classReference instanceof FieldReferenceImpl ) {
            //return validateAgainstFieldReference( classMethodConfig, ( FieldReferenceImpl ) classReference );
            return true;
        } else if ( classReference instanceof ClassReferenceImpl ) {
            return validateAgainstClassReference( methodReference, classMethodConfig, ( ClassReferenceImpl ) classReference );
        }

        return false;

    }


    private boolean validateAgainstClassReference( MethodReferenceImpl methodReference,
                                                   ClassMethodConfig classMethodConfig,
                                                   ClassReferenceImpl classReference ) {
        if ( classReference.getFQN() == null ) {
            return false;
        }

        return classReference.getFQN().equals( classMethodConfig.getFqnClassName() ) &&
                methodReference.getName()
                               .equals( classMethodConfig
                                       .getMethodName()
                               );

    }


    private boolean validateAgainstVariableReference( MethodReferenceImpl methodReference,
                                                      ClassMethodConfig classMethodConfig,
                                                      VariableImpl variableImpl ) {
        PhpType methodPhpType = variableImpl.getType();
     /*   if ( methodPhpType.toString().equals( classMethodConfig.getFqnClassName() ) ) {
            return true;
        }

*/
        if( validMethodCallCache.containsKey( methodPhpType.toString() ) ){
            return validMethodCallCache.get(  methodPhpType.toString() );
        }

        boolean hasSuperClass = methodPhpType
                .findSuper(  classMethodConfig.getFqnClassName(), methodPhpType.toString(), PhpIndex
                        .getInstance( methodReference.getProject() )
                );

       // validMethodCallCache.put( methodPhpType.toString(), hasSuperClass );
        return hasSuperClass;
    }


    /**
     * Cannot seem to be able to get a fields type without hitting the index so am going to have to add deferral
     * logic now
     *
     * @param classMethodConfig
     * @param fieldReference
     * @return
     */
    private boolean validateAgainstFieldReference( ClassMethodConfig classMethodConfig,
                                                   FieldReferenceImpl fieldReference ) {

        PhpType fieldReferenceType = fieldReference.getType();
        String rawReference = fieldReferenceType.toString();

        if ( rawReference.equals( classMethodConfig.getFqnClassName() ) ) {
            return true;
        }

        int endIndex = rawReference.lastIndexOf( '|' );
        if ( endIndex == -1 ) {
            return false;
        }
        String[] split = rawReference.substring( 0, endIndex ).split( "(#P#C|\\.)" );

        if ( split.length < 3 ) {
            return false;
        }

        return true;
    }


    @Override
    public void notifyJsonFileHasChanged( VirtualFile virtualFile ) {
        validMethodCallCache = new HashMap<String, Boolean>();

    }
}