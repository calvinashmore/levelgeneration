/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.auto.value.AutoValue;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class ConnectionPlacement<T extends Room<T, ?, K>, K extends KeyType> {

  public abstract ConnectionTemplate<T, K> getConnection();

  public abstract ConnectionTransformation<T> getTransform();

  public ConnectionPlacement<T, K> transform(Geometry.GeometryTransformation<T> geomTransform) {
    return create(getConnection(), getTransform().transform(geomTransform));
  }

  public static <T extends Room<T, ?, K>, K extends KeyType> ConnectionPlacement<T, K> create(ConnectionTemplate<T, K> connection, ConnectionTransformation<T> transform) {
    return new AutoValue_ConnectionPlacement(connection, transform);
  }

  /**
   * True if both the transforms and the types of the connections match.
   */
  public boolean matches(ConnectionPlacement<T, K> other) {
    return other.getConnection().matches(this.getConnection()) && other.getTransform().matches(this.getTransform());
  }

  /**
   * True if the transforms of the two connections match.
   */
  public boolean transformMatches(ConnectionPlacement<T, K> other) {
    return other.getTransform().matches(this.getTransform());
  }

  @Override
  public String toString() {
    return getTransform() + ":" + getConnection();
  }

  public ConnectionPlacement<T,K> getOpposite() {
    return ConnectionPlacement.create(getConnection(), getTransform().getOpposite());
  }
}
