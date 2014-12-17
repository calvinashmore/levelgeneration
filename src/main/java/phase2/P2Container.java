/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import generation.Geometry;
import generation.KeyType;
import generation.Room;
import generation.RoomTemplate;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P2Container extends Room<P2Container, P2Room, KeyType> {

  public static P2Container create(Iterable<P2Room> rooms) {
    return new AutoValue_P2Container(ImmutableSet.copyOf(rooms));
  }

  @Override
  public RoomTemplate<P2Container, KeyType> getTemplate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract ImmutableSet<P2Room> getChildren();

  @Override
  public Geometry.GeometryTransformation<P2Container> getGeometryTransformation() {
    throw new UnsupportedOperationException();
  }
}
