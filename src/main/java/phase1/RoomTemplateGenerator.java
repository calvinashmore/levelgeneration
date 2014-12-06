/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import generation.ConnectionTemplate;
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
public class RoomTemplateGenerator {

  private final P1Geometry geometry;
  private final ListMultimap<P1Geometry.P1ConnectionTransformation, P1ConnectionTemplate> possibleConnections;

  public RoomTemplateGenerator(P1Geometry geometry) {
    this.geometry = geometry;
    possibleConnections = ArrayListMultimap.create();
  }

  public RoomTemplateGenerator addConnections(
          P1Geometry.P1ConnectionTransformation transform,
          Iterable<P1ConnectionTemplate> connections) {
    possibleConnections.putAll(transform, connections);
    return this;
  }

  public RoomTemplateGenerator addConnections(
          P1Geometry.P1ConnectionTransformation transform,
          P1ConnectionTemplate... connections) {
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
  private boolean isValid(P1RoomTemplate template) {
    // needs at least one connection that's not a wall
    return template.getConnections().stream()
            .map(ConnectionTemplate.ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .anyMatch(v -> v > 0);
  }

  /**
   * Generates templates using every possible combination of the possibleConnections.
   */
  public Iterable<P1RoomTemplate> generateTemplates() {
    return explode(possibleConnections).stream()
            .map(mappings -> mappings.entrySet().stream()
                .map(entry -> ConnectionTemplate.ConnectionPlacement.create(entry.getValue(), entry.getKey()))
                .collect(Collectors.toSet()))
            .map(placements -> P1RoomTemplate.create(geometry, placements))
            .filter(this::isValid)
            .collect(Collectors.toList());
  }
}
