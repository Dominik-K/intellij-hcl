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
package org.intellij.plugins.hcl.terraform.config.model

import org.intellij.plugins.hcl.psi.*

// Model for element types

public open class Type(val name: String)
public open class PropertyType(val name: String, val type: Type, val typeHint: String? = null, val description: String? = null)
public open class BlockType(val literal: String, val args: Int = 0, vararg val properties: PropertyOrBlockType = arrayOf())
public class PropertyOrBlockType private constructor(val property: PropertyType? = null, val block: BlockType? = null) {
  val name: String = if (property != null) property.name else block!!.literal

  init {
    assert(property != null || block != null);
  }

  constructor(property: PropertyType) : this(property, null)

  constructor(block: BlockType) : this(null, block)
}

public fun PropertyType.toPOBT(): PropertyOrBlockType {
  return PropertyOrBlockType(this)
}

public fun BlockType.toPOBT(): PropertyOrBlockType {
  return PropertyOrBlockType(this)
}

object Types {
  val Identifier = Type("Identifier")
  val String = Type("String")
  val Number = Type("Number")
  val Boolean = Type("Boolean")
  val Null = Type("Null")
  val Array = Type("Array")
  val Object = Type("Object")
}

public class ResourceType(val type: String, vararg properties: PropertyOrBlockType = arrayOf()) : BlockType("resource", 2, *properties)
public class ProviderType(val type: String, vararg properties: PropertyOrBlockType = arrayOf()) : BlockType("provider", 1, *properties)
public class VariableType(vararg properties: PropertyOrBlockType = arrayOf()) : BlockType("variable", 1, *properties)

val DefaultResourceTypeProperties: Array<PropertyOrBlockType> = arrayOf(
    PropertyType("count", Types.Number).toPOBT()
)
val DefaultProviderTypeProperties: Array<PropertyOrBlockType> = arrayOf()


public object TypeModel {
  val resources: List<ResourceType> = listOf(
      ResourceType("aws_elb",
          PropertyType("name", Types.String).toPOBT(),
          PropertyType("availability_zones", Types.Array, "String").toPOBT(),
          PropertyType("instances", Types.Array, "String").toPOBT(),
          *DefaultResourceTypeProperties),
      ResourceType("aws_instance",
          PropertyType("instance_type", Types.String).toPOBT(),
          PropertyType("ami", Types.String).toPOBT(),
          *DefaultResourceTypeProperties)
  )
  val providers: List<ProviderType> = listOf(
      ProviderType("aws", PropertyOrBlockType(PropertyType("region", Types.String)), *DefaultProviderTypeProperties)
  )
  val variables: List<VariableType> = listOf()

  fun getResourceType(name: String): ResourceType? {
    return resources.firstOrNull { it.type == name }
  }

  fun getProviderType(name: String): ProviderType? {
    return providers.firstOrNull { it.type == name }
  }

  fun getBlockTypeNames(): List<String> {
    return listOf(
        "atlas",
        "module",
        "output",
        "provider",
        "resource",
        "variable"
    )
  }


}
