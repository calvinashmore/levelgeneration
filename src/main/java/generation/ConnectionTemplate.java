/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;


/**
 *
 * @author ashmore
 */
public interface ConnectionTemplate<T extends Room<T,?>> {

  /**
   * if other is null, this connection can be sealed
   */
  boolean matches(@Nullable ConnectionTemplate<T> other);

  @AutoValue
  public static abstract class ConnectionPlacement<T extends Room<T,?>> {
    public abstract ConnectionTemplate<T> getConnection();
    public abstract Geometry.ConnectionTransformation<T> getTransform();

    public ConnectionPlacement<T> transform(Geometry.GeometryTransformation<T> geomTransform) {
      return create(getConnection(), getTransform().transform(geomTransform));
    }

    public static <T extends Room<T,?>> ConnectionPlacement<T> create(
            ConnectionTemplate<T> connection, Geometry.ConnectionTransformation<T> transform) {
      return new AutoValue_ConnectionTemplate_ConnectionPlacement(connection, transform);
    }
  }
}
