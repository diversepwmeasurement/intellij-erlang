package org.intellij.erlang.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.erlang.psi.ErlangAttribute;
import org.intellij.erlang.psi.ErlangFile;
import org.intellij.erlang.psi.ErlangModule;
import org.intellij.erlang.psi.ErlangQAtom;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author ignatov
 */
public class ErlangModuleReferenceImpl<T extends ErlangQAtom> extends PsiReferenceBase<T> {
  private String myReferenceName;

  public ErlangModuleReferenceImpl(@NotNull T element, TextRange range, String name) {
    super(element, range);
    myReferenceName = name;
  }

  @Override
  public PsiElement resolve() {
    PsiFile[] files = FilenameIndex.getFilesByName(myElement.getProject(), myReferenceName, GlobalSearchScope.projectScope(myElement.getProject()));
    for (PsiFile file : files) {
      if (file instanceof ErlangFile) {
        List<ErlangAttribute> attributes = PsiTreeUtil.getChildrenOfTypeAsList(file, ErlangAttribute.class);
        for (ErlangAttribute attribute : attributes) {
          ErlangModule module = attribute.getModule();
          if (module != null) {
            return module;
          }
        }
      }
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return ArrayUtil.toObjectArray(ErlangPsiImplUtil.getRecordLookupElements(myElement.getContainingFile()));
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.getAtom().replace(ErlangElementFactory.createQAtomFromText(getElement().getProject(), newElementName));
    return myElement;
  }
}
