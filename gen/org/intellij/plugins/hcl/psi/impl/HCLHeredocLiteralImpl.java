// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.hcl.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.plugins.hcl.HCLElementTypes.*;
import org.intellij.plugins.hcl.psi.*;

public class HCLHeredocLiteralImpl extends HCLHeredocLiteralMixin implements HCLHeredocLiteral {

  public HCLHeredocLiteralImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HCLElementVisitor) ((HCLElementVisitor)visitor).visitHeredocLiteral(this);
    else super.accept(visitor);
  }

  @NotNull
  public String getValue() {
    return HCLPsiImplUtilJ.getValue(this);
  }

  @Override
  @NotNull
  public List<HCLHeredocLine> getLinesList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HCLHeredocLine.class);
  }

  @Override
  @NotNull
  public HCLHeredocMarker getMarkerStart() {
    List<HCLHeredocMarker> p1 = PsiTreeUtil.getChildrenOfTypeAsList(this, HCLHeredocMarker.class);
    return p1.get(0);
  }

  @Override
  @Nullable
  public HCLHeredocMarker getMarkerEnd() {
    List<HCLHeredocMarker> p1 = PsiTreeUtil.getChildrenOfTypeAsList(this, HCLHeredocMarker.class);
    return p1.size() < 2 ? null : p1.get(1);
  }

}
