/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionTemplate;
import generation.Geometry;
import generation.RoomTemplate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import math3i.Point3i;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1RoomTemplate implements RoomTemplate<P1Room> {

  @Override
  public abstract P1Geometry getGeometry();

  @Override
  public abstract ImmutableSet<ConnectionTemplate.ConnectionPlacement<P1Room>> getConnections();

  public int getNumberOfDoors() {
    return (int) getConnections().stream()
            .map(ConnectionTemplate.ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .filter(priority -> priority > 0)
            .count();
  }

  @Nullable
  public P1ConnectionTemplate getConnectionAt(Point3i position, Point3i direction) {
    return (P1ConnectionTemplate) getConnections().stream()
            .filter(placement ->
                    ((P1Geometry.P1ConnectionTransformation) placement.getTransform()).getPosition().equals(position)
                 && ((P1Geometry.P1ConnectionTransformation) placement.getTransform()).getFacing().equals(direction))
            .findAny()
            .map(ConnectionTemplate.ConnectionPlacement::getConnection)
            .orElse(null);
  }

  public static P1RoomTemplate create(P1Geometry geometry, Set<ConnectionTemplate.ConnectionPlacement<P1Room>> connections) {

    for(ConnectionTemplate.ConnectionPlacement<P1Room> connection : connections) {
      P1Geometry.P1ConnectionTransformation transform = (P1Geometry.P1ConnectionTransformation) connection.getTransform();
      Preconditions.checkArgument(
              geometry.getVolume().contains(transform.getPosition()),
              "Geometry %s must contain the point of a connection %s",
              geometry, connection);
      Preconditions.checkArgument(
              !geometry.getVolume().contains(transform.getPosition().add(transform.getFacing())),
              "Geometry %s must not contain the facing direction of a connection %s",
              geometry, connection);
    }

    Map<Geometry.ConnectionTransformation.ConnectionTransformationEquivalence<P1Room>,
            ConnectionTemplate.ConnectionPlacement<P1Room>> walls =
        P1Geometry.P1ConnectionTransformation.getBoundaries(geometry.getVolume()).stream()
            .map(t -> ConnectionTemplate.ConnectionPlacement.create(P1ConnectionTemplate.WALL, t) )
            .collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), p -> p));

    // add the external connections to the walls.
    walls.putAll(connections.stream().collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), p -> p)));

    return new AutoValue_P1RoomTemplate(geometry, ImmutableSet.copyOf(walls.values()));
  }
}
