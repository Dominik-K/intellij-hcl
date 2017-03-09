/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package org.intellij.plugins.hil.refactoring

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.intellij.plugins.hil.psi.ILExpression

class IntroduceOperation(val project: Project,
                         val editor: Editor,
                         val file: PsiFile,
                         var name: String?) {
  var isReplaceAll: Boolean = false
  var element: PsiElement? = null
  var initializer: ILExpression? = null
  var occurrences: List<PsiElement> = emptyList()
  var suggestedNames: Collection<String>? = null
}