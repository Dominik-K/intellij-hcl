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
package org.intellij.plugins.hcl.terraform.config.refactoring;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public abstract class BaseIntroduceVariableRefactoringTest extends LightPlatformCodeInsightFixtureTestCase {

  protected void doTest() {
    doTest(true);
  }

  protected void doTest(boolean replaceAll) {
    doTest(replaceAll, "foo");
  }

  protected void doTest(boolean replaceAll, String name) {
    myFixture.configureByFile(getTestName(false) + ".tf");
    final EditorSettings settings = myFixture.getEditor().getSettings();
    boolean inplaceEnabled = settings.isVariableInplaceRenameEnabled();
    try {
      settings.setVariableInplaceRenameEnabled(false);
      BaseIntroduceVariableHandler handler = createHandler();
      final BaseIntroduceOperation operation = createIntroduceOperation(name);
      operation.setReplaceAll(replaceAll);
      //noinspection unchecked
      handler.performAction(operation);
      myFixture.checkResultByFile(getTestName(false) + ".after" + ".tf");
    } finally {
      settings.setVariableInplaceRenameEnabled(inplaceEnabled);
    }
  }

  @NotNull
  protected abstract BaseIntroduceOperation createIntroduceOperation(String name);

  @NotNull
  protected abstract BaseIntroduceVariableHandler createHandler();

}
