/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import generation.v3room.V3ContainerProgress;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public class P2ContainerProgress extends V3ContainerProgress<P2Container, P2Room, P2KeyType> {

  public P2ContainerProgress(Volume3i enclosingVolume) {
    super(enclosingVolume, P2ConnectionTemplate.NONE);
  }
  
  @Override
  public P2Container build() {
    return P2Container.create(getChildren());
  }
}
