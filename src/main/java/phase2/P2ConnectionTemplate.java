/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import generation.ConnectionTemplate;

/**
 *
 * @author ashmore
 */
public enum P2ConnectionTemplate implements ConnectionTemplate<P2Room, P2KeyType>{

  NONE, // represents the edge of the map
  GRASS,
  CREEK_IN,
  CREEK_OUT,
  ;

  @Override
  public boolean matches(ConnectionTemplate<P2Room, P2KeyType> other) {
    switch(this) {
    case CREEK_IN: return other == CREEK_OUT;
    case CREEK_OUT: return other == CREEK_IN;
    default: return other == this;
    }
  }

  @Override
  public P2KeyType getKeyType() {
    // TODO: return something better here.
    return P2KeyType.OPEN;
  }

  @Override
  public int getMatchPriority() {
    switch(this) {
      case NONE:
        return 0;
      case CREEK_IN:
      case CREEK_OUT:
        return 1;
      default:
        return 1;
    }
  }

  /**
   * True if this represents a feature that continues across tiles, and runs the risk of being unmatched.
   */
  public boolean isFeature() {
    switch(this) {
      case CREEK_IN:
      case CREEK_OUT:
        return true;
      default:
        return false;
    }
  }

}
