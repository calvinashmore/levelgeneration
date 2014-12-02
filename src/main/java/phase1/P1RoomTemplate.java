/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionTemplate;
import generation.Geometry;
import generation.RoomTemplate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

  public static P1RoomTemplate create(P1Geometry geometry, Set<ConnectionTemplate.ConnectionPlacement<P1Room>> connections) {

    Map<Equivalence.Wrapper<Geometry.ConnectionTransformation<P1Room>>, ConnectionTemplate.ConnectionPlacement<P1Room>> walls =
        P1Geometry.P1ConnectionTransformation.getBoundaries(geometry.getVolume()).stream()
            .map(t -> ConnectionTemplate.ConnectionPlacement.create(P1ConnectionTemplate.WALL, t) )
            .collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), p -> p));

    // add the external connections to the walls.
    walls.putAll(connections.stream().collect(Collectors.toMap(p -> p.getTransform().getEquivalence(), p -> p)));

    return new AutoValue_P1RoomTemplate(geometry, ImmutableSet.copyOf(walls.values()));
  }
}
