/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.ConnectionTemplate;
import math3i.Point3i;

/**
 *
 * @author ashmore
 */
public enum P1ConnectionTemplate implements ConnectionTemplate<P1Room> {

  /**
   * Only used for enclosure boundaries in P1Container.
   */
  WALL,
  DOOR_1,
  DOOR_2,
  ;

  @Override
  public boolean matches(ConnectionTemplate<P1Room> other) {
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
            type, P1Geometry.P1ConnectionTransformation.create(position, direction));
  }
}
