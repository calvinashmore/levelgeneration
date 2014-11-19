/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;

/**
 * A volume represented as voxel set of {@link Point3}s
 */
public class Volume3i {

  public static final Volume3i EMPTY = new Volume3i(ImmutableSet.<Point3i>of());

  /**
   * Returns a box whose dimensions in [0,wx) x [0,wy) x [0,wz)
   */
  public static Volume3i box(int wx, int wy, int wz) {
    Preconditions.checkArgument(wx > 0 && wy > 0 && wz > 0);
    Set<Point3i> points = new HashSet<>();
    for(int x = 0; x < wx; x++)
    for(int y = 0; y < wy; y++)
    for(int z = 0; z < wz; z++)
      points.add(Point3i.create(x, y, z));
    return new Volume3i(points);
  }

  private final ImmutableSet<Point3i> points;

  public Volume3i(Iterable<Point3i> points) {
    this.points = ImmutableSet.copyOf(points);
  }

  public Volume3i union(Volume3i that) {
    return new Volume3i(Sets.union(this.points, that.points));
  }

  public static Volume3i union(Iterable<Volume3i> volumes) {
    Volume3i union = EMPTY;
    for(Volume3i volume : volumes) {
      union = union.union(volume);
    }
    return union;
  }

  public Volume3i intersection(Volume3i that) {
    return new Volume3i(Sets.intersection(this.points, that.points));
  }

  public Volume3i difference(Volume3i that) {
    return new Volume3i(Sets.difference(this.points, that.points));
  }

  /**
   * Returns true if this wholly contains that.
   */
  public boolean contains(Volume3i that) {
    return points.containsAll(that.points);
  }

  public boolean contains(Point3i v) {
    return points.contains(v);
  }

  /**
   * Total number of points in the volume.
   */
  public int size() {
    return points.size();
  }

  /**
   * Returns the point whose x,y,z coordinates are the least of all in the volume.
   * Note that this point may not actually be included in the volume.
   */
  public Point3i getMinimum() {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;
    for (Point3i point : points) {
      minX = Math.min(minX, point.getX());
      minY = Math.min(minY, point.getY());
      minZ = Math.min(minZ, point.getZ());
    }
    return Point3i.create(minX, minY, minZ);
  }

  /**
   * Returns the point whose x,y,z coordinates are the greatest of all in the volume.
   * Note that this point may not actually be included in the volume.
   */
  public Point3i getMaximum() {
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    int maxZ = Integer.MIN_VALUE;
    for (Point3i point : points) {
      maxX = Math.max(maxX, point.getX());
      maxY = Math.max(maxY, point.getY());
      maxZ = Math.max(maxZ, point.getZ());
    }
    return Point3i.create(maxX, maxY, maxZ);
  }

  /**
   * Return a point whose coordinates correspond to the dimensions of this volume.
   */
  public Point3i getDimensions() {

    if(points.isEmpty()) {
      return Point3i.ZERO;
    }

    Point3i min = getMinimum();
    Point3i max = getMaximum();
    // we have to add 1 to the end, otherwise a singleton would have dimensions of zero.
    return max.subtract(min).add(Point3i.create(1, 1, 1));
  }

  public boolean intersects(Volume3i that) {
    for (Point3i point : that.points) {
      if (this.points.contains(point))
        return true;
    }
    return false;
  }

  public Volume3i transform(Transformation3i t) {
    return new Volume3i(FluentIterable.from(points).transform(t));
  }

  /**
   * Returns all translations that can be used to maneuver the
   * given volume so that it is wholly contained by this volume.
   */
  // @Nullable
  public Set<Transformation3i> getValidTranslations(Volume3i volume) {

    Point3i thisMinimum = this.getMinimum();
    Point3i thisMaximum = this.getMaximum();

    Point3i thatMinimum = volume.getMinimum();
    Point3i thatMaximum = volume.getMaximum();

    int thisSizeX = thisMaximum.getX() - thisMinimum.getX();
    int thisSizeY = thisMaximum.getY() - thisMinimum.getY();
    int thisSizeZ = thisMaximum.getZ() - thisMinimum.getZ();

    int thatSizeX = thatMaximum.getX() - thatMinimum.getX();
    int thatSizeY = thatMaximum.getY() - thatMinimum.getY();
    int thatSizeZ = thatMaximum.getZ() - thatMinimum.getZ();

    /*

    consider 1d case

    suppose min = 0

    then values to test: [0, container.max - contained.max] // inclusive

    can translate container to 0 by using -min
    can translate contained to 0 by using -min

    */

    Transformation3i base = Transformation3i.translation(thisMinimum)
            .compose(Transformation3i.translation(thatMinimum.multiply(-1)));


    ImmutableSet.Builder<Transformation3i> results = ImmutableSet.builder();
    // TODO: deal with this some way other than brute force
    for(int x = 0; x <= thisSizeX - thatSizeX; x++)
      for(int y = 0; y <= thisSizeY - thatSizeY; y++)
        for(int z = 0; z <= thisSizeZ - thatSizeZ; z++) {
          Transformation3i t = base.compose(Transformation3i.translation(x, y, z));
          if(this.contains(volume.transform(t)))
            results.add(t);
        }

    // can do some checks in case the size is way off
    return results.build();
  }

  /**
   * Returns true if the points in this volume are part of one contiguous set.
   * Volumes joined at a diagonal are not contiguous.
   */
  public boolean isContiguous() {
    throw new UnsupportedOperationException();

  }
}
