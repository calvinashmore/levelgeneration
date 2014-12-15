/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import generation.Room;
import generation.v3room.V3Geometry;
import generation.v3room.V3Room;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1Room extends V3Room<P1Room, Room.EmptyType, P1KeyType> {

  public static P1Room create(P1RoomTemplate template, V3Geometry.V3GeometryTransformation<P1Room> xform) {
    return new AutoValue_P1Room(template, xform);
  }

  @Override
  public ImmutableSet<EmptyType> getChildren() {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract P1RoomTemplate getTemplate();

  @Override
  public abstract V3Geometry.V3GeometryTransformation<P1Room> getGeometryTransformation();
}
