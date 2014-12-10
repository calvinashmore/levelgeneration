/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import javax.annotation.Nullable;
import math3i.Point3i;

/**
 *
 * @author ashmore
 */
public enum P1ConnectionTemplate implements ConnectionTemplate<P1Room, P1KeyType> {

  /**
   * Only used for enclosure boundaries in P1Container.
   */
  WALL(0, null),
  DOOR_1(1, P1KeyType.NORMAL),
  DOOR_2(2, P1KeyType.RED_KEY),
  ;

  private final int matchPriority;
  @Nullable
  private final P1KeyType keyType;

  private P1ConnectionTemplate(int matchPriority, P1KeyType keyType) {
    this.matchPriority = matchPriority;
    this.keyType = keyType;
  }

  @Override
  public int getMatchPriority() {
    return matchPriority;
  }

  @Nullable
  @Override
  public P1KeyType getKeyType() {
    return keyType;
  }

  @Override
  public boolean matches(ConnectionTemplate<P1Room, P1KeyType> other) {
    if(other == null)
      // note: I've waffled on this a few times; the correct behavior here is not really clear.
      // null passes
      return true;

    P1ConnectionTemplate other1 = (P1ConnectionTemplate) other;
    return other1 == this;
  }

  public static ConnectionPlacement<P1Room, P1KeyType> placement(
          P1ConnectionTemplate type, Point3i position, Point3i direction) {
    return ConnectionPlacement.create(
            type, P1ConnectionTransformation.create(position, direction));
  }
}
