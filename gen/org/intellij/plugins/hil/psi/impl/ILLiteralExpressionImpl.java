// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.hil.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.plugins.hil.HILElementTypes.*;
import org.intellij.plugins.hil.psi.*;

public class ILLiteralExpressionImpl extends ILExpressionWithReference implements ILLiteralExpression {

  public ILLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ILGeneratedVisitor) ((ILGeneratedVisitor)visitor).visitILLiteralExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getDoubleQuotedString() {
    return findChildByType(DOUBLE_QUOTED_STRING);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

  public Class getTypeClass() {
    return HILPsiImplUtilJ.getTypeClass(this);
  }

  @Nullable
  public String getUnquotedText() {
    return HILPsiImplUtilJ.getUnquotedText(this);
  }

}
