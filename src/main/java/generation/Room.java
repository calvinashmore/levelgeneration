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
public abstract class Room <T extends Room<T,Child>, Child extends Room<Child,?>> {

  /**
   * this would be parent type of top level, child type of lowest level.
   */
  public static abstract class EmptyType extends Room<EmptyType, EmptyType> {
    private EmptyType() {}
  }

  public abstract RoomTemplate<T> getTemplate();
  public abstract ImmutableSet<Child> getChildren();
  public abstract Geometry.GeometryTransformation<T> getGeometryTransformation();

  public Geometry.TransformedGeometry<T> getTransformedGeometry() {
    return getGeometryTransformation().transform(getTemplate().getGeometry());
  }

  public Set<ConnectionTemplate.ConnectionPlacement<T>> getConnectionPlacements() {
    Set<ConnectionTemplate.ConnectionPlacement<T>> placements = new HashSet<>();

    getTemplate().getConnections().stream().forEach((placement) -> {
      placements.add(placement.transform(getGeometryTransformation()));
    });
    return placements;
  }
}
