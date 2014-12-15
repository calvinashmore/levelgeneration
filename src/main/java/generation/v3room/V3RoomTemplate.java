/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.ConnectionTransformation;
import generation.KeyType;
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
public abstract class V3RoomTemplate<T extends V3Room<T,?,K>, K extends KeyType> implements RoomTemplate<T, K> {

  @Override
  public abstract V3Geometry getGeometry();

  public int getNumberOfDoors() {
    return (int) getConnections().stream()
            .map(ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .filter(priority -> priority > 0)
            .count();
  }

  @Nullable
  public ConnectionTemplate<T, K> getConnectionAt(Point3i position, Point3i direction) {
    return getConnections().stream()
            .filter(placement ->
                    ((V3ConnectionTransformation) placement.getTransform()).getPosition().equals(position)
                 && ((V3ConnectionTransformation) placement.getTransform()).getFacing().equals(direction))
            .findAny()
            .map(ConnectionPlacement::getConnection)
            .orElse(null);
  }

  protected abstract K getDefaultKey();

  protected abstract ConnectionTemplate<T,K> getDefaultWall();

  @Override
  public Graph<ConnectionPlacement<T, K>, K> getConnectivityGraph() {
    return Graph.createSaturatedGraph(
            getConnections().stream().filter(
                    c -> c.getConnection().getKeyType() != null).collect(Collectors.toList()),
            getDefaultKey());
  }

  protected static <T extends V3Room<T,?,K>, K extends KeyType> ImmutableSet<ConnectionPlacement<T, K>>
        buildConnections(V3Geometry<T> geometry, Set<ConnectionPlacement<T, K>> connections, ConnectionTemplate<T,K> defaultWall) {

    for(ConnectionPlacement<T, K> connection : connections) {
      V3ConnectionTransformation transform = (V3ConnectionTransformation) connection.getTransform();
      Preconditions.checkArgument(
              geometry.getVolume().contains(transform.getPosition()),
              "Geometry %s must contain the point of a connection %s",
              geometry, connection);
      Preconditions.checkArgument(
              !geometry.getVolume().contains(transform.getPosition().add(transform.getFacing())),
              "Geometry %s must not contain the facing direction of a connection %s",
              geometry, connection);
    }

    V3ConnectionTransformation.getBoundaries(geometry.getVolume()).stream()
            .map(t -> ConnectionPlacement.create(defaultWall, (ConnectionTransformation<T>) t));

    Map<ConnectionTransformation.ConnectionTransformationEquivalence<T>,
            ConnectionPlacement<T, K>> walls =
        V3ConnectionTransformation.getBoundaries(geometry.getVolume()).stream()
            .map(t -> ConnectionPlacement.create(defaultWall, (ConnectionTransformation<T>) t) )
            .collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), Function.identity()));

    // add the external connections to the walls.
    walls.putAll(connections.stream().collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), Function.identity())));
    return ImmutableSet.copyOf(walls.values());
  }
}
