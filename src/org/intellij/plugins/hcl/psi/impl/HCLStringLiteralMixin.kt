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
package org.intellij.plugins.hcl.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper

public abstract class HCLStringLiteralMixin(node: ASTNode?) : HCLLiteralImpl(node), PsiLanguageInjectionHost {
  override fun isValidHost() = true

  override fun updateText(text: String): PsiLanguageInjectionHost {
    val vNode = node.firstChildNode
    assert(vNode is LeafElement)
    (vNode as LeafElement).replaceWithText(text)
    return this
  }

  override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
    return StringLiteralEscaper<HCLStringLiteralMixin>(this);
  }
}
