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
package org.intellij.plugins.hcl.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.util.IncorrectOperationException
import org.intellij.plugins.hcl.HCLElementTypes
import org.intellij.plugins.hcl.psi.impl.HCLHeredocContentMixin
import org.intellij.plugins.hcl.psi.impl.HCLPsiImplUtils

class HCLHeredocContentManipulator : AbstractElementManipulator<HCLHeredocContent>() {

  // There two major cases:
  // 1. all content replaced with actual diff very small (full heredoc injection):
  // 1.1 One line modified
  // 1.2 One line added
  // 1.3 One line removed
  // 2. one line content (probably not all) replaced with any diff (HIL injection)
  // This cases should work quite fast

  @Throws(IncorrectOperationException::class)
  override fun handleContentChange(element: HCLHeredocContent, range: TextRange, newContent: String): HCLHeredocContent {
    if (range.length == 0 && element.linesCount == 0) {
      // Replace empty with something
      return replaceStupidly(element, newContent)
    }

    //////////
    // Calculate affected elements (strings) (based on offsets)

    var offset: Int = 0
    val lines = HCLPsiImplUtils.getLinesWithEOL(element)
    val ranges = lines.map {
      val r: TextRange = TextRange.from(offset, it.length)
      offset += it.length
      r
    }

    val startString: Int
    val endString: Int

    val linesToChange = ranges.indices.filter { ranges[it].intersects(range) }
    assert(linesToChange.isNotEmpty())
    startString = linesToChange.first()
    endString = linesToChange.last()

    val node = element.node as TreeElement

    val prefixStartString = lines[startString].substring(0, range.startOffset - ranges[startString].startOffset)
    val suffixEndString = lines[endString].substring(range.endOffset - ranges[endString].startOffset).removeSuffix("\n")

    //////////
    // Prepare new lines content

    val newText = prefixStartString + newContent + suffixEndString
    val newLines: List<String> = getReplacementLines(newText).let { if (it.size >= 2 && element.textLength <= range.endOffset && it.last() == "") it.dropLast(1) else it }

    //////////
    // Replace nodes

    var stopNode: ASTNode? = lookupLine(node, endString)?.let { nextLine(it) } // children[endString].treeNext?.treeNext
    if (range.endOffset == ranges[endString].startOffset) {
      stopNode = stopNode?.let { nextLine(it) }
    }
    var iter: ASTNode? = lookupLine(node, startString) //children[startString]
    for (line in newLines) {
      if (iter != null && iter != stopNode) {
        // Replace existing lines
        val next = nextLine(iter) // .treeNext?.treeNext
        if (iter.isHD_EOL) {
          // Line was empty.
          if (line.isEmpty()) {
            // Left intact
          } else {
            // Add HD_LINE before 'iter' (HD_EOL)
            node.addLeaf(HCLElementTypes.HD_LINE, line, iter)
          }
        } else {
          assert(iter.elementType == HCLElementTypes.HD_LINE)
          if (line.isEmpty()) {
            // Remove HD_LINE; HD_EOL would be preserved
            node.removeChild(iter)
          } else {
            if (iter.text != line) {
              // Replace node text
              (iter as LeafElement).replaceWithText(line)
            }
          }
        }
        iter = next
      } else {
        // Add new lines to end
        node.addLeaf(HCLElementTypes.HD_LINE, line, stopNode)
        node.addLeaf(HCLElementTypes.HD_EOL, "\n", stopNode)
      }
    }
    // Remove extra lines
    if (iter != null && iter != stopNode) {
      node.removeRange(iter, stopNode)
    }
    return element
  }

  companion object {
    fun getReplacementLines(newText: String): List<String> {
      if (newText == "") (return emptyList())
      // TODO: Convert line separators to \n ?
      val list = StringUtil.splitByLinesKeepSeparators(newText).toList().map { it.removeSuffix("\n") }
      if (newText.endsWith("\n")) return list + ""
      return list
    }

    private fun lookupLine(node: TreeElement, n: Int): ASTNode? {
      var cn: ASTNode? = node.firstChildNode
      var counter: Int = 0
      while (cn != null) {
        if (counter == n) return cn
        if (cn.elementType == HCLElementTypes.HD_EOL) counter++
        cn = cn.treeNext
      }
      return null
    }

    private fun nextLine(node: ASTNode): ASTNode? {
      if (node.isHD_EOL) {
        return node.treeNext
      } else {
        return node.treeNext?.treeNext
      }
    }
  }

  override fun handleContentChange(element: HCLHeredocContent, newContent: String): HCLHeredocContent {
    if (element is HCLHeredocContentMixin) {
      return handleContentChange(element, TextRange.from(0, element.textLength), newContent)
    }
    return replaceStupidly(element, newContent);
  }

  fun replaceStupidly(element: HCLHeredocContent, newContent: String): HCLHeredocContent {
    // Do simple full replacement
    val newLines = getReplacementLines(newContent)

    val node = element.node
    if (node.firstChildNode != null) {
      node.removeRange(node.firstChildNode, null)
    }
    for (line in newLines) {
      if (line.isNotEmpty()) node.addLeaf(HCLElementTypes.HD_LINE, line, null)
      node.addLeaf(HCLElementTypes.HD_EOL, "\n", null)
    }
    return element
  }

  override fun getRangeInElement(element: HCLHeredocContent): TextRange {
    if (element.textLength == 0) return TextRange.EMPTY_RANGE
    return TextRange.from(0, element.textLength - 1)
  }
}

val ASTNode.isHD_EOL: Boolean
  get() = this.elementType == HCLElementTypes.HD_EOL


