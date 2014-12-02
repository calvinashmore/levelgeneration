/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.base.Equivalence;

/**
 *
 * @author ashmore
 */
public interface Geometry<T extends Room<T,?>> {

  public static interface GeometryTransformation<T extends Room<T,?>> {
    GeometryTransformation<T> transform(GeometryTransformation<T> xform);
    TransformedGeometry<T> transform(Geometry<T> geometry);
  }

  public static abstract class ConnectionTransformation<T extends Room<T,?>> {
    public abstract ConnectionTransformation<T> transform(GeometryTransformation<T> xform);
    public abstract ConnectionTransformation<T> getOpposite();

    public boolean matches(ConnectionTransformation<T> other) {
      return this.equals(other) || this.getOpposite().equals(other);
    }

    public Equivalence.Wrapper<ConnectionTransformation<T>> getEquivalence() {
      return CONNECTION_TRANFORMATION_EQUIVALENCE.wrap(this);
    }

    private static final Equivalence<ConnectionTransformation<?>> CONNECTION_TRANFORMATION_EQUIVALENCE =
            new Equivalence<ConnectionTransformation<?>>() {
          @Override
          protected boolean doEquivalent(ConnectionTransformation<?> t, ConnectionTransformation<?> t1) {
            return t.equals(t1) || t.equals(t1.getOpposite());
          }

          @Override
          protected int doHash(ConnectionTransformation<?> t) {
            return t.hashCode() + t.getOpposite().hashCode();
          }
        };
  }

  public static interface TransformedGeometry<T extends Room<T,?>> {
  }
}
