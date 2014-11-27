/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

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


  public static class ConnectionPlacement<T extends Room<T,?>> {
    ConnectionTemplate<T> connection;
    Geometry.ConnectionTransformation<T> transform;
  }
}
