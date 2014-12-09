/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.Geometry;
import generation.InProgressRoom;
import java.util.stream.Collectors;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public class P1ContainerProgress extends InProgressRoom<P1Container, P1Room, P1KeyType> {

  private final Volume3i enclosingVolume;
  private transient Volume3i filledRoomVolume = Volume3i.EMPTY;
  private transient Volume3i freeVolume;

  public P1ContainerProgress(Volume3i enclosingVolume) {
    this.enclosingVolume = enclosingVolume;
    this.freeVolume = enclosingVolume;
    setEnclosure();
  }

  public Volume3i getEnclosingVolume() {
    return enclosingVolume;
  }

  public Volume3i getFilledRoomVolume() {
    if(filledRoomVolume == null)
      calculateVolumes();
    return filledRoomVolume;
  }

  public Volume3i getFreeVolume() {
    if(freeVolume == null)
      calculateVolumes();
    return freeVolume;
  }

  @Override
  public void addChild(P1Room child) {
    super.addChild(child);
    calculateVolumes();
  }

  private void calculateVolumes() {
    filledRoomVolume = Volume3i.union(getChildren().stream()
        .map(child -> ((P1Room) child).getTransformedGeometry().getVolume())
        .collect(Collectors.toList()));
    freeVolume = enclosingVolume.difference(filledRoomVolume);
  }

  /**
   * Returns true if there are no open connections.
   */
  @Override
  public boolean isValid() {
    return getOpenConnections().stream()
            .noneMatch(placement -> placement.getConnection().getMatchPriority() > 0);
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

  /**
   * Sets wall connection templates on all sides of this container. Called within constructor.
   */
  private void setEnclosure() {
    for(P1ConnectionTransformation connectionTransform :
            P1ConnectionTransformation.getBoundaries(getEnclosingVolume())) {
      addParentConnection(P1ConnectionTemplate.ConnectionPlacement.create(
              P1ConnectionTemplate.WALL, connectionTransform));
    }
  }
}
