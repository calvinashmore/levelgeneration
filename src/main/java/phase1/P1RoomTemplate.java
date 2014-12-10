/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.ConnectionTransformation;
import generation.RoomTemplate;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import math3i.Point3i;
import util.Graph;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1RoomTemplate implements RoomTemplate<P1Room, P1KeyType> {

  @Override
  public abstract P1Geometry getGeometry();

  @Override
  public abstract ImmutableSet<ConnectionPlacement<P1Room, P1KeyType>> getConnections();

  public int getNumberOfDoors() {
    return (int) getConnections().stream()
            .map(ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .filter(priority -> priority > 0)
            .count();
  }

  @Nullable
  public P1ConnectionTemplate getConnectionAt(Point3i position, Point3i direction) {
    return (P1ConnectionTemplate) getConnections().stream()
            .filter(placement ->
                    ((P1ConnectionTransformation) placement.getTransform()).getPosition().equals(position)
                 && ((P1ConnectionTransformation) placement.getTransform()).getFacing().equals(direction))
            .findAny()
            .map(ConnectionPlacement::getConnection)
            .orElse(null);
  }

  @Override
  public Graph<ConnectionPlacement<P1Room, P1KeyType>, P1KeyType> getConnectivityGraph() {
    return Graph.createSaturatedGraph(
            getConnections().stream().filter(
                    c -> c.getConnection().getKeyType() != null).collect(Collectors.toList()),
            P1KeyType.NORMAL);
  }

  public static P1RoomTemplate create(P1Geometry geometry, Set<ConnectionPlacement<P1Room, P1KeyType>> connections) {

    for(ConnectionPlacement<P1Room, P1KeyType> connection : connections) {
      P1ConnectionTransformation transform = (P1ConnectionTransformation) connection.getTransform();
      Preconditions.checkArgument(
              geometry.getVolume().contains(transform.getPosition()),
              "Geometry %s must contain the point of a connection %s",
              geometry, connection);
      Preconditions.checkArgument(
              !geometry.getVolume().contains(transform.getPosition().add(transform.getFacing())),
              "Geometry %s must not contain the facing direction of a connection %s",
              geometry, connection);
    }

    Map<ConnectionTransformation.ConnectionTransformationEquivalence<P1Room>,
            ConnectionPlacement<P1Room, P1KeyType>> walls =
        P1ConnectionTransformation.getBoundaries(geometry.getVolume()).stream()
            .map(t -> ConnectionPlacement.create(P1ConnectionTemplate.WALL, t) )
            .collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), Function.identity()));

    // add the external connections to the walls.
    walls.putAll(connections.stream().collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), Function.identity())));

    return new AutoValue_P1RoomTemplate(geometry, ImmutableSet.copyOf(walls.values()));
  }
}
