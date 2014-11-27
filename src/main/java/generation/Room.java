/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;

/**
 * Denotes a finished and rendered room.
 * @param <T> Defines the type of the room. Subclass types will be used to parameterize other types.
 */
public abstract class Room <T extends Room<T,Child>, Child extends Room<Child,?>> {

  /**
   * this would be parent type of top level, child type of lowest level.
   */
  public static abstract class EmptyType extends Room<EmptyType, EmptyType> {
    private EmptyType() {}
  }

  public abstract ImmutableSet<Child> getChildren();
  public abstract Geometry<T> getGeometry();
  public abstract Geometry.GeometryTransformation<T> getGeometryTransformation();
}
