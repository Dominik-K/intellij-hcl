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
import org.intellij.plugins.hcl.terraform.config.TerraformFileType
import java.util.ArrayList

public class ILLanguageInjector : LanguageInjector {
  override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
    if (host !is HCLStringLiteralImpl) return;
    // Only .tf (Terraform config) files
    val file = host.getContainingFile() ?: return
    if (file.getFileType() !is TerraformFileType) return;
    val text = host.getText()
    val ranges = getILRangesInText(text)
    for (range in ranges) {
      places.addPlace(TILLanguage, range, null, null)
    }
  }

  companion object {
    public fun getILRangesInText(text: String): ArrayList<TextRange> {
      val ranges: ArrayList<TextRange> = ArrayList()
      var start = text.indexOf("\${")
      out@ while (start != -1) {
        // Take as much as we can
        var b = false;
        var level = 1;
        for (i in start + 1..text.lastIndex) {
          val c = text[i];
          if (c == '}') {
            level--;
          }
          if (c == '{' && b) {
            level++;
          }
          b = c == '$'

          if (level == 0) {
            val end = i;
            val range = TextRange(start, end + 1)
            ranges.add(range);
            start = text.indexOf("\${", end + 1)
            continue@out;
          }
        }
        if (level != 0) {
          // Unexpected EOF, mark everything as ranges
          val end = text.lastIndexOf('}');
          if (end != -1) {
            ranges.add(TextRange(start, end + 1));
          }
        }
        break@out;
      }
      return ranges;
    }

  }
}