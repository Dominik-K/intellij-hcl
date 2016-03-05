/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.plugins.hil.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.hil.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HILPsiImplUtilJ {
  public static Class getTypeClass(ILLiteralExpressionImpl expression) {
    return HILPsiImplUtils.INSTANCE.getTypeClass(expression);
  }

  @NotNull
  public static ILExpression[] getParameters(ILParameterList list) {
    return HILPsiImplUtils.INSTANCE.getParameters(list);
  }

  public static ILExpression getQualifier(ILMethodCallExpression expression) {
    return HILPsiImplUtils.INSTANCE.getQualifier(expression);
  }

  @Nullable
  public static ILVariable getMethod(ILMethodCallExpression expression) {
    return HILPsiImplUtils.INSTANCE.getMethod(expression);
  }

  public static ILParameterList getParameterList(ILMethodCallExpressionImpl expression) {
    return HILPsiImplUtils.INSTANCE.getParameterList(expression);
  }

  @NotNull
  public static String getName(ILVariableImpl variable) {
    return HILPsiImplUtils.INSTANCE.getName(variable);
  }

  public static PsiNamedElement setName(ILVariableImpl variable, @NotNull String name) throws IncorrectOperationException {
    return HILPsiImplUtils.INSTANCE.setName(variable, name);
  }

  @NotNull
  public static SearchScope getUseScope(ILVariableImpl variable) {
    return HILPsiImplUtils.INSTANCE.getUseScope(variable);
  }

  @NotNull
  public static GlobalSearchScope getResolveScope(ILVariableImpl variable) {
    return HILPsiImplUtils.INSTANCE.getResolveScope(variable);
  }

  @Nullable
  public static ILVariable createVariable(String name, Project project) {
    return HILPsiImplUtils.INSTANCE.createVariable(name, project);
  }

  @Nullable
  public static ILExpression getField(ILSelectExpression expression) {
    return HILPsiImplUtils.INSTANCE.getField(expression);
  }

  @Nullable
  public static String getUnquotedText(ILLiteralExpression literal){
    return HILPsiImplUtils.INSTANCE.getUnquotedText(literal);
  }

  @NotNull
  public static ILExpression getOperand(ILUnaryExpression expression) {
    return HILPsiImplUtils.INSTANCE.getOperand(expression);
  }

  @NotNull
  public static IElementType getOperationSign(ILUnaryExpression expression) {
    return HILPsiImplUtils.INSTANCE.getOperationSign(expression);
  }
}
