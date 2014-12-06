/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This generates room templates where there a number of different connection types.
 * Note for when using this: the system knows how to rotate rooms when placing them,
 * so templates produced by this should not be rotational transforms of each other.
 */
public abstract class RoomTemplateGenerator<T extends Room<T,?>> {

  private final Geometry<T> geometry;
  private final ListMultimap<Geometry.ConnectionTransformation<T>, ConnectionTemplate<T>> possibleConnections;

  public RoomTemplateGenerator(Geometry<T> geometry) {
    this.geometry = geometry;
    possibleConnections = ArrayListMultimap.create();
  }

  protected Geometry<T> getGeometry() {
    return geometry;
  }

  public RoomTemplateGenerator<T> addConnections(
          Geometry.ConnectionTransformation<T> transform,
          Iterable<ConnectionTemplate<T>> connections) {
    possibleConnections.putAll(transform, connections);
    return this;
  }

  public RoomTemplateGenerator addConnections(
          Geometry.ConnectionTransformation<T> transform,
          ConnectionTemplate<T>... connections) {
    possibleConnections.putAll(transform, Arrays.asList(connections));
    return this;
  }

  /**
   * Turns a multimap into a collection of maps, which contain all possible combinations
   * of the individual elements. For instance, if the multimap is
   * {@code {k1:[a,b],k2:[c,d]}}, the explode would be
   * {@code [{k1:a,k2:c}, {k1:b,k2:c}, {k1:a,k2:d}, {k1:b,k2:d}]}.
   *
   * Because I am a bad person, this returns mutable types.
   */
  private static <K, V> Set<Map<K, V>> explode(ListMultimap<K, V> multimap) {

    if(multimap.isEmpty())
      return ImmutableSet.of();

    K key = Iterables.getFirst(multimap.keySet(), null);
    List<V> values = multimap.get(key);

    // A multimap that does not contain the key we just removed.
    ListMultimap<K,V> next = Multimaps.filterKeys(multimap, t -> !t.equals(key));
    Set<Map<K, V>> nextExploded = explode(next);
    Set<Map<K, V>> results = new HashSet<>();

    for (V value : values) {
      if(nextExploded.isEmpty()) {
        Map<K,V> initialMap = new HashMap<>();
        initialMap.put(key, value);
        results.add(initialMap);
      } else {
        for (Map<K,V> otherMap : nextExploded) {
          Map<K,V> initialMap = new HashMap<>(otherMap);
          initialMap.put(key, value);
          results.add(initialMap);
        }
      }
    }

    return results;
  }

  /**
   * Returns true if the given template is a valid output of this generator.
   */
  abstract protected boolean isValid(RoomTemplate<T> template);

  abstract protected RoomTemplate<T> createTemplate(Set<ConnectionTemplate.ConnectionPlacement<T>> placements);

  /**
   * Generates templates using every possible combination of the possibleConnections.
   */
  public Iterable<RoomTemplate<T>> generateTemplates() {
    return explode(possibleConnections).stream()
            .map(mappings -> mappings.entrySet().stream()
                .map(entry -> ConnectionTemplate.ConnectionPlacement.create(entry.getValue(), entry.getKey()))
                .collect(Collectors.toSet()))
            .map(this::createTemplate)
            .filter(this::isValid)
            .collect(Collectors.toList());
  }
}
