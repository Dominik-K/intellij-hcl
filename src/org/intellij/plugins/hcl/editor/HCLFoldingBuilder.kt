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
package org.intellij.plugins.hcl.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import org.intellij.plugins.hcl.HCLElementTypes
import java.util.*

public class HCLFoldingBuilder : FoldingBuilder {
  override fun isCollapsedByDefault(node: ASTNode): Boolean {
    return false
  }

  override fun buildFoldRegions(node: ASTNode, document: Document): Array<out FoldingDescriptor> {
    val descriptors = ArrayList<FoldingDescriptor>()
    collect(node, document, descriptors);
    return descriptors.toArray(emptyArray())
  }

  private fun collect(node: ASTNode, document: Document, descriptors: ArrayList<FoldingDescriptor>) {
    when (node.getElementType()) {
      HCLElementTypes.ARRAY, HCLElementTypes.OBJECT -> if (isSpanMultipleLines(node, document)) {
        descriptors.add(FoldingDescriptor(node, node.getTextRange()))
      }
      HCLElementTypes.BLOCK_COMMENT -> descriptors.add(FoldingDescriptor(node, node.getTextRange()))
    // TODO: multiple single comments into one folding block
      HCLElementTypes.LINE_COMMENT -> descriptors.add(FoldingDescriptor(node, node.getTextRange()))
    }
    for (c in node.getChildren(null)) {
      collect(c, document, descriptors)
    }
  }

  override fun getPlaceholderText(node: ASTNode): String? {
    return when (node.getElementType()) {
      HCLElementTypes.ARRAY -> "[...]"
      HCLElementTypes.OBJECT -> "{...}"
      HCLElementTypes.BLOCK_COMMENT -> "/*...*/"
      HCLElementTypes.LINE_COMMENT -> "//..."
      else -> "..."
    }
  }

  private fun isSpanMultipleLines(node: ASTNode, document: Document): Boolean {
    val range = node.getTextRange()
    return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset())
  }
}
