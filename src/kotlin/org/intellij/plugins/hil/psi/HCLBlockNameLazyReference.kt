/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package org.intellij.plugins.hil.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import org.intellij.plugins.hcl.psi.HCLBlock

class HCLBlockNameLazyReference<T : PsiElement>(from: T, soft: Boolean, val index: Int, val doResolve: HCLBlockNameLazyReference<T>.(incompleteCode: Boolean) -> List<HCLBlock>) : PsiReferenceBase.Poly<T>(from, soft) {
  override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
    return PsiElementResolveResult.createResults(doResolve(incompleteCode).map { it.nameElements.getOrNull(index) }.filterNotNull())
  }

  override fun getVariants(): Array<out Any> {
    //    return EMPTY_ARRAY
    return doResolve(true).map { it.nameElements.getOrNull(index) }.filterNotNull().toTypedArray()
  }
}