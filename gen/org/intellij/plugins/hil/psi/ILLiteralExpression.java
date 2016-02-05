// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.hil.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ILLiteralExpression extends ILExpression {

  @Nullable
  PsiElement getDoubleQuotedString();

  @Nullable
  PsiElement getNumber();

  Class getTypeClass();

  @Nullable
  String getUnquotedText();

}
