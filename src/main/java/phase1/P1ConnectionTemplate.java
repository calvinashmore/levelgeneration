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

  FOO
  ;

  @Override
  public boolean matches(ConnectionTemplate<P1Room> other) {
    P1ConnectionTemplate other1 = (P1ConnectionTemplate) other;

    // *****************
    throw new UnsupportedOperationException();
  }
}
