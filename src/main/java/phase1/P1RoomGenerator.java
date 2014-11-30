/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Predicate;
import generation.ConnectionTemplate.ConnectionPlacement;
import generation.InProgressRoom;
import generation.RoomGenerator;
import generation.RoomTemplate;
import java.util.HashSet;
import java.util.Set;
import math3i.Volume3i;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class P1RoomGenerator extends RoomGenerator<P1Container, P1Room> {

  public P1RoomGenerator(InProgressRoom<P1Container, P1Room> inProgressParent, PrioritizedCollection<P1RoomTemplate> templates) {
    super(inProgressParent, templates);
  }

  @Override
  public PrioritizedCollection<P1RoomTemplate> getTemplates() {
    return (PrioritizedCollection<P1RoomTemplate>) super.getTemplates();
  }

  @Override
  public P1ContainerProgress getInProgressParent() {
    return (P1ContainerProgress) super.getInProgressParent();
  }

  @Override
  public P1Room generateRoom() {

    // **********************
    throw new UnsupportedOperationException();
  }

  private Volume3i getAvailableSpace() {
    P1ContainerProgress parent = getInProgressParent();
    return parent.getEnclosingVolume().difference(parent.getFilledRoomVolume());
  }

  private Set<ConnectionPlacement<P1Room>> getCurrentConnections() {

    Set<ConnectionPlacement<P1Room>> currentConnections = new HashSet<>();
    for(P1Room child : getInProgressParent().getChildren()) {
      // ***********************
    }

    return currentConnections;
  }

  @Override
  public Predicate<RoomTemplate<P1Room>> validRoomPredicate() {
    return new Predicate<RoomTemplate<P1Room>>() {
      @Override public boolean apply(RoomTemplate<P1Room> t) {

        // **********************
        throw new UnsupportedOperationException();
      }
    };
  }

}
