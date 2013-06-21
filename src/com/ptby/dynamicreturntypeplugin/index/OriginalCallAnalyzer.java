package com.ptby.dynamicreturntypeplugin.index;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.elements.ExtendsList;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.ImplementsList;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.PhpTraitUseRule;
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
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


    public Collection<? extends PhpNamedElement> getMethodCallReturnType( PhpIndex phpIndex,
                                                                          String className,
                                                                          String calledMethod ) {

        Method method;
        Collection<PhpClass> anyByFQN = phpIndex.getAnyByFQN( className );
        for ( PhpClass phpClass : anyByFQN ) {
            if ( null != ( method = findClassMethodByName( phpClass, calledMethod ) ) ) {
                String returnType = method.getType().toString();
                Collection<PhpClass> registeredTypes = phpIndex.getAnyByFQN( returnType );

                if( registeredTypes.size() > 0 ){
                    return registeredTypes;
                }

                Collection<PhpClass> primitiveList = new ArrayList<PhpClass>( );
                primitiveList.add( new PrimitiveClass( method.getType() ) );
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


    /**
     * There does not seem to be an easy way to get a primitive representation from the index
     */
    class PrimitiveClass implements PhpClass{

        private final PhpType type;


        PrimitiveClass( PhpType type ) {

            this.type = type;
        }


        @Nullable
        @Override
        public ASTNode getNameNode() {
            return null;
        }


        @NotNull
        @Override
        public String getName() {
            return null;
        }


        @Override
        public PsiElement setName( @NonNls @NotNull String s ) throws IncorrectOperationException {
            return null;
        }


        @Nullable
        @Override
        public ItemPresentation getPresentation() {
            return null;
        }


        @NotNull
        @Override
        public CharSequence getNameCS() {
            return null;
        }


        @Nullable
        @Override
        public PhpDocComment getDocComment() {
            return null;
        }


        @Nullable
        @Override
        public PhpDocComment getExtraDoc() {
            return null;
        }


        @Override
        public boolean isInterface() {
            return false;
        }


        @Override
        public boolean isAbstract() {
            return false;
        }


        @Override
        public boolean isFinal() {
            return false;
        }


        @Nullable
        @Override
        public ExtendsList getExtendsList() {
            return null;
        }


        @Nullable
        @Override
        public ImplementsList getImplementsList() {
            return null;
        }


        @Nullable
        @Override
        public String getSuperName() {
            return null;
        }


        @Nullable
        @Override
        public String getSuperFQN() {
            return null;
        }


        @Nullable
        @Override
        public PhpClass getSuperClass() {
            return null;
        }


        @NotNull
        @Override
        public String[] getInterfaceNames() {
            return new String[ 0 ];
        }


        @Override
        public PhpClass[] getImplementedInterfaces() {
            return new PhpClass[ 0 ];
        }


        @Override
        public boolean hasTraitUses() {
            return false;
        }


        @NotNull
        @Override
        public String[] getTraitNames() {
            return new String[ 0 ];
        }


        @Override
        public PhpClass[] getTraits() {
            return new PhpClass[ 0 ];
        }


        @Override
        public PhpClass[] getSupers() {
            return new PhpClass[ 0 ];
        }


        @Override
        public Collection<Field> getFields() {
            return null;
        }


        @Override
        public Field[] getOwnFields() {
            return new Field[ 0 ];
        }


        @Override
        public Collection<Method> getMethods() {
            return null;
        }


        @Override
        public Method[] getOwnMethods() {
            return new Method[ 0 ];
        }


        @Override
        public boolean hasOwnStaticMembers() {
            return false;
        }


        @Override
        public boolean hasStaticMembers() {
            return false;
        }


        @Nullable
        @Override
        public Method getConstructor() {
            return null;
        }


        @Nullable
        @Override
        public Method findMethodByName( @Nullable CharSequence charSequence ) {
            return null;
        }


        @Nullable
        @Override
        public Method findOwnMethodByName( @Nullable CharSequence charSequence ) {
            return null;
        }


        @Nullable
        @Override
        public Field findFieldByName( @Nullable CharSequence charSequence, boolean b ) {
            return null;
        }


        @Nullable
        @Override
        public Field findOwnFieldByName( @Nullable CharSequence charSequence, boolean b ) {
            return null;
        }


        @Override
        public Icon getIcon() {
            return null;
        }


        @Override
        public boolean hasMethodTags() {
            return false;
        }


        @Override
        public boolean hasPropertyTags() {
            return false;
        }


        @Override
        public boolean hasConstructorFields() {
            return false;
        }


        @Nullable
        @Override
        public Method getOwnConstructor() {
            return null;
        }


        @Override
        public boolean isTrait() {
            return false;
        }


        @Override
        public List<PhpTraitUseRule> getTraitUseRules() {
            return null;
        }


        @Nullable
        @Override
        public String getPresentableFQN() {
            return null;
        }


        @Nullable
        @Override
        public <ParentType extends PhpPsiElement> ParentType getParentOfType( Class<ParentType> parentTypeClass ) {
            return null;
        }


        @Nullable
        @Override
        public String getFQN() {
            return null;
        }


        @NotNull
        @Override
        public String getNamespaceName() {
            return null;
        }


        @Override
        public boolean isDeprecated() {
            return false;
        }


        @Nullable
        @Override
        public PhpPsiElement getFirstPsiChild() {
            return null;
        }


        @Nullable
        @Override
        public PhpPsiElement getNextPsiSibling() {
            return null;
        }


        @Nullable
        @Override
        public PhpPsiElement getPrevPsiSibling() {
            return null;
        }


        @Override
        public void navigate( boolean b ) {

        }


        @Override
        public boolean canNavigate() {
            return false;
        }


        @Override
        public boolean canNavigateToSource() {
            return false;
        }


        @NotNull
        @Override
        public PhpType getType() {
            return type;
        }


        @Nullable
        @Override
        public PsiElement getNameIdentifier() {
            return null;
        }


        @NotNull
        @Override
        public Project getProject() throws PsiInvalidElementAccessException {
            return null;
        }


        @NotNull
        @Override
        public Language getLanguage() {
            return null;
        }


        @Override
        public PsiManager getManager() {
            return null;
        }


        @NotNull
        @Override
        public PsiElement[] getChildren() {
            return new PsiElement[ 0 ];
        }


        @Override
        public PsiElement getParent() {
            return null;
        }


        @Override
        public PsiElement getFirstChild() {
            return null;
        }


        @Override
        public PsiElement getLastChild() {
            return null;
        }


        @Nullable
        @Override
        public PsiElement getNextSibling() {
            return null;
        }


        @Nullable
        @Override
        public PsiElement getPrevSibling() {
            return null;
        }


        @Override
        public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
            return null;
        }


        @Override
        public TextRange getTextRange() {
            return null;
        }


        @Override
        public int getStartOffsetInParent() {
            return 0;
        }


        @Override
        public int getTextLength() {
            return 0;
        }


        @Nullable
        @Override
        public PsiElement findElementAt( int i ) {
            return null;
        }


        @Nullable
        @Override
        public PsiReference findReferenceAt( int i ) {
            return null;
        }


        @Override
        public int getTextOffset() {
            return 0;
        }


        @Override
        public String getText() {
            return null;
        }


        @NotNull
        @Override
        public char[] textToCharArray() {
            return new char[ 0 ];
        }


        @Override
        public PsiElement getNavigationElement() {
            return null;
        }


        @Override
        public PsiElement getOriginalElement() {
            return null;
        }


        @Override
        public boolean textMatches( @NotNull @NonNls CharSequence charSequence ) {
            return false;
        }


        @Override
        public boolean textMatches( @NotNull PsiElement psiElement ) {
            return false;
        }


        @Override
        public boolean textContains( char c ) {
            return false;
        }


        @Override
        public void accept( @NotNull PsiElementVisitor psiElementVisitor ) {

        }


        @Override
        public void acceptChildren( @NotNull PsiElementVisitor psiElementVisitor ) {

        }


        @Override
        public PsiElement copy() {
            return null;
        }


        @Override
        public PsiElement add( @NotNull PsiElement psiElement ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public PsiElement addBefore( @NotNull PsiElement psiElement, @Nullable PsiElement psiElement2 ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public PsiElement addAfter( @NotNull PsiElement psiElement, @Nullable PsiElement psiElement2 ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public void checkAdd( @NotNull PsiElement psiElement ) throws IncorrectOperationException {

        }


        @Override
        public PsiElement addRange( PsiElement psiElement, PsiElement psiElement2 ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public PsiElement addRangeBefore( @NotNull PsiElement psiElement, @NotNull PsiElement psiElement2, PsiElement psiElement3 ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public PsiElement addRangeAfter( PsiElement psiElement, PsiElement psiElement2, PsiElement psiElement3 ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public void delete() throws IncorrectOperationException {

        }


        @Override
        public void checkDelete() throws IncorrectOperationException {

        }


        @Override
        public void deleteChildRange( PsiElement psiElement, PsiElement psiElement2 ) throws IncorrectOperationException {

        }


        @Override
        public PsiElement replace( @NotNull PsiElement psiElement ) throws IncorrectOperationException {
            return null;
        }


        @Override
        public boolean isValid() {
            return false;
        }


        @Override
        public boolean isWritable() {
            return false;
        }


        @Nullable
        @Override
        public PsiReference getReference() {
            return null;
        }


        @NotNull
        @Override
        public PsiReference[] getReferences() {
            return new PsiReference[ 0 ];
        }


        @Nullable
        @Override
        public <T> T getCopyableUserData( Key<T> tKey ) {
            return null;
        }


        @Override
        public <T> void putCopyableUserData( Key<T> tKey, @Nullable T t ) {

        }


        @Override
        public boolean processDeclarations( @NotNull PsiScopeProcessor psiScopeProcessor, @NotNull ResolveState resolveState, @Nullable PsiElement psiElement, @NotNull PsiElement psiElement2 ) {
            return false;
        }


        @Nullable
        @Override
        public PsiElement getContext() {
            return null;
        }


        @Override
        public boolean isPhysical() {
            return false;
        }


        @NotNull
        @Override
        public GlobalSearchScope getResolveScope() {
            return null;
        }


        @NotNull
        @Override
        public SearchScope getUseScope() {
            return null;
        }


        @Override
        public ASTNode getNode() {
            return null;
        }


        @Override
        public boolean isEquivalentTo( PsiElement psiElement ) {
            return false;
        }


        @Override
        public Icon getIcon( @IconFlags int i ) {
            return null;
        }


        @Nullable
        @Override
        public <T> T getUserData( @NotNull Key<T> tKey ) {
            return null;
        }


        @Override
        public <T> void putUserData( @NotNull Key<T> tKey, @Nullable T t ) {

        }


        @NotNull
        @Override
        public PhpModifier getModifier() {
            return null;
        }


        @Override
        public IStubElementType getElementType() {
            return null;
        }


        @Override
        public NamedStub getStub() {
            return null;
        }
    }
}


