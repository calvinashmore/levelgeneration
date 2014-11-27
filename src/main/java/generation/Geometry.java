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
    public GeometryTransformation<T> transform(GeometryTransformation<T> xform);
  }

  public static interface ConnectionTransformation<T extends Room<T,?>> {
    public ConnectionTransformation<T> transform(GeometryTransformation<T> xform);
  }
}
