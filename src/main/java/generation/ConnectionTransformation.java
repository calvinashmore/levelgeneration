/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import java.util.Objects;

/**
 *
 * @author ashmore
 */
public abstract class ConnectionTransformation<T extends Room<T, ?>> {

  public abstract ConnectionTransformation<T> transform(Geometry.GeometryTransformation<T> xform);

  public abstract ConnectionTransformation<T> getOpposite();
  private transient ConnectionTransformationEquivalence<T> equivalence;

  public boolean matches(ConnectionTransformation<T> other) {
    return this.equals(other) || this.getOpposite().equals(other);
  }

  public ConnectionTransformationEquivalence<T> getEquivalence() {
    if (equivalence == null) {
      equivalence = new ConnectionTransformationEquivalence<>(this, getOpposite());
    }
    return equivalence;
  }

  public static final class ConnectionTransformationEquivalence<T extends Room<T, ?>> {

    private final int hash;
    private final ConnectionTransformation<T> xform1;
    private final ConnectionTransformation<T> xform2;

    public ConnectionTransformationEquivalence(ConnectionTransformation<T> xform1, ConnectionTransformation<T> xform2) {
      this.xform1 = xform1;
      this.xform2 = xform2;
      hash = xform1.hashCode() + xform2.hashCode();
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final ConnectionTransformationEquivalence<?> that = (ConnectionTransformationEquivalence<?>) obj;
      return Objects.equals(this.xform1, that.xform1) || Objects.equals(this.xform1, that.xform2);
    }
  }
}
