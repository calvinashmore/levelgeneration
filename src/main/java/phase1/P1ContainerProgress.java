/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.ConnectionPlacement;
import generation.Geometry;
import generation.InProgressRoom;
import generation.v3room.V3ContainerProgress;
import generation.v3room.V3Geometry;
import java.util.stream.Collectors;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public class P1ContainerProgress extends V3ContainerProgress<P1Container, P1Room, P1KeyType> {

  public P1ContainerProgress(Volume3i enclosingVolume) {
    super(enclosingVolume, P1ConnectionTemplate.WALL);
  }
  @Override
  public P1Container build() {
    return P1Container.create(getChildren());
  }
}
