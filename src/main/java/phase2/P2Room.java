/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

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
public abstract class P2Room extends V3Room<P2Room, Room.EmptyType, P2KeyType> {

  public static P2Room create(P2RoomTemplate template, V3Geometry.V3GeometryTransformation<P2Room> xform) {
    return new AutoValue_P2Room(template, xform);
  }

  @Override
  public ImmutableSet<EmptyType> getChildren() {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract P2RoomTemplate getTemplate();

  @Override
  public abstract V3Geometry.V3GeometryTransformation<P2Room> getGeometryTransformation();
}
