/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.v3room.V3ConnectionTransformation;
import generation.v3room.V3Geometry;
import generation.v3room.V3RoomTemplate;
import java.util.Set;
import math3i.Point3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P2RoomTemplate extends V3RoomTemplate<P2Room, P2KeyType> {

  @Override
  public abstract V3Geometry<P2Room> getGeometry();

  @Override
  public abstract ImmutableSet<ConnectionPlacement<P2Room, P2KeyType>> getConnections();

  @Override
  protected P2KeyType getDefaultKey() {
    return P2KeyType.OPEN;
  }

  @Override
  protected ConnectionTemplate<P2Room, P2KeyType> getDefaultWall() {
    throw new UnsupportedOperationException();
  }

  public static P2RoomTemplate create(V3Geometry<P2Room> geometry, Set<ConnectionPlacement<P2Room, P2KeyType>> connections) {
    return new AutoValue_P2RoomTemplate(geometry,
            buildConnections(geometry, connections, P2ConnectionTemplate.NONE));
  }

  public static P2RoomTemplate create(P2ConnectionTemplate north, P2ConnectionTemplate west, P2ConnectionTemplate south, P2ConnectionTemplate east) {
    return create(V3Geometry.create(Volume3i.UNIT_BOX), ImmutableSet.of(
            ConnectionPlacement.create(north, V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.NORTH)),
            ConnectionPlacement.create(west, V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.WEST)),
            ConnectionPlacement.create(south, V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.SOUTH)),
            ConnectionPlacement.create(east, V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.EAST))
    ));
  }
}
