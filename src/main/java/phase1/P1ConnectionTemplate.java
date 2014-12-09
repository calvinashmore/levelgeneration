/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

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
  WALL(0),
  DOOR_1(1),
  DOOR_2(2),
  ;

  private final int matchPriority;

  private P1ConnectionTemplate(int matchPriority) {
    this.matchPriority = matchPriority;
  }

  @Override
  public int getMatchPriority() {
    return matchPriority;
  }

  private static final P1KeyType KEY_TYPE = new P1KeyType();

  @Nullable
  @Override
  public P1KeyType getKeyType() {
    // TODO: replace with something substantive.
    if(matchPriority == 0)
      return null;
    else return KEY_TYPE;
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

  public static ConnectionTemplate.ConnectionPlacement placement(
          P1ConnectionTemplate type, Point3i position, Point3i direction) {
    return ConnectionTemplate.ConnectionPlacement.create(
            type, P1ConnectionTransformation.create(position, direction));
  }
}
