/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.v3room.V3Geometry;
import generation.v3room.V3RoomTemplate;
import java.util.Set;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1RoomTemplate extends V3RoomTemplate<P1Room, P1KeyType> {

  @Override
  public abstract V3Geometry<P1Room> getGeometry();

  @Override
  public abstract ImmutableSet<ConnectionPlacement<P1Room, P1KeyType>> getConnections();

  @Override
  protected P1KeyType getDefaultKey() {
    return P1KeyType.NORMAL;
  }

  @Override
  protected ConnectionTemplate<P1Room, P1KeyType> getDefaultWall() {
    return P1ConnectionTemplate.WALL;
  }

  public static P1RoomTemplate create(V3Geometry<P1Room> geometry, Set<ConnectionPlacement<P1Room, P1KeyType>> connections) {
    return new AutoValue_P1RoomTemplate(geometry,
            buildConnections(geometry, connections, P1ConnectionTemplate.WALL));
  }
}
