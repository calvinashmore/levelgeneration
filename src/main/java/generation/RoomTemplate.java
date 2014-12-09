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
public interface RoomTemplate<T extends Room<T,?, K>, K extends KeyType> {

  Geometry<T> getGeometry();
  Set<ConnectionTemplate.ConnectionPlacement<T, K>> getConnections();

  public interface RoomPlacement<T extends Room<T,?,K>, K extends KeyType> {
    RoomTemplate<T,K> getConnection();
    Geometry.GeometryTransformation<T> getTransform();
  }
}
