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
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.intellij.util.ui.EmptyIcon;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * There does not seem to be an easy way to get a primitive representation from the index
 * keep java for a minute as conversion is a bit iffy
 */
public class LocalClassImpl implements PhpNamedElement {

    private final PhpType type;
    private final Project project;


    public LocalClassImpl( PhpType type, Project project ) {
        this.type = type;
        this.project = project;
    }


    @Nullable
    @Override
    public ASTNode getNameNode() {
        return null;
    }


    @NotNull
    @Override
    public String getName() {
        return "";
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
        return "";
    }


    @Nullable
    @Override
    public PhpDocComment getDocComment() {
        return null;
    }


    @Override
    public void processDocs( Processor<PhpDocComment> processor ) {

    }


    @NotNull
    @Override
    public Icon getIcon() {
        return new EmptyIcon( 0,0 );
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
        return "";
    }


    @Override
    public boolean isDeprecated() {
        return false;
    }


    public boolean isInternal() {
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
        return project;
    }


    @NotNull
    @Override
    public Language getLanguage() {
        return PhpLanguage.INSTANCE;
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
}
