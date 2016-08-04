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
package org.intellij.plugins.hcl.terraform.config.findUsages

import com.intellij.psi.PsiElement
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import org.intellij.plugins.hcl.psi.*

class HCLUsageTypeProvider : UsageTypeProvider {
  override fun getUsageType(element: PsiElement?): UsageType? {
    if (element == null) return null
    if (element !is HCLElement) return null
    if (element is HCLStringLiteral || element is HCLIdentifier) {
      val parent = element.parent
      if (parent is HCLProperty) {
        if (parent.nameIdentifier === element) {
          return UsageType.WRITE
        } else {
          return UsageType.READ
        }
      } else if (parent is HCLBlock) {
        if (parent.nameIdentifier === element) {
          return UsageType.WRITE
        }
      }
    }
    return null
  }
}
