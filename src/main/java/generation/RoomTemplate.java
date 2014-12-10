/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import java.util.Set;
import util.Graph;

/**
 *
 * @author ashmore
 */
public interface RoomTemplate<T extends Room<T,?, K>, K extends KeyType> {

  Geometry<T> getGeometry();
  Set<ConnectionPlacement<T, K>> getConnections();

  /**
   * Returns the *untransformed* connectivity graph between the outgoing connections of this room.
   * For instance, if a room has an obstacle preventing passage from one side to another, that could
   * be expressed here. Otherwise it is recommended to use a saturated graph.
   */
  public Graph<ConnectionPlacement<T, K>, K> getConnectivityGraph();

  public interface RoomPlacement<T extends Room<T,?,K>, K extends KeyType> {
    RoomTemplate<T,K> getConnection();
    Geometry.GeometryTransformation<T> getTransform();
  }
}
