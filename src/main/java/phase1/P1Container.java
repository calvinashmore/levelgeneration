/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import generation.Geometry;
import generation.Room;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1Container extends Room<P1Container, P1Room>{

  public static P1Container create(Iterable<P1Room> children) {
    return new AutoValue_P1Container(ImmutableSet.copyOf(children));
  }

  // necessary to declare this for @AutoValue to do its thing
  @Override
  public abstract ImmutableSet<P1Room> getChildren();

  @Override
  public Geometry<P1Container> getGeometry() {
    return null;
  }

  @Override
  public Geometry.GeometryTransformation<P1Container> getGeometryTransformation() {
    return null;
  }
}
