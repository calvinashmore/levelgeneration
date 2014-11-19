/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math3i;

import com.google.auto.value.AutoValue;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class Point3i {

  public static final Point3i ZERO = create(0,0,0);
  public static final Point3i UNIT_X = create(1,0,0);
  public static final Point3i UNIT_Y = create(0,1,0);
  public static final Point3i UNIT_Z = create(0,0,1);

  public static Point3i create(int x, int y, int z) {
    return new AutoValue_Point3i(x,y,z);
  }

  public abstract int getX();
  public abstract int getY();
  public abstract int getZ();

  public Point3i add(Point3i that) {
    return create(
        this.getX() + that.getX(),
        this.getY() + that.getY(),
        this.getZ() + that.getZ());
  }

  public Point3i subtract(Point3i that) {
    return create(
        this.getX() - that.getX(),
        this.getY() - that.getY(),
        this.getZ() - that.getZ());
  }

  public Point3i multiply(int c) {
    return create(
        this.getX() * c,
        this.getY() * c,
        this.getZ() * c);
  }

  @Override
  public String toString() {
    return String.format("<%d, %d, %d>", getX(), getY(), getZ());
  }
}
