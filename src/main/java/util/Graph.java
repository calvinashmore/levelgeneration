/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.auto.value.AutoValue;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * A non-directed graph of nodes of type T, with a connection property of C. T
 * must have equals and hashCode defined.
 */
public class Graph<T, C> {

  private final SetMultimap<T, Connection<T,C>> allConnections = HashMultimap.create();

  /**
   * Adds all of other's connections into this graph. Modifies this graph, but leaves other unchanged.
   */
  public void union(Graph<T, C> other) {
    allConnections.putAll(other.allConnections);
  }

  public void connect(C connectionType, T node1, T node2) {
    // note: could add a disconnect, but whatever.
    Connection<T,C> connection = Connection.create(connectionType, node1, node2);
    allConnections.put(node1, connection);
    allConnections.put(node2, connection);
  }

  /**
   * Returns a path from start to end, using only connections with the specified
   * types. Returns null if no such path exists. The resulting list will contain
   * both start and end points.
   */
  @Nullable
  public List<T> findPath(T start, T end, Set<C> allowableConnections) {
    // dijkstra?
    return null;
  }

  private Set<T> findAccessibleNodes(T node, Set<C> allowableConnections) {
    return allConnections.get(node).stream()
            .filter(c -> allowableConnections.contains(c.type()))
            .map(c -> node.equals(c.getNode1()) ? c.getNode2() : c.getNode1() )
            .collect(Collectors.toSet());
  }

  @AutoValue
  abstract static class Connection<T, C> {
    abstract C type();
    abstract T getNode1();
    abstract T getNode2();

    static <T,C> Connection<T,C> create(C type, T node1, T node2) {
      return new AutoValue_Graph_Connection<>(type, node1, node2);
    }

    @Override
    public boolean equals(Object obj) {
      if(obj instanceof Connection) {
        Connection<T,C> that = (Connection<T,C>) obj;
        if(!Objects.equals(this.type(), that.type()))
          return false;
        return (Objects.equals(this.getNode1(), that.getNode1()) &&
                Objects.equals(this.getNode2(), that.getNode2()))
            || (Objects.equals(this.getNode1(), that.getNode2()) &&
                Objects.equals(this.getNode2(), that.getNode1()));
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(type(), getNode1().hashCode() + getNode2().hashCode());
    }

    private T getOpposite(T node) {
      // check?
      return node.equals(getNode1()) ? getNode2() : getNode1();
    }
  }
}
