/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import java.util.Set;

/**
 *
 * @author ashmore
 */
public interface RoomTemplate<T extends Room<T,?>> {

  Geometry<T> getGeometry();
  Set<ConnectionTemplate.ConnectionPlacement<T>> getConnections();

  public interface RoomPlacement<T extends Room<T,?>> {
    RoomTemplate<T> getConnection();
    Geometry.GeometryTransformation<T> getTransform();
  }
}
