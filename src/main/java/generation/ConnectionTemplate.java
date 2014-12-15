/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import javax.annotation.Nullable;


/**
 * Connection templates describe the kinds of connections rooms may have with
 * each other. Null implies that there is no connection present at the given transform.
 */
public interface ConnectionTemplate<T extends Room<T,?,K>, K extends KeyType> {

  /**
   * Whether this template matches the other, accounting that the other may be null.
   */
  boolean matches(@Nullable ConnectionTemplate<T,K> other);

  /**
   * Null means that this connection cannot actually be traversed, and should not be added to a graph.
   */
  @Nullable
  public K getKeyType();

  /**
   * System will match all connections with the highest match priority before proceeding to the lower ones.
   */
  int getMatchPriority();

}
