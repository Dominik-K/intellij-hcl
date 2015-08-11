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
package org.intellij.plugins.hcl.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.LightPlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HCLElementGeneratorTest extends LightPlatformTestCase {
  protected HCLElementGenerator myElementGenerator;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myElementGenerator = createElementGenerator();
  }

  @NotNull
  protected HCLElementGenerator createElementGenerator() {
    return new HCLElementGenerator(getProject());
  }


  public void testCreateIdentifier() throws Exception {
    final HCLIdentifier element = myElementGenerator.createIdentifier("id");
    assertEquals("id", element.getId());
//    assertEquals("id", element.getName());
  }

  public void testCreateStringLiteral() throws Exception {
    final HCLStringLiteral element = myElementGenerator.createStringLiteral("literal");
    assertEquals("literal", element.getValue());
//    assertEquals("literal", element.getName());
    final List<Pair<TextRange, String>> fragments = element.getTextFragments();
    assertEquals(1, fragments.size());
    assertEquals("literal", fragments.iterator().next().second);
  }

  public void testCreateProperty() throws Exception {
    final HCLProperty element = myElementGenerator.createProperty("n", "'v'");
    assertEquals("n", element.getName());
    final HCLValue value = element.getValue();
    assertNotNull(value);
    assertTrue(value instanceof HCLStringLiteral);
    HCLStringLiteral literal = (HCLStringLiteral) value;
    assertEquals("v", literal.getValue());
  }

  public void testCreateStringValue() throws Exception {
    HCLValue element = myElementGenerator.createValue("'v'");
//    assertEquals("v", element.getName());
    assertTrue(element instanceof HCLStringLiteral);
    HCLStringLiteral literal = (HCLStringLiteral) element;
    assertEquals("v", literal.getValue());

  }

  public void testCreateIdentifierValue() throws Exception {
    HCLValue element = myElementGenerator.createValue("id");
    assertTrue(element instanceof HCLIdentifier);
    assertEquals("id", ((HCLIdentifier) element).getId());
  }

  public void testCreateBooleanValue() throws Exception {
    HCLValue element = myElementGenerator.createValue("true");
    assertTrue(element instanceof HCLBooleanLiteral);
    assertEquals(true, ((HCLBooleanLiteral) element).getValue());
    element = myElementGenerator.createValue("false");
    assertTrue(element instanceof HCLBooleanLiteral);
    assertEquals(false, ((HCLBooleanLiteral) element).getValue());
  }

  public void testCreateNumericalValue() throws Exception {
    HCLValue element = myElementGenerator.createValue("42");
    assertTrue(element instanceof HCLNumberLiteral);
    assertEquals(42.0, ((HCLNumberLiteral) element).getValue());
  }

  public void testCreateObject() throws Exception {
    final HCLObject element = myElementGenerator.createObject("a=1");
//    assertEquals("bbb", element.getName());
    final HCLProperty property = element.findProperty("a");
    assertNotNull(property);
    assertEquals("a", property.getName());
  }

  public void testCreateBlock() throws Exception {
    final HCLBlock element = myElementGenerator.createBlock("bbb");
    assertEquals("bbb", element.getName());
    assertEquals(1, element.getNameElements().length);
  }


//  public void testCreate() throws Exception {
//  }
}
