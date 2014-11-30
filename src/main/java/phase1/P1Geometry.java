/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import generation.Geometry;
import generation.Geometry.TransformedGeometry;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1Geometry implements Geometry<P1Room>, TransformedGeometry<P1Room> {
  public abstract Volume3i getVolume();

  public static P1Geometry create(Volume3i volume) {
    return new AutoValue_P1Geometry(volume);
  }

  @AutoValue
  public abstract static class P1GeometryTransformation implements GeometryTransformation<P1Room> {
    public abstract Transformation3i getTransformation();

    public static P1GeometryTransformation create(Transformation3i transformation) {
      return new AutoValue_P1Geometry_P1GeometryTransformation(transformation);
    }

    @Override
    public P1GeometryTransformation transform(GeometryTransformation<P1Room> xform) {
      P1GeometryTransformation xform1 = (P1GeometryTransformation) xform;
      return create(this.getTransformation().compose(xform1.getTransformation()));
    }

    @Override
    public TransformedGeometry<P1Room> transform(Geometry<P1Room> geometry) {
      P1Geometry geometry1 = (P1Geometry) geometry;
      return P1Geometry.create(geometry1.getVolume().transform(getTransformation()));
    }
  }

  @AutoValue
  public abstract static class P1ConnectionTransformation implements ConnectionTransformation<P1Room> {

    /**
     * Position within the geometry.
     */
    public abstract Point3i getPosition();

    /**
     * Facing vector. Must be a unit vector.
     */
    public abstract Point3i getFacing();

    public static P1ConnectionTransformation create(Point3i position, Point3i facing) {
      Preconditions.checkArgument(ImmutableSet.of(
              Point3i.UNIT_X, Point3i.UNIT_Y, Point3i.UNIT_Z,
              Point3i.UNIT_X.multiply(-1),
              Point3i.UNIT_Y.multiply(-1),
              Point3i.UNIT_Z.multiply(-1)).contains(facing));
      return new AutoValue_P1Geometry_P1ConnectionTransformation(position, facing);
    }

    @Override
    public P1ConnectionTransformation transform(GeometryTransformation<P1Room> xform) {
      P1GeometryTransformation xform1 = (P1GeometryTransformation) xform;

      return create(
              xform1.getTransformation().apply(getPosition()),
              xform1.getTransformation().applyToVector(getFacing()));
    }
  }
}
