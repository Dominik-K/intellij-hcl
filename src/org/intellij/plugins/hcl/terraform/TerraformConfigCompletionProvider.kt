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
package org.intellij.plugins.hcl.terraform

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PlatformPatterns.psiFile
import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.StandardPatterns.not
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.DebugUtil
import com.intellij.util.ProcessingContext
import org.intellij.plugins.hcl.HCLElementTypes
import org.intellij.plugins.hcl.codeinsight.HCLCompletionProvider
import org.intellij.plugins.hcl.psi.*
import org.intellij.plugins.hcl.terraform.config.TerraformLanguage
import org.intellij.plugins.hcl.terraform.config.model.*
import org.intellij.plugins.hcl.terraform.il.ILFileType
import java.util.*

public class TerraformConfigCompletionProvider : HCLCompletionProvider() {
  init {
    val WhiteSpace = psiElement(PsiWhiteSpace::class.java)
    val ID = psiElement(HCLElementTypes.ID)

    val Identifier = psiElement(HCLIdentifier::class.java)
    val File = psiElement(HCLFile::class.java)
    val Block = psiElement(HCLBlock::class.java)
    val Property = psiElement(HCLProperty::class.java)
    val Object = psiElement(HCLObject::class.java)

    val TerraformConfigFile = psiFile(HCLFile::class.java).withLanguage(TerraformLanguage)

    extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
        .inFile(TerraformConfigFile)
        .withParent(File)
        .andNot(psiElement().afterSiblingSkipping2(WhiteSpace, or(ID, Identifier))),
        BlockKeywordCompletionProvider);
    extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
        .inFile(TerraformConfigFile)
        .withParent(Identifier)
        .withSuperParent(2, Block)
        .withSuperParent(3, File)
        .withParent(not(psiElement(HCLIdentifier::class.java).afterSiblingSkipping2(WhiteSpace, or(ID, Identifier)))),
        BlockKeywordCompletionProvider);

    // TODO: Provide data from all resources in folder (?)

    if (MODEL_BASED_COMPLETION_ENABLED) {

      extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
          .inFile(TerraformConfigFile)
          .withParent(not(Identifier))
          .andOr(psiElement().withSuperParent(1, File), psiElement().withSuperParent(1, Block))
          .afterSiblingSkipping2(WhiteSpace, or(ID, Identifier))
          , BlockTypeOrNameCompletionProvider);
      extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
          .inFile(TerraformConfigFile)
          .withParent(psiElement(HCLIdentifier::class.java).afterSiblingSkipping2(WhiteSpace, or(ID, Identifier)))
          .andOr(psiElement().withSuperParent(2, File), psiElement().withSuperParent(2, Block))
          , BlockTypeOrNameCompletionProvider);


      extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
          .inFile(TerraformConfigFile)
          .withParent(Object)
          .withSuperParent(2, Block)
          , ResourcePropertiesCompletionProvider);

      extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
          .inFile(TerraformConfigFile)
          .withParent(Identifier)
          .withSuperParent(2, Property)
          .withSuperParent(3, Object)
          .withSuperParent(4, Block)
          , ResourcePropertiesCompletionProvider);
      extend(CompletionType.BASIC, psiElement(HCLElementTypes.ID)
          .inFile(TerraformConfigFile)
          .withParent(Identifier)
          .withSuperParent(2, Block)
          .withSuperParent(3, Object)
          .withSuperParent(4, Block)
          , ResourcePropertiesCompletionProvider);

    }
  }

  companion object {
    public val BLOCK_KEYWORDS: TreeSet<String> = sortedSetOf(
        "atlas",
        "module",
        "output",
        "provider",
        "resource",
        "variable"
    )

    val MODEL_BASED_COMPLETION_ENABLED: Boolean by lazy {
      val application = ApplicationManager.getApplication()
      true || application.isUnitTestMode || application.isInternal
    }

    public val COMMON_RESOURCE_PROPERTIES: SortedSet<String> = DefaultResourceTypeProperties.map { it.name }.toSortedSet()

    private val LOG = Logger.getInstance(TerraformConfigCompletionProvider::class.java)
    fun DumpPsiFileModel(element: PsiElement): () -> String {
      return { DebugUtil.psiToString(element.containingFile, true) }
    }
  }

  private abstract class OurCompletionProvider : CompletionProvider<CompletionParameters>() {
    protected fun getTypeModel(): TypeModel {
      val provider = ServiceManager.getService(TypeModelProvider::class.java)
      return provider.get()
    }
  }

  private object BlockKeywordCompletionProvider : OurCompletionProvider() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext?, result: CompletionResultSet) {
      LOG.debug("TF.BlockKeywordCompletionProvider")
      val position = parameters.position
      LOG.debug("position = $position")
      val parent = position.parent
      LOG.debug("parent = $parent")
      LOG.debug("left = ${position.prevSibling}")
      val leftNWS = position.getPrevSiblingNonWhiteSpace()
      LOG.debug("leftNWS = $leftNWS")
      if (leftNWS is HCLIdentifier || leftNWS?.node?.elementType == HCLElementTypes.ID) {
        return assert(false, DumpPsiFileModel(position))
      }
      result.addAllElements(BLOCK_KEYWORDS.map { LookupElementBuilder.create(it) })
    }
  }

  private object BlockTypeOrNameCompletionProvider : OurCompletionProvider() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext?, result: CompletionResultSet) {
      LOG.debug("TF.BlockTypeOrNameCompletionProvider")
      val position = parameters.position
      LOG.debug("position = $position")
      val parent = position.parent
      LOG.debug("parent = $parent")
      val obj = when {
        parent is HCLIdentifier -> parent
        position.node.elementType == HCLElementTypes.ID -> position // In case of two IDs (not Identifiers) nearby (start of block in empty file)
        else -> return assert(false, DumpPsiFileModel(position))
      }
      LOG.debug("obj = $obj")
      val leftNWS = obj.getPrevSiblingNonWhiteSpace()
      LOG.debug("leftNWS = $leftNWS")
      val type: String = when {
        leftNWS is HCLIdentifier -> leftNWS.id
        leftNWS?.node?.elementType == HCLElementTypes.ID -> leftNWS!!.text
        else -> return assert(false, DumpPsiFileModel(position))
      }
      when (type) {
        "resource" ->
          result.addAllElements(getTypeModel().resources.map { LookupElementBuilder.create(it.type) })

        "provider" ->
          result.addAllElements(getTypeModel().providers.map { LookupElementBuilder.create(it.type) })
      }
      return
    }
  }

  private object ResourcePropertiesCompletionProvider : OurCompletionProvider() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext?, result: CompletionResultSet) {
      LOG.debug("TF.ResourcePropertiesCompletionProvider")
      val position = parameters.position
      var _parent: PsiElement? = position.parent
      LOG.debug("_parent = $_parent")
      var right: Type? = null;
      var isProperty = false;
      var isBlock = false;
      if (_parent is HCLIdentifier) {
        val pob = _parent.parent // Property or Block
        if (pob is HCLProperty) {
          val value = pob.value
          right = ModelUtil.getValueType(value)
          if (right == Types.String && value is PsiLanguageInjectionHost) {
            // Check for Injection
            InjectedLanguageManager.getInstance(pob.project).enumerate(value, object: PsiLanguageInjectionHost.InjectedPsiVisitor {
              override fun visit(injectedPsi: PsiFile, places: MutableList<PsiLanguageInjectionHost.Shred>) {
                if (injectedPsi.fileType == ILFileType) {
                  right = Types.StringWithInjection;
                }
              }
            })
          }
          isProperty = true
        } else if (pob is HCLBlock) {
          isBlock = true
        }
        _parent = pob?.parent // Object
      }
      val parent: PsiElement = _parent ?: return assert(false, DumpPsiFileModel(position));
      assert(parent is HCLObject, DumpPsiFileModel(position))
      if (parent is HCLObject) {
        val pp = parent.parent
        if (pp is HCLBlock) {
          val tt = pp.getNameElementUnquoted(0)
          if (tt == "resource") {
            val type = pp.getNameElementUnquoted(1)
            val resourceType = if (type != null) getTypeModel().getResourceType(type) else null
            val properties = ArrayList<PropertyOrBlockType>()
            properties.addAll(DefaultResourceTypeProperties)
            if (resourceType?.properties != null) {
              properties.addAll(resourceType?.properties)
            }
            result.addAllElements (properties
                .filter { isRightOfPropertyWithCompatibleType(isProperty, it, right) || (isBlock && it.block != null) || (!isProperty && !isBlock) }
                .map { it.name }
                .filter { parent.findProperty(it) == null }
                // TODO: Better renderer for properties/blocks
                .map { LookupElementBuilder.create(it) })
          }
        }
      }
    }

    private fun isRightOfPropertyWithCompatibleType(isProperty: Boolean, it: PropertyOrBlockType, right: Type?): Boolean {
      if (!isProperty) return false
      if (it.property == null) return false
      if (right == Types.StringWithInjection) {
        // StringWithInjection may be anything
        // TODO: Check interpolation result
        return true;
      }
      return it.property.type == right
    }
  }
}

public fun HCLBlock.getNameElementUnquoted(i: Int): String? {
  val elements = this.nameElements
  if (elements.size() < i - 1) return null
  val element = elements.get(i)
  return when (element) {
    is HCLIdentifier -> element.id
    is HCLStringLiteral -> element.value
    else -> null
  }
}

fun PsiElement.getPrevSiblingNonWhiteSpace(): PsiElement? {
  var prev = this.prevSibling
  while (prev != null && prev is PsiWhiteSpace) {
    prev = prev.prevSibling
  }
  return prev;
}


public fun <T : PsiElement, Self : PsiElementPattern<T, Self>> PsiElementPattern<T, Self>.afterSiblingSkipping2(skip: ElementPattern<out Any>, pattern: ElementPattern<out PsiElement>): Self {
  return with(object : PatternCondition<T>("afterSiblingSkipping2") {
    override fun accepts(t: T, context: ProcessingContext): Boolean {
      var o = t.prevSibling
      while (o != null) {
        if (!skip.accepts(o, context)) {
          return pattern.accepts(o, context)
        }
        o = o.prevSibling
      }
      return false
    }
  })
}