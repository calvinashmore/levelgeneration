/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

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
public abstract class P1ConnectionTransformation extends ConnectionTransformation<P1Room> {

  /**
   * Position within the geometry.
   */
  public abstract Point3i getPosition();

  /**
   * Facing vector. Must be a unit vector.
   */
  public abstract Point3i getFacing();

  public static P1ConnectionTransformation create(Point3i position, Point3i facing) {
    Preconditions.checkArgument(P1Geometry.DIRECTIONS.contains(facing));
    return new AutoValue_P1ConnectionTransformation(position, facing);
  }

  @Override
  public ConnectionTransformation<P1Room> getOpposite() {
    return create(getPosition().add(getFacing()), getFacing().multiply(-1));
  }

  @Override
  public P1ConnectionTransformation transform(Geometry.GeometryTransformation<P1Room> xform) {
    P1Geometry.P1GeometryTransformation xform1 = (P1Geometry.P1GeometryTransformation) xform;
    return create(xform1.getTransformation().apply(getPosition()), xform1.getTransformation().applyToVector(getFacing()));
  }

  public static Set<P1ConnectionTransformation> getBoundaries(Volume3i volume) {
    Set<P1ConnectionTransformation> results = new HashSet<>();
    for (Point3i point : volume.getPoints()) {
      for (Point3i direction : P1Geometry.DIRECTIONS) {
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
    if (getFacing().equals(P1Geometry.NORTH)) {
      directionString = "N";
    } else if (getFacing().equals(P1Geometry.SOUTH)) {
      directionString = "S";
    } else if (getFacing().equals(P1Geometry.EAST)) {
      directionString = "E";
    } else if (getFacing().equals(P1Geometry.WEST)) {
      directionString = "W";
    } else {
      throw new IllegalStateException();
    }
    return getPosition() + ":" + directionString;
  }

}
