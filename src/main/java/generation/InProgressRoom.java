/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import phase1.P1Room;

/**
 * Mutable class representing a room of type T that has children of type Child.
 */
public abstract class InProgressRoom<T extends Room<T,Child>, Child extends Room<Child,?>> {

  private final List<Child> children = new ArrayList<>();
  private final List<ConnectionTemplate.ConnectionPlacement<Child>> openConnections = new ArrayList<>();

  public void addChild(Child child) {
    children.add(child);

    Set<ConnectionTemplate.ConnectionPlacement<Child>> childPlacements = child.getConnectionPlacements();

    // assume at this stage that all connections match; any unmatched connections are open.
    List<ConnectionTemplate.ConnectionPlacement<Child>> matchedConnections = childPlacements.stream()
            .filter(t1 -> openConnections.stream().anyMatch(t1::matches))
            .collect(Collectors.toList());

    openConnections.removeIf(matchedConnections::contains);
    openConnections.addAll(childPlacements.stream()
            .filter(t -> !matchedConnections.contains(t))
            .collect(Collectors.toList()));
  }

  public ImmutableList<Child> getChildren() {
    return ImmutableList.copyOf(children);
  }

  public List<ConnectionTemplate.ConnectionPlacement<Child>> getOpenConnections() {
    return openConnections;
  }

  public abstract boolean geometryMatches(Geometry.TransformedGeometry<Child> geometry);

  /**
   * Return true if the connections given by the provided template can correctly match the connections already present.
   * Logically, we check all connections whose placement match, and then fail if the connection types do not match.
   */
  public boolean connectionsMatch(RoomTemplate<Child> template, Geometry.GeometryTransformation<Child> transform) {

    List<ConnectionTemplate.ConnectionPlacement<Child>> parentConnections = getOpenConnections();
    List<ConnectionTemplate.ConnectionPlacement<Child>> roomConnections = template.getConnections().stream()
            .map(c -> c.transform(transform))
            .collect(Collectors.toList());

    // fail if there are any connections whose transform matches, but whose connection does not.
    if (parentConnections.stream().anyMatch(connection -> (!roomConnections.stream()
            .filter(connection::transformMatches)
            .allMatch(connection::matches)))) {
      return false;
    }

    // I think this is how the functional actually works for the thing below;
    // keep in case needed to debug.
//    for(ConnectionTemplate.ConnectionPlacement<Child> connection : roomConnections) {
//      if(!parentConnections.stream()
//              .filter(connection::transformMatches)
//              .allMatch(connection::matches))
//        return false;
//    }

    if (roomConnections.stream().anyMatch(connection -> (!parentConnections.stream()
            .filter(connection::transformMatches)
            .allMatch(connection::matches)))) {
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
