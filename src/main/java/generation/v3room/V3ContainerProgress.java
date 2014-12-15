/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.Geometry;
import generation.InProgressRoom;
import generation.KeyType;
import generation.Room;
import java.util.stream.Collectors;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public abstract class V3ContainerProgress<Parent extends Room<Parent,T,?>, T extends V3Room<T,?,K>, K extends KeyType> extends InProgressRoom<Parent, T, K> {

  private final Volume3i enclosingVolume;
  private final ConnectionTemplate<T,K> wallTemplate;
  private transient Volume3i filledRoomVolume = Volume3i.EMPTY;
  private transient Volume3i freeVolume;

  public V3ContainerProgress(Volume3i enclosingVolume, ConnectionTemplate<T,K> wallTemplate) {
    this.enclosingVolume = enclosingVolume;
    this.freeVolume = enclosingVolume;
    this.wallTemplate = wallTemplate;
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
  public void addChild(T child) {
    super.addChild(child);
    calculateVolumes();
  }

  private void calculateVolumes() {
    filledRoomVolume = Volume3i.union(getChildren().stream()
        .map(child -> child.getTransformedGeometry().getVolume())
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
  public boolean geometryMatches(Geometry.TransformedGeometry<T> geometry) {
    V3Geometry geometry1 = (V3Geometry) geometry;
    return getFreeVolume().contains(geometry1.getVolume());
  }

  /**
   * Sets wall connection templates on all sides of this container. Called within constructor.
   */
  private void setEnclosure() {
    for(V3ConnectionTransformation connectionTransform :
            V3ConnectionTransformation.getBoundaries(getEnclosingVolume())) {
      addParentConnection(ConnectionPlacement.<T,K>create(
              wallTemplate, connectionTransform));
    }
  }
}