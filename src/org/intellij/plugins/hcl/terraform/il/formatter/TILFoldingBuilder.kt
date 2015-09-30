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
package org.intellij.plugins.hcl.terraform.il.formatter

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.tree.TokenSet
import org.intellij.plugins.hcl.terraform.il.TILElementTypes
import java.util.*

public class TILFoldingBuilder : FoldingBuilder {
  override fun isCollapsedByDefault(node: ASTNode): Boolean {
    return false
  }

  override fun buildFoldRegions(node: ASTNode, document: Document): Array<out FoldingDescriptor> {
    val descriptors = ArrayList<FoldingDescriptor>()
    collect(node, document, descriptors);
    return descriptors.toTypedArray()
  }

  companion object {
    private val Accepted = TokenSet.create(TILElementTypes.IL_PARENTHESIZED_EXPRESSION, TILElementTypes.IL_PARAMETER_LIST, TILElementTypes.IL_EXPRESSION_HOLDER)
  }

  private fun collect(node: ASTNode, document: Document, descriptors: ArrayList<FoldingDescriptor>) {
    if (Accepted.contains(node.elementType) && node.textLength > 5) {
      descriptors.add(FoldingDescriptor(node, node.textRange))
    }
    for (c in node.getChildren(null)) {
      collect(c, document, descriptors)
    }
  }

  override fun getPlaceholderText(node: ASTNode): String? {
    return when (node.elementType) {
      TILElementTypes.IL_PARENTHESIZED_EXPRESSION -> "(...)"
      TILElementTypes.IL_PARAMETER_LIST -> "(...)"
      TILElementTypes.IL_EXPRESSION_HOLDER -> "\${...}"
      else -> "..."
    }
  }
}