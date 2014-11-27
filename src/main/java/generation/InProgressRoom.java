/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

/**
 * Mutable class representing a room of type T that has children of type Child.
 */
public abstract class InProgressRoom<T extends Room<T,Child>, Child extends Room<Child,?>> {

  private final List<Child> children = new ArrayList<>();

  public void addChild(Child child) {
    children.add(child);
  }

  public ImmutableList<Child> getChildren() {
    return ImmutableList.copyOf(children);
  }

  abstract T build();
}
