/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import javax.annotation.Nullable;

/**
 *
 * @author ashmore
 */
public abstract class RoomGenerator<Parent extends Room<Parent,T>, T extends Room<T,?>> {

  private final InProgressRoom<Parent, T> inProgressParent;

  public RoomGenerator(InProgressRoom<Parent, T> inProgressParent) {
    this.inProgressParent = inProgressParent;
  }

  public InProgressRoom<Parent, T> getInProgressParent() {
    return inProgressParent;
  }

  /**
   * Returns null if one cannot be generated.
   */
  @Nullable
  public abstract T generateRoom();
}
