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
import util.Graph;

/**
 * Mutable class representing a room of type T that has children of type Child.
 */
public abstract class InProgressRoom<
        T extends Room<T,Child,?>,
        Child extends Room<Child,?,ChildKey>,
        ChildKey extends KeyType> {

  private final List<Child> children = new ArrayList<>();
  private final Map<ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
          ConnectionPlacement<Child,ChildKey>> parentConnections = new HashMap<>();
  private final Map<ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
          ConnectionPlacement<Child,ChildKey>> openConnections = new HashMap<>();

  public void addChild(Child child) {
    children.add(child);

    Set<ConnectionPlacement<Child, ChildKey>> childPlacements =
        child.getConnectionPlacements();

    // connections from the child that are matched by openConnections.
    Set<ConnectionPlacement<Child, ChildKey>> matchedConnections =
        childPlacements.stream()
            .filter((generation.ConnectionPlacement<Child, ChildKey> t) -> openConnections.containsKey(t.getTransform().getEquivalence()))
            .collect(Collectors.toSet());

    // connections that are not yet matched
    Set<ConnectionPlacement<Child, ChildKey>> unmatchedConnections =
            Sets.difference(childPlacements, matchedConnections);

    // assume at this stage that all connections match; any unmatched connections are open.
    matchedConnections.stream().forEach((generation.ConnectionPlacement<Child, ChildKey> placement) ->
            openConnections.remove(placement.getTransform().getEquivalence()));
    unmatchedConnections.stream().forEach((generation.ConnectionPlacement<Child, ChildKey> placement) -> addConnection(placement));
  }

  public void removeChild(Child child) {
    if(!children.remove(child)) {
      throw new IllegalArgumentException("Attempting to remove child that is not present: "+child);
    }

    Set<ConnectionPlacement<Child, ChildKey>> childPlacements =
        child.getConnectionPlacements();

    // connections from the child that are matched by openConnections.
    Set<ConnectionPlacement<Child, ChildKey>> matchedConnections =
        childPlacements.stream()
            .filter((generation.ConnectionPlacement<Child, ChildKey> t) -> openConnections.containsKey(t.getTransform().getEquivalence()))
            .collect(Collectors.toSet());

    // connections that are not yet matched
    Set<ConnectionPlacement<Child, ChildKey>> unmatchedConnections =
            Sets.difference(childPlacements, matchedConnections);

    // We are removing all open connections that are only opened by the child we are removing
    // and adding open connections that will be opened by removing the child.
    unmatchedConnections.stream().forEach((generation.ConnectionPlacement<Child, ChildKey> placement) ->
            openConnections.remove(placement.getTransform().getEquivalence()));
    matchedConnections.stream().forEach((generation.ConnectionPlacement<Child, ChildKey> placement) -> addConnection(placement));

    // TODO: tests, validation (maybe check that state is as we expect it?)
  }

  /**
   * Add a connection connecting this room to the outside world.
   */
  public void addParentConnection(ConnectionPlacement<Child, ChildKey> connection) {
    parentConnections.put(connection.getTransform().getEquivalence(), connection);
    addConnection(connection);
  }

  private void addConnection(ConnectionPlacement<Child, ChildKey> connection) {
    openConnections.put(connection.getTransform().getEquivalence(), connection);
  }

  public Collection<ConnectionPlacement<Child, ChildKey>> getOpenConnections() {
    return openConnections.values();
  }

  public Map<ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
        ConnectionPlacement<Child, ChildKey>> getParentConnections() {
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
  public List<ConnectionPlacement<Child, ChildKey>> getHighestPriorityConnections() {
    if(openConnections.isEmpty())
      return ImmutableList.of();

    int maximumPriority = Ordering.natural().max(
        openConnections.values().stream()
            .map(ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .collect(Collectors.toList()));

    return openConnections.values().stream()
            .filter((generation.ConnectionPlacement<Child, ChildKey> connection) -> connection.getConnection().getMatchPriority() == maximumPriority)
            .collect(Collectors.toList());
  }

  /**
   * Return the connection at the given transform if it exists, null otherwise.
   */
  @Nullable
  public ConnectionPlacement<Child, ChildKey> getConnectionAt(ConnectionTransformation<Child> connectionTransform) {
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
  public boolean connectionsMatch(RoomTemplate<Child, ChildKey> template, Geometry.GeometryTransformation<Child> transform) {

    List<ConnectionPlacement<Child, ChildKey>> roomConnections = template.getConnections().stream()
            .map((generation.ConnectionPlacement<Child, ChildKey> c) -> c.transform(transform))
            .collect(Collectors.toList());

    if (!checkRoomConnectionsMatch(roomConnections)) {
      return false;
    }

    Map<ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
            ConnectionPlacement<Child, ChildKey>> roomConnectionMap =
            roomConnections.stream().collect(Collectors.toMap((generation.ConnectionPlacement<Child, ChildKey> t) -> t.getTransform().getEquivalence(), Function.identity()));

    if (!checkOpenConnectionsMatch(roomConnectionMap)) {
      return false;
    }

    return true;
  }

  /**
   * Check that all of the outgoing connections from a child room will match the
   * open connections in the in-progress room.
   */
  private boolean checkRoomConnectionsMatch(List<ConnectionPlacement<Child, ChildKey>> roomConnections) {
    // check that all room connections match with the parent connections
    for (ConnectionPlacement<Child, ChildKey> roomConnection : roomConnections) {
      ConnectionPlacement<Child, ChildKey> parentConnection = getConnectionAt(roomConnection.getTransform());
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
          Map<ConnectionTransformation.ConnectionTransformationEquivalence<Child>,
          ConnectionPlacement<Child, ChildKey>> roomConnectionMap) {
    // check that all parent connections match with the child connections
    for (ConnectionPlacement<Child, ChildKey> parentConnection : openConnections.values()) {
      ConnectionPlacement<Child, ChildKey> roomConnection =
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
  public boolean isValidChildPlacement(RoomTemplate<Child, ChildKey> template, Geometry.GeometryTransformation<Child> transform) {
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

  /**
   * Builds a graph of all connections represented by this in-progress room.
   * This does the full work of creating all the connections, etc etc etc.
   */
  public Graph<ConnectionPlacement<Child,ChildKey>, ChildKey> buildConnectionGraph() {
    Graph<ConnectionPlacement<Child,ChildKey>, ChildKey> graph = new Graph<>();

    for (Child child : children) {
      // add internal connections
      graph.union( child.getTemplate().getConnectivityGraph().transformNodes(
              n -> n.transform(child.getGeometryTransformation())));

      // add external connections
      // note that this is going to add each internal connection twice.
      for (ConnectionPlacement<Child, ChildKey> placement : child.getConnectionPlacements()) {
        if(placement.getConnection().getKeyType() != null) {
          graph.connect(placement.getConnection().getKeyType(), placement, placement.getOpposite());
        }
      }
    }

    return graph;
  }
}
