/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Denotes a finished and rendered room.
 * @param <T> Defines the type of the room. Subclass types will be used to parameterize other types.
 */
public abstract class Room <T extends Room<T,Child,K>, Child extends Room<Child,?,?>, K extends KeyType> {

  /**
   * this would be parent type of top level, child type of lowest level.
   */
  public static abstract class EmptyType extends Room<EmptyType, EmptyType,KeyType> {
    private EmptyType() {}
  }

  public abstract RoomTemplate<T, K> getTemplate();
  public abstract ImmutableSet<Child> getChildren();
  public abstract Geometry.GeometryTransformation<T> getGeometryTransformation();

  public Geometry.TransformedGeometry<T> getTransformedGeometry() {
    return getGeometryTransformation().transform(getTemplate().getGeometry());
  }

  public Set<ConnectionPlacement<T, K>> getConnectionPlacements() {
    Set<ConnectionPlacement<T, K>> placements = new HashSet<>();

    getTemplate().getConnections().stream().forEach((generation.ConnectionPlacement<T, K> placement) -> {
      placements.add(placement.transform(getGeometryTransformation()));
    });
    return placements;
  }
}
