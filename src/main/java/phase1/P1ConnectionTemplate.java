/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.ConnectionTemplate;

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
      // null does not pass
      return false;

    P1ConnectionTemplate other1 = (P1ConnectionTemplate) other;
    return other1 == this;
  }
}
