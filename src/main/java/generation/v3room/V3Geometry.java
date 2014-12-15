/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import generation.Geometry;
import generation.Geometry.TransformedGeometry;
import java.util.Set;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class V3Geometry<T extends V3Room<T,?,?>> implements Geometry<T>, TransformedGeometry<T> {

  public static final Point3i SOUTH = Point3i.UNIT_Y;
  public static final Point3i NORTH = Point3i.UNIT_Y.multiply(-1);
  public static final Point3i EAST = Point3i.UNIT_X;
  public static final Point3i WEST = Point3i.UNIT_X.multiply(-1);
  public static final Set<Point3i> DIRECTIONS = ImmutableSet.of(SOUTH, NORTH, EAST, WEST);

  public abstract Volume3i getVolume();

  public static V3Geometry create(Volume3i volume) {
    return new AutoValue_V3Geometry(volume);
  }

  @Override
  public String toString() {
    return "size:"+getVolume().size();
  }

  /**
   * Convenience wrapper.
   */
  public static V3ConnectionTransformation connection(Point3i position, Point3i facing) {
    return V3ConnectionTransformation.create(position, facing);
  }

  @AutoValue
  public abstract static class V3GeometryTransformation<T extends V3Room<T,?,?>> implements GeometryTransformation<T> {
    public abstract Transformation3i getTransformation();

    public static V3GeometryTransformation create(Transformation3i transformation) {
      return new AutoValue_V3Geometry_V3GeometryTransformation(transformation);
    }

    @Override
    public V3GeometryTransformation transform(GeometryTransformation<T> xform) {
      V3GeometryTransformation xform1 = (V3GeometryTransformation) xform;
      return create(this.getTransformation().compose(xform1.getTransformation()));
    }

    @Override
    public V3Geometry transform(Geometry<T> geometry) {
      V3Geometry geometry1 = (V3Geometry) geometry;
      return V3Geometry.create(geometry1.getVolume().transform(getTransformation()));
    }
  }
}
