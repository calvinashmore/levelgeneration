/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import com.google.common.base.Function;

/**
 * Represents a (linear) transformation on {@link Point3i}s
 */
public abstract class Transformation3i implements Function<Point3i, Point3i> {

  public static final Transformation3i IDENTITY = new Transformation3i() {
    @Override
    public Point3i apply(Point3i v) {
      return v;
    }
  };

  public abstract Point3i apply(Point3i v);

  public static Transformation3i translation(final Point3i offset) {
    return new Transformation3i() {
      @Override
      public Point3i apply(Point3i v) {
        return offset.add(v);
      }
    };
  }

  public static Transformation3i translation(int dx, int dy, int dz) {
    return translation(Point3i.create(dx, dy, dz));
  }

  public static final Transformation3i REFLECTION_X = perAxisScale(-1, 0, 0);
  public static final Transformation3i REFLECTION_Y = perAxisScale(0, -1, 0);
  public static final Transformation3i REFLECTION_Z = perAxisScale(0, 0, -1);

  public static Transformation3i perAxisScale(final int sx, final int sy, final int sz) {
    return new Transformation3i() {
      @Override
      public Point3i apply(Point3i v) {
        return Point3i.create(sx*v.getX(), sy*v.getY(), sz*v.getZ());
      }
    };
  }

  /**
   * transformations F, G:
   * F.compose(G).apply(x) = FGx
   */
  public Transformation3i compose(Transformation3i inner) {
    return new Composition(inner, this);
  }

  private static final class Composition extends Transformation3i {

    private final Transformation3i first, second;

    /**
     * The {@code first} transformation is applied first.
     */
    public Composition(Transformation3i first, Transformation3i second) {
      this.first = first;
      this.second = second;
    }


    @Override
    public Point3i apply(Point3i v) {
      return second.apply(first.apply(v));
    }
  }

  /**
   * Represents a rotation in 90 degree increments
   */
  public enum RotationAngle {
    ANGLE_0(1,0),
    ANGLE_90(0,1),
    ANGLE_180(-1,0),
    ANGLE_270(0,-1);

    private final int cos, sin;
    private RotationAngle(int cos, int sin) {
      this.cos = cos;
      this.sin = sin;
    }
  }

  public enum Axis {
    X, Y, Z
  }

  public Transformation3i rotationZ(Axis axis, RotationAngle angle) {
    if(angle == RotationAngle.ANGLE_0)
      return IDENTITY;
    return new Rotation(axis, angle);
  }

  private static final class Rotation extends Transformation3i {

    private final Axis axis;
    private final RotationAngle angle;

    public Rotation(Axis axis, RotationAngle angle) {
      this.axis = axis;
      this.angle = angle;
    }

    @Override
    public Point3i apply(Point3i v) {
      int x = v.getX();
      int y = v.getY();
      int z = v.getZ();
      int c = angle.cos;
      int s = angle.sin;
      switch(axis) {
        case X: return Point3i.create(x, c*y + s*z, c*z - s*z);
        case Y: return Point3i.create(c*x + s*z, y, c*z - s*x);
        case Z: return Point3i.create(c*x - s*y, c*y + s*x, z);
        default: throw new IllegalArgumentException(""+axis);
      }
    }
  }
}
