/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import generation.KeyType;
import generation.Room;

/**
 *
 * @author ashmore
 */
public abstract class V3Room<T extends V3Room<T,Child,K>, Child extends Room<Child,?,?>, K extends KeyType> extends Room<T, Child, K> {

  @Override
  public V3Geometry<T> getTransformedGeometry() {
    return (V3Geometry<T>) super.getTransformedGeometry();
  }

  @Override
  public abstract V3Geometry.V3GeometryTransformation<T> getGeometryTransformation();
}
