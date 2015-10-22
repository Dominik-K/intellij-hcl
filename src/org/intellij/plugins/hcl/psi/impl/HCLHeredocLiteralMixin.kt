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

import com.intellij.json.psi.impl.JSStringLiteralEscaper
import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.LeafElement
import org.intellij.plugins.hcl.psi.HCLElementGenerator
import org.intellij.plugins.hcl.psi.HCLHeredocLine
import org.intellij.plugins.hcl.psi.HCLHeredocLiteral
import java.util.*

public abstract class HCLHeredocLiteralMixin(node: ASTNode?) : HCLLiteralImpl(node), PsiLanguageInjectionHost, HCLHeredocLiteral {
  override fun isValidHost() = true

  override fun updateText(text: String): PsiLanguageInjectionHost {
    val newLines = text.lines()
    val hclLines = ArrayList(this.linesList)
    var i = 0;
    while (i < Math.min(newLines.size, hclLines.size)) {
      val hclHeredocLine = hclLines.get(i)!!
      // TODO: use origin EOL
      (hclHeredocLine.node as LeafElement).replaceWithText(newLines.get(0) + "\n");
      ++i;
    }
    var j = i;
    while (j < hclLines.size) {
      val hclHeredocLine = hclLines.get(j)!!
      hclHeredocLine.delete();
      ++j;
    }
    j = i;
    while (j < newLines.size) {
      val line = newLines.get(j)

      this.add(createHeredocLinePsi(line))
      ++j;
    }
    //    val vNode = getNode().getFirstChildNode() as LeafElement
    //    vNode.replaceWithText(text)
    return this
  }

  private fun createHeredocLinePsi(line: String): HCLHeredocLine {
    val generator = HCLElementGenerator(project)
    return generator.createHeredocLine(line)
  }

  override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
    // TODO: Ensure it works
    return object : JSStringLiteralEscaper<PsiLanguageInjectionHost>(this) {
      override fun isRegExpLiteral() = true
    }
  }
}
