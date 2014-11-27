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
public abstract class P1Room extends Room<P1Room, Room.EmptyType> {

  public P1Room create(P1Geometry geometry, P1Geometry.P1GeometryTransformation xform) {
    return new AutoValue_P1Room(geometry, xform);
  }

  @Override
  public ImmutableSet<EmptyType> getChildren() {
    return null;
  }

  @Override
  public abstract P1Geometry getGeometry();

  @Override
  public abstract P1Geometry.P1GeometryTransformation getGeometryTransformation();

}
