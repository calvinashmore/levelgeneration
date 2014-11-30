/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.Geometry;
import generation.InProgressRoom;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public class P1ContainerProgress extends InProgressRoom<P1Container, P1Room> {

  private Volume3i enclosingVolume;
  private volatile Volume3i filledRoomVolume = Volume3i.EMPTY;

  public Volume3i getEnclosingVolume() {
    return enclosingVolume;
  }

  public Volume3i getFilledRoomVolume() {
    return filledRoomVolume;
  }

  public Volume3i getFreeVolume() {
    return getEnclosingVolume().difference(getFilledRoomVolume());
  }

  @Override
  public void addChild(P1Room child) {
    super.addChild(child);
    filledRoomVolume = filledRoomVolume.union(child.getTransformedGeometry().getVolume());
  }

  @Override
  public boolean isValid() {
    Volume3i childrenVolume = Volume3i.EMPTY;
    for(P1Room child : getChildren())
      childrenVolume = childrenVolume.union(child.getTransformedGeometry().getVolume());

    return childrenVolume.equals(enclosingVolume);
  }

  @Override
  public P1Container build() {
    return P1Container.create(getChildren());
  }

  @Override
  public boolean geometryMatches(Geometry.TransformedGeometry<P1Room> geometry) {
    P1Geometry geometry1 = (P1Geometry) geometry;
    return getFreeVolume().contains(geometry1.getVolume());
  }
}
