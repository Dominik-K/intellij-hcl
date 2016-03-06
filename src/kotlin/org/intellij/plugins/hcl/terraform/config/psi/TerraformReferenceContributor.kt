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
package org.intellij.plugins.hcl.terraform.config.psi

import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import getNameElementUnquoted
import org.intellij.plugins.hcl.psi.*
import org.intellij.plugins.hcl.terraform.config.TerraformLanguage
import org.intellij.plugins.hcl.terraform.config.model.getTerraformModule
import org.intellij.plugins.hil.psi.HCLElementLazyReference

class TerraformReferenceContributor : PsiReferenceContributor() {
  override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
    val TerraformConfigFile = PlatformPatterns.psiFile(HCLFile::class.java).withLanguage(TerraformLanguage)

    registrar.registerReferenceProvider(
        psiElement(HCLStringLiteral::class.java)
            .inFile(TerraformConfigFile)
            .withParent(psiElement(HCLProperty::class.java).with(object : PatternCondition<HCLProperty?>("HCLProperty(provider)") {
              override fun accepts(t: HCLProperty?, context: ProcessingContext?): Boolean {
                return "provider" == t?.name
              }
            }))
            .withSuperParent(3, psiElement(HCLBlock::class.java).with(object : PatternCondition<HCLBlock?>("HCLBlock(resource)") {
              override fun accepts(t: HCLBlock?, context: ProcessingContext?): Boolean {
                return t?.getNameElementUnquoted(0) == "resource"
              }
            }))
        , SimpleReferenceProvider);
  }
}

object SimpleReferenceProvider : PsiReferenceProvider() {
  override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<out PsiReference> {
    if (element !is HCLStringLiteral) return PsiReference.EMPTY_ARRAY
    val parent = element.parent
    if (parent !is HCLProperty) return PsiReference.EMPTY_ARRAY
    if (parent.nameElement == element) return PsiReference.EMPTY_ARRAY
    return arrayOf(HCLElementLazyReference(element, false) { incomplete, fake ->
      @Suppress("NAME_SHADOWING")
      val element = this.element
      if (incomplete) {
        element.getTerraformModule().getDefinedProviders().map { it.first.nameIdentifier as HCLElement }
      } else {
        element.getTerraformModule().findProviders(element.value).map { it.nameIdentifier as HCLElement }
      }
    })
  }
}
