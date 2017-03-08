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
package org.intellij.plugins.hil;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import org.intellij.plugins.hil.psi.HILLexer;

public class HILLexerTest extends LexerTestCase {
  @Override
  protected Lexer createLexer() {
    return new HILLexer();
  }

  @Override
  protected String getDirPath() {
    return "data/hil/lexer";
  }

  public void testLogicalOps() throws Exception {
    doTest("true && true", "true ('true')\n" +
        "WHITE_SPACE (' ')\n" +
        "&& ('&&')\n" +
        "WHITE_SPACE (' ')\n" +
        "true ('true')");
    doTest("true || false", "true ('true')\n" +
        "WHITE_SPACE (' ')\n" +
        "|| ('||')\n" +
        "WHITE_SPACE (' ')\n" +
        "false ('false')");
    doTest("!true", "! ('!')\n" +
        "true ('true')");
  }

  public void testCompareOps() throws Exception {
    doTest("1==2", "NUMBER ('1')\n" +
        "== ('==')\n" +
        "NUMBER ('2')");
    doTest("1!=2", "NUMBER ('1')\n" +
        "!= ('!=')\n" +
        "NUMBER ('2')");
    doTest("1>2", "NUMBER ('1')\n" +
        "> ('>')\n" +
        "NUMBER ('2')");
    doTest("1<2", "NUMBER ('1')\n" +
        "< ('<')\n" +
        "NUMBER ('2')");
    doTest("1>=2", "NUMBER ('1')\n" +
        ">= ('>=')\n" +
        "NUMBER ('2')");
    doTest("1<=2", "NUMBER ('1')\n" +
        "<= ('<=')\n" +
        "NUMBER ('2')");
  }

  public void testTernaryOp() throws Exception {
    doTest("true ? 1 : 2", "true ('true')\n" +
        "WHITE_SPACE (' ')\n" +
        "? ('?')\n" +
        "WHITE_SPACE (' ')\n" +
        "NUMBER ('1')\n" +
        "WHITE_SPACE (' ')\n" +
        ": (':')\n" +
        "WHITE_SPACE (' ')\n" +
        "NUMBER ('2')");
  }

  public void testTernaryOpWithInterpolationBranch() throws Exception {
    doTest("true ? 1 : \"${\"x\"}\"\"", "true ('true')\n" +
        "WHITE_SPACE (' ')\n" +
        "? ('?')\n" +
        "WHITE_SPACE (' ')\n" +
        "NUMBER ('1')\n" +
        "WHITE_SPACE (' ')\n" +
        ": (':')\n" +
        "WHITE_SPACE (' ')\n" +
        "LITERAL ('\"${\"x\"}\"')");
  }

  public void testUnsupportedOps() throws Exception {
    doTest("1|2", "NUMBER ('1')\n" +
        "BAD_CHARACTER ('|')\n" +
        "NUMBER ('2')");
    doTest("1&2", "NUMBER ('1')\n" +
        "BAD_CHARACTER ('&')\n" +
        "NUMBER ('2')");
    doTest("1=2", "NUMBER ('1')\n" +
        "BAD_CHARACTER ('=')\n" +
        "NUMBER ('2')");
  }
}
