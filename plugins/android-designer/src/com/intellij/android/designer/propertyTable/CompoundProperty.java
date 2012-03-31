/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
package com.intellij.android.designer.propertyTable;

import com.intellij.android.designer.model.RadViewComponent;
import com.intellij.designer.propertyTable.Property;
import org.jetbrains.android.dom.attrs.AttributeDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Lobas
 */
public class CompoundProperty extends AttributeProperty {
  private List<Property> myChildren = new ArrayList<Property>();

  public CompoundProperty(@NotNull String name, @NotNull AttributeDefinition definition) {
    super(name, definition);
  }

  @Override
  public List<Property> getChildren(@Nullable RadViewComponent component) {
    return myChildren;
  }

  @Override
  public Property createForNewPresentation() {
    CompoundProperty property = new CompoundProperty(getName(), myDefinition);
    List<Property> children = property.getChildren(null);
    for (Property childProperty : myChildren) {
      children.add(childProperty.createForNewPresentation());
    }
    return property;
  }
}