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
import java.util.HashSet;
import java.util.Set;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1Geometry implements Geometry<P1Room>, TransformedGeometry<P1Room> {
  
  public static final Point3i NORTH = Point3i.UNIT_Y;
  public static final Point3i SOUTH = Point3i.UNIT_Y.multiply(-1);
  public static final Point3i EAST = Point3i.UNIT_X;
  public static final Point3i WEST = Point3i.UNIT_X.multiply(-1);
  public static final Set<Point3i> DIRECTIONS = ImmutableSet.of(NORTH, SOUTH, EAST, WEST);

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
    public P1Geometry transform(Geometry<P1Room> geometry) {
      P1Geometry geometry1 = (P1Geometry) geometry;
      return P1Geometry.create(geometry1.getVolume().transform(getTransformation()));
    }
  }

  @AutoValue
  public abstract static class P1ConnectionTransformation extends ConnectionTransformation<P1Room> {

    /**
     * Position within the geometry.
     */
    public abstract Point3i getPosition();

    /**
     * Facing vector. Must be a unit vector.
     */
    public abstract Point3i getFacing();

    public static P1ConnectionTransformation create(Point3i position, Point3i facing) {
      Preconditions.checkArgument(DIRECTIONS.contains(facing));
      return new AutoValue_P1Geometry_P1ConnectionTransformation(position, facing);
    }

    @Override
    public ConnectionTransformation<P1Room> getOpposite() {
      return create(getPosition().add(getFacing()), getFacing().multiply(-1));
    }

    @Override
    public P1ConnectionTransformation transform(GeometryTransformation<P1Room> xform) {
      P1GeometryTransformation xform1 = (P1GeometryTransformation) xform;

      return create(
              xform1.getTransformation().apply(getPosition()),
              xform1.getTransformation().applyToVector(getFacing()));
    }

    public static Set<P1ConnectionTransformation> getBoundaries(Volume3i volume) {
      Set<P1ConnectionTransformation> results = new HashSet<>();

      for(Point3i point : volume.getPoints()) {
        for(Point3i direction : DIRECTIONS) {
          Point3i next = point.add(direction);
          if(!volume.contains(next)) {
            results.add(create(point,direction));
          }
        }
      }
      return results;
    }
  }
}
