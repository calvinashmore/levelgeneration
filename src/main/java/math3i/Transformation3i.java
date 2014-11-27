/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math3i;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a (linear) transformation on {@link Point3i}s
 */
public class Transformation3i implements Function<Point3i, Point3i> {

  public static final Transformation3i IDENTITY =
          new Transformation3i(new int[] {1,0,0,0, 0,1,0,0, 0,0,1,0});

  public static final Transformation3i REFLECTION_X =
          new Transformation3i(new int[] {-1,0,0,0, 0,1,0,0, 0,0,1,0});
  public static final Transformation3i REFLECTION_Y =
          new Transformation3i(new int[] {1,0,0,0, 0,-1,0,0, 0,0,1,0});
  public static final Transformation3i REFLECTION_Z =
          new Transformation3i(new int[] {1,0,0,0, 0,1,0,0, 0,0,-1,0});

  public static Transformation3i translation(int tx, int ty, int tz) {
    return new Transformation3i(new int[] {1,0,0,tx, 0,1,0,ty, 0,0,1,tz});
  }

  public static Transformation3i translation(Point3i v) {
    return translation(v.getX(), v.getY(), v.getZ());
  }

  /**
   * Rotate about the z axis in the given number of 90 degree increments.
   */
  public static Transformation3i rotationZ(int increments) {
    int floorIncrements = (increments % 4 + 4) %4;

    Transformation3i base = new Transformation3i(
            new int[] {0,1,0,0, -1,0,0,0, 0,0,1,0});

    Transformation3i t = IDENTITY;
    for(int angle = 0; angle < floorIncrements; angle++)
      t = t.compose(base);

    return t;
  }

  /**
   * This has the shape of an affine translation matrix for 3d space.
   * [A0  A1  A2  A3 ]
   * [A4  A5  A6  A7 ]
   * [A8  A9  A10 A11]
   * [0   0   0   1  ]
   */
  private final int[] A = new int[12];

  private Transformation3i(int xform[]) {
    Preconditions.checkArgument(xform.length == 12);
    System.arraycopy(xform, 0, this.A, 0, 12);
  }

  @Override
  public Point3i apply(Point3i v) {
    return Point3i.create(
        A[0]*v.getX() + A[1]*v.getY() + A[2]*v.getZ() + A[3],
        A[4]*v.getX() + A[5]*v.getY() + A[6]*v.getZ() + A[7],
        A[8]*v.getX() + A[9]*v.getY() + A[10]*v.getZ() + A[11]);
  }

  /**
   * Applies the transformation to a vector (not a point), omitting the translation component.
   */
  public Point3i applyToVector(Point3i v) {
    return Point3i.create(
        A[0]*v.getX() + A[1]*v.getY() + A[2]*v.getZ(),
        A[4]*v.getX() + A[5]*v.getY() + A[6]*v.getZ(),
        A[8]*v.getX() + A[9]*v.getY() + A[10]*v.getZ());
  }

  /**
   * The resulting xform will be this * other * v
   */
  public Transformation3i compose(Transformation3i other) {
    int[] B = other.A;

    //Cij = sum(k, Aik * Bkj
    return new Transformation3i(new int[] {
      A[0]*B[0] + A[1]*B[4] + A[2]*B[8],
      A[0]*B[1] + A[1]*B[5] + A[2]*B[9],
      A[0]*B[2] + A[1]*B[6] + A[2]*B[10],
      A[0]*B[3] + A[1]*B[7] + A[2]*B[11] + A[3],

      A[4]*B[0] + A[5]*B[4] + A[6]*B[8],
      A[4]*B[1] + A[5]*B[5] + A[6]*B[9],
      A[4]*B[2] + A[5]*B[6] + A[6]*B[10],
      A[4]*B[3] + A[5]*B[7] + A[6]*B[11] + A[7],

      A[8]*B[0] + A[9]*B[4] + A[10]*B[8],
      A[8]*B[1] + A[9]*B[5] + A[10]*B[9],
      A[8]*B[2] + A[9]*B[6] + A[10]*B[10],
      A[8]*B[3] + A[9]*B[7] + A[10]*B[11] + A[11],
    });
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Transformation3i) {
      Transformation3i that = (Transformation3i) obj;
      return Arrays.equals(this.A, that.A);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash((Object) A);
  }

  @Override
  public String toString() {
    return String.format(
           "[%d %d %d %d]\n"
         + "[%d %d %d %d]\n"
         + "[%d %d %d %d]\n"
         + "[0 0 0 1]",
            A[0], A[1], A[2], A[3],
            A[4], A[5], A[6], A[7],
            A[8], A[9], A[10], A[11]);
  }
}
