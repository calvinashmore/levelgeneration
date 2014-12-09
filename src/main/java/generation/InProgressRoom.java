/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Mutable class representing a room of type T that has children of type Child.
 */
public abstract class InProgressRoom<T extends Room<T,Child>, Child extends Room<Child,?>> {

  private final List<Child> children = new ArrayList<>();
  private final Map<Geometry.ConnectionTransformation.ConnectionTransformationEquivalence<Child>, ConnectionTemplate.ConnectionPlacement<Child>> parentConnections = new HashMap<>();
  private final Map<Geometry.ConnectionTransformation.ConnectionTransformationEquivalence<Child>, ConnectionTemplate.ConnectionPlacement<Child>> openConnections = new HashMap<>();

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

  public void removeChild(Child child) {
    if(!children.remove(child)) {
      throw new IllegalArgumentException("Attempting to remove child that is not present: "+child);
    }

    Set<ConnectionTemplate.ConnectionPlacement<Child>> childPlacements = child.getConnectionPlacements();

    // connections from the child that are matched by openConnections.
    Set<ConnectionTemplate.ConnectionPlacement<Child>> matchedConnections = childPlacements.stream()
            .filter(t -> openConnections.containsKey(t.getTransform().getEquivalence()))
            .collect(Collectors.toSet());

    // connections that are not yet matched
    Set<ConnectionTemplate.ConnectionPlacement<Child>> unmatchedConnections =
            Sets.difference(childPlacements, matchedConnections);

    // We are removing all open connections that are only opened by the child we are removing
    // and adding open connections that will be opened by removing the child.
    unmatchedConnections.stream().forEach(placement ->
            openConnections.remove(placement.getTransform().getEquivalence()));
    matchedConnections.stream().forEach(placement -> addConnection(placement));

    // TODO: tests, validation (maybe check that state is as we expect it?)
  }

  /**
   * Add a connection connecting this room to the outside world.
   */
  public void addParentConnection(ConnectionTemplate.ConnectionPlacement<Child> connection) {
    parentConnections.put(connection.getTransform().getEquivalence(), connection);
    addConnection(connection);
  }

  private void addConnection(ConnectionTemplate.ConnectionPlacement<Child> connection) {
    openConnections.put(connection.getTransform().getEquivalence(), connection);
  }

  public Collection<ConnectionTemplate.ConnectionPlacement<Child>> getOpenConnections() {
    return openConnections.values();
  }

  public Map<Geometry.ConnectionTransformation.ConnectionTransformationEquivalence<Child>, ConnectionTemplate.ConnectionPlacement<Child>> getParentConnections() {
    return ImmutableMap.copyOf(parentConnections);
  }

  public ImmutableList<Child> getChildren() {
    return ImmutableList.copyOf(children);
  }

  /**
   * Return a list of the open connections that have high priority. High
   * priority connections should be matched before the low priority ones, so
   * that we can encourage creation of the right kinds of features. Note that
   * walls are also connections, and should have low priority.
   */
  public List<ConnectionTemplate.ConnectionPlacement<Child>> getHighestPriorityConnections() {
    if(openConnections.isEmpty())
      return ImmutableList.of();

    int maximumPriority = Ordering.natural().max(
        openConnections.values().stream()
            .map(ConnectionTemplate.ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .collect(Collectors.toList()));

    return openConnections.values().stream()
            .filter(connection -> connection.getConnection().getMatchPriority() == maximumPriority)
            .collect(Collectors.toList());
  }

  /**
   * Return the connection at the given transform if it exists, null otherwise.
   */
  @Nullable
  public ConnectionTemplate.ConnectionPlacement<Child> getConnectionAt(Geometry.ConnectionTransformation<Child> connectionTransform) {
    return openConnections.get(connectionTransform.getEquivalence());
  }

  /**
   * Return true if the given geometry can fit in this in progress room. This does not account for anything connection related.
   */
  public abstract boolean geometryMatches(Geometry.TransformedGeometry<Child> geometry);

  /**
   * Return true if the connections given by the provided template can correctly match the connections already present.
   * Logically, we check all connections whose placement match, and then fail if the connection types do not match.
   */
  public boolean connectionsMatch(RoomTemplate<Child> template, Geometry.GeometryTransformation<Child> transform) {

    List<ConnectionTemplate.ConnectionPlacement<Child>> roomConnections = template.getConnections().stream()
            .map(c -> c.transform(transform))
            .collect(Collectors.toList());

    if (!checkRoomConnectionsMatch(roomConnections)) {
      return false;
    }

    Map<Geometry.ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
            ConnectionTemplate.ConnectionPlacement<Child>> roomConnectionMap =
            roomConnections.stream().collect(Collectors.toMap(t -> t.getTransform().getEquivalence(), Function.identity()));

    if (!checkOpenConnectionsMatch(roomConnectionMap)) {
      return false;
    }

    return true;
  }

  /**
   * Check that all of the outgoing connections from a child room will match the
   * open connections in the in-progress room.
   */
  private boolean checkRoomConnectionsMatch(List<ConnectionTemplate.ConnectionPlacement<Child>> roomConnections) {
    // check that all room connections match with the parent connections
    for (ConnectionTemplate.ConnectionPlacement<Child> roomConnection : roomConnections) {
      ConnectionTemplate.ConnectionPlacement<Child> parentConnection = getConnectionAt(roomConnection.getTransform());
      if (parentConnection != null && !parentConnection.matches(roomConnection)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check that the open connection in the in-progress room will match the
   * connections of the child room. This is necessary in addition to
   * {@link #checkRoomConnectionsMatch(java.util.List)} so that any empty
   * connections are matched. This is less important when there will never be an
   * empty connection (i.e.) there are always walls, however if connection
   * matching is non-commutative, then this will be important.
   */
  private boolean checkOpenConnectionsMatch(
          Map<Geometry.ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
          ConnectionTemplate.ConnectionPlacement<Child>> roomConnectionMap) {
    // check that all parent connections match with the child connections
    for (ConnectionTemplate.ConnectionPlacement<Child> parentConnection : openConnections.values()) {
      ConnectionTemplate.ConnectionPlacement<Child> roomConnection =
              roomConnectionMap.get(parentConnection.getTransform().getEquivalence());
      if (roomConnection != null && !roomConnection.matches(parentConnection)) {
        return false;
      }
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
   * Return true if this in progress room can be successfully built. Default
   * implementation returns true if there are no open connections. Subclasses
   * should override if they want to require specialized conditions.
   */
  public boolean isValid() {
    return openConnections.isEmpty();
  }

  /**
   * Construct the actual room from this in-progress room. Throws an
   * IllegalStateException if the room is not valid.
   */
  public abstract T build();
}
