package com.ptby.dynamicreturntypeplugin;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.ptby.dynamicreturntypeplugin.callvalidator.DeprecatedMethodCallValidator;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer;
import com.ptby.dynamicreturntypeplugin.index.VariableAnalyser;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.json.JsonFileSystemChangeListener;
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner;
import com.ptby.dynamicreturntypeplugin.typecalculation.CallReturnTypeCalculator;
import com.ptby.dynamicreturntypeplugin.typecalculation.MethodCallTypeCalculator;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class DynamicReturnTypeProvider implements PhpTypeProvider2 {

    private final MethodCallTypeCalculator methodCallTypeCalculator;
    private final CallReturnTypeCalculator callReturnTypeCalculator = new CallReturnTypeCalculator();
    private final ConfigAnalyser configAnalyser;
    private final FunctionCallReturnTypeScanner functionCallReturnTypeScanner;
    private final MethodCallReturnTypeScanner methodCallReturnTypeScanner;
    private final ClassConstantAnalyzer classConstantAnalyzer;
    private final JsonFileSystemChangeListener jsonFileSystemChangeListener;
    private com.intellij.openapi.diagnostic.Logger logger = getInstance( "DynamicReturnTypePlugin" );
    private FieldReferenceAnalyzer fieldReferenceAnalyzer;
    private VariableAnalyser variableAnalyser;


    public DynamicReturnTypeProvider() {
        DeprecatedMethodCallValidator deprecatedMethodCallValidator = new DeprecatedMethodCallValidator();
        configAnalyser = new ConfigAnalyser( deprecatedMethodCallValidator );

        functionCallReturnTypeScanner = new FunctionCallReturnTypeScanner( callReturnTypeCalculator );

        methodCallTypeCalculator = new MethodCallTypeCalculator( deprecatedMethodCallValidator, callReturnTypeCalculator );
        methodCallReturnTypeScanner = new MethodCallReturnTypeScanner( methodCallTypeCalculator );


        jsonFileSystemChangeListener = new JsonFileSystemChangeListener();
        jsonFileSystemChangeListener.registerChangeListener( configAnalyser );

        fieldReferenceAnalyzer = new FieldReferenceAnalyzer( configAnalyser );
        classConstantAnalyzer = new ClassConstantAnalyzer();
        variableAnalyser = new VariableAnalyser( configAnalyser, classConstantAnalyzer );


        java.awt.EventQueue.invokeLater( new Runnable() {
            public void run() {
                Project project = null;
           /*     while( project == null ){*/
                    try {
                        Thread.sleep( 100 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }

                    DataContext dataContext = DataManager.getInstance().getDataContext();
                     project = ( Project ) dataContext.getData( DataConstants.PROJECT );
                 /*   if( project != null ){*/
                        jsonFileSystemChangeListener.setCurrentProject( project );
                   /* }*/
            /*    }*/
            }
        }
        );
    }


    @Override
    public char getKey() {
        return 0;
    }


    public String getType( PsiElement psiElement ) {
        try {
            try {
                try {
                    String dynamicReturnType = createDynamicReturnType( psiElement );
                    if ( dynamicReturnType == null ) {
                        return null;
                    }

                    return dynamicReturnType;
                } catch ( MalformedJsonException e ) {
                    logger.warn( "MalformedJsonException", e );
                } catch ( JsonSyntaxException e ) {
                    logger.warn( "JsonSyntaxException", e );
                } catch ( IOException e ) {
                    logger.error( "IOException", e );
                }

            } catch ( IndexNotReadyException e ) {
                logger.error( "IndexNotReadyException", e );
            }
        } catch ( Exception e ) {
            logger.error( "Exception", e );

            e.printStackTrace();
        }

        return null;
    }


    private String createDynamicReturnType( PsiElement psiElement ) throws IOException {
        if ( PlatformPatterns.psiElement( PhpElementTypes.METHOD_REFERENCE ).accepts( psiElement ) ) {
            MethodReferenceImpl classMethod = ( MethodReferenceImpl ) psiElement;

            List<ClassMethodConfig> classMethodConfigs
                    = getDynamicReturnTypeConfig( psiElement ).getClassMethodConfigs();

            String typeFromMethodCall
                    = methodCallReturnTypeScanner.getTypeFromMethodCall( classMethodConfigs, classMethod );

            return typeFromMethodCall;
        } else if ( PlatformPatterns.psiElement( PhpElementTypes.FUNCTION_CALL ).accepts( psiElement ) ) {
            FunctionReferenceImpl functionReference
                    = ( FunctionReferenceImpl ) psiElement;

            List<FunctionCallConfig> functionCallConfigs
                    = getDynamicReturnTypeConfig( psiElement ).getFunctionCallConfigs();

            String typeFromFunctionCall = functionCallReturnTypeScanner
                    .getTypeFromFunctionCall( functionCallConfigs, functionReference
                    );

            return typeFromFunctionCall;
        }

        return null;
    }


    @Override
    public Collection<? extends PhpNamedElement> getBySignature( String type, Project project ) {
        System.out.println("attempted type " + type);
        PhpIndex phpIndex = PhpIndex.getInstance( project );
        if ( classConstantAnalyzer.verifySignatureIsClassConstant( type ) ) {
            return phpIndex.getAnyByFQN(
                    classConstantAnalyzer.getClassNameFromConstantLookup( type, project )
            );
        } else if ( fieldReferenceAnalyzer.verifySignatureIsFieldCall( type ) ) {
            return  fieldReferenceAnalyzer.getClassNameFromFieldLookup( type, project );
        } else if ( variableAnalyser.verifySignatureIsVariableCall( type ) ) {
            return phpIndex.getAnyByFQN(
                    variableAnalyser.getClassNameFromFieldLookup( type, project )
            );

        }


        System.out.println("unfound type " + type);
        return phpIndex.getBySignature( type, null, 0 );
    }


    private DynamicReturnTypeConfig getDynamicReturnTypeConfig( PsiElement psiElement ) throws IOException {
        return configAnalyser.getCurrentConfig();
    }


}
