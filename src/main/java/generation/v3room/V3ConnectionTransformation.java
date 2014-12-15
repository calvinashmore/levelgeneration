/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import generation.ConnectionTransformation;
import generation.Geometry;
import java.util.HashSet;
import java.util.Set;
import math3i.Point3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class V3ConnectionTransformation<T extends V3Room<T,?,?>> extends ConnectionTransformation<T>  {

  /**
   * Position within the geometry.
   */
  public abstract Point3i getPosition();

  /**
   * Facing vector. Must be a unit vector.
   */
  public abstract Point3i getFacing();

  public static V3ConnectionTransformation create(Point3i position, Point3i facing) {
    Preconditions.checkArgument(V3Geometry.DIRECTIONS.contains(facing));
    return new AutoValue_V3ConnectionTransformation(position, facing);
  }

  @Override
  public ConnectionTransformation<T> getOpposite() {
    return create(getPosition().add(getFacing()), getFacing().multiply(-1));
  }

  @Override
  public V3ConnectionTransformation transform(Geometry.GeometryTransformation<T> xform) {
    V3Geometry.V3GeometryTransformation xform1 = (V3Geometry.V3GeometryTransformation) xform;
    return create(xform1.getTransformation().apply(getPosition()), xform1.getTransformation().applyToVector(getFacing()));
  }

  public static <T extends V3Room<T,?,?>> Set<V3ConnectionTransformation<T>> getBoundaries(Volume3i volume) {
    Set<V3ConnectionTransformation<T>> results = new HashSet<>();
    for (Point3i point : volume.getPoints()) {
      for (Point3i direction : V3Geometry.DIRECTIONS) {
        Point3i next = point.add(direction);
        if (!volume.contains(next)) {
          results.add(create(point, direction));
        }
      }
    }
    return results;
  }

  @Override
  public String toString() {
    String directionString;
    if (getFacing().equals(V3Geometry.NORTH)) {
      directionString = "N";
    } else if (getFacing().equals(V3Geometry.SOUTH)) {
      directionString = "S";
    } else if (getFacing().equals(V3Geometry.EAST)) {
      directionString = "E";
    } else if (getFacing().equals(V3Geometry.WEST)) {
      directionString = "W";
    } else {
      throw new IllegalStateException();
    }
    return getPosition() + ":" + directionString;
  }
}
