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
package org.intellij.plugins.hcl.terraform.il

import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import org.intellij.plugins.hcl.psi.impl.HCLStringLiteralImpl

class ILLanguageInjector : LanguageInjector {
  override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
    if (host !is HCLStringLiteralImpl) return;
    // Only .tf (Terraform config) files
    val file = host.getContainingFile() ?: return
    val virtualFile = file.getVirtualFile() ?: return
    if (!"tf".equals(virtualFile.getExtension())) return;
    val text = host.getValue()
    var start = text.indexOf("\${")
    while (start != -1) {
      var end = text.indexOf('}', start + 2)
      if (end == -1) {
        break;
      } else {
        // shift for '"'
        places.addPlace(TILLanguage, TextRange(start, end + 1).shiftRight(1), null, null)
      }
      start = text.indexOf("\${", end + 1)
    }
  }
}