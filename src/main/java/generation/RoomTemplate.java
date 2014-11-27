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
public abstract class RoomTemplate<T extends Room<T,?>> {

  Geometry<T> geometry;
  Set<ConnectionTemplate.ConnectionPlacement<T>> connections;


  public static class RoomPlacement<T extends Room<T,?>> {
    RoomTemplate<T> connection;
    Geometry.GeometryTransformation<T> transform;
  }
}
