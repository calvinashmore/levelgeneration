/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nullable;
import phase1.P1RoomTemplate;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public abstract class RoomGenerator<Parent extends Room<Parent,T>, T extends Room<T,?>> {

  private final InProgressRoom<Parent, T> inProgressParent;
  private final PrioritizedCollection<? extends RoomTemplate<T>> templates;

  public RoomGenerator(InProgressRoom<Parent, T> inProgressParent,
          PrioritizedCollection<? extends RoomTemplate<T>> templates) {
    this.inProgressParent = Preconditions.checkNotNull(inProgressParent);
    this.templates = Preconditions.checkNotNull(templates);
  }

  public InProgressRoom<Parent, T> getInProgressParent() {
    return inProgressParent;
  }

  public PrioritizedCollection<? extends RoomTemplate<T>> getTemplates() {
    return templates;
  }

  public abstract List<? extends Geometry.GeometryTransformation<T>> getPossibleTransformations(
          P1RoomTemplate template,
          List<? extends Geometry.ConnectionTransformation<T>> highestPriorityConnections);

  /**
   * Returns null if one cannot be generated.
   */
  @Nullable
  public abstract T generateRoom();
}
