package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiElementBase;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.Collection;

public class ClassConstantAnalyzer {

    public String castClassConstantToPhpType( PsiElementBase elementBase, PsiElement element, String classConstant ) {
        return castClassConstantToPhpType(classConstant,  elementBase.getProject() );
    }


    public String castClassConstantToPhpType( String classConstant, Project project ) {
        String[] constantParts = classConstant.split( "(#K#C|\\.|\\|\\?)" );
        if ( constantParts.length < 3 ) {
            return null;
        }

        String className = constantParts[ 1 ];
        String constantName = constantParts[ 2 ];

        PhpIndex phpIndex = PhpIndex.getInstance( project  );
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
                        return className;
                    }
                }
            }
        }

        return null;
    }

}
