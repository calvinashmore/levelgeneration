/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

/**
 *
 * @author ashmore
 */
public interface Geometry<T extends Room<T,?>> {

  public static interface GeometryTransformation<T extends Room<T,?>> {
    GeometryTransformation<T> transform(GeometryTransformation<T> xform);
    TransformedGeometry<T> transform(Geometry<T> geometry);
  }

  public static interface ConnectionTransformation<T extends Room<T,?>> {
    ConnectionTransformation<T> transform(GeometryTransformation<T> xform);
    boolean matches(ConnectionTransformation<T> other);
  }

  public static interface TransformedGeometry<T extends Room<T,?>> {
  }
}
