/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Mutable class representing a room of type T that has children of type Child.
 */
public abstract class InProgressRoom<T extends Room<T,Child>, Child extends Room<Child,?>> {

  private final List<Child> children = new ArrayList<>();
  //private final List<ConnectionTemplate.ConnectionPlacement<Child>> openConnections = new ArrayList<>();
  private final Map<Equivalence.Wrapper<Geometry.ConnectionTransformation<Child>>, ConnectionTemplate.ConnectionPlacement<Child>> openConnections = new HashMap<>();

  public void addChild(Child child) {
    children.add(child);

    Set<ConnectionTemplate.ConnectionPlacement<Child>> childPlacements = child.getConnectionPlacements();

    // connections from the child that are matched by openConnections.
    Set<ConnectionTemplate.ConnectionPlacement<Child>> matchedConnections = childPlacements.stream()
            .filter(t -> openConnections.containsKey(t.getTransform().getEquivalence()))
            .collect(Collectors.toSet());

    // connections that are not yet matched
    Set<ConnectionTemplate.ConnectionPlacement<Child>> unmatchedConnections =
            Sets.difference(childPlacements, matchedConnections);

    // assume at this stage that all connections match; any unmatched connections are open.
    matchedConnections.stream().forEach(placement ->
            openConnections.remove(placement.getTransform().getEquivalence()));
    unmatchedConnections.stream().forEach(placement -> addConnection(placement));
  }

  public void addConnection(ConnectionTemplate.ConnectionPlacement<Child> connection) {
    openConnections.put(connection.getTransform().getEquivalence(), connection);
  }


  public ImmutableList<Child> getChildren() {
    return ImmutableList.copyOf(children);
  }

  @Nullable
  public ConnectionTemplate.ConnectionPlacement<Child> getConnectionAt(Geometry.ConnectionTransformation<Child> connectionTransform) {
    return openConnections.get(connectionTransform.getEquivalence());
  }

  public abstract boolean geometryMatches(Geometry.TransformedGeometry<Child> geometry);

  /**
   * Return true if the connections given by the provided template can correctly match the connections already present.
   * Logically, we check all connections whose placement match, and then fail if the connection types do not match.
   */
  public boolean connectionsMatch(RoomTemplate<Child> template, Geometry.GeometryTransformation<Child> transform) {

    List<ConnectionTemplate.ConnectionPlacement<Child>> roomConnections = template.getConnections().stream()
            .map(c -> c.transform(transform))
            .collect(Collectors.toList());

    // check that all room connections match with the parent connections
    for(ConnectionTemplate.ConnectionPlacement<Child> roomConnection : roomConnections) {
      ConnectionTemplate.ConnectionPlacement<Child> parentConnection = getConnectionAt(roomConnection.getTransform());
      if(parentConnection != null && !parentConnection.matches(roomConnection)) {
        return false;
      }
    }

    Map<Equivalence.Wrapper<Geometry.ConnectionTransformation<Child>>, ConnectionTemplate.ConnectionPlacement<Child>> roomConnectionMap =
            roomConnections.stream().collect(Collectors.toMap(t -> t.getTransform().getEquivalence(), t -> t));

    // check that all parent connections match with the child connections
    for(ConnectionTemplate.ConnectionPlacement<Child> parentConnection : openConnections.values()) {
      ConnectionTemplate.ConnectionPlacement<Child> roomConnection = roomConnectionMap.get(parentConnection.getTransform().getEquivalence());
      if(roomConnection != null && !roomConnection.matches(parentConnection))
        return false;
    }

    return true;
  }

  /**
   * Return true if the child template and transform can be placed in this in-progress room.
   */
  public boolean isValidChildPlacement(RoomTemplate<Child> template, Geometry.GeometryTransformation<Child> transform) {
    return geometryMatches(transform.transform(template.getGeometry()))
        && connectionsMatch(template, transform);
  }

  /**
   * Return true if this in progress room can be successfully built.
   */
  public abstract boolean isValid();

  /**
   * Construct the actual room from this in-progress room. Throws an
   * IllegalStateException if the room is not valid.
   */
  public abstract T build();
}
