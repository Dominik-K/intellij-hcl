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
package org.intellij.plugins.hil

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object HILFileType : LanguageFileType(HILLanguage) {
  public val DEFAULT_EXTENSION: String = "hil";

  override fun getIcon(): Icon? {
    return AllIcons.FileTypes.Custom
  }

  override fun getDefaultExtension(): String {
    return DEFAULT_EXTENSION
  }

  override fun getDescription(): String {
    return "HashiCorp Interpolation Language file"
  }

  override fun getName(): String {
    return "HIL"
  }
}

class HILFileTypeFactory : FileTypeFactory() {
  override fun createFileTypes(consumer: FileTypeConsumer) {
    consumer.consume(HILFileType, HILFileType.DEFAULT_EXTENSION)
  }
}