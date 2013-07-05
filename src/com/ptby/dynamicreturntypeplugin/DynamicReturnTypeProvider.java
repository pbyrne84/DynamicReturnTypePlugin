package com.ptby.dynamicreturntypeplugin;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponse;
import com.ptby.dynamicreturntypeplugin.gettype.GetTypeResponseFactory;
import com.ptby.dynamicreturntypeplugin.index.ClassAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.json.JsonFileSystemChangeListener;
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class DynamicReturnTypeProvider implements PhpTypeProvider2 {

    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final JsonFileSystemChangeListener jsonFileSystemChangeListener;
    private final ClassAnalyzer classAnalyzer;
    private final GetTypeResponseFactory getTypeResponseFactory;
    private int currentFileListenerAttempt = 0;
    private com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );
    private FieldReferenceAnalyzer fieldReferenceAnalyzer;
    private VariableAnalyser variableAnalyser;


    public DynamicReturnTypeProvider() {
        ConfigAnalyser configAnalyser = new ConfigAnalyser();

        jsonFileSystemChangeListener = new JsonFileSystemChangeListener();
        jsonFileSystemChangeListener.registerChangeListener( configAnalyser );

        fieldReferenceAnalyzer = new FieldReferenceAnalyzer( configAnalyser );
        classConstantAnalyzer = new ClassConstantAnalyzer();
        variableAnalyser = new VariableAnalyser( configAnalyser, classConstantAnalyzer );
        classAnalyzer = new ClassAnalyzer( configAnalyser );

        getTypeResponseFactory = createGetTyepResponseFactory( configAnalyser );

        attemptToInitialiseFileListener();
    }


    private GetTypeResponseFactory createGetTyepResponseFactory( ConfigAnalyser configAnalyser ) {
        CallReturnTypeCalculator callReturnTypeCalculator = new CallReturnTypeCalculator();
        FunctionCallReturnTypeScanner functionCallReturnTypeScanner = new FunctionCallReturnTypeScanner( callReturnTypeCalculator );
        MethodCallReturnTypeScanner methodCallReturnTypeScanner = new MethodCallReturnTypeScanner( callReturnTypeCalculator );

        return new GetTypeResponseFactory(
                configAnalyser, methodCallReturnTypeScanner, functionCallReturnTypeScanner
        );
    }


    private void attemptToInitialiseFileListener() {
        int maxFileListenerInitialisationAttempts = 5;
        if ( ++currentFileListenerAttempt == maxFileListenerInitialisationAttempts ) {
            return;
        }

        java.awt.EventQueue.invokeLater( new Runnable() {
            public void run() {
                DataContext dataContext = DataManager.getInstance().getDataContext();
                Project project = ( Project ) dataContext.getData( DataConstants.PROJECT );
                if ( project == null ) {
                    attemptToInitialiseFileListener();
                    return;
                }

                jsonFileSystemChangeListener.setCurrentProject( project );
            }
        }
        );
    }


    @Override
    public char getKey() {
        return 'Ђ';
    }


    public String getType( PsiElement psiElement ) {
        try {
            try {
                GetTypeResponse dynamicReturnType = getTypeResponseFactory.createDynamicReturnType( psiElement );
                if( dynamicReturnType.isNull() ){
                    return null;
                }

                return dynamicReturnType.toString();
            } catch ( MalformedJsonException e ) {
                logger.warn( "MalformedJsonException", e );
            } catch ( JsonSyntaxException e ) {
                logger.warn( "JsonSyntaxException", e );
            } catch ( IOException e ) {
                logger.warn( "IOException", e );
            }
        } catch ( Exception e ) {
            if ( !( e instanceof ProcessCanceledException ) ) {
                logger.error( "Exception", e );
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    public Collection<? extends PhpNamedElement> getBySignature( String signature, Project project ) {
        PhpIndex phpIndex = PhpIndex.getInstance( project );
        if ( classAnalyzer.verifySignatureIsFieldCall( signature ) ) {
            return classAnalyzer.getClassNameFromClassLookup( signature, project );
        } else if ( classConstantAnalyzer.verifySignatureIsClassConstant( signature ) ) {
            return phpIndex.getAnyByFQN(
                    classConstantAnalyzer.getClassNameFromConstantLookup( signature, project )
            );
        } else if ( fieldReferenceAnalyzer.verifySignatureIsFieldCall( signature ) ) {
            return fieldReferenceAnalyzer.getClassNameFromFieldLookup( signature, project );
        } else if ( variableAnalyser.verifySignatureIsVariableCall( signature ) ) {
            return variableAnalyser.getClassNameFromVariableLookup( signature, project );
        }

        if ( signature.indexOf( "#" ) != 0 ) {
            if ( signature.indexOf( "\\" ) != 0 ) {
                signature = "\\" + signature;
            }

            return phpIndex.getAnyByFQN( signature );
        }

        try {
            return phpIndex.getBySignature( signature, null, 0 );
        } catch ( RuntimeException e ) {
            logger.warn( "Cannot decode " + signature );
            return Collections.emptySet();
        }
    }
}
